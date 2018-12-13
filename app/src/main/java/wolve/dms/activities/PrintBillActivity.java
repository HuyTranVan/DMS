package wolve.dms.activities;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.PrintBillAdapter;
import wolve.dms.adapter.PrintOldBillAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackList;
import wolve.dms.callback.CallbackProcess;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.printerdriver.BluetoothPrintBitmap;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.User;
import wolve.dms.utils.BitmapView;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;
import static wolve.dms.utils.Transaction.gotoImageChooser;

/**
 * Created by macos on 9/15/17.
 */

public class PrintBillActivity extends BaseActivity implements View.OnClickListener, BluetoothListFragment.OnDataPass  {
    private ImageView imgLogo, btnBack, imgOrderPhone;
    private TextView tvCompany, tvAdress, tvHotline, tvWebsite, tvTitle, tvShopName, tvCustomerName, tvCustomerAddress, tvDate, tvEmployee,
            tvSum, tvOrderPhone, tvThanks, tvPrintSize, tvPrinterMainText, tvPrinterName, tvTotal, tvPaid, tvRemain;
    private CTextIcon tvListPrinter;
    private RecyclerView rvBills, rvDebts;
    private LinearLayout lnMain, lnBottom, lnSubmit;
    private View line1, line2, line3;
    private NestedScrollView scContentParent;

    private Customer currentCustomer;
    private PrintBillAdapter adapterBill;
    private PrintOldBillAdapter adapterOldBill;
    private DebtAdapter adapterDebt;
    private List<JSONObject> listBills = new ArrayList<>();
    private List<JSONObject> listDebts = new ArrayList<>();
    private Uri imageChangeUri ;
    private Dialog dialogPayment;
    private Boolean rePrint;

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
        imgOrderPhone = findViewById(R.id.print_bill_qrphone);
        tvCompany = findViewById(R.id.print_bill_company);
        tvAdress = findViewById(R.id.print_bill_address);
        tvHotline = findViewById(R.id.print_bill_hotline);
        tvWebsite = findViewById(R.id.print_bill_website);
        tvTitle = findViewById(R.id.print_bill_title);
        tvShopName = findViewById(R.id.print_bill_shopname);
        tvCustomerName = findViewById(R.id.print_bill_customername);
//        tvCustomerPhone = findViewById(R.id.print_bill_customerphone);
        tvCustomerAddress = findViewById(R.id.print_bill_customeraddress);
        tvDate = findViewById(R.id.print_bill_date);
        tvEmployee = findViewById(R.id.print_bill_employee);
        tvSum = findViewById(R.id.print_bill_sum);
        tvTotal = findViewById(R.id.print_bill_total);
        tvPaid = findViewById(R.id.print_bill_paid);
        tvRemain = findViewById(R.id.print_bill_remain);
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
        line1 = findViewById(R.id.print_bill_line1);
        line2 = findViewById(R.id.print_bill_line2);
        line3 = findViewById(R.id.print_bill_line3);
        scContentParent = findViewById(R.id.print_bill_content_parent);

    }

    @Override
    public void initialData() {
        currentCustomer = new Customer( getIntent().getExtras().getString(Constants.CUSTOMER));
        rePrint = getIntent().getExtras().getBoolean(Constants.RE_PRINT);
        listBills = DataUtil.array2ListObject(getIntent().getExtras().getString(Constants.BILLS));
        listDebts = getListDebt(currentCustomer.getJSONArray("bills"));

        if (rePrint){
            tvTitle.setText("HÓA ĐƠN (in lại)");
            line1.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
            tvSum.setVisibility(View.GONE);
            tvPrinterMainText.setText("IN LẠI HÓA ĐƠN");
            createOldRVBill(listBills);

            String debtMoney = "Tong no:    " +"<b>" + Util.FormatMoney(adapterOldBill.getDebtMoney() ) + "</b> ";
            tvTotal.setText(Html.fromHtml(debtMoney));

        }else {
            tvTitle.setText("HÓA ĐƠN");
            line1.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
            tvSum.setVisibility(View.VISIBLE);
            tvPrinterMainText.setText("IN HÓA ĐƠN & LƯU");
            createCurrentRVBill(listBills);
            createRVDebt(listDebts);

            tvSum.setText(Util.FormatMoney(adapterBill.getTotalMoney()));
            String totalMoney = Util.FormatMoney(adapterBill.getTotalMoney() + adapterDebt.getTotalMoney());
            tvTotal.setText(totalMoney);
            tvPaid.setText("0");
            tvRemain.setText(totalMoney);


        }

        tvCompany.setText(Constants.COMPANY_NAME);
        tvAdress.setText(Constants.COMPANY_ADDRESS);
        tvHotline.setText(Constants.COMPANY_HOTLINE);
        tvWebsite.setText(Constants.COMPANY_WEBSITE);
        tvOrderPhone.setText(Constants.COMPANY_ORDERPHONE);
        tvThanks.setText(Constants.COMPANY_THANKS);
        tvShopName.setText(String.format(": %s %s",Constants.getShopInfo(currentCustomer.getString("shopType"), null).toUpperCase() , currentCustomer.getString("signBoard").toUpperCase()));

        String phone = currentCustomer.getString("phone").equals("")? "--" : Util.PhoneFormat(currentCustomer.getString("phone"));
        tvCustomerName.setText(String.format(": %s - %s",currentCustomer.getString("name"), phone ));

        tvCustomerAddress.setText(": " + String.format("%s, %s", currentCustomer.getString("district"), currentCustomer.getString("province")));
        tvDate.setText           (": " + Util.CurrentMonthYearHour());
        tvEmployee.setText       (": " + User.getFullName());

        if (!Util.getDeviceName().equals(Constants.currentEmulatorDevice)){
            registerBluetooth();
        }

        if (CustomSQL.getString(Constants.PRINTER_SIZE).equals("") || CustomSQL.getString(Constants.PRINTER_SIZE).equals(Constants.PRINTER_57)){
            tvPrintSize.setText(Constants.PRINTER_57);
        }else {
            tvPrintSize.setText(Constants.PRINTER_80);
        }

        try {
            QRGEncoder qrgEncoder = new QRGEncoder(User.getPhone(), null, QRGContents.Type.PHONE, Util.convertDp2PxInt(100));
            imgOrderPhone.setImageBitmap(qrgEncoder.encodeAsBitmap());
        } catch (WriterException e) {
            e.printStackTrace();
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
                onBackPressed();
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
                if (rePrint){
                    doPrintOldBill();

                }else {
                    showDialogPayment();
                }

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
//                        setWidthDefault(Constants.PRINTER_57);

                    }

                    @Override
                    public void Method2(Boolean two) {
                        tvPrintSize.setText("80mm");
                        CustomSQL.setString(Constants.PRINTER_SIZE, Constants.PRINTER_80);
//                        setWidthDefault(Constants.PRINTER_80);

                    }
                });
    }

    private void showDialogPayment(){
        dialogPayment = CustomCenterDialog.showDialogPayment("NHẬP SỐ TIỀN KHÁCH TRẢ", adapterBill.getTotalMoney(listBills),0, listDebts,Util.valueMoney(tvPaid), new CallbackList() {
            @Override
            public void onResponse(final List result) {
                dialogPayment.dismiss();

                if (isPrinterConnected()){
                    doPrintCurrentBill(result);

                }else {
                    CustomCenterDialog.alertWithCancelButton(null, "Chưa kết nối máy in. Bạn muốn tiếp tục thanh toán không xuất hóa đơn", "Tiếp tục","hủy", new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean bool) {
                            postBilltoServer(result);

                        }
                    });
                }

            }

            @Override
            public void onError(String error) {

            }


        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Transaction.returnShopCartActivity(Constants.PRINT_BILL_ACTIVITY, "", Constants.RESULT_PRINTBILL_ACTIVITY);


    }

    private void createCurrentRVBill(final List<JSONObject> list){
        adapterBill = new PrintBillAdapter(tvPrintSize.getText().toString().equals(Constants.PRINTER_80)? 80:57 , list) ;
        Util.createLinearRV(rvBills, adapterBill);

    }

    private void createRVDebt(final List<JSONObject> list){
        adapterDebt = new DebtAdapter( list, false) ;
        Util.createLinearRV(rvDebts, adapterDebt);

    }

    private void createOldRVBill(final List<JSONObject> list){
        adapterOldBill = new PrintOldBillAdapter(tvPrintSize.getText().toString().equals(Constants.PRINTER_80)? 80:57 , list) ;
        Util.createLinearRV(rvBills, adapterOldBill);

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
        String billHaveReturn = checkBillReturn(array).toString();
        for (int i=0; i<array.length(); i++){
            try {
                JSONObject object = array.getJSONObject(i);
                if (object.getDouble("debt") > 0 && !billHaveReturn.contains(object.getString("id"))){
                    listResult.add(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return listResult;
    }

    private List<String> checkBillReturn(JSONArray array){
        List<String> listResult = new ArrayList<>();
        try {
            for (int i=0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                if (!object.getString("note").equals("") && object.getString("note").matches(Util.DETECT_NUMBER)){
                    listResult.add(object.getString("id"));
                    listResult.add(object.getString("note"));

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listResult;

    }

    private Boolean isPrinterConnected(){
        if (btsocket != null && btsocket.isConnected())
            return true;

        return false;

    }

    private void doPrintCurrentBill(final List<JSONObject> listPayments){
        Util.getInstance().showLoading("Đang in...");
        final Double total  = adapterBill.getTotalMoney() + adapterDebt.getTotalMoney();
        Double paid = getPaid(listPayments);
        int printSize = tvPrintSize.getText().toString().equals(Constants.PRINTER_80)? Constants.PRINTER_80_WIDTH: Constants.PRINTER_57_WIDTH;

        tvPaid.setText(Util.FormatMoney(paid));
        tvRemain.setText(Util.FormatMoney(total -paid));

        new BluetoothPrintBitmap(outputStream,
                BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), printSize),
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        Util.getInstance().stopLoading(true);

                        if (result){
                            CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc tiếp tục thanh toán", "TIẾP TỤC", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
                                @Override
                                public void Submit(Boolean boolSubmit) {
                                    postBilltoServer(listPayments);

                                }

                                @Override
                                public void Cancel(Boolean boolCancel) {
                                    doPrintCurrentBill(listPayments);
                                }

                            });
                        }else {
                            CustomCenterDialog.alertWithButton("LỖI", "Kết nối máy in thất bại. Vui lòng thực hiện kết nối lại", "ĐỒNG Ý", new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    tvPrinterName.setText("Chưa kết nối được máy in");
                                    lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
                                }
                            });
                        }
                    }
                }).execute();




//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                CustomerConnect.printBillCustom(tvPrintSize.getText().toString(), outputStream, currentCustomer,listBills, listDebts, total, getPaid(listPayments), new CallbackBoolean() {
//                    @Override
//                    public void onRespone(Boolean result) {
//                        Util.getInstance().stopLoading(true);
//                        if (result){
//                            CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc tiếp tục thanh toán", "TIẾP TỤC", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
//                                @Override
//                                public void Submit(Boolean boolSubmit) {
//                                    postBilltoServer(listPayments);
//
//                                }
//
//                                @Override
//                                public void Cancel(Boolean boolCancel) {
//                                    doPrintCurrentBill(listPayments);
//                                }
//
//                            });
//                        }else {
//                            CustomCenterDialog.alertWithButton("LỖI", "Kết nối máy in thất bại. Vui lòng thực hiện kết nối lại", "ĐỒNG Ý", new CallbackBoolean() {
//                                @Override
//                                public void onRespone(Boolean result) {
//                                    tvPrinterName.setText("Chưa kết nối được máy in");
//                                    lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
//                                }
//                            });
//                        }
//
//
//                    }
//                });
//
//                }
//            }); thread.start();

    }

    private void doPrintOldBill(){
        if (isPrinterConnected()){
            Util.getInstance().showLoading("Đang in...");
            //final Double total  = adapterBill.getTotalMoney() + adapterDebt.getTotalMoney();

            int printSize = tvPrintSize.getText().toString().equals(Constants.PRINTER_80)? Constants.PRINTER_80_WIDTH: Constants.PRINTER_57_WIDTH;

            new BluetoothPrintBitmap(outputStream,
                    BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), printSize),
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            Util.getInstance().stopLoading(true);

                            if (result){
                                CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc quay trở lại ", "TRỞ VỀ", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
                                    @Override
                                    public void Submit(Boolean boolSubmit) {
                                        Transaction.returnCustomerActivity(Constants.PRINT_BILL_ACTIVITY,"", Constants.RESULT_PRINTBILL_ACTIVITY);


                                    }

                                    @Override
                                    public void Cancel(Boolean boolCancel) {
                                        doPrintOldBill();
                                    }

                                });
                            }else {
                                CustomCenterDialog.alertWithButton("LỖI", "Kết nối máy in thất bại. Vui lòng thực hiện kết nối lại", "ĐỒNG Ý", new CallbackBoolean() {
                                    @Override
                                    public void onRespone(Boolean result) {
                                        tvPrinterName.setText("Chưa kết nối được máy in");
                                        lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
                                    }
                                });
                            }
                        }
                    }).execute();



//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    CustomerConnect.printOldBillCustom(tvPrintSize.getText().toString(), outputStream, currentCustomer,listBill, adapterOldBill.getDebtMoney(), new CallbackBoolean() {
//                        @Override
//                        public void onRespone(Boolean result) {
//                            Util.getInstance().stopLoading(true);
//                            if (result){
//                                CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc quay trở lại ", "TRỞ VỀ", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
//                                    @Override
//                                    public void Submit(Boolean boolSubmit) {
//                                        Transaction.returnCustomerActivity(Constants.PRINT_BILL_ACTIVITY,"", Constants.RESULT_PRINTBILL_ACTIVITY);
//                                    }
//
//                                    @Override
//                                    public void Cancel(Boolean boolCancel) {
//                                        doPrintOldBill(listBill);
//                                    }
//
//                                });
//                            }else {
//                                CustomCenterDialog.alertWithButton("LỖI", "Kết nối máy in thất bại. Vui lòng thực hiện kết nối lại", "ĐỒNG Ý", new CallbackBoolean() {
//                                    @Override
//                                    public void onRespone(Boolean result) {
//                                        tvPrinterName.setText("Chưa kết nối được máy in");
//                                        lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
//                                    }
//                                });
//                            }
//
//
//                        }
//                    });
//
//                }
//            }); thread.start();

        }else {
            CustomCenterDialog.alertWithCancelButton(null, "Chưa kết nối máy in. Vui lòng thực hiện kết nối lại với máy in", "Đồng ý","hủy", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean bool) {


                }
            });
        }




    }

    private void postBilltoServer(final List<JSONObject> listPayments) {
        final String params = DataUtil.createPostBillParam(currentCustomer.getInt("id"), adapterBill.getTotalMoney(), 0.0, listBills, "");

        CustomerConnect.PostBill(params, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {

                try {
                    if (listPayments.size()>0 ){
                        if (listPayments.get(0).getInt("billId") == 0){
                            listPayments.get(0).put("billId", result.getInt("id"));
                        }

                        postPayToServer(DataUtil.createListPaymentParam(currentCustomer.getInt("id"),listPayments), true);

                    }else {
                        Transaction.returnShopCartActivity(Constants.PRINT_BILL_ACTIVITY, Constants.RELOAD_DATA, Constants.RESULT_PRINTBILL_ACTIVITY);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
            }
        }, false);

    }

    private void postPayToServer(List<String> listParam, Boolean stopLoading){
        CustomerConnect.PostListPay(listParam, new CallbackList() {
            @Override
            public void onResponse(List result) {
                Transaction.returnShopCartActivity(Constants.PRINT_BILL_ACTIVITY, Constants.RELOAD_DATA, Constants.RESULT_PRINTBILL_ACTIVITY);



            }

            @Override
            public void onError(String error) {

            }//
        }, stopLoading);
    }

    private Double getPaid(List<JSONObject> listPayments){
        Double paid = 0.0;

        try {
            for (int i=0;i<listPayments.size();i++){
                paid+= listPayments.get(i).getDouble("paid");

            }
        } catch (JSONException e) {
            return paid;
        }
        return paid;
    }



}
