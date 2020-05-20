package wolve.dms.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.Import_ProductAdapter;
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
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
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
    private TextView tvTitle, tvFromWarehouse,tvToWarehouse;
    private Button btnSubmit;
    private RelativeLayout coParent;

    private List<BaseModel> listWarehouse;
    private BaseModel fromWarehouse = null, toWarehouse = null;
    private ViewpagerMultiListAdapter viewpagerAdapter;
    private List<RecyclerView.Adapter>  listadapter;
    private ProductImportAdapter adapterProduct;
    private ProductImportChoosenAdapter adapterChoosen;
    private ProductInventoryAdapter adapterInventory;
    private Import_ProductAdapter adapterImport;
    //private List<BaseModel> listCurrentInventory = new ArrayList<>();
    private int currentPosition =0;
    private List<String> mTitle = new ArrayList<>();
    private boolean[] searches = new boolean[]{false, false, false, false};

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
        mTitle = createTabTitle();

        tvTitle.setText("nhập kho");
        tvToWarehouse.setText(Util.getStringIcon(toWarehouse.getString("name"), "   ", R.string.icon_down));
        if (Util.isAdmin()){
            tvToWarehouse.setFocusable(true);
            tvToWarehouse.setFocusableInTouchMode(true);

        }else {
            tvToWarehouse.setFocusable(false);
        }

        tabLayout.setupWithViewPager(viewPager);
        getListImport(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                setupViewPager(new ArrayList<>(), list);
                if (list.size()>0){
                    updateImportTempTitle(list.size());
                }
                getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
                    @Override
                    public void onResponse(List<BaseModel> list) {
                        updateInventoryTitle(list);
                        if (list.size()>0){
                            viewPager.setCurrentItem(2, true);
                        }

                        selectFromWarehouse(false);
                    }
                });
            }
        }, true, false);




    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvToWarehouse.setOnClickListener(this);
        tvFromWarehouse.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.import_submit:
                submitEvent();
                break;

            case R.id.import_fromwarehouse:
                selectFromWarehouse(true);

                break;

            case R.id.import_towarehouse:
                if (Util.isAdmin()){
                    selectToWarehouse(true);

                }else {
                    Util.showSnackbar("Không thể chọn kho hàng khác", null, null);

                }

                break;

        }
    }

    private List<String> createTabTitle(){
        List<String> titles = new ArrayList<>();
        titles.add(0,"DANH SÁCH");
        titles.add(1, "ĐÃ CHỌN");
        titles.add(2, "TỒN " +toWarehouse.getString("name"));
        titles.add(3,"LỊCH SỬ");
        return titles;
    }

    private void getListImport(CallbackListObject listener, boolean showloading, boolean stoploading){
        String param = String.format("?warehouse_id=%d", toWarehouse.getInt("id"));
        CustomerConnect.ListImport(param, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                listener.onResponse(DataUtil.filterListImport(results, toWarehouse.getInt("id")));
            }

            @Override
            public void onError(String error) {

            }
        }, showloading, stoploading);
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.import_parent);
        if (Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);
        }else {
            Transaction.returnPreviousActivity();
        }
    }

    private void setupViewPager(List<BaseModel> listProduct, List<BaseModel> listImport ){
        listadapter  = new ArrayList<>();
        adapterProduct = new ProductImportAdapter(listProduct, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                adapterChoosen.insertData(object);
            }
        });

        adapterChoosen = new ProductImportChoosenAdapter(new ArrayList<>(), true, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                updateChoosenTitle(value);
                btnSubmit.setVisibility(value > 0 && currentPosition == 1 ? View.VISIBLE : View.GONE);

            }}, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                adapterProduct.updateData(object);
            }
        });

        adapterImport = new Import_ProductAdapter(listImport, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                reloadListImport(false, false);

            }
        });

        adapterInventory = new ProductInventoryAdapter(new ArrayList<>());

        listadapter.add(0, adapterProduct);
        listadapter.add(1, adapterChoosen);
        listadapter.add(2, adapterInventory);
        listadapter.add(3, adapterImport);

        viewpagerAdapter = new ViewpagerMultiListAdapter(listadapter, mTitle, searches, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                if (position ==0){
                    adapterProduct.getFilter().filter(data);
                }
            }
        });
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(4);

        for (int i=0; i<mTitle.size(); i++){
            updateTabNotify(i, mTitle.get(i),0);

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
        String param = DataUtil.createPostImportParam(toWarehouse.getInt("id"),
                fromWarehouse.getInt("id"),
                adapterChoosen.getmData(),
                "");
        postImport(param, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    DataUtil.saveProductPopular(adapterChoosen.getmData());
                    reloadAllWarehouse(new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            reloadListImport(false, false);
                            returnPreviousActivity();

                        }
                    });
                    viewPager.setCurrentItem(Util.isAdmin()? 2: 3, true);
                }
            }
        });

    }

    private void returnPreviousActivity(){
        CustomCenterDialog.alertWithCancelButton("Nhập kho thành công",
                "Bạn muốn nhập kho tiếp hay trở về",
                "Trở về",
                "Nhập tiếp",
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result){
                            onBackPressed();
                        }
                    }
                });
    }

    public void reloadListImport(boolean setActive, boolean reloadToWarehouse){
        getListImport(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                if (list.size()>0){
                    updateImportTempTitle(list.size());
                }
                adapterImport.reloadData(list);
                if (setActive){
                    viewPager.setCurrentItem(3, true);
                }
                if (reloadToWarehouse){
                    getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
                        @Override
                        public void onResponse(List<BaseModel> list2) {
                            updateInventoryTitle(list2);

                        }
                    });
                }
            }
        }, true, true);

    }

    private void dialogSelectWarehouse(String title, List<BaseModel> list, CallbackObject listener){
        CustomBottomDialog.choiceListObject(title, list, "name", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                listener.onResponse(object);

            }

        }, null);

    }

    private void getListWarehouse(CallbackListObject listener , boolean showloading){
        if (listWarehouse.size() ==0){
            SystemConnect.ListWarehouse(showloading, new CallbackCustomList() {
                @Override
                public void onResponse(List<BaseModel> results) {
                    listWarehouse = filterWarehouseHaveQuantity(results);
                    listener.onResponse(listWarehouse);

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

    private void selectFromWarehouse(boolean showloading){
        //todo: show all include Master Warehouse
        getListWarehouse(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                dialogSelectWarehouse("CHỌN KHO XUẤT",filterListFromWarehouse(list), new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {

                        if (object.getInt("isMaster") ==1 ){
                            if (toWarehouse.getInt("isMaster") != 2){
                                CustomCenterDialog.alert("Không thể thực hiện", "Chỉ có thể xuất từ kho tổng sang kho tạm. Vui lòng chọn kho đích là kho tạm", " đồng ý");

                            }else {
                                tvFromWarehouse.setText(Util.getStringIcon(object.getString("name"),"   ",R.string.icon_down));
                                viewPager.setCurrentItem(0, true);
                                fromWarehouse = object;
                                adapterProduct.updateData(Product.getProductList());
                                adapterChoosen.emptyList();
                                updateChoosenTitle(0);
                                refreshSearchView(0, Product.getProductList().size()> 0? true: false );

                            }

                        }else {
                            getInventory(object.getInt("id"), new CallbackListObject() {
                                @Override
                                public void onResponse(List<BaseModel> list) {
                                    tvFromWarehouse.setText(Util.getStringIcon(object.getString("name"),"   ",R.string.icon_down));
                                    viewPager.setCurrentItem(0, true);
                                    fromWarehouse = object;
                                    adapterProduct.updateData(list);
                                    adapterChoosen.emptyList();

                                    updateChoosenTitle(0);
                                    refreshSearchView(0, list.size()> 0? true: false );

                                }
                            });

                        }

                    }
                });


            }
        }, showloading);

    }

    private void refreshSearchView(int position, boolean isShow){
        RelativeLayout lnSearch = viewpagerAdapter.getView(position).findViewById(R.id.search_parent);
        EditText tvSearch = viewpagerAdapter.getView(position).findViewById(R.id.search_text);

        lnSearch.setVisibility(isShow? View.VISIBLE: View.GONE);
        tvSearch.setText("");

    }

    private void selectToWarehouse(boolean showloading){
        //todo: show all include Master Warehouse
        getListWarehouse(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {

                dialogSelectWarehouse("CHỌN KHO NHẬP", filterListToWarehouse(list), new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        toWarehouse = object;
                        tvToWarehouse.setText(Util.getStringIcon(object.getString("name"),"   ", R.string.icon_down));

                        reloadAllWarehouse(null);

                    }
                });

            }
        }, showloading);


    }

    private List<BaseModel> filterListToWarehouse(List<BaseModel> list){
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model: list){
            if (model.getInt("isMaster") !=1){
                if (fromWarehouse != null && model.getInt("id") != fromWarehouse.getInt("id")){
                    results.add(model);

                }else if (fromWarehouse == null){
                    results.add(model);

                }

            }
        }
        DataUtil.sortbyStringKey("isMaster", results, false);
        return results;
    }

    private List<BaseModel> filterListFromWarehouse(List<BaseModel> list){
        List<BaseModel> listResutl = new ArrayList<>();
        for (BaseModel model: list){
            if (Util.isAdmin()){
                if (model.getInt("id") != toWarehouse.getInt("id") ){
                    if (toWarehouse.getInt("isMaster") == 2){
                        listResutl.add(model);

                    }else if (model.getInt("isMaster") != 1){
                        listResutl.add(model);

                    }


                }

            }else {
                if (model.getInt("id") != toWarehouse.getInt("id")
                        && model.getInt("isMaster") != 1){
                    listResutl.add(model);
                }
            }
        }
        DataUtil.sortbyStringKey("isMaster", listResutl, false);
        return listResutl;
    }

    private List<BaseModel> filterWarehouseHaveQuantity(List<BaseModel> list){
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model: list){
            if (Util.isAdmin()){
                results.add(model);

            }else {
                if (model.getInt("quantity") > 0 || model.getInt("isMaster") ==1 || model.getInt("isMaster") ==2){
                    results.add(model);

                }
            }

        }

        return results;
    }

    private void updateInventoryTitle(List<BaseModel> list){
        adapterInventory.updateData(list);
        updateTabNotify(2, "TỒN "+ toWarehouse.getString("name").toUpperCase(), list.size());
    }

    private void updateChoosenTitle(int number){
        updateTabNotify(1, mTitle.get(1), number);
    }

    private void updateImportTempTitle(int number){
        updateTabNotify(3, mTitle.get(3), number);
    }

    private void postImport(String param, CallbackBoolean listener){
        CustomerConnect.PostImport(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                listener.onRespone(true);


            }

            @Override
            public void onError(String error) {
                listener.onRespone(false);
            }
        }, true);
    }

    private void reloadAllWarehouse(CallbackBoolean listener){
        if (fromWarehouse == null || fromWarehouse.getInt("isMaster") == 1){
            adapterProduct.updateData(Product.getProductList());
            adapterChoosen.emptyList();
            updateChoosenTitle(0);

            getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
                @Override
                public void onResponse(List<BaseModel> list2) {
                    updateInventoryTitle(list2);
                    if (listener != null){
                        listener.onRespone(true);
                    }
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
                            if (listener != null){
                                listener.onRespone(true);
                            }

                        }
                    });

                }
            });

        }
    }





}
