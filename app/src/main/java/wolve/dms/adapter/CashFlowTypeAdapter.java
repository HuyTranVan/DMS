package wolve.dms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;

/**
 * Created by tranhuy on 5/24/17.
 */

public class CashFlowTypeAdapter extends RecyclerView.Adapter<CashFlowTypeAdapter.StatusAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackClickAdapter mListener;
    private CallbackDeleteAdapter mDeleteListener;

    public CashFlowTypeAdapter(List<BaseModel> list, CallbackClickAdapter callbackClickAdapter, CallbackDeleteAdapter callbackDeleteAdapter) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = callbackClickAdapter;
        this.mDeleteListener = callbackDeleteAdapter;

        DataUtil.sortbyStringKey("kind", mData, false);
    }

    @Override
    public StatusAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cashflow_type_item, parent, false);
        return new StatusAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatusAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.iconDefault.setVisibility(mData.get(position).getInt("kind") == 4 ? View.INVISIBLE : View.VISIBLE);
        holder.iconDefault.setText(mData.get(position).getInt("kind") == 4 ? "" : Util.getIcon(R.string.icon_protect));
        if (mData.get(position).getInt("isIncome") == 0){
            holder.tvType.setText(Util.getIcon(R.string.icon_circle_up));
            holder.tvType.setTextColor(mContext.getResources().getColor(R.color.orange));

        }else {
            holder.tvType.setText(Util.getIcon(R.string.icon_circle_down));
            holder.tvType.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.get(position).getInt("kind") != 4 && User.getId() !=2){
                    Util.showToast("Không thể sửa loại thu chi này!");
                }else {
                    mListener.onRespone(mData.get(position).BaseModelstoString(), position);

                }

            }
        });

        holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteType(position);


                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class StatusAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName, tvType, iconDefault;
        private LinearLayout lnParent;

        public StatusAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.cftype_item_name);
            tvType = (TextView) itemView.findViewById(R.id.cftype_item_type_income);
            iconDefault = (TextView) itemView.findViewById(R.id.cftype_item_icon_default);
            lnParent = (LinearLayout) itemView.findViewById(R.id.cftype_item_parent);
        }

    }

    public void updateType(BaseModel item){
        boolean checkExist = false;
        for (int i=0; i< mData.size(); i++){
            if (mData.get(i).getInt("id") == item.getInt("id")){
                mData.remove(i);
                mData.add(i, item);
                checkExist = true;
                notifyItemChanged(i);
                break;
            }
        }

        if (!checkExist){
            mData.add(item);
            notifyItemInserted(mData.size() -1);
        }
    }

    private void deleteType(int position){
        if (mData.get(position).getInt("kind") > 2) {
            CustomCenterDialog.alertWithCancelButton(null,
                    "Bạn muốn xóa " + mData.get(position).getString("name"),
                    "ĐỒNG Ý",
                    "HỦY",
                    new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    if (result){
                        BaseModel param = createGetParam(ApiUtil.CASHFLOWTYPE_DELETE() + mData.get(position).getString("id"), false);
                        new GetPostMethod(param, new NewCallbackCustom() {
                            @Override
                            public void onResponse(BaseModel result, List<BaseModel> list) {
                                mDeleteListener.onDelete(mData.get(position).BaseModelstoString(), position);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, 1).execute();
                    }


                }
            });
        } else {
            Util.showSnackbar("Không thể xóa loại thu chi này", null, null);
        }
    }


}
