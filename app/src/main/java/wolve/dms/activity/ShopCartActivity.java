package wolve.dms.activity;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CartProductsAdapter;
import wolve.dms.adapter.CartPromotionsAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.Product;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDialog;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ShopCartActivity extends BaseActivity implements  View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ImageView btnBack;
    private Button btnSubmit;
    private TextView tvTitle, tvTotal;
    private RadioGroup rgPay;
    private RadioButton rdCash, rdDebt;
    private RecyclerView rvProducts, rvPromotions;
    private FloatingActionButton btnAdd, btnAddPromotion;


    private CartProductsAdapter adapterProducts;
    private CartPromotionsAdapter adapterPromotions;
    private Customer currentCustomer;
    private List<Product> listProducts = new ArrayList<>();
    private List<Product> listCurrentProduct = new ArrayList<>();
    private List<Product> listCurrentPromotion = new ArrayList<>();

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
        btnSubmit = (Button) findViewById(R.id.cart_submit);
        btnBack = (ImageView) findViewById(R.id.icon_back);
        tvTitle = (TextView) findViewById(R.id.cart_title);
        tvTotal = (TextView) findViewById(R.id.cart_total);
        btnAdd = (FloatingActionButton) findViewById(R.id.cart_add_product);
        btnAddPromotion = (FloatingActionButton) findViewById(R.id.cart_add_promotion);
        rvProducts = (RecyclerView) findViewById(R.id.cart_rvproduct);
        rvPromotions = (RecyclerView) findViewById(R.id.cart_rvpromotion);

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

        if (listCurrentProduct.size() <=0){
            showDialogProduct("CHỌN SẢN PHẨM BÁN", false);
        }
        createRVProduct(listCurrentProduct);
        createRVPromotion(listCurrentPromotion);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnAddPromotion.setOnClickListener(this);

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
                submitBill();
                break;

            case R.id.cart_add_product:
                showDialogProduct("CHON SẢN PHẨM BÁN", false);
                break;

            case R.id.cart_add_promotion:
                showDialogProduct("CHON SẢN PHẨM TẶNG KÈM", true);
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
        adapterProducts = new CartProductsAdapter(list, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                updatePrice(listCurrentProduct);

//                rvProducts.requestLayout();
//                rvProducts.invalidate();
            }
        }, new CallbackDeleteAdapter() {
            @Override
            public void onDelete(String data, int position) {

                listCurrentProduct.remove(position);
                updatePrice(listCurrentProduct);

                rvProducts.requestLayout();
                rvProducts.invalidate();
            }
        });
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


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.customer_radio_status_interested:

                break;

        }
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
        CustomDialog.showDialogChoiceProduct(title ,listProducts,isPromotion, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                for (int i=0; i<listProducts.size(); i++){
                    if (listProducts.get(i).getBoolean("checked")){
                        try {
                            Product product = new Product();
                            product = listProducts.get(i);
                            product.put("isPromotion", isPromotion);
                            if (isPromotion){
                                product.put("isPromotion", isPromotion);
                                product.put("totalMoney",0);
                                product.put("discount", product.getDouble("unitPrice"));
                                adapterPromotions.addItem(product);
                                listCurrentPromotion.add(product);
                            }else {
                                adapterProducts.addItem(product);
                                listCurrentProduct.add(product);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
//                updatePrice(listCurrentProduct);

            }
        });
    }

    private void submitBill(){
        try {
            JSONObject params = new JSONObject();
            params.put("debt", 0);
            params.put("total", Util.valueMoney(tvTotal.getText().toString()));
            params.put("paid", Util.valueMoney(tvTotal.getText().toString()));
            params.put("customerId", currentCustomer.getInt("id"));
            params.put("distributorId", Distributor.getCurrentDistributorId());
            params.put("userId", User.getCurrentUserId());

            JSONArray array = new JSONArray();
            for (int i=0; i< listProducts.size(); i++){
                JSONObject object = new JSONObject();
                object.put("productId", listProducts.get(i).getInt("id"));
                object.put("discount", listProducts.get(i).getDouble("discount"));
                object.put("quantity", listProducts.get(i).getDouble("quantity"));

                array.put(object);
            }
            params.put("billDetails", (Object) array);

            CustomerConnect.PostBill(params.toString(), new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {
                    returnPreviousScreen(currentCustomer);
                }

                @Override
                public void onError(String error) {

                }
            }, true);



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updatePrice(List<Product> list){
        Double sum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).getDouble("totalMoney");

        }
        tvTotal.setText(Util.FormatMoney(sum));
    }






}
