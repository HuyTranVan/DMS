package wolve.dms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

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
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ShopCartActivity extends BaseActivity implements  View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    public GoogleMap mMap;
    public SupportMapFragment mapFragment;
    private ImageView btnBack;
    private CTextView tvTrash;
    private Button btnSubmit, btnCurLocation;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_shopcart;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void initializeView() {
        btnSubmit = (Button) findViewById(R.id.add_customer_submit);
        btnBack = (ImageView) findViewById(R.id.icon_back);



    }


    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvTrash.setOnClickListener(this);
        rgStatus.setOnCheckedChangeListener(this);
        btnCurLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                returnPreviousScreen(currentCustomer);

                break;

            case R.id.add_customer_submit:

                break;

            case R.id.icon_more:

                break;

            case R.id.add_customer_currentlocation:

                break;
        }
    }

    private void returnPreviousScreen(Customer customer){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.CUSTOMER, customer.CustomertoString());
        setResult(Constants.RESULT_CUSTOMER_ACTIVITY,returnIntent);
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

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






}
