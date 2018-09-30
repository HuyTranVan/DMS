package wolve.dms.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CartProductsAdapter;
import wolve.dms.adapter.PrintBillAdapter;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.Product;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/15/17.
 */

public class PrintBillActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ImageView imgLogo, btnBack;
    private TextView tvCompany, tvAdress, tvHotline, tvWebsite, tvTitle, tvShopName, tvCustomerName,
                        tvCustomerPhone, tvCustomerAddress, tvDate, tvEmployee, tvTotal, tvPaid, tvRemain, tvOrderPhone, tvThanks;
    private RecyclerView rvBills;
    private LinearLayout lnMain;
    private RadioGroup rdPrinter;

    private Customer currentCustomer;
    private PrintBillAdapter adapter;
    private List<JSONObject> listBill  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_print_bill;
    }

    @Override
    public int setIdContainer() {
        return R.id.print_bill_parent;
    }

    @Override
    public void findViewById() {
        imgLogo = findViewById(R.id.print_bill_logo);
        btnBack = findViewById(R.id.icon_back);
        tvCompany = findViewById(R.id.print_bill_company);
        tvAdress = findViewById(R.id.print_bill_address);
        tvHotline = findViewById(R.id.print_bill_hotline);
        tvWebsite = findViewById(R.id.print_bill_website);
        tvTitle = findViewById(R.id.print_bill_title);
        tvShopName = findViewById(R.id.print_bill_shopname);
        tvCustomerName = findViewById(R.id.print_bill_customername);
        tvCustomerPhone = findViewById(R.id.print_bill_customerphone);
        tvCustomerAddress = findViewById(R.id.print_bill_customeraddress);
        tvDate = findViewById(R.id.print_bill_date);
        tvEmployee = findViewById(R.id.print_bill_employee);
        tvTotal = findViewById(R.id.print_bill_total);
        tvPaid = findViewById(R.id.print_bill_paid);
        tvRemain = findViewById(R.id.print_bill_remain);
        tvOrderPhone = findViewById(R.id.print_bill_orderphone);
        tvThanks = findViewById(R.id.print_bill_thanks);
        rvBills = findViewById(R.id.print_bill_bills);
        lnMain = findViewById(R.id.print_bill_mainlayout);
        rdPrinter = findViewById(R.id.printer_all);

    }

    @Override
    public void initialData() {
        setWidthDefault(200);
        Customer currentCustomer = new Customer( getIntent().getExtras().getString(Constants.CUSTOMER));
        String bills = getIntent().getExtras().getString(Constants.BILLS);
        listBill = DataUtil.array2ListObject(bills);

        tvCompany.setText(Constants.COMPANY_NAME);
        tvAdress.setText(Constants.COMPANY_ADDRESS);
        tvHotline.setText(Constants.COMPANY_HOTLINE);
        tvWebsite.setText(Constants.COMPANY_WEBSITE);
        tvOrderPhone.setText(Constants.COMPANY_ORDERPHONE);
        tvThanks.setText(Constants.COMPANY_THANKS);

        tvShopName.setText("CH    : " + Constants.getShopInfo(currentCustomer.getString("shopType")  , null));
        tvCustomerName.setText("KH    : " + currentCustomer.getString("name"));
        String phone = currentCustomer.getString("phone").equals("")? "--" : Util.PhoneFormat(currentCustomer.getString("phone"));
        tvCustomerPhone.setText("SDT   : " + phone);
        tvCustomerAddress.setText("D.CHI : " + String.format("%s, %s", currentCustomer.getString("street"), currentCustomer.getString("district")));
        tvDate.setText("NGAY  : " + Util.CurrentMonthYearHour());
        tvEmployee.setText("N.VIEN: " + User.getFullName());

        createRVBill(listBill);

        String totalMoney = "TONG:  " +"<b>" + Util.FormatMoney(adapter.getTotalMoney()) + "</b> ";
        tvTotal.setText(Html.fromHtml(totalMoney));



    }



    @Override
    public void addEvent() {
        rdPrinter.setOnCheckedChangeListener(this);
        btnBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
        }
    }

    private void setWidthDefault(int width){
        ViewGroup.LayoutParams param1 = lnMain.getLayoutParams();
        param1.width = (int) Util.convertDp2Px(width);
        param1.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        lnMain.setLayoutParams(param1);
    }

    private void createRVBill(final List<JSONObject> list){
        adapter = new PrintBillAdapter(rdPrinter.getCheckedRadioButtonId() == R.id.printer_57 ? 57 : 80 , list) ;
        rvBills.setAdapter(adapter);
        rvBills.setHasFixedSize(true);
        rvBills.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvBills.setLayoutManager(layoutManager);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.printer_57:
                setWidthDefault(200);

                break;

            case R.id.printer_80:
                setWidthDefault(300);
                break;

        }
    }
}
