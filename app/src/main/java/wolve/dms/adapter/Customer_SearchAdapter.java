package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.activities.SearchCustomerFragment;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_SearchAdapter extends RecyclerView.Adapter<Customer_SearchAdapter.CustomerSearchViewHolder>  implements Filterable  {
    private List<BaseModel> mData = new ArrayList<>();
    private List<BaseModel> baseData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBaseModel mListener;
    private CallbackString mPhoneListener;
    private  CallbackInt mCount;
    private boolean showPhone;
    private String text ="";

    public Customer_SearchAdapter(List<BaseModel> data, CallbackBaseModel listener, CallbackString phone, CallbackInt count) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.baseData = data;
        this.mData = baseData;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mPhoneListener = phone;
        this.showPhone = false;
        this.mCount = count;

        mCount.onResponse(mData.size());
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
        String phone = Util.FormatPhone(mData.get(position).getString("phone"));
        if (showPhone){
            holder.tvSecondText.setText(phone);
            DataUtil.setHighLightedText(holder.tvSecondText, text);


        }else {
            holder.tvSecondText.setText(address);
        }

        holder.tvPhone.setVisibility(Util.isPhoneFormat(mData.get(position).getString("phone")) != null ? VISIBLE : GONE);
        holder.tvLine.setVisibility(position == mData.size() - 1 ? GONE : VISIBLE);
        holder.tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoneListener.Result(mData.get(position).getString("phone"));
            }
        });

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

    public void reupdateList(String filter , List<BaseModel> list){
        baseData = new ArrayList<>(list);
        getFilter().filter(filter);

    }

    public void setShowPhone(boolean show){
        showPhone = show;

    }

    public void setTextHighligh(String text){
        this.text = text;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.equals(Constants.ALL_FILTER)) {
                    mData = baseData;

                } else {
                    List<BaseModel> listTemp = new ArrayList<>();
                    for (BaseModel row : baseData) {
                        if (row.getString("district").toLowerCase().equals(charString.toLowerCase())) {
                            listTemp.add(row);
                        }

                    }

                    mData = new ArrayList<>(listTemp);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (ArrayList<BaseModel>) filterResults.values;
                notifyDataSetChanged();
                mCount.onResponse(mData.size());
            }
        };
    }



}
