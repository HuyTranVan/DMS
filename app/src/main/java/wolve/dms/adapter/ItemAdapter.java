package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 9/30/16.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ChoiceMethodViewHolder>{

    private List<BaseModel> mData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private CustomBottomDialog.PositionListener mListener;
    private String mKey;

    public interface CountListener{
        void onRespone(int count);
    }

    public ItemAdapter(List<BaseModel> data, String key, CustomBottomDialog.PositionListener mListener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mListener = mListener;
        this.mData = data;
        this.mKey = key;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ChoiceMethodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_listmethod_item, parent, false);
        return new ChoiceMethodViewHolder(itemView);
    }

    public class ChoiceMethodViewHolder extends RecyclerView.ViewHolder {
        private TextView text, line,  icon;

        public ChoiceMethodViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.list_method_text);
            line = (TextView) itemView.findViewById(R.id.list_method_line);
            icon = itemView.findViewById(R.id.list_method_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  mListener.onResponse(getAdapterPosition());

                }
            });

        }
    }


    @Override
    public void onBindViewHolder(final ChoiceMethodViewHolder holder, final int position) {
        holder.text.setText(mData.get(position).getString(mKey));
        holder.line.setVisibility(position == mData.size() -1?View.GONE:View.VISIBLE);
        if (mData.get(position).hasKey("icon")){
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setText(mData.get(position).getString("icon"));
        }else {
            holder.icon.setVisibility(View.GONE);
        }

    }



}

