package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 9/30/16.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ChoiceMethodViewHolder> {

    private List<BaseModel> mData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private CustomBottomDialog.PositionListener mListener;
    private String mKey;

    public interface CountListener {
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
        private TextView text, icon;
        private View line;
        private CircleImageView image;

        public ChoiceMethodViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.list_method_text);
            line = (View) itemView.findViewById(R.id.list_method_line);
            icon = itemView.findViewById(R.id.list_method_icon);
            image = itemView.findViewById(R.id.list_method_image);

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
        holder.line.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        if (mData.get(position).hasKey("icon")) {
            holder.icon.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.icon.setText(mData.get(position).getString("icon"));
        }else if (mData.get(position).hasKey("image")){
            holder.icon.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(mData.get(position).getString("image")).placeholder(R.drawable.ic_user).fitCenter().into(holder.image);

        } else {
            holder.image.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
        }

    }


}

