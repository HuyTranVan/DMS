package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.libraries.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createPostParam;


/**
 * Created by tranhuy on 5/24/17.
 */

public class InventoryEditAdapter extends RecyclerView.Adapter<InventoryEditAdapter.ProductAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public InventoryEditAdapter(List<BaseModel> list) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;

    }

    @Override
    public ProductAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_product_import_item, parent, false);
        return new ProductAdapterViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ProductAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvGroup.setText(mData.get(position).getBaseModel("productGroup").getString("name"));
        holder.edQuantity.setText(mData.get(position).getString("currentQuantity"));

        holder.edQuantity.setVisibility(View.VISIBLE);
        holder.tvPlus.setVisibility(View.GONE);
        holder.tvMinus.setVisibility(View.GONE);

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEditInventory(position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvGroup, tvQuantityLimit, tvPlus, tvMinus;
        private EditText edQuantity;
        private LinearLayout lnParent;

        public ProductAdapterViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.product_import_item_parent);
            tvName = (TextView) itemView.findViewById(R.id.product_import_item_name);
            tvGroup = (TextView) itemView.findViewById(R.id.product_import_item_group);
            tvMinus = itemView.findViewById(R.id.product_import_item_minus);
            tvPlus = itemView.findViewById(R.id.product_import_item_plus);
            edQuantity = itemView.findViewById(R.id.product_import_item_number);
            tvQuantityLimit = itemView.findViewById(R.id.product_import_item_currentquantity);

        }

    }

    public List<BaseModel> getmData() {
        return mData;
    }

    private void showDialogEditInventory(int pos) {
        CustomCenterDialog.showDialogInputQuantity(mData.get(pos).getString("name"),
                mData.get(pos).getString("currentQuantity"),
                mData.get(pos).getInt("currentQuantity"),
                true,
                new CallbackString() {
                    @Override
                    public void Result(String s) {
                        BaseModel param = createPostParam(ApiUtil.INVENTORY_EDIT_QUANTITY(),
                                String.format(ApiUtil.INVENTORY_EDIT_QUANTITY_PARAM, mData.get(pos).getInt("id"), s),
                                false,
                                false);
                        new GetPostMethod(param, new NewCallbackCustom() {
                            @Override
                            public void onResponse(BaseModel result, List<BaseModel> list) {
                                mData.get(pos).put("currentQuantity", result.getInt("quantity"));
                                notifyItemChanged(pos);

                                Util.showToast("Cập nhật thành công");
                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, true).execute();
//                        SystemConnect.UpdateInventoryQuantity(param, new CallbackCustom() {
//                            @Override
//                            public void onResponse(BaseModel result) {
//
//                            }
//
//                            @Override
//                            public void onError(String error) {
//
//                            }
//                        }, true);


                    }
                });


    }

}
