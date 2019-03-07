package wolve.dms.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ProductGroupActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btnBack;
    private RecyclerView rvProductGroup;
    private ProductGroupAdapter productGroupAdapter;
    private FloatingActionButton btnAddProductGroup;

    public ArrayList<ProductGroup> listProductGroup ;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_product_group;
    }

    @Override
    public int setIdContainer() {
        return R.id.product_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvProductGroup = (RecyclerView) findViewById(R.id.product_group_rvgroup);
        btnAddProductGroup =  findViewById(R.id.product_group_add_new);

    }

    @Override
    public void initialData() {
        loadProductGroup();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnAddProductGroup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
//                Transaction.gotoHomeActivityRight(true);
                onBackPressed();
                break;

            case R.id.product_group_add_new:
                openFragmentNewProductGroup(null);

                break;

        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK){
//            Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
//            if(mFragment != null && mFragment instanceof AddProdGroupFragment
//                    ||mFragment != null && mFragment instanceof AddProductFragment) {
//                getSupportFragmentManager().popBackStack();
//            }else {
//                Transaction.gotoHomeActivityRight(true);
//            }
//
//        }
//        return true;
//    }

    protected void loadProductGroup() {
        listProductGroup = new ArrayList<>();

        ProductConnect.ListProductGroup(true, new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                createRVProductGroup(result);

//                for (int i=0; i<result.length(); i++){
//                    try {
//                        ProductGroup productGroup = new ProductGroup(result.getJSONObject(i));
//                        listProductGroup.add(productGroup);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }

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
        Util.createLinearRV(rvProductGroup, productGroupAdapter);
//        rvProductGroup.setAdapter(productGroupAdapter);
//        rvProductGroup.setHasFixedSize(true);
//        rvProductGroup.setNestedScrollingEnabled(false);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        rvProductGroup.setLayoutManager(layoutManager);
    }

    private void openFragmentNewProductGroup(String productgroup){
        AddProdGroupFragment groupFragment = new AddProdGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PRODUCTGROUP, productgroup);
        changeFragment(groupFragment, bundle, true );
    }




}
