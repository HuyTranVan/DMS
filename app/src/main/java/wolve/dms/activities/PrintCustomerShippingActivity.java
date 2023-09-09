package wolve.dms.activities;

import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import com.google.zxing.WriterException;

import org.apache.http.client.UserTokenHandler;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import wolve.dms.R;
import wolve.dms.adapter.DebtAdapter;
import wolve.dms.adapter.PrintBillAdapter;
import wolve.dms.adapter.PrintOldBillAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostListMethod;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackObject;
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
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/15/17.
 */

public class PrintCustomerShippingActivity extends BluetoothActivity implements View.OnClickListener, CallbackObject {
    private ImageView btnBack;
    private TextView tvCompany, tvAdress, tvSender, tvPhone, tvShopName, tvCustomerName, tvCustomerAddress,
            tvCustomerPhone, tvNote, tvListPrinter, tvPrinterName;

    private LinearLayout lnMain, lnBottom, lnSubmit;
    //private View line1, line2, line3, line4;
    private NestedScrollView scContentParent;

    private BaseModel currentCustomer;
    private Bitmap mLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_print_customer_shipping;
    }

    @Override
    public int setIdContainer() {
        return R.id.print_customer_shipping_parent;
    }

    @Override
    public void findViewById() {
        btnBack = findViewById(R.id.icon_back);
        tvCompany = findViewById(R.id.print_customer_shipping_company);
        tvAdress = findViewById(R.id.print_customer_shipping_address);
        tvSender = findViewById(R.id.print_customer_shipping_sender);
        tvPhone = findViewById(R.id.print_customer_shipping_phone);

        tvShopName = findViewById(R.id.print_customer_shipping_shopname);
        tvCustomerName = findViewById(R.id.print_customer_shipping_shopowner);
        tvCustomerAddress = findViewById(R.id.print_customer_shipping_shopaddress);
        tvCustomerPhone = findViewById(R.id.print_customer_shipping_shopphone);
        lnMain = findViewById(R.id.print_bill_mainlayout);
        tvListPrinter = findViewById(R.id.print_customer_shipping_bottom_printerselect);
        tvPrinterName = findViewById(R.id.print_customer_shipping_bottom_secondtext);
        lnBottom = findViewById(R.id.print_customer_shipping_bottom);
        lnSubmit = findViewById(R.id.print_customer_shipping_submit);

        scContentParent = findViewById(R.id.print_customer_shipping_content_parent);

    }

    @Override
    public void initialData() {
        currentCustomer = new BaseModel(CustomSQL.getString(Constants.CUSTOMER));


        BaseModel distributor = Distributor.getObject();
        tvCompany.setText(distributor.getString("company"));
        tvAdress.setText(distributor.getString("address"));
        tvSender.setText(String.format("Người gửi: %s", User.getFullName()));
        tvPhone.setText(String.format("SĐT: %s", Util.FormatPhone(User.getContact())));


        tvShopName.setText(String.format("%s", currentCustomer.getString("signBoard").toUpperCase()));
        tvCustomerAddress.setText(String.format("%s, %s", currentCustomer.getString("district"), currentCustomer.getString("province")));

        tvCustomerName.setText(String.format("Người nhận: %s", currentCustomer.getString("name")));

        String phone = currentCustomer.getString("phone").equals("") ? "--" : Util.FormatPhone(currentCustomer.getString("phone"));
        tvCustomerPhone.setText(phone);


    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        lnBottom.setOnClickListener(this);
        tvListPrinter.setOnClickListener(this);
        lnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.print_bill_bottom:


                break;

            case R.id.print_customer_shipping_bottom_printerselect:
                openBluetoothSeclectFragment();

                break;


            case R.id.print_customer_shipping_submit:
                doPrintLabel();

                break;


        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Transaction.returnPreviousActivity();
        Transaction.returnPreviousActivity(Constants.PRINT_BILL_ACTIVITY,
                new BaseModel(),
                Constants.RESULT_PRINTBILL_ACTIVITY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mBluetoothAdapter.startDiscovery();


        }
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

                if (ActivityCompat.checkSelfPermission(PrintCustomerShippingActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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
    public void onResponse(BaseModel object) {

    }

    private void doPrintLabel() {
        //int printSize = tvPrintSize.getText().toString().equals(Constants.PRINTER_80) ? Constants.PRINTER_80_WIDTH : Constants.PRINTER_57_WIDTH;
        Util.getInstance().showLoading("Đang in...");
        mLabel = BitmapView.ResizeBitMapDependWidth(BitmapView.getBitmapFromView(scContentParent), 0);
        new BluetoothPrintBitmap(outputStream,
                mLabel,
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        Util.getInstance().stopLoading(true);

                        if (result) {
                            CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc quay trở lại ", "TRỞ VỀ", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
                                @Override
                                public void Submit(Boolean boolSubmit) {
                                    if (boolSubmit)
                                        onBackPressed();
                                        //Transaction.returnPreviousActivity();


                                }

                                @Override
                                public void Cancel(Boolean boolCancel) {
                                    doPrintLabel();
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
}
