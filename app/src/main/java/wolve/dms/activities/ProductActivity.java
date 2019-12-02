package wolve.dms.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.ProductAdapter;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.adapter.ViewpagerProductAdapter;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.libraries.MySwipeRefreshLayout;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;

/**
 * Created by macos on 9/16/17.
 */

public class ProductActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{
    private ImageView btnBack;
    private ProductGroupAdapter productGroupAdapter;
    private FloatingActionButton btnAddProduct;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MySwipeRefreshLayout swipeRefreshLayout;

    private ProductAdapter productAdapter;
    public List<BaseModel> listProductGroup ;
    private List<BaseModel> listProduct ;
    private ViewpagerProductAdapter viewpagerAdapter;
    public int currentPosition =0;

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
        btnAddProduct = (FloatingActionButton) findViewById(R.id.product_add_new);
        viewPager = (ViewPager) findViewById(R.id.product_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.product_tabs);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.product_swipelayout);

    }

    @Override
    public void initialData() {
        loadProductGroup(true);
        tabLayout.setupWithViewPager(viewPager);
        btnAddProduct.setVisibility(User.getRole().equals(Constants.ROLE_ADMIN) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        btnAddProduct.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.product_add_new:
                openFragmentNewProduct(null);

                break;
        }
    }

    protected void loadProductGroup(Boolean loading) {
        listProductGroup = new ArrayList<>();

        ProductConnect.ListProductGroup(loading, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                for (int i=0; i<results.size(); i++){
                    listProductGroup.add(results.get(i));

                }
                DataUtil.sortProductGroup(listProductGroup, false);

                loadProduct();
            }

            @Override
            public void onError(String error) {

            }
        }, false);
    }

    public void loadProduct() {
        listProduct = new ArrayList<>();
        ProductConnect.ListProduct(new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                for (int i=0; i<results.size(); i++){
                    listProduct.add(results.get(i));

                }
                DataUtil.sortProduct(listProduct, false);
                setupViewPager(listProductGroup , listProduct);
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void openFragmentNewProduct(String product){
        AddProductFragment groupFragment = new AddProductFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PRODUCT, product);
        changeFragment(groupFragment, bundle, true );
    }

    private void setupViewPager(final List<BaseModel> listproductgroup, final List<BaseModel> listproduct){

        final List<RecyclerView.Adapter> listadapter = new ArrayList<>();

        for (int  i=0; i<listproductgroup.size(); i++){
            ProductAdapter productAdapters = new ProductAdapter(listproductgroup.get(i), listproduct, new CallbackClickAdapter() {
                @Override
                public void onRespone(String data, int position) {
                    openFragmentNewProduct(data);
                }

            }, new CallbackDeleteAdapter() {
                @Override
                public void onDelete(String data, int position) {
                    loadProductGroup(true);
                }
            });

            listadapter.add(productAdapters);
        }

        viewpagerAdapter = new ViewpagerProductAdapter(listadapter, listproductgroup);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i=0; i<listproductgroup.size(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(this).inflate(R.layout.view_tab_product, null);
            TextView tabTextTitle = (TextView) customView.findViewById(R.id.tabNotify);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

            textTitle.setText(listproductgroup.get(i).getString("name"));
            tabTextTitle.setVisibility(View.GONE);

//            if (listadapter.get(i).getItemCount() <=0){
//                tabTextTitle.setVisibility(View.GONE);
//            }else {
//                tabTextTitle.setVisibility(View.VISIBLE);
//                tabTextTitle.setText(String.valueOf(listadapter.get(i).getItemCount()));
//            }

//

            tab.setCustomView(customView);
        }

        swipeRefreshLayout.setRefreshing(false);

    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2196f3"));
        loadProductGroup(false);
    }
}
