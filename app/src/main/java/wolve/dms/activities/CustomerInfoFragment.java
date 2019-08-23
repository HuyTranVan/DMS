package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerInfoFragment extends Fragment implements View.OnClickListener {
    private View view;
    private CInputForm edAdress, edPhone, edName;
//    private RecyclerView rvPayment;
    //private TextView tvCount;


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



    }

    public void reloadInfo(){
        String add= String.format("%s%s, %s, %s",
                Util.isEmpty(mActivity.currentCustomer.getString("address")) ? "" : mActivity.currentCustomer.getString("address") + " ",
                Util.isEmpty(mActivity.currentCustomer.getString("street")) ? "" : mActivity.currentCustomer.getString("street"),
                mActivity.currentCustomer.getString("district"),
                mActivity.currentCustomer.getString("province"));
        edAdress.setText(add);
        edAdress.setFocusable(false);


        edPhone.setText(Util.FormatPhone(mActivity.currentCustomer.getString("phone")));
        edPhone.addTextChangePhone();

        edName.setText(mActivity.currentCustomer.getString("name"));
        edName.addTextChangeName();
    }

    private void addEvent() {
        edAdress.setOnClickView(new CInputForm.ClickListener() {
            @Override
            public void onClick(View v) {
                Util.showToast("abc");
            }
        });

    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        edAdress = (CInputForm) view.findViewById(R.id.customer_info_adress);
        edPhone = (CInputForm) view.findViewById(R.id.customer_info_phone);
        edName = (CInputForm) view.findViewById(R.id.customer_info_name);
        //rvPayment = (RecyclerView) view.findViewById(R.id.customer_rvpayment);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.customer_info_adress:


            break;


        }
    }

}
