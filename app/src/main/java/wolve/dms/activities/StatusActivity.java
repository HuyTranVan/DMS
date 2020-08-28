package wolve.dms.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.StatusAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Status;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatusActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private RecyclerView rvStatus;
    private FloatingActionButton btnAddStatus;

    private StatusAdapter statusAdapter;
    private ArrayList<Status> listStatus;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_status;
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
        loadStatus();
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
                openFragmentNewStatus(null);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.status_parent);
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        } else if (mFragment != null && mFragment instanceof NewUpdateStatusFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }

    }


    protected void loadStatus() {
        BaseModel param = createGetParam(ApiUtil.STATUS(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createRVProductGroup(list);
            }

            @Override
            public void onError(String error) {

            }
        }, true).execute();

    }


    private void createRVProductGroup(List<BaseModel> list) {
        listStatus = new ArrayList<>();
        statusAdapter = new StatusAdapter(list, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                openFragmentNewStatus(data);
            }

        }, new CallbackDeleteAdapter() {
            @Override
            public void onDelete(String data, int position) {
                loadStatus();
            }
        });
        rvStatus.setAdapter(statusAdapter);
        rvStatus.setHasFixedSize(true);
        rvStatus.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvStatus.setLayoutManager(layoutManager);
    }


    private void openFragmentNewStatus(String status) {
        NewUpdateStatusFragment statusFragment = new NewUpdateStatusFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.STATUS, status);
        changeFragment(statusFragment, bundle, true);
    }


}
