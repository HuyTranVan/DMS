package wolve.dms.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.StatusAdapter;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.models.Status;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;

/**
 * Created by macos on 9/16/17.
 */

public class StatusActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btnBack;
    private RecyclerView rvStatus;
    private ImageView btnAddStatus;

    private StatusAdapter statusAdapter;
    private ArrayList<Status> listStatus ;

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
        btnAddStatus = (ImageView) findViewById(R.id.status_addnew);

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
        switch (v.getId()){
            case R.id.icon_back:
                Transaction.gotoHomeActivityRight(true);
                break;

            case R.id.status_addnew:
                openFragmentNewStatus(null);

                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.status_parent);
            if(mFragment != null && mFragment instanceof AddStatusFragment) {
                getSupportFragmentManager().popBackStack();
            }else {
                Transaction.gotoHomeActivityRight(true);
            }

        }
        return true;
    }


    protected void loadStatus() {
        StatusConnect.ListStatus(new CallbackJSONArray() {
            @Override
            public void onResponse(JSONArray result) {
                createRVProductGroup(result);

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }


    private void createRVProductGroup(JSONArray jsonArray){
        listStatus = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++){
            try {
                Status status = new Status(jsonArray.getJSONObject(i));
                listStatus.add(status);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        statusAdapter = new StatusAdapter(listStatus, new CallbackClickAdapter() {
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


    private void openFragmentNewStatus(String status){
        AddStatusFragment statusFragment = new AddStatusFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.STATUS, status);
        changeFragment(statusFragment, bundle, true );
    }


}
