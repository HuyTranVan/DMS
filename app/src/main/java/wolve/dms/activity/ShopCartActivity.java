package wolve.dms.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
import wolve.dms.libraries.printerdriver.PrinterCommands;
import wolve.dms.libraries.printerdriver.UtilPrinter;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.Product;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;

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
    private RelativeLayout rlCover, rlBluetooth;
    private ProgressBar printerProgress;
    private ImageView imgLogo;

    private static final int REQUEST_ENABLE_BT = 0*1000;
    private static final String BLUETOOTH_STATUS = "Danh sách Bluetooth (%d)";

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
    private Uri imageChangeUri ;

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
        imgLogo = findViewById(R.id.cart_bluetooth_printer);
        tvPrinterName = findViewById(R.id.cart_printer_name);
        rlBluetooth = findViewById(R.id.cart_printer_group);
        printerProgress = findViewById(R.id.cart_printer_progress);

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
        Glide.with(this).load(CustomSQL.getString("logo")).placeholder(R.drawable.ic_logo).centerCrop().into(imgLogo);
        createRVProduct(listInitialProduct);
        createRVPromotion(listInitialPromotion);
        registerBluetooth();


    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnAddPromotion.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
        rlBluetooth.setOnClickListener(this);
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
                startImageChooser();
                break;

            case R.id.cart_printer_group:
                showListBluetooth(listDevice);
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
            for (int i=0 ; i<Product.getProductList().size(); i++){
                Product product = Product.getProductList().get(i);
                product.put("checked", false);
                listProducts.add(product);

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
            params.put("userId", User.getUserId());
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

            flushBillDetail(total, paid);

            Thread.sleep(1000);

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
        } catch (InterruptedException e) {
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

    private void choicePayMethod(){
        CustomBottomDialog.choiceTwoOption(getString(R.string.icon_money), "Thu tiền mặt",
                getString(R.string.icon_warning),"Công nợ", new CustomBottomDialog.TwoMethodListener() {
            @Override
            public void Method1(Boolean one) {
                Util.getInstance().showLoading("Đang in...");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        submitBill(Util.valueMoney(tvTotal) , Util.valueMoney(tvTotal));

                    }
                }); thread.start();
            }

            @Override
            public void Method2(Boolean two) {
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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent intent) {
        super.onActivityResult(reqCode, resultCode, intent);
        if (reqCode == REQUEST_CHOOSE_IMAGE){
            if (intent != null){
                Crop.of(Uri.parse(intent.getData().toString()), imageChangeUri).withAspect(35,15).withMaxSize(350,150).start(this);

            }

        } else if (reqCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            Glide.with(this).load(imageChangeUri).fitCenter().into(imgLogo);

        } else if (reqCode == Crop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(imageChangeUri).fitCenter().into(imgLogo);
                CustomSQL.setString("logo", Util.getRealPathFromURI(imageChangeUri));

            } else if (resultCode == Crop.RESULT_ERROR) {
                Util.showToast(Crop.getError(intent).getMessage());

            }
        } else if (reqCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
                try {
                    Set<BluetoothDevice> btDeviceList = mBluetoothAdapter.getBondedDevices();
                    if (btDeviceList.size() > 0) {
                        for (BluetoothDevice device : btDeviceList) {
                            if (btDeviceList.contains(device) == false) {
                                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
                                    connectBluetoothDevice(device);
                                }

                                listDevice.add(device);
                                tvPrinterName.setText(String.format(BLUETOOTH_STATUS, listDevice.size()));

                            }
                        }
                    }
                } catch (Exception ex) {
                }
                mBluetoothAdapter.startDiscovery();
            }



    }

    private void flushBillDetail(Double total, Double paid){
        //print command
        try {
            //Thread.sleep(1000);

            UtilPrinter.printPhoto(outputStream,CustomSQL.getString("logo"));
            UtilPrinter.printCustomText(outputStream,"CTY TNHH XNK TRAN VU ANH",1,1);
            UtilPrinter.printCustomText(outputStream, "1 duong 57,P.Binh Trung Dong,Q 2", 1,1);
            UtilPrinter.printCustomText(outputStream,"Hotline: 0931072223",4,1);
            UtilPrinter.printCustomText(outputStream,"www.wolver.vn",4,1);
            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomText(outputStream, "  HOA DON BAN HANG ",3,1);
            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomText(outputStream,"CH     :  " + Constants.getShopInfo(currentCustomer.getString("shopType") , null) + " "+ currentCustomer.getString("signBoard") , 1,0);
            UtilPrinter.printCustomText(outputStream,"KH     :  " + currentCustomer.getString("name"), 1,0);
            UtilPrinter.printCustomText(outputStream,"SDT    :  " + currentCustomer.getString("phone"), 1,0);
            UtilPrinter.printCustomText(outputStream,"NGAY   :  " + Util.CurrentMonthYearHour(),1,0);
            UtilPrinter.printCustomText(outputStream,"N.VIEN :  " + User.getFullName(), 1,0);
            UtilPrinter.printCustomText(outputStream,"---------------------",3,1);

            List<Product> listProduct = getListProduct();
            for (int i=0; i<listProduct.size(); i++){
                UtilPrinter.printCustomText(outputStream,String.format("%d.%s (%s)" ,i+1, listProduct.get(i).getString("name"), Util.FormatMoney(listProduct.get(i).getDouble("unitPrice"))) , 1,0);
                String discount = listProduct.get(i).getDouble("discount") == 0? "" : " -" + Util.FormatMoney(listProduct.get(i).getDouble("discount"));
                UtilPrinter.printCustom2Text(outputStream ,
                        "   "+ Util.FormatMoney(listProduct.get(i).getDouble("quantity")) + "x" + Util.FormatMoney(listProduct.get(i).getDouble("unitPrice")) + discount ,
                        Util.FormatMoney(listProduct.get(i).getDouble("totalMoney")) , 1,0);

                if (i != listProduct.size() -1){
                    UtilPrinter.printCustomText(outputStream,"--------------------------------",1,1);
                }
            }
            UtilPrinter.printCustomText(outputStream,"---------------------",3,1);
            UtilPrinter.printCustom2Text(outputStream,"TONG:", Util.FormatMoney(total),4,2);
            UtilPrinter.printCustom2Text(outputStream,"TRA:", Util.FormatMoney(paid),4,2);
            UtilPrinter.printCustom2Text(outputStream,"CON LAI:", Util.FormatMoney(total - paid),4,2);
            UtilPrinter.printCustomText(outputStream,"---------------------",3,1);
            UtilPrinter.printCustomText(outputStream, "Tran trong cam on quy khach hang", 1,1);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.flush();
            Util.getInstance().stopLoading(true);

        } catch (IOException e) {
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

    private void registerBluetooth() {
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

        printerProgress.setVisibility(View.VISIBLE);
        tvPrinterName.setText(String.format(BLUETOOTH_STATUS, listDevice.size()));

    }

    private void connectBluetoothDevice(final BluetoothDevice device) {
        if (mBluetoothAdapter == null) {
            return;
        }
        Util.showSnackbar("Connecting to " + device.getName(), null, null);

        Thread connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (device.getUuids()!= null){
                        UUID uuid = device.getUuids()[0].getUuid();
                        btsocket = device.createRfcommSocketToServiceRecord(uuid);
                        btsocket.connect();
                    }

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
                            try {
                                if (btsocket != null){
                                    tvPrinterName.setText("Đang kết nối: "+ btsocket.getRemoteDevice().getName());
                                    printerProgress.setVisibility(View.GONE);

                                    CustomSQL.setString(Constants.BLUETOOTH_DEVICE, btsocket.getRemoteDevice().getAddress());
                                    outputStream = btsocket.getOutputStream();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //mBluetoothAdapter.cancelDiscovery();

                        }
                    });
                }
            }
        });connectThread.start();
    }

    private void showListBluetooth(List<BluetoothDevice> list){
        CustomCenterDialog.showBluetoothDevices(list, new BluetoothListAdapter.CallbackBluetooth() {
            @Override
            public void OnDevice(BluetoothDevice device) {
                try {
                    if (btsocket != null){
                        btsocket.close();
                        btsocket = null;
                    }
                    connectBluetoothDevice(device);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
                    connectBluetoothDevice(device);
                }

                listDevice.add(device);
                tvPrinterName.setText(String.format(BLUETOOTH_STATUS, listDevice.size()));

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

    private int initDevicesList() {
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

        //Util.showToast("Getting all available Bluetooth Devices");

        return 0;

    }

    private void startImageChooser() {
        // Kiểm tra permission với android sdk >= 23
        imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
        if (Build.VERSION.SDK_INT <= 19) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, REQUEST_CHOOSE_IMAGE);

        } else if (Build.VERSION.SDK_INT > 19) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
        }

    }




}
