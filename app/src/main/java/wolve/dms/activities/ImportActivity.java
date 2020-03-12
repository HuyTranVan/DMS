package wolve.dms.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.apache.http.client.UserTokenHandler;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.ProductImportAdapter;
import wolve.dms.adapter.ProductImportChoosenAdapter;
import wolve.dms.adapter.ProductInventoryAdapter;
import wolve.dms.adapter.ViewpagerMultiListAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ImportActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btnBack;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView tvTitle;
    private CTextIcon  tvFromWarehouse,tvToWarehouse;
    private Button btnSubmit;
    private RelativeLayout coParent;

    private List<BaseModel> listWarehouse;
    private BaseModel fromWarehouse, toWarehouse;
    private ViewpagerMultiListAdapter viewpagerAdapter;
    private List<RecyclerView.Adapter>  listadapter;
    private ProductImportAdapter adapterProduct;
    private ProductImportChoosenAdapter adapterChoosen;
    private ProductInventoryAdapter adapterInventory;
    private List<BaseModel> listCurrentInventory = new ArrayList<>();
    private int currentPosition =0;
    private String[] titles = new String[]{"DANH SÁCH", "ĐÃ CHỌN", "TỒN KHO "};
    private boolean[] searches = new boolean[]{true, false, false};



    @Override
    public int getResourceLayout() {
        return R.layout.activity_import;
    }

    @Override
    public int setIdContainer() {
        return R.id.import_parent;
    }

    @Override
    public void findViewById() {
        coParent = findViewById(R.id.import_parent);
        btnBack = (ImageView) findViewById(R.id.icon_back);
        viewPager = findViewById(R.id.import_viewpager);
        tabLayout = findViewById(R.id.import_tabs);
        btnSubmit = findViewById(R.id.import_submit);
        tvFromWarehouse = findViewById(R.id.import_fromwarehouse);
        tvToWarehouse = findViewById(R.id.import_towarehouse);
        tvTitle = findViewById(R.id.import_title);

    }

    @Override
    public void initialData() {
        listWarehouse = new ArrayList<>();
        toWarehouse = new BaseModel(getIntent().getStringExtra(Constants.WAREHOUSE));

        tvTitle.setText("nhập kho");
        tvToWarehouse.setText(Util.getStringIcon(toWarehouse.getString("name"), "   ", R.string.icon_down));

        tabLayout.setupWithViewPager(viewPager);

        setupViewPager(new ArrayList<>());
        getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                updateInventoryTitle(list);
                if (list.size()>0){
                    viewPager.setCurrentItem(2, true);
                }


            }
        });

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        if (!getIntent().getBooleanExtra(Constants.FLAG,false )){
            tvToWarehouse.setOnClickListener(this);
        }
        tvFromWarehouse.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.import_submit:
                submitEvent();
                break;

            case R.id.import_fromwarehouse:
                selectFromWarehouse();

                break;

            case R.id.import_towarehouse:
                selectToWarehouse();

                break;

        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.import_parent);
        if (Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);
        }else if(mFragment != null && mFragment instanceof NewUpdateDepotFragment) {

        }else {
            Transaction.returnPreviousActivity();
        }
    }

    private void setupViewPager(List<BaseModel> listProduct ){
        listadapter  = new ArrayList<>();
        adapterProduct = new ProductImportAdapter(listProduct, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                adapterChoosen.insertData(object);
            }
        });

        adapterChoosen = new ProductImportChoosenAdapter(coParent, new ArrayList<>(), new CallbackString() {
            @Override
            public void Result(String s) {
                updateChoosenTitle(Integer.parseInt(s));
                btnSubmit.setVisibility(Integer.parseInt(s) > 0 && currentPosition == 1 ? View.VISIBLE : View.GONE);
            }
        }, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                adapterProduct.updateData(object);
            }
        });

        adapterInventory = new ProductInventoryAdapter(listCurrentInventory);

        listadapter.add(adapterProduct);
        listadapter.add(adapterChoosen);
        listadapter.add(adapterInventory);

        String[] mTitles = new String[]{titles[0], titles[1], "TỒN KHO " +toWarehouse.getString("name")};
        viewpagerAdapter = new ViewpagerMultiListAdapter(listadapter, mTitles, searches, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                if (position ==0){
                    adapterProduct.getFilter().filter(data);
                }

            }
        });
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(3);

        for (int i=0; i<mTitles.length; i++){
            updateTabNotify(i, mTitles[i],0);

        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                Util.hideKeyboard(viewPager);
                if (position ==1 && adapterChoosen.getItemCount() >0){
                    btnSubmit.setVisibility(View.VISIBLE);
                }else {
                    btnSubmit.setVisibility(View.GONE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void updateTabNotify(int position, String title, int number){
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View customView = LayoutInflater.from(ImportActivity.this).inflate(R.layout.view_tab_product, null);
        TextView tabTextNotify = (TextView) customView.findViewById(R.id.tabNotify);
        TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

        textTitle.setText(title);
        if (number >0){
            tabTextNotify.setVisibility(View.VISIBLE);
            tabTextNotify.setText(String.valueOf(number));
        }else {
            tabTextNotify.setVisibility(View.GONE);
        }

        tab.setCustomView(null);
        tab.setCustomView(customView);
    }

    private void submitEvent(){
        if (fromWarehouse.getInt("isMaster") != 1){
            String param = DataUtil.createPostImportParam(toWarehouse.getInt("id"),
                    fromWarehouse.getInt("id"),
                    adapterChoosen.getmData(),
                    "");
            postImport(param, null);

        }else {
            if (toWarehouse.getInt("isMaster") == 2){
                String param = DataUtil.createPostImportParam(toWarehouse.getInt("id"),
                        fromWarehouse.getInt("id"),
                        adapterChoosen.getmData(),
                        "");
                postImport(param, null);

            }else {
                CustomCenterDialog.alertWithCancelButton(null,
                        "Thực hiện tự động nhập từ kho tổng sang kho tạm trước khi nhập vào kho nhân viên",
                        "Tiếp tục",
                        "hủy", new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                if (result){

                                    CustomBottomDialog.choiceListObject("CHỌN KHO TẠM TRUNG GIAN",
                                            filterTempWarehouse(listWarehouse),
                                            "name",
                                            new CallbackBaseModel() {
                                        @Override
                                        public void onResponse(BaseModel object) {

                                            String param1 = DataUtil.createPostImportParam(object.getInt("id"),
                                                    fromWarehouse.getInt("id"),
                                                    adapterChoosen.getmData(),
                                                    "");

                                            postImport(param1, new CallbackBoolean() {
                                                @Override
                                                public void onRespone(Boolean result) {
                                                    if (result){
                                                        String param2 = DataUtil.createPostImportParam(toWarehouse.getInt("id"),
                                                                object.getInt("id"),
                                                                adapterChoosen.getmData(),
                                                                "");

                                                        postImport(param2, null);


                                                    }
                                                }
                                            });

                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });

                                }
                            }
                        }
                );

            }

        }



    }

    private void dialogSelectWarehouse(List<BaseModel> list, CallbackObject listener){
        CustomBottomDialog.choiceListObject("CHỌN KHO HÀNG", list, "name", new CallbackBaseModel() {
            @Override
            public void onResponse(BaseModel object) {
                listener.onResponse(object);

            }

            @Override
            public void onError() {

            }
        });

    }

    private void getListWarehouse(CallbackListObject listener ){
        if (listWarehouse.size() ==0){
            SystemConnect.ListWarehouse(true, new CallbackCustomList() {
                @Override
                public void onResponse(List<BaseModel> results) {
                    listWarehouse = results;
                    listener.onResponse(results);

                }
                @Override
                public void onError(String error) {

                }
            }, true);


        }else {
            listener.onResponse(listWarehouse);

        }

    }

    private void getInventory(int warehouse_id, CallbackListObject listener){
        SystemConnect.GetInventoryList(warehouse_id, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                listener.onResponse(result);

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void selectFromWarehouse(){
        //todo: show all include Master Warehouse
        getListWarehouse(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {

                dialogSelectWarehouse(DataUtil.removeObjectFromList(list, toWarehouse, "id"), new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        viewPager.setCurrentItem(0, true);
                        fromWarehouse = object;
                        tvFromWarehouse.setText(Util.getStringIcon(object.getString("name"),"   ",R.string.icon_down));

                        if (object.getInt("isMaster") ==1 ){
                            adapterProduct.updateData(Product.getProductList());
                            adapterChoosen.emptyList();
                            updateChoosenTitle(0);

                        }else {
                            getInventory(object.getInt("id"), new CallbackListObject() {
                                @Override
                                public void onResponse(List<BaseModel> list) {
                                    adapterProduct.updateData(list);
                                    adapterChoosen.emptyList();
                                    updateChoosenTitle(0);

                                }
                            });

                        }

                    }
                });


            }
        });

    }

    private void selectToWarehouse(){
        //todo: show all include Master Warehouse
        getListWarehouse(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {

                dialogSelectWarehouse(filterListToWarehouse(list), new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        toWarehouse = object;
                        tvToWarehouse.setText(Util.getStringIcon(object.getString("name"),"   ", R.string.icon_down));

                        reloadAllWarehouse();

                    }
                });


            }
        });


    }

    private List<BaseModel> filterListToWarehouse(List<BaseModel> list){
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model: list){
            if (model.getInt("isMaster") !=1){
                if (Util.isAdmin()){
                    results.add(model);

                }else if (model.getInt("user_id") == User.getId()){
                    results.add(model);
                }

            }
        }

        return results;
    }

    private List<BaseModel> filterTempWarehouse(List<BaseModel> list){
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model: list){
            if (model.getInt("isMaster") ==2){
                results.add(model);


            }
        }

        return results;
    }

    private void updateInventoryTitle(List<BaseModel> list){
        listCurrentInventory = list;
        adapterInventory.updateData(list);
        updateTabNotify(2, titles[2]+ toWarehouse.getString("name").toUpperCase(), list.size());
    }

    private void updateChoosenTitle(int number){
        updateTabNotify(1, titles[1], number);
    }

    private void postImport(String param, CallbackBoolean listener){
        CustomerConnect.PostImport(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                if (listener != null){
                    listener.onRespone(true);

                }else {
                    reloadAllWarehouse();
                    viewPager.setCurrentItem(2, true);
                }


            }

            @Override
            public void onError(String error) {
                if (listener != null)
                    listener.onRespone(false);
            }
        }, true);
    }

    private void reloadAllWarehouse(){
        if (fromWarehouse.getInt("isMaster") ==1){
            adapterProduct.updateData(Product.getProductList());
            adapterChoosen.emptyList();
            updateChoosenTitle(0);

            getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
                @Override
                public void onResponse(List<BaseModel> list2) {
                    updateInventoryTitle(list2);

                }
            });


        }else {
            getInventory(fromWarehouse.getInt("id"), new CallbackListObject() {
                @Override
                public void onResponse(List<BaseModel> list1) {
                    adapterProduct.updateData(list1);
                    adapterChoosen.emptyList();
                    updateChoosenTitle(0);

                    getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
                        @Override
                        public void onResponse(List<BaseModel> list2) {
                            updateInventoryTitle(list2);

                        }
                    });

                }
            });

        }





    }

}
