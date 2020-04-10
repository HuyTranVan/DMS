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
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Checkin;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_CheckinsAdapter extends RecyclerView.Adapter<Customer_CheckinsAdapter.CheckinsAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBoolean mListener;

    public Customer_CheckinsAdapter(List<BaseModel> list, CallbackBoolean listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = listener;

        DataUtil.sortbyStringKey("create", mData, true);

    }

    @Override
    public CheckinsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_customer_checkins_item, parent, false);
        return new CheckinsAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CheckinsAdapterViewHolder holder, final int position) {
        String note = mData.get(position).getInt("meetOwner") == 0?
                "Không gặp chủ nhà. " + mData.get(position).getString("note") : mData.get(position).getString("note");
        holder.tvContent.setText(note);
        holder.tvDate.setText(Util.getIconString(R.string.icon_district, "   ",Util.DateHourString(mData.get(position).getLong("createAt"))));
        holder.tvEmployee.setText(Util.getIconString(R.string.icon_username, "   ",mData.get(position).getBaseModel("user").getString("displayName")));

        holder.line.setVisibility(mData.size() -1 == position ?View.GONE : View.VISIBLE );
        holder.tvRating.setText( Util.getStringIcon(mData.get(position).getString("rating"),"", R.string.icon_star));
        if (Util.isAdmin()){
            holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View view) {
                   CustomCenterDialog.alertWithCancelButton(null, String.format("Xác nhận xóa Checkin ngày %s", Util.DateHourString(mData.get(position).getLong("createAt"))) , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                       @Override
                       public void onRespone(Boolean result) {
                           CustomerConnect.DeleteCheckin(mData.get(position).getString("id"), new CallbackCustom() {
                               @Override
                               public void onResponse(BaseModel result) {
                                   Util.getInstance().stopLoading(true);
                                   Util.showToast("Xóa thành công!");
                                   mData.remove(position);
                                   notifyDataSetChanged();
                                   mListener.onRespone(true);

                               }

                               @Override
                               public void onError(String error) {
                                   Util.getInstance().stopLoading(true);
                                   Constants.throwError(error);

                               }


                           }, true);
                       }
                   });

                   return true;
               }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CheckinsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvContent, tvTime, tvEmployee, tvDate, tvRating;
        private LinearLayout lnParent;
        private View line, under;

        public CheckinsAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (LinearLayout) itemView.findViewById(R.id.checkins_item_parent);
            tvDate = (TextView) itemView.findViewById(R.id.checkins_item_date);
            tvRating = (TextView) itemView.findViewById(R.id.checkins_item_rating);
            tvEmployee = (TextView) itemView.findViewById(R.id.checkins_item_employee);
            tvContent = (TextView) itemView.findViewById(R.id.checkins_item_content);
            line = (View) itemView.findViewById(R.id.checkins_item_line);
            //under = (View) itemView.findViewById(R.id.checkins_item_under);

        }

    }

}
