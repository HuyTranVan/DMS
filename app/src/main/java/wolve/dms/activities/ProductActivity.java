package wolve.dms.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.ProductAdapter;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.adapter.ViewpagerProductAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.libraries.MySwipeRefreshLayout;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ProductActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView btnBack;
    private ProductGroupAdapter productGroupAdapter;
    private FloatingActionButton btnAddProduct;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RelativeLayout lnParent;
    private MySwipeRefreshLayout swipeRefreshLayout;

    public List<BaseModel> listProductGroup;
    private List<BaseModel> listProduct;
    private ViewpagerProductAdapter viewpagerAdapter;
    private List<ProductAdapter> listadapter;
    public int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitScrollWithFullscreen.assistActivity(this, 1);
    }

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
        lnParent = findViewById(R.id.product_parent);
        lnParent.setPadding(0, 0, 0, Util.getNavigationBarHeight());
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
        btnAddProduct.setVisibility(User.getCurrentRoleId() == Constants.ROLE_ADMIN ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        btnAddProduct.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.product_add_new:
                openFragmentNewProduct(null);

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
        if (mFragment != null && mFragment instanceof NewUpdateProductFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }
    }

    protected void loadProductGroup(Boolean loading) {
        listProductGroup = new ArrayList<>();

        BaseModel param = createGetParam(ApiUtil.PRODUCT_GROUPS(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                for (int i = 0; i < list.size(); i++) {
                    listProductGroup.add(list.get(i));

                }
                DataUtil.sortProductGroup(listProductGroup, false);

                loadProduct();
            }

            @Override
            public void onError(String error) {

            }
        }, 2).execute();


    }

    public void loadProduct() {
        listProduct = new ArrayList<>();
        BaseModel param = createGetParam(ApiUtil.PRODUCTS(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                for (int i = 0; i < list.size(); i++) {
                    listProduct.add(list.get(i));

                }
                DataUtil.sortProduct(listProduct, false);
                setupViewPager(listProductGroup, listProduct);
            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }

    private void openFragmentNewProduct(String product) {
        NewUpdateProductFragment groupFragment = new NewUpdateProductFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PRODUCT, product);
        changeFragment(groupFragment, bundle, true);
    }

    private void setupViewPager(final List<BaseModel> listproductgroup, final List<BaseModel> listproduct) {
        listadapter = new ArrayList<>();

        for (int i = 0; i < listproductgroup.size(); i++) {
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

        for (int i = 0; i < listproductgroup.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(this).inflate(R.layout.view_tab_product, null);
            //LinearLayout tabParent =  customView.findViewById(R.id.tabParent);
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

    protected void reupdateProduct(BaseModel product){
        boolean check = false;
        for (int i=0; i<listadapter.size(); i++){
            List<BaseModel> listItem = listadapter.get(i).getmData();
            for (int ii=0; ii<listItem.size(); ii++){
                if (product.getInt("id") == listItem.get(ii).getInt("id")){
                    listadapter.get(i).notifyItem(product, ii);
                    check = true;
                    break;

                }

            }
            if (check){
                break;
            }
        }

    }
}
