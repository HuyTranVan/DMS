package wolve.dms.activity;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import wolve.dms.adapter.CustomerBillsAdapter;
import wolve.dms.adapter.CustomerCheckinsAdapter;
import wolve.dms.adapter.CustomerViewpagerAdapter;
import wolve.dms.adapter.CartCheckinReasonAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackStatus;
import wolve.dms.controls.CInputForm;
import wolve.dms.controls.CTextView;
import wolve.dms.controls.WorkaroundMapFragment;
import wolve.dms.models.Bill;
import wolve.dms.models.Checkin;
import wolve.dms.models.Customer;
import wolve.dms.models.Status;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements OnMapReadyCallback,GoogleMap.OnCameraIdleListener ,  View.OnClickListener, RadioGroup.OnCheckedChangeListener,ViewTreeObserver.OnGlobalLayoutListener {
    public GoogleMap mMap;
    public SupportMapFragment mapFragment;
    private ImageView btnBack;
    private CTextView tvTrash;
    private Button btnSubmit, btnEditLocation;
    private FloatingActionButton btnShopCart, btnCurrentLocation;
    private TextView tvTitle;
    private CInputForm edName, edPhone , edAdress, edStreet, edDistrict, edCity, edNote , edShopType, edShopName;
    private RadioGroup rgStatus;
    private RadioButton rdInterested, rdNotInterested, rdOrdered;
    private ScrollView scParent;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout lnBottomSheet;
    private CoordinatorLayout coParent;
    private RelativeLayout rlHeader;


    private CartCheckinReasonAdapter cartCheckinReasonAdapter;
    private CustomerCheckinsAdapter customerCheckinsAdapter;
    private CustomerBillsAdapter customerBillsAdapter;
    private CustomerViewpagerAdapter viewpagerAdapter;
    private Customer currentCustomer;
    private List<Checkin> listCheckins = new ArrayList<>();
    private List<Bill> listBills = new ArrayList<>();
    private List<RecyclerView.Adapter> listAdapter = new ArrayList<>();
    private Status currentStatus= Util.mListStatus.get(0);
    private int currentPosition=0;
    private String firstName ="";
    private float bottomSheetHeight;
    private final String MAP_EDIT = "CHỈNH VỊ TRÍ";
    private final String MAP_SAVE = "LƯU VỊ TRÍ";


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
        coParent = (CoordinatorLayout) findViewById(R.id.customer_parent);
        rlHeader = (RelativeLayout) findViewById(R.id.customer_header);
        btnSubmit = (Button) findViewById(R.id.add_customer_submit);
        btnEditLocation = (Button) findViewById(R.id.add_customer_editlocation);
        btnCurrentLocation = (FloatingActionButton) findViewById(R.id.add_customer_currentlocation);
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
        scParent = (ScrollView) findViewById(R.id.customer_scrollview);
        viewPager = (ViewPager) findViewById(R.id.customer_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.customer_tabs);
        lnBottomSheet = (LinearLayout) findViewById(R.id.customer_bottom_sheet);

        //fix MapView in ScrollView
        mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_customer_map);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_customer_map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scParent.requestDisallowInterceptTouchEvent(true);
            }
        });

    }

    @Override
    public void initialData() {
        //loadReasonList();
        tabLayout.setupWithViewPager(viewPager);

        String bundle = getIntent().getExtras().getString(Constants.CUSTOMER);
        if (bundle != null){
            try {
                currentCustomer = new Customer(new JSONObject(bundle));
                firstName = currentCustomer.getString("name");

                edName.setText(currentCustomer.getString("name") == null? "" : currentCustomer.getString("name"));
                edPhone.setText(currentCustomer.getString("phone") == null? "" : currentCustomer.getString("phone"));
                edAdress.setText((currentCustomer.getString("address") == null? "" : currentCustomer.getString("address")));
                edStreet.setText(currentCustomer.getString("street") == null? "" : currentCustomer.getString("street"));
                edNote.setTextHint(currentCustomer.getString("note") == null? "Ghi chú" : "Ghi chú: " +currentCustomer.getString("note"));
                edDistrict.setText(currentCustomer.getString("district"));
                edCity.setText(currentCustomer.getString("province"));
                edShopName.setText(currentCustomer.getString("signBoard"));
                edShopType.setText(Constants.getShopInfo(currentCustomer.getString("shopType") , null));
                tvTitle.setText(edShopType.getText().toString() +" - " + currentCustomer.getString("signBoard") );

                if (currentCustomer.getString("status") != null){
                    currentStatus = new Status(currentCustomer.getJsonObject("status"));
                }

                switch (currentStatus.getInt("id")){
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

                if (currentCustomer.getString("checkIns") != null ){
                    JSONArray array = new JSONArray(currentCustomer.getString("checkIns"));
                    for (int i=0; i<array.length(); i++){
                        listCheckins.add(new Checkin(array.getJSONObject(i)));
                    }
                }

                if (currentCustomer.getString("bills") != null ){
                    JSONArray array = new JSONArray(currentCustomer.getString("bills"));
                    for (int i=0; i<array.length(); i++){
                        listBills.add(new Bill(array.getJSONObject(i)));
                    }
                    currentPosition = listBills.size();
                }

                listAdapter.add(0, new CustomerCheckinsAdapter(listCheckins));
                listAdapter.add(1, new CustomerBillsAdapter(listBills, new CallbackDeleteAdapter() {
                    @Override
                    public void onDelete(String data, int position) {
                        deleteBill(position);
                        viewpagerAdapter.notifyDataSetChanged();
                    }
                }));
                setupViewPager(listAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        btnEditLocation.setText(MAP_EDIT);
        if (currentCustomer.getInt("id") !=0){
            tvTrash.setVisibility(User.getCurrentUser().getString("role").equals("MANAGER") ? View.VISIBLE : View.GONE);
            btnSubmit.setText("CHECK_IN & CẬP NHẬT");
        }else {
            tvTrash.setVisibility(View.GONE);
            btnSubmit.setText("CẬP NHẬT");
        }

        if (edShopName.getText().toString().trim().equals("")){
            edShopName.setSelection();
        }else {
            edPhone.setSelection();
        }


        edShopType.setDropdown(true);
        edShopType.setDropdownList(Util.arrayToList(Constants.shopName));

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
        btnEditLocation.setOnClickListener(this);
        btnCurrentLocation.setOnClickListener(this);
        btnShopCart.setOnClickListener(this);
        coParent.getViewTreeObserver().addOnGlobalLayoutListener(this) ;
    }

    private void setupBottomSheet() {
        bottomSheetHeight = Util.convertDp2Px(57);
        mBottomSheetBehavior = BottomSheetBehavior.from(lnBottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) bottomSheetHeight);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            int lastState = BottomSheetBehavior.STATE_COLLAPSED;

            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                int windowHeight = Util.getWindowSize().heightPixels;

            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {


            }
        });
    }

    private void setupViewPager(List<RecyclerView.Adapter> listadapter){
        ArrayList<String> listTitle = new ArrayList<>();
        listTitle.add(0,"CHECK_IN");
        listTitle.add(1, "HÓA ĐƠN");

        viewpagerAdapter = new CustomerViewpagerAdapter(listadapter, listTitle, getTotalMoney(listBills));
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(2);

        for (int i=0; i<listTitle.size(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(this).inflate(R.layout.view_customer_badge, null);
            TextView tabTextTitle = (TextView) customView.findViewById(R.id.tabNotify);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);
            textTitle.setText(listTitle.get(i));

            if (listadapter.get(i).getItemCount() <=0){
                tabTextTitle.setVisibility(View.GONE);
            }else {
                tabTextTitle.setVisibility(View.VISIBLE);
                tabTextTitle.setText(String.valueOf(listadapter.get(i).getItemCount()));
            }

            tab.setCustomView(customView);
        }
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.add_customer_submit:
                if (currentCustomer.getInt("id") ==0){
                    submitCustomerCheckin(currentStatus);
                }else {
                    loadReasonList();
                }

                break;

            case R.id.icon_more:
                deleteCustomer();

                break;

            case R.id.add_customer_editlocation:
                btnLocationEvent();

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
                    try {
                        currentCustomer.put("id", responseCustomer.getInt("id"));
                        Transaction.gotoShopCartActivity(responseCustomer.CustomertoString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String error) {

                }
            }, true);

        }else {
            Transaction.gotoShopCartActivity(customer.CustomertoString());
        }

    }

    private Boolean checkInputField(){
        Boolean check = false;
        if (edName.getText().toString().trim().equals("")
                || edDistrict.getText().toString().trim().equals("")
                || edCity.getText().toString().trim().equals("")
                || edShopName.getText().toString().trim().equals("")
                || edShopType.getText().toString().trim().equals("")){
            CustomDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                @Override public void onRespone(Boolean result) {

                }
            });

            check = false;

        }else {
            check = true;
        }
        return check;
    }

    private void submitCustomer(){
        if (checkInputField()){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    final Customer responseCustomer = new Customer(result);
                    returnPreviousScreen(responseCustomer);

                }

                @Override
                public void onError(String error) {

                }
            }, true);
        }

    }

    private void submitCustomerCheckin(final Status status){
        if (checkInputField()){
            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    final Customer responseCustomer = new Customer(result);
                    submitCheckin(responseCustomer, status);

                }

                @Override
                public void onError(String error) {

                }
            }, false);
        }

    }

    private void submitCheckin(final Customer customer, Status status){
            String params = String.format(Api_link.SCHECKIN_CREATE_PARAM, customer.getInt("id"),
                    status.getInt("id"),
                    Util.encodeString(edNote.getText().toString().trim()),
                    User.getCurrentUserId()
            );

            CustomerConnect.PostCheckin(params, new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    returnPreviousScreen(customer);
                }

                @Override
                public void onError(String error) {

                }
            }, true);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

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
            backEvent();
        }
        return true;
    }

    private void backEvent(){
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else {
            returnPreviousScreen(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra(Constants.CUSTOMER_CART) != null && requestCode == Constants.RESULT_SHOPCART_ACTIVITY){
            try {
                Customer customer =new Customer(new JSONObject(data.getStringExtra(Constants.CUSTOMER_CART)));
                if (customer.getString("bills") != null ){
                    JSONArray array = new JSONArray(customer.getString("bills"));
                    if (array.length() >0){
                        if (currentStatus.getInt("id") != 3){
                            rdOrdered.setChecked(true);
                            CustomerConnect.CreateCustomer(createParamCustomer(getCurrentCustomer()), new CallbackJSONObject() {
                                @Override
                                public void onResponse(JSONObject result) {
                                }

                                @Override
                                public void onError(String error) {

                                }
                            }, true);
                        }

                    }
                    listBills = new ArrayList<>();
                    for (int i=0; i<array.length(); i++){
                        listBills.add(new Bill(array.getJSONObject(i)));
                    }

                }
                listAdapter.add(1, new CustomerBillsAdapter(listBills, new CallbackDeleteAdapter() {
                    @Override
                    public void onDelete(String data, int position) {
                        deleteBill(position);
                        viewpagerAdapter.notifyDataSetChanged();
                    }
                }));
                setupViewPager(listAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.customer_radio_status_interested:
                currentStatus = Util.mListStatus.get(0);

                break;

            case R.id.customer_radio_status_nointerested:
                currentStatus = Util.mListStatus.get(1);

                break;

            case R.id.customer_radio_status_ordered:
                currentStatus = Util.mListStatus.get(2);

                break;

        }
    }

    private void loadReasonList() {
        CustomDialog.showCheckinReason("CHECK_IN", Util.mListStatus, new CallbackStatus() {
            @Override
            public void Status(Status status) {
                if (status == null){
                    submitCustomer();
                }else {
                    submitCustomerCheckin(status );
                }
            }
        });

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
            customer.put("status.id", currentStatus.getInt("id"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return customer;
    }

    private String createParamCustomer(Customer customer){
        String param = String.format(Api_link.CUSTOMER_CREATE_PARAM, customer.getInt("id") == 0? "" : "id="+ customer.getString("id") +"&",
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

    @Override
    public void onGlobalLayout() {
        coParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        int height = coParent.getHeight() - rlHeader.getHeight();

        ViewGroup.LayoutParams params = lnBottomSheet.getLayoutParams();
        params.height = height;
        lnBottomSheet.requestLayout();

        setupBottomSheet();

    }

    private void btnLocationEvent(){
        switch (btnEditLocation.getText().toString().trim()){
            case MAP_EDIT:
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                btnEditLocation.setText(MAP_SAVE);
                btnEditLocation.setBackgroundResource(R.drawable.btn_confirm_blue);
                btnCurrentLocation.setVisibility(View.VISIBLE);

                break;

            case MAP_SAVE:
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                btnEditLocation.setText(MAP_EDIT);
                btnEditLocation.setBackgroundResource(R.drawable.btn_confirm_red);
                btnCurrentLocation.setVisibility(View.GONE);
                break;
        }
    }

    private Double getTotalMoney(List<Bill> list){
        Double money =0.0;
        for (int i=0; i<list.size(); i++){
            money += list.get(i).getDouble("total");
        }
        return money;
    }

    private void deleteBill(int count){
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        TextView tabTextTitle = (TextView) tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tabNotify);
        tabTextTitle.setText(String.valueOf(count));

        if (count <=0){
            tabTextTitle.setVisibility(View.GONE);
        }else {
            tabTextTitle.setVisibility(View.VISIBLE);
            tabTextTitle.setText(String.valueOf(count));
        }

    }



}
