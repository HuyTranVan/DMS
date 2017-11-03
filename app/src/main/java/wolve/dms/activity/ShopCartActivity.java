package wolve.dms.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.adapter.CartProductsAdapter;
import wolve.dms.adapter.CartPromotionsAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackChangePrice;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackListProduct;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.controls.CInputForm;
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

public class ShopCartActivity extends BaseActivity implements  View.OnClickListener {
    private ImageView btnBack;
    private Button btnSubmit;
    private TextView tvTitle, tvTotal;
    private CInputForm tvNote;
    private RecyclerView rvProducts, rvPromotions;
    private FloatingActionButton btnAdd, btnAddPromotion;
    private RelativeLayout rlCover;
    private DialogPlus dialog;

    private DatePickerDialog fromDatePickerDialog;

    private CartProductsAdapter adapterProducts;
    private CartPromotionsAdapter adapterPromotions;
    private Customer currentCustomer;
    private List<Product> listProducts = new ArrayList<>();
    private List<Product> listInitialProduct = new ArrayList<>();
    private List<Product> listInitialPromotion = new ArrayList<>();

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
        tvNote = (CInputForm) findViewById(R.id.cart_note);
        btnAdd = (FloatingActionButton) findViewById(R.id.cart_add_product);
        btnAddPromotion = (FloatingActionButton) findViewById(R.id.cart_add_promotion);
        rvProducts = (RecyclerView) findViewById(R.id.cart_rvproduct);
        rvPromotions = (RecyclerView) findViewById(R.id.cart_rvpromotion);
        rlCover = (RelativeLayout) findViewById(R.id.cart_cover);

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

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnAddPromotion.setOnClickListener(this);
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
//                choicePayMethod();
                createBillImage();

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
        CustomDialog.showDialogChoiceProduct(title ,listProducts,isPromotion, new CallbackListProduct() {
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
        dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.view_choice_pay_method))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.colorwhite_corner)
                .setMargin(10,10,10,10)
                .setPadding(20,30,20,20)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        LinearLayout lnCash = (LinearLayout) dialog.findViewById(R.id.view_pay_method_cash);
        LinearLayout lnDebt = (LinearLayout) dialog.findViewById(R.id.view_pay_method_debt);

        lnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                submitBill(Util.valueMoney(tvTotal) , Util.valueMoney(tvTotal));

            }
        });

        lnDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                showDialogInputPaid();
            }
        });

        dialog.show();
    }

    private void createBillImage(){
        CustomDialog.showDialogBillImage(currentCustomer,tvTotal.getText().toString(), getListProduct(), new CallbackPayBill() {
            @Override
            public void OnRespone(Double total, Double pay) {

            }
        });
    }


    private void showDialogInputPaid() {
        CustomDialog.showDialogInputPaid("Nhập số tiến khách trả", "Tổng tiền hóa đơn", Util.valueMoney(tvTotal.getText().toString()), new CallbackPayBill() {
                    @Override
                    public void OnRespone(Double total, Double pay) {
                        submitBill(total, pay);
                    }
                });
    }




}
