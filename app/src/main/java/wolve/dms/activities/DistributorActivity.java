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
import wolve.dms.adapter.DistributorAdapter;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class DistributorActivity extends BaseActivity implements View.OnClickListener, CallbackObject {
    private ImageView btnBack;
    private RecyclerView rvDistributor;
    private DistributorAdapter distributorAdapter;
    private FloatingActionButton btnAddDistributor;

    public List<BaseModel> listDistributor;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_distributor;
    }

    @Override
    public int setIdContainer() {
        return R.id.distributor_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvDistributor = (RecyclerView) findViewById(R.id.distributor_rvgroup);
        btnAddDistributor = findViewById(R.id.distributor_add_new);

    }

    @Override
    public void initialData() {
        loadDistributors();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnAddDistributor.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
//                Transaction.gotoHomeActivityRight(true);
                onBackPressed();
                break;

            case R.id.distributor_add_new:
                openFragmentNewDistributor(null);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.distributor_parent);
        if (mFragment != null && mFragment instanceof NewUpdateDistributorFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }
    }

    protected void loadDistributors() {
        BaseModel param = createGetParam(ApiUtil.DISTRIBUTORS(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createRVDistributor(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }


    private void createRVDistributor(List<BaseModel> list) {
        listDistributor = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            listDistributor.add(list.get(i));

        }
        distributorAdapter = new DistributorAdapter(listDistributor, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                openFragmentNewDistributor(object.BaseModelstoString());
            }
        });
        Util.createLinearRV(rvDistributor, distributorAdapter);

    }

    private void openFragmentNewDistributor(String distributor) {
        NewUpdateDistributorFragment groupFragment = new NewUpdateDistributorFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DISTRIBUTOR, distributor);
        changeFragment(groupFragment, bundle, true);
    }

    @Override
    public void onResponse(BaseModel object) {
        distributorAdapter.updateDistributor(object);
    }
}
