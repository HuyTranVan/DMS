package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Checkin;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_CheckinsAdapter extends RecyclerView.Adapter<Customer_CheckinsAdapter.CheckinsAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public Customer_CheckinsAdapter(List<BaseModel> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;

        DataUtil.sortbyStringKey("create", mData, true);

    }

    @Override
    public CheckinsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_customer_checkins_item, parent, false);
        return new CheckinsAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CheckinsAdapterViewHolder holder, final int position) {
        BaseModel user = mData.get(position).getBaseModel("user");

        holder.tvNumber.setText(String.valueOf(mData.size()-position));
        holder.tvContent.setText(mData.get(position).getString("note"));
        holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
        holder.tvEmployee.setText(String.format("Nhân viên: %s", user.getString("displayName")));

        holder.line.setVisibility(position == mData.size() -1 ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CheckinsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvEmployee, tvContent, tvDate, tvTime;
        private LinearLayout lnParent;
        private View line;

        public CheckinsAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (LinearLayout) itemView.findViewById(R.id.checkins_item_content_parent);
            tvDate = (TextView) itemView.findViewById(R.id.checkins_item_date);
            tvNumber = (TextView) itemView.findViewById(R.id.checkins_item_number);
            tvEmployee = (TextView) itemView.findViewById(R.id.checkins_item_employee);
            tvContent = (TextView) itemView.findViewById(R.id.checkins_item_content);
            line = (View) itemView.findViewById(R.id.checkins_item_line);

        }

    }

}
