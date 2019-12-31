package wolve.dms.activities;

import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CartProductsAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ShopCartActivity extends BaseActivity implements  View.OnClickListener,  View.OnLongClickListener {
    private ImageView btnBack;
    private Button btnSubmit;
    private TextView tvTitle, tvTotal, tvBDF, tvAddress;
    private CInputForm tvNote;
    private RecyclerView rvProducts;
    private RelativeLayout rlCover;
    private LinearLayout lnSubmitGroup;
    private FloatingActionButton btnAddProduct;

    private CartProductsAdapter adapterProducts;
    private BaseModel currentCustomer;
    protected List<BaseModel> listInitialProduct = new ArrayList<>();
    protected List<BaseModel> listProducts = new ArrayList<>();
    protected List<BaseModel> listProductGroups = new ArrayList<>();
    protected List<BaseModel> listBillDetail = new ArrayList<>();
    private String debt;


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
        tvBDF = findViewById(R.id.cart_bdf);
        tvNote =  findViewById(R.id.cart_note);
        lnSubmitGroup = findViewById(R.id.cart_submit_group);
        rvProducts =  findViewById(R.id.cart_rvproduct);
        rlCover =  findViewById(R.id.cart_cover);
        btnAddProduct = findViewById(R.id.add_product);
        tvAddress = findViewById(R.id.cart_address);

    }

    @Override
    public void initialData() {
        Util.shopCartActivity = this;
        currentCustomer = CustomSQL.getBaseModel(Constants.CUSTOMER);
        debt = getIntent().getExtras().getString(Constants.ALL_DEBT);
        listBillDetail = DataUtil.array2ListObject(CustomSQL.getString(Constants.BILL_DETAIL));

        lnSubmitGroup.setVisibility(View.GONE);
        rlCover.setVisibility(View.VISIBLE);
        tvTitle.setText(String.format("%s %s",Constants.getShopName(currentCustomer.getString("shopType") ), currentCustomer.getString("signBoard").toUpperCase() ));

        btnSubmit.setText(CustomSQL.getLong(Constants.CHECKIN_TIME) != 0 ? "in và thanh toán" : "lưu hóa đơn");

        tvAddress.setText(String.format(Constants.addressFormat,
                currentCustomer.getString("address"),
                currentCustomer.getString("street"),
                currentCustomer.getString("district"),
                currentCustomer.getString("province")));

//        if (!CustomSQL.getString("logo").equals("")){
//            Glide.with(this).load(CustomSQL.getString("logo")).centerCrop().into(imgLogo);
//
//        }else {
//            new DownloadImage(this, imgLogo, Api_link.LOGO_BILL).execute();
//        }
        tvBDF.setVisibility(listInitialProduct.size() ==0? View.GONE :View.VISIBLE);
        loadListProduct();
        loadListProductGroup();
        createRVProduct(listInitialProduct);


        if(listInitialProduct.size() ==0){
            changeFragment(new ChoiceProductFragment() , true);
        }

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSubmit.setOnLongClickListener(this);
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
            Transaction.returnPreviousActivity(Constants.SHOP_CART_ACTIVITY, new BaseModel(), Constants.RESULT_SHOPCART_ACTIVITY);

        }

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                onBackPressed();
//                Transaction.returnCustomerActivity(currentCustomer);

                break;

            case R.id.cart_submit:
                if (CustomSQL.getLong(Constants.CHECKIN_TIME) != 0){
                    gotoPrintBill();

                }else {
                    postBillAndSave();
                }

                break;

//            case R.id.cart_logo:
//                imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
//                gotoImageChooser();
//                break;


            case R.id.add_product:
                changeFragment(new ChoiceProductFragment() , true);

                break;
        }
    }

    private void loadListProduct(){
        List<BaseModel> all = Product.getProductList();
        for (int i=0 ; i<all.size(); i++){
            BaseModel product = all.get(i);
            product.put("checked", false);
            listProducts.add(product);

        }

        DataUtil.sortProduct(listProducts, false);
    }

    private void loadListProductGroup(){
        listProductGroups = ProductGroup.getProductGroupList();

    }

    private void createRVProduct(final List<BaseModel> list){
        adapterProducts = new CartProductsAdapter(list, listBillDetail, new CallbackChangePrice() {
            @Override
            public void NewPrice(Double price) {
                tvTotal.setText(Util.FormatMoney(price));

                rvProducts.requestLayout();
                rvProducts.invalidate();

                lnSubmitGroup.setVisibility(adapterProducts.getAllDataProduct().size()>0 ? View.VISIBLE : View.GONE);
                rlCover.setVisibility(adapterProducts.getAllDataProduct().size()>0 ? View.GONE : View.VISIBLE);

                updateBDFValue();
            }
        }) ;
        Util.createLinearRV(rvProducts, adapterProducts);

    }

    protected void updatelistProduct(List<BaseModel> list_product){
        for (int i=0; i<list_product.size(); i++){
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

        }
        updateBDFValue();


    }

    private void updateBDFValue(){
        if (adapterProducts.getAllDataProduct().size() >0 ){
            tvBDF.setVisibility(View.VISIBLE );
            tvBDF.setText(String.format("BDF:%s ", DataUtil.defineBDFPercent(adapterProducts.getAllData())) +"%");

        }else {
            tvBDF.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent intent) {
        super.onActivityResult(reqCode, resultCode, intent);
        Util.getInstance().setCurrentActivity(this);
        if (reqCode == Constants.RESULT_PRINTBILL_ACTIVITY){
            BaseModel data = new BaseModel(intent.getStringExtra(Constants.PRINT_BILL_ACTIVITY));
            if (data.hasKey(Constants.RELOAD_DATA) && data.getBoolean(Constants.RELOAD_DATA)){
                Transaction.returnPreviousActivity(Constants.SHOP_CART_ACTIVITY,data , Constants.RESULT_SHOPCART_ACTIVITY);

            }

        }

    }

    @Override
    public boolean onLongClick(View v) {
        if (User.getRole().equals(Constants.ROLE_ADMIN)){
            gotoPrintBill();

        }

        return true;
    }

    private void postBillAndSave(){
        final String params = DataUtil.createPostBillParam(currentCustomer.getInt("id"),
                User.getId(),
                adapterProducts.totalPrice(),
                0.0,
                adapterProducts.getAllData(),
                "",
                0,
                0);

        CustomerConnect.PostBill(params, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                BaseModel modelResult = new BaseModel();
                modelResult.put(Constants.RELOAD_DATA, true);
                Transaction.returnPreviousActivity(Constants.SHOP_CART_ACTIVITY,modelResult,Constants.RESULT_SHOPCART_ACTIVITY);
            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
            }

        }, true);


    }

    private String createNoteTemp(){
        JSONObject noteObject = new JSONObject();
        try {
            noteObject.put(Constants.TEMPBILL, true);

        } catch (JSONException e) {
            return "";
        }

        return Security.encrypt(noteObject.toString());
    }

    private void gotoPrintBill(){
        BaseModel bill = new BaseModel();
        bill.put("id",0);
        bill.putBaseModel("user", User.getCurrentUser());
        bill.put("total", adapterProducts.totalPrice());
        bill.put("debt", adapterProducts.totalPrice());
        bill.putList(Constants.BILL_DETAIL,adapterProducts.getAllData() );

        Transaction.gotoPrintBillActivity(bill, false);
    }


}
