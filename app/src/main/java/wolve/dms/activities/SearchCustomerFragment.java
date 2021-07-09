package wolve.dms.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Customer_SearchAdapter;
import wolve.dms.adapter.HistoryTextAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackInt;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.District;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createPostParam;

/**
 * Created by macos on 9/16/17.
 */

public class SearchCustomerFragment extends DialogFragment implements View.OnClickListener,
                                                RadioGroup.OnCheckedChangeListener {
    private View view;
    private TextView tvClose, tvDistrict, tvResultCount, tvDistrictIcon;
    private ImageView btnBack;
    private EditText edSearch;
    private RecyclerView rvCustomer, rvHistory;
    private RadioButton rdName, rdPhone;
    private RadioGroup rdGroup;
    private LinearLayout lnMain, lnDistrictGroup, mParent;


    private List<BaseModel> listCustomer = new ArrayList<>();
    private Customer_SearchAdapter adapter;
    private HistoryTextAdapter historyAdapter;
    private String mSearchText = "";
    private Handler mHandlerSearch = new Handler();
    private CallbackObject mListener;
    private String countFormat ="Kết quả: %d";
    private boolean isLoading = false;
//    private CallbackListObject mListListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (CallbackObject) context;
        //mListListener = (CallbackListObject) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.fragment_customer_search, null);

        Dialog dialog = new Dialog(getContext(),R.style.dialogFullScreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_corner5_white);

        dialog.setContentView(view);

        findViewById();

        initialData();
        addEvent();

        addEvent();

        return dialog;
    }

    public void findViewById() {
        mParent = view.findViewById(R.id.customer_search_parent);
        btnBack = view.findViewById(R.id.icon_back);
        tvClose = view.findViewById(R.id.customer_search_close);
        lnMain = view.findViewById(R.id.customer_search_list_group);
        tvDistrict = view.findViewById(R.id.customer_search_district_title);
        lnDistrictGroup = view.findViewById(R.id.customer_search_district);
        rdName = view.findViewById(R.id.customer_search_name);
        rdPhone = view.findViewById(R.id.customer_search_phone);
        edSearch = view.findViewById(R.id.customer_search_maintext);
        rvCustomer = view.findViewById(R.id.customer_search_rvlist);
        tvResultCount = view.findViewById(R.id.customer_search_result);
        tvDistrictIcon = view.findViewById(R.id.customer_search_district_icon);
        rdGroup = view.findViewById(R.id.customer_search_select_group);
        rvHistory = view.findViewById(R.id.customer_search_historylist);

    }

    public void initialData() {
        Util.setTextEdDelay(edSearch, "");
        createRVList(new ArrayList<>());
        updateCountResult(0);

        rvHistory.setVisibility(View.VISIBLE);
        rvCustomer.setVisibility(View.GONE);
        createRVHistory();

    }

    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        rdGroup.setOnCheckedChangeListener(this);
        lnDistrictGroup.setOnClickListener(this);
        tvDistrictIcon.setOnClickListener(this);
        searchEvent();
    }

    private void backEvent(){
        //getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimationOut;
        this.dismiss();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                Util.hideKeyboard(v);
                backEvent();

                break;

            case R.id.customer_search_district:
                Util.hideKeyboard(v);
                selectDistrict();
                break;

            case R.id.customer_search_close:
                resetSearch();
                //selectDistrict();

                break;

            case R.id.customer_search_district_icon:
                if (!tvDistrict.getText().toString().equals(Constants.ALL_FILTER)){
                    tvDistrict.setText(Constants.ALL_FILTER);
                    tvDistrictIcon.setText(Util.getIcon(R.string.icon_district));
                    if (!Util.isEmpty(edSearch)){
                        adapter.getFilter().filter(Constants.ALL_FILTER);

                    }
                }
                break;


        }
    }

    private void selectDistrict(){
         List<String> districts = DataUtil.listObject2ListString(District.getDistricts(), "text");
         districts.add(0, Constants.ALL_FILTER);

         CustomDropdow.createListDropdown(tvDistrict,
                 districts,
                 Util.convertSdpToInt(R.dimen._300sdp),
                 new CallbackClickAdapter() {
             @Override
             public void onRespone(String data, int position) {
                 tvDistrict.setText(data);
                 if (!Util.isEmpty(edSearch)){
                     //mHandlerSearch.postDelayed(delayForSerch, 500);
                     adapter.getFilter().filter(data);
                 }

                 tvDistrictIcon.setText(position ==0?
                         Util.getIcon(R.string.icon_district):
                         Util.getIcon(R.string.icon_x));



             }


        });

    }

    private void updateCountResult(int count){
        if (count >0){
            tvResultCount.setText(String.format(countFormat, count));

        }else {
            tvResultCount.setText("Lịch sử tìm kiếm");

        }

    }

    private void searchEvent(){
        Util.textEvent(edSearch, new CallbackString() {
            @Override
            public void Result(String s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    tvClose.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                    rvCustomer.setVisibility(View.VISIBLE);

                    mSearchText = s.toString();
                    if (rdPhone.isChecked()){
                        adapter.setTextHighligh(Util.FormatPhone(mSearchText));
                    }
                    mHandlerSearch.removeCallbacks(delayForSerch);
                    mHandlerSearch.postDelayed(delayForSerch, 500);

                } else {
                    tvClose.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                    rvCustomer.setVisibility(View.GONE);
                    updateCountResult(0);
                    createRVHistory();

                }
            }
        });
    }

    private Runnable delayForSerch = new Runnable() {
        @Override
        public void run() {
            loadCustomer(0, new CallbackListObject() {
                @Override
                public void onResponse(List<BaseModel> list) {
                    adapter.reupdateList(tvDistrict.getText().toString(), list);
                    //updateCountResult(list.size());
                }
            });

        }
    };

    private void loadCustomer(int page, CallbackListObject listener){
        String paramFormat = "phone=%s&shopName=%s&district=%s";
        String mText;
        if (rdName.isChecked()){
            mText = String.format(paramFormat,
                    "",
                    Util.encodeString(mSearchText),
                    tvDistrict.getText().toString().equals(Constants.ALL_FILTER)?
                            "":
                            Util.encodeString(tvDistrict.getText().toString())
            );

        }else {
            mText = String.format(paramFormat,
                    Util.encodeString(mSearchText),
                    "",
                    tvDistrict.getText().toString().equals(Constants.ALL_FILTER)?
                            "":
                            Util.encodeString(tvDistrict.getText().toString())
            );

        }

        BaseModel param = createPostParam(
                ApiUtil.CUSTOMERS(),
                mText,
                false,
                true);

        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(list);



            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }

    private void createRVList(List<BaseModel> list) {
        adapter = new Customer_SearchAdapter(list, new CallbackBaseModel() {
            @Override
            public void onResponse(BaseModel result) {
                if (!Util.isEmpty(edSearch) &&
                        !DataUtil.checkDuplicate(CustomSQL.getListString(Constants.HISTORY_SEARCH), edSearch.getText().toString())){
                    CustomSQL.setStringToList(Constants.HISTORY_SEARCH, mSearchText);
                }

                dismiss();
                mListener.onResponse(result);

            }

            @Override
            public void onError() {
                dismiss();
            }


        }, new CallbackString() {
            @Override
            public void Result(String s) {
                dismiss();
//                (MapsActivity)getActivity().currentPhone = s;
//                checkPhonePermission();

            }
        }, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                updateCountResult(value);
            }
        });
        Util.createLinearRV(rvCustomer, adapter);
        rvCustomer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    Util.hideKeyboard(rvCustomer);
                }
            }

        });


    }

    private void createRVHistory(){
        historyAdapter = new HistoryTextAdapter(new CallbackString() {
            @Override
            public void Result(String s) {
                edSearch.setText(s);
                edSearch.setSelection(s.length());

            }
        });
        Util.createLinearRV(rvHistory, historyAdapter);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Util.showKeyboard(edSearch);
        switch (checkedId){
            case R.id.customer_search_name:
                edSearch.setInputType(InputType.TYPE_CLASS_TEXT);
                resetSearch();
                adapter.setShowPhone(false);

                break;

            case R.id.customer_search_phone:
                edSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
                resetSearch();
                adapter.setShowPhone(true);
                break;


        }
    }

    private void resetSearch(){
        edSearch.setText("");
        adapter.reupdateList(tvDistrict.getText().toString(), new ArrayList<>());
        tvResultCount.setText("Lịch sử tìm kiếm");

    }


}
