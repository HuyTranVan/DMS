package wolve.dms.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.WaitingListAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.MapUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;

/**
 * Created by macos on 9/16/17.
 */

public class MapWaitingListFragment extends DialogFragment implements View.OnClickListener{
    private View view;
    private TextView tvTitle, tvClose, tvCount, tvCheckall, tvSortTitle;
    private Button btnSubmit;
    private ImageView btnBack;
    private RecyclerView rvCustomerWaiting;
    private RelativeLayout mCheckGroup, mParent, mBottom;
    private LinearLayout lnCheckAll, lnSort;

    private List<BaseModel> listCustomerWaiting = new ArrayList<>();
    private WaitingListAdapter adapter;
    private CallbackObject mListener;
    private CallbackListObject mListListener;
    private List<String> listSortTitle;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CallbackObject) context;
        mListListener = (CallbackListObject) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.fragment_map_waitinglist, null);

        Dialog dialog = new Dialog(getContext(),R.style.dialogFullScreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_corner5_white);

        dialog.setContentView(view);

        findViewById();

        initialData();
        addEvent();

        addEvent();

        return dialog;
    }

    public void findViewById() {
        mParent = view.findViewById(R.id.waitinglist_parent);
        btnBack = view.findViewById(R.id.icon_back);
        tvTitle = view.findViewById(R.id.waitinglist_title);
        rvCustomerWaiting = view.findViewById(R.id.waitinglist_rvCustomer);
        tvClose = view.findViewById(R.id.waitinglist_select_close);
        tvCount = view.findViewById(R.id.waitinglist_select_count);
        mCheckGroup = view.findViewById(R.id.waitinglist_select_group);
        btnSubmit = view.findViewById(R.id.waitinglist_submit);
        mBottom = view.findViewById(R.id.waitinglist_submit_parent);
        lnCheckAll = view.findViewById(R.id.waitinglist_checkall_parent);
        tvCheckall = view.findViewById(R.id.waitinglist_checkall_check);
        lnSort = view.findViewById(R.id.waitinglist_sort);
        tvSortTitle = view.findViewById(R.id.waitinglist_sort_title);


    }

    public void initialData() {
        getWaitingList();
        listSortItem();

    }

    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        lnCheckAll.setOnClickListener(this);
        lnSort.setOnClickListener(this);
    }

    private void backEvent(){
        //getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimationOut;
        this.dismiss();

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.waitinglist_select_close:
                adapter.unCheckAll();
                mCheckGroup.setVisibility(View.GONE);
                tvCheckall.setText(Util.getIcon(R.string.icon_circle_border));

                break;

            case R.id.waitinglist_submit:
                mListListener.onResponse(adapter.getAllChecked());
                backEvent();

                break;

            case R.id.waitinglist_checkall_parent:
                if (tvCheckall.getText().toString().equals(Util.getIcon(R.string.icon_circle_border))){
                    adapter.CheckAll();
                    tvCheckall.setText(Util.getIcon(R.string.icon_circle_check));

                }else if (tvCheckall.getText().toString().equals(Util.getIcon(R.string.icon_circle_check))){
                    adapter.unCheckAll();
                    tvCheckall.setText(Util.getIcon(R.string.icon_circle_border));
                }

                break;

            case R.id.waitinglist_sort:
                CustomDropdow.createListDropdown(lnSort, listSortTitle, 0, false, new CallbackClickAdapter() {
                    @Override
                    public void onRespone(String data, int position) {
                        tvSortTitle.setText(data);
                        adapter.sort(position);

                    }
                });

                break;



        }
    }

    private void getWaitingList(){
        listCustomerWaiting = new ArrayList<>();
        BaseModel param = createGetParam(ApiUtil.CUSTOMER_WAITING_LIST() , false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listCustomerWaiting = addLocationToListCustomer(result.getList("list"),
                                                                CustomSQL.getDouble(Constants.LAT),
                                                                CustomSQL.getDouble(Constants.LNG));
                createCustomerWaitingRV(listCustomerWaiting);

            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }

    private void createCustomerWaitingRV(List<BaseModel> list){
        adapter = new WaitingListAdapter(list, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                tvCount.setText(String.format("%s Khách hàng",value > 0 ? String.valueOf(value) : ""));
                if (value > 0) {
                    mCheckGroup.setVisibility(View.VISIBLE);
                    mBottom.setVisibility(View.VISIBLE);
                    //setMarginSubmitButton(rvCustomerWaiting.getHeight());
                    //cbCheckAll.setOnCheckedChangeListener(null);
                    if (value == list.size()) {
                        tvCheckall.setText(Util.getIcon(R.string.icon_circle_check));
                    } else {
                        tvCheckall.setText(Util.getIcon(R.string.icon_circle_border));
                    }
                    //cbCheckAll.setOnCheckedChangeListener(MapWaitingListFragment.this);

                } else {
                    mCheckGroup.setVisibility(View.GONE);
                    mBottom.setVisibility(View.GONE);

                }
            }
        }, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                mListener.onResponse(object);
                backEvent();

            }
        });
        Util.createLinearRV(rvCustomerWaiting, adapter);

    }

    private List<BaseModel> addLocationToListCustomer(List<BaseModel> listCustomerWaiting, double lat, double lng) {
        for (BaseModel model : listCustomerWaiting) {
            double distance = MapUtil.distance(lat, lng, model.getDouble("lat"), model.getDouble("lng"));
            model.put("distance", distance);

        }

        return listCustomerWaiting;

    }

    private void setMarginSubmitButton(int recycleViewHeight){
        int height = 0;

        if (recycleViewHeight > mParent.getHeight() - Util.convertSdpToInt(R.dimen._80sdp)){
            height = mParent.getHeight() - Util.convertSdpToInt(R.dimen._40sdp);
        }else {
            height = recycleViewHeight + Util.convertSdpToInt(R.dimen._50sdp);
        }


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnSubmit.getLayoutParams();
        params.setMargins(0, height, 0, 0);
        btnSubmit.setLayoutParams(params);
    }

    private void listSortItem(){
        listSortTitle = new ArrayList<>();

        listSortTitle.add(0,"Khoảng cách gần nhất");
        listSortTitle.add(1,"Khoảng cách xa nhất");
        listSortTitle.add(2,"Thời gian gần nhất");
        listSortTitle.add(3,"Thời gian xa nhất");

    }


}
