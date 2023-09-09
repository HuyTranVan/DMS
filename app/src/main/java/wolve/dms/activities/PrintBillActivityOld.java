package wolve.dms.activities;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.zxing.WriterException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import wolve.dms.R;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.PrintBillAdapter;
import wolve.dms.adapter.PrintOldBillAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.DownloadImageMethod;
import wolve.dms.apiconnect.apiserver.GetPostListMethod;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBitmap;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackProcess;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.libraries.BitmapView;
import wolve.dms.libraries.printerdriver.BluetoothPrintBitmap;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.PdfGenerator;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/15/17.
 */

public class PrintBillActivityOld extends BluetoothActivity implements View.OnClickListener{
    private ImageView imgLogo, btnBack, imgOrderPhone;
    private TextView tvCompany, tvAdress, tvHotline, tvWebsite, tvTitle, tvShopName, tvCustomerName, tvCustomerAddress, tvDate, tvEmployee,
            tvSumCurrentBill, tvOrderPhone, tvThanks, tvPrinterMainText, tvPrinterName, tvTotal, tvPaid, tvRemain, tvTotalTitle,
            tvEmployeeSign, tvDeliver, tvDeliverTitle, tvListPrinter, tvPrintText;
    private RecyclerView rvBills, rvDebts;
    private LinearLayout lnMain, lnBottom, lnSubmit, lnTotalGroup, lnPaidGroup, lnRemainGroup, lnSignature;
    private View line1, line2, line3, line4;
    private NestedScrollView scContentParent;
    private BaseModel currentCustomer, currentBill, distributor;
    private PrintBillAdapter adapterBill;

    private PrintOldBillAdapter adapterOldBill;
    private DebtAdapter adapterDebt;
    private List<BaseModel> listDebts = new ArrayList<>();
    private boolean rePrint;
    ;
    private Dialog dialogPayment;
    private String orderPhone;
    private Uri currentImagePath;
    private Bitmap logoBitmap = null;
    private double currentPaid = 0.0;
    private PDFView pdfView;
    private Uri uriBill;
    private File currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_print_bill_pdf;
    }

    @Override
    public int setIdContainer() {
        return R.id.print_bill_parent;
    }

    @Override
    public void findViewById() {
        pdfView = findViewById(R.id.print_bill_pdf);
        btnBack = findViewById(R.id.icon_back);
        lnBottom = findViewById(R.id.print_bill_bottom);
        lnSubmit = findViewById(R.id.print_bill_submit);

    }

    private void findIdThermalPrinter(){
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
        //tvPrintSize = findViewById(R.id.print_bill_bottom_printersize);
        tvListPrinter = findViewById(R.id.print_bill_bottom_printerselect);
        //tvShare = findViewById(R.id.print_bill_bottom_share);
        tvEmployeeSign = findViewById(R.id.print_bill_employee_sign);
//        lnBottom = findViewById(R.id.print_bill_bottom);
//        rvDebts = findViewById(R.id.print_bill_rvdebt);
//        lnSubmit = findViewById(R.id.print_bill_submit);
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
        tvPrintText = findViewById(R.id.print_bill_bottom_printerselect_text);

    }

    private void findIdIsoPrinter(){


    }
    @Override
    public void initialData() {
        distributor = Distributor.getObject();
        currentCustomer = new BaseModel(CustomSQL.getString(Constants.CUSTOMER));
        currentBill = new BaseModel(getIntent().getExtras().getString(Constants.BILL));
        listDebts = new ArrayList<>(DataUtil.array2ListObject(currentCustomer.getString(Constants.DEBTS)));
        rePrint = getIntent().getExtras().getBoolean(Constants.RE_PRINT);

        new DownloadImageMethod(distributor.getString("image"), new CallbackBitmap() {
            @Override
            public void onResponse(Bitmap bitmap) {
                logoBitmap = bitmap;
                if (rePrint){
                    pdfView.fromFile(Util.createNewFile(PdfGenerator.createPdfOldBill(currentCustomer, listDebts, logoBitmap))).spacing(2).load();

                }else {
                    pdfView.fromFile(Util.createNewFile(PdfGenerator.createPdfBill(currentCustomer,
                                                        currentBill,
                                                        listDebts,
                                                        logoBitmap,
                                                        currentPaid))).spacing(2).load();

                }

            }
        }).execute();



    }

    private void inititalThermalData(){
        if (rePrint) {
            orderPhone = User.getContact();
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
            orderPhone = currentBill.getBaseModel("user").getString("contact");
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

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        lnBottom.setOnClickListener(this);
        lnSubmit.setOnClickListener(this);
//        tvListPrinter.setOnClickListener(this);

//        imgLogo.setOnClickListener(this);

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
                openBluetoothSeclectFragment();

                break;


            case R.id.print_bill_submit:
                if (rePrint) {
                    CustomCenterDialog.showDialogOptionShare(new CallbackInt() {
                        @Override
                        public void onResponse(int value) {
                            Transaction.shareVia(Util.storePDF(PdfGenerator.createPdfOldBill(currentCustomer, listDebts, logoBitmap)),
                                                    false,
                                                    value,
                                                    currentCustomer);
                            returnPreActivity(false);

                        }
                    });


                } else {
                    showDialogPayment(null);
                }

                break;

        }
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
        dialogPayment = CustomCenterDialog.showDialogPayment("Nhập số tiền khách trả",
                getAllDebt(),
                0.0,
                true,
                new CallbackListCustom() {
                    @Override
                    public void onResponse(final List result) {
                        dialogPayment.dismiss();

                        final Double total = adapterBill.getTotalMoney() + adapterDebt.getTotalMoney();
                        currentPaid = DataUtil.sumValueFromList(result, "paid");
                        Double remain = total - currentPaid;

                        tvPaid.setText(Util.FormatMoney(currentPaid));
                        tvRemain.setText(Util.FormatMoney(remain));

                        if (remain > 0) {
                            line4.setVisibility(View.GONE);
                            lnSignature.setVisibility(View.VISIBLE);
                            tvEmployeeSign.setText(String.format("NV: %s", User.getFullName()));
                        } else {
                            line4.setVisibility(View.VISIBLE);
                            lnSignature.setVisibility(View.GONE);
                        }

                        if (isShareZalo != null) {
                            isShareZalo.onRespone(true);

                        } else {
                            Util.getInstance().showLoading("Đang kiểm tra...");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Util.getInstance().stopLoading(true);
                                    if (isPrinterConnected()) {
                                        doPrintCurrentBill(result);

                                    } else {
                                        CustomCenterDialog.alertWithCancelButton(null, "Chưa kết nối máy in. Bạn muốn tiếp tục thanh toán không xuất hóa đơn", "Tiếp tục", "Quay lại", new CallbackBoolean() {
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
        returnPreActivity(false);

    }

    private void returnPreActivity(boolean loadData) {

        if (loadData) {
            BaseModel modelResult = new BaseModel();
            modelResult.put(Constants.RELOAD_DATA, true);

            CustomCenterDialog.alertWithCancelButton(null,
                    !rePrint ? "Cập nhật hóa đơn lên hệ thống thành công. Bạn muốn chia sẻ hóa đơn không?" :
                            "Chưa kết nối máy in. Bạn muốn tiếp tục chia sẻ công nợ",
                    "Chia sẻ", "Không",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean bool) {
//                            Uri uri = PdfGenerator.createPdfBill(currentCustomer,
//                                    currentBill,
//                                    listDebts,
//                                    logoBitmap,
//                                    currentPaid);
//                            Transaction.shareDocument(uri, currentCustomer);
                            if (bool) {
//                                currentImagePath = Util.storeImage(BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), 0),
//                                        "DMS",
//                                        "SHARE",
//                                        true);

//                                currentImagePath = PdfGenerator.createPdfBill(currentCustomer,
//                                                                                currentBill,
//                                        ),
//                                        "DMS",
//                                        "SHARE",
//                                        true);




                                Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY,
                                        modelResult,
                                        Constants.RESULT_PRINTBILL_ACTIVITY);

                            } else {
                                Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY,
                                        modelResult,
                                        Constants.RESULT_PRINTBILL_ACTIVITY);

                            }

                        }
                    });


        } else {
            Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY, new BaseModel(), Constants.RESULT_PRINTBILL_ACTIVITY);

        }


    }

    private void createCurrentRVBill(BaseModel bill) {
        adapterBill = new PrintBillAdapter(DataUtil.array2ListObject(bill.getString(Constants.BILL_DETAIL)));
        Util.createLinearRV(rvBills, adapterBill);

    }

    private void createRVDebt(final List<BaseModel> list) {
        DataUtil.sortbyStringKey("createAt", list, true);
        adapterDebt = new DebtAdapter(list, true, false);
        Util.createLinearRV(rvDebts, adapterDebt);

    }

    private void createOldRVBill(final List<BaseModel> list) {
        adapterOldBill = new PrintOldBillAdapter(list);
        Util.createLinearRV(rvBills, adapterOldBill);

    }

    private void doPrintCurrentBill(final List<BaseModel> listPayments) {
        //int printSize = tvPrintSize.getText().toString().equals(Constants.PRINTER_80) ? Constants.PRINTER_80_WIDTH : Constants.PRINTER_57_WIDTH;
        if (!Util.getInstance().isLoading()) {
            Util.getInstance().showLoading("Đang in...");
        }

        new BluetoothPrintBitmap(outputStream,
                BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent) , 0),
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
        //int printSize = tvPrintSize.getText().toString().equals(Constants.PRINTER_80) ? Constants.PRINTER_80_WIDTH : Constants.PRINTER_57_WIDTH;
        Util.getInstance().showLoading("Đang in...");

        new BluetoothPrintBitmap(outputStream,
                BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), 0),
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
                    adapterBill.getNetTotalMoney(),
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
                    postPayToServer(DataUtil.createListPaymentParam(currentCustomer.getInt("id"), listPayments, false),
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result) {
                                        returnPreActivity(true);
                                    }
                                }
                            });

                } else {
                    returnPreActivity(true);


                }
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }

    private void postPayToServer(List<String> listParam, CallbackBoolean success) {
        List<BaseModel> params = new ArrayList<>();

        for (String itemdetail : listParam) {
            BaseModel item = createPostParam(ApiUtil.PAY_NEW(), itemdetail, false, false);
            params.add(item);
        }

        new GetPostListMethod(params, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                success.onRespone(true);
//                BaseModel modelResult = new BaseModel();
//                modelResult.put(Constants.RELOAD_DATA, true);
//                Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY, modelResult, Constants.RESULT_PRINTBILL_ACTIVITY);

            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }

    protected void updateViewWhileConnectBlu(BluetoothDevice device, boolean showloading) {
        Util.getInstance().showLoading(showloading);
        connectBluetoothDevice(device, new CallbackProcess() {
            @Override
            public void onStart() {
                tvPrinterName.setText(Constants.CONNECTING_PRINTER);
                lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
                currentBluetooth = null;
                if (bluFragment != null) {
                    bluFragment.updateItem(currentBluetooth, false);

                }


            }

            @Override
            public void onError() {
                Util.getInstance().stopLoading(true);
                tvPrinterName.setText("Chưa kết nối được máy in");
                Util.showToast(Constants.CONNECTED_PRINTER_ERROR);
                lnBottom.setBackgroundColor(getResources().getColor(R.color.black_text_color_hint));
                currentBluetooth = null;
                if (bluFragment != null) {
                    bluFragment.updateItem(currentBluetooth, false);

                }


            }

            @Override
            public void onSuccess(String name) {
                Util.getInstance().stopLoading(true);

                if (ActivityCompat.checkSelfPermission(PrintBillActivityOld.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                tvPrinterName.setText(String.format(Constants.CONNECTED_PRINTER,
                        CustomSQL.getString(Constants.PRINTER_SIZE),
                        device.getName(),
                        device.getAddress()));
                lnBottom.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                currentBluetooth = device;
                //bluFragment.updateItem(currentBluetooth, false);

                if (bluFragment != null) {
                    bluFragment.finish();

                }
            }
        });
    }

    @Override
    public void onResponse(BaseModel object){

    }


}
