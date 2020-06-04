package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Statistical_OrderedAdapter;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalOrderedFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvCustomer;
    private ImageView btnBack;

    private StatisticalActivity mActivity;
    private List<BaseModel> listOrdered = new ArrayList<>();
    private Statistical_OrderedAdapter adapter;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ordered, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        loadListOrdered(0, new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                listOrdered = list;
                createRVCustomerOrdered(listOrdered);
            }
        }, true);

    }

    private void addEvent() {


    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        btnBack = view.findViewById(R.id.icon_back);
        rvCustomer = view.findViewById(R.id.ordered_rvcustomer);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;


        }
    }

    private void loadListOrdered(int offset, CallbackListObject listener, boolean showloading) {
        CustomerConnect.getCustomerOrderedList(offset, mActivity.getCurrentUserId(), new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                listener.onResponse(results);

            }

            @Override
            public void onError(String error) {

            }
        }, showloading);


    }

    private void createRVCustomerOrdered(List<BaseModel> list) {
        adapter = new Statistical_OrderedAdapter(mActivity.tvEmployeeName.getText().toString().trim(),
                list, new CallbackString() {
            @Override
            public void Result(String s) {
                openCustomerActivity(s);

            }

        });
        rvCustomer.setLayoutManager(new LinearLayoutManager(mActivity));
        rvCustomer.setAdapter(adapter);
        //Util.createLinearRV(rvCustomer, adapter);
//        rvCustomer.setAdapter(adapter);
        initScrollListener();
    }

    private void openCustomerActivity(String id) {
        CustomerConnect.GetCustomerDetail(id, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                BaseModel customer = DataUtil.rebuiltCustomer(result, false);
                CustomSQL.setBaseModel(Constants.CUSTOMER, customer);

                Transaction.gotoCustomerActivity();
            }

            @Override
            public void onError(String error) {

            }
        }, true, true);
    }

    private void initScrollListener() {
        rvCustomer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });

    }

    private void loadMore() {
        adapter.addItem(null);
        //adapter.notifyItemInserted(listOrdered.size() - 1);

        loadListOrdered(adapter.getItemCount() - 1, new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                adapter.removeItem(adapter.getItemCount() - 1);
                adapter.notifyDataSetChanged();
                int scrollPosition = listOrdered.size();
//                adapter.notifyItemRemoved(scrollPosition);
//                int currentSize = scrollPosition;
//                int nextLimit = currentSize + list.size();

                for (int i = 0; i < list.size(); i++) {
//                    listOrdered.add(list.get(i));

                    adapter.addItem(list.get(i));
                    //adapter.notifyItemInserted(listOrdered.size()-1);

                }

//                while (currentSize - 1 < nextLimit) {
//                    listOrdered.add(listOrdered.get(listOrdered.size() -1 ));
//                    currentSize++;
//                }

                adapter.notifyDataSetChanged();
                isLoading = false;


            }
        }, false);

    }


}
