package wolve.dms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wolve.dms.R;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CTextView;
import wolve.dms.models.Status;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ReasonAdapterViewHolder> {
    private ArrayList<Status> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;


    public ReasonAdapter( ArrayList<Status> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();

        for (int i=0; i<list.size(); i++){
            if (!list.get(i).getBoolean("defaultStatus")){
                this.mData.add(list.get(i));
            }
        }


        resetReasonList();

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

    public int getCheckedReason(){
        int pos = 0;
        for (int i=0; i<mData.size(); i++){
            if (mData.get(i).getBoolean("checked")){
                pos= mData.get(i).getInt("id");
                break;
            }
        }
        return pos;
    }


    @Override
    public void onBindViewHolder(final ReasonAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.cbCheck.setChecked(mData.get(position).getBoolean("checked"));

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cbCheck.setOnCheckedChangeListener(null);
                resetReasonList();
                try {
                    mData.get(position).put("checked", true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        });

        holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    holder.cbCheck.setOnCheckedChangeListener(null);
                    resetReasonList();
                    try {
                        mData.get(position).put("checked", true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ReasonAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private CheckBox cbCheck;
        private LinearLayout lnParent;

        public ReasonAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.checkin_reason_name);
            cbCheck = (CheckBox) itemView.findViewById(R.id.checkin_reason_checkbox);
            lnParent = (LinearLayout) itemView.findViewById(R.id.checkin_reason_parent);
        }

    }

    private void resetReasonList(){
        for (int i=0; i<mData.size(); i++){
            try {
                mData.get(i).put("checked", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
