package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.PrintBillViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Boolean showPaid;

    public DebtAdapter(List<BaseModel> list, boolean isNewFirst, Boolean showpaid) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.showPaid = showpaid;
        inputPaid(0.0, isNewFirst);
    }

    @Override
    public PrintBillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_debt_item, parent, false);
        return new PrintBillViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PrintBillViewHolder holder, final int position) {
//        try {
        if (mData.get(position).getInt("id") == 0){
            holder.tvDate.setText("HĐ hiện tại");
//            holder.tvDate.setAllCaps(true);
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            holder.tvPaid.setTextColor(mContext.getResources().getColor(R.color.colorBlueTransparent));
            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.colorBlue));



        }else {
//            holder.tvDate.setAllCaps(false);
            holder.tvDate.setText(String.format("HĐ %s", Util.DateMonthYearString(mData.get(position).getLong("createAt"))));
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.black_text_color));
            holder.tvPaid.setTextColor(mContext.getResources().getColor(R.color.black_text_color_hint));
            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.black_text_color));
        }

        Double total =  !mData.get(position).isNull("tempPaid")? mData.get(position).getDouble("debt")-mData.get(position).getDouble("tempPaid"):
                mData.get(position).getDouble("debt");
//        holder.tvDate.setTextColor(total ==0.0? mContext.getResources().getColor(R.color.black_text_color_hint) : mContext.getResources().getColor(R.color.black_text_color));
        holder.tvTotal.setText(String.format("%s",Util.FormatMoney(total)));
        holder.tvPaid.setVisibility(showPaid? View.VISIBLE : View.GONE);
        if (!mData.get(position).isNull("tempPaid")){
            holder.tvPaid.setText(Util.FormatMoney(mData.get(position).getDouble("tempPaid")));
        }

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Double getTotalMoney(){
        Double total =0.0;
        for (int i=0; i<mData.size(); i++){
//            try {
                total += (mData.get(i).getDouble("debt") );
//            } catch (JSONException e) {
//                total =0.0;
//            }
        }
        return total;
    }

    public class PrintBillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvTotal, tvPaid;

        public PrintBillViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.debt_item_date);
            tvTotal = (TextView) itemView.findViewById(R.id.debt_item_total);
            tvPaid = (TextView) itemView.findViewById(R.id.debt_item_paid);

        }

    }

    public void inputPaid(Double paid, boolean payNew){
        Double money = paid;
//        try {
            if (mData.size() ==1 && mData.get(0).getDouble("debt") <0){
                if (money.equals(0.0)){
                    mData.get(0).put("tempPaid", 0);
                    notifyItemChanged(0);
                }else {
                    mData.get(0).put("tempPaid", money );
                    notifyItemChanged(0);
                    money = 0.0;
                }

            }else if (payNew){
                for (int i=0; i<mData.size(); i++){
                    if (money ==null){
                        mData.get(i).put("tempPaid", 0);
                        notifyItemChanged(i);

                    }else if (money < mData.get(i).getDouble("debt")){
                        mData.get(i).put("tempPaid", money);
                        notifyItemChanged(i);
                        money = 0.0;
//                    break;

                    }else {
                        mData.get(i).put("tempPaid", mData.get(i).getDouble("debt"));
                        notifyItemChanged(i);
                        money = money - mData.get(i).getDouble("debt");
                    }

                }
            }else if (!payNew){
                for (int i=mData.size()-1; i>= 0; i--){
                    if (money ==null){
                        mData.get(i).put("tempPaid", 0);
                        notifyItemChanged(i);

                    }else if (money < mData.get(i).getDouble("debt")){
                        mData.get(i).put("tempPaid", money);
                        notifyItemChanged(i);
                        money = 0.0;
//                    break;

                    }else {
                        mData.get(i).put("tempPaid", mData.get(i).getDouble("debt"));
                        notifyItemChanged(i);
                        money = money - mData.get(i).getDouble("debt");
                    }

                }
            }



//        } catch (JSONException e) {
//            e.printStackTrace();
            notifyDataSetChanged();
//        }
    }

    public List<BaseModel> getListBillPayment(){
        List<BaseModel> listResult = new ArrayList<>();
//        try {
            for (int i=0; i<mData.size(); i++){
                BaseModel object = new BaseModel();
                if (!mData.get(i).isNull("tempPaid") && mData.get(i).getDouble("tempPaid") !=0){
                    object.put("billId", mData.get(i).getInt("id"));
                    object.put("paid", mData.get(i).getDouble("tempPaid"));
                    listResult.add(object);
                }

            }
//        } catch (JSONException e) {
////            e.printStackTrace();
//            return listResult;
//        }

        return listResult;
    }

}
