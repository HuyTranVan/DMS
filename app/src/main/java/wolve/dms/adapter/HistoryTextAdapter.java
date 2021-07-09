package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 9/30/16.
 */
public class HistoryTextAdapter extends RecyclerView.Adapter<HistoryTextAdapter.ChoiceMethodViewHolder> {

    private List<String> mData ;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private CallbackString mListener;


    public HistoryTextAdapter(CallbackString listener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mListener = listener;
        this.mData = new ArrayList<>();
        List<String> list = CustomSQL.getListString(Constants.HISTORY_SEARCH);

        Collections.reverse(list);
        if (list.size() > 6){
            mData = list.subList(0,5);
        }else {
            mData = list;
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ChoiceMethodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_icon_string_item, parent, false);
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

