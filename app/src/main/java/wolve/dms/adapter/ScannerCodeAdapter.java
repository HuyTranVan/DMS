package wolve.dms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 9/30/16.
 */
public class ScannerCodeAdapter extends RecyclerView.Adapter<ScannerCodeAdapter.ScannerCodeViewHolder>{

    private List<String> mData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private CountListener mListener;

    public interface CountListener{
        void onRespone(int count);
    }

    public ScannerCodeAdapter(List<String> data, CountListener listener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mListener = listener;
        this.mData = data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void  addItem(String code) {
        if (!checkDuplicate(code)){
            mData.add(code);
            saveCurrentList();
            notifyDataSetChanged();
            mListener.onRespone(mData.size());

        }else {
            mListener.onRespone(-1);
        }

    }

    public void deleteAllItem(){
        mData = new ArrayList<>();
        saveCurrentList();
        notifyDataSetChanged();

    }

    public List<String> getAllItem(){
        return mData;

    }

    @Override
    public ScannerCodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_scanner_code_item, parent, false);
        return new ScannerCodeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ScannerCodeViewHolder holder, final int position) {
        holder.tvCode.setText(mData.get(position));
        holder.tvNumber.setText(String.valueOf(mData.size() -position));
        holder.line.setVisibility(position == mData.size() -1?View.GONE:View.VISIBLE);

        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCenterDialog.alertWithCancelButton(null, "Xóa " + mData.get(position) +" khỏi danh sách" , "ĐỒNG Ý","HỦY", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        mData.remove(position);
                        saveCurrentList();
                        notifyDataSetChanged();
//                        notifyItemRemoved(position);
//                        notifyItemRangeChanged(position, getItemCount());

                        mListener.onRespone(mData.size());

                    }
                });
            }
        });

    }

    public class ScannerCodeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvCode;
        private ImageView btnClose;
        private View line;

        public ScannerCodeViewHolder(View itemView) {
            super(itemView);
            tvNumber = (TextView) itemView.findViewById(R.id.scanner_code_item_number);
            tvCode = (TextView) itemView.findViewById(R.id.scanner_code_item_code);
            btnClose =  itemView.findViewById(R.id.icon_close);
            line = itemView.findViewById(R.id.scanner_code_item_seperateline);
        }
    }

    private boolean checkDuplicate(String code){
        boolean check = false;
        for (int i=0; i<mData.size(); i++){
            if (code.equals(mData.get(i))){
                check = true;
                break;
            }
        }

        return check;

    }

    private void saveCurrentList(){
        CustomSQL.setString(Constants.BARCODE, new Gson().toJson(mData));
    }



}

