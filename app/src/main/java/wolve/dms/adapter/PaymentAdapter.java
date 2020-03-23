package wolve.dms.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
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
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PrintBillViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public PaymentAdapter(List<BaseModel> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;

        DataUtil.sortbyStringKey("createAt", mData, true);

    }

    @Override
    public PrintBillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_payment_item, parent, false);
        return new PrintBillViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PrintBillViewHolder holder, final int position) {
        String date = Util.DateHourString(mData.get(position).getLong("createAt"));
        String note = "";
        if (mData.get(position).getInt("payByReturn") == 1){
            note = "Trừ tiền thu hàng".toUpperCase();
            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.black_text_color_hint));

        }else if (mData.get(position).getDouble("paid") <0.0){
            note ="trả khách tiền mặt".toUpperCase();
            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.colorRedTransparent));

        }else {
            note ="thu tiền mặt".toUpperCase();
            holder.tvTotal.setTextColor(mContext.getResources().getColor(R.color.colorBlue));

        }

        holder.tvDate.setText(String.format("%s %s:",date, note));

        holder.tvTotal.setText(String.format("%s %s đ",mData.get(position).getDouble("paid") <0.0? "-" : "+",
                mData.get(position).getDouble("paid") <0.0? Util.FormatMoney(mData.get(position).getDouble("paid") *-1) :Util.FormatMoney(mData.get(position).getDouble("paid"))));

//        String user = mData.get(position).getBaseModel("user").getString("displayName");
//        holder.tvName.setText(user);

        String user = Util.getIconString(R.string.icon_username,mData.get(position).getBaseModel("user").getString("displayName"));
        String collect = mData.get(position).getInt("user_id") != mData.get(position).getInt("user_collect")?
                String.format(" (%s thu hộ)",mData.get(position).getBaseModel("collect_by").getString("displayName") ) : "";
        holder.tvName.setText(user + collect);
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
        private TextView tvDate, tvTotal  ;
        private View vLine;
        private CTextIcon tvName;

        public PrintBillViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.payment_item_date);
            tvTotal = (TextView) itemView.findViewById(R.id.payment_item_total);
            tvName = (CTextIcon) itemView.findViewById(R.id.payment_item_name);
            vLine = itemView.findViewById(R.id.item_seperateline);
        }

    }

}
