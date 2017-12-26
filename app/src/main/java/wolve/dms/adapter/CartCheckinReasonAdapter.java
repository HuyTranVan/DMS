package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.Status;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartCheckinReasonAdapter extends RecyclerView.Adapter<CartCheckinReasonAdapter.ReasonAdapterViewHolder> {
    private List<Status> mStatus = new ArrayList<>();
    private List<String> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackStatus callbackStatus;

    public interface CallbackStatus {
        void Status(Status status);
        void UpdateOnly();
    }


    public CartCheckinReasonAdapter(List<Status> list, CallbackStatus callbackStatus) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.callbackStatus = callbackStatus ;
        //this.mStatus = list;

        for (int i=0; i<list.size(); i++){
            if (!list.get(i).getBoolean("defaultStatus")){
                this.mData.add(list.get(i).getString("name"));
                this.mStatus.add(list.get(i));
            }
        }
        mData.add("Chỉ lưu thông tin cập nhật");

    }

    @Override
    public ReasonAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_checkin_reason, parent, false);
        return new ReasonAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReasonAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position));

        holder.tvLine.setVisibility(position == mData.size() -1 ? View.GONE : View.VISIBLE);

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == mData.size()-1){
                    callbackStatus.UpdateOnly();
                }else {
                    callbackStatus.Status(mStatus.get(position));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ReasonAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvLine;
        private LinearLayout lnParent;

        public ReasonAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.checkin_reason_name);
            lnParent = (LinearLayout) itemView.findViewById(R.id.checkin_reason_parent);
            tvLine = itemView.findViewById(R.id.checkin_reason_line);
        }

    }


}
