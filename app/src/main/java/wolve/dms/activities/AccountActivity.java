package wolve.dms.activities;

import android.icu.util.UniversalTimeScale;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.Api;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.AccountAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackLong;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

public class AccountActivity extends BluetoothActivity implements View.OnClickListener {
    private ImageView btnBack;
    private TextView tvTitle, tvMonth, tvIncome, tvOutcome;
    private RadioGroup rdSort;
    private RecyclerView rvDetail;
    private FloatingActionButton btnNew;

    private long currentTime = Util.CurrentTimeStamp();
    private AccountAdapter adapter;
    private List<BaseModel> listType;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_account;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        btnBack = findViewById(R.id.icon_back);
        tvTitle = findViewById(R.id.title);
        tvMonth = findViewById(R.id.account_month);
        rdSort = findViewById(R.id.account_sort);
        rvDetail = findViewById(R.id.account_rv_detail);
        tvIncome = findViewById(R.id.account_income);
        tvOutcome = findViewById(R.id.account_outcome);
        btnNew = findViewById(R.id.account_add_new);

    }

    @Override
    public void initialData() {
        createSortGroup(0);
        tvMonth.setText(Util.getIconString(R.string.icon_down, "   ", Util.CurrentMonthYear()));
        loadDetail(Util.CurrentTimeStamp());
    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvMonth.setOnClickListener(this);
        btnNew.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Transaction.gotoHomeActivityRight(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.account_month:
                selectMonth();
                break;

            case R.id.account_add_new:
                createNewCashFlow();
                break;
        }
    }

    private void createSortGroup(int position){
        for (int i=0; i< Constants.sortGroups.length; i++){
            RadioButton radioButton  = new RadioButton(this);

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(Util.convertSdpToInt(R.dimen._10sdp), 0, 0, 0);
            radioButton.setLayoutParams(params);

            radioButton.setText(Constants.sortGroups[i]);

            radioButton.setId(i);

            radioButton.setPadding(Util.convertSdpToInt(R.dimen._10sdp),
                    Util.convertSdpToInt(R.dimen._5sdp),
                    Util.convertSdpToInt(R.dimen._10sdp),
                    Util.convertSdpToInt(R.dimen._5sdp));

            radioButton.setBackground(getResources().getDrawable(R.drawable.radiobutton_selector));
            radioButton.setButtonDrawable(null);

            rdSort.addView(radioButton);
        }
        rdSort.check(0);
        rdSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case 0:
                        adapter.getFilter().filter(Constants.ALL_TOTAL);
                        break;

                    case 1:
                        adapter.getFilter().filter(Constants.OTHERS);
                        break;

                    case 2:
                        adapter.getFilter().filter(Constants.PAY_DISTRIBUTOR);
                        break;

                    case 3:
                        adapter.getFilter().filter(Constants.IN_COME);
                        break;

//                    case 4:
//                        adapter.getFilter().filter(Constants.OUT_COME);
//                        break;


                }

                reupdateIncome();
            }
        });
    }

    private void selectMonth(){
        CustomBottomDialog.selectDate("chọn tháng",
                currentTime,
                new CallbackLong() {
            @Override
            public void onResponse(Long value) {
                currentTime = value;
                tvMonth.setText(Util.getIconString(R.string.icon_down, "   ", Util.DateDisplay(value, "MM-yyyy")));
                loadDetail(currentTime);

            }
        });
    }

    private void loadDetail(long timestamp){
        BaseModel rangeTime = Util.getMonthRange(timestamp);
        BaseModel param = createGetParam(String.format(ApiUtil.CASHFLOWS(),
                rangeTime.getLong("start"),
                rangeTime.getLong("end")),
                true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createRVDetail(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }

    private void createRVDetail(List<BaseModel> list){
        adapter = new AccountAdapter(list, new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });
        Util.createLinearRV(rvDetail, adapter);

        reupdateIncome();

    }

    private void createNewCashFlow(){
        getListType(new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                CustomCenterDialog.dialogNewCashFlow("Thêm mục thu chi", list, new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        adapter.addItem(object);
                        reupdateIncome();
                    }
                });
            }
        });

    }

    private void getListType(CallbackListObject listener){
        if (listType != null){
            listener.onResponse(listType);

        }else {
            BaseModel param = createGetParam(ApiUtil.CASHFLOWTYPES(), true);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list){
                    listType = new ArrayList<>();
                    for (BaseModel item: list){
                        if (item.getInt("kind") != 1 &&item.getInt("kind") != 2){
                            listType.add(item);
                        }
                    }

                    DataUtil.sortbyStringKey("kind", listType, false);
                    listener.onResponse(listType);
                }

                @Override
                public void onError(String error) {

                }
            }, 1).execute();
        }

    }

    private void reupdateIncome(){
        tvIncome.setText(String.format("Thu: %s", Util.FormatMoney(adapter.getIncome())));
        tvOutcome.setText(String.format("Chi: %s", Util.FormatMoney(adapter.getOutcome())));
    }

}
