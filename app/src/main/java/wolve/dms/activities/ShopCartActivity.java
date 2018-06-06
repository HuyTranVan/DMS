package wolve.dms.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import wolve.dms.adapter.CartGroupButtonAdapter;
import wolve.dms.adapter.CartProductsAdapter;
import wolve.dms.adapter.CartPromotionsAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackListProduct;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.customviews.CInputForm;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.ItemDecorationGridSpace;
import wolve.dms.libraries.connectapi.DownloadImage;
import wolve.dms.libraries.printerdriver.PrinterCommands;
import wolve.dms.libraries.printerdriver.UtilPrinter;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;
import static wolve.dms.utils.Transaction.gotoImageChooser;

/**
 * Created by macos on 9/16/17.
 */

public class ShopCartActivity extends BaseActivity implements  View.OnClickListener {
    private ImageView btnBack;
    private Button btnSubmit;
    private CTextIcon tvBluetooth;
    private TextView tvTitle, tvTotal, tvHint; //btnAdd, btnAddPromotion;
    private CInputForm tvNote;
    private RecyclerView rvProducts, rvPromotions, rvButtonGroup;
//    private RelativeLayout rlCover;
    private ImageView imgLogo;
    private LinearLayout lnSubmitGroup;

    private CartProductsAdapter adapterProducts;
    private CartPromotionsAdapter adapterPromotions;
    private CartGroupButtonAdapter groupButtonAdapter;
    private Customer currentCustomer;
    private List<Product> listInitialProduct = new ArrayList<>();
    private List<Product> listInitialPromotion = new ArrayList<>();
    private List<Bill> listBills = new ArrayList<>();
    private Uri imageChangeUri ;
//    protected List<Product> listProducts = new ArrayList<>();
//    private List<BluetoothDevice> listDevice = new ArrayList<>();
//    private BluetoothAdapter mBluetoothAdapter = null;
//    private static BluetoothSocket btsocket;
//    private static OutputStream outputStream;



    @Override
    public int getResourceLayout() {
        return R.layout.activity_shopcart;
    }

    @Override
    public int setIdContainer() {
        return R.id.cart_parent;
    }

    @Override
    public void findViewById() {
        btnSubmit =  findViewById(R.id.cart_submit);
        btnBack =  findViewById(R.id.icon_back);
        tvTitle =  findViewById(R.id.cart_title);
        tvTotal =  findViewById(R.id.cart_total);
        tvHint =  findViewById(R.id.cart_hint);
        tvNote =  findViewById(R.id.cart_note);
        lnSubmitGroup = findViewById(R.id.cart_submit_group);
        //btnAdd =  findViewById(R.id.cart_add_product);
        //btnAddPromotion =  findViewById(R.id.cart_add_promotion);
        rvProducts =  findViewById(R.id.cart_rvproduct);
        rvPromotions =  findViewById(R.id.cart_rvpromotion);
        //rlCover =  findViewById(R.id.cart_cover);
        imgLogo = findViewById(R.id.cart_bluetooth_printer);
        tvBluetooth = findViewById(R.id.cart_bluetooth);
        rvButtonGroup = findViewById(R.id.cart_rvGroupButton);

    }

    @Override
    public void initialData() {
        String bundle = getIntent().getExtras().getString(Constants.CUSTOMER_CART);
        createRVButtonGroup(ProductGroup.getProductGroupList());
        if (bundle != null){
            try {
                currentCustomer = new Customer(new JSONObject(bundle));
                tvTitle.setText(Constants.getShopInfo(currentCustomer.getString("shopType") , null) +" - " + currentCustomer.getString("signBoard") );

                JSONArray array = new JSONArray(currentCustomer.getString("bills"));
                for (int i=0; i<array.length(); i++){
                    listBills.add(new Bill(array.getJSONObject(i)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!CustomSQL.getString("logo").equals("")){
            Glide.with(this).load(CustomSQL.getString("logo")).centerCrop().into(imgLogo);

        }else {
            new DownloadImage(this, imgLogo, Api_link.LOGO_BILL).execute();
        }

        createRVProduct(listInitialProduct);
        createRVPromotion(listInitialPromotion);
        registerBluetooth();
    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
        tvBluetooth.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.cart_parent);
        if(mFragment != null && mFragment instanceof ChoiceProductFragment) {
            ChoiceProductFragment fragment = (ChoiceProductFragment) mFragment;
            fragment.submitProduct();
            //getSupportFragmentManager().popBackStack();
        }else {
            returnCustomerActivity(currentCustomer);
        }

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                returnCustomerActivity(currentCustomer);

                break;

            case R.id.cart_submit:
                choicePayMethod();

                break;

            case R.id.cart_bluetooth_printer:
                imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
                gotoImageChooser();
                break;

            case R.id.cart_bluetooth:
                BluetoothListFragment fragment = new BluetoothListFragment();
                showFragmentDialog(fragment );

                break;
        }
    }

    private void returnCustomerActivity(Customer customer){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.CUSTOMER_CART, customer.CustomertoString());
        setResult(Constants.RESULT_SHOPCART_ACTIVITY,returnIntent);
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    private void createRVButtonGroup(final List<ProductGroup> list){
        lnSubmitGroup.setVisibility(View.GONE);

        groupButtonAdapter = new CartGroupButtonAdapter(list, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                ProductGroup productGroup = new ProductGroup(data);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PRODUCTGROUP, productGroup.ProductGrouptoString());
                changeFragment(new ChoiceProductFragment() , bundle, true);

            }
        });
        rvButtonGroup.setAdapter(groupButtonAdapter);
        rvButtonGroup.setHasFixedSize(true);
        rvButtonGroup.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvButtonGroup.setLayoutManager(layoutManager);
    }

    private void createRVProduct(final List<Product> list){
        adapterProducts = new CartProductsAdapter(list, new CallbackChangePrice() {
            @Override
            public void NewPrice(Double price) {
                tvTotal.setText(Util.FormatMoney(price));

                rvProducts.requestLayout();
                rvProducts.invalidate();

                lnSubmitGroup.setVisibility(adapterProducts.getAllDataProduct().size()>0 ? View.VISIBLE : View.GONE);
                tvHint.setVisibility(adapterProducts.getAllDataProduct().size()>0 ? View.VISIBLE : View.GONE);
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

                lnSubmitGroup.setVisibility(adapterPromotions.getAllDataPromotion().size()>0 ? View.VISIBLE : View.GONE);
                tvHint.setVisibility(adapterPromotions.getAllDataPromotion().size()>0 ? View.VISIBLE : View.GONE);
            }
        });
        rvPromotions.setAdapter(adapterPromotions);
        rvPromotions.setHasFixedSize(true);
        rvPromotions.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        rvPromotions.setLayoutManager(layoutManager);
    }

    protected void updatelistProduct(List<Product> list_product, Boolean isPromotion){
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
                    //btnAddPromotion.setVisibility(adapterProducts.getItemCount() >0 ?View.VISIBLE : View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitBill(final Double total, final Double paid){
        Util.getInstance().showLoading("Đang in...");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String params = createPostBillParam(total, paid, getListProduct());

//                try {
//                    final JSONObject params = new JSONObject();
//                    params.put("debt", total - paid);
//                    params.put("total", total);
//                    params.put("paid", paid);
//                    params.put("customerId", currentCustomer.getInt("id"));
//                    params.put("distributorId", Distributor.getDistributorId());
//                    params.put("userId", User.getUserId());
//                    params.put("note", tvNote.getText().toString().trim());
//
//                    JSONArray array = new JSONArray();
//                    for (int i=0; i< listProductChoice.size(); i++){
//                        JSONObject object = new JSONObject();
//                        object.put("productId", listProductChoice.get(i).getInt("id"));
//                        object.put("discount", listProductChoice.get(i).getDouble("discount"));
//                        object.put("quantity", listProductChoice.get(i).getDouble("quantity"));
//
//                        array.put(object);
//                    }
//                    params.put("billDetails", (Object) array);

                if (btsocket != null && btsocket.isConnected()){
                    CustomerConnect.printBill(outputStream, currentCustomer, getListProduct(), listBills,total,  paid, new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            Util.getInstance().stopLoading(true);
                            CustomCenterDialog.alertWithCancelButton2("TIẾP TỤC", "In thêm hoặc tiếp tục thanh toán", "TIẾP TỤC", "IN LẠI", new CustomCenterDialog.ButtonCallback() {
                                @Override
                                public void Submit(Boolean boolSubmit) {
                                    postBills(params.toString());

                                }

                                @Override
                                public void Cancel(Boolean boolCancel) {
                                    submitBill(total,paid);
                                }

                            });
                        }
                    });

                }else {
                    postBills(params.toString());
                }

            }
        }); thread.start();

    }

    private String createPostBillParam(final Double total, final Double paid, List<Product> listProduct){
        final JSONObject params = new JSONObject();
        try {
            params.put("debt", total - paid);
            params.put("total", total);
            params.put("paid", paid);
            params.put("customerId", currentCustomer.getInt("id"));
            params.put("distributorId", Distributor.getDistributorId());
            params.put("userId", User.getUserId());
            params.put("note", tvNote.getText().toString().trim());

            JSONArray array = new JSONArray();
            for (int i=0; i< listProduct.size(); i++){
                JSONObject object = new JSONObject();
                object.put("productId", listProduct.get(i).getInt("id"));
                object.put("discount", listProduct.get(i).getDouble("discount"));
                object.put("quantity", listProduct.get(i).getDouble("quantity"));

                array.put(object);
            }
            params.put("billDetails", (Object) array);
        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }

    private void postBills(String params){
        CustomerConnect.PostBill(params, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                //get customer detail from serer
                String param = currentCustomer.getString("id");
                CustomerConnect.GetCustomerDetail(param, new CallbackJSONObject() {
                    @Override
                    public void onResponse(JSONObject result) {
                        returnCustomerActivity(new Customer(result));

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
    }

    private List<Product> getListProduct(){
        List<Product> listProductChoice = new ArrayList<>();

        for (int i=0; i<adapterProducts.getAllDataProduct().size(); i++){
            Product product = adapterProducts.getAllDataProduct().get(i);
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
                submitBill(Util.valueMoney(tvTotal) , Util.valueMoney(tvTotal));

            }

            @Override
            public void Method2(Boolean two) {
                showDialogInputPaid();
            }

        });

    }

    private void showDialogInputPaid() {
        CustomCenterDialog.showDialogInputPaid("Nhập số tiến khách trả", "Tổng tiền hóa đơn", Util.valueMoney(tvTotal.getText().toString()), new CallbackPayBill() {
                    @Override
                    public void OnRespone(final Double total, final Double pay) {
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
        }



    }

    //    private void showDialogProduct(String title, final Boolean isPromotion, ProductGroup group){
//        listProducts = new ArrayList<>();
//        try {
//            for (int i=0 ; i<Product.getProductList().size(); i++){
//                Product product = Product.getProductList().get(i);
//                product.put("checked", false);
//                listProducts.add(product);
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        CustomCenterDialog.showDialogChoiceProduct(title ,listProducts,group, new CallbackListProduct() {
//            @Override
//            public void Products(List<Product> list_product) {
//                for (int i=0; i<list_product.size(); i++){
//                    try {
//                        Product product = new Product(new JSONObject()) ;
//                        product.put("id", list_product.get(i).getInt("id"));
//                        product.put("name", list_product.get(i).getString("name"));
//                        product.put("productGroup", list_product.get(i).getString("productGroup"));
//                        product.put("promotion", list_product.get(i).getBoolean("promotion"));
//                        product.put("unitPrice", list_product.get(i).getDouble("unitPrice"));
//                        product.put("purchasePrice", list_product.get(i).getDouble("purchasePrice"));
//                        product.put("volume", list_product.get(i).getInt("volume"));
//                        product.put("image", list_product.get(i).getString("image"));
//                        product.put("imageUrl", list_product.get(i).getString("imageUrl"));
//                        product.put("checked", list_product.get(i).getBoolean("checked"));
//                        product.put("isPromotion", isPromotion);
//                        product.put("quantity", 1);
//
//                        if (isPromotion){
//                            product.put("totalMoney",0);
//                            product.put("discount", product.getDouble("unitPrice"));
//                            adapterPromotions.addItemPromotion(product);
//                        }else {
//                            product.put("totalMoney", product.getDouble("unitPrice"));
//                            product.put("discount", 0);
//                            adapterProducts.addItemProduct(product);
//                            //btnAddPromotion.setVisibility(adapterProducts.getItemCount() >0 ?View.VISIBLE : View.GONE);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                rlCover.setVisibility(list_product.size() <=0? View.VISIBLE: View.GONE);
//            }
//        });
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            if(btsocket!= null){
//                outputStream.close();
//                btsocket.close();
//                btsocket = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        try {
//            unregisterReceiver(mBTReceiver);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void registerBluetooth() {
//        try {
//            if (initDevicesList() != 0) {
//                this.finish();
//                return;
//            }
//
//        } catch (Exception ex) {
//            this.finish();
//            return;
//        }
//        IntentFilter btIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mBTReceiver, btIntentFilter);
//
//    }

//    private void connectBluetoothDevice(final BluetoothDevice device){
//        if (mBluetoothAdapter == null) {
//            return;
//        }
//        Util.showSnackbar("Connecting to " + device.getName(), null, null);
//
//        Thread connectThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (device.getUuids()!= null){
//                        UUID uuid = device.getUuids()[0].getUuid();
//                        btsocket = device.createRfcommSocketToServiceRecord(uuid);
//                        btsocket.connect();
//                    }
//
//                } catch (IOException ex) {
//                    runOnUiThread(socketErrorRunnable);
//                    try {
//                        btsocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    btsocket = null;
//                    return;
//                } finally {
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                if (btsocket != null){
//                                    tvBluetooth.setText(R.string.icon_bluetooth_connected);
//                                    tvBluetooth.setTextColor(getResources().getColor(R.color.colorBlue));
//                                    //tvPrinterName.setText("Đang kết nối: "+ btsocket.getRemoteDevice().getName());
//                                    //printerProgress.setVisibility(View.GONE);
//
//                                    CustomSQL.setString(Constants.BLUETOOTH_DEVICE, btsocket.getRemoteDevice().getAddress());
//                                    outputStream = btsocket.getOutputStream();
//
//                                    mBluetoothAdapter.cancelDiscovery();
//                                }
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//                    });
//                }
//            }
//        });connectThread.start();
//    }

//    private void showListBluetooth(List<BluetoothDevice> list){
////        CustomCenterDialog.showBluetoothDevices(list, new BluetoothListAdapter.CallbackBluetooth() {
////            @Override
////            public void OnDevice(BluetoothDevice device) {
////                try {
////                    connectBluetoothDevice(device);
////                    if (btsocket != null){
////                        btsocket.close();
////                        btsocket = null;
////                    }
////
////
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////
////            }
////        });


//    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
//                    connectBluetoothDevice(device);
//                }
//
//                listDevice.add(device);
//
//            }
//        }
//    };

//    private Runnable socketErrorRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            Util.showToast("Cannot establish connection");
//            mBluetoothAdapter.startDiscovery();
//
//        }
//    };
//
//    private int initDevicesList() {
//        try {
//            if (btsocket != null) {
//                btsocket.close();
//
//                btsocket = null;
//            }
//
//            if (mBluetoothAdapter != null) {
//                mBluetoothAdapter.cancelDiscovery();
//            }
//
//            finalize();
//
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            Util.showToast("Bluetooth not supported!!");
//
//            return -1;
//        }
//
//        if (mBluetoothAdapter.isDiscovering()) {
//            mBluetoothAdapter.cancelDiscovery();
//        }
//
//
//        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        try {
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        } catch (Exception ex) {
//            return -2;
//        }
//
//        //Util.showToast("Getting all available Bluetooth Devices");
//
//        return 0;
//
//    }

}
