package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private BaseModel mGroup;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;
    private CallbackDeleteAdapter mDeleteListener;

    public ProductAdapter(BaseModel group, List<BaseModel> list, CallbackClickAdapter callbackClickAdapter, CallbackDeleteAdapter callbackDeleteAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mGroup = group;
        this.mListener = callbackClickAdapter;
        this.mDeleteListener = callbackDeleteAdapter;

        for (int i = 0; i < list.size(); i++) {
            ProductGroup productGroup = new ProductGroup(list.get(i).getJsonObject("productGroup"));
            if (productGroup.getInt("id") == group.getInt("id")) {
                mData.add(list.get(i));
            }
        }

    }

    @Override
    public ProductAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_product_item, parent, false);
        return new ProductAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<Product> list) {
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ProductAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvPrice.setText(Util.FormatMoney(mData.get(position).getDouble("unitPrice")));
        String baseprice = Util.isAdmin() ? (String.format(" (%s)", Util.FormatMoney(mData.get(position).getDouble("basePrice")))) : "";
        holder.tvBasePrice.setText(Util.FormatMoney(mData.get(position).getDouble("purchasePrice")) + baseprice);


        holder.tvGift.setVisibility(mData.get(position).getInt("promotion") == 1 ? View.VISIBLE : View.GONE);
        if (!Util.checkImageNull(mData.get(position).getString("image"))) {
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.imageProduct);

        } else {
            Glide.with(mContext).load(R.drawable.ic_wolver).centerCrop().into(holder.imageProduct);

        }

        if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
            holder.rlParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRespone(mData.get(position).BaseModelstoString(), position);

                }
            });

            holder.rlParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa sản phẩm " + mData.get(position).getString("name"), "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result) {
                                BaseModel param = createGetParam(ApiUtil.PRODUCT_DELETE() + mData.get(position).getString("id"), false);
                                new GetPostMethod(param, new NewCallbackCustom() {
                                    @Override
                                    public void onResponse(BaseModel result, List<BaseModel> list) {
                                        Util.getInstance().stopLoading(true);
                                        mDeleteListener.onDelete(mData.get(position).BaseModelstoString(), position);
                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                }, 1).execute();

//                                ProductConnect.DeleteProduct(param, new CallbackCustom() {
//                                    @Override
//                                    public void onResponse(BaseModel result) {
//                                        Util.getInstance().stopLoading(true);
//                                        mDeleteListener.onDelete(mData.get(position).BaseModelstoString(), position);
//
//
//                                    }
//
//                                    @Override
//                                    public void onError(String error) {
//                                        Util.getInstance().stopLoading(true);
//                                        Constants.throwError(error);
//
//                                    }
//                                }, true);
                            }

                        }
                    });

                    return true;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvBasePrice, tvPrice, tvGift;
        private RelativeLayout rlParent;
        private CircleImageView imageProduct;

        public ProductAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.product_item_name);
            tvBasePrice = (TextView) itemView.findViewById(R.id.product_item_base_price);
            tvPrice = (TextView) itemView.findViewById(R.id.product_item_unitprice);
            tvGift = (TextView) itemView.findViewById(R.id.product_item_gift);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.product_item_parent);
            imageProduct = itemView.findViewById(R.id.product_item_image);
        }

    }

    public List<BaseModel> getmData(){
        return mData;
    }

    public void notifyItem(BaseModel item, int pos){
        mData.remove(pos);
        mData.add(pos, item);
        notifyItemChanged(pos);

    }

}
