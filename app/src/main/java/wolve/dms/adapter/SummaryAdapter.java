package wolve.dms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ItemAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;

    public SummaryAdapter(List<BaseModel> list, CallbackClickAdapter callbackClickAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = callbackClickAdapter;
    }

    @Override
    public ItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_summary_item, parent, false);
        //itemView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        return new ItemAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemAdapterViewHolder holder, final int position) {
        holder.line.setVisibility(position == mData.size() -1 ? View.GONE : View.VISIBLE);

        holder.tvname.setText(mData.get(position).getString("title"));
        holder.tvIcon.setTextColor(Color.parseColor(mData.get(position).getString("color")));

        holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getDouble("value")));
        holder.tvTotal.setTextColor(Color.parseColor(mData.get(position).getString("color")));



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ItemAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIcon, tvname, tvTotal;
        private View line;


        public ItemAdapterViewHolder(View itemView) {
            super(itemView);
            tvIcon = (TextView) itemView.findViewById(R.id.summary_item_icon);
            tvname = (TextView) itemView.findViewById(R.id.summary_item_name);
            tvTotal = (TextView) itemView.findViewById(R.id.summary_item_total);
            line = itemView.findViewById(R.id.line);


        }

    }

    public void reloadItem(){
        notifyDataSetChanged();
    }


}
