package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import wolve.dms.R;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductGroupAdapter extends RecyclerView.Adapter<ProductGroupAdapter.ProductGroupAdapterViewHolder> {
    private ArrayList<ProductGroup> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;
    private CallbackDeleteAdapter mDeleteListener;

    public ProductGroupAdapter(ArrayList<ProductGroup> list,  CallbackClickAdapter callbackClickAdapter, CallbackDeleteAdapter callbackDeleteAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        mListener = callbackClickAdapter;
        this.mDeleteListener = callbackDeleteAdapter;

        DataUtil.sortProductGroup(mData, false);
    }

    @Override
    public ProductGroupAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_productgroup_item, parent, false);
        return new ProductGroupAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<ProductGroup> list){
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ProductGroupAdapterViewHolder holder, final int position) {
        holder.tvGroupName.setText(mData.get(position).getString("name"));

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRespone(mData.get(position).ProductGrouptoString() , position);

            }
        });

        holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa nhóm " + mData.get(position).getString("name"), "XÓA","HỦY", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        String param = String.valueOf(mData.get(position).getInt("id"));
                        ProductConnect.DeleteProductGroup(param, new CallbackJSONObject() {
                            @Override
                            public void onResponse(JSONObject result) {
                                mDeleteListener.onDelete(mData.get(position).ProductGrouptoString(), position);
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


    public class ProductGroupAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName;
        private LinearLayout lnParent;

        public ProductGroupAdapterViewHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.productgroup_item_name);
            lnParent = (LinearLayout) itemView.findViewById(R.id.productgroup_item_parent);
        }

    }

}
