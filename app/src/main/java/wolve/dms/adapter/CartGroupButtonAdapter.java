package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class CartGroupButtonAdapter extends RecyclerView.Adapter<CartGroupButtonAdapter.CartGroupButtonViewHolder> {
    private List<ProductGroup> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;

    public CartGroupButtonAdapter(List<ProductGroup> list, CallbackClickAdapter callbackClickAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        mListener = callbackClickAdapter;
    }

    @Override
    public CartGroupButtonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cart_button_group, parent, false);
        return new CartGroupButtonViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final CartGroupButtonViewHolder holder, final int position) {
        holder.tvGroupName.setText(mData.get(position).getString("name"));
        switch ((position+1) %4){
            case 0:
                holder.tvGroupName.setBackground(mContext.getResources().getDrawable(R.drawable.btn_choice_blue));
                holder.tvGroupName.setTextColor(mContext.getResources().getColor(R.color.colorBlueDark));
                break;

            case 1:
                holder.tvGroupName.setBackground(mContext.getResources().getDrawable(R.drawable.btn_choice_red));
                holder.tvGroupName.setTextColor(mContext.getResources().getColor(R.color.colorRedDark));
                break;

            case 2:
                holder.tvGroupName.setBackground(mContext.getResources().getDrawable(R.drawable.btn_choice_green));
                holder.tvGroupName.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                break;

            case 3:
                holder.tvGroupName.setBackground(mContext.getResources().getDrawable(R.drawable.btn_choice_orange));
                holder.tvGroupName.setTextColor(mContext.getResources().getColor(R.color.orange_dark));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class CartGroupButtonViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName;

        public CartGroupButtonViewHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.cart_button_group_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRespone(mData.get(getAdapterPosition()).ProductGrouptoString() , getAdapterPosition());
                }
            });
        }

    }

}
