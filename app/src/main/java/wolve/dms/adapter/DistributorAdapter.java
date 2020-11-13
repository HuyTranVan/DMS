package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;


/**
 * Created by tranhuy on 5/24/17.
 */

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.ProductAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private BaseModel mGroup;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;

    public DistributorAdapter(List<BaseModel> list, CallbackObject listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mData = list;

    }

    @Override
    public ProductAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_distributor_item, parent, false);
        return new ProductAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductAdapterViewHolder holder, final int position) {
        holder.line.setVisibility(position == mData.size() -1? View.GONE : View.VISIBLE);
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvLocation.setText(mData.get(position).getBaseModel("province").getString("name"));

        if (!Util.checkImageNull(mData.get(position).getString("image"))) {
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.imageDistributor);

        } else {
            Glide.with(mContext).load(R.drawable.lub_logo_red_50).centerCrop().into(holder.imageDistributor);

        }

        holder.rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResponse(mData.get(position));

            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        private View line;
        private TextView tvName, tvLocation;
        private RelativeLayout rlParent;
        private ImageView imageDistributor;

        public ProductAdapterViewHolder(View itemView) {
            super(itemView);
            line = itemView.findViewById(R.id.line);
            tvName = (TextView) itemView.findViewById(R.id.distributor_item_name);
            tvLocation = (TextView) itemView.findViewById(R.id.distributor_item_location);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.distributor_item_parent);
            imageDistributor = itemView.findViewById(R.id.distributor_item_image);
        }

    }

    public List<BaseModel> getmData(){
        return mData;
    }

    public void updateDistributor(BaseModel distributor){
        boolean checkExist = false;
        for (int i=0; i< mData.size(); i++){
            if (mData.get(i).getInt("id") == distributor.getInt("id")){
                mData.remove(i);
                mData.add(i, distributor);
                checkExist = true;
                notifyItemChanged(i);
                break;
            }
        }

        if (!checkExist){
            mData.add(distributor);
            notifyItemInserted(mData.size() -1);
        }
    }

}
