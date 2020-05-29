package wolve.dms.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomInputDialog;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductImportChoosenAdapter extends RecyclerView.Adapter<ProductImportChoosenAdapter.ProductAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackInt mListener;
    private CallbackObject mListenerObject;
    private boolean flagRemove;
    //private View mView;
    private Timer mTimer;

    public ProductImportChoosenAdapter(List<BaseModel> list, boolean flag_remove, CallbackInt listener, CallbackObject listenerOb) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mData = list;
        this.mListener = listener;
        this.mListenerObject = listenerOb;
        this.flagRemove = flag_remove;

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
        if (mData.get(position).hasKey("currentQuantity") && mData.get(position).getInt("currentQuantity") >0){
            holder.tvQuantityLimit.setText(mData.get(position).getString("currentQuantity"));
        }else {
            holder.tvQuantityLimit.setText("");
        }

        if (mData.get(position).hasKey("quantity") && mData.get(position).getInt("quantity") >0 ){
            holder.tvMinus.setVisibility(View.VISIBLE);
            holder.edQuantity.setVisibility(View.VISIBLE);
            holder.edQuantity.setText(mData.get(position).getString("quantity"));

        }else {
            holder.tvMinus.setVisibility(View.GONE);
            holder.edQuantity.setVisibility(View.GONE);
        }

        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = mData.get(position).hasKey("quantity")? mData.get(position).getInt("quantity") : 0;
                if (!mData.get(position).hasKey("currentQuantity") || mData.get(position).getInt("currentQuantity") > quantity ){
                    mData.get(position).put("quantity", quantity +1);
                    notifyItemChanged(position);
                    mListenerObject.onResponse(mData.get(position));

                }else {
                    Util.showToast("Sản phẩm hết tồn kho");
                }
                mListener.onResponse(mData.size());

            }
        });

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 CustomCenterDialog.showDialogInputQuantity(mData.get(position).getString("name"),
                         mData.get(position).getString("quantity"),
                         0,
                         false,
                         new CallbackString() {
                             @Override
                             public void Result(String s) {
                                 if (s.equals("") || Integer.parseInt(s) == 0) {
                                     mData.get(position).put("quantity", 0);

                                     mListenerObject.onResponse(mData.get(position));
                                     mData.remove(position);
                                     mListener.onResponse(mData.size());

                                 } else {
                                     if (!mData.get(position).hasKey("currentQuantity") || mData.get(position).getInt("currentQuantity") >= Integer.parseInt(s)){
                                         mListenerObject.onResponse(mData.get(position));
                                         mData.get(position).put("quantity", Integer.parseInt(s));
                                     }else {
                                         Util.showSnackbar("Vui lòng nhập giá trị nhỏ hơn số tồn", null, null);
                                     }

                                 }
                                 notifyDataSetChanged();
                             }
                         });

             }

         });

        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.get(position).put("quantity", mData.get(position).getInt("quantity") -1);
                mListenerObject.onResponse(mData.get(position));
                if (mData.get(position).getInt("quantity") ==0){
                    if (flagRemove){
                        mData.remove(position);

                    }

                }
                notifyDataSetChanged();
                mListener.onResponse(mData.size());
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
            tvMinus =  itemView.findViewById(R.id.product_import_item_minus);
            tvPlus =  itemView.findViewById(R.id.product_import_item_plus);
            edQuantity = itemView.findViewById(R.id.product_import_item_number);
            tvQuantityLimit = itemView.findViewById(R.id.product_import_item_currentquantity);

        }

    }

    public void insertData(BaseModel model){
        boolean checkDup = false;
        for (int i =0; i<mData.size(); i++){
            if (model.getInt("id") == mData.get(i).getInt("id")){
                if (model.getInt("quantity") ==0){
                    mData.remove(i);
                }else {
                    mData.get(i).put("quantity", model.getInt("quantity"));
                }

                checkDup = true;
                break;
            }
        }
        if (!checkDup){
            mData.add(model);

        }
        notifyDataSetChanged();
        mListener.onResponse(mData.size());
    }

    public List<BaseModel> getmData(){
        return mData;
    }

    public void emptyList(){
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void selectAll(){
        for (BaseModel model: mData){
            model.put("quantity", model.getInt("currentQuantity"));

        }
        notifyDataSetChanged();
        mListener.onResponse(mData.size());
    }

    public void noneSelect(){
        for (BaseModel model: mData){
            model.put("quantity", 0);

        }
        notifyDataSetChanged();
        mListener.onResponse(mData.size());
    }

    public int getAllSelected(){
        int quantity = 0;
        int limit = 0;

        for (BaseModel model: mData){
            quantity += model.getInt("quantity");
            limit += model.getInt("currentQuantity");

        }

        if (quantity  == 0){
            return -1;
        }else if (quantity <limit){
            return 0;
        }else {
            return 1;
        }

    }

    public List<BaseModel> getAllDataHaveQuantity(){
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model: mData){
            if (model.getInt("quantity") >0){
                results.add(model);
            }

        }
        return  results;
    }


}
