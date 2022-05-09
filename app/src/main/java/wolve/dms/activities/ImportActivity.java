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

import wolve.dms.R;
import wolve.dms.adapter.Export_ProductAdapter;
import wolve.dms.adapter.Import_ProductAdapter;
import wolve.dms.adapter.ProductImportAdapter;
import wolve.dms.adapter.ProductImportChoosenAdapter;
import wolve.dms.adapter.ProductInventoryAdapter;
import wolve.dms.adapter.ViewpagerMultiListAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostListMethod;
import wolve.dms.apiconnect.apiserver.UtilLoading;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
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

public class ImportActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView tvTitle, tvFromWarehouse, tvToWarehouse;
    private Button btnSubmit;

    private List<BaseModel> listWarehouse;
    private BaseModel fromWarehouse = null,
            toWarehouse = null;
    private ViewpagerMultiListAdapter viewpagerAdapter;
    private List<RecyclerView.Adapter> listadapter;
    private ProductImportAdapter adapterProduct;
    private ProductImportChoosenAdapter adapterChoosen;
    private ProductInventoryAdapter adapterInventory;
    private Import_ProductAdapter adapterImport;
    private Export_ProductAdapter adapterExport;
    private int currentPosition = 0;
    private List<String> mTitle = new ArrayList<>();
    private boolean[] searches = new boolean[]{false, false, false, false, false};

    public interface Callback2ListObject {
        void onResponse(List<BaseModel> list1, List<BaseModel> list2);
    }

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
        mTitle = createTabTitle(toWarehouse);
        tvTitle.setText(String.format("nhập kho (%s)" , toWarehouse.getString("name")));
        setToWarehouse(toWarehouse, false);

        tabLayout.setupWithViewPager(viewPager);

        getListWarehouse(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                getListImport(0, new Callback2ListObject() {
                    @Override
                    public void onResponse(List<BaseModel> list1, List<BaseModel> list2) {
                        setupViewPager(new ArrayList<>(),
                                        list1,
                                        list2);
                        getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
                            @Override
                            public void onResponse(List<BaseModel> list) {
                                updateInventoryTitle(list);
                                if (list.size() > 0) {
                                    viewPager.setCurrentItem(2, true);
                                }

                                selectFromWarehouse(0);
                            }
                        });
                    }
                },0);

//                getListImport(0, new CallbackListObject(){
//                    @Override
//                    public void onResponse(List<BaseModel> list) {
//                        setupViewPager(new ArrayList<>(),
//                                list);
//                        getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
//                            @Override
//                            public void onResponse(List<BaseModel> list) {
//                                updateInventoryTitle(list);
//                                if (list.size() > 0) {
//                                    viewPager.setCurrentItem(2, true);
//                                }
//
//                                selectFromWarehouse(0);
//                            }
//                        });
//                    }
//                }, 0);
            }
        }, 4);




    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvFromWarehouse.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.import_submit:
                submitEvent();
                break;

            case R.id.import_fromwarehouse:
                selectFromWarehouse(1);

                break;


        }
    }

    private List<String> createTabTitle(BaseModel warehouse) {
        List<String> titles = new ArrayList<>();
        titles.add(0, "DANH SÁCH");
        titles.add(1, "ĐÃ CHỌN");
        titles.add(2, "TỒN " + warehouse.getString("name"));
        titles.add(3, "NHẬP KHO");
        titles.add(4, "XUẤT KHO");
        return titles;
    }

    private void getListImport(int offset, Callback2ListObject listener, int loadingtimes) {
        BaseModel param0 = createGetParam(String.format(ApiUtil.IMPORTS(), offset, 20, toWarehouse.getInt("id")), true);
        BaseModel param1 = createGetParam(String.format(ApiUtil.EXPORTS(), offset, 20, toWarehouse.getInt("id")), true);



        new GetPostMethod(param0, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list0) {
                new GetPostMethod(param1, new NewCallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result, List<BaseModel> list1) {
                        listener.onResponse(
                                list0,
                                //DataUtil.filterListImport(list0, toWarehouse.getInt("id")),
                                list1);
                                //DataUtil.filterListImport(list1, toWarehouse.getInt("id")));
                    }

                    @Override
                    public void onError(String error) {

                    }
                },0).execute();


                //listener.onResponse(list);

            }

            @Override
            public void onError(String error){

            }
        }, loadingtimes).execute();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.import_parent);
        if (UtilLoading.getInstance().isLoading()) {
            UtilLoading.getInstance().stopLoading();
        } else {
            Transaction.returnPreviousActivity();
        }
    }

    private void setupViewPager(List<BaseModel> listProduct,
                                List<BaseModel> listImport,
                                List<BaseModel> listExport) {
        listadapter = new ArrayList<>();
        adapterProduct = new ProductImportAdapter(listProduct, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                adapterChoosen.insertData(object, fromWarehouse.getInt("isMaster") == 3? true : false);
            }
        });

        adapterChoosen = new ProductImportChoosenAdapter(new ArrayList<>(), true,  new CallbackInt() {
            @Override
            public void onResponse(int value) {
                updateChoosenTitle(value);
                btnSubmit.setVisibility(value > 0 && currentPosition == 1 ? View.VISIBLE : View.GONE);

            }
        }, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                adapterProduct.updateData(object);
            }
        });

        adapterInventory = new ProductInventoryAdapter(new ArrayList<>());

        adapterImport = new Import_ProductAdapter(toWarehouse.getInt("id"), listImport, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                reloadListImport(false, false);

            }
        });

        adapterExport = new Export_ProductAdapter(toWarehouse.getInt("id"), listExport, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {

            }
        });


        listadapter.add(0, adapterProduct);
        listadapter.add(1, adapterChoosen);
        listadapter.add(2, adapterInventory);
        listadapter.add(3, adapterImport);
        listadapter.add(4, adapterExport);

        viewpagerAdapter = new ViewpagerMultiListAdapter(listadapter, mTitle, searches, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                if (position == 0) {
                    adapterProduct.getFilter().filter(data);
                }
            }
        });
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(5);

        adapterImport.initScrollListener();
        adapterExport.initScrollListener();

        for (int i = 0; i < mTitle.size(); i++) {
            updateTabNotify(i, mTitle.get(i), 0);

        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                Util.hideKeyboard(viewPager);
                if (position == 1 && adapterChoosen.getItemCount() > 0) {
                    btnSubmit.setVisibility(View.VISIBLE);
                } else {
                    btnSubmit.setVisibility(View.GONE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void updateTabNotify(int position, String title, int number) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        View customView = LayoutInflater.from(ImportActivity.this).inflate(R.layout.view_tab_product, null);
        TextView tabTextNotify = (TextView) customView.findViewById(R.id.tabNotify);
        TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

        textTitle.setText(title);
        if (number > 0) {
            tabTextNotify.setVisibility(View.VISIBLE);
            tabTextNotify.setText(String.valueOf(number));
        } else {
            tabTextNotify.setVisibility(View.GONE);
        }

        tab.setCustomView(null);
        tab.setCustomView(customView);
    }

    private void submitEvent() {
            List<BaseModel> listDiff = DataUtil.listImportNotEnough( adapterChoosen.getmData());

            if (listDiff.size() > 0 && fromWarehouse.getInt("isMaster") == 2 && Util.isAdmin()){
                CustomCenterDialog.alertWithButton("Thiếu tồn kho!",
                        "Tồn kho " + fromWarehouse.getString("name").toUpperCase() + " hiện tại không đủ, chọn 'Chờ nhập kho' để hoàn tất sau",
                        "đồng ý",
                        new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                if (result){
                                    postImport(0);
                                }


                            }
                        });

            } else {
                postImport(Util.isAdmin() ? User.getId() : 0);


            }

    }

    private void postImport(int accepUser){
        String param = DataUtil.createPostImportJsonParam(toWarehouse.getInt("id"),
                fromWarehouse.getInt("id"),
                adapterChoosen.getmData(),
                accepUser,
                "");

        postImport(param, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    DataUtil.saveProductPopular(adapterChoosen.getmData());
                    double total = DataUtil.sumMoneyFromList(adapterChoosen.getmData(), "basePrice", "quantity");
                    reloadAllWarehouse(new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            reloadListImport(false, false);
                            if (fromWarehouse.getInt("isMaster") == 1){
                                updateDebitNote(total);
                            }else {
                                returnPreviousActivity();
                            }

                        }
                    });
                    viewPager.setCurrentItem(Util.isAdmin()? 2 : 3, true);
                }
            }
        });
    }

    private void returnPreviousActivity() {
        CustomCenterDialog.alertWithCancelButton("Nhập kho thành công",
                "Bạn muốn nhập kho tiếp hay trở về",
                "Trở về",
                "Nhập tiếp",
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result) {
                            onBackPressed();
                        }
                    }
                });
    }

    private void updateDebitNote(double total){
        CustomCenterDialog.alertWithCancelButton("Cập nhật công nợ",
                "Cập nhật đơn nhập kho này vào công nợ của nhà cung cấp",
                "Đồng ý",
                "Không",
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result) {
                            BaseModel param = createPostParam(ApiUtil.DEBITNOTE_NEW(),
                                    String.format(ApiUtil.DEBITNOTE_CREATE_PARAM, total),
                                    false, false);
                            new GetPostMethod(param, new NewCallbackCustom() {
                                @Override
                                public void onResponse(BaseModel result, List<BaseModel> list) {
                                    returnPreviousActivity();
                                }

                                @Override
                                public void onError(String error) {

                                }
                            }, 1).execute();


                        }else {
                            returnPreviousActivity();
                        }
                    }
                });

    }

    public void reloadListImport(boolean setActive, boolean reloadToWarehouse) {
        getListImport(0, new Callback2ListObject(){
            @Override
            public void onResponse(List<BaseModel> list1, List<BaseModel> list2) {
//                adapterImport.reloadData(list);
//                if (setActive) {
//                    viewPager.setCurrentItem(3, true);
//                }
//                if (reloadToWarehouse) {
//                    getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
//                        @Override
//                        public void onResponse(List<BaseModel> list2) {
//                            updateInventoryTitle(list2);
//
//                        }
//                    });
//                }
            }

//            @Override
//            public void onResponse(List<BaseModel> list) {
//                adapterImport.reloadData(list);
//                if (setActive) {
//                    viewPager.setCurrentItem(3, true);
//                }
//                if (reloadToWarehouse) {
//                    getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
//                        @Override
//                        public void onResponse(List<BaseModel> list2) {
//                            updateInventoryTitle(list2);
//
//                        }
//                    });
//                }
//            }
        }, 0);

    }

    private void dialogSelectWarehouse(String title, List<BaseModel> list, CallbackObject listener) {
        CustomBottomDialog.choiceListObject(title, list, "name", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                listener.onResponse(object);

            }

        }, null);

    }

    private void getListWarehouse(CallbackListObject listener, int loadingtimes) {
        if (listWarehouse.size() == 0) {
            BaseModel param = createGetParam(ApiUtil.WAREHOUSES(), true);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    listWarehouse = filterWarehouseHaveQuantity(list);
                    listener.onResponse(listWarehouse);
                }

                @Override
                public void onError(String error) {

                }
            }, loadingtimes).execute();



        } else {
            listener.onResponse(listWarehouse);
            UtilLoading.getInstance().stopLoading();
        }

    }

    private void getInventory(int warehouse_id, CallbackListObject listener) {
        BaseModel param = createGetParam(String.format(ApiUtil.INVENTORIES(), warehouse_id, 0), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(list);
            }

            @Override
            public void onError(String error){

            }
        }, 0).execute();

    }

    private void selectFromWarehouse(int loadingtimes) {
        getListWarehouse(new CallbackListObject(){
            @Override
            public void onResponse(List<BaseModel> listWa) {

                dialogSelectWarehouse("CHỌN KHO XUẤT", filterListFromWarehouse(listWa), new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object){
                        setFromWarehouse(object);

                    }
                });


            }
        }, loadingtimes);

    }

    private BaseModel getisMasterWarehouse(List<BaseModel> list, int isMaster) {
        BaseModel object = new BaseModel();
        for (BaseModel item: list){
            if (item.getInt("isMaster") == isMaster){
                object = item;
                break;
            }
        }
        return object;
    }

    private void refreshSearchView(int position, boolean isShow) {
        RelativeLayout lnSearch = viewpagerAdapter.getView(position).findViewById(R.id.search_parent);
        EditText tvSearch = viewpagerAdapter.getView(position).findViewById(R.id.search_text);

        lnSearch.setVisibility(isShow ? View.VISIBLE : View.GONE);
        tvSearch.setText("");

    }

    private List<BaseModel> filterListFromWarehouse(List<BaseModel> list) {
        List<BaseModel> listResutl = new ArrayList<>();
        for (BaseModel model : list) {
            if (Util.isAdmin()) {
                if (fromWarehouse != null) {
                    if (toWarehouse.getInt("isMaster") == 2
                            && model.getInt("id") != toWarehouse.getInt("id")
                            && model.getInt("id") != fromWarehouse.getInt("id")){
                        listResutl.add(model);
                    }else if (model.getInt("isMaster") != 1
                            && model.getInt("id") != toWarehouse.getInt("id")
                            && model.getInt("id") != fromWarehouse.getInt("id")){
                        listResutl.add(model);

                    }

                }else {
                    if (toWarehouse.getInt("isMaster") == 2
                            && model.getInt("id") != toWarehouse.getInt("id")){
                        listResutl.add(model);
                    }else if (model.getInt("isMaster") != 1
                            && model.getInt("id") != toWarehouse.getInt("id")){
                        listResutl.add(model);

                    }

                }

            } else {
                if (fromWarehouse != null){
                    if (model.getInt("id") != toWarehouse.getInt("id")
                            && model.getInt("id") != fromWarehouse.getInt("id")
                            && model.getInt("isMaster") != 1) {
                        listResutl.add(model);
                    }

                }else {
                    if (model.getInt("id") != toWarehouse.getInt("id")
                            && model.getInt("isMaster") != 1) {
                        listResutl.add(model);
                    }

                }

            }
        }
        DataUtil.sortbyStringKey("isMaster", listResutl, false);
        return listResutl;
    }

    private List<BaseModel> filterWarehouseHaveQuantity(List<BaseModel> list) {
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model : list) {
            if (Util.isAdmin()) {
                results.add(model);

            } else {
                if (model.getInt("quantity") > 0 || model.getInt("isMaster") == 1 || model.getInt("isMaster") == 2) {
                    results.add(model);

                }
            }

        }

        return results;
    }

    private void updateInventoryTitle(List<BaseModel> list) {
        adapterInventory.updateData(list);
        updateTabNotify(2, "TỒN " + toWarehouse.getString("name").toUpperCase(), list.size());

    }

    private void updateChoosenTitle(int number) {
        updateTabNotify(1, mTitle.get(1), number);
    }

    private void updateImportTempTitle(int number) {
        updateTabNotify(3, mTitle.get(3), number);
    }

    private void postImport(String paramdetail, CallbackBoolean listener) {
        BaseModel param = createPostParam(ApiUtil.IMPORT_NEW(),
                paramdetail, true, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onRespone(true);
            }

            @Override
            public void onError(String error) {
                listener.onRespone(false);
            }
        }, 1).execute();


    }

    private void reloadAllWarehouse(CallbackBoolean listener) {
        if (fromWarehouse == null || fromWarehouse.getInt("isMaster") == 1) {
            adapterProduct.updateData(Product.getProductList());
            adapterChoosen.emptyList();
            updateChoosenTitle(0);

            getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
                @Override
                public void onResponse(List<BaseModel> list2) {
                    updateInventoryTitle(list2);
                    if (listener != null) {
                        listener.onRespone(true);
                    }
                }
            });


        } else {
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
                            if (listener != null) {
                                listener.onRespone(true);
                            }

                        }
                    });

                }
            });

        }
    }

    public static void alertInventoryNotEnough(String warehouseName, CallbackBoolean listener){
        CustomCenterDialog.alertWithButton("Nhập kho!",
                "Nhập thêm sản phẩm vào kho " + warehouseName.toUpperCase() + " để tiếp tục",
                "đồng ý",
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result){
                            listener.onRespone(result);
                        }


                    }
                });
    }

    private void setToWarehouse(BaseModel object, boolean haveReset){
        toWarehouse = new BaseModel(object.BaseModelJSONObject());
        tvToWarehouse.setText(Util.getStringIcon(toWarehouse.getString("name"), "   ", R.string.icon_down));
        if (haveReset){
            reloadAllWarehouse(null);
        }

    }

    private void setFromWarehouse(BaseModel object){
        fromWarehouse = new BaseModel(object.BaseModelJSONObject());
        if (object.getInt("isMaster") == 1) {
            tvFromWarehouse.setText(Util.getStringIcon(object.getString("name"), "   ", R.string.icon_down));
            viewPager.setCurrentItem(0, true);
            adapterProduct.updateData(Product.getProductList());
            adapterChoosen.emptyList();

            updateChoosenTitle(0);
            refreshSearchView(0, Product.getProductList().size() > 0 ? true : false);

        }else {
            getInventory(object.getInt("id"), new CallbackListObject() {
                @Override
                public void onResponse(List<BaseModel> listIn) {
                    int size = listIn.size();
                    tvFromWarehouse.setText(Util.getStringIcon(object.getString("name"), "   ", R.string.icon_down));
                    viewPager.setCurrentItem(0, true);
                    //fromWarehouse = object;
                    adapterProduct.updateData(object.getInt("isMaster") == 2? Product.getProductInventoryList(listIn) :listIn);
                    adapterChoosen.emptyList();

                    updateChoosenTitle(0);
                    refreshSearchView(0, size > 0 ? true : false);
                    //adapterProduct.notifyDataSetChanged();

                }
            });

        }
    }

//    private void selectToWarehouse(int loadingtimes) {
//        //todo: show all include Master Warehouse
//        getListWarehouse(new CallbackListObject() {
//            @Override
//            public void onResponse(List<BaseModel> list) {
//                dialogSelectWarehouse("CHỌN KHO NHẬP", filterListToWarehouse(list), new CallbackObject() {
//                    @Override
//                    public void onResponse(BaseModel object) {
//                        setToWarehouse(object, true);
////                        toWarehouse = object;
////                        tvToWarehouse.setText(Util.getStringIcon(object.getString("name"), "   ", R.string.icon_down));
////
////                        reloadAllWarehouse(null);
//
//                    }
//                });
//
//            }
//        }, loadingtimes);
//
//
//    }

//    private List<BaseModel> filterListToWarehouse(List<BaseModel> list) {
//        List<BaseModel> results = new ArrayList<>();
//        for (BaseModel model : list) {
//            if (model.getInt("isMaster") != 1) {
//                if (fromWarehouse != null && model.getInt("id") != fromWarehouse.getInt("id")) {
//                    results.add(model);
//
//                } else if (fromWarehouse == null) {
//                    results.add(model);
//
//                }
//
//            }
//        }
//        DataUtil.sortbyStringKey("isMaster", results, false);
//        return results;
//    }

//    @Override
//    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
//        if (statecheckevent){
//            if (isChecked){
//                setFromWarehouse(masterWarehouse);
//                setToWarehouse(tempWarehouse, false);
//                getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
//                    @Override
//                    public void onResponse(List<BaseModel> list2) {
//                        updateInventoryTitle(list2);
//                    }
//                });
//
//            }else {
//                setFromWarehouse(tempWarehouse);
//                setToWarehouse(userWarehouse, false);
//                getInventory(toWarehouse.getInt("id"), new CallbackListObject() {
//                    @Override
//                    public void onResponse(List<BaseModel> list2) {
//                        updateInventoryTitle(list2);
//
//                    }
//                });
//
//            }
//        }else {
//            statecheckevent = true;
//        }
//
//    }
}
