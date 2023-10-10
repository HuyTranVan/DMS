package wolve.dms.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
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
import wolve.dms.adapter.MapPinListAdapter;
import wolve.dms.adapter.MapWaitingListAdapter;
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
import wolve.dms.utils.CustomTopDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;
import static wolve.dms.models.User.getAllFilterUser;
import static wolve.dms.utils.MapUtil.addLocationToListCustomer;
import static wolve.dms.utils.MapUtil.distance;

import com.bumptech.glide.Glide;

/**
 * Created by macos on 9/16/17.
 */

public class MapPinListFragment extends DialogFragment implements View.OnClickListener{
    private View view;
    private TextView tvTitle, tvClose, tvCount, tvSortTitle, tvUser;
    private Button btnSubmit;
    private ImageView btnBack;
    private RecyclerView rvCustomerWaiting;
    private RelativeLayout mCheckGroup, mParent ;
    private LinearLayout lnSort, mBottom;
    private CheckBox rdCheckAll;

    private List<BaseModel> listCustomerPin = new ArrayList<>();
    private MapPinListAdapter adapter;
    private CallbackInt mListener;
    private CallbackListObject mListListener;
    private List<String> listSortTitle;
    private List<BaseModel> listUser = new ArrayList<>();
    private BaseModel currentUser;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CallbackInt) context;
        mListListener = (CallbackListObject) context;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.fragment_map_pin_list, null);

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
        mParent = view.findViewById(R.id.pinlist_parent);
        btnBack = view.findViewById(R.id.icon_back);
        tvTitle = view.findViewById(R.id.pinlist_title);
        rvCustomerWaiting = view.findViewById(R.id.pinlist_rvCustomer);
        tvClose = view.findViewById(R.id.pinlist_select_close);
        tvCount = view.findViewById(R.id.pinlist_select_count);
        mCheckGroup = view.findViewById(R.id.pinlist_select_group);
        btnSubmit = view.findViewById(R.id.pinlist_submit);
        mBottom = view.findViewById(R.id.pinlist_submit_parent);
        rdCheckAll = view.findViewById(R.id.pinlist_checkall);
        lnSort = view.findViewById(R.id.pinlist_sort);
        tvSortTitle = view.findViewById(R.id.pinlist_sort_title);
        tvUser = view.findViewById(R.id.pinlist_employee);

    }

    public void initialData() {
        currentUser = User.getCurrentUser();
        tvUser.setText(currentUser.getString("displayName"));
        listUser.add(0, getAllFilterUser());
        getPinList();
        listSortItem();

    }

    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        lnSort.setOnClickListener(this);
        tvUser.setOnClickListener(this);
        rdCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rdCheckAll.isChecked()){
                    adapter.CheckAll();
                }else {
                    adapter.unCheckAll();
                }
            }
        });
    }

    private void backEvent(){
        this.dismiss();

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.pinlist_select_close:
                adapter.unCheckAll();
                mCheckGroup.setVisibility(View.GONE);
                rdCheckAll.setChecked(false);

                break;

            case R.id.pinlist_submit:
                mListListener.onResponse(adapter.getAllChecked());
                backEvent();

                break;

            case R.id.pinlist_employee:
                CustomDropdow.createListDropdown(tvUser, getListUserString(), 0, false, new CallbackClickAdapter() {
                    @Override
                    public void onRespone(String data, int position) {
                        currentUser = listUser.get(position);
                        tvUser.setText(data);
                        adapter.getFilter().filter(data);


                    }
                });
                break;


            case R.id.pinlist_sort:
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

    private void getPinList(){
        listCustomerPin = new ArrayList<>();
        BaseModel param = createGetParam(ApiUtil.CUSTOMER_PIN_LIST() , false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listCustomerPin = addLocationToListCustomer(result.getList("list"),
                                                                CustomSQL.getDouble(Constants.LAT),
                                                                CustomSQL.getDouble(Constants.LNG));
                createCustomerWaitingRV(listCustomerPin);
                updateListUser(listCustomerPin);

            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }

    private void createCustomerWaitingRV(List<BaseModel> list){
        adapter = new MapPinListAdapter(list, currentUser.getString("displayName"), new CallbackInt() {
            @Override
            public void onResponse(int value) {
                tvCount.setText(String.format("%s Khách hàng", value > 0 ? String.valueOf(value) : ""));
                if (value > 0) {
                    mCheckGroup.setVisibility(View.VISIBLE);
                    mBottom.setVisibility(View.VISIBLE);
                    //setMarginSubmitButton(rvCustomerWaiting.getHeight());
                    //cbCheckAll.setOnCheckedChangeListener(null);
                    if (value == list.size()) {
                        rdCheckAll.setChecked(true);
                    } else {
                        rdCheckAll.setChecked(false);
                    }
                    //cbCheckAll.setOnCheckedChangeListener(MapWaitingListFragment.this);

                } else {
                    mCheckGroup.setVisibility(View.GONE);
                    mBottom.setVisibility(View.GONE);

                }}}, new CallbackInt() {
                    @Override
                    public void onResponse(int value) {
                        mListener.onResponse(value);
                        if (value == 0){
                            backEvent();

                        }

                    }
        });
        Util.createLinearRV(rvCustomerWaiting, adapter);

    }



//    private void setMarginSubmitButton(int recycleViewHeight){
//        int height = 0;
//
//        if (recycleViewHeight > mParent.getHeight() - Util.convertSdpToInt(R.dimen._80sdp)){
//            height = mParent.getHeight() - Util.convertSdpToInt(R.dimen._40sdp);
//        }else {
//            height = recycleViewHeight + Util.convertSdpToInt(R.dimen._50sdp);
//        }
//
//
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnSubmit.getLayoutParams();
//        params.setMargins(0, height, 0, 0);
//        btnSubmit.setLayoutParams(params);
//    }

    private void listSortItem(){
        listSortTitle = new ArrayList<>();

        listSortTitle.add(0,"Khoảng cách gần nhất");
        listSortTitle.add(1,"Khoảng cách xa nhất");
        listSortTitle.add(2,"Thời gian gần nhất");
        listSortTitle.add(3,"Thời gian xa nhất");

    }

    private void updateListUser(List<BaseModel> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getBaseModel("user").getInt("active") == 1){
                if (!DataUtil.checkDuplicate(listUser, "displayName", list.get(i).getBaseModel("user"))) {
                    listUser.add(list.get(i).getBaseModel("user"));
                }

            }

        }
    }

    private List<String> getListUserString(){
        List<String> list = new ArrayList<>();
        for (int i=0; i< listUser.size(); i++){
            list.add(listUser.get(i).getString("displayName"));

        }

        return list;
    }


}
