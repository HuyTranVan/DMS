package wolve.dms.activity;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CheckinsAdapter;
import wolve.dms.adapter.ReasonAdapter;
import wolve.dms.models.Checkin;
import wolve.dms.models.Customer;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ShopCartActivity extends BaseActivity implements  View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ImageView btnBack;
    private Button btnSubmit;
    private TextView tvTitle;
    private RadioGroup rgStatus;
    private RadioButton rdInterested, rdNotInterested, rdOrdered;
    private RecyclerView rvShop;
    private FloatingActionButton btnAdd;


    private ReasonAdapter reasonAdapter;
    private CheckinsAdapter checkinsAdapter ;
    private Customer currentCustomer;
    private List<Checkin> listCheckins = new ArrayList<>();
    private int currentStatusId = 1;
    private String firstName ="";

    @Override
    public int getResourceLayout() {
        return R.layout.activity_shopcart;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        btnSubmit = (Button) findViewById(R.id.cart_submit);
        btnBack = (ImageView) findViewById(R.id.icon_back);
        tvTitle = (TextView) findViewById(R.id.cart_title);
        btnAdd = (FloatingActionButton) findViewById(R.id.cart_add_product);


    }

    @Override
    public void initialData() {
        String bundle = getIntent().getExtras().getString(Constants.CUSTOMER_CART);
        if (bundle != null){
            try {
                currentCustomer = new Customer(new JSONObject(bundle));
                //tvTitle.setText(edShopType.getText().toString() +" - " + currentCustomer.getString("signBoard") );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            returnPreviousScreen(currentCustomer);
        }
        return true;
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

            case R.id.cart_add_product:
                Util.getInstance().showCustomDialog(R.layout.view_dialog_select_product);
                break;


        }
    }

    private void returnPreviousScreen(Customer customer){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.CUSTOMER_CART, customer.CustomertoString());
        setResult(Constants.RESULT_SHOPCART_ACTIVITY,returnIntent);
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
