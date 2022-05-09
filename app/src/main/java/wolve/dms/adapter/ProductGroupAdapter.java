package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackObjectAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductGroupAdapter extends RecyclerView.Adapter<ProductGroupAdapter.ProductGroupAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObjectAdapter mItem, mEdit, mDelete;

    public ProductGroupAdapter(List<BaseModel> list,
                               CallbackObjectAdapter item,
                               CallbackObjectAdapter edit,
                               CallbackObjectAdapter delete) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mItem = item;
        this.mEdit = edit;
        this.mDelete = delete;

        DataUtil.sortProductGroup(mData, false);
    }

    @Override
    public ProductGroupAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_productgroup_item, parent, false);
        return new ProductGroupAdapterViewHolder(itemView);
    }

    public void addItems(ArrayList<ProductGroup> list) {
        mData = new ArrayList<>();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ProductGroupAdapterViewHolder holder, int position) {
        holder.tvGroupName.setText(
                String.format("%s (%d)",
                mData.get(position).getString("name"),
                mData.get(position).getInt("product_nums"))        );
        holder.tvSales.setVisibility(mData.get(position).getInt("isSales") == 1? View.VISIBLE : View.GONE);
        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem.onRespone(mData.get(holder.getAdapterPosition()), holder.getAdapterPosition());

            }
        });

        holder.tvMenu.setVisibility(Util.isAdmin()? View.VISIBLE : View.GONE);
        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDropdow.createListDropdown(
                        holder.tvMenu,
                        DataUtil.createList2String(Util.getIconString(R.string.icon_edit, "  ", "Chỉnh sửa"),
                                Util.getIconString(R.string.icon_delete, "   ", "Xóa")),
                        0,
                        true,
                        new CallbackClickAdapter() {
                    @Override
                    public void onRespone(String data, int pos) {
                        if (pos ==0){
                            mEdit.onRespone(mData.get(holder.getAdapterPosition()), holder.getAdapterPosition());

                        }else if(pos ==1){
                            if (mData.get(holder.getAdapterPosition()).getInt("product_nums") >0){
                                Util.showSnackbar("Không thể xoá nhóm đã có sản phảm", null, null);

                            }else {
                                CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa nhóm " + mData.get(holder.getAdapterPosition()).getString("name"), "XÓA", "HỦY", new CallbackBoolean() {
                                    @Override
                                    public void onRespone(Boolean result) {
                                        if (result){
                                            BaseModel param = createGetParam(ApiUtil.PRODUCT_GROUP_DELETE() + mData.get(holder.getAdapterPosition()).getString("id"), false);
                                            new GetPostMethod(param, new NewCallbackCustom() {
                                                @Override
                                                public void onResponse(BaseModel result, List<BaseModel> list) {
                                                    Util.getInstance().stopLoading(true);
                                                    mDelete.onRespone(mData.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                                                }

                                                @Override
                                                public void onError(String error) {

                                                }
                                            }, 1).execute();
                                        }


                                    }
                                });

                            }


                        }

                    }

                });

            }
        });

//        holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//
//                return true;
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductGroupAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvMenu, tvSales;
        private LinearLayout lnParent;

        public ProductGroupAdapterViewHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.productgroup_item_name);
            tvSales = (TextView) itemView.findViewById(R.id.productgroup_item_sales);
            tvMenu = itemView.findViewById(R.id.productgroup_item_menu);
            lnParent = (LinearLayout) itemView.findViewById(R.id.productgroup_item_parent);
        }

    }

}
