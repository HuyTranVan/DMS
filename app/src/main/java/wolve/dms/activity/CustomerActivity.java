package wolve.dms.activity;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CheckinsAdapter;
import wolve.dms.adapter.ReasonAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CInputForm;
import wolve.dms.controls.CTextView;
import wolve.dms.controls.WorkaroundMapFragment;
import wolve.dms.models.Checkin;
import wolve.dms.models.Customer;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDialog;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements OnMapReadyCallback,GoogleMap.OnCameraIdleListener ,  View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    public GoogleMap mMap;
    public SupportMapFragment mapFragment;
    private ImageView btnBack;
    private CTextView tvTrash;
    private Button btnSubmit, btnCurLocation ;
    private FloatingActionButton btnShopCart;
    private TextView tvTitle;
    private CInputForm edName, edPhone , edAdress, edStreet, edDistrict, edCity, edNote , edShopType, edShopName;
    private RadioGroup rgStatus;
    private RadioButton rdInterested, rdNotInterested, rdOrdered;
    private RecyclerView rvReason, rvCheckins;
    private ScrollView scParent;


    private ReasonAdapter reasonAdapter;
    private CheckinsAdapter checkinsAdapter ;
    private Customer currentCustomer;
    private List<Checkin> listCheckins = new ArrayList<>();
    private int currentStatusId = 1;
    private String firstName ="";


    @Override
    public int getResourceLayout() {
        return R.layout.activity_customer;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        btnSubmit = (Button) findViewById(R.id.add_customer_submit);
        btnCurLocation = (Button) findViewById(R.id.add_customer_currentlocation);
        btnBack = (ImageView) findViewById(R.id.icon_back);
        btnShopCart = (FloatingActionButton) findViewById(R.id.add_customer_shopcart);
        tvTrash = (CTextView) findViewById(R.id.icon_more);
        tvTitle = (TextView) findViewById(R.id.add_customer_title);
        edName = (CInputForm) findViewById(R.id.add_customer_name);
        edPhone = (CInputForm) findViewById(R.id.add_customer_phone);
        edAdress = (CInputForm) findViewById(R.id.add_customer_adress);
        edStreet = (CInputForm) findViewById(R.id.add_customer_street);
        edDistrict = (CInputForm) findViewById(R.id.add_customer_district);
        edCity = (CInputForm) findViewById(R.id.add_customer_city);
        edNote = (CInputForm) findViewById(R.id.add_customer_note);
        edShopType = (CInputForm) findViewById(R.id.add_customer_shoptype);
        edShopName = (CInputForm) findViewById(R.id.add_customer_shopname);
        rgStatus = (RadioGroup) findViewById(R.id.customer_radiogroup_status);
        rdInterested = (RadioButton) findViewById(R.id.customer_radio_status_interested);
        rdNotInterested = (RadioButton) findViewById(R.id.customer_radio_status_nointerested);
        rdOrdered = (RadioButton) findViewById(R.id.customer_radio_status_ordered);
        rvReason = (RecyclerView) findViewById(R.id.add_customer_rvreason);
        rvCheckins = (RecyclerView) findViewById(R.id.add_customer_rvcheckins);
        scParent = (ScrollView) findViewById(R.id.customer_parent);

        //fix MapView in ScrollView
        mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_customer_map);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_customer_map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scParent.requestDisallowInterceptTouchEvent(true);
            }
        });

        edShopType.setDropdown(true);
        edShopType.setDropdownList(Util.arrayToList(Constants.shopName));


    }

    @Override
    public void initialData() {
        loadReasonList();
        String bundle = getIntent().getExtras().getString(Constants.CUSTOMER);
        if (bundle != null){
            try {
                currentCustomer = new Customer(new JSONObject(bundle));
                firstName = currentCustomer.getString("name");

                edName.setText(currentCustomer.getString("name") == null? "" : currentCustomer.getString("name"));
                edPhone.setText(currentCustomer.getString("phone") == null? "" : currentCustomer.getString("phone"));
                edAdress.setText((currentCustomer.getString("address") == null? "" : currentCustomer.getString("address")));
                edStreet.setText(currentCustomer.getString("street") == null? "" : currentCustomer.getString("street"));
                edDistrict.setText(currentCustomer.getString("district"));
                edCity.setText(currentCustomer.getString("province"));
                edShopName.setText(currentCustomer.getString("signBoard"));
                edShopType.setText(Constants.getShopInfo(currentCustomer.getString("shopType") , null));
                tvTitle.setText(edShopType.getText().toString() +" - " + currentCustomer.getString("signBoard") );

                if (currentCustomer.getString("status") != null){
                    currentStatusId = new JSONObject(currentCustomer.getString("status")).getInt("id");
                }



                switch (currentStatusId){
                    case 1:
                        rdInterested.setChecked(true);
                        break;
                    case 2:
                        rdNotInterested.setChecked(true);
                        break;
                    case 3:
                        rdOrdered.setChecked(true);
                        break;
                }

                if (currentCustomer.getString("checkIns") != null && !currentCustomer.getString("checkIns").equals("[]")){
                    JSONArray array = new JSONArray(currentCustomer.getString("checkIns"));
                    for (int i=0; i<array.length(); i++){
                        listCheckins.add(new Checkin(array.getJSONObject(i)));
                    }

                    loadCheckinsList(listCheckins);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (currentCustomer.getInt("id") !=0){
            tvTrash.setVisibility(User.getCurrentUser().getString("role").equals("MANAGER") ? View.VISIBLE : View.GONE);
            btnCurLocation.setVisibility(View.VISIBLE);
        }else {
            tvTrash.setVisibility(View.GONE);
            btnCurLocation.setVisibility(View.GONE);
        }

        if (edShopName.getText().toString().trim().equals("")){
            edShopName.setSelection();
        }else {
            edPhone.setSelection();
        }

        edShopName.addTextChangeListenter(new CInputForm.OnQueryTextListener() {
            @Override
            public boolean textChanged(String query) {
                if (!firstName.contains("Anh ")){
                    edName.setText("Anh "+ query.substring(query.trim().lastIndexOf(" ")+1));
                }
                tvTitle.setText(edShopType.getText().toString() + " - " + query);
                return true;
            }
        });

    }

    @Override
    public void addEvent() {
        mapFragment.getMapAsync(this);
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvTrash.setOnClickListener(this);
        rgStatus.setOnCheckedChangeListener(this);
        btnCurLocation.setOnClickListener(this);
        btnShopCart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                returnPreviousScreen(currentCustomer);

                break;

            case R.id.add_customer_submit:
                submitCustomer();

                break;

            case R.id.icon_more:
                deleteCustomer();

                break;

            case R.id.add_customer_currentlocation:
                LatLng curLocation = new LatLng(getCurLocation().getLatitude(), getCurLocation().getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 18), 400, null);

                break;

            case R.id.add_customer_shopcart:
                openShopCartScreen(getCurrentCustomer());

                break;
        }
    }

    private void returnPreviousScreen(Customer customer){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.CUSTOMER, customer != null? customer.CustomertoString() : null);
        setResult(Constants.RESULT_CUSTOMER_ACTIVITY,returnIntent);
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    private void openShopCartScreen(Customer customer){
        if (customer.getInt("id") == 0){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    final Customer responseCustomer = new Customer(result);
                    Transaction.gotoShopCartActivity(responseCustomer.CustomertoString());

                }

                @Override
                public void onError(String error) {

                }
            }, true);

        }else {
            Transaction.gotoShopCartActivity(customer.CustomertoString());
        }

    }

    private void submitCustomer(){
        if (edName.getText().toString().trim().equals("")
                || edAdress.getText().toString().trim().equals("")
                || edDistrict.getText().toString().trim().equals("")
                || edCity.getText().toString().trim().equals("")
                || edShopName.getText().toString().trim().equals("")
                || edShopType.getText().toString().trim().equals("")){
            CustomDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                @Override public void onRespone(Boolean result) {

                }
            });

        }else {

            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    final Customer responseCustomer = new Customer(result);

                    if (reasonAdapter.getCheckedReason() > 0){
                        String params = String.format(Constants.SCHECKIN_CREATE_PARAM, responseCustomer.getInt("id"),
                                reasonAdapter.getCheckedReason() ==0 ? currentStatusId : reasonAdapter.getCheckedReason(),
                                Util.encodeString(edNote.getText().toString().trim()),
                                User.getCurrentUserId()
                        );

                        CustomerConnect.PostCheckin(params, new CallbackJSONObject() {
                            @Override
                            public void onResponse(JSONObject result) {
                                returnPreviousScreen(responseCustomer);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, true);

                    }else {
                        returnPreviousScreen(responseCustomer);
                    }


                }

                @Override
                public void onError(String error) {

                }
            }, false);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);

        LatLng curLocation = new LatLng(currentCustomer.getDouble("lat"), currentCustomer.getDouble("lng"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 18), 200, null);

    }

    @Override
    public void onCameraIdle() {
        Util.hideKeyboard(edName);
        LatLng curLocation =  mMap.getCameraPosition().target;
        try {
            currentCustomer.put("lat", curLocation.latitude);
            currentCustomer.put("lng", curLocation.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteCustomer(){
        CustomDialog.alertWithCancelButton(null, "Xóa khách hàng " + edShopType.getText().toString() +" " + currentCustomer.getString("signBoard"), "ĐỒNG Ý","HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                CustomerConnect.DeleteCustomer(currentCustomer.getString("id"), new CallbackJSONObject() {
                    @Override
                    public void onResponse(JSONObject result) {
                        returnPreviousScreen(null);
                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            returnPreviousScreen(currentCustomer);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra(Constants.CUSTOMER_CART) != null && requestCode == Constants.RESULT_SHOPCART_ACTIVITY){

        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.customer_radio_status_interested:
                currentStatusId = 1;

                break;

            case R.id.customer_radio_status_nointerested:
                currentStatusId = 2;

                break;

            case R.id.customer_radio_status_ordered:
                currentStatusId = 3;

                break;

        }
    }

    private void loadReasonList() {
        reasonAdapter = new ReasonAdapter(Util.mListStatus);
        reasonAdapter.notifyDataSetChanged();
        rvReason.setAdapter(reasonAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvReason.setLayoutManager(linearLayoutManager);

    }

    private void loadCheckinsList(List<Checkin> mList) {
        checkinsAdapter = new CheckinsAdapter(mList);
        checkinsAdapter.notifyDataSetChanged();
        rvCheckins.setAdapter(checkinsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvCheckins.setLayoutManager(linearLayoutManager);

    }

    private Customer getCurrentCustomer(){
        Customer customer = new Customer();
        try {
            customer = currentCustomer;
            customer.put("name", edName.getText().toString().trim());
            customer.put("signBoard", edShopName.getText().toString().trim());
            customer.put("address", edAdress.getText().toString().trim());
            customer.put("phone", edPhone.getText().toString().trim());
            customer.put("street", edStreet.getText().toString().trim());
            customer.put("note", edNote.getText().toString().trim());
            customer.put("district", edDistrict.getText().toString().trim());
            customer.put("province", edCity.getText().toString().trim());
            customer.put("lat", currentCustomer.getDouble("lat"));
            customer.put("lng", currentCustomer.getDouble("lng"));
            customer.put("volumeEstimate", 10);
            customer.put("shopType", Constants.getShopInfo(null,edShopType.getText().toString()));
            customer.put("status.id", currentStatusId);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customer;
    }

    private String createParamCustomer(Customer customer){
        String param = String.format(Constants.CUSTOMER_CREATE_PARAM, customer.getInt("id") == 0? "" : "id="+ customer.getString("id") +"&",
                Util.encodeString(customer.getString("name")),//name
                Util.encodeString(customer.getString("signBoard")),//signBoard
                Util.encodeString(customer.getString("address")), //address
                Util.encodeString(customer.getString("phone")), //phone
                Util.encodeString(customer.getString("street")), //street
                Util.encodeString(customer.getString("note")), //note
                Util.encodeString(customer.getString("district")), //district
                Util.encodeString(customer.getString("province")), //province
                customer.getDouble("lat"), //lat
                customer.getDouble("lng"), //lng
                customer.getInt("volumeEstimate"), //province
                Util.encodeString(customer.getString("shopType")), //shopType
                customer.getInt("status.id") //currentStatusId
        );

        return param;
    }



}
