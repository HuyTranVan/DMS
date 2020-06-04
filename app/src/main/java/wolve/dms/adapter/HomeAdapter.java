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

import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemAdapterViewHolder> {
    private ArrayList<JSONObject> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;

    public HomeAdapter(CallbackClickAdapter callbackClickAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = Constants.HomeItemList();
        mListener = callbackClickAdapter;
    }

    @Override
    public ItemAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_home_item, parent, false);
        //itemView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        return new ItemAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<JSONObject> list) {
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ItemAdapterViewHolder holder, final int position) {
        try {
            holder.tvIcon.setText(mData.get(position).getString("icon"));
            holder.tvTitle.setText(mData.get(position).getString("text"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRespone(holder.tvTitle.getText().toString(), position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ItemAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvIcon;
        private LinearLayout rlParent;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);
            tvIcon = (TextView) itemView.findViewById(R.id.item_home_icon);
            tvTitle = (TextView) itemView.findViewById(R.id.item_home_text);
            rlParent = (LinearLayout) itemView.findViewById(R.id.item_home_parent);
        }

    }

}
