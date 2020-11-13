package wolve.dms.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class ProductGroupActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private RecyclerView rvProductGroup;
    private ProductGroupAdapter productGroupAdapter;
    private FloatingActionButton btnAddProductGroup;

    public List<BaseModel> listProductGroup;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_product_group;
    }

    @Override
    public int setIdContainer() {
        return R.id.product_group_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvProductGroup = (RecyclerView) findViewById(R.id.product_group_rvgroup);
        btnAddProductGroup = findViewById(R.id.product_group_add_new);

    }

    @Override
    public void initialData() {
        loadProductGroup();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnAddProductGroup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
//                Transaction.gotoHomeActivityRight(true);
                onBackPressed();
                break;

            case R.id.product_group_add_new:
                openFragmentNewProductGroup(null);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.product_group_parent);
        if (mFragment != null && mFragment instanceof NewUpdateProductGroupFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }
    }

    protected void loadProductGroup() {
        BaseModel param = createGetParam(ApiUtil.PRODUCT_GROUPS(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createRVProductGroup(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }


    private void createRVProductGroup(List<BaseModel> list) {
        listProductGroup = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            listProductGroup.add(list.get(i));

        }
        productGroupAdapter = new ProductGroupAdapter(listProductGroup, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                openFragmentNewProductGroup(data);
            }

        }, new CallbackDeleteAdapter() {
            @Override
            public void onDelete(String data, int position) {
                loadProductGroup();
            }
        });
        Util.createLinearRV(rvProductGroup, productGroupAdapter);

    }

    private void openFragmentNewProductGroup(String productgroup) {
        NewUpdateProductGroupFragment groupFragment = new NewUpdateProductGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PRODUCTGROUP, productgroup);
        changeFragment(groupFragment, bundle, true);
    }


}
