package wolve.dms.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.ProductAdapter;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;

/**
 * Created by macos on 9/16/17.
 */

public class ProductActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btnBack;
    private RecyclerView rvProduct, rvProductGroup;
    private ProductGroupAdapter productGroupAdapter;
    private ImageView btnAddGroup, btnAddProduct;

    private ProductAdapter productAdapter;
    public ArrayList<ProductGroup> listProductGroup ;
    private ArrayList<Product> listProduct ;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_product;
    }

    @Override
    public int setIdContainer() {
        return R.id.product_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvProduct = (RecyclerView) findViewById(R.id.product_rvproduct);
        rvProductGroup = (RecyclerView) findViewById(R.id.product_rvproductgroup);
        btnAddGroup = (ImageView) findViewById(R.id.product_addgroup);
        btnAddProduct = (ImageView) findViewById(R.id.product_addproduct);


    }

    @Override
    public void initialData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductGroup();
    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnAddGroup.setOnClickListener(this);
        btnAddProduct.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                Transaction.gotoHomeActivityRight(true);
                break;

            case R.id.product_addgroup:
                openFragmentNewProductGroup(null);

                break;

            case R.id.product_addproduct:
                openFragmentNewProduct(null);

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
            if(mFragment != null && mFragment instanceof AddProdGroupFragment
                    ||mFragment != null && mFragment instanceof AddProductFragment) {
                getSupportFragmentManager().popBackStack();
            }else {
                Transaction.gotoHomeActivityRight(true);
            }

        }
        return true;
    }

    protected void loadProductGroup() {
        ProductConnect.ListProductGroup(new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                createRVProductGroup(result);

                loadProduct();

            }

            @Override
            public void onError(String error) {

            }
        }, false);
    }

    protected void loadProduct() {
        ProductConnect.ListProduct(new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                createRVProduct(result);

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void createRVProductGroup(JSONArray jsonArray){
        listProductGroup = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++){
            try {
                ProductGroup productGroup = new ProductGroup(jsonArray.getJSONObject(i));
                listProductGroup.add(productGroup);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        productGroupAdapter = new ProductGroupAdapter(listProductGroup, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                openFragmentNewProductGroup(data);
            }

        }, new CallbackDeleteAdapter() {
            @Override
            public void onDelete(String data, int position) {
                loadProductGroup();
            }
        });
        rvProductGroup.setAdapter(productGroupAdapter);
        rvProductGroup.setHasFixedSize(true);
        rvProductGroup.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvProductGroup.setLayoutManager(layoutManager);
    }

    private void createRVProduct(JSONArray jsonArray){
        listProduct = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++){
            try {
                Product product = new Product(jsonArray.getJSONObject(i));
                listProduct.add(product);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        productAdapter = new ProductAdapter(listProduct, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                openFragmentNewProduct(data);
            }

        }, new CallbackDeleteAdapter() {
            @Override
            public void onDelete(String data, int position) {
                loadProductGroup();
            }
        });
        rvProduct.setAdapter(productAdapter);
        rvProduct.setHasFixedSize(true);
        rvProduct.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvProduct.setLayoutManager(layoutManager);
    }

    private void openFragmentNewProductGroup(String productgroup){
        AddProdGroupFragment groupFragment = new AddProdGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PRODUCTGROUP, productgroup);
        changeFragment(groupFragment, bundle, true );
    }

    private void openFragmentNewProduct(String product){
        AddProductFragment groupFragment = new AddProductFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PRODUCT, product);
        changeFragment(groupFragment, bundle, true );
    }


}
