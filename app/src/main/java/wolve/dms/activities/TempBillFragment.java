package wolve.dms.activities;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.TempbillAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.Callback2Int;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;

/**
 * Created by macos on 9/16/17.
 */

public class TempBillFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private TextView tvTitle, tvCheck, tvList, tvClose;
    private RecyclerView rvTempBill;
    private RelativeLayout lnSelect;

    private HomeActivity mActivity;
    private TempbillAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tempbill, container, false);
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
        tvCheck.setOnClickListener(this);
        tvList.setOnClickListener(this);
        tvClose.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (HomeActivity) getActivity();
        rvTempBill = view.findViewById(R.id.tempbill_rvtempbill);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        lnSelect = view.findViewById(R.id.tempbill_select_group);
        tvCheck = view.findViewById(R.id.tempbill_select_check);
        tvList = view.findViewById(R.id.tempbill_select_list);
        tvTitle = view.findViewById(R.id.tempbill_title);
        tvClose = view.findViewById(R.id.tempbill_select_close);

    }

    public void reloadData() {
        adapter.reloadData(mActivity.listTempBill);
        if (mActivity.listTempBill.size() == 0) {
            mActivity.onBackPressed();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;

            case R.id.tempbill_select_check:
                if (tvCheck.getText().toString().equals(Constants.CHECK_ALL)) {
                    adapter.checkAllData(true);
                    tvCheck.setText(Constants.UNCHECK);

                } else {
                    adapter.checkAllData(false);
                    tvCheck.setText(Constants.CHECK_ALL);
                }
                break;

            case R.id.tempbill_select_close:
                closeSelected();
                break;

            case R.id.tempbill_select_list:
                List<BaseModel> listbill = getBillFromTemp(adapter.getCheckedData());
                if (listbill.size() > 0) {
                    CustomCenterDialog.showListProduct("Danh sách sản phẩm", sumProduct(listbill));

                } else {
                    Util.showToast("Vui lòng chọn hóa đơn đê thao tác tiếp");

                }

                break;

        }
    }

    public boolean checkSelected() {
        return lnSelect.getVisibility() == View.VISIBLE;
    }

    public void closeSelected() {
        lnSelect.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        adapter.checkAllData(false);
    }

    private void createRVBill(List<BaseModel> list) {
        adapter = new TempbillAdapter(list, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                if (User.getId() == object.getInt("user_id") || Util.isAdmin()){
                    gotoPrintBillScreen(object);

                }else {
                    checkLocation(new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double distance = MapUtil.distance(location.getLatitude(),
                                    location.getLongitude(),
                                    object.getBaseModel("customer").getDouble("lat"),
                                    object.getBaseModel("customer").getDouble("lng"));

                            if (distance < Constants.CHECKIN_DISTANCE ) {
                                gotoPrintBillScreen(object);

                            } else {

                                Util.showSnackbar("Không thể tiếp tục do ở bên ngoài của hàng ", null, null);
                            }


                        }
                    }, true);

                }
            }
        }, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    if (adapter.getCheckedData().size() > 0) {
                        lnSelect.setVisibility(View.VISIBLE);
                        tvCheck.setText(Constants.CHECK_ALL);
                        tvTitle.setVisibility(View.GONE);
                        btnBack.setVisibility(View.GONE);

                    } else {
                        lnSelect.setVisibility(View.GONE);
                        tvTitle.setVisibility(View.VISIBLE);
                        btnBack.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, new Callback2Int() {
            @Override
            public void onResponse(int value1, int value2) {
                Transaction.gotoEditTempBillActivity(value1, value2);

            }
        });
        Util.createLinearRV(rvTempBill, adapter);
    }

    private void gotoPrintBillScreen(BaseModel model) {
        BaseModel param = createGetParam(ApiUtil.CUSTOMER_GETDETAIL() + model.getString("customer_id"), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                BaseModel customer = DataUtil.rebuiltCustomer(result, true);
                CustomSQL.setBaseModel(Constants.CUSTOMER, customer);

                if (User.checkUserWarehouse())
                    Transaction.checkInventoryBeforePrintBill(customer.getBaseModel("temp_bill"),
                            DataUtil.array2ListObject(customer.getBaseModel("temp_bill").getString(Constants.BILL_DETAIL)),
                            User.getCurrentUser().getInt("warehouse_id"), new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result){
                                        mActivity.onBackPressed();
                                    }
                                }
                            });
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }

    private void checkLocation(LocationListener listener, boolean stopLoading) {
        Util.getInstance().showLoading();
        mActivity.getCurrentLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Util.getInstance().stopLoading(stopLoading);
                listener.onLocationChanged(location);

            }
        });
    }

    private List<BaseModel> sumProduct(List<BaseModel> listbill) {
        List<BaseModel> listdetail = DataUtil.getAllBillDetail(listbill);
        List<BaseModel> results = new ArrayList<>();

        for (int i = 0; i < listdetail.size(); i++) {
            boolean check = false;
            for (int ii = 0; ii < results.size(); ii++) {
                if (listdetail.get(i).getInt("productId") == results.get(ii).getInt("productId")) {
                    results.get(ii).put("quantity", results.get(ii).getInt("quantity") + listdetail.get(i).getInt("quantity"));
                    check = true;
                    break;
                }

            }
            if (!check) {
                results.add(listdetail.get(i));
            }


        }

        return results;
    }

    private List<BaseModel> getBillFromTemp(List<BaseModel> list) {
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model : list) {
            results.add(model.getBaseModel("bill"));
        }

        return results;
    }


}
