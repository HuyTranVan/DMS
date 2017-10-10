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
            JSONObject object = new JSONObject(mData.get(position).getString("status"));
            holder.tvName.setText(object.getString("name"));
            holder.tvNote.setText(mData.get(position).getString("note").equals("")? "--":mData.get(position).getString("note"));
            holder.tvDate.setText(Util.DateMonthString(mData.get(position).getLong("updateAt")));
            holder.tvYear.setText(Util.YearString(mData.get(position).getLong("updateAt")));
            holder.tvHour.setText(Util.HourString(mData.get(position).getLong("updateAt")));


            holder.lnParent.setBackground(( position % 2 ) == 0 ?  mContext.getResources().getDrawable(R.drawable.colorgrey_corner):
                    mContext.getResources().getDrawable(R.drawable.colorwhite_bordergrey_corner));

            if (mData.size()==1){
                holder.vUpper.setVisibility(View.GONE);
                holder.vUnder.setVisibility(View.GONE);
            }else if(position ==0){
                holder.vUpper.setVisibility(View.GONE);
            }else if (position==mData.size()-1){
                holder.vUnder.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CheckinsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvNote, tvDate, tvYear, tvHour;
        private LinearLayout lnParent;
        private View vUpper, vUnder;

        public CheckinsAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (LinearLayout) itemView.findViewById(R.id.checkins_item_content_parent);
            tvName = (TextView) itemView.findViewById(R.id.checkins_item_name);
            tvNote = (TextView) itemView.findViewById(R.id.checkins_item_note);
            tvDate = (TextView) itemView.findViewById(R.id.checkins_item_date);
            tvHour = (TextView) itemView.findViewById(R.id.checkins_item_hour);
            tvYear = (TextView) itemView.findViewById(R.id.checkins_item_year);
            vUpper = (View) itemView.findViewById(R.id.checkins_item_upper);
            vUnder = (View) itemView.findViewById(R.id.checkins_item_under);
        }

    }

}
