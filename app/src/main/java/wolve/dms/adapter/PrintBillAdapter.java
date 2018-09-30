package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.CustomCenterDialog;
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
        View itemView = mLayoutInflater.inflate(printSize == 57? R.layout.adapter_printbill_57_item : R.layout.adapter_printbill_80_item, parent, false);
        return new PrintBillViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PrintBillViewHolder holder, final int position) {
        try {
            holder.tvName.setText(String.format("%d. %s", position+1 , mData.get(position).getString("name")));
            holder.tvQuantity.setText(String.format("x%s", mData.get(position).getString("quantity")));
            Double discount = mData.get(position).getDouble("discount");
            holder.tvPrice.setText(discount ==0.0 ? Util.FormatMoney(mData.get(position).getDouble("unitPrice")) : String.format("(%s - %s)", Util.FormatMoney(mData.get(position).getDouble("unitPrice")) , Util.FormatMoney(discount)));
            Double total = (mData.get(position).getDouble("unitPrice") - mData.get(position).getDouble("discount")) * mData.get(position).getDouble("quantity");
            holder.tvTotal.setText(Util.FormatMoney(total));


            holder.vLine.setVisibility(position == mData.size()-1 ? View.GONE : View.VISIBLE);


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
