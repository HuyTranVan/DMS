package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.adapter.ViewpagerShopcartAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ChoiceProductFragment extends Fragment implements View.OnClickListener {
    private View view;
//    private ImageView btnBack;
    private TextView tvSubmit;
    private LinearLayout btnSubmit ;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ShopCartActivity mActivity;
    private CartProductDialogAdapter adapter;
    private ProductGroup productGroup;
    private ViewpagerShopcartAdapter viewpagerAdapter;
    private int currentPosition =0;
    private List<CartProductDialogAdapter> listadapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart_choice_product,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager();

    }

    private void addEvent() {
        btnSubmit.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (ShopCartActivity) getActivity();
        tvSubmit = view.findViewById(R.id.cart_choice_tvsubmit);
        btnSubmit =  view.findViewById(R.id.cart_choice_submit);
        viewPager = view.findViewById(R.id.cart_choice_viewpager);
        tabLayout = view.findViewById(R.id.cart_choice_tabs);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                Util.hideKeyboard(v);
                break;

            case R.id.cart_choice_submit:
                submitProduct();
                break;

        }
    }

    private void setupViewPager( ){
        listadapter  = new ArrayList<>();

        for (int  i=0; i<mActivity.listProductGroups.size(); i++){
            CartProductDialogAdapter productAdapters = new CartProductDialogAdapter(mActivity.listInitialProduct, mActivity.listProducts, i,mActivity.listProductGroups.get(i), new CartProductDialogAdapter.CallbackViewPager() {
                @Override
                public void onChoosen(int position, int count) {
                    reloadBillCount(position, count);
                    showSubmitEvent();
                }
            });

            listadapter.add(productAdapters);

        }

        viewpagerAdapter = new ViewpagerShopcartAdapter(listadapter, mActivity.listProductGroups);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(3);

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

        for (int i=0; i<mActivity.listProductGroups.size(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(mActivity).inflate(R.layout.view_tab_product, null);
            TextView tabTextTitle = (TextView) customView.findViewById(R.id.tabNotify);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

            textTitle.setText(mActivity.listProductGroups.get(i).getString("name"));
            tabTextTitle.setVisibility(View.GONE);

            tab.setCustomView(customView);
        }


    }


    protected void  submitProduct(){
        List<BaseModel> listChecked = new ArrayList<>();

        for (int i=0; i<listadapter.size(); i++){
            List<BaseModel> listTemp = new ArrayList<>();
            listTemp = listadapter.get(i).getAllData();
            for (int j=0; j<listTemp.size(); j++){
                if (listTemp.get(j).getBoolean("checked")){
                    listChecked.add(listTemp.get(j));
                }
            }
        }



        mActivity.updatelistProduct(listChecked);
        getActivity().getSupportFragmentManager().popBackStack();

    }

    private void reloadBillCount(int position, int count) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View customView = LayoutInflater.from(mActivity).inflate(R.layout.view_tab_product, null);
        TextView tabTextTitle = (TextView) customView.findViewById(R.id.tabNotify);
        TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

        textTitle.setText(mActivity.listProductGroups.get(position).getString("name"));
        if (count <= 0) {
            tabTextTitle.setVisibility(View.GONE);
        } else {
            tabTextTitle.setVisibility(View.VISIBLE);
            tabTextTitle.setText(String.valueOf(count));
        }
        tab.setCustomView(null);
        tab.setCustomView(customView);

    }

    private void showSubmitEvent(){
        int count = 0;
        for (int i=0; i<listadapter.size(); i++){
            count += listadapter.get(i).getCount();
        }

        btnSubmit.setVisibility(count == 0?View.GONE: View.VISIBLE);
        tvSubmit.setText(String.format("Thêm vào giỏ hàng (%d)", count));
    }


}
