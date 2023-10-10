package wolve.dms.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.adapter.ProductUnitAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackObjectAdapter;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ProductUnitActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private RecyclerView rvProductUnit;
    private TextView tvNew, tvClear;
    private EditText edName;
    private RelativeLayout lnParent, lnInput;
    private ProductUnitAdapter unitAdapter;
    public List<BaseModel> mListUnits;
    private BaseModel currentUnit = null;


    @Override
    public int getResourceLayout() {
        return R.layout.activity_product_unit;
    }

    @Override
    public int setIdContainer() {
        return R.id.product_unit_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvProductUnit = (RecyclerView) findViewById(R.id.product_unit_rvunit);
        edName = findViewById(R.id.product_unit_name);
        tvNew = findViewById(R.id.product_unit_addnew);
        tvClear = findViewById(R.id.product_unit_clear);
        lnParent = findViewById(R.id.product_unit_parent);
        lnInput = findViewById(R.id.product_unit_input);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadProductUnit();
//
//    }

    @Override
    public void initialData() {
        loadProductUnit();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvNew.setOnClickListener(this);
        tvClear.setOnClickListener(this);
        Util.textEvent(edName, new CallbackString() {
            @Override
            public void Result(String s) {
                tvClear.setVisibility(Util.isEmpty(s)? View.GONE : View.VISIBLE);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.product_unit_addnew:
                submitUnit();

                break;

            case R.id.product_unit_clear:
                edName.setText("");
                break;

        }
    }

    @Override
    public void onBackPressed() {
        Transaction.returnPreviousActivity();
    }

    protected void loadProductUnit() {
        BaseModel param = createGetParam(ApiUtil.PRODUCT_UNITS(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createRVProductUnit(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }


    private void createRVProductUnit(List<BaseModel> list) {
         mListUnits= new ArrayList<>();


        for (int i = 0; i < list.size(); i++) {
            mListUnits.add(list.get(i));

        }
        unitAdapter = new ProductUnitAdapter(rvProductUnit, mListUnits, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                currentUnit = object;
                edName.setText(currentUnit.getString("name"));
                tvNew.setText("cập nhât");
                Util.showKeyboardEditTextDelay(edName);
            }
        });
        Util.createLinearRV(rvProductUnit, unitAdapter);

    }

    private void submitUnit(){
        if (Util.isEmpty(edName)){
            Util.showSnackbar("Vui lòng nhập tên đơn vị tính!", null, null);

        }else {
            BaseModel param = createPostParam(ApiUtil.PRODUCT_UNIT_NEW(),
                    String.format(ApiUtil.PRODUCTUNIT_CREATE_PARAM,
                            currentUnit == null ? "" : "id=" + currentUnit.getString("id") + "&",
                            Util.encodeString(edName.getText().toString())),
                    false,
                    false);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {

                    if (currentUnit != null){
                        currentUnit = null;
                        unitAdapter.updateItem(result);

                    }else {
                        unitAdapter.addItem(result);

                    }

                    edName.setText("");
                    tvNew.setText("tạo");
                    Util.hideKeyboard(edName);



                }

                @Override
                public void onError(String error) {

                }
            }, 1).execute();

        }

    }


}
