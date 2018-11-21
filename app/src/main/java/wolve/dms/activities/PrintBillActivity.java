package wolve.dms.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.PrintBillAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.callback.CallbackProcess;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.Customer;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;
import static wolve.dms.utils.Transaction.gotoImageChooser;

/**
 * Created by macos on 9/15/17.
 */

public class PrintBillActivity extends BaseActivity implements View.OnClickListener, BluetoothListFragment.OnDataPass  {
    private ImageView imgLogo, btnBack;
    private TextView tvCompany, tvAdress, tvHotline, tvWebsite, tvTitle, tvShopName, tvCustomerName,
                        tvCustomerPhone, tvCustomerAddress, tvDate, tvEmployee, tvSum,
                        tvOrderPhone, tvThanks, tvPrintSize, tvPrinterMainText, tvPrinterName, tvTotal;
    private CTextIcon tvListPrinter;
    private RecyclerView rvBills, rvDebts;
    private LinearLayout lnMain, lnBottom, lnSubmit;

    private Customer currentCustomer;
    private PrintBillAdapter adapterBill;
    private DebtAdapter adapterDebt;
    private List<JSONObject> listBills = new ArrayList<>();
    private List<JSONObject> listDebts = new ArrayList<>();
    private Uri imageChangeUri ;

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
        tvSum = findViewById(R.id.print_bill_sum);
        tvTotal = findViewById(R.id.print_bill_total);
//        tvRemain = findViewById(R.id.print_bill_remain);
        tvOrderPhone = findViewById(R.id.print_bill_orderphone);
        tvThanks = findViewById(R.id.print_bill_thanks);
        rvBills = findViewById(R.id.print_bill_bills);
        lnMain = findViewById(R.id.print_bill_mainlayout);
        tvPrinterMainText = findViewById(R.id.print_bill_bottom_maintext);
        tvPrinterName = findViewById(R.id.print_bill_bottom_secondtext);
        tvPrintSize = findViewById(R.id.print_bill_bottom_printersize);
        tvListPrinter = findViewById(R.id.print_bill_bottom_printerselect);
        lnBottom = findViewById(R.id.print_bill_bottom);
        rvDebts = findViewById(R.id.print_bill_rvdebt);
        lnSubmit = findViewById(R.id.print_bill_submit);

    }

    @Override
    public void initialData() {
        currentCustomer = new Customer( getIntent().getExtras().getString(Constants.CUSTOMER));
        listBills = DataUtil.array2ListObject(getIntent().getExtras().getString(Constants.BILLS));
        listDebts = getListDebt(currentCustomer.getJSONArray("bills"));

        tvCompany.setText(Constants.COMPANY_NAME);
        tvAdress.setText(Constants.COMPANY_ADDRESS);
        tvHotline.setText(Constants.COMPANY_HOTLINE);
        tvWebsite.setText(Constants.COMPANY_WEBSITE);
        tvOrderPhone.setText(Constants.COMPANY_ORDERPHONE);
        tvThanks.setText(Constants.COMPANY_THANKS);

        tvShopName.setText       ("CH    : " + Constants.getShopInfo(currentCustomer.getString("shopType")  , null));
        tvCustomerName.setText   ("KH    : " + currentCustomer.getString("name"));
        String phone = currentCustomer.getString("phone").equals("")? "--" : Util.PhoneFormat(currentCustomer.getString("phone"));
        tvCustomerPhone.setText  ("SDT   : " + phone);
        tvCustomerAddress.setText("D.CHI : " + String.format("%s, %s", currentCustomer.getString("street"), currentCustomer.getString("district")));
        tvDate.setText           ("NGAY  : " + Util.CurrentMonthYearHour());
        tvEmployee.setText       ("N.VIEN: " + User.getFullName());

        createRVBill(listBills);
        createRVDebt(listDebts);

        tvSum.setText(Util.FormatMoney(adapterBill.getTotalMoney()));
        String totalMoney = "TONG:    " +"<b>" + Util.FormatMoney(adapterBill.getTotalMoney() + adapterDebt.getTotalMoney()) + "</b> ";
        tvTotal.setText(Html.fromHtml(totalMoney));


        if (!Util.getDeviceName().equals(Constants.currentEmulatorDevice)){
            registerBluetooth();
        }

        if (CustomSQL.getString(Constants.PRINTER_SIZE).equals("") || CustomSQL.getString(Constants.PRINTER_SIZE).equals(Constants.PRINTER_57)){
            setWidthDefault(Constants.PRINTER_57);
            tvPrintSize.setText(Constants.PRINTER_57);
        }else {
            setWidthDefault(Constants.PRINTER_80);
            tvPrintSize.setText(Constants.PRINTER_80);
        }

    }



    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        lnBottom.setOnClickListener(this);
        tvListPrinter.setOnClickListener(this);
        tvPrintSize.setOnClickListener(this);
        lnSubmit.setOnClickListener(this);
        imgLogo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;

            case R.id.print_bill_bottom:


                break;

            case R.id.print_bill_bottom_printerselect:
                BluetoothListFragment fragment = new BluetoothListFragment();
                showFragmentDialog(fragment );

                break;

            case R.id.print_bill_bottom_printersize:
                choicePrinterSize();
                break;

            case R.id.print_bill_submit:
                CustomCenterDialog.showDialogPayment(listBills, listDebts, new CallbackPayBill() {
                    @Override
                    public void OnRespone(Double total, Double pay) {
                        if (isPrinterConnected()){
                            doPrint();
                        }else {
                            CustomCenterDialog.alertWithCancelButton(null, "Chưa kết nối máy in. Bạn muốn tiếp tục thanh toán không xuất hóa đơn", "Tiếp tục","hủy", new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                postBilltoServer(adapterBill.getTotalMoney());

                                }
                            });
                        }
                    }
                });
//                doPrint();
                break;

            case R.id.print_bill_logo:
                imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
                gotoImageChooser();
                break;
        }
    }


    private void choicePrinterSize(){
        CustomBottomDialog.choiceTwoOption(getString(R.string.icon_print), "Khổ giấy 57mm",
                getString(R.string.icon_print), "Khổ giấy 80mm", new CustomBottomDialog.TwoMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        tvPrintSize.setText("57mm");
                        CustomSQL.setString(Constants.PRINTER_SIZE, Constants.PRINTER_57);
                        setWidthDefault(Constants.PRINTER_57);
                    }

                    @Override
                    public void Method2(Boolean two) {
                        tvPrintSize.setText("80mm");
                        CustomSQL.setString(Constants.PRINTER_SIZE, Constants.PRINTER_80);
                        setWidthDefault(Constants.PRINTER_80);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setWidthDefault(String width){
        int size = width.equals(Constants.PRINTER_57) ? 200: 300;
        ViewGroup.LayoutParams param1 = lnMain.getLayoutParams();
        param1.width = (int) Util.convertDp2Px(size);
        param1.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        lnMain.setLayoutParams(param1);
    }

    private void createRVBill(final List<JSONObject> list){
        adapterBill = new PrintBillAdapter(tvPrintSize.getText().toString().equals(Constants.PRINTER_80)? 80:57 , list) ;
        Util.createLinearRV(rvBills, adapterBill);

    }

    private void createRVDebt(final List<JSONObject> list){
        adapterDebt = new DebtAdapter( list) ;
        Util.createLinearRV(rvDebts, adapterDebt);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            try {
                Set<BluetoothDevice> btDeviceList = mBluetoothAdapter.getBondedDevices();
                if (btDeviceList.size() > 0) {
                    for (final BluetoothDevice device : btDeviceList) {
                        Log.e("printer", device.getAddress() +"\n" + device.getName() + "\n" +device.getBluetoothClass() +"\n" + device.getBondState() +"\n" + device.getType() +"\n" + device.getUuids());

                        if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
                            connectBluetoothDevice(device, new CallbackProcess() {
                                @Override
                                public void onStart() {
                                    tvPrinterName.setText(Constants.CONNECTING_PRINTER);
                                    lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));

                                }

                                @Override
                                public void onError() {
                                    tvPrinterName.setText("Chưa kết nối được máy in");
                                    Util.showToast(Constants.CONNECTED_PRINTER_ERROR);
                                    lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
                                }

                                @Override
                                public void onSuccess(String name) {
                                    tvPrinterName.setText(String.format(Constants.CONNECTED_PRINTER, device.getName()));
                                    lnBottom.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                }
                            });
                        }

                    }
                }
            } catch (Exception ex) {
            }
            mBluetoothAdapter.startDiscovery();

        }
    }

    @Override
    public void dataReturnFromFragment(String data1, String data2) {
        switch (data1){
            case Constants.ONSTART:
                tvPrinterName.setText(Constants.CONNECTING_PRINTER);
                break;

            case Constants.ONFAIL:
                tvPrinterName.setText("Chưa kết nối được máy in");
                break;

            case Constants.ONSUCCESS:
                tvPrinterName.setText(String.format(Constants.CONNECTED_PRINTER, data2));
                break;
        }
    }

    private List<JSONObject> getListDebt(JSONArray array){
        List<JSONObject> listResult = new ArrayList<>();
        for (int i=0; i<array.length(); i++){
            try {
                JSONObject object = array.getJSONObject(i);
                if (object.getDouble("debt") > 0){
                    listResult.add(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return listResult;
    }

    private Boolean isPrinterConnected(){
        if (btsocket != null && btsocket.isConnected())
            return true;

        return false;

    }

    private void doPrint(){
            Util.getInstance().showLoading("Đang in...");
            final Double total  = adapterBill.getTotalMoney() + adapterDebt.getTotalMoney();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                CustomerConnect.printBillCustom(outputStream, currentCustomer,listBills, listDebts, total, total, new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        Util.getInstance().stopLoading(true);
                        CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc tiếp tục thanh toán", "TIẾP TỤC", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
                            @Override
                            public void Submit(Boolean boolSubmit) {
//                                    postBills(params.toString());

                            }

                            @Override
                            public void Cancel(Boolean boolCancel) {
                                doPrint();
                            }

                        });
                    }
                });

                }
            }); thread.start();

    }

    private void postBilltoServer(final Double total) {
        final String params = DataUtil.createPostBillParam(currentCustomer.getInt("id"),total, 0.0, listBills, "");

        CustomerConnect.PostBill(params, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                try {
                    postPayToServer(createPayParam(total, result.getInt("id") , ""));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {

            }
        }, false);

    }

    private void postPayToServer(String param){
        CustomerConnect.PostPay(param, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                String s = result.toString();

            }

            @Override
            public void onError(String error) {

            }//
        }, true);
    }

    private String createPayParam(Double paid, int billId, String note){
        String customerId = currentCustomer.getString("id");
        return String.format(Api_link.PAY_PARAM, customerId, String.valueOf(Math.round(paid)), billId, note.equals("")?"" :"&note="+note );
    }

}
