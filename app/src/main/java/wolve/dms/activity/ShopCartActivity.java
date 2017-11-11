package wolve.dms.activity;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.BluetoothListAdapter;
import wolve.dms.adapter.CartProductsAdapter;
import wolve.dms.adapter.CartPromotionsAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackListProduct;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.controls.CInputForm;
import wolve.dms.controls.CTextView;
import wolve.dms.libraries.printerdriver.PrinterCommands;
import wolve.dms.libraries.printerdriver.UtilPrinter;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.Printer;
import wolve.dms.models.Product;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.StringUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ShopCartActivity extends BaseActivity implements  View.OnClickListener {
    private ImageView btnBack;
    private Button btnSubmit;
    private TextView tvTitle, tvTotal, tvPrinterName;
    private CInputForm tvNote;
    private RecyclerView rvProducts, rvPromotions;
    private FloatingActionButton btnAdd, btnAddPromotion;
    private RelativeLayout rlCover;
    private CTextView btnPrinter;

    private DatePickerDialog fromDatePickerDialog;
    private static final int REQUEST_ENABLE_BT = 0*1000;

    private CartProductsAdapter adapterProducts;
    private CartPromotionsAdapter adapterPromotions;
    private Customer currentCustomer;
    private List<Product> listProducts = new ArrayList<>();
    private List<Product> listInitialProduct = new ArrayList<>();
    private List<Product> listInitialPromotion = new ArrayList<>();
    private List<BluetoothDevice> listDevice = new ArrayList<>();
    static private BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;

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
        btnSubmit =  findViewById(R.id.cart_submit);
        btnBack =  findViewById(R.id.icon_back);
        tvTitle =  findViewById(R.id.cart_title);
        tvTotal =  findViewById(R.id.cart_total);
        tvNote =  findViewById(R.id.cart_note);
        btnAdd =  findViewById(R.id.cart_add_product);
        btnAddPromotion =  findViewById(R.id.cart_add_promotion);
        rvProducts =  findViewById(R.id.cart_rvproduct);
        rvPromotions =  findViewById(R.id.cart_rvpromotion);
        rlCover =  findViewById(R.id.cart_cover);
        btnPrinter = findViewById(R.id.cart_bluetooth_printer);
        tvPrinterName = findViewById(R.id.cart_printer_name);

    }

    @Override
    public void initialData() {
        String bundle = getIntent().getExtras().getString(Constants.CUSTOMER_CART);
        if (bundle != null){
            try {
                currentCustomer = new Customer(new JSONObject(bundle));
                tvTitle.setText(Constants.getShopInfo(currentCustomer.getString("shopType") , null) +" - " + currentCustomer.getString("signBoard") );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (listInitialProduct.size() <=0){
            showDialogProduct("CHỌN SẢN PHẨM BÁN", false);
        }
        createRVProduct(listInitialProduct);
        createRVPromotion(listInitialPromotion);

        if(btsocket != null){
            tvPrinterName.setVisibility(View.VISIBLE);
            tvPrinterName.setText("Đang kết nối: "+ btsocket.getRemoteDevice().getName());

            try {
                outputStream = btsocket.getOutputStream();
            } catch (IOException e) {
                Util.showToast("Không thể kết nối");
                tvPrinterName.setVisibility(View.GONE);
            }

        }else {
            tvPrinterName.setVisibility(View.GONE);
        }

        IntentFilter btIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTReceiver, btIntentFilter);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnAddPromotion.setOnClickListener(this);
        btnPrinter.setOnClickListener(this);
        DatePickerEvent();
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

            case R.id.cart_submit:
                choicePayMethod();
//                createBillImage();



                break;

            case R.id.cart_add_product:
                showDialogProduct("CHON SẢN PHẨM BÁN", false);
                break;

            case R.id.cart_add_promotion:
                showDialogProduct("CHON SẢN PHẨM TẶNG KÈM", true);
                break;

            case R.id.cart_bluetooth_printer:
                if(btsocket == null){
                    Transaction.gotoBluetoothListActivity();
                }


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

    private void createRVProduct(final List<Product> list){
        adapterProducts = new CartProductsAdapter(list, new CallbackChangePrice() {
            @Override
            public void NewPrice(Double price) {
                tvTotal.setText(Util.FormatMoney(price));

                rvProducts.requestLayout();
                rvProducts.invalidate();
            }
        }) ;
        rvProducts.setAdapter(adapterProducts);
        rvProducts.setHasFixedSize(true);
        rvProducts.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvProducts.setLayoutManager(layoutManager);
    }

    private void createRVPromotion(final List<Product> list){
        adapterPromotions = new CartPromotionsAdapter(list, new CallbackDeleteAdapter() {
            @Override
            public void onDelete(String data, int position) {
//                listCurrentPromotion.remove(position);
                rvPromotions.requestLayout();
                rvPromotions.invalidate();
            }
        });
        rvPromotions.setAdapter(adapterPromotions);
        rvPromotions.setHasFixedSize(true);
        rvPromotions.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvPromotions.setLayoutManager(layoutManager);
    }

    private void showDialogProduct(String title, final Boolean isPromotion){
        listProducts = new ArrayList<>();
        try {
            for (int i=0 ; i<Util.mListProduct.size(); i++){
                Util.mListProduct.get(i).put("checked", false);
                listProducts.add(Util.mListProduct.get(i));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CustomCenterDialog.showDialogChoiceProduct(title ,listProducts,isPromotion, new CallbackListProduct() {
            @Override
            public void Products(List<Product> list_product) {
                for (int i=0; i<list_product.size(); i++){
                    try {
                        Product product = new Product(new JSONObject()) ;
                        product.put("id", list_product.get(i).getInt("id"));
                        product.put("name", list_product.get(i).getString("name"));
                        product.put("productGroup", list_product.get(i).getString("productGroup"));
                        product.put("promotion", list_product.get(i).getBoolean("promotion"));
                        product.put("unitPrice", list_product.get(i).getDouble("unitPrice"));
                        product.put("purchasePrice", list_product.get(i).getDouble("purchasePrice"));
                        product.put("volume", list_product.get(i).getInt("volume"));
                        product.put("image", list_product.get(i).getString("image"));
                        product.put("imageUrl", list_product.get(i).getString("imageUrl"));
                        product.put("checked", list_product.get(i).getBoolean("checked"));
                        product.put("isPromotion", isPromotion);
                        product.put("quantity", 1);

                        if (isPromotion){
                            product.put("totalMoney",0);
                            product.put("discount", product.getDouble("unitPrice"));
                            adapterPromotions.addItemPromotion(product);
                        }else {
                            product.put("totalMoney", product.getDouble("unitPrice"));
                            product.put("discount", 0);
                            adapterProducts.addItemProduct(product);
                            btnAddPromotion.setVisibility(adapterProducts.getItemCount() >0 ?View.VISIBLE : View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                rlCover.setVisibility(list_product.size() <=0? View.VISIBLE: View.GONE);
            }
        });
    }

    private void submitBill(Double total, Double paid){
        List<Product> listProductChoice = getListProduct();

        try {
            JSONObject params = new JSONObject();
            params.put("debt", total - paid);
            params.put("total", total);
            params.put("paid", paid);
            params.put("customerId", currentCustomer.getInt("id"));
            params.put("distributorId", Distributor.getCurrentDistributorId());
            params.put("userId", User.getCurrentUserId());
            params.put("note", tvNote.getText().toString().trim());

            JSONArray array = new JSONArray();
            for (int i=0; i< listProductChoice.size(); i++){
                JSONObject object = new JSONObject();
                object.put("productId", listProductChoice.get(i).getInt("id"));
                object.put("discount", listProductChoice.get(i).getDouble("discount"));
                object.put("quantity", listProductChoice.get(i).getDouble("quantity"));

                array.put(object);
            }
            params.put("billDetails", (Object) array);
            CustomerConnect.PostBill(params.toString(), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    //get customer detail from serer
                    String param = currentCustomer.getString("id");
                    CustomerConnect.GetCustomerDetail(param, new CallbackJSONObject() {
                        @Override
                        public void onResponse(JSONObject result) {
                            returnPreviousScreen(new Customer(result));

                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, true);
                }

                @Override
                public void onError(String error) {

                }
            }, false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Product> getListProduct(){
        List<Product> listProductChoice = new ArrayList<>();

        for (int i=0; i<adapterProducts.getAllDataProduct().size(); i++){
            listProductChoice.add(adapterProducts.getAllDataProduct().get(i));
        }
        for (int j=0; j<adapterPromotions.getAllDataPromotion().size(); j++){
            listProductChoice.add(adapterPromotions.getAllDataPromotion().get(j));
        }

        return listProductChoice;
    }

    private void DatePickerEvent(){
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvNote.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(newDate.getTime()));


            }

        },Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    private void choicePayMethod(){
        CustomBottomDialog.choicePayMethod(new CustomBottomDialog.PayMethodResult() {
            @Override
            public void OnCash(Boolean cash) {
                submitBill(Util.valueMoney(tvTotal) , Util.valueMoney(tvTotal));
            }

            @Override
            public void OnDebt(Boolean debt) {
                showDialogInputPaid();
            }
        });

    }

    private void createBillImage(){
        CustomCenterDialog.showDialogBillImage(currentCustomer,tvTotal.getText().toString(), getListProduct(), new CallbackPayBill() {
            @Override
            public void OnRespone(Double total, Double pay) {

            }
        });
    }

    private void showDialogInputPaid() {
        CustomCenterDialog.showDialogInputPaid("Nhập số tiến khách trả", "Tổng tiền hóa đơn", Util.valueMoney(tvTotal.getText().toString()), new CallbackPayBill() {
                    @Override
                    public void OnRespone(Double total, Double pay) {
                        submitBill(total, pay);
                    }
                });
    }

    private void printCustomText(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x05};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x00};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x30}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(StringUtil.unAccent(msg).getBytes());
            outputStream.write(PrinterCommands.LF);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), img);
            if(bmp!=null){
                byte[] command = UtilPrinter.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);

                outputStream.write(command);

            }else{
                Util.showToast("The file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.showToast("The file isn't exists");
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            btsocket = ListBluetoothActivity.getSocket();
//            if(btsocket != null){
//                tvPrinterName.setVisibility(View.VISIBLE);
//                tvPrinterName.setText("Đang kết nối: "+ btsocket.getRemoteDevice().getName());
//
//                outputStream = btsocket.getOutputStream();
//
//                flushBillDetai();
//
//            }else {
//                tvPrinterName.setVisibility(View.GONE);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent intent) {
        super.onActivityResult(reqCode, resultCode, intent);

        switch (reqCode) {
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    Set<BluetoothDevice> btDeviceList = mBluetoothAdapter.getBondedDevices();
                    try {
                        if (btDeviceList.size() > 0) {

                            for (BluetoothDevice device : btDeviceList) {
                                if (btDeviceList.contains(device) == false) {
                                    listDevice.add(device);

                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                }

                break;
        }
        mBluetoothAdapter.startDiscovery();

    }

    private void flushBillDetai(){
        //print command
        try {
            Thread.sleep(1000);

            outputStream.write(0x1C); outputStream.write(0x2E); // Cancels Chinese  character mode (FS .)
            outputStream.write(0x1B); outputStream.write(0x74); outputStream.write(0x10); // Select character code table (ESC t n) - n = 16(0x10) for WPC1252

            //printPhoto(R.drawable.ic_logo);

            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);

            printCustomText("  HOA DON  ",3,1);


            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);

            printCustomText("KH     :  " + Constants.getShopInfo(currentCustomer.getString("shopType") , null) + " "+ currentCustomer.getString("signBoard") , 1,0);
            printCustomText("SDT    :  " + currentCustomer.getString("phone"), 1,0);
            printCustomText("NGAY   :  " + Util.CurrentMonthYearHour(),1,0);
            printCustomText("N.VIEN :  " + User.getFullName(), 1,0);
            printCustomText("----------------",3,1);
            outputStream.write(PrinterCommands.FEED_LINE);


            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(btsocket!= null){
                outputStream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mBTReceiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectBluetoothDevice(final BluetoothDevice device) {
        if (mBluetoothAdapter == null) {
            return;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        Util.showToast("Connecting to " + device.getName());

        Thread connectThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    UUID uuid = device.getUuids()[0].getUuid();
                    btsocket = device.createRfcommSocketToServiceRecord(uuid);

                    btsocket.connect();
                } catch (IOException ex) {
                    runOnUiThread(socketErrorRunnable);
                    try {
                        btsocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btsocket = null;
                    return;
                } finally {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            finish();

                        }
                    });
                }
            }
        });

        connectThread.start();
    }

    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listDevice.add(device);
            }
        }
    };

    private Runnable socketErrorRunnable = new Runnable() {

        @Override
        public void run() {
            Util.showToast("Cannot establish connection");
            mBluetoothAdapter.startDiscovery();

        }
    };





}
