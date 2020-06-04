package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.Util;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_SearchAdapter extends RecyclerView.Adapter<Customer_SearchAdapter.CustomerSearchViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBaseModel mListener;
    private CustomBottomDialog.FourMethodListener mListerner;

    public Customer_SearchAdapter(List<BaseModel> data, CallbackBaseModel listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;

    }

    @Override
    public CustomerSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_item, parent, false);
        return new CustomerSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerSearchViewHolder holder, final int position) {
        holder.tvMainText.setText(String.format("%s - %s", mData.get(position).getString("signBoard"), mData.get(position).getString("name")));
        String address = String.format("%s %s - %s", mData.get(position).getString("address"), mData.get(position).getString("street"), mData.get(position).getString("district"));
        holder.tvSecondText.setText(address);
        holder.tvPhone.setText(mData.get(position).getString("phone"));
        holder.tvLine.setVisibility(position == mData.size() - 1 ? GONE : VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CustomerSearchViewHolder extends RecyclerView.ViewHolder {
        TextView tvMainText, tvSecondText, tvPhone, tvLine;

        public CustomerSearchViewHolder(View itemView) {
            super(itemView);
            tvMainText = itemView.findViewById(R.id.suggestion_maintext);
            tvSecondText = itemView.findViewById(R.id.suggestion_secondtext);
            tvPhone = itemView.findViewById(R.id.suggestion_contact);
            tvLine = itemView.findViewById(R.id.suggestion_line);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onResponse(mData.get(getAdapterPosition()));

                }
            });

        }

    }

}
