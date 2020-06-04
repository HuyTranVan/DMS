package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ListUserChangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public ListUserChangeAdapter(List<BaseModel> list, CallbackObject listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;

        this.mData = list;
        mData.add(null);


    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_user_change_item, parent, false);
            return new ListUserChangeAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_user_change_new, parent, false);
            return new ListUserChangeAdapter.SetNewViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ListUserChangeAdapter.ItemViewHolder) {
            setItemRows((ListUserChangeAdapter.ItemViewHolder) viewHolder, position);

        } else if (viewHolder instanceof ListUserChangeAdapter.SetNewViewHolder) {
            setNewItem((ListUserChangeAdapter.SetNewViewHolder) viewHolder, position);

        }

    }

    private void setItemRows(ListUserChangeAdapter.ItemViewHolder holder, int position) {
        holder.tvName.setText(String.format("%s _ %s", mData.get(position).getString("displayName"),
                mData.get(position).getBaseModel("distributor").getString("name")));
        holder.tvPhone.setText(mData.get(position).getString("phone"));
        holder.tvRole.setText(User.getRoleString(mData.get(position).getInt("role")));

        if (!Util.checkImageNull(mData.get(position).getString("image"))) {
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.imgUser);
        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(mData.get(position));
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvPhone, tvRole;
        private CircleImageView imgUser;
        private View vLine;
        private RelativeLayout lnParent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.list_user_change_item_parent);
            tvName = itemView.findViewById(R.id.list_user_change_item_name);
            tvPhone = itemView.findViewById(R.id.list_user_change_item_phone);
            tvRole = itemView.findViewById(R.id.list_user_change_item_role);
            imgUser = itemView.findViewById(R.id.list_user_change_item_image);
            vLine = itemView.findViewById(R.id.seperateline);


        }
    }

    public static class SetNewViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout lnParent;

        public SetNewViewHolder(@NonNull View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.list_user_change_new_parent);
        }
    }

    private void setNewItem(ListUserChangeAdapter.SetNewViewHolder holder, int position) {
        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(null);
            }
        });
    }


}
