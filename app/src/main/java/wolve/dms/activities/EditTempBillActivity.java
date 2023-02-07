package wolve.dms.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.CartProductsAdapter;
import wolve.dms.adapter.TempbillEditAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class EditTempBillActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private Button btnSubmit;
    private TextView tvTitle, tvTotal, tvDate, tvAddress, tvEmployee, tvDelete;
    private CInputForm tvNote;
    private RecyclerView rvProducts;
    private RelativeLayout rlCover;
    private LinearLayout lnSubmitGroup, lnEmployee;
    private FloatingActionButton btnAddProduct;

    private TempbillEditAdapter adapter;
    private BaseModel currentBill, currentUser;
    private double total;
    private int temp_id;
    protected List<BaseModel> listBillDetail = new ArrayList<>();
    protected List<BaseModel> listEmployee = null;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_edittempbill;
    }

    @Override
    public int setIdContainer() {
        return R.id.edittempbill_parent;
    }

    @Override
    public void findViewById() {
        btnSubmit = findViewById(R.id.edittempbill_submit);
        btnBack = findViewById(R.id.icon_back);
        tvTitle = findViewById(R.id.edittempbill_title);
        tvDate = findViewById(R.id.edittempbill_date);
        tvEmployee = findViewById(R.id.edittempbill_employee);
        tvTotal = findViewById(R.id.edittempbill_total);
        tvNote = findViewById(R.id.edittempbill_note);
        lnSubmitGroup = findViewById(R.id.edittempbill_submit_group);
        rvProducts = findViewById(R.id.edittempbill_rvproduct);
        tvAddress = findViewById(R.id.edittempbill_address);
        lnEmployee =findViewById(R.id.edittempbill_employee_group);
        tvDelete = findViewById(R.id.edittempbill_delete);

    }

    @Override
    public void initialData() {
        loadBillDetail(getIntent().getExtras().getInt(Constants.BILL_ID));
        temp_id = (getIntent().getExtras().getInt(Constants.TEMPBILL_ID));


    }

    private void loadBillDetail(int bill_id) {
        BaseModel param = createGetParam(ApiUtil.BILL_DETAIL()+ bill_id, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                //currentBill = result;
                updateViews(result);
            }

            @Override
            public void onError(String error) {

            }
        },1).execute();


    }

    private void updateViews(BaseModel currentbill) {
        currentBill = currentbill;
        currentUser = currentbill.getBaseModel("user");
        total = currentbill.getDouble("total");

        BaseModel customer = currentBill.getBaseModel("customer");
        listBillDetail = currentBill.getList("billDetails");
        tvTitle.setText(String.format("%s %s", Constants.shopName[customer.getInt("shopType")], customer.getString("signBoard").toUpperCase()));
        tvAddress.setText(String.format(Constants.addressFormat,
                customer.getString("address"),
                customer.getString("street"),
                customer.getString("district"),
                customer.getString("province")));
        tvDate.setText(Util.getIconString(R.string.icon_calendar,
                "   ",
                Util.DateString(currentBill.getLong("createAt"))));
        tvEmployee.setText(currentBill.getBaseModel("user").getString("displayName"));
        tvTotal.setText(Util.FormatMoney(total));
        tvNote.setText(currentbill.getString("temp_note"));
        tvNote.setSelection();

        createRVBillDetail(listBillDetail);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        if (Util.isAdmin()){
            lnEmployee.setOnClickListener(this);
        }


    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.edittempbill_parent);
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        }  else {
            Transaction.gotoHomeActivityRight(true);
            //Transaction.returnPreviousActivity(Constants.SHOP_CART_ACTIVITY, new BaseModel(), Constants.RESULT_SHOPCART_ACTIVITY);

        }

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();

                break;

            case R.id.edittempbill_submit:
                submitBill();

                break;

            case R.id.edittempbill_employee_group:
                loadUsers();

                break;

            case R.id.edittempbill_delete:
                deleteBill();

                break;
        }
    }

    private void loadUsers(){
        if (listEmployee != null){
            choiceUser();

        }else {
            BaseModel param = createGetParam(ApiUtil.USERS()+ "?warehouse_id=1&active=1", true);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    listEmployee = list;
                    choiceUser();
                }

                @Override
                public void onError(String error) {

                }
            },1).execute();

        }

    }

    private void choiceUser(){
        CustomDropdow.createListDropdown(lnEmployee, DataUtil.listObject2ListString(listEmployee, "displayName"), 0, false, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                tvEmployee.setText(data);
                currentUser = listEmployee.get(position);
            }
        });

    }

    private void createRVBillDetail(final List<BaseModel> list) {
        adapter = new TempbillEditAdapter(list, new CallbackDouble() {
            @Override
            public void Result(Double d) {
                total = d;
                tvTotal.setText(Util.FormatMoney(total));
                btnSubmit.setVisibility(adapter.checkAllDeleted()? View.INVISIBLE : View.VISIBLE);
            }
        });
        Util.createLinearRV(rvProducts, adapter);

    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent intent) {
        super.onActivityResult(reqCode, resultCode, intent);
        Util.getInstance().setCurrentActivity(this);
        if (reqCode == Constants.RESULT_PRINTBILL_ACTIVITY) {
            BaseModel data = new BaseModel(intent.getStringExtra(Constants.PRINT_BILL_ACTIVITY));
            if (data.hasKey(Constants.RELOAD_DATA) && data.getBoolean(Constants.RELOAD_DATA)) {
                Transaction.returnPreviousActivity(Constants.SHOP_CART_ACTIVITY, data, Constants.RESULT_SHOPCART_ACTIVITY);

            }

        }

    }

    private void submitBill() {
        BaseModel param = createPostParam(ApiUtil.TEMPBILL_UPDATE(),
                DataUtil.updateTempBillJsonParam(temp_id,
                        currentBill.getInt("id"),
                        currentUser.getInt("id"),
                        currentUser.getInt("warehouse_id"),
                        total,
                        adapter.getNetTotalMoney(),
                        tvNote.getText().toString(),
                        adapter.getmData()),
                true,
                false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                if (result.getBoolean("updated")){
                    onBackPressed();

                }
//                BaseModel modelResult = new BaseModel();
//                modelResult.put(Constants.RELOAD_DATA, true);
//                Transaction.returnPreviousActivity(Constants.SHOP_CART_ACTIVITY, modelResult, Constants.RESULT_SHOPCART_ACTIVITY);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }

    private void deleteBill(){
        CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xoá hoá đơn", "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    BaseModel param = createGetParam(ApiUtil.BILL_DELETE() + currentBill.getString("id"), false);
                    new GetPostMethod(param, new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {
                            if (result.getBoolean("deleted")){
                                Util.showToast("Xoá thành công");
                                onBackPressed();

                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    },1).execute();




                }

            }
        });


    }




}
