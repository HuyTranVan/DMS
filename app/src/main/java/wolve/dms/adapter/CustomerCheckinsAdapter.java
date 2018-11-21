package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.Checkin;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CustomerCheckinsAdapter extends RecyclerView.Adapter<CustomerCheckinsAdapter.CheckinsAdapterViewHolder> {
    private List<Checkin> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CustomerCheckinsAdapter(List<Checkin> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        Collections.reverse(mData);

    }

    @Override
    public CheckinsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_customer_checkins_item, parent, false);
        return new CheckinsAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<Checkin> list){
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final CheckinsAdapterViewHolder holder, final int position) {
        try {
            JSONObject user = new JSONObject(mData.get(position).getString("user"));
            holder.tvNumber.setText(String.valueOf(mData.size()-position));
            holder.tvContent.setText(mData.get(position).getString("note"));
            holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
            holder.tvEmployee.setText(String.format("Nhân viên: %s", user.getString("displayName")));

//            holder.tvName.setText(object.getString("name"));
//            holder.tvNote.setText(mData.get(position).getString("note").equals("")? "--":mData.get(position).getString("note"));
//            holder.tvDate.setText(Util.DateMonthString(mData.get(position).getLong("updateAt")));
//            holder.tvYear.setText(Util.YearString(mData.get(position).getLong("updateAt")));
//            holder.tvHour.setText(Util.HourString(mData.get(position).getLong("updateAt")));



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CheckinsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvEmployee, tvContent, tvDate, tvTime;
        private LinearLayout lnParent;

        public CheckinsAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (LinearLayout) itemView.findViewById(R.id.checkins_item_content_parent);
            tvDate = (TextView) itemView.findViewById(R.id.checkins_item_date);
            tvNumber = (TextView) itemView.findViewById(R.id.checkins_item_number);
            tvEmployee = (TextView) itemView.findViewById(R.id.checkins_item_employee);
            tvContent = (TextView) itemView.findViewById(R.id.checkins_item_content);
//            tvTime = (TextView) itemView.findViewById(R.id.checkins_item_time);

        }

    }

}
