package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

public class CustomerInfoFragment extends Fragment implements View.OnClickListener {
    private View view;
    private CInputForm edAdress, edPhone, edName;
    private EditText edShopName, edNote;
    private RadioGroup rdType;
    private CButtonVertical mDirection, mPrint, mCall;


//    private Customer_PaymentAdapter adapter;
    private CustomerActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_info,container,false);
        CustomerActivity.infoFragment = this;

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    public void intitialData() {
        edAdress.setIconMoreText(mActivity.getResources().getString(R.string.icon_edit_pen));

//        mDirection.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_border_blue));
//        mPrint.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_border_grey));


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
        //edNote.setText(getCustomerNote());

        createEditShopName(mActivity.currentCustomer.getString("shopType"));

        setNoteText(mActivity.currentCustomer.getString("note"));

//todo addevent after setdata
        phoneEvent();
        nameEvent();
        shopNameEvent();
        noteEvent();
    }



    private void addEvent() {

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
        rdType = view.findViewById(R.id.customer_info_shoptype);
        mDirection = view.findViewById(R.id.customer_info_direction);
        mPrint = view.findViewById(R.id.customer_info_print);
        mCall = view.findViewById(R.id.customer_info_call);
        edNote = view.findViewById(R.id.add_customer_note);

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

    private void createEditShopName(String shoptype) {
        rdType.removeAllViews();
        rdType.clearCheck();

        rdType.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        for(int i=0; i< Constants.shopName.length; i++){
            RadioButton radioButton  = new RadioButton(mActivity);
            radioButton.setText(Constants.shopName[i]);


            radioButton.setTag(Constants.shopType[i]);
            rdType.addView(radioButton);

            if (shoptype.equals(Constants.shopType[i])){
                rdType.check(radioButton.getId());

            }
        }
        rdType.setOnCheckedChangeListener(null);

        rdType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) view.findViewById(checkedId);
                    if (radioButton != null){
                        mActivity.currentCustomer.put("shopType", Constants.getShopType(radioButton.getText().toString()));
                        updateShopName();

                        mActivity.saveCustomerToLocal(mActivity.currentCustomer);

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


}
