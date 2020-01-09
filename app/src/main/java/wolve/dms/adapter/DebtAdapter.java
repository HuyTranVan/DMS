package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
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
        if (mData.get(position).getInt("id") == 0){
            holder.tvDate.setText("HĐ hiện tại");
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            holder.tvPaid.setTextColor(mContext.getResources().getColor(R.color.colorBlueTransparent));
            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.colorBlue));

        }else {
            holder.tvDate.setText(String.format("HÓA ĐƠN %s", Util.DateHourString(mData.get(position).getLong("createAt"))));
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.black_text_color));

        }

        holder.tvTotal.setText(String.format("%s đ",Util.FormatMoney(mData.get(position).getDouble("debt"))));
        holder.tvPaid.setVisibility(showPaid? View.VISIBLE : View.GONE);

        if (!mData.get(position).isNull("tempPaid")){
            holder.tvPaid.setText(String.format("%s đ",Util.FormatMoney(mData.get(position).getDouble("tempPaid"))));
            holder.tvRemain.setText(String.format("%s đ",Util.FormatMoney(mData.get(position).getDouble("debt") - mData.get(position).getDouble("tempPaid"))));
        }

        holder.line.setVisibility(position  == mData.size()-1 ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Double getTotalMoney(){
        Double total =0.0;
        for (int i=0; i<mData.size(); i++){
            total += (mData.get(i).getDouble("debt") );

        }
        return total;
    }

    public class PrintBillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvTotal, tvPaid, tvRemain;
        private View line;

        public PrintBillViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.debt_item_date);
            tvTotal = (TextView) itemView.findViewById(R.id.debt_item_total);
            tvPaid = (TextView) itemView.findViewById(R.id.debt_item_paid);
            tvRemain = (TextView) itemView.findViewById(R.id.debt_item_remain);
            line = itemView.findViewById(R.id.line);

        }

    }

    public void inputPaid(Double paid, boolean payNew){
        Double money = paid;
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

            notifyDataSetChanged();
    }

    public List<BaseModel> getListBillPayment(){
        List<BaseModel> listResult = new ArrayList<>();
        for (int i=0; i<mData.size(); i++){
            if (!mData.get(i).isNull("tempPaid") && mData.get(i).getDouble("tempPaid") !=0){
                BaseModel object = new BaseModel();
                object.put("billId", mData.get(i).getInt("id"));
                object.put("paid", mData.get(i).getDouble("tempPaid"));
                object.put("billTotal", mData.get(i).getDouble("total"));
                object.put("user_id", mData.get(i).getInt("user_id"));
                listResult.add(object);

            }else if (mData.get(i).getInt("id") == 0){
                if (mData.get(i).getDoubleValue("debt") == 0){
                    BaseModel object = new BaseModel();
                    object.put("billId", mData.get(i).getInt("id"));
                    object.put("paid", 0.0);
                    object.put("billTotal", 0.0);
                    object.put("user_id", mData.get(i).getInt("user_id"));
                    listResult.add(object);


                }

            }

        }

        return listResult;
    }

}
