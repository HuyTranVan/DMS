package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Status;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartCheckinReasonAdapter extends RecyclerView.Adapter<CartCheckinReasonAdapter.ReasonAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private ReasonCallback mListener;

    public interface ReasonCallback{
        void onResult(BaseModel result, int position);
    }

    public CartCheckinReasonAdapter(List<BaseModel> list, ReasonCallback callback) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = callback ;
        this.mData = list;



    }

    @Override
    public ReasonAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_checkin_reason, parent, false);
        return new ReasonAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReasonAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("content"));
        holder.line.setVisibility(position == mData.size() -1 ? View.GONE : View.VISIBLE);

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResult(mData.get(position), position);


            }
        });

        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.remove(position);
                CustomSQL.setListBaseModel(Constants.STATUS, mData);
                notifyDataSetChanged();
//                notifyItemRemoved(position);
//                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ReasonAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, btnClose;
        private View line;
        private RelativeLayout lnParent;

        public ReasonAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.checkin_reason_name);
            btnClose = (TextView) itemView.findViewById(R.id.checkin_reason_close);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.checkin_reason_parent);
            line = itemView.findViewById(R.id.checkin_reason_line);
        }

    }


}
