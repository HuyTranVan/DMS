package wolve.dms.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createGetParam;
import static wolve.dms.activities.BaseActivity.createPostParam;

/**
 * Created by macos on 9/16/17.
 */

public class NewUpdateWarehouseFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edInput, edUser;
    private Button btnSubmit;
    private BaseModel currentDepot;
    //private WarehouseActivity mActivity;
//    private RadioGroup rdDepotType;
//    private RadioButton rdUser;

    private List<BaseModel> listUser = new ArrayList<>();
    private CallbackObject onDataPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_depot, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData(){
        String bundle = getArguments().getString(Constants.DEPOT);
        if (bundle != null) {
            currentDepot = new BaseModel(bundle);
            edInput.setText(currentDepot.getString("name"));
//            edUser.setFocusable(false);
//            edUser.setText(currentDepot.getBaseModel("user").getString("displayName"));

            if (currentDepot.getInt("isMaster") == 2){
                edUser.setVisibility(View.GONE);
            }else {
                edUser.setVisibility(View.VISIBLE);
                edUser.setFocusable(false);
                edUser.setText(currentDepot.getBaseModel("user").getString("displayName"));
            }

//            switch (currentDepot.getInt("isMaster")) {
//                case 1:
//                    rdDepotType.check(R.id.add_depot_rdmain);
//                    rdUser.setVisibility(View.GONE);
//                    break;
//
//                case 2:
//                    rdDepotType.check(R.id.add_depot_rdtemp);
//                    rdUser.setVisibility(View.GONE);
//                    break;
//
//                case 3:
//                    rdDepotType.check(R.id.add_depot_rduser);
//                    rdUser.setVisibility(View.VISIBLE);
//                    break;
//
//            }

        }

        Util.showKeyboardEditTextDelay(edInput.getEdInput());


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        //rdDepotType.setOnCheckedChangeListener(this);
        //userEvent();
    }

    private void initializeView() {
        //mActivity = (WarehouseActivity) getActivity();
        edInput = (CInputForm) view.findViewById(R.id.add_depot_input);
        edUser = (CInputForm) view.findViewById(R.id.add_depot_user);
        btnSubmit = (Button) view.findViewById(R.id.add_depot_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
//        rdDepotType = view.findViewById(R.id.add_depot_rd);
//        rdUser = view.findViewById(R.id.add_depot_rduser);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                Util.hideKeyboard(v);
                getActivity().getSupportFragmentManager().popBackStack();
                //mActivity.onBackPressed();
                break;

            case R.id.add_depot_submit:
                if (edInput.getText().toString().trim().equals("") || edUser.getText().toString().trim().equals("")) {
                    CustomCenterDialog.alert(null, "Vui lòng điền đầy đủ thông tin", "đồng ý");

                } else {
                    Util.hideKeyboard(v);
                    BaseModel param = createPostParam(ApiUtil.WAREHOUSE_NEW(),
                            String.format(ApiUtil.WAREHOUSE_CREATE_PARAM, !currentDepot.hasKey("id") ? "" : "id=" + currentDepot.getString("id") + "&",
                            Util.encodeString(edInput.getText().toString()),
                            currentDepot.getInt("user_id"),
                            currentDepot.getInt("isMaster")),
                            false,
                            false);

                    new GetPostMethod(param, new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {

                            getActivity().getSupportFragmentManager().popBackStack();
                            //mActivity.loadDepot();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, 1).execute();

                }
                break;
        }
    }

//    private void userEvent() {
//        edUser.setDropdown(true, new CInputForm.ClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listUser.size() > 0) {
//                    dialogChoiceUser(filterListBaseModel(listUser));
//
//                } else {
//                    BaseModel param = createGetParam(ApiUtil.USERS(), true);
//                    new GetPostMethod(param, new NewCallbackCustom() {
//                        @Override
//                        public void onResponse(BaseModel result, List<BaseModel> list) {
//                            listUser = list;
//                            dialogChoiceUser(filterListBaseModel(listUser));
//                        }
//
//                        @Override
//                        public void onError(String error) {
//
//                        }
//                    }, 1).execute();
//
//
//                }
//
//            }
//        });
//    }

    private void dialogChoiceUser(List<BaseModel> list) {
        CustomBottomDialog.choiceListObject("CHỌN NHÂN VIÊN", list, "displayName", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                edUser.setText(object.getString("displayName"));
                currentDepot.put("user_id", object.getInt("id"));

            }
        }, null);
    }

//    private List<BaseModel> filterListBaseModel(List<BaseModel> list) {
//        List<BaseModel> results = new ArrayList<>();
//        for (BaseModel model : list) {
//            if (model.getInt("role") == 1) {
//                results.add(model);
//            }
//
//        }
//        return results;
//    }
}
