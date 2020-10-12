package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.http.client.UserTokenHandler;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class WarehouseImportAdapter extends RecyclerView.Adapter<WarehouseImportAdapter.WarehouseImportViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;

    public WarehouseImportAdapter(List<BaseModel> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        //this.mListener = listener;

    }

    @Override
    public WarehouseImportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_import_warehouse, parent, false);
        return new WarehouseImportViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final WarehouseImportViewHolder holder, final int position) {
        holder.line.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        holder.tvName.setText(mData.get(position).hasKey("productName")? mData.get(position).getString("productName") : mData.get(position).getString("name"));
        holder.tvGroup.setText(mData.get(position).getBaseModel("productGroup").getString("name"));
        holder.tvQuantityLimit.setText(mData.get(position).getString("quantity"));
        holder.edQuantity.setText(mData.get(position).getString("quantity"));

        Util.textEvent(holder.edQuantity, new CallbackString() {
            @Override
            public void Result(String s) {
                mData.get(position).put("quantity", s.equals("")? 0 : s);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class WarehouseImportViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvGroup , tvQuantityLimit;
        private EditText edQuantity;
        private View line;


        public WarehouseImportViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.import_warehouse_item_name);
            tvGroup = (TextView) itemView.findViewById(R.id.import_warehouse_item_group);
            tvQuantityLimit = (TextView) itemView.findViewById(R.id.import_warehouse_item_currentquantity);
            edQuantity = itemView.findViewById(R.id.import_warehouse_item_number);
            line = itemView.findViewById(R.id.line);

        }

    }

    public List<BaseModel> getmData(){
        return mData;

    }

}
