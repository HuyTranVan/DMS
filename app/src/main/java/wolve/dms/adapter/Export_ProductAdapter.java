package wolve.dms.adapter;

import static wolve.dms.activities.BaseActivity.createGetParam;
import static wolve.dms.activities.BaseActivity.createPostParam;
import static wolve.dms.activities.ImportActivity.alertInventoryNotEnough;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 5/24/17.
 */

public class Export_ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackBoolean mListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private RecyclerView rvImport;
    private boolean isLoading = false;
    private int warehouse_id;

    public Export_ProductAdapter(int warehouseid, List<BaseModel> data, CallbackBoolean listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = data;
        this.mListener = listener;
        this.warehouse_id = warehouseid;
        //DataUtil.sortbyStringKey("createAt", mData, true);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rvImport = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void addItem(BaseModel model) {
        mData.add(model);
        notifyItemInserted(mData.size() - 1);
    }

    public void removeItem(int pos) {
        mData.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_import_product_item1, parent, false);
            return new Export_ProductAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new Export_ProductAdapter.LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof Export_ProductAdapter.ItemViewHolder) {
            setItemRows((Export_ProductAdapter.ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof Export_ProductAdapter.LoadingViewHolder) {
            showLoadingView((Export_ProductAdapter.LoadingViewHolder) viewHolder, position);
        }

    }

    private void showLoadingView(Export_ProductAdapter.LoadingViewHolder viewHolder, int position) {

    }

    private void setItemRows(ItemViewHolder holder, int position) {
        holder.tvDate.setText(Util.DateHourString(mData.get(position).getLong("createAt")));
        holder.tvUser.setText(Util.getIconString(R.string.icon_username, "  ", mData.get(position).getBaseModel("user").getString("displayName")));
        holder.lnParent.setBackgroundResource(position % 2 == 0 ? R.color.colorWhite : R.color.colorLightGrey);
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

        holder.tvTotal.setText(Util.FormatMoney(DataUtil.sumMoneyFromList(importdetails,
                mData.get(position).getBaseModel("fr_warehouse").getInt("isMaster") == 1 ? "basePrice" : "purchasePrice",
                "quantity")));

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImport(position);

            }
        });
        holder.tvAccept.setVisibility(mData.get(position).getInt("acceptBy") != 0 ? View.GONE : View.VISIBLE);
        holder.tvDelete.setVisibility(mData.get(position).getInt("acceptBy") != 0 ? View.GONE : View.VISIBLE);
        holder.tvCopy.setVisibility(mData.get(position).getInt("acceptBy") != 0 && Util.isAdmin() ? View.VISIBLE : View.GONE);

        if (Util.isAdmin()
                || User.getId() == mData.get(position).getBaseModel("fr_warehouse").getInt("user_id")
                && mData.get(position).getBaseModel("warehouse").getInt("isMaster") != 2) {
            holder.tvAccept.setText(Util.getIconString(R.string.icon_check, "  ", "DUYỆT NHẬP KHO"));
            holder.tvAccept.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            holder.tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitAcceptImport(position,
                            DataUtil.array2ListObject(mData.get(position).getString("details")));
                }
            });

        } else {
            holder.tvAccept.setText(Util.getIconString(R.string.icon_x, "  ", "CHƯA DUYỆT"));
            holder.tvAccept.setTextColor(mContext.getResources().getColor(R.color.black_text_color_hint));
            holder.tvAccept.setOnClickListener(null);
        }

        holder.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transaction.shareViaOtherApp(createImportContentForShare(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvDelete, tvUser, tvWarehouse, tvAccept, tvCopy, tvTotal;
        private RecyclerView rvProduct;
        private LinearLayout lnParent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            lnParent = (LinearLayout) itemView.findViewById(R.id.import_product_item_parent);
            tvDate = itemView.findViewById(R.id.import_product_item_title);
            tvDelete = itemView.findViewById(R.id.import_product_item_delete);
            tvCopy = itemView.findViewById(R.id.import_product_item_copy);
            tvUser = itemView.findViewById(R.id.import_product_item_user);
            tvWarehouse = itemView.findViewById(R.id.import_product_item_warehouse);
            tvAccept = itemView.findViewById(R.id.import_product_item_accept);
            rvProduct = itemView.findViewById(R.id.import_product_item_rvproduct);
            tvTotal = itemView.findViewById(R.id.import_product_item_total);

        }

    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void submitAcceptImport(int position, List<BaseModel> importdetails) {
        checkFromInventory(importdetails, mData.get(position).getBaseModel("fr_warehouse"), new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    CustomCenterDialog.alertWithCancelButton(null, String.format("Xác nhận nhập kho %s của nhân viên %s",
                            Util.DateHourString(mData.get(position).getLong("createAt")),
                            mData.get(position).getBaseModel("user").getString("displayName")),
                            "XÁC NHẬN", "HỦY", new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result) {
                                        String param = DataUtil.createUpdateAcceptImportParam(mData.get(position).getInt("id"), User.getId());
                                        postUpdateImport(param, new CallbackBoolean() {
                                            @Override
                                            public void onRespone(Boolean result) {
                                                mListener.onRespone(true);

                                            }
                                        });


                                    }

                                }
                            });

                } else {
                    CustomCenterDialog.alert("Nhập kho",
                            String.format("Vui lòng nhập kho %s để thao tác tiếp", mData.get(position).getBaseModel("fr_warehouse").getString("name")),
                            "đồng ý");
                }
            }
        });

    }

    private void deleteImport(int position) {
        CustomCenterDialog.alertWithCancelButton(null, String.format("Bạn muốn xóa nhập kho %s", Util.DateHourString(mData.get(position).getLong("createAt"))),
                "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result) {
                            BaseModel param = createGetParam(ApiUtil.IMPORT_DELETE() + mData.get(position).getString("id"), false);
                            new GetPostMethod(param, new NewCallbackCustom() {
                                @Override
                                public void onResponse(BaseModel result, List<BaseModel> list) {
                                    if (result.getBoolean("deleted")) {
                                        Util.getInstance().stopLoading(true);
                                        Util.showToast("Xóa thành công!");
                                        mListener.onRespone(true);
                                    }
                                }

                                @Override
                                public void onError(String error) {

                                }
                            }, 1).execute();

                        }

                    }
                });
    }

    public void reloadData(List<BaseModel> list) {
        mData = list;
        DataUtil.sortbyStringKey("createAt", mData, true);

        notifyDataSetChanged();
    }

    private void postUpdateImport(String paramdetail, CallbackBoolean listener) {
        BaseModel param = createPostParam(ApiUtil.IMPORT_NEW(), paramdetail, true, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onRespone(true);
            }

            @Override
            public void onError(String error) {
                listener.onRespone(false);
            }
        }, 1).execute();

    }

    private String createImportContentForShare(int position) {
        String patern = "Ngày %s\nNhập từ %s >>> %s\n\n%s\n\n=>>> %s";
        String detail = "";
        List<BaseModel> details = DataUtil.array2ListObject(mData.get(position).getString("details"));
        for (int i = 0; i < details.size(); i++) {
            detail += String.format("%s x  %s (%s)",
                    details.get(i).getInt("quantity"),
                    details.get(i).getString("productName"),
                    Util.FormatMoney(details.get(i).getDouble("basePrice")))
                    + (i == details.size() - 1 ? "" : "\n");
        }

        return String.format(patern, Util.DateHourString(mData.get(position).getLong("createAt")),
                mData.get(position).getBaseModel("fr_warehouse").getString("name"),
                mData.get(position).getBaseModel("warehouse").getString("name"),
                detail,
                Util.FormatMoney(DataUtil.sumMoneyFromList(details, "basePrice", "quantity")));

    }

    private void checkFromInventory(List<BaseModel> importdetails, BaseModel fromWarehouse, CallbackBoolean listener) {
        DataUtil.listImportNotEnough(importdetails, fromWarehouse, new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> listNotEnough) {
                if (listNotEnough.size() > 0){
                    if (fromWarehouse.getInt("isMaster") == 2){
                        alertInventoryNotEnough(fromWarehouse.getString("name"), new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                CustomCenterDialog.importToTempWarehouse(listNotEnough.get(0).getBaseModel("masterWarehouse"),
                                        fromWarehouse,
                                        listNotEnough,
                                        new CallbackBoolean() {
                                            @Override
                                            public void onRespone(Boolean result) {
                                                if (result){
                                                    CustomCenterDialog.alertWithButtonCanceled("Thành công",
                                                            "Nhập từ kho tổng thành công, vui lòng nhập kho lại!",
                                                            "Đồng ý",false,  new CallbackBoolean() {
                                                                @Override
                                                                public void onRespone(Boolean result) {
                                                                    listener.onRespone(true);
                                                                }
                                                            });

                                                }else {
                                                    listener.onRespone(false);
                                                }
                                            }
                                        });
                            }
                        });

                    }else if (fromWarehouse.getInt("isMaster") == 3){
                        listener.onRespone(false);

                    }

                }else {
                    listener.onRespone(true);
                }



            }
        });

    }











//        DataUtil.checkInventory(importdetails, warehouse.getInt("id"), new CallbackListObject() {
//            @Override
//            public void onResponse(List<BaseModel> list) {
//                if (list.size() > 0 && fromWarehouse.getInt("isMaster") == 2) {
//
//
//
//
//                    CustomCenterDialog.showListProductWithDifferenceQuantity(warehouse.getString("name") + ": KHÔNG ĐỦ TỒN KHO ",
//                            list,
//                            new CallbackBoolean() {
//                                @Override
//                                public void onRespone(Boolean result) {
//                                    listener.onRespone(false);
//
//
//                                }
//                            });
//
//                } else {
//                    listener.onRespone(true);
//                }
//            }
//        });


    public void initScrollListener() {
        rvImport.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() >= 20-1 &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == getItemCount() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });

    }

    private void loadMore() {
        addItem(null);
        notifyItemInserted(mData.size() - 1);

        BaseModel param = createGetParam(String.format(ApiUtil.EXPORTS(), getItemCount() - 1, 20, warehouse_id), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                removeItem(getItemCount() - 1);
                notifyDataSetChanged();
                for (BaseModel model: list) {
                    addItem(model);

                }
                notifyDataSetChanged();
                isLoading = false;

            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }

}
