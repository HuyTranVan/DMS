package wolve.dms.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wolve.dms.R;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CInputForm;
import wolve.dms.models.Product;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class AddProductFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName, edUnitPrice, edPurchasePrice, edGroup, edVolume, edIsPromotion;
    private Button btnSubmit;
    private Product product;
    private ProductActivity mActivity;

    private ArrayList<String> listGroup = new ArrayList<>();
    private ArrayList<String> listBoolean = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        for (int i=0; i<mActivity.listProductGroup.size(); i++){
            listGroup.add(mActivity.listProductGroup.get(i).getString("name"));
        }

        listBoolean.add(0,Constants.IS_PROMOTION);
        listBoolean.add(1,Constants.NO_PROMOTION);

        edGroup.setDropdownList(listGroup);
        edGroup.setText(listGroup.get(0));

        edIsPromotion.setDropdownList(listBoolean);
        edIsPromotion.setText(listBoolean.get(0));

        String bundle = getArguments().getString(Constants.PRODUCT);
        if (bundle != null){
            try {
                product = new Product(new JSONObject(bundle));

                edName.setText(product.getString("name"));
                edUnitPrice.setText(product.getString("unitPrice"));
                edPurchasePrice.setText(product.getString("purchasePrice"));
                edGroup.setText(new JSONObject(product.getString("productGroup")).getString("name"));
                edVolume.setText(product.getString("volume"));
                edIsPromotion.setText(product.getBoolean("promotion")? Constants.IS_PROMOTION :Constants.NO_PROMOTION);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void initializeView() {
        edName = (CInputForm) view.findViewById(R.id.add_product_name);
        edUnitPrice = (CInputForm) view.findViewById(R.id.add_product_unit_price);
        edPurchasePrice = (CInputForm) view.findViewById(R.id.add_product_purchase_price);
        edGroup = (CInputForm) view.findViewById(R.id.add_product_group);
        edVolume = (CInputForm) view.findViewById(R.id.add_product_volume);
        edIsPromotion = (CInputForm) view.findViewById(R.id.add_product_promotion);
        btnSubmit = (Button) view.findViewById(R.id.add_product_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);

        mActivity = (ProductActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                Util.hideKeyboard(v);
                break;

            case R.id.add_product_submit:
                submitProduct();
                break;
        }
    }

    private int defineGroupId(String groupName){
        int id=0;
        for (int i=0; i<mActivity.listProductGroup.size(); i++){
            if (mActivity.listProductGroup.get(i).getString("name").equals(groupName)){
                id = mActivity.listProductGroup.get(i).getInt("id");
            }
        }
        return id;
    }

    private void submitProduct(){
        if (edName.getText().toString().trim().equals("")
                || edUnitPrice.getText().toString().trim().equals("")
                || edPurchasePrice.getText().toString().trim().equals("")){
            Util.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {

                }
            });

        }else {
            String param = String.format(Constants.PRODUCT_CREATE_PARAM, product == null? "" : "id="+ product.getString("id") +"&",
                    Util.encodeString(edName.getText().toString()),
                    edIsPromotion.getText().toString().trim().equals(Constants.IS_PROMOTION) ? true: false,
                    Integer.parseInt(edUnitPrice.getText().toString().trim().replace(",","")),
                    Integer.parseInt(edPurchasePrice.getText().toString().trim().replace(",","")),
                    Integer.parseInt(edVolume.getText().toString().trim().replace(",","")),
                    defineGroupId(edGroup.getText().toString().trim()));


            ProductConnect.CreateProduct(param, new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {

                    getActivity().getSupportFragmentManager().popBackStack();
                    mActivity.loadProductGroup();
                }

                @Override
                public void onError(String error) {

                }
            }, true);
        }
    }
}
