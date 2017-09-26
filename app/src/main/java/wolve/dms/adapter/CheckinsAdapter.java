package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CTextView;
import wolve.dms.models.Checkin;
import wolve.dms.models.Product;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CheckinsAdapter extends RecyclerView.Adapter<CheckinsAdapter.CheckinsAdapterViewHolder> {
    private List<Checkin> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
//    private CallbackClickAdapter mListener;

    public CheckinsAdapter(List<Checkin> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        //this.mListener = callbackClickAdapter;

    }

    @Override
    public CheckinsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_checkins_item, parent, false);
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
            holder.tvNote.setVisibility(mData.get(position).getString("note").equals("")? View.GONE : View.VISIBLE);
            holder.tvNote.setText(mData.get(position).getString("note"));
            holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("updateAt")));
            holder.rlParent.setBackground(( position % 2 ) == 0 ?  mContext.getResources().getDrawable(R.color.colorGray):
                    mContext.getResources().getDrawable(R.color.colorWhite));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CheckinsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvNote, tvDate;
        private RelativeLayout rlParent;

        public CheckinsAdapterViewHolder(View itemView) {
            super(itemView);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.checkins_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.checkins_item_name);
            tvNote = (TextView) itemView.findViewById(R.id.checkins_item_note);
            tvDate = (TextView) itemView.findViewById(R.id.checkins_item_date);
        }

    }

}
