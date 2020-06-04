package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private BaseModel mGroup;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;
    private CallbackDeleteAdapter mDeleteListener;

    public UserAdapter(List<BaseModel> list, CallbackClickAdapter callbackClickAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        mListener = callbackClickAdapter;

    }

    @Override
    public UserAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_user_item, parent, false);
        return new UserAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("displayName"));
        holder.tvPhone.setText(Util.FormatPhone(mData.get(position).getString("phone")));
        holder.tvRole.setText(User.getRoleString(mData.get(position).getInt("role")));

        holder.line.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        if (!Util.checkImageNull(mData.get(position).getString("image"))) {
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.imageUser);

        }
        holder.rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRespone(mData.get(position).BaseModelstoString(), mData.get(position).getInt("id"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class UserAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvRole, tvPhone;
        private RelativeLayout rlParent;
        private CircleImageView imageUser;
        private View line;

        public UserAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.user_item_name);
            tvRole = itemView.findViewById(R.id.user_item_role);
            tvPhone = itemView.findViewById(R.id.user_item_phone);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.user_item_parent);
            imageUser = itemView.findViewById(R.id.user_item_image);
            line = itemView.findViewById(R.id.user_item_seperateline);

        }

    }

}
