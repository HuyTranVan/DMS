package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wolve.dms.R;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CTextView;
import wolve.dms.models.Product;
import wolve.dms.utils.CustomDialog;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapterViewHolder> {
    private ArrayList<Product> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;
    private CallbackDeleteAdapter mDeleteListener;

    public ProductAdapter( ArrayList<Product> list, CallbackClickAdapter callbackClickAdapter, CallbackDeleteAdapter callbackDeleteAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = callbackClickAdapter;
        this.mDeleteListener = callbackDeleteAdapter;

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
                CustomDialog.alertWithCancelButton(null, "Bạn muốn xóa sản phẩm " + mData.get(position).getString("name"), "ĐỒNG Ý","HỦY", new CallbackBoolean() {
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
        private CTextView tvGift;
        private RelativeLayout rlParent;

        public ProductAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.product_item_name);
            tvGroup = (TextView) itemView.findViewById(R.id.product_item_group);
            tvPrice = (TextView) itemView.findViewById(R.id.product_item_unitprice);
            tvGift = (CTextView) itemView.findViewById(R.id.product_item_gift);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.product_item_parent);
        }

    }

}