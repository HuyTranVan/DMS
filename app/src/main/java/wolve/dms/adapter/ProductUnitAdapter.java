package wolve.dms.adapter;

import static wolve.dms.activities.BaseActivity.createGetParam;

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
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductUnitAdapter extends RecyclerView.Adapter<ProductUnitAdapter.ProductUnitAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private RecyclerView recyclerView;

    public ProductUnitAdapter(RecyclerView rv, List<BaseModel> list, CallbackObject listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.recyclerView = rv;
        this.mListener = listener;


        //DataUtil.sortProductGroup(mData, false);
    }

    @Override
    public ProductUnitAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_productunit_item, parent, false);
        return new ProductUnitAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductUnitAdapterViewHolder holder, int position) {
        holder.edName.setText(mData.get(position).getString("name"));
        holder.edName.setFocusable(false);

        holder.tvDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                deleteItem(holder.getAdapterPosition());
            }
        });

        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(mData.get(holder.getAdapterPosition()));

            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ProductUnitAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEdit, tvDelete;
        private EditText edName;
        private LinearLayout lnParent;

        public ProductUnitAdapterViewHolder(View itemView) {
            super(itemView);
            edName = itemView.findViewById(R.id.productunit_item_name);
            tvDelete = (TextView) itemView.findViewById(R.id.productunit_item_delete);
            tvEdit = itemView.findViewById(R.id.productunit_item_edit);
            lnParent = (LinearLayout) itemView.findViewById(R.id.productunit_item_parent);
        }

    }

    public void addItem(BaseModel item){
        mData.add(item);
        notifyItemInserted(mData.size()-1);
        recyclerView.requestLayout();

    }

    private void deleteItem(int position){
        CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa đơn vị " + mData.get(position).getString("name"), "XÓA", "HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    BaseModel param = createGetParam(ApiUtil.PRODUCT_UNIT_DELETE() + mData.get(position).getString("id"), false);
                    new GetPostMethod(param, new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {
                            notifyItemRemoved(position);
                            mData.remove(position);
                            recyclerView.requestLayout();



                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, 1).execute();
                }


            }
        });


    }

    public void updateItem(BaseModel item){
        for (int i=0; i<mData.size(); i++){
            if (item.getInt("id") == mData.get(i).getInt("id")){
                mData.get(i).put("name", item.getString("name"));
                notifyItemChanged(i);
                break;

            }


        }


    }


}
