package wolve.dms.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createPostParam;

/**
 * Created by tranhuy on 5/24/17.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.StatisticalBillsViewHolder> implements Filterable {
    private List<BaseModel> baseData = new ArrayList<>();
    private List<BaseModel> mData;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public AccountAdapter(List<BaseModel> data, CallbackString listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;

        this.baseData = data;

        this.mData = baseData;
        DataUtil.sortbyStringKey("createAt", mData, true);

    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_accout_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        BaseModel type = mData.get(position).getBaseModel("cashflowtype");
        holder.tvName.setText(type.getString("name"));

        if (mData.get(position).getDouble("total") >0){
            holder.tvNumber.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            holder.tvTotal.setText("+ " + Util.FormatMoney(mData.get(position).getDouble("total")));

        }else {
            holder.tvNumber.setTextColor(mContext.getResources().getColor(R.color.black_text_color));
            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.black_text_color));
            holder.tvTotal.setText("- " +  Util.FormatMoney(mData.get(position).getDouble("total") *-1));

        }

        if (mData.get(position).hasKey("customer")){
            holder.tvNote.setVisibility(View.VISIBLE);
            BaseModel customer = mData.get(position).getBaseModel("customer");
            holder.tvNote.setText(String.format("%s %s", customer.getString("shopTypeTitle") , customer.getString("signBoard") ));
        }else if (!mData.get(position).getString("note").equals("")){
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(mData.get(position).getString("note"));
        }else {
            holder.tvNote.setVisibility(View.GONE);
        }

        switch (type.getInt("kind")){
            case 0:
                holder.tvTime.setText(Util.DateHourString(mData.get(position).getLong("updateAt")));
                holder.tvNumber.setTextColor(mContext.getResources().getColor(R.color.orange_dark));
                holder.tvNumber.setText(Util.getIcon(R.string.icon_money_check));
                break;

            case 1:
                holder.tvTime.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
                holder.tvNumber.setText(Util.getIcon(R.string.icon_bill));
                break;

            case 2:
                holder.tvTime.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
                holder.tvNumber.setTextColor(mContext.getResources().getColor(R.color.orange_dark));
                holder.tvNumber.setText(Util.getIcon(R.string.icon_hand_on_money));
                break;

            case 3:
                holder.tvTime.setText(Util.DateHourString(mData.get(position).getLong("updateAt")));
                holder.tvNumber.setText(Util.getIcon(R.string.icon_tag));
                break;

            case 4:
                holder.tvTime.setText(Util.DateHourString(mData.get(position).getLong("updateAt")));
                holder.tvNumber.setText(Util.getIcon(R.string.icon_tag));
                break;

        }

        holder.tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.getInt("kind") != 1 && type.getInt("kind") != 2){
                    CustomCenterDialog.alertWithButtonCanceled("", "Cập nhật lại ngày", "tiếp tục", true, new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                modifyTime(mData.get(position), position);

                            }
                        }
                    });

                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvName,  tvTime, tvTotal, tvNote;
        private View vLine;
        private LinearLayout lnParent;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.account_item_number);
            tvName = itemView.findViewById(R.id.account_item_name);
            tvTime = itemView.findViewById(R.id.account_item_time);
            tvTotal = itemView.findViewById(R.id.account_item_paid);
            tvNote = itemView.findViewById(R.id.account_item_note);
            vLine = itemView.findViewById(R.id.account_item_line);

        }
    }

    public double sumPayments() {
        double totalPayment = 0.0;
        for (BaseModel row : mData) {
            totalPayment += row.getDouble("paid");
        }
        return totalPayment;
    }



    public double sumProfit() {
        double totalProfit = 0.0;
        for (BaseModel row : mData) {
            totalProfit += row.getDouble("bill_profit");
        }
        return totalProfit;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                mData = new ArrayList<>();
                if (charString.equals(Constants.ALL_TOTAL)) {
                    mData = baseData;

                } else if (charString.equals(Constants.PAY_DISTRIBUTOR)){
                    for (BaseModel row : baseData) {
                        if (row.getBaseModel("cashflowtype").getInt("kind") == 0) {
                            mData.add(row);
                        }

                    }

                }else if (charString.equals(Constants.IN_COME)){
                    for (BaseModel row : baseData) {
                        if (row.getBaseModel("cashflowtype").getInt("kind") == 1 ||
                                row.getBaseModel("cashflowtype").getInt("kind") == 2) {
                            mData.add(row);
                        }

                    }

                }
//                else if (charString.equals(Constants.OUT_COME)){
//                    for (BaseModel row : baseData) {
//                        if (row.getBaseModel("cashflowtype").getInt("kind") == 2) {
//                            mData.add(row);
//                        }
//
//                    }
//
//                }
                else {
                    for (BaseModel row : baseData) {
                        if (row.getBaseModel("cashflowtype").getInt("kind") > 2 ) {
                            mData.add(row);
                        }

                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (ArrayList<BaseModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public double getIncome(){
        double total = 0;
        for (BaseModel item: mData){
            if (item.getDouble("total") > 0 )
            total += item.getDouble("total");
        }

        return total;
    }

    public double getOutcome(){
        double total = 0;
        for (BaseModel item: mData){
            if (item.getDouble("total") < 0 )
                total += item.getDouble("total");
        }

        return total;
    }

    public void addItem(BaseModel item){
        mData.add(0, item);
        notifyItemInserted(0);
    }

    private void modifyTime(BaseModel item, int pos){

        final Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.setTimeInMillis(item.getLong("createAt"));

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                BaseModel param = createPostParam(ApiUtil.CASHFLOW_NEW(),
                        String.format("id=%d&updateAt=%d", item.getInt("id"),newDate.getTimeInMillis() ),
                        false,
                        false);
                new GetPostMethod(param, new NewCallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result, List<BaseModel> list) {
                        mData.remove(pos);
                        mData.add(pos, result);
                        notifyItemChanged(pos);

                    }

                    @Override
                    public void onError(String error) {

                    }
                }, 1).execute();

                }
            }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }





}
