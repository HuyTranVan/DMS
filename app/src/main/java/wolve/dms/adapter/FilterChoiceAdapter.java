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
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 9/30/16.
 */
public class FilterChoiceAdapter extends RecyclerView.Adapter<FilterChoiceAdapter.ChoiceMethodViewHolder>{

    private List<String> mData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private CallbackString mListener;


    public FilterChoiceAdapter(CallbackString listener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mListener = listener;

        List<String> list = new ArrayList<>();
        list.add(Constants.ALL_FILTER);
        list.add(Constants.FILTER_BY_DATE);
        list.add(Constants.FILTER_BY_MONTH);
        list.add(Constants.FILTER_BY_YEAR);

        this.mData = list;

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ChoiceMethodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_filterchoice_item, parent, false);
        return new ChoiceMethodViewHolder(itemView);
    }

    public class ChoiceMethodViewHolder extends RecyclerView.ViewHolder {
        private TextView text, icon;

        public ChoiceMethodViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.filterchoice_text);
            icon = itemView.findViewById(R.id.filterchoice_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  mListener.Result(mData.get(getAdapterPosition()));

                }
            });

        }
    }


    @Override
    public void onBindViewHolder(final ChoiceMethodViewHolder holder, final int position) {
        holder.text.setText(mData.get(position));


    }



}

