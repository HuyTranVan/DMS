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
import wolve.dms.callback.CallbackObjectAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;
import static wolve.dms.activities.BaseActivity.createPostParam;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    //private BaseModel mGroup;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObjectAdapter mItem, mCopy;

    public ProductAdapter(List<BaseModel> list, CallbackObjectAdapter itemlistener, CallbackObjectAdapter copylistener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        //this.mGroup = group;
        this.mData = list;
        this.mItem = itemlistener;
        this.mCopy = copylistener;

//        for (int i = 0; i < list.size(); i++) {
//            ProductGroup productGroup = new ProductGroup(list.get(i).getJsonObject("productGroup"));
//            if (productGroup.getInt("id") == group.getInt("id")) {
//                mData.add(list.get(i));
//            }
//        }

    }

    @Override
    public ProductAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_product_item, parent, false);
        return new ProductAdapterViewHolder(itemView);
    }

    public void addItem(ArrayList<Product> list) {
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void updateItem(BaseModel item){
        //boolean check= false;
        for (int i=0; i<mData.size(); i++){
            if (mData.get(i).getInt("id") ==  item.getInt("id")){
                mData.remove(i);
                mData.add(i, item);
                notifyItemChanged(i);

                //check = true;
                break;
            }

        }

    }


    @Override
    public void onBindViewHolder(final ProductAdapterViewHolder holder, int position) {
        holder.line.setVisibility(position == mData.size()-1? View.GONE: View.VISIBLE);
        holder.tvNote.setVisibility(Util.isEmpty(mData.get(position).getString("note"))? View.GONE : View.VISIBLE);
        holder.tvNote.setText(mData.get(position).getString("note"));
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvPrice.setText(Util.FormatMoney(mData.get(position).getDouble("unitPrice")));
        String baseprice = Util.isAdmin() ? (String.format(" (%s)", Util.FormatMoney(mData.get(position).getDouble("basePrice")))) : "";
        holder.tvBasePrice.setText(Util.FormatMoney(mData.get(position).getDouble("purchasePrice")) + baseprice);

        if (mData.get(position).getInt("deleted") == 1){
            holder.tvHide.setVisibility(View.VISIBLE);
            holder.tvMenu.setVisibility(View.GONE);
            holder.tvPrice.setVisibility(View.GONE);
        }else {
            holder.tvHide.setVisibility(View.GONE);
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvMenu.setVisibility(View.VISIBLE);
        }

        holder.tvGift.setVisibility(mData.get(position).getInt("promotion") == 1 ? View.VISIBLE : View.GONE);
        if (!Util.checkImageNull(mData.get(position).getString("image"))) {
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.imageProduct);

        } else {
            Glide.with(mContext).load(R.drawable.lub_logo_grey).centerCrop().into(holder.imageProduct);

        }

        if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
            holder.rlParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItem.onRespone(mData.get(holder.getAdapterPosition()), holder.getAdapterPosition());

                }
            });
        }

        holder.tvMenu.setVisibility(Util.isAdmin()? View.VISIBLE : View.GONE);
        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDropdow.createListDropdown(
                        holder.tvMenu,
                        DataUtil.createList3String(Util.getIconString(R.string.icon_copy, "  ", "Copy"),
                                Util.getIconString(R.string.icon_ban, "   ", "Ẩn sản phẩm"),
                                Util.getIconString(R.string.icon_delete, "   ", "Xoá sản phẩm")),
                        0,
                        true,
                        new CallbackClickAdapter() {
                            @Override
                            public void onRespone(String data, int pos) {
                                if (pos ==0){
                                    BaseModel product = BaseModel.copyToNewBaseModel(mData.get(holder.getAdapterPosition()));
                                    product.put("id", 0);
                                    product.put("name", mData.get(holder.getAdapterPosition()).getString("name") + " _copy");
                                    mCopy.onRespone(product, holder.getAdapterPosition());

                                }else if(pos ==1) {
                                    BaseModel param = createPostParam(ApiUtil.PRODUCT_ACTIVE(),
                                            String.format(ApiUtil.PRODUCT_ACTIVE_PARAM,
                                                    mData.get(holder.getAdapterPosition()).getInt("id"),
                                                    1),
                                            false, false);
                                    new GetPostMethod(param, new NewCallbackCustom() {
                                        @Override
                                        public void onResponse(BaseModel result, List<BaseModel> list) {
                                            mData.remove(holder.getAdapterPosition());
                                            mData.add(holder.getAdapterPosition(), result);
                                            notifyItemChanged(holder.getAdapterPosition());
                                        }

                                        @Override
                                        public void onError(String error) {

                                        }
                                    }, 1).execute();
                                }else if (pos==2){
                                    CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xoá sản phẩm " + mData.get(holder.getAdapterPosition()).getString("name") +" khỏi danh sách", "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
                                        @Override
                                        public void onRespone(Boolean result) {
                                            if (result) {
                                                BaseModel param = createGetParam(ApiUtil.PRODUCT_DELETE() + mData.get(holder.getAdapterPosition()).getString("id"), false);
                                                new GetPostMethod(param, new NewCallbackCustom() {
                                                    @Override
                                                    public void onResponse(BaseModel result, List<BaseModel> list) {
                                                        Util.getInstance().stopLoading(true);

                                                        mData.remove(holder.getAdapterPosition());
                                                        notifyItemRemoved(holder.getAdapterPosition());
                                                    }

                                                    @Override
                                                    public void onError(String error) {

                                                    }
                                                }, 1).execute();

                                            }

                                        }
                                    });
                                }

                            }

                        });

            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvBasePrice, tvPrice, tvGift,tvHide, tvMenu, tvNote;
        private RelativeLayout rlParent;
        private CircleImageView imageProduct;
        private View line;

        public ProductAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.product_item_name);
            tvNote = itemView.findViewById(R.id.product_item_note);
            tvBasePrice = (TextView) itemView.findViewById(R.id.product_item_base_price);
            tvPrice = (TextView) itemView.findViewById(R.id.product_item_unitprice);
            tvGift = (TextView) itemView.findViewById(R.id.product_item_gift);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.product_item_parent);
            imageProduct = itemView.findViewById(R.id.product_item_image);
            tvHide = itemView.findViewById(R.id.product_item_hide);
            tvMenu = itemView.findViewById(R.id.product_item_menu);
            line = itemView.findViewById(R.id.product_item_seperateline);
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
