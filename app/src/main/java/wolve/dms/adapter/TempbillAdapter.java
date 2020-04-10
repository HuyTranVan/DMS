package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class TempbillAdapter extends RecyclerView.Adapter<TempbillAdapter.TempbillAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private CallbackBoolean boListener;

    public TempbillAdapter(List<BaseModel> list, CallbackObject listener, CallbackBoolean boollistener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = listener;
        this.boListener = boollistener;

        DataUtil.sortbyStringKey("create", mData, true);

    }

    @Override
    public TempbillAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_tempbill_item, parent, false);
        return new TempbillAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TempbillAdapterViewHolder holder, final int position) {
        if (mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")){
            holder.tvNumber.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_oval_green));
            holder.tvNumber.setText(mContext.getResources().getString(R.string.icon_check));

        }else {
            holder.tvNumber.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_oval_denim));
            holder.tvNumber.setText(String.valueOf(mData.size()-position));
        }

        BaseModel customer = mData.get(position).getBaseModel("customer");
        holder.tvShopName.setText(Constants.getShopName(customer.getString("shopType")) + " " + customer.getString("signBoard"));
        holder.tvAddress.setText(customer.getString("street") + " - " + customer.getString("district"));

        holder.tvTotal.setText(Util.FormatMoney(mData.get(position).getBaseModel("bill").getDouble("total")));
        holder.tvEmployee.setText(String.format("Nhân viên: %s", mData.get(position).getBaseModel("user").getString("displayName")));

        List<BaseModel> details = mergeDetail(DataUtil.array2ListObject(mData.get(position).getBaseModel("bill").getString("billDetails")));
        String detail = "";
        for (int i=0; i<details.size(); i++){
            detail += String.format("%d  x  %s", details.get(i).getInt("quantity"), details.get(i).getString("productName") )
                        + (i == (details.size()-1)? "" : "\n");

        }
        holder.tvDetail.setText(detail);

        holder.tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.get(position).hasKey("checked") && mData.get(position).getBoolean("checked")){
                    mData.get(position).put("checked", false);

                }else {
                    mData.get(position).put("checked", true);

                }
                boListener.onRespone(true);
                notifyItemChanged(position);

            }
        });
        if (mData.size()==1){
            holder.vLineUpper.setVisibility(View.GONE);
            holder.vLineUnder.setVisibility(View.GONE);
        }else if(position ==0){
            holder.vLineUpper.setVisibility(View.GONE);
            holder.vLineUnder.setVisibility(View.VISIBLE);
        }else if (position==mData.size()-1){
            holder.vLineUpper.setVisibility(View.VISIBLE);
            holder.vLineUnder.setVisibility(View.GONE);
        }

        holder.lnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomCenterDialog.alertWithCancelButton("Chỉ đường", "Mở ứng dụng bản đồ để tiếp tục chỉ đường", "Tiếp tục","Quay lại", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean re) {
                        if (re){
                            Transaction.openGoogleMapRoute( mData.get(position).getBaseModel("customer").getDouble("lat"),
                                    mData.get(position).getBaseModel("customer").getDouble("lng"));
                        }

                    }
                });


            }
        });

        holder.lnDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(mData.get(position));
            }
        });




    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void reloadData(List<BaseModel> list){
        mData = list;
        notifyDataSetChanged();
    }

    public List<BaseModel> getCheckedData(){
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model: mData){
            if (model.hasKey("checked") && model.getBoolean("checked")){
                results.add(model);
            }
        }
        return results;
    }


    public void checkAllData(boolean check){
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model: mData){
            model.put("checked", check);

        }
        notifyDataSetChanged();
    }


    public class TempbillAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEmployee, tvShopName, tvTotal, tvAddress, tvDetail, tvNumber;
        private LinearLayout lnParent, lnDirection, lnDeliver;
        private View vLineUpper, vLineUnder;

        public TempbillAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = (LinearLayout) itemView.findViewById(R.id.tempbill_item_parent);
            lnDirection = (LinearLayout) itemView.findViewById(R.id.tempbill_item_direction);
            lnDeliver = (LinearLayout) itemView.findViewById(R.id.tempbill_item_deliver);
            tvTotal = (TextView) itemView.findViewById(R.id.tempbill_item_total);
            tvNumber = itemView.findViewById(R.id.tempbill_item_number);
            tvEmployee = (TextView) itemView.findViewById(R.id.tempbill_item_employee);
            tvShopName = (TextView) itemView.findViewById(R.id.tempbill_item_shopname);
            tvAddress = (TextView) itemView.findViewById(R.id.tempbill_item_address);
            tvDetail = itemView.findViewById(R.id.tempbill_item_detail);
            vLineUnder = (View) itemView.findViewById(R.id.tempbill_item_under);
            vLineUpper = (View) itemView.findViewById(R.id.tempbill_item_upper);

        }

    }

    private List<BaseModel> mergeDetail(List<BaseModel> list){
        List<BaseModel> listResult = new ArrayList<>();

        for (int i=0; i<list.size(); i++){
            if (!DataUtil.checkDuplicate(listResult, "productId", list.get(i))){
                BaseModel newDetail = new BaseModel();
                newDetail.put("productId", list.get(i).getInt("productId"));
                newDetail.put("productName", list.get(i).getString("productName"));

                int quantity = list.get(i).getInt("quantity");
                for (int ii = i+1; ii< list.size(); ii++){
                    if (list.get(ii).getInt("productId") == list.get(i).getInt("productId")){
                        quantity += list.get(ii).getInt("quantity");
                    }

                }

                newDetail.put("quantity", quantity);

                listResult.add(newDetail);
            }

        }

        return listResult;
    }

}
