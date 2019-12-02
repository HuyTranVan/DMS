package wolve.dms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Status;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;
    private CallbackDeleteAdapter mDeleteListener;

    public StatusAdapter(List<BaseModel> list, CallbackClickAdapter callbackClickAdapter, CallbackDeleteAdapter callbackDeleteAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        mListener = callbackClickAdapter;
        this.mDeleteListener = callbackDeleteAdapter;
    }

    @Override
    public StatusAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_status_item, parent, false);
        return new StatusAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<Status> list){
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final StatusAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvColor.setBackgroundColor(Color.parseColor(mData.get(position).getString("color")));

        holder.tvIconDefault.setVisibility(mData.get(position).getBoolean("defaultStatus") ? View.VISIBLE : View.INVISIBLE);


        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRespone(mData.get(position).BaseModelstoString() , position);

            }
        });

        holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mData.get(position).getBoolean("defaultStatus")){
                    CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa trạng thái " + mData.get(position).getString("name"), "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            String param = String.valueOf(mData.get(position).getInt("id"));
                            StatusConnect.DeleteStatus(param, new CallbackCustom() {
                                @Override
                                public void onResponse(BaseModel result) {
                                    Util.getInstance().stopLoading(true);
                                    mDeleteListener.onDelete(mData.get(position).BaseModelstoString(), position);

                                }

                                @Override
                                public void onError(String error) {
                                    Util.getInstance().stopLoading(true);
                                    Constants.throwError(error);

                                }
                            }, true);
                        }
                    });
                }else {
                    Util.showSnackbar("Không thể xóa trạng thái này", null , null);
                }



                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class StatusAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvColor;
        private CTextIcon tvIconDefault;
        private LinearLayout lnParent;

        public StatusAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.status_item_name);
            tvColor = (TextView) itemView.findViewById(R.id.status_item_color);
            tvIconDefault = (CTextIcon) itemView.findViewById(R.id.status_item_icon_default);
            lnParent = (LinearLayout) itemView.findViewById(R.id.status_item_parent);
        }

    }

}
