package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class PriceSuggestAdapter extends RecyclerView.Adapter<PriceSuggestAdapter.StatusAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackDouble mListener;

    public PriceSuggestAdapter(List<BaseModel> list, CallbackDouble listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = listener;

        DataUtil.sortbyStringKey("createAt", mData, true);

    }

    @Override
    public StatusAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_price_suggest, parent, false);
        return new StatusAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatusAdapterViewHolder holder, final int position) {
        holder.tvText.setText(Util.FormatMoney(mData.get(position).getDouble("value")));
        holder.tvDate.setText(Util.DateString(mData.get(position).getLong("createAt")));

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.Result(mData.get(position).getDouble("value"));

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class StatusAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvText, tvDate;
        private LinearLayout lnParent;

        public StatusAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (LinearLayout) itemView.findViewById(R.id.price_suggest_parent);
            tvText = (TextView) itemView.findViewById(R.id.price_suggest_text);
            tvDate = (TextView) itemView.findViewById(R.id.price_suggest_date);
        }

    }

}
