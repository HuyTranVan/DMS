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
import wolve.dms.callback.CallbackStatus;
import wolve.dms.models.Status;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartCheckinReasonAdapter extends RecyclerView.Adapter<CartCheckinReasonAdapter.ReasonAdapterViewHolder> {
    private List<Status> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackStatus callbackStatus;


    public CartCheckinReasonAdapter(List<Status> list, CallbackStatus callbackStatus) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.callbackStatus = callbackStatus ;

        for (int i=0; i<list.size(); i++){
            if (!list.get(i).getBoolean("defaultStatus")){
                this.mData.add(list.get(i));
            }
        }


    }

    @Override
    public ReasonAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_checkin_reason, parent, false);
        return new ReasonAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<Status> list){
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ReasonAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackStatus.Status(mData.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ReasonAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private LinearLayout lnParent;

        public ReasonAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.checkin_reason_name);
            lnParent = (LinearLayout) itemView.findViewById(R.id.checkin_reason_parent);
        }

    }


}
