package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CButtonVertical;
import wolve.dms.customviews.CInputForm;
import wolve.dms.customviews.CTextIcon;
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
    private View view;
    private CInputForm edAdress, edPhone, edName, edNote;
    protected CTextIcon tvCheckin, tvHistory;
    private EditText edShopName ;
    private CButtonVertical mDirection, mCall, mInterest;
    private TextView tvLastCheckin, tvType;
    private RadioGroup rgType;

    private CustomerActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_info,container,false);


        initializeView();

        addEvent();

        return view;
    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        mActivity.infoFragment = this;

        edAdress = (CInputForm) view.findViewById(R.id.customer_info_adress);
        edPhone = (CInputForm) view.findViewById(R.id.customer_info_phone);
        edName = (CInputForm) view.findViewById(R.id.customer_info_name);
        edShopName = (EditText) view.findViewById(R.id.customer_info_shopname_name);
        mDirection = view.findViewById(R.id.customer_info_direction);
        mInterest = view.findViewById(R.id.customer_info_interest);
        mCall = view.findViewById(R.id.customer_info_call);
        edNote = view.findViewById(R.id.customer_info_note);
        tvLastCheckin = view.findViewById(R.id.customer_info_last_status);
        rgType = view.findViewById(R.id.customer_info_shoptype);
        tvCheckin = view.findViewById(R.id.customer_checkin);
        tvType = view.findViewById(R.id.customer_info_type);
        tvHistory = view.findViewById(R.id.customer_history);

    }


    protected void reshowAddress(BaseModel address){
        String add= String.format("%s%s, %s, %s",
                Util.isEmpty(address.getString("address")) ? "" : address.getString("address") + " ",
                Util.isEmpty(address.getString("street")) ? "" : address.getString("street"),
                address.getString("district"),
                address.getString("province"));
        edAdress.setFocusable(false);
        edAdress.setText(add);
        mActivity.tvAddress.setText(add);


        //mActivity.reshowAdd(address);
    }

    public void reloadInfo(){
        edAdress.setIconMoreText(mActivity.getResources().getString(R.string.icon_edit_pen));
        edNote.setIconMoreText(mActivity.getResources().getString(R.string.icon_edit_pen));
        edNote.setFocusable(false);
        reshowAddress(mActivity.currentCustomer);

        edPhone.setText(Util.FormatPhone(mActivity.currentCustomer.getString("phone")));
        edName.setText(mActivity.currentCustomer.getString("name"));
        edShopName.setText(mActivity.currentCustomer.getString("signBoard"));
        edNote.setText(getNoteText(mActivity.currentCustomer.getString("note")));
        //setNoteText(mActivity.currentCustomer.getString("note"));
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
            String note = mActivity.listCheckins.get(mActivity.listCheckins.size()-1).getInt("meetOwner") == 0?
                    "Không gặp chủ nhà. " + mActivity.listCheckins.get(mActivity.listCheckins.size()-1).getString("note") :
                    mActivity.listCheckins.get(mActivity.listCheckins.size()-1).getString("note");

            String checkin = String.format("%s %s",Util.DateHourString(mActivity.listCheckins.get(mActivity.listCheckins.size()-1).getLong("createAt")), note );
            tvLastCheckin.setText(checkin);
            tvHistory.setVisibility(View.VISIBLE);
        }else {
            tvHistory.setVisibility(View.GONE);

        }
        mCall.setVisibility(mActivity.currentCustomer.getString("phone").equals("")?View.GONE: View.VISIBLE);
        
        updateStatus(mActivity.currentCustomer.getBaseModel("status").getInt("id"));


//todo addevent after setdata
        phoneEvent();
        nameEvent();
        shopNameEvent();
        noteEvent();
        rgType.setOnCheckedChangeListener(this);
    }

    private void addEvent() {
        tvHistory.setOnClickListener(this);
        mDirection.setOnClickListener(this);
        mCall.setOnClickListener(this);
        mInterest.setOnClickListener(this);
        tvCheckin.setOnClickListener(this);
        edAdress.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                mActivity.openEditMapFragment();

            }
        });
        edNote.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNote();

            }
        });



    }

    private void updateStatus(int status){
        if (status ==2){
            tvType.setTextColor(mActivity.getResources().getColor(R.color.black_text_color_hint));

            mInterest.setText("Không quan tâm");
            mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_grey));
            mInterest.setTextColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            mInterest.setIconText(mActivity.getResources().getString(R.string.icon_x));
            mDirection.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            mCall.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));

            edAdress.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edNote.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edName.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edPhone.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));


            mActivity.btnShopCart.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_white_border_blue));
            mActivity.btnShopCart.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));


        }else {
            tvType.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));

            mDirection.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            mCall.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));

            edAdress.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edNote.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edName.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edPhone.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));



            mActivity.btnShopCart.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_blue));
            mActivity.btnShopCart.setTextColor(mActivity.getResources().getColor(R.color.colorWhite));

            if (status == 3){
                mInterest.setText("Đã mua hàng");
                mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_blue));
                mInterest.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));
                mInterest.setIconText(mActivity.getResources().getString(R.string.icon_check));

            }else if (status ==1){
                mInterest.setText("Có quan tâm");
                mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_orange));
                mInterest.setTextColor(mActivity.getResources().getColor(R.color.orange_dark));
                mInterest.setIconText(mActivity.getResources().getString(R.string.icon_heart));

            }else if (status ==0){
                mInterest.setText("Khách hàng mới");
                mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_pink));
                mInterest.setTextColor(mActivity.getResources().getColor(R.color.colorPink));
                mInterest.setIconText(mActivity.getResources().getString(R.string.icon_star));
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
                Transaction.openCallScreen(mActivity.currentCustomer.getString("phone"));

                break;

            case R.id.customer_history:
                mActivity.changeFragment(new CheckinFragment() , true);

                break;

            case R.id.customer_checkin:
                if (!tvCheckin.getText().toString().toLowerCase().contains("checkin")){
                    showDialogNote();

                }else {
                    int currentRating = mActivity.listCheckins.size()>0?
                            mActivity.listCheckins.get(mActivity.listCheckins.size() -1).getInt("rating"):
                            1;
                    CustomCenterDialog.showCheckinDialog("chi tiết ghé thăm cửa hàng",
                            mActivity.currentCustomer.getInt("id"),
                            mActivity.currentCustomer.getBaseModel("status").getInt("id"),
                            currentRating,
                            new CallbackObject() {
                                @Override
                                public void onResponse(BaseModel object) {
                                    submitCheckin(object.getString("param"), new CallbackBoolean() {
                                        @Override
                                        public void onRespone(Boolean result) {
                                            if (result){
                                                if (object.getBoolean("updateStatus")){
                                                    mActivity.currentCustomer.put("status_id", object.getInt("status"));
                                                    mActivity.submitCustomer(mActivity.currentCustomer,mActivity.listCheckins.size() +1,  new CallbackBoolean() {
                                                        @Override
                                                        public void onRespone(Boolean result) {
                                                            mActivity.returnPreviousScreen(mActivity.currentCustomer.BaseModelstoString());

                                                        }
                                                    });

                                                }else {
                                                    mActivity.returnPreviousScreen(mActivity.currentCustomer.BaseModelstoString());

                                                }


                                            }
                                        }
                                    });

                                }
                            });

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
//        Util.textEvent(edNote, new CallbackString() {
//            @Override
//            public void Result(String s) {
//                mActivity.saveCustomerToLocal("note", getCustomerNote(edNote.getText().toString()));
//            }
//        });
    }

    private String getNoteText(String note){
        if (!note.isEmpty()){
            try {
                JSONObject object = new JSONObject(note.trim());
                return object.getString("note");


            } catch (JSONException e) {
                return note;
            }
        }else {
            return "";
        }

    }


    private void updateShopName(){
        mActivity.tvTitle.setText(String.format("%s %s",
                Constants.getShopName(mActivity.currentCustomer.getString("shopType")),
                mActivity.currentCustomer.getString("signBoard")));


    }

//    private String getCustomerNote(String text){
//        String result = "";
//        //Double total = Util.getTotal(listBills).getDouble("debt");
//        if (mActivity.currentDebt > 0.0){
//            try{
//                JSONObject object = new JSONObject();
//                //object.put("debt", total);
//                object.put("note", edNote.getText().toString());
//                object.put("userId", mActivity.listBills.get(mActivity.listBills.size()-1).getJsonObject("user").getInt("id"));
//                object.put("userName", mActivity.listBills.get(mActivity.listBills.size()-1).getJsonObject("user").getString("displayName"));
//                result = object.toString();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }else {
//            result = text;
//        }
//
//        //return result;
//        return  text;
//    }



//    private void changeShopTypeBackground(boolean isEnable){
//        for (int i = 0; i < rgType.getChildCount(); ++i) {
//            RadioButton radioButton = (RadioButton) rgType.getChildAt(i);
//            radioButton.setBackgroundResource(isEnable? R.drawable.btn_round_white_border_grey : R.drawable.btn_round_white_border_grey);
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

    private String createParamCheckin(BaseModel customer, int statusId, String note){
        String params = String.format(Api_link.SCHECKIN_CREATE_PARAM, customer.getInt("id"),
                statusId,
                Util.encodeString(note),
                User.getId());

        return params;
    }

    protected void submitCheckin(String param, CallbackBoolean listener){
        CustomerConnect.PostCheckin(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                listener.onRespone(true);

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void showDialogNote(){
        String currentNote = getNoteText(mActivity.currentCustomer.getString("note"));
        CustomCenterDialog.showReasonChoice("GHI CHÚ KHÁCH HÀNG",
                "Nhập nội dung ghi chú ",
                Util.isEmpty(currentNote)? "" : currentNote +"\n",
                false,
                new CallbackString() {
                    @Override
                    public void Result(String s) {
                        edNote.setText(s);
                        mActivity.saveCustomerToLocal("note", s);

                    }
                });
    }


}

