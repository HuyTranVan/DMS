package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.CartProductDialogAdapter;
import wolve.dms.adapter.InventoryDetailAdapter;
import wolve.dms.adapter.ViewpagerMultiListAdapter;
import wolve.dms.adapter.ViewpagerShopcartAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class InventoryDetailFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tvTitle;
    private ImageView btnBack;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private WarehouseActivity mActivity;
    private BaseModel currrentWarehouse;
    private List<RecyclerView.Adapter> listadapter;
    private ViewpagerMultiListAdapter viewpagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inventory_detail, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.TEMPWAREHOUSE);
        if (bundle != null) {
            currrentWarehouse = new BaseModel(bundle);
            tvTitle.setText(String.format("Tồn kho: %s", currrentWarehouse.getString("name")));

            tabLayout.setupWithViewPager(viewPager);
            setupViewPager();

        }


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (WarehouseActivity) getActivity();
        btnBack = view.findViewById(R.id.icon_back);
        tvTitle = view.findViewById(R.id.inventory_detail_title);
        viewPager = view.findViewById(R.id.inventory_detail_viewpager);
        tabLayout = view.findViewById(R.id.inventory_detail_tabs);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;


        }
    }


//    private void createRVInventory(List<BaseModel> list) {
//        adapter = new InventoryDetailAdapter(currrentWarehouse, list);
//        Util.createLinearRV(rvInventory, adapter);
//    }

    private void setupViewPager() {
        listadapter = new ArrayList<>();

        for (int i = 0; i < mActivity.listInventoryDetail.size(); i++) {
            InventoryDetailAdapter productAdapters = new InventoryDetailAdapter(currrentWarehouse,
                    mActivity.listInventoryDetail.get(i).getList("inventories"));

            listadapter.add(productAdapters);

        }

        viewpagerAdapter = new ViewpagerMultiListAdapter(listadapter, null, null, null);
        viewPager.setAdapter(viewpagerAdapter);
        //viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0; i < mActivity.listInventoryDetail.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(mActivity).inflate(R.layout.view_tab_product, null);
            TextView tabTextNotify = (TextView) customView.findViewById(R.id.tabNotify);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

            textTitle.setText(mActivity.listInventoryDetail.get(i).getString("name"));
            if (mActivity.listInventoryDetail.get(i).getList("inventories").size() >0){
                tabTextNotify.setVisibility(View.VISIBLE);
                tabTextNotify.setText(String.valueOf(mActivity.listInventoryDetail.get(i).getList("inventories").size()));
            }else {
                tabTextNotify.setVisibility(View.GONE);
            }


            tab.setCustomView(customView);
        }


    }


}
