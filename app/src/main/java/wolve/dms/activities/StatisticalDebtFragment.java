package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Statistical_DebtAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.libraries.GetPostMethod;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalDebtFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvDebts;
    private TextView tvSum, tvSumDebt, tvSumOrder, tvSort;
    private StatisticalActivity mActivity;
    private LinearLayout lnSum, lnOrdered;

    protected Statistical_DebtAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_debt, container, false);
        Util.debtFragment = this;
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        //listDebt = new ArrayList<>();
    }

    private void addEvent() {
        tvSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDebt();
            }
        });
        lnSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortDebt();
            }
        });

        lnOrdered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.changeFragment(new StatisticalOrderedFragment(), true);
            }
        });

    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        rvDebts = (RecyclerView) view.findViewById(R.id.statistical_debt_rvbill);
        tvSum = view.findViewById(R.id.statistical_debt_count);
        tvSort = view.findViewById(R.id.statistical_debt_sort);
        tvSumDebt = view.findViewById(R.id.statistical_debt_number_customer_debt);
        tvSumOrder = view.findViewById(R.id.statistical_debt_number_customer);
        lnSum = view.findViewById(R.id.statistical_debt_count_parent);
        lnOrdered = view.findViewById(R.id.statistical_ordered_count_parent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void updateCustomerNumber(int user_id, List<BaseModel> list) {
        if (user_id == 0) {
            tvSumOrder.setText(String.valueOf(DataUtil.sumNumberFromList(list, "num_of_order")));

        } else {
            for (BaseModel model : list) {
                if (model.getInt("user_id") == user_id) {
                    tvSumOrder.setText(model.getString("num_of_order"));
                    break;
                }
            }

        }

    }

    public void reloadData(String user, List<BaseModel> listDebt) {
        createRVCDebt(user, listDebt);

    }

    private void createRVCDebt(String user, List<BaseModel> list) {
        adapter = new Statistical_DebtAdapter(user, list, new CallbackString() {
            @Override
            public void Result(String id) {
                BaseModel param = createGetParam(ApiUtil.CUSTOMER_GETDETAIL() + id, false);
                new GetPostMethod(param, new NewCallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result, List<BaseModel> list) {
                        BaseModel customer = DataUtil.rebuiltCustomer(result, false);
                        CustomSQL.setBaseModel(Constants.CUSTOMER, customer);

                        Transaction.gotoCustomerActivity();

                    }

                    @Override
                    public void onError(String error) {

                    }
                }, true).execute();

//                CustomerConnect.GetCustomerDetail(s, new CallbackCustom() {
//                    @Override
//                    public void onResponse(BaseModel result) {
//
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                }, true, true);

            }
        }, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                tvSumDebt.setText(String.valueOf(value));
            }
        });
        Util.createLinearRV(rvDebts, adapter);
        tvSum.setText(Util.FormatMoney(adapter.sumDebts()));


    }

    protected double getSumDebt() {
        return adapter.sumDebts();
    }

    private void sortDebt() {
        if (tvSort.getRotation() == 180) {
            tvSort.setRotation(0);

        } else {
            tvSort.setRotation(180);
        }
        adapter.sortUp();
    }

    public int CoutList() {
        return adapter.getItemCount();

    }
}
