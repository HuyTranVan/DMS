package wolve.dms.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.suke.widget.SwitchButton;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createPostParam;

/**
 * Created by macos on 9/16/17.
 */

public class NewUpdateCFTypeFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName, edKind;
    private Button btnSubmit;
    private RadioGroup rdgStatusDefault;
    private RadioButton rdIncome, rdOutcome;

    private BaseModel currentCFType;
    private CashFlowTypeActivity mActivity;
    private CallbackObject onDataPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_cftype, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.CFTYPE);
        if (bundle != null) {
                currentCFType = new BaseModel(bundle);

        }else {

            currentCFType = new BaseModel();
            currentCFType.put("id", 0);
            currentCFType.put("isDefault", 0);
            currentCFType.put("isIncome", 0);
            currentCFType.put("kind", 4);
        }

        updateView(currentCFType);
    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (CashFlowTypeActivity) getActivity();
        edName = (CInputForm) view.findViewById(R.id.add_cftype_name);
        edKind = (CInputForm) view.findViewById(R.id.add_cftype_kind);
        btnSubmit = (Button) view.findViewById(R.id.add_cftype_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        rdgStatusDefault = (RadioGroup) view.findViewById(R.id.add_cftype_radiogroup);
        rdIncome = (RadioButton) view.findViewById(R.id.add_cftype_income);
        rdOutcome = (RadioButton) view.findViewById(R.id.add_cftype_outcome);


    }

    private void updateView(BaseModel item){
        edName.setText(item.getString("name"));
        Util.showKeyboardEditTextDelay(edName.getEdInput());
        rdIncome.setChecked(item.getInt("isIncome") == 1 ? true : false);
        rdOutcome.setChecked(item.getInt("isIncome") == 0 ? true : false);
        edKind.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomDialog.choiceListObject("CHỌN LOẠI THU CHI", Constants.listCashFlowKind(mActivity.listType.size() >2 ? false : true), "text", new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edKind.setText(object.getString("text"));
                        edKind.setIconText(object.getString("icon"));
                        currentCFType.put("kind", object.getInt("kind"));

                        if (object.getInt("kind") <3){
                            edName.setText(object.getString("text"));
                        }


                    }
                }, null);
            }
        });
        edKind.setText(Constants.cashFlowKind(item.getInt("kind")).getString("text"));
        edKind.setIconText(Constants.cashFlowKind(item.getInt("kind")).getString("icon"));
    }


    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                Util.hideKeyboard(v);
                mActivity.onBackPressed();
                break;

            case R.id.add_cftype_submit:
                submitCashFlowType(currentCFType);

                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.add_cftype_income:
                currentCFType.put("isIncome", 1);
                break;

            case R.id.add_cftype_outcome:
                currentCFType.put("isIncome", 0);
                break;
        }
    }


    private void submitCashFlowType(BaseModel item){
        if (Util.isEmpty(edName.getEdInput())) {
            CustomCenterDialog.alert(null, "Vui lòng nhập đủ thông tin", "đồng ý");

        } else {
            BaseModel param = createPostParam(ApiUtil.CASHFLOWTYPE_NEW(),
                    String.format(ApiUtil.CFTYPE_CREATE_PARAM, item.getInt("id") == 0? "" : "id=" + item.getString("id") + "&",
                            Util.encodeString(edName.getText().toString()),
                            rdIncome.isChecked() ? 1 : 0,
                            currentCFType.getInt("kind")),
                    false,
                    false);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    onDataPass.onResponse(result);
                    getActivity().getSupportFragmentManager().popBackStack();
                    //mActivity.loadStatus();
                }

                @Override
                public void onError(String error) {

                }
            }, 1).execute();

        }
    }
}
