package wolve.dms.activities;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Customer_CheckinsAdapter;
import wolve.dms.adapter.TempbillAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class TempBillFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private RecyclerView rvTempBill;

    private HomeActivity mActivity;
    private TempbillAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tempbill,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        mActivity.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        createRVBill(mActivity.listTempBill);


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (HomeActivity) getActivity();
        rvTempBill = view.findViewById(R.id.tempbill_rvtempbill);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);


    }

    public void reloadData(){
        adapter.reloadData(mActivity.listTempBill);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;

        }
    }

    private void createRVBill(List<BaseModel> list){
        adapter = new TempbillAdapter(list, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                checkLocation(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double distance = MapUtil.distance(location.getLatitude(),
                                location.getLongitude(),
                                object.getBaseModel("customer").getDouble("lat"),
                                object.getBaseModel("customer").getDouble("lng"));

                        if (distance < Constants.CHECKIN_DISTANCE){
                            gotoPrintBillScreen(object);

                        }else {
                            Util.getInstance().stopLoading(true);
                            Util.showSnackbar("Không thể tiếp tục do ở bên ngoài của hàng ", null, null);
                        }


                    }
                }, false);


            }
        });
        Util.createLinearRV(rvTempBill, adapter);
    }

    private void gotoPrintBillScreen(BaseModel model ){
        CustomerConnect.GetCustomerDetail(model.getString("customer_id"), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                BaseModel customer = DataUtil.rebuiltCustomer(result, true);
                CustomSQL.setBaseModel(Constants.CUSTOMER, customer);

                Transaction.gotoPrintBillActivity(customer.getBaseModel("temp_bill"), false);
            }

            @Override
            public void onError(String error) {

            }
        }, false,true);
    }

    private void checkLocation(LocationListener listener, boolean stopLoading){
        Util.getInstance().showLoading();
        mActivity.getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Util.getInstance().stopLoading(stopLoading);
                listener.onLocationChanged(location);



//                CustomSQL.setLong(Constants.CURRENT_DISTANCE, (long) distance);
//                mSuccess.onRespone(true);
//
//                if (distance < Constants.CHECKIN_DISTANCE){
//                    tvCheckInStatus.setText(String.format("Đang trong phạm vi cửa hàng ~%sm", Math.round(distance)));
//                    threadShowTime.start();
//
//                }else {
//                    tvCheckInStatus.setText(String.format("Đang bên ngoài cửa hàng ~%s", distance >1000? Math.round(distance)/1000 +"km": Math.round(distance) + "m"));
//
//                }

            }
        });
    }



}
