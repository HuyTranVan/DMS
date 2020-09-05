package wolve.dms.activities;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import wolve.dms.R;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.PrintBillAdapter;
import wolve.dms.adapter.PrintOldBillAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.CustomGetPostListMethod;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackProcess;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.libraries.BitmapView;
import wolve.dms.libraries.printerdriver.BluetoothPrintBitmap;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;

/**
 * Created by macos on 9/15/17.
 */

public class PrintBillActivity extends BaseActivity implements View.OnClickListener, BluetoothListFragment.OnDataPass {
    private ImageView imgLogo, btnBack, imgOrderPhone;
    private TextView tvCompany, tvAdress, tvHotline, tvWebsite, tvTitle, tvShopName, tvCustomerName, tvCustomerAddress, tvDate, tvEmployee,
            tvSumCurrentBill, tvOrderPhone, tvThanks, tvPrintSize, tvPrinterMainText, tvPrinterName, tvTotal, tvPaid, tvRemain, tvTotalTitle,
            tvEmployeeSign, tvDeliver, tvDeliverTitle, tvListPrinter, tvShare;
    private RecyclerView rvBills, rvDebts;
    private LinearLayout lnMain, lnBottom, lnSubmit, lnTotalGroup, lnPaidGroup, lnRemainGroup, lnSignature;
    private View line1, line2, line3, line4;
    private NestedScrollView scContentParent;

    private BaseModel currentCustomer, currentBill;
    private PrintBillAdapter adapterBill;
    private PrintOldBillAdapter adapterOldBill;
    private DebtAdapter adapterDebt;
    private List<BaseModel> listDebts = new ArrayList<>();
    private boolean rePrint;
    ;
    private Dialog dialogPayment;
    private String orderPhone;
    private Uri currentImagePath;

    private BluetoothListFragment bluFragment = null;
    protected BluetoothAdapter mBluetoothAdapter = null;
    protected BluetoothSocket btsocket;
    protected OutputStream outputStream;
    protected List<BluetoothDevice> listDevice = new ArrayList<>();

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
        tvDeliverTitle = findViewById(R.id.print_bill_deliver_title);
        tvDeliver = findViewById(R.id.print_bill_deliver);
        tvCustomerAddress = findViewById(R.id.print_bill_customeraddress);
        tvDate = findViewById(R.id.print_bill_date);
        tvEmployee = findViewById(R.id.print_bill_employee);
        tvSumCurrentBill = findViewById(R.id.print_bill_sum);
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
        tvShare = findViewById(R.id.print_bill_bottom_share);
        tvEmployeeSign = findViewById(R.id.print_bill_employee_sign);
        lnBottom = findViewById(R.id.print_bill_bottom);
        rvDebts = findViewById(R.id.print_bill_rvdebt);
        lnSubmit = findViewById(R.id.print_bill_submit);
        line1 = findViewById(R.id.print_bill_line1);
        line2 = findViewById(R.id.print_bill_line2);
        line3 = findViewById(R.id.print_bill_line3);
        line4 = findViewById(R.id.print_bill_line4);
        scContentParent = findViewById(R.id.print_bill_content_parent);
        tvTotalTitle = findViewById(R.id.print_bill_total_title);
        lnTotalGroup = findViewById(R.id.print_bill_total_group);
        lnPaidGroup = findViewById(R.id.print_bill_paid_group);
        lnRemainGroup = findViewById(R.id.print_bill_remain_group);
        lnSignature = findViewById(R.id.print_bill_signature_group);


    }

    @Override
    public void initialData() {
        currentCustomer = new BaseModel(CustomSQL.getString(Constants.CUSTOMER));
        currentBill = new BaseModel(getIntent().getExtras().getString(Constants.BILL));
        listDebts = new ArrayList<>(DataUtil.array2ListObject(currentCustomer.getString(Constants.DEBTS)));

        rePrint = getIntent().getExtras().getBoolean(Constants.RE_PRINT);
        if (rePrint) {
            orderPhone = User.getPhone();
            tvTitle.setText("CÔNG NỢ ");
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
            tvSumCurrentBill.setVisibility(View.GONE);
            tvPrinterMainText.setText("IN LẠI HÓA ĐƠN");
            tvTotalTitle.setText("Tổng nợ:");
            lnPaidGroup.setVisibility(View.GONE);
            lnRemainGroup.setVisibility(View.GONE);
            tvDeliverTitle.setVisibility(View.GONE);
            tvDeliver.setVisibility(View.GONE);
            tvEmployee.setText(": " + User.getFullName());

            createOldRVBill(listDebts);
            tvTotal.setText(Util.FormatMoney(adapterOldBill.getDebtMoney()));

        } else {
            orderPhone = currentBill.getBaseModel("user").getString("phone");
            tvTitle.setText("HÓA ĐƠN");
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
            tvSumCurrentBill.setVisibility(View.VISIBLE);
            tvPrinterMainText.setText("IN HÓA ĐƠN & LƯU");
            lnPaidGroup.setVisibility(View.VISIBLE);
            lnRemainGroup.setVisibility(View.VISIBLE);
            tvDeliverTitle.setVisibility(View.VISIBLE);
            tvDeliver.setVisibility(View.VISIBLE);
            tvDeliver.setText(": " + User.getFullName());
            tvEmployee.setText(": " + currentBill.getBaseModel("user").getString("displayName"));

            createCurrentRVBill(currentBill);
            createRVDebt(listDebts);
            tvSumCurrentBill.setText(Util.FormatMoney(adapterBill.getTotalMoney()));
            String totalMoney = Util.FormatMoney(adapterBill.getTotalMoney() + adapterDebt.getTotalMoney());
            tvTotalTitle.setText("Tổng:");
            tvTotal.setText(totalMoney);

            tvPaid.setText("0");
            tvRemain.setText(totalMoney);

        }

        BaseModel distributor = Distributor.getObject();
        tvCompany.setText(distributor.getString("company"));
        tvAdress.setText(distributor.getString("address"));
        tvHotline.setText(String.format("Hotline: %s", Util.FormatPhone(distributor.getString("phone"))));
        tvWebsite.setText(distributor.getString("website"));
        if (!Util.checkImageNull(distributor.getString("image"))) {
            Glide.with(this).load(distributor.getString("image")).centerCrop().into(imgLogo);

        }


        tvOrderPhone.setText(String.format("Đặt hàng: %s", Util.FormatPhone(orderPhone)));
        try {
            QRGEncoder qrgEncoder = new QRGEncoder(orderPhone, null, QRGContents.Type.PHONE, Util.convertDp2PxInt(100));
            imgOrderPhone.setImageBitmap(qrgEncoder.encodeAsBitmap());
        } catch (WriterException e) {
            e.printStackTrace();
        }

        tvThanks.setText(distributor.getString("thanks"));
        tvShopName.setText(String.format(": %s %s", Constants.shopName[currentCustomer.getInt("shopType")].toUpperCase(), currentCustomer.getString("signBoard").toUpperCase()));

        String phone = currentCustomer.getString("phone").equals("") ? "--" : Util.FormatPhone(currentCustomer.getString("phone"));
        tvCustomerName.setText(String.format(": %s - %s", currentCustomer.getString("name"), phone));

        tvCustomerAddress.setText(": " + String.format("%s, %s", currentCustomer.getString("district"), currentCustomer.getString("province")));
        tvDate.setText(": " + Util.CurrentMonthYearHour());

        if (!Util.getDeviceName().equals(Constants.currentEmulatorDevice)) {
            registerBluetooth();

        }

        if (CustomSQL.getString(Constants.PRINTER_SIZE).equals("") || CustomSQL.getString(Constants.PRINTER_SIZE).equals(Constants.PRINTER_80)) {
            tvPrintSize.setText(Constants.PRINTER_80);
        } else {
            tvPrintSize.setText(Constants.PRINTER_57);
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
        tvShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.print_bill_bottom:


                break;

            case R.id.print_bill_bottom_printerselect:
                bluFragment = new BluetoothListFragment();
                showFragmentDialog(bluFragment);

                break;

            case R.id.print_bill_bottom_printersize:
                choicePrinterSize();
                break;

            case R.id.print_bill_submit:
                if (rePrint) {
                    doPrintOldBill();

                } else {
                    showDialogPayment(null);
                }

                break;

            case R.id.print_bill_bottom_share:
                CustomCenterDialog.alertWithCancelButton2("Chia sẻ",
                        "Gửi hình chụp hóa đơn thông qua ZALO",
                        "Tiếp tục",
                        "Hủy",
                        new CustomCenterDialog.ButtonCallback() {
                            @Override
                            public void Submit(Boolean boolSubmit) {
                                if (!rePrint){
                                    showDialogPayment(new CallbackBoolean() {
                                        @Override
                                        public void onRespone(Boolean result) {
                                            if (result){
//                                                currentImagePath = Uri.parse(Util.storeImage(BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), 512) ,
//                                                        "SHARE",
//                                                        "SHARE",
//                                                        true));
                                                currentImagePath = Util.storeImage(BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), 512) ,
                                                        "SHARE",
                                                        "SHARE",
                                                        true);

                                                Transaction.shareImageViaZalo(currentImagePath, currentCustomer);
                                            }
                                        }
                                    });

                                }else {
                                    currentImagePath = Util.storeImage(BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), 512) ,
                                            "SHARE",
                                            "SHARE",
                                            true);

                                    Transaction.shareImageViaZalo(currentImagePath, currentCustomer);

                                }



                            }

                            @Override
                            public void Cancel(Boolean boolCancel) {

                            }
                        });


                break;
        }
    }

    private void choicePrinterSize() {
        CustomBottomDialog.choiceTwoOption(getString(R.string.icon_print), "Khổ giấy 57mm",
                getString(R.string.icon_print), "Khổ giấy 80mm", new CustomBottomDialog.TwoMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        tvPrintSize.setText("57mm");
                        CustomSQL.setString(Constants.PRINTER_SIZE, Constants.PRINTER_57);

                    }

                    @Override
                    public void Method2(Boolean two) {
                        tvPrintSize.setText("80mm");
                        CustomSQL.setString(Constants.PRINTER_SIZE, Constants.PRINTER_80);

                    }
                });
    }

    private List<BaseModel> getAllDebt() {
        List<BaseModel> list = new ArrayList<>();
        BaseModel currentdebt = new BaseModel();
        currentdebt.put("id", 0);
        currentdebt.put("debt", currentBill.getDouble("total"));
        currentdebt.put("user_id", currentBill.getBaseModel("user").getInt("id"));

        list.add(0, currentdebt);


        DataUtil.sortbyStringKey("createAt", listDebts, true);
        for (int i = 0; i < listDebts.size(); i++) {
            list.add(listDebts.get(i));
        }

        return list;
    }

    private void showDialogPayment(CallbackBoolean isShareZalo) {
        dialogPayment = CustomCenterDialog.showDialogPayment("NHẬP SỐ TIỀN KHÁCH TRẢ",
                getAllDebt(),
                0.0,
                true,
                new CallbackListCustom() {
                    @Override
                    public void onResponse(final List result) {
                        dialogPayment.dismiss();

                        final Double total = adapterBill.getTotalMoney() + adapterDebt.getTotalMoney();
                        Double paid = DataUtil.sumValueFromList(result, "paid");
                        Double remain = total - paid;

                        tvPaid.setText(Util.FormatMoney(paid));
                        tvRemain.setText(Util.FormatMoney(remain));

                        if (remain > 0) {
                            line4.setVisibility(View.GONE);
                            lnSignature.setVisibility(View.VISIBLE);
                            tvEmployeeSign.setText(String.format("NV: %s", User.getFullName()));
                        } else {
                            line4.setVisibility(View.VISIBLE);
                            lnSignature.setVisibility(View.GONE);
                        }

                        if (isShareZalo != null){
                            isShareZalo.onRespone(true);

                        }else {
                            Util.getInstance().showLoading("Đang kiểm tra...");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Util.getInstance().stopLoading(true);
                                    if (isPrinterConnected()) {
                                        doPrintCurrentBill(result);

                                    } else {
                                        CustomCenterDialog.alertWithCancelButton(null, "Chưa kết nối máy in. Bạn muốn tiếp tục thanh toán không xuất hóa đơn", "Tiếp tục", "hủy", new CallbackBoolean() {
                                            @Override
                                            public void onRespone(Boolean bool) {
                                                if (bool) {
                                                    postBilltoServer(result, "");

                                                }


                                            }
                                        });
                                    }


                                }
                            }, 500);

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
        Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY, new BaseModel(), Constants.RESULT_PRINTBILL_ACTIVITY);

    }

    private void createCurrentRVBill(BaseModel bill) {
        adapterBill = new PrintBillAdapter(tvPrintSize.getText().toString().equals(Constants.PRINTER_80) ? 80 : 57, DataUtil.array2ListObject(bill.getString(Constants.BILL_DETAIL)));
        Util.createLinearRV(rvBills, adapterBill);

    }

    private void createRVDebt(final List<BaseModel> list) {
        DataUtil.sortbyStringKey("createAt", list, true);
        adapterDebt = new DebtAdapter(list, true, false);
        Util.createLinearRV(rvDebts, adapterDebt);

    }

    private void createOldRVBill(final List<BaseModel> list) {
        adapterOldBill = new PrintOldBillAdapter(tvPrintSize.getText().toString().equals(Constants.PRINTER_80) ? 80 : 57, list);
        Util.createLinearRV(rvBills, adapterOldBill);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            mBluetoothAdapter.startDiscovery();


        }
    }

    @Override
    public void dataReturnFromFragment(String data1, String data2) {
        switch (data1) {
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

    private Boolean isPrinterConnected() {
        if (btsocket != null && btsocket.isConnected())
            return true;

        return false;

    }

    private void doPrintCurrentBill(final List<BaseModel> listPayments) {
        int printSize = tvPrintSize.getText().toString().equals(Constants.PRINTER_80) ? Constants.PRINTER_80_WIDTH : Constants.PRINTER_57_WIDTH;
        if (!Util.getInstance().isLoading()){
            Util.getInstance().showLoading("Đang in...");
        }

        new BluetoothPrintBitmap(outputStream,
                BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), printSize),
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        Util.getInstance().stopLoading(true);

                        if (result) {
                            CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc tiếp tục thanh toán", "TIẾP TỤC", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
                                @Override
                                public void Submit(Boolean boolSubmit) {
                                    postBilltoServer(listPayments, "");

                                }

                                @Override
                                public void Cancel(Boolean boolCancel) {
                                    doPrintCurrentBill(listPayments);
                                }

                            });
                        } else {
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

    }

    private void doPrintOldBill() {
        int printSize = tvPrintSize.getText().toString().equals(Constants.PRINTER_80) ? Constants.PRINTER_80_WIDTH : Constants.PRINTER_57_WIDTH;
        Util.getInstance().showLoading("Đang in...");

        new BluetoothPrintBitmap(outputStream,
                BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), printSize),
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        Util.getInstance().stopLoading(true);

                        if (result) {
                            CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc quay trở lại ", "TRỞ VỀ", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
                                @Override
                                public void Submit(Boolean boolSubmit) {
                                    Transaction.returnCustomerActivity(Constants.PRINT_BILL_ACTIVITY, "", Constants.RESULT_PRINTBILL_ACTIVITY);


                                }

                                @Override
                                public void Cancel(Boolean boolCancel) {
                                    doPrintOldBill();
                                }

                            });
                        } else {
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

    }

    private void postBilltoServer(final List<BaseModel> listPayments, String note) {
        String params = "";

        if (currentBill.isNull(Constants.DELIVER_BY) && currentBill.getInt("id") != 0) {
            params = DataUtil.updateBillDeliveredJsonParam(currentCustomer.getInt("id"),
                    currentBill,
                    User.getId(),
                    DataUtil.array2ListObject(currentBill.getString(Constants.BILL_DETAIL)));

        } else {
            params = DataUtil.newBillJsonParam(currentCustomer.getInt("id"),
                    User.getId(),
                    adapterBill.getTotalMoney(),
                    0.0,
                    DataUtil.array2ListObject(currentBill.getString(Constants.BILL_DETAIL)),
                    note,
                    User.getId(),
                    0);
        }

        BaseModel param = createPostParam(ApiUtil.BILL_NEW(), params, true, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                if (listPayments.size() > 0) {
                    if (listPayments.get(0).getInt("billId") == 0) {
                        listPayments.get(0).put("billId", result.getInt("id"));
                        listPayments.get(0).put("billTotal", result.getDouble("total"));

                    }
                    postPayToServer(DataUtil.createListPaymentParam(currentCustomer.getInt("id"), listPayments, false), true);

                } else {
                    BaseModel modelResult = new BaseModel();
                    modelResult.put(Constants.RELOAD_DATA, true);
                    Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY, modelResult, Constants.RESULT_PRINTBILL_ACTIVITY);

                }
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();
//        CustomerConnect.PostBill(params, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                if (listPayments.size() > 0) {
//                    if (listPayments.get(0).getInt("billId") == 0) {
//                        listPayments.get(0).put("billId", result.getInt("id"));
//                        listPayments.get(0).put("billTotal", result.getDouble("total"));
//
//                    }
//                    postPayToServer(DataUtil.createListPaymentParam(currentCustomer.getInt("id"), listPayments, false), true);
//
//                } else {
//                    BaseModel modelResult = new BaseModel();
//                    modelResult.put(Constants.RELOAD_DATA, true);
//                    Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY, modelResult, Constants.RESULT_PRINTBILL_ACTIVITY);
//
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//            }
//        }, false);

    }

    private void postPayToServer(List<String> listParam, Boolean stopLoading) {
        List<BaseModel> params = new ArrayList<>();

        for (String itemdetail: listParam){
            BaseModel item = createPostParam(ApiUtil.PAY_NEW(), itemdetail, false, false);
            params.add(item);
        }

        new CustomGetPostListMethod(params, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                BaseModel modelResult = new BaseModel();
                modelResult.put(Constants.RELOAD_DATA, true);
                Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY, modelResult, Constants.RESULT_PRINTBILL_ACTIVITY);

            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


//        CustomerConnect.PostListPay(listParam, new CallbackListCustom() {
//            @Override
//            public void onResponse(List result) {
//                BaseModel modelResult = new BaseModel();
//                modelResult.put(Constants.RELOAD_DATA, true);
//                Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY, modelResult, Constants.RESULT_PRINTBILL_ACTIVITY);
//
//
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }//
//        }, stopLoading);
    }

    //************** Todo Bluetooth SETUP

    @Override
    protected void onDestroy() {
        Util.getInstance().stopLoading(true);
        try {
            if (btsocket != null) {
                if (outputStream != null)
                    outputStream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mBTReceiver);

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private Runnable socketErrorRunnable = new Runnable() {

        @Override
        public void run() {
            Util.showToast("Cannot establish connection");
            mBluetoothAdapter.startDiscovery();

        }
    };

    public void registerBluetooth() {
        try {
            if (initDevicesList() != 0) {
                this.finish();
                return;
            }

        } catch (Exception ex) {
            this.finish();
            return;
        }
        IntentFilter btIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTReceiver, btIntentFilter);

    }

    public final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                addBluetoothItem2List(device);

                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))) {
                    updateViewWhileConnectBlu(device, false);

                }

            }
        }
    };

    protected void updateViewWhileConnectBlu(BluetoothDevice device, boolean showloading) {
        Util.getInstance().showLoading(showloading);
        connectBluetoothDevice(device, new CallbackProcess() {
            @Override
            public void onStart() {
                tvPrinterName.setText(Constants.CONNECTING_PRINTER);
                lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));

            }

            @Override
            public void onError() {
                Util.getInstance().stopLoading(true);
                tvPrinterName.setText("Chưa kết nối được máy in");
                Util.showToast(Constants.CONNECTED_PRINTER_ERROR);
                lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
            }

            @Override
            public void onSuccess(String name) {
                Util.getInstance().stopLoading(true);
                tvPrinterName.setText(String.format(Constants.CONNECTED_PRINTER, device.getName()));
                lnBottom.setBackgroundColor(getResources().getColor(R.color.colorBlue));

                if (bluFragment != null) {
                    bluFragment.finish();
                }
            }
        });
    }

    public int initDevicesList() {
        try {
            if (btsocket != null) {
                btsocket.close();

                btsocket = null;
            }

            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }

            finalize();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Util.showToast("Bluetooth not supported!!");

            return -1;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } catch (Exception ex) {
            return -2;
        }

        return 0;

    }

    public void connectBluetoothDevice(final BluetoothDevice device, final CallbackProcess mListener) {
        mListener.onStart();
        if (mBluetoothAdapter == null) {
            mListener.onError();
            return;

        }

        Thread connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (device.getUuids() != null) {
                        UUID uuid = device.getUuids()[0].getUuid();
                        btsocket = null;
                        btsocket = device.createRfcommSocketToServiceRecord(uuid);
                        btsocket.connect();
                    }

                } catch (IOException ex) {
                    runOnUiThread(socketErrorRunnable);
                    try {
                        if (btsocket != null)
                            btsocket.close();
                    } catch (IOException e) {
                        mListener.onError();
//                        e.printStackTrace();
                    }
                    btsocket = null;

                    return;
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if (btsocket != null) {
                                    CustomSQL.setString(Constants.BLUETOOTH_DEVICE, btsocket.getRemoteDevice().getAddress());
                                    outputStream = btsocket.getOutputStream();
                                    mBluetoothAdapter.cancelDiscovery();


                                    mListener.onSuccess(device.getName());
                                } else {
                                    mListener.onError();
                                }


                            } catch (IOException e) {
                                mListener.onError();
//                                e.printStackTrace();
                            }


                        }
                    });
                }
            }
        });
        connectThread.start();
    }

    protected void addBluetoothItem2List(BluetoothDevice device) {
        Boolean exist = false;
        for (int i = 0; i < listDevice.size(); i++) {
            if (listDevice.get(i).getAddress().equals(device.getAddress())) {
                exist = true;
                break;
            }
        }
        if (!exist) {
            listDevice.add(device);

            if (bluFragment != null) {
                bluFragment.updateList();
            }

        }
    }

}
