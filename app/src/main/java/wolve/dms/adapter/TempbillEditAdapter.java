package wolve.dms.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.libraries.DoubleTextWatcher;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class TempbillEditAdapter extends RecyclerView.Adapter<TempbillEditAdapter.TempbillEditViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private CallbackDouble mTotal;
    private CallbackInt billIdListener;

    public TempbillEditAdapter(List<BaseModel> list, CallbackDouble total) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();

        for (BaseModel model : list) {
            model.put("deleted", false);
        }
        this.mData = list;
        this.mTotal = total;

    }

    @Override
    public TempbillEditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_edittempbill_item, parent, false);
        return new TempbillEditViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TempbillEditViewHolder holder, int position) {
        holder.tvProductName.setText(mData.get(holder.getAdapterPosition()).getString("productName"));
        holder.tvQuantity.setText(mData.get(holder.getAdapterPosition()).getString("quantity"));
        Double netprice = mData.get(holder.getAdapterPosition()).getDouble("unitPrice")
                - mData.get(holder.getAdapterPosition()).getDouble("discount");
        holder.tvPrice.setText(Util.FormatMoney(netprice));
        if (mData.get(holder.getAdapterPosition()).getBoolean("deleted")){
            holder.lnParent.setBackground(mContext.getDrawable(R.color.black_text_color_hint));
            holder.tvDelete.setText(Util.getIcon(R.string.icon_return));
            holder.tvDelete.setTextColor(mContext.getResources().getColor(R.color.colorBlueTransparent));
            holder.lnParent.setOnClickListener(null);

        }else {
            holder.lnParent.setBackground(mContext.getDrawable(R.drawable.btn_transparent));
            holder.tvDelete.setText(Util.getIcon(R.string.icon_delete));
            holder.tvDelete.setTextColor(mContext.getResources().getColor(R.color.colorRedTransparent));
            holder.lnParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogEditBillDetail(mData.get(holder.getAdapterPosition()), new CallbackObject() {
                        @Override
                        public void onResponse(BaseModel object) {

                            updateItem(holder.getAdapterPosition());
                        }
                    });
                }
            });
        }



        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(mData.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });

    }

    private void deleteItem(BaseModel baseModel, int pos) {
        if (baseModel.getBoolean("deleted")){
            mData.get(pos).put("deleted", false);
            updateItem(pos);

        }else {
            mData.get(pos).put("deleted", true);
            updateItem(pos);

        }




    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void reloadData(List<BaseModel> list) {
        mData = list;
        notifyDataSetChanged();
    }

    public List<BaseModel> getmData(){
        return mData;
    }

    public class TempbillEditViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQuantity, tvProductName, tvPrice, tvDelete;
        private RelativeLayout lnParent;
        private LinearLayout lnBody;

        public TempbillEditViewHolder(View itemView) {
            super(itemView);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.edittempbill_item_parent);
            lnBody = (LinearLayout) itemView.findViewById(R.id.edittempbill_item_body);
            tvPrice = itemView.findViewById(R.id.edittempbill_item_price);
            tvQuantity = itemView.findViewById(R.id.edittempbill_item_quantity);
            tvProductName = itemView.findViewById(R.id.edittempbill_item_name);
            tvDelete = itemView.findViewById(R.id.edittempbill_item_delete);

        }

    }

    private void updateItem(int pos){
        notifyItemChanged(pos);
        mTotal.Result(sumTotal());

    }

    private double sumTotal(){
        double total = 0.0;
        for (BaseModel item: mData){
            if (!item.getBoolean("deleted")){
                total += item.getInt("quantity") * (item.getDouble("unitPrice") - item.getDouble("discount"));

            }

        }
        return total;
    }

    public boolean checkAllDeleted(){
        boolean check = true;
        for (BaseModel item: mData){
            if (!item.getBoolean("deleted")){
                check = false;
                break;
            }
        }
        return  check;

    }

    public Double getNetTotalMoney() {
        Double total = 0.0;
        for (int i = 0; i < mData.size(); i++) {
            if (!mData.get(i).getBoolean("deleted")){
                total += mData.get(i).getDouble("purchasePrice") * mData.get(i).getDouble("quantity");
            }

        }
        return total;
    }

    public static void showDialogEditBillDetail(final BaseModel detail, final CallbackObject returnDetail) {
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_edit_tempbill);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_edit_tempbill_title);
        EditText tvUnitPrice = dialogResult.findViewById(R.id.dialog_edit_tempbill_unitprice);
        final EditText edNetPrice = dialogResult.findViewById(R.id.dialog_edit_tempbill_netprice);
        final TextView tvTotal = dialogResult.findViewById(R.id.dialog_edit_tempbill_total);
        final EditText edDiscount = dialogResult.findViewById(R.id.dialog_edit_tempbill_discount);
        TextInputLayout tvQuantity = dialogResult.findViewById(R.id.dialog_edit_tempbill_quantity_title);
        final EditText edQuantity = dialogResult.findViewById(R.id.dialog_edit_tempbill_quantity);
        TextView tvClear = dialogResult.findViewById(R.id.dialog_edit_tempbill_netprice_clear);

        btnCancel.setText("HỦY");
        btnSubmit.setText("CẬP NHẬT");
        tvTitle.setText(detail.getString("productName"));
        tvUnitPrice.setText(Util.FormatMoney(detail.getDouble("unitPrice")));

        edDiscount.setFocusable(false);
//        edDiscount.setText(detail.getDouble("discount") == 0 ? "" : Util.FormatMoney((double) Math.round(detail.getDouble("discount"))));
        edDiscount.setText(Util.FormatMoney(detail.getDouble("discount")));

        edNetPrice.setText(Util.FormatMoney(detail.getDouble("unitPrice") - detail.getDouble("discount")));

        //edQuantity.setText(String.valueOf(Math.round(product.getDouble("quantity"))));
        Util.numberEvent(edQuantity, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                if (value != 0) {
                    tvTotal.setText(Util.FormatMoney(value * (detail.getDouble("unitPrice") - Util.valueMoney(edDiscount))));

                } else {
                    tvTotal.setText("0");
                }

//                tvQuantity.setHint(String.format("Số lượng %s",
//                        Util.quantityProductDisplay(product, value)));
            }
        });
        edQuantity.setText(String.valueOf(detail.getInt("quantity")));
        tvTotal.setText(Util.FormatMoney(detail.getDouble("quantity") * (detail.getDouble("unitPrice") - detail.getDouble("discount"))));

        //edDiscount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(tvUnitPrice.getText().toString().trim().length())});

        Util.showKeyboardEditTextDelay(edQuantity, 500);

        Util.textMoneyEvent(edNetPrice, null, new CallbackDouble() {
            @Override
            public void Result(Double d) {
                tvClear.setVisibility(Util.isEmpty(edNetPrice) || edNetPrice.getText().toString().equals("0") ? View.GONE : View.VISIBLE);
                edDiscount.setText(Util.FormatMoney(detail.getDouble("unitPrice") - Util.moneyValue(edNetPrice)));
                tvTotal.setText(Util.FormatMoney(d * Util.valueMoney(edQuantity)));
//                edNetPrice.setSelection(edNetPrice.getText().toString().length());

            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edNetPrice.setText("");
                tvTotal.setText("0");
                Util.showKeyboardEditTextDelay(edNetPrice);
            }
        });

//        edQuantity.addTextChangedListener(new DoubleTextWatcher(edQuantity, new CallbackString() {
//            @Override
//            public void Result(String text) {
//                if (!text.toString().equals("") && !text.equals("0")) {
//                    tvTotal.setText(Util.FormatMoney(Double.parseDouble(text) * (detail.getDouble("unitPrice") - Util.valueMoney(edDiscount))));
//
//                } else if (text.toString().equals("")) {
//                    tvTotal.setText("0");
//                }
//            }
//        }));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isEmptyValue(edQuantity) || Util.isEmptyValue(edNetPrice)) {
                    Util.showToast("Vui lòng nhập số lượng >0 ");

                }
//                else if (Util.isEmptyValue(edNetPrice)) {
//                    Util.showToast("Vui lòng nhập giá tiền  >0 ");
//
//                }
//                else if (Util.moneyValue(edNetPrice) > detail.getDouble("unitPrice")) {
//                    Util.showToast("Vui lòng nhập giá tiền nhỏ hơn giá đơn vị ");
//
//                }
                else {
                    detail.put("quantity", String.valueOf(edQuantity.getText().toString()));
                    detail.put("discount", edDiscount.getText().toString().equals("") ? 0 : Util.valueMoney(edDiscount));
                    detail.put("totalMoney", tvTotal.getText().toString().replace(".", ""));

                    returnDetail.onResponse(detail);
                    dialogResult.dismiss();


                }

            }
        });

    }

}
