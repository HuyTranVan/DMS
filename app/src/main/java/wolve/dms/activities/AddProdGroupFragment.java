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

import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class AddProdGroupFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edInput;
    private Button btnSubmit;
    private ProductGroup productGroup;
    private ProductGroupActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_productgroup,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.PRODUCTGROUP);
        if (bundle != null){
            try {
                productGroup = new ProductGroup(new JSONObject(bundle));
                edInput.setText(productGroup.getString("name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Util.showKeyboardEditTextDelay(edInput.getEdInput());


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void initializeView() {
        edInput = (CInputForm) view.findViewById(R.id.add_productgroup_input);
        btnSubmit = (Button) view.findViewById(R.id.add_productgroup_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);

        mActivity = (ProductGroupActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                Util.hideKeyboard(v);
                break;

            case R.id.add_productgroup_submit:
                if (edInput.getText().toString().trim().equals("")){
                    CustomCenterDialog.alertWithCancelButton(null, "Nhập tên nhóm sản phẩm", "đồng ý", null, new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {

                        }
                    });

                }else {
                    String param = String.format(Api_link.PRODUCTGROUP_CREATE_PARAM, productGroup == null? "" : "id="+ productGroup.getString("id") +"&",
                                Util.encodeString(edInput.getText().toString()));


                    ProductConnect.CreateProductGroup(param, new CallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            mActivity.loadProductGroup();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, true);
                }
                break;
        }
    }
}
