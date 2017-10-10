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
import wolve.dms.adapter.ReasonAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CInputForm;
import wolve.dms.controls.CTextView;
import wolve.dms.controls.WorkaroundMapFragment;
import wolve.dms.models.Bill;
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
    private RecyclerView rvReason, rvCheckins;
    private ScrollView scParent;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout lnBottomSheet;
    private CoordinatorLayout coParent;
    private RelativeLayout rlHeader;


    private ReasonAdapter reasonAdapter;
    private CustomerCheckinsAdapter customerCheckinsAdapter;
    private CustomerBillsAdapter customerBillsAdapter;
    private CustomerViewpagerAdapter viewpagerAdapter;
    private Customer currentCustomer;
    private List<Checkin> listCheckins = new ArrayList<>();
    private List<Bill> listBills = new ArrayList<>();
    private List<RecyclerView.Adapter> listAdapter = new ArrayList<>();
    private int currentStatusId = 1;
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
        rvReason = (RecyclerView) findViewById(R.id.add_customer_rvreason);
        rvCheckins = (RecyclerView) findViewById(R.id.add_customer_rvcheckins);
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

        tabLayout.setupWithViewPager(viewPager);
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
                }

                listAdapter.add(0, new CustomerCheckinsAdapter(listCheckins));
                listAdapter.add(1, new CustomerBillsAdapter(listBills));
                setupViewPager(listAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (currentCustomer.getInt("id") !=0){
            tvTrash.setVisibility(User.getCurrentUser().getString("role").equals("MANAGER") ? View.VISIBLE : View.GONE);
            btnEditLocation.setVisibility(View.VISIBLE);
            btnEditLocation.setText(MAP_EDIT);
        }else {
            tvTrash.setVisibility(View.GONE);
            btnEditLocation.setVisibility(View.GONE);
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

        viewpagerAdapter = new CustomerViewpagerAdapter(listadapter, listTitle);
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
                submitCustomer();

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
            returnPreviousScreen(currentCustomer);
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
                        if (currentStatusId != 3){
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
                listAdapter.add(1, new CustomerBillsAdapter(listBills));
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

}
