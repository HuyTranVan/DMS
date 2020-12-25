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
import wolve.dms.adapter.CashFlowTypeAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class CashFlowTypeActivity extends BaseActivity implements View.OnClickListener, CallbackObject {
    private ImageView btnBack;
    private RecyclerView rvStatus;
    private FloatingActionButton btnAddStatus;

    private CashFlowTypeAdapter adapter;
    protected List<BaseModel> listType;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_cftype;
    }

    @Override
    public int setIdContainer() {
        return R.id.status_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvStatus = (RecyclerView) findViewById(R.id.status_rvstatus);
        btnAddStatus = findViewById(R.id.status_addnew);

    }

    @Override
    public void initialData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCFType();
    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnAddStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.status_addnew:
                openFragmentNewCFType(null);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.status_parent);
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        } else if (mFragment != null && mFragment instanceof NewUpdateCFTypeFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }

    }


    protected void loadCFType() {
        BaseModel param = createGetParam(ApiUtil.CASHFLOWTYPES(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createRVCFType(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }


    private void createRVCFType(List<BaseModel> list) {
        listType = new ArrayList<>();
        adapter = new CashFlowTypeAdapter(list, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                openFragmentNewCFType(data);
            }

        }, new CallbackDeleteAdapter() {
            @Override
            public void onDelete(String data, int position) {
                loadCFType();
            }
        });
        Util.createLinearRV(rvStatus, adapter);
    }


    private void openFragmentNewCFType(String status) {
        NewUpdateCFTypeFragment statusFragment = new NewUpdateCFTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CFTYPE, status);
        changeFragment(statusFragment, bundle, true);
    }


    @Override
    public void onResponse(BaseModel object) {
        adapter.updateType(object);
    }
}
