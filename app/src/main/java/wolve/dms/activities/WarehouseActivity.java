package wolve.dms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.WarehouseAdapter;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class WarehouseActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private RecyclerView rvDepot;
    private WarehouseAdapter adapter;
    private TextView btnAddPDepot;
    private LinearLayout btnImport;

    public List<BaseModel> listDepot;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_warehouse;
    }

    @Override
    public int setIdContainer() {
        return R.id.depot_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvDepot = (RecyclerView) findViewById(R.id.depot_rv);
        btnAddPDepot = findViewById(R.id.depot_add_new);
        btnImport = findViewById(R.id.warehouse_import);

    }

    @Override
    public void initialData() {
        btnAddPDepot.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
        loadDepot();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnAddPDepot.setOnClickListener(this);
        btnImport.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.depot_add_new:
                BaseModel baseModel = new BaseModel();

                if (checkMasterWarehouseExist(listDepot)) {
                    CustomCenterDialog.alertWithButton("Tạo kho hàng",
                            "Đã tồn tại kho tổng, bạn chỉ có thể tạo kho hàng tạm", "đồng ý", new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result) {
                                        baseModel.put("isMaster", 2);
                                        openFragmentNewDepot(baseModel.BaseModelstoString());

                                    }
                                }
                            });


                } else {
                    CustomBottomDialog.choiceTwoOption(null, "Kho tổng", null, "Kho tạm", new CustomBottomDialog.TwoMethodListener() {
                        @Override
                        public void Method1(Boolean one) {
                            baseModel.put("isMaster", 1);
                            openFragmentNewDepot(baseModel.BaseModelstoString());
                        }

                        @Override
                        public void Method2(Boolean two) {
                            baseModel.put("isMaster", 2);
                            openFragmentNewDepot(baseModel.BaseModelstoString());
                        }
                    });


                }


                break;

            case R.id.warehouse_import:


                break;

        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.depot_parent);
        if (mFragment != null && mFragment instanceof NewUpdateWarehouseFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (mFragment != null && mFragment instanceof ImportReturnFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (mFragment != null && mFragment instanceof InventoryEditFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }
    }

    protected void loadDepot() {
        SystemConnect.ListWarehouse(true, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                createRVDepot(results);

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void createRVDepot(List<BaseModel> list) {
        listDepot = list;
        adapter = new WarehouseAdapter(listDepot, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                if (Util.isAdmin() || User.getId() == object.getInt("user_id")) {
                    if (object.getInt("isMaster") == 1) {
                        Util.showSnackbar("Không thể nhập kho tổng", null, null);

                    } else if (object.getInt("isMaster") == 2 || object.getInt("isMaster") == 3) {
                        Transaction.gotoImportActivity(object, false);

                    }

                } else {
                    Util.showSnackbar("Chức năng này chỉ do admin thực thiện", null, null);
                }

            }

        }, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                if (Util.isAdmin()) {
                    openFragmentNewDepot(object.BaseModelstoString());

                } else {
                    Util.showSnackbar("Không thể sửa thông tin", null, null);
                }
            }

        }, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                selectTempWarehouseReturn(object);

            }
        }, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                openEditInventoryFragment(object);
            }
        });
        Util.createLinearRV(rvDepot, adapter);

    }

    private void openFragmentNewDepot(String depot) {
        NewUpdateWarehouseFragment depotFragment = new NewUpdateWarehouseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEPOT, depot);
        changeFragment(depotFragment, bundle, true);
    }

    private void openFragmentImportReturn(BaseModel warehouse, BaseModel temptWarehouse) {
        ImportReturnFragment importReturnFragment = new ImportReturnFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.WAREHOUSE, warehouse.BaseModelstoString());
        bundle.putString(Constants.TEMPWAREHOUSE, temptWarehouse.BaseModelstoString());
        changeFragment(importReturnFragment, bundle, true);
    }

    private void openFragmentInventoryEdit(BaseModel temptWarehouse) {
        InventoryEditFragment importReturnFragment = new InventoryEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TEMPWAREHOUSE, temptWarehouse.BaseModelstoString());
        changeFragment(importReturnFragment, bundle, true);
    }

    private boolean checkMasterWarehouseExist(List<BaseModel> list) {
        boolean check = false;
        for (BaseModel model : list) {
            if (model.getInt("isMaster") == 1) {
                check = true;
                break;
            }
        }
        return check;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);
        initialData();

    }

    private void selectTempWarehouseReturn(BaseModel objectWarehouse) {
        if (adapter.getAllTempWarehouse().size() == 1) {
            openFragmentImportReturn(objectWarehouse, adapter.getAllTempWarehouse().get(0));

        } else {
            CustomBottomDialog.choiceListObject("chọn kho nhận hàng trả về",
                    adapter.getAllTempWarehouse(),
                    "name",
                    new CallbackObject() {
                        @Override
                        public void onResponse(BaseModel object) {
                            openFragmentImportReturn(objectWarehouse, object);

                        }
                    }, null);

        }

    }

    private void openEditInventoryFragment(BaseModel curentWarehouse) {
        CustomCenterDialog.alertWithCancelButton("Hiệu chỉnh  tồn kho",
                "Mở trang điều chỉnh số lượng tồn kho tạm",
                "tiếp tục",
                "hủy",
                new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result) {
                            openFragmentInventoryEdit(curentWarehouse);

                        }

                    }
                });
    }
}
