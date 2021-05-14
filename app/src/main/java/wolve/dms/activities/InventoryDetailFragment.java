package wolve.dms.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;

/**
 * Created by macos on 9/16/17.
 */

public class InventoryDetailFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tvTitle;
    private ImageView btnBack;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //private WarehouseActivity mActivity;
    private BaseModel currrentWarehouse;
    private List<RecyclerView.Adapter> listadapter;
    private ViewpagerMultiListAdapter viewpagerAdapter;
    public List<BaseModel> listInventory = new ArrayList<>();
    private CallbackObject onDataPass;
    public boolean hasChange = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inventory_detail, container, false);
        initializeView();

        currrentWarehouse = CustomSQL.getBaseModel(Constants.CURRENT_WAREHOUSE);
        tvTitle.setText(String.format("Tồn kho: %s", currrentWarehouse.getString("name")));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                intitialData();

            }
        }, 400);

        addEvent();
        return view;
    }

    private void intitialData(){
        BaseModel param = createGetParam(String.format(ApiUtil.INVENTORIES(),
                currrentWarehouse.getInt("id"),
                1),
                true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listInventory = new ArrayList<>();
                for (int i=0; i<list.size(); i++){
                    if (list.get(i).getList("inventories").size() >0){
                        listInventory.add(list.get(i));
                    }
                }

                tabLayout.setupWithViewPager(viewPager);
                setupViewPager(listInventory);
                //openFragmentInventoryEdit(curentWarehouse);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


//        String bundle = getArguments().getString(Constants.TEMPWAREHOUSE);
//        if (bundle != null) {
//            currrentWarehouse = new BaseModel(bundle);
//            tvTitle.setText(String.format("Tồn kho: %s", currrentWarehouse.getString("name")));
//
//            tabLayout.setupWithViewPager(viewPager);
//            setupViewPager();
//
//        }


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);

    }

    private void initializeView() {
        //mActivity = (WarehouseActivity) getActivity();
        btnBack = view.findViewById(R.id.icon_back);
        tvTitle = view.findViewById(R.id.inventory_detail_title);
        viewPager = view.findViewById(R.id.inventory_detail_viewpager);
        tabLayout = view.findViewById(R.id.inventory_detail_tabs);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                if (hasChange){
                    onDataPass.onResponse(new BaseModel());
                }
                getActivity().getSupportFragmentManager().popBackStack();
                break;


        }
    }

    private void setupViewPager(List<BaseModel> inventories) {
        listadapter = new ArrayList<>();
        for (int i = 0; i < inventories.size(); i++) {
            InventoryDetailAdapter productAdapters = new InventoryDetailAdapter(currrentWarehouse,
                    inventories.get(i).getList("inventories"),
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            hasChange = true;
                        }
                    });

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

        for (int i = 0; i < inventories.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(getActivity()).inflate(R.layout.view_tab_product, null);
            TextView tabTextNotify = (TextView) customView.findViewById(R.id.tabNotify);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

            textTitle.setText(inventories.get(i).getString("name"));
            if (inventories.get(i).getList("inventories").size() >0){
                tabTextNotify.setVisibility(View.VISIBLE);
                tabTextNotify.setText(String.valueOf(inventories.get(i).getList("inventories").size()));
            }else {
                tabTextNotify.setVisibility(View.GONE);
            }


            tab.setCustomView(customView);
        }


    }


}
