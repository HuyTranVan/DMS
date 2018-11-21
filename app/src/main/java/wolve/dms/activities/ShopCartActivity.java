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
import android.provider.Settings;
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
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
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
import wolve.dms.callback.CallbackProcess;
import wolve.dms.customviews.CInputForm;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.libraries.ItemDecorationGridSpace;
import wolve.dms.libraries.connectapi.DownloadImage;
import wolve.dms.libraries.printerdriver.PrinterCommands;
import wolve.dms.libraries.printerdriver.UtilPrinter;
import wolve.dms.models.BaseModel;
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
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_ENABLE_BT;
import static wolve.dms.utils.Transaction.gotoImageChooser;

/**
 * Created by macos on 9/16/17.
 */

public class ShopCartActivity extends BaseActivity implements  View.OnClickListener, BluetoothListFragment.OnDataPass, View.OnLongClickListener {
    private ImageView btnBack;
    private Button btnSubmit;
    protected TextView tvPrinterStatus;
    private TextView tvTitle, tvTotal;
    private CInputForm tvNote;
    private RecyclerView rvProducts;
    private RelativeLayout rlCover;
    private LinearLayout lnSubmitGroup;
    private FloatingActionButton btnAddProduct;

    private CartProductsAdapter adapterProducts;
    private Customer currentCustomer;
    private List<Product> listInitialProduct = new ArrayList<>();
    private List<Bill> listBills = new ArrayList<>();
    private Uri imageChangeUri ;
    protected List<Product> listProducts = new ArrayList<>();
    protected List<ProductGroup> listProductGroups = new ArrayList<>();


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
        tvPrinterStatus =  findViewById(R.id.cart_printer_status);
        tvNote =  findViewById(R.id.cart_note);
        lnSubmitGroup = findViewById(R.id.cart_submit_group);
        rvProducts =  findViewById(R.id.cart_rvproduct);
        rlCover =  findViewById(R.id.cart_cover);
        btnAddProduct = findViewById(R.id.add_product);

    }

    @Override
    public void initialData() {
        Util.shopCartActivity = this;
        String bundle = getIntent().getExtras().getString(Constants.CUSTOMER_CART);
        lnSubmitGroup.setVisibility(View.GONE);
        rlCover.setVisibility(View.VISIBLE);
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

//        if (!CustomSQL.getString("logo").equals("")){
//            Glide.with(this).load(CustomSQL.getString("logo")).centerCrop().into(imgLogo);
//
//        }else {
//            new DownloadImage(this, imgLogo, Api_link.LOGO_BILL).execute();
//        }
        loadListProduct();
        loadListProductGroup();
        createRVProduct(listInitialProduct);

        if (!Util.getDeviceName().equals(Constants.currentEmulatorDevice)){
            registerBluetooth();
        }

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSubmit.setOnLongClickListener(this);
//        imgLogo.setOnClickListener(this);
        tvPrinterStatus.setOnClickListener(this);
        btnAddProduct.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.cart_parent);
        if (Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);

        }else if(mFragment != null && mFragment instanceof ChoiceProductFragment) {
            ChoiceProductFragment fragment = (ChoiceProductFragment) mFragment;
            fragment.submitProduct();

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

//            case R.id.cart_logo:
//                imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
//                gotoImageChooser();
//                break;

            case R.id.cart_printer_status:
                BluetoothListFragment fragment = new BluetoothListFragment();
                showFragmentDialog(fragment );

                break;

            case R.id.add_product:
                changeFragment(new ChoiceProductFragment() , true);

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

    private void loadListProduct(){
        List<Product> all = Product.getProductList();
        try {
            for (int i=0 ; i<all.size(); i++){
                Product product = all.get(i);
                product.put("checked", false);

                listProducts.add(product);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadListProductGroup(){
        listProductGroups = ProductGroup.getProductGroupList();

    }

    private void createRVProduct(final List<Product> list){
        adapterProducts = new CartProductsAdapter(list, new CallbackChangePrice() {
            @Override
            public void NewPrice(Double price) {
                tvTotal.setText(Util.FormatMoney(price));

                rvProducts.requestLayout();
                rvProducts.invalidate();

                lnSubmitGroup.setVisibility(adapterProducts.getAllDataProduct().size()>0 ? View.VISIBLE : View.GONE);
                rlCover.setVisibility(adapterProducts.getAllDataProduct().size()>0 ? View.GONE : View.VISIBLE);
            }
        }) ;
        Util.createLinearRV(rvProducts, adapterProducts);

    }

    protected void updatelistProduct(List<Product> list_product){
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
                product.put("isPromotion", false);
                product.put("quantity", 1);
                product.put("totalMoney", product.getDouble("unitPrice"));
                product.put("discount", 0);
                adapterProducts.addItemProduct(product);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitBill(final Double total, final Double paid){
        final String params = DataUtil.createPostBillParam(currentCustomer.getInt("id"),total, paid, adapterProducts.getAllData(), tvNote.getText().toString().trim());

        if (btsocket != null && btsocket.isConnected()){
            Util.getInstance().showLoading("Đang in...");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    CustomerConnect.printBill(outputStream, currentCustomer, adapterProducts.getAllDataProduct(), listBills,total,  paid, new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            Util.getInstance().stopLoading(true);
                            if (result){
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
                            }else {
                                CustomCenterDialog.alertWithButton("LỖI", "Kết nối máy in thất bại. Vui lòng thực hiện kết nối lại", "ĐỒNG Ý", new CallbackBoolean() {
                                    @Override
                                    public void onRespone(Boolean result) {
                                        tvPrinterStatus.setText("Chưa kết nối được máy in");
                                    }
                                });
                            }

                        }
                    });



                }
            }); thread.start();

        }else {
            CustomCenterDialog.alertWithCancelButton(null, "Chưa kết nối máy in. Bạn muốn tiếp tục thanh toán không xuất hóa đơn", "Tiếp tục","hủy", new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    postBills(params.toString());

                }
            });

        }

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
        if (reqCode == REQUEST_CHOOSE_IMAGE) {
            if (intent != null) {
                Crop.of(Uri.parse(intent.getData().toString()), imageChangeUri).withAspect(35, 15).withMaxSize(350, 150).start(this);

            }

        }
//        else if (reqCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            Glide.with(this).load(imageChangeUri).fitCenter().into(imgLogo);
//
//        } else if (reqCode == Crop.REQUEST_CROP) {
//            if (resultCode == RESULT_OK) {
//                Glide.with(this).load(imageChangeUri).fitCenter().into(imgLogo);
//                CustomSQL.setString("logo", Util.getRealPathFromURI(imageChangeUri));
//
//            } else if (resultCode == Crop.RESULT_ERROR) {
//                Util.showToast(Crop.getError(intent).getMessage());
//
//            }
//        }

        else if (reqCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            try {
                Set<BluetoothDevice> btDeviceList = mBluetoothAdapter.getBondedDevices();
                if (btDeviceList.size() > 0) {
                    for (final BluetoothDevice device : btDeviceList) {
                        Log.e("printer", device.getAddress() +"\n" + device.getName() + "\n" +device.getBluetoothClass() +"\n" + device.getBondState() +"\n" + device.getType() +"\n" + device.getUuids());

                        if (device.getAddress().equals(CustomSQL.getString(Constants.BLUETOOTH_DEVICE))){
                            connectBluetoothDevice(device, new CallbackProcess() {
                                @Override
                                public void onStart() {
                                    tvPrinterStatus.setText(Constants.CONNECTING_PRINTER);
                                    tvPrinterStatus.setOnClickListener(null);

                                }

                                @Override
                                public void onError() {
                                    tvPrinterStatus.setText("Chưa kết nối được máy in");
                                    Util.showToast(Constants.CONNECTED_PRINTER_ERROR);
                                    tvPrinterStatus.setOnClickListener(ShopCartActivity.this);
                                }

                                @Override
                                public void onSuccess(String name) {
                                    tvPrinterStatus.setText(String.format(Constants.CONNECTED_PRINTER, device.getName()));
                                    tvPrinterStatus.setOnClickListener(ShopCartActivity.this);
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
                tvPrinterStatus.setText(Constants.CONNECTING_PRINTER);
                tvPrinterStatus.setOnClickListener(null);
                break;

            case Constants.ONFAIL:
                tvPrinterStatus.setText("Chưa kết nối được máy in");
                tvPrinterStatus.setOnClickListener(this);
                break;

            case Constants.ONSUCCESS:
                tvPrinterStatus.setText(String.format(Constants.CONNECTED_PRINTER, data2));
                tvPrinterStatus.setOnClickListener(this);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        String s = currentCustomer.CustomertoString();
        Transaction.gotoPrintBillActivity(currentCustomer.CustomertoString(), DataUtil.convertListObject2Array(adapterProducts.getAllData()).toString());

        return true;
    }
}
