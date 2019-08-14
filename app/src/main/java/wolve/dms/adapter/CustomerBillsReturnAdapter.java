package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerBillsReturnAdapter extends RecyclerView.Adapter<CustomerBillsReturnAdapter.CustomerBillsReturnAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CustomerBillsReturnAdapter(List<BaseModel> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();

        DataUtil.sortbyStringKey("createAt", mData, true);

    }

    @Override
    public CustomerBillsReturnAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_customer_bills_return_item, parent, false);
        return new CustomerBillsReturnAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerBillsReturnAdapterViewHolder holder, final int position) {
        try {
            holder.tvTitle.setText(String.format("Trả hàng %s", Util.DateHourString(mData.get(position).getLong("createAt"))));
            holder.tvTotal.setText(String.format("%s đ",Util.FormatMoney(mData.get(position).getDouble("total"))));

            final List<BaseModel> listBillDetail = new ArrayList<>(DataUtil.array2ListBaseModel(new JSONArray(mData.get(position).getString("billDetails"))));

            CustomerBillsDetailAdapter adapter = new CustomerBillsDetailAdapter(listBillDetail);
            Util.createLinearRV(holder.rvReturn, adapter);

        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerBillsReturnAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle , tvTotal;
        private RecyclerView rvReturn;
        private LinearLayout lnParent;

        public CustomerBillsReturnAdapterViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.bills_return_item_title);
            tvTotal = (TextView) itemView.findViewById(R.id.bills_return_item_total);
            rvReturn = (RecyclerView) itemView.findViewById(R.id.bills_return_item_rvproduct);
            lnParent = (LinearLayout) itemView.findViewById(R.id.bills_return_item_parent);

        }

    }


}
