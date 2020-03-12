package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductSelectAdapter extends RecyclerView.Adapter<ProductSelectAdapter.StatusAdapterViewHolder> {
    private List<String> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    //private CallbackDouble mListener;

    public ProductSelectAdapter(List<String> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        //this.mListener = listener;

        //Collections.reverse(mData);

    }

    @Override
    public StatusAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_product_select_item, parent, false);
        return new StatusAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatusAdapterViewHolder holder, final int position) {
        holder.tvText.setText(mData.get(position));

        holder.tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.remove(position);
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class StatusAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvText;
        private CTextIcon tvClose;
        private RelativeLayout lnParent;

        public StatusAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.product_select_parent);
            tvText = (TextView) itemView.findViewById(R.id.product_select_text);
            tvClose= (CTextIcon) itemView.findViewById(R.id.product_select_close);

        }

    }

    public void addItem(String item){
        mData.add(item);
        notifyDataSetChanged();
    }

    public List<String> getData(){
        return mData;

    }

}
