package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 9/30/16.
 */
public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ChoiceMethodViewHolder>{

    private List<String> mData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private CustomBottomDialog.PositionListener mListener;

    public interface CountListener{
        void onRespone(int count);
    }

    public StringAdapter(List<String> data, CustomBottomDialog.PositionListener mListener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mListener = mListener;
        this.mData = data;
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
        private TextView text, line;

        public ChoiceMethodViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.list_method_text);
            line = (TextView) itemView.findViewById(R.id.list_method_line);

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
        holder.text.setText(mData.get(position));
        holder.line.setVisibility(position == mData.size() -1?View.GONE:View.VISIBLE);

    }



}

