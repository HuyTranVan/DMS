package wolve.dms.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

import static wolve.dms.activities.BaseActivity.createPostParam;

/**
 * Created by macos on 9/16/17.
 */

public class NewUpdateProductGroupFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edInput, edIsSales;
    private Button btnSubmit;
    private BaseModel productGroup;
    private ProductGroupActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_productgroup, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        edIsSales.setText(Constants.NO_SALES);
        String bundle = getArguments().getString(Constants.PRODUCTGROUP);
        if (bundle != null) {
            productGroup = new BaseModel(bundle);
            edInput.setText(productGroup.getString("name"));
            edIsSales.setText(productGroup.getInt("isSales") == 1 ? Constants.IS_SALES : Constants.NO_SALES);

        }

        Util.showKeyboardEditTextDelay(edInput.getEdInput());


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        isSalesEvent();
    }

    private void isSalesEvent() {
        edIsSales.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomDropdow.createListDropdown(
                        edIsSales.getMoreButton(),
                        DataUtil.createList2String(Util.getIconString(R.string.icon_product_group, "  ", Constants.IS_SALES),
                                                    Util.getIconString(R.string.icon_empty, "   ", Constants.NO_SALES)),
                        0,
                        false,
                        new CallbackClickAdapter() {
                            @Override
                            public void onRespone(String data, int pos) {
                                if (pos ==0){
                                    edIsSales.setText(Constants.IS_SALES);

                                }else if(pos ==1) {
                                    edIsSales.setText(Constants.NO_SALES);

                                }

                            }

                        });

            }
        });
    }

    private void initializeView() {
        edInput = (CInputForm) view.findViewById(R.id.add_productgroup_input);
        edIsSales = (CInputForm) view.findViewById(R.id.add_productgroup_sales);
        btnSubmit = (Button) view.findViewById(R.id.add_productgroup_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);

        mActivity = (ProductGroupActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                Util.hideKeyboard(v);
                mActivity.onBackPressed();
                break;

            case R.id.add_productgroup_submit:
                if (edInput.getText().toString().trim().equals("")) {
                    Util.showSnackbar("Tên nhóm không được để trống!", null, null);

                } else {
                    BaseModel param = createPostParam(ApiUtil.PRODUCT_GROUP_NEW(),
                            String.format(ApiUtil.PRODUCTGROUP_CREATE_PARAM,
                                                    productGroup == null ? "" : "id=" + productGroup.getString("id") + "&",
                                                    Util.encodeString(edInput.getText().toString()),
                                                    edIsSales.getText().equals(Constants.IS_SALES)? 1 :0),
                            false,
                            false);
                    new GetPostMethod(param, new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            mActivity.loadProductGroup();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, 1).execute();

                    break;
                }
        }
    }
}
