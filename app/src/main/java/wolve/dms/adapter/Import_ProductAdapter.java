package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Import_ProductAdapter extends RecyclerView.Adapter<Import_ProductAdapter.Import_ProductViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBoolean mListener;

    public Import_ProductAdapter(List<BaseModel> data, CallbackBoolean listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = data;
        this.mListener = listener;

        DataUtil.sortbyStringKey("createAt", mData, true);

    }


    @Override
    public Import_ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_import_product_item, parent, false);
        return new Import_ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Import_ProductViewHolder holder, final int position) {
        holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
        holder.tvUser.setText(Util.getIconString(R.string.icon_username, "  ", mData.get(position).getBaseModel("user").getString("displayName")));

        String warehouse = String.format("%s  %s%s%s  %s",
                mData.get(position).getBaseModel("fr_warehouse").getString("name"),
                Util.getIcon(R.string.icon_next),
                Util.getIcon(R.string.icon_next),
                Util.getIcon(R.string.icon_next),
                mData.get(position).getBaseModel("warehouse").getString("name"));
        holder.tvWarehouse.setText(warehouse);

        List<BaseModel> importdetails = DataUtil.array2ListObject(mData.get(position).getString("details"));
        Import_ProductDetailAdapter adapter = new Import_ProductDetailAdapter(importdetails);
        Util.createLinearRV(holder.rvProduct, adapter);

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImport(position);

            }
        });
        holder.tvAccept.setVisibility(mData.get(position).getInt("acceptBy") != 0? View.GONE: View.VISIBLE);
        holder.tvDelete.setVisibility(mData.get(position).getInt("acceptBy") != 0? View.GONE: View.VISIBLE);
        holder.tvCopy.setVisibility(mData.get(position).getInt("acceptBy") != 0 && Util.isAdmin()? View.VISIBLE: View.GONE);

        if (Util.isAdmin()
                || User.getId() == mData.get(position).getBaseModel("fr_warehouse").getInt("user_id")
                && mData.get(position).getBaseModel("warehouse").getInt("isMaster") != 2){
            holder.tvAccept.setText(Util.getIconString(R.string.icon_check, "  ", "DUYỆT NHẬP KHO"));
            holder.tvAccept.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            holder.tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitAcceptImport(position,
                            DataUtil.array2ListObject(mData.get(position).getString("details")));
                }
            });

        }else {
            holder.tvAccept.setText(Util.getIconString(R.string.icon_x, "  ", "CHƯA DUYỆT"));
            holder.tvAccept.setTextColor(mContext.getResources().getColor(R.color.black_text_color_hint));
            holder.tvAccept.setOnClickListener(null);
        }

        holder.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transaction.shareViaZalo(createImportContentForShare(position));
            }
        });



    }

    private void submitAcceptImport(int position, List<BaseModel> importdetails) {
        checkInventory(importdetails, mData.get(position).getBaseModel("fr_warehouse"), new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    CustomCenterDialog.alertWithCancelButton(null, String.format("Xác nhận nhập kho %s của nhân viên %s",
                        Util.DateHourString(mData.get(position).getLong("createAt")),
                        mData.get(position).getBaseModel("user").getString("displayName")),
                        "XÁC NHẬN","HỦY", new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                if (result){
                                    String param = DataUtil.createUpdateAcceptImportParam(mData.get(position).getInt("id"), User.getId());
                                    postUpdateImport(param, new CallbackBoolean() {
                                        @Override
                                        public void onRespone(Boolean result){
                                            mListener.onRespone(true);

                                        }
                                    });


                                }

                            }
                        });

                }else {
                    CustomCenterDialog.alert("Nhập kho",
                            String.format("Vui lòng nhập kho %s để thao tác tiếp", mData.get(position).getBaseModel("fr_warehouse").getString("name")),
                            "đồng ý");
                }
            }
        });



    }

    private void deleteImport(int position) {
        CustomCenterDialog.alertWithCancelButton(null, String.format("Bạn muốn xóa nhập kho %s",Util.DateHourString(mData.get(position).getLong("createAt"))),
        "ĐỒNG Ý","HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    CustomerConnect.DeleteImport(mData.get(position).getString("id"), new CallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result) {
                            if (result.getBoolean("deleted")){
                                Util.getInstance().stopLoading(true);
                                Util.showToast("Xóa thành công!");
                                mListener.onRespone(true);
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, true);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Import_ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate,  tvDelete, tvUser, tvWarehouse, tvAccept, tvCopy;
        private RecyclerView rvProduct;
        private CardView lnParent;

        public Import_ProductViewHolder(View itemView) {
            super(itemView);
            lnParent = (CardView) itemView.findViewById(R.id.import_product_item_parent);
            tvDate = itemView.findViewById(R.id.import_product_item_title);
            tvDelete = itemView.findViewById(R.id.import_product_item_delete);
            tvCopy = itemView.findViewById(R.id.import_product_item_copy);
            tvUser = itemView.findViewById(R.id.import_product_item_user);
            tvWarehouse = itemView.findViewById(R.id.import_product_item_warehouse);
            tvAccept = itemView.findViewById(R.id.import_product_item_accept);
            rvProduct = itemView.findViewById(R.id.import_product_item_rvproduct);


        }

    }

    public void reloadData(List<BaseModel> list){
        mData = list;
        DataUtil.sortbyStringKey("createAt", mData, true);

        notifyDataSetChanged();
    }

    private void postUpdateImport(String param, CallbackBoolean listener){
        CustomerConnect.PostImport(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                listener.onRespone(true);

            }

            @Override
            public void onError(String error) {
                listener.onRespone(false);
            }
        }, true);
    }

    private String createImportContentForShare(int position){
        String patern = "Ngày %s\nNhập từ %s >>> %s\n\n%s\n\n=>>> %s";
        String detail = "";
        List<BaseModel> details = DataUtil.array2ListObject(mData.get(position).getString("details"));
        for (int i=0; i<details.size(); i++){
            detail += String.format("%s x  %s (%s)",
                    details.get(i).getInt("quantity"),
                    details.get(i).getString("productName"),
                    Util.FormatMoney(details.get(i).getDouble("basePrice")))
                    + (i == details.size()-1? "": "\n");
        }

        return String.format(patern, Util.DateHourString(mData.get(position).getLong("createAt")),
                mData.get(position).getBaseModel("fr_warehouse").getString("name"),
                mData.get(position).getBaseModel("warehouse").getString("name"),
                detail,
                Util.FormatMoney(DataUtil.sumMoneyFromList(details, "basePrice", "quantity")));

    }

    private void checkInventory(List<BaseModel> importdetails, BaseModel warehouse, CallbackBoolean listener){
        DataUtil.checkInventory(importdetails, warehouse.getInt("id"), new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                if (list.size() >0){
                    CustomCenterDialog.showListProductWithDifferenceQuantity( warehouse.getString("name") +": KHÔNG ĐỦ TỒN KHO ",
                            list,
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    listener.onRespone(false);


                                }
                            });

                }else {
                    listener.onRespone(true);
                }
            }
        });
    }

}
