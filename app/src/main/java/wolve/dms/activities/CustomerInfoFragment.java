package wolve.dms.activities;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CButtonVertical;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerInfoFragment extends Fragment implements View.OnClickListener,  SwitchButton.OnCheckedChangeListener , RadioGroup.OnCheckedChangeListener {
    private View view;
    private CInputForm edAdress, edPhone, edName;
    private EditText edShopName, edNote;
    private CButtonVertical mDirection, mPrint, mCall;
    private TextView tvStatus;
    private SwitchButton swStatus;
    private RadioGroup rgType;

    private CustomerActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_info,container,false);
        CustomerActivity.infoFragment = this;

        initializeView();

        addEvent();

        intitialData();

        return view;
    }

    public void intitialData() {
        edAdress.setIconMoreText(mActivity.getResources().getString(R.string.icon_edit_pen));

        new Handler().postDelayed (new Runnable() {
            @Override
            public void run() {
                swStatus.setChecked(true);
            }}, 500);





    }

    protected void reshowAddress(BaseModel address){
        String add= String.format("%s%s, %s, %s",
                Util.isEmpty(address.getString("address")) ? "" : address.getString("address") + " ",
                Util.isEmpty(address.getString("street")) ? "" : address.getString("street"),
                address.getString("district"),
                address.getString("province"));
        edAdress.setText(add);
        edAdress.setFocusable(false);
    }

    public void reloadInfo(){
        reshowAddress(mActivity.currentCustomer);
        updateCallButton();
        edPhone.setText(Util.FormatPhone(mActivity.currentCustomer.getString("phone")));
        edName.setText(mActivity.currentCustomer.getString("name"));
        edShopName.setText(mActivity.currentCustomer.getString("signBoard"));
        setNoteText(mActivity.currentCustomer.getString("note"));

        if (mActivity.currentCustomer.getString("shopType").equals(Constants.shopType[0])){
            rgType.check(R.id.customer_info_repair);
        }else if (mActivity.currentCustomer.getString("shopType").equals(Constants.shopType[1])){
            rgType.check(R.id.customer_info_wash);
        }else if (mActivity.currentCustomer.getString("shopType").equals(Constants.shopType[2])){
            rgType.check(R.id.customer_info_access);
        }else if (mActivity.currentCustomer.getString("shopType").equals(Constants.shopType[3])){
            rgType.check(R.id.customer_info_ser);
        }



//todo addevent after setdata
        phoneEvent();
        nameEvent();
        shopNameEvent();
        noteEvent();
        rgType.setOnCheckedChangeListener(this);
    }



    private void addEvent() {
        swStatus.setOnCheckedChangeListener(this);
        mDirection.setOnClickListener(this);
        mCall.setOnClickListener(this);
        mPrint.setOnClickListener(this);
        edAdress.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                mActivity.openEditMapFragment();

            }
        });


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
        rgType = view.findViewById(R.id.customer_info_shoptype);

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
                mActivity.currentCustomer.put("signBoard", s);
                updateShopName();
                mActivity.saveCustomerToLocal(mActivity.currentCustomer);

            }
        });
    }

    private void phoneEvent(){
        edPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.currentCustomer.put("phone", s);
                mActivity.saveCustomerToLocal(mActivity.currentCustomer);

                updateCallButton();

            }
        });
    }

    private void nameEvent(){
        edName.addTextChangeName(new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.currentCustomer.put("name", s);
                mActivity.saveCustomerToLocal(mActivity.currentCustomer);

            }
        });
    }

    private void noteEvent(){
        Util.textEvent(edNote, new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.currentCustomer.put("note", getCustomerNote());
                mActivity.saveCustomerToLocal(mActivity.currentCustomer);
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

    private void updateCallButton(){
        mCall.setVisibility(mActivity.currentCustomer.getString("phone").equals("")?View.GONE: View.VISIBLE);
    }

    private String getCustomerNote(){
        String note = "";
        //Double total = Util.getTotal(listBills).getDouble("debt");
        if (mActivity.currentDebt > 0.0){
            try{
                JSONObject object = new JSONObject();
                //object.put("debt", total);
                object.put("note", edNote.getText().toString());
                object.put("userId", mActivity.listBills.get(mActivity.listBills.size()-1).getJsonObject("user").getInt("id"));
                object.put("userName", mActivity.listBills.get(mActivity.listBills.size()-1).getJsonObject("user").getString("displayName"));
                note = object.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            note = edNote.getText().toString();
        }

        return note;
    }


    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (isChecked){
            changeShopTypeBackground(true);
            mCall.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_blue));
            mCall.setIconColor(mActivity.getResources().getColor(R.color.colorWhite));
            mCall.setTextColor(mActivity.getResources().getColor(R.color.colorBlueTransparent));
            mDirection.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            mPrint.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edAdress.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edName.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edPhone.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            tvStatus.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));

        }else {
            changeShopTypeBackground(false);
            mCall.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_grey));
            mCall.setIconColor(mActivity.getResources().getColor(R.color.colorWhite));
            mCall.setTextColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            mDirection.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            mPrint.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edAdress.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edName.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edPhone.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            tvStatus.setTextColor(mActivity.getResources().getColor(R.color.black_text_color_hint));



        }
    }

    private void changeShopTypeBackground(boolean isEnable){
        for (int i = 0; i < rgType.getChildCount(); ++i) {
            RadioButton radioButton = (RadioButton) rgType.getChildAt(i);
            radioButton.setBackgroundResource(isEnable? R.drawable.btn_round_grey_selected_blue : R.drawable.btn_round_white_selected_grey);
            radioButton.setTextColor(isEnable? getResources().getColor(R.color.colorWhite) : getResources().getColor(R.color.black_text_color));
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.customer_info_repair:
                mActivity.currentCustomer.put("shopType", Constants.shopType[0]);

                break;

            case R.id.customer_info_wash:
                mActivity.currentCustomer.put("shopType", Constants.shopType[1]);

                break;

            case R.id.customer_info_access:
                mActivity.currentCustomer.put("shopType", Constants.shopType[2]);

                break;

            case R.id.customer_info_ser:

                mActivity.currentCustomer.put("shopType", Constants.shopType[3]);
                break;
        }

        updateShopName();
        mActivity.saveCustomerToLocal(mActivity.currentCustomer);
    }
}
