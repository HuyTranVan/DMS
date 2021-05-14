package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Customer;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Statistical_BillsAdapter extends RecyclerView.Adapter<Statistical_BillsAdapter.StatisticalBillsViewHolder> implements Filterable {
    private List<BaseModel> baseData;
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackString mListener;

    public Statistical_BillsAdapter(String username, List<BaseModel> data, CallbackString callbackString) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
//        this.mData = data;
        //this.baseData = data;
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = callbackString;

        if (username.equals(Constants.ALL_FILTER)) {
            this.baseData = data;

        } else {
            baseData = new ArrayList<>();
            for (BaseModel row : data) {
                if (row.getBaseModel("user").getString("displayName").equals(username)){
                    baseData.add(row);
                }
            }
        }

        this.mData = baseData;

        DataUtil.sortbyStringKey("createAt", mData, true);
    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_statistical_bills_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        try {
//            holder.lnParent.setBackground(( position % 2 ) == 0 ?  mContext.getResources().getDrawable(R.drawable.bg_corner5_lightgrey):
//                    mContext.getResources().getDrawable(R.drawable.bg_corner5_white_border_grey));

            final Customer customer = new Customer(mData.get(position).getJsonObject("customer"));

//            holder.tvDate.setText(Util.DateString(mData.get(position).getLong("createAt")));
//            holder.tvHour.setText(Util.HourString(mData.get(position).getLong("createAt")));
            holder.tvTotal.setText("Tổng: " + Util.FormatMoney(mData.get(position).getDouble("total")));
            holder.tvPay.setText("Trả: " + Util.FormatMoney(mData.get(position).getDouble("paid")));
            holder.tvDebt.setText("Nợ: " + Util.FormatMoney(mData.get(position).getDouble("debt")));
            holder.tvNumber.setText(String.valueOf(mData.size() - position));


            if (!customer.getString("note").isEmpty() && Util.isJSONValid(customer.getString("note"))) {
                holder.tvNumber.setTextColor(mContext.getResources().getColor(R.color.color_red));

            } else {
                holder.tvNumber.setTextColor(mContext.getResources().getColor(R.color.white_text_color));

            }


            holder.tvsignBoard.setText(Constants.shopName[customer.getInt("shopType")] + " " + customer.getString("signBoard"));
            holder.tvDistrict.setText(customer.getString("street") + " - " + customer.getString("district"));
            String user = String.format("Nhân viên: %s", mData.get(position).getJsonObject("user").getString("displayName"));
            String hour = Util.DateHourString(mData.get(position).getLong("createAt"));
            holder.tvUser.setText(String.format("%s         %s", user, hour));

            JSONArray arrayBillDetail = new JSONArray(mData.get(position).getString("billDetails"));
            List<BaseModel> listBillDetail = new ArrayList<>();
            for (int i = 0; i < arrayBillDetail.length(); i++) {
                BaseModel billDetail = new BaseModel(arrayBillDetail.getJSONObject(i));
                listBillDetail.add(billDetail);
            }
            Customer_BillsDetailAdapter adapter = new Customer_BillsDetailAdapter(listBillDetail);
            Util.createLinearRV(holder.rvBillDetail, adapter);
            if (!mData.get(position).getString("note").equals("")) {
                String note = Security.decrypt(mData.get(position).getString("note"));
                if (Util.isJSONObject(note)) {
                    BaseModel object = new BaseModel(note);
                    if (object.hasKey(Constants.TEMPBILL) && object.getBoolean(Constants.TEMPBILL)) {
                        holder.tvIsTempBill.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvIsTempBill.setVisibility(View.GONE);
                    }


                } else {
                    holder.tvIsTempBill.setVisibility(View.GONE);
                }

            } else {
                holder.tvIsTempBill.setVisibility(View.GONE);
            }

            if (mData.size() == 1) {
                holder.vLineUpper.setVisibility(View.GONE);
                holder.vLineUnder.setVisibility(View.GONE);
            } else if (position == 0) {
                holder.vLineUpper.setVisibility(View.GONE);
            } else if (position == mData.size() - 1) {
                holder.vLineUnder.setVisibility(View.GONE);
            }

            holder.tvNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.Result(customer.getString("id"));
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvHour, tvPay, tvDebt, tvTotal, tvNumber, tvsignBoard, tvDistrict, tvUser, tvIsTempBill;
        private RecyclerView rvBillDetail, rvPayment;
        private View vLineUpper, vLineUnder;
        private LinearLayout lnParent;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);

//            tvDate = (TextView) itemView.findViewById(R.id.statistical_bills_item_date);
            tvIsTempBill = (TextView) itemView.findViewById(R.id.statistical_bills_item_istemp);
            tvPay = (TextView) itemView.findViewById(R.id.statistical_bills_item_pay);
            tvDebt = (TextView) itemView.findViewById(R.id.statistical_bills_item_debt);
            tvTotal = (TextView) itemView.findViewById(R.id.statistical_bills_item_total);
            rvBillDetail = (RecyclerView) itemView.findViewById(R.id.statistical_bills_item_rvproduct);
            vLineUnder = (View) itemView.findViewById(R.id.statistical_bills_item_under);
            vLineUpper = (View) itemView.findViewById(R.id.statistical_bills_item_upper);
            lnParent = (LinearLayout) itemView.findViewById(R.id.statistical_bills_item_content_parent);
            tvNumber = (TextView) itemView.findViewById(R.id.statistical_bills_item_number);
            tvsignBoard = (TextView) itemView.findViewById(R.id.statistical_bills_item_signboard);
            tvDistrict = (TextView) itemView.findViewById(R.id.statistical_bills_item_district);
            tvUser = itemView.findViewById(R.id.statistical_bills_item_user);
            rvPayment = itemView.findViewById(R.id.statistical_bills_item_payment);
        }

    }

    public List<BaseModel> getAllBill() {
        return mData;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.equals(Constants.ALL_TOTAL)) {
                    mData = baseData;

                } else {
                    List<BaseModel> listTemp = new ArrayList<>();
                    for (BaseModel row : baseData) {
                        if (row.getDouble("debt") > 0) {
                            listTemp.add(row);
                        }

                    }

                    mData = listTemp;
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

    public BaseModel sumBill() {
        BaseModel bill = new BaseModel();
        double total = 0.0;
        double debt = 0.0;
        for (int i = 0; i < mData.size(); i++) {
            total += mData.get(i).getDouble("total");
            debt += mData.get(i).getDouble("debt");

        }
        bill.put("total", total);
        bill.put("debt", debt);
        return bill;
    }

    public double sumBillTotal() {
        double total = 0.0;
        for (int i = 0; i < mData.size(); i++) {
            total += mData.get(i).getDouble("total");

        }

        return total;
    }

    public double sumNetTotal(){
        double total = 0.0;
        for (int i = 0; i < mData.size(); i++) {
            List<BaseModel> details = mData.get(i).getList("billDetails");

            for (int ii =0; ii<details.size(); ii++){
                total += details.get(ii).getDouble("purchasePrice")*details.get(ii).getInt("quantity");

            }
        }

        return total;
    }

}
