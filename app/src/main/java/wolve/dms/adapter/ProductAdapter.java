package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CTextIcon;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder> {
    private List<Product> mData = new ArrayList<>();
    private ProductGroup mGroup;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;
    private CallbackDeleteAdapter mDeleteListener;

    public ProductAdapter(ProductGroup group, List<Product> list, CallbackClickAdapter callbackClickAdapter, CallbackDeleteAdapter callbackDeleteAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
//        this.mData = list;
        this.mGroup = group;
        this.mListener = callbackClickAdapter;
        this.mDeleteListener = callbackDeleteAdapter;

        for (int i=0; i< list.size(); i++){
            ProductGroup productGroup = new ProductGroup(list.get(i).getJsonObject("productGroup"));
            if (productGroup.getInt("id")== group.getInt("id")){
                mData.add(list.get(i));
            }
        }

    }

    @Override
    public ProductAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_product_item, parent, false);
        return new ProductAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<Product> list){
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ProductAdapterViewHolder holder, final int position) {
        try {
            holder.tvName.setText(mData.get(position).getString("name"));
            holder.tvPrice.setText(Util.FormatMoney(mData.get(position).getDouble("unitPrice")));
            holder.tvGroup.setText(new JSONObject(mData.get(position).getString("productGroup")).getString("name"));
            holder.tvGift.setVisibility(mData.get(position).getBoolean("promotion")?View.VISIBLE : View.GONE);
            if (!Util.checkImageNull(mData.get(position).getString("image"))){
                Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.imageProduct);

            }else {
                Glide.with(mContext).load( R.drawable.ic_wolver).centerCrop().into(holder.imageProduct);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        holder.rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRespone(mData.get(position).ProducttoString() , position);

            }
        });

        holder.rlParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa sản phẩm " + mData.get(position).getString("name"), "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        String param = String.valueOf(mData.get(position).getInt("id"));
                        ProductConnect.DeleteProduct(param, new CallbackJSONObject() {
                            @Override
                            public void onResponse(JSONObject result) {
                                mDeleteListener.onDelete(mData.get(position).ProducttoString(), position);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, true);
                    }
                });

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvGroup, tvPrice;
        private CTextIcon tvGift;
        private RelativeLayout rlParent;
        private CircleImageView imageProduct;

        public ProductAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.product_item_name);
            tvGroup = (TextView) itemView.findViewById(R.id.product_item_group);
            tvPrice = (TextView) itemView.findViewById(R.id.product_item_unitprice);
            tvGift = (CTextIcon) itemView.findViewById(R.id.product_item_gift);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.product_item_parent);
            imageProduct = itemView.findViewById(R.id.product_item_image);
        }

    }

}
