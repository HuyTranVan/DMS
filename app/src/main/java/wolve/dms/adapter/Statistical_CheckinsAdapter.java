package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Statistical_CheckinsAdapter extends RecyclerView.Adapter<Statistical_CheckinsAdapter.StatisticalBillsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public Statistical_CheckinsAdapter(List<BaseModel> data) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();

        Collections.reverse(mData);

    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_checkin_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        try {
            holder.tvNumber.setText(String.valueOf(mData.size() -position));
            holder.tvsignBoard.setText(Constants.getShopName(mData.get(position).getString("shopType") ) + " " + mData.get(position).getString("signBoard"));
            holder.tvDistrict.setText(mData.get(position).getString("street") + " - " + mData.get(position).getString("district"));

            //JSONObject objectCheckin = mData.get(position).getJsonObject("checkin");

            String user = String.format("Nhân viên: %s",mData.get(position).getJsonObject("user").getString("displayName"));
            String hour = Util.DateHourString(mData.get(position).getLong("createAt"));
            String time = mData.get(position).getString("note").contains("[") ?
                    mData.get(position).getString("note").substring(mData.get(position).getString("note").indexOf("[") + 1, mData.get(position).getString("note").indexOf("]")):
                    "";
            holder.tvUser.setText(String.format("%s         %s   %s", user, hour, time));

//            holder.tvTime.setText(String.format("Thời gian Checkin: %s", ""));
            String note = mData.get(position).getString("note").contains("]")? mData.get(position).getString("note").split("]")[1] :   mData.get(position).getString("note");
            holder.tvContent.setText(note);

            holder.vLine.setVisibility(position == mData.size()-1? View.GONE:View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvsignBoard, tvDistrict, tvUser, tvTime, tvContent;
        private View vLine;
        private LinearLayout lnParent;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.statistical_checkin_item_number);
            tvsignBoard = itemView.findViewById(R.id.statistical_checkin_item_signboard);
            tvDistrict = itemView.findViewById(R.id.statistical_checkin_item_district);
            tvUser = itemView.findViewById(R.id.statistical_checkin_item_user);
//            tvTime = itemView.findViewById(R.id.statistical_checkin_item_time);
            tvContent = itemView.findViewById(R.id.statistical_checkin_item_content);
            vLine = itemView.findViewById(R.id.statistical_checkin_item_line);
//            vLineUpper = itemView.findViewById(R.id.statistical_checkin_item_upper);


        }

    }


}
