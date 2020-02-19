package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class NewUpdateDepotFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edInput, edUser;
    private Button btnSubmit;
    private BaseModel currentDepot;
    private WarehouseActivity mActivity;
    private RadioGroup rdDepotType;

    private List<BaseModel> listUser = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_depot,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.DEPOT);
        if (bundle != null){
            currentDepot = new BaseModel(bundle);
            edInput.setText(currentDepot.getString("name"));
            edUser.setText(currentDepot.getBaseModel("user").getString("displayName"));
            switch (currentDepot.getInt("isMaster")){
                case 1:
                    rdDepotType.check(R.id.add_depot_rdmain);
                    break;

                case 2:
                    rdDepotType.check(R.id.add_depot_rdtemp);
                    break;

                case 3:
                    rdDepotType.check(R.id.add_depot_rduser);
                    break;

                default:
                    currentDepot.put("isMaster", 3);
                    rdDepotType.check(R.id.add_depot_rduser);

            }

        }else {
            currentDepot = new BaseModel();
            rdDepotType.check(R.id.add_depot_rduser);
        }

        Util.showKeyboardEditTextDelay(edInput.getEdInput());


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        rdDepotType.setOnCheckedChangeListener(this);
        userEvent();
    }

    private void initializeView() {
        edInput = (CInputForm) view.findViewById(R.id.add_depot_input);
        edUser = (CInputForm) view.findViewById(R.id.add_depot_user);
        btnSubmit = (Button) view.findViewById(R.id.add_depot_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        rdDepotType = view.findViewById(R.id.add_depot_rd);

        mActivity = (WarehouseActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                Util.hideKeyboard(v);
                mActivity.onBackPressed();
                break;

            case R.id.add_depot_submit:
                if (edInput.getText().toString().trim().equals("")|| edUser.getText().toString().trim().equals("")){
                    CustomCenterDialog.alert(null, "Vui lòng điền đầy đủ thông tin", "đồng ý");

                }else {
                    String param = String.format(Api_link.WAREHOUSE_CREATE_PARAM, !currentDepot.hasKey("id")? "" : "id="+ currentDepot.getString("id") +"&",
                                Util.encodeString(edInput.getText().toString()),
                            currentDepot.getInt("user_id"),
                            currentDepot.getInt("isMaster"));

                    SystemConnect.CreateDepot(param, new CallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            mActivity.loadDepot();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, true);
                }
                break;
        }
    }

    private void userEvent(){
        edUser.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                if (listUser.size() >0){
                    dialogChoiceUser(listUser);

                }else {
                    UserConnect.ListUser(new CallbackCustomList() {
                        @Override
                        public void onResponse(List<BaseModel> results) {
                            listUser = results;
                            dialogChoiceUser(listUser);

                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, true);

                }

            }
        });
    }

    private void dialogChoiceUser(List<BaseModel> list){
        CustomBottomDialog.choiceListObject("CHỌN NHÂN VIÊN", list, "displayName", new CallbackBaseModel() {
            @Override
            public void onResponse(BaseModel object) {
                edUser.setText(object.getString("displayName"));
                currentDepot.put("user_id", object.getInt("id"));

            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.add_depot_rdmain:
                currentDepot.put("isMaster", 1);
                break;

            case R.id.add_depot_rdtemp:
                currentDepot.put("isMaster", 2);
                break;

            case R.id.add_depot_rduser:
                currentDepot.put("isMaster", 3);
                break;
        }
    }
}
