package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CButtonVertical;
import wolve.dms.customviews.CInputForm;
import wolve.dms.libraries.CustomSwitchButton;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerInfoFragment extends Fragment implements View.OnClickListener,  RadioGroup.OnCheckedChangeListener {
    private View view, swCover;
    private CInputForm edAdress, edPhone, edName;
    private EditText edShopName, edNote;
    private CButtonVertical mDirection, mPrint, mCall;
    private TextView tvStatus, tvLastStatus, tvType;
    private CustomSwitchButton swStatus;
    private RadioGroup rgType;

    private CustomerActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_info,container,false);
        CustomerActivity.infoFragment = this;

        initializeView();

        addEvent();

        return view;
    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        edAdress = (CInputForm) view.findViewById(R.id.customer_info_adress);
        edPhone = (CInputForm) view.findViewById(R.id.customer_info_phone);
        edName = (CInputForm) view.findViewById(R.id.customer_info_name);
        edShopName = (EditText) view.findViewById(R.id.customer_info_shopname_name);
        mDirection = view.findViewById(R.id.customer_info_direction);
        mPrint = view.findViewById(R.id.customer_info_print);
        mCall = view.findViewById(R.id.customer_info_call);
        edNote = view.findViewById(R.id.add_customer_note);
        swStatus = view.findViewById(R.id.customer_info_switch);
        tvStatus = view.findViewById(R.id.customer_info_status);
        tvLastStatus = view.findViewById(R.id.customer_info_last_status);
        rgType = view.findViewById(R.id.customer_info_shoptype);
        swCover = view.findViewById(R.id.customer_info_switch_cover);
        tvType = view.findViewById(R.id.customer_info_type);

    }


    protected void reshowAddress(BaseModel address){
        String add= String.format("%s%s, %s, %s",
                Util.isEmpty(address.getString("address")) ? "" : address.getString("address") + " ",
                Util.isEmpty(address.getString("street")) ? "" : address.getString("street"),
                address.getString("district"),
                address.getString("province"));
        edAdress.setText(add);
        edAdress.setFocusable(false);

        mActivity.reshowAdd(address);
    }

    public void reloadInfo(){
        edAdress.setIconMoreText(mActivity.getResources().getString(R.string.icon_edit_pen));

        reshowAddress(mActivity.currentCustomer);

        edPhone.setText(Util.FormatPhone(mActivity.currentCustomer.getString("phone")));


        edName.setText(mActivity.currentCustomer.getString("name"));
        edShopName.setText(mActivity.currentCustomer.getString("signBoard"));
        setNoteText(mActivity.currentCustomer.getString("note"));
        tvType.setText(Constants.getShopName(mActivity.currentCustomer.getString("shopType")));

        if (mActivity.currentCustomer.getString("shopType").equals(Constants.shopType[0])){
            rgType.check(R.id.customer_info_repair);
        }else if (mActivity.currentCustomer.getString("shopType").equals(Constants.shopType[1])){
            rgType.check(R.id.customer_info_wash);
        }else if (mActivity.currentCustomer.getString("shopType").equals(Constants.shopType[2])){
            rgType.check(R.id.customer_info_access);
        }else if (mActivity.currentCustomer.getString("shopType").equals(Constants.shopType[3])){
            rgType.check(R.id.customer_info_ser);
        }

        if (mActivity.listCheckins.size() >0){
            String checkin = String.format("%s %s",Util.DateHourString(mActivity.listCheckins.get(mActivity.listCheckins.size()-1).getLong("createAt")), mActivity.listCheckins.get(mActivity.listCheckins.size()-1).getString("note") );
            tvLastStatus.setText(checkin);
        }

        mPrint.setVisibility(mActivity.listDebtBill.size() > 0? View.VISIBLE : View.GONE);
        mCall.setVisibility(mActivity.currentCustomer.getString("phone").equals("")?View.GONE: View.VISIBLE);
        
        updateStatus();


//todo addevent after setdata
        phoneEvent();
        nameEvent();
        shopNameEvent();
        noteEvent();
        rgType.setOnCheckedChangeListener(this);
    }

    private void addEvent() {
        //swStatus.setOnCheckedChangeListener(this);
        mDirection.setOnClickListener(this);
        mCall.setOnClickListener(this);
        mPrint.setOnClickListener(this);
        swCover.setOnClickListener(this);
        edAdress.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                mActivity.openEditMapFragment();

            }
        });


    }

    private void updateStatus(){
        if (mActivity.customerStatusID ==2){
            swStatus.setChecked(false);
            tvStatus.setText("Khách hàng không quan tâm");
            tvStatus.setTextColor(mActivity.getResources().getColor(R.color.black_text_color));
            tvType.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_border_grey));
            tvType.setTextColor(mActivity.getResources().getColor(R.color.black_text_color_hint));

            //changeShopTypeBackground(false);
            mCall.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_grey));
            mCall.setIconColor(mActivity.getResources().getColor(R.color.colorWhite));
            mCall.setTextColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            mDirection.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            mPrint.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edAdress.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edName.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edPhone.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));


            mActivity.btnShopCart.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_border_blue));
            mActivity.btnShopCart.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));


        }else {
            swStatus.setChecked(true);
            //changeShopTypeBackground(true);
            mCall.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_blue));
            mCall.setIconColor(mActivity.getResources().getColor(R.color.colorWhite));
            mCall.setTextColor(mActivity.getResources().getColor(R.color.colorBlueTransparent));
            mDirection.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            mPrint.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edAdress.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edName.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edPhone.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));

            tvType.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_border_grey));
            tvType.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));

            mActivity.btnShopCart.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_blue));
            mActivity.btnShopCart.setTextColor(mActivity.getResources().getColor(R.color.colorWhite));

            if (mActivity.customerStatusID == 3){
                swStatus.setCheckedColor(getResources().getColor(R.color.colorBlue));
                tvStatus.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));
                if (mActivity.listBills.size() >0){
                    String text = String.format("Khách đã mua hàng - Cách %d ngày", Util.countDay(mActivity.listBills.get(0).getLong("createAt")));
                    tvStatus.setText(text);

                }else {
                    tvStatus.setText("Khách đã mua hàng");
                }

            }else if (mActivity.customerStatusID ==1){
                if (mActivity.listCheckins.size() >0){
                    swStatus.setCheckedColor(getResources().getColor(R.color.orange_dark));
                    tvStatus.setTextColor(mActivity.getResources().getColor(R.color.orange_dark));
                    tvStatus.setText("Khách hàng có quan tâm");

                }else {
                    swStatus.setCheckedColor(getResources().getColor(R.color.colorPink));
                    tvStatus.setTextColor(mActivity.getResources().getColor(R.color.colorPink));
                    tvStatus.setText("Khách hàng mới");

                }

            }




        }

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.customer_info_direction:
                openGoogleMap();

                break;

            case R.id.customer_info_call:
                mActivity.openCallScreen(mActivity.currentCustomer.getString("phone"));

                break;

            case R.id.customer_info_print:
                mActivity.printDebtBills();

                break;

            case R.id.customer_info_switch_cover:
                if (swStatus.isChecked()){
                    CustomCenterDialog.showReasonChoice("CHỌN LÝ DO KHÔNG QUAN TÂM",
                            "Nhập lý do khác ",
                            new ArrayList<>(), new CallbackString() {
                                @Override
                                public void Result(String s) {
                                    updateRessonNotInterted(s);

                                }
                            });

                }else {
                    mActivity.customerStatusID = 1;
                    updateStatus();
                }


                break;

        }
    }

    private void openGoogleMap(){
        CustomCenterDialog.alertWithCancelButton("Chỉ đường", "Mở ứng dụng bản đồ để tiếp tục chỉ đường", "Tiếp tục","Quay lại", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean re) {
                if (re){
                    Transaction.openGoogleMapRoute(mActivity.currentCustomer.getDouble("lat"), mActivity.currentCustomer.getDouble("lng"));

                }

            }
        });
    }

    private void shopNameEvent(){
        Util.textEvent(edShopName, new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.saveCustomerToLocal("signBoard", s);
                updateShopName();

            }
        });
    }

    private void phoneEvent(){
        edPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.saveCustomerToLocal("phone", s);

                mCall.setVisibility(mActivity.currentCustomer.getString("phone").equals("")?View.GONE: View.VISIBLE);

            }
        });
    }

    private void nameEvent(){
        edName.addTextChangeName(new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.saveCustomerToLocal("name", s);

            }
        });
    }

    private void noteEvent(){
        Util.textEvent(edNote, new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.saveCustomerToLocal("note", getCustomerNote(edNote.getText().toString()));
            }
        });
    }

    private void setNoteText(String note){
        if (!note.isEmpty()){
            try {
                JSONObject object = new JSONObject(note.trim());
                edNote.setText(object.getString("note"));

            } catch (JSONException e) {
                edNote.setText(note);
            }
        }

    }

    private void updateShopName(){
        mActivity.tvTitle.setText(String.format("%s %s",
                Constants.getShopName(mActivity.currentCustomer.getString("shopType")),
                mActivity.currentCustomer.getString("signBoard")));


    }

    private String getCustomerNote(String text){
        String result = "";
        //Double total = Util.getTotal(listBills).getDouble("debt");
        if (mActivity.currentDebt > 0.0){
            try{
                JSONObject object = new JSONObject();
                //object.put("debt", total);
                object.put("note", edNote.getText().toString());
                object.put("userId", mActivity.listBills.get(mActivity.listBills.size()-1).getJsonObject("user").getInt("id"));
                object.put("userName", mActivity.listBills.get(mActivity.listBills.size()-1).getJsonObject("user").getString("displayName"));
                result = object.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            result = text;
        }

        //return result;
        return  text;
    }



//    private void changeShopTypeBackground(boolean isEnable){
//        for (int i = 0; i < rgType.getChildCount(); ++i) {
//            RadioButton radioButton = (RadioButton) rgType.getChildAt(i);
//            radioButton.setBackgroundResource(isEnable? R.drawable.btn_round_white_selected_grey : R.drawable.btn_round_white_selected_grey);
//            radioButton.setTextColor(isEnable? getResources().getColor(R.color.black_text_color) : getResources().getColor(R.color.black_text_color));
//        }
//
//    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.customer_info_repair:
                mActivity.saveCustomerToLocal("shopType", Constants.shopType[0]);
                tvType.setText(Constants.shopName[0]);
                break;

            case R.id.customer_info_wash:
                mActivity.saveCustomerToLocal("shopType", Constants.shopType[1]);
                tvType.setText(Constants.shopName[1]);
                break;

            case R.id.customer_info_access:
                mActivity.saveCustomerToLocal("shopType", Constants.shopType[2]);
                tvType.setText(Constants.shopName[2]);
                break;

            case R.id.customer_info_ser:
                mActivity.saveCustomerToLocal("shopType", Constants.shopType[3]);
                tvType.setText(Constants.shopName[3]);
                break;
        }

        updateShopName();

    }

    private void updateRessonNotInterted(String content){
        String note = String.format("CHUYỂN SANG KHÔNG QUAN TÂM VÌ: %s", content);
        mActivity.customerStatusID = 2;

        CustomerConnect.PostCheckin(createParamCheckin(mActivity.currentCustomer, mActivity.customerStatusID, note), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                mActivity.saveCustomerToLocal("status.id",2);
                //mActivity.currentCustomer.put("status.id", mActivity.customerStatusID);
                //mActivity.submitCustomer();
                updateStatus();

                Util.showToast("Đã chuyển sang không quan tâm");

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private String createParamCheckin(BaseModel customer, int statusId, String note){
        String params = String.format(Api_link.SCHECKIN_CREATE_PARAM, customer.getInt("id"),
                statusId,
                Util.encodeString(note),
                User.getUserId());
                //Util.encodeString(String.format("[%s] %s", Util.HourStringNatural(countTime), note)),


        return params;
    }


}

