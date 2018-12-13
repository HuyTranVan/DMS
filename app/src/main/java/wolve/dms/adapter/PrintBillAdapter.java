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
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class PrintBillAdapter extends RecyclerView.Adapter<PrintBillAdapter.PrintBillViewHolder> {
    private List<JSONObject> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int printSize;

    public PrintBillAdapter(int printSize, List<JSONObject> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.printSize = printSize;

    }

    @Override
    public PrintBillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_printbill_item, parent, false);
        return new PrintBillViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PrintBillViewHolder holder, final int position) {
        try {
            String name = mData.get(position).isNull("name")? mData.get(position).getString("productName"): mData.get(position).getString("name");
            holder.tvName.setText(name);

            holder.tvQuantity.setText(mData.get(position).getString("quantity"));

            holder.tvPrice.setText(Util.FormatMoney(mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount")));

//            Double discount = mData.get(position).getDouble("discount");
//            holder.tvPrice.setText(String.format("%sx(%s%s)",
//                    mData.get(position).getString("quantity"),
//                    Util.FormatMoney(mData.get(position).getDouble("unitPrice")),
//                    discount ==0.0 ?"" : " - "+ Util.FormatMoney(discount)));
//

            Double total = (mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount")) * mData.get(position).getDouble("quantity");
            holder.tvTotal.setText(Util.FormatMoney(total));

            if (!mData.get(position).isNull("name")){
                holder.vLine.setVisibility(position == mData.size()-1 ? View.GONE : View.VISIBLE);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Double getTotalMoney(){
        Double total =0.0;
        for (int i=0; i<mData.size(); i++){
            try {
                total += (mData.get(i).getDouble("unitPrice") - mData.get(i).getDouble("discount")) * mData.get(i).getDouble("quantity");
            } catch (JSONException e) {
                total =0.0;
            }
        }
        return total;
    }

    public  Double getTotalMoney(List<JSONObject> list){
        Double total =0.0;
        for (int i=0; i<list.size(); i++){
            try {
                total += (list.get(i).getDouble("unitPrice") - list.get(i).getDouble("discount")) * list.get(i).getDouble("quantity");
            } catch (JSONException e) {
                total =0.0;
            }
        }
        return total;
    }

    public class PrintBillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity, tvPrice, tvTotal ;
        private View vLine;

        public PrintBillViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.printbill_item_name);
            tvQuantity = (TextView) itemView.findViewById(R.id.printbill_item_quantity);
            tvPrice = (TextView) itemView.findViewById(R.id.printbill_item_price);
            tvTotal = (TextView) itemView.findViewById(R.id.printbill_item_total);
            vLine= itemView.findViewById(R.id.item_seperateline);
        }

    }

}
