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
import wolve.dms.adapter.UserAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class UserActivity extends BaseActivity implements View.OnClickListener, CallbackObject {
    private ImageView btnBack;
    private RecyclerView rvUser;
    private FloatingActionButton btnNew;


    private UserAdapter adapter;
    private List<BaseModel> listUser;
    private boolean gotoUserDetail;


    @Override
    public int getResourceLayout() {
        return R.layout.activity_user;
    }

    @Override
    public int setIdContainer() {
        return R.id.user_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvUser = findViewById(R.id.user_rvusers);
        btnNew = findViewById(R.id.user_add_new);

    }

    @Override
    public void initialData() {
        loadUser();
        btnNew.setVisibility(View.GONE);

//        gotoUserDetail = getIntent().getExtras().getBoolean(Constants.FLAG);
//        if (!gotoUserDetail) {
//
//
//        } else {
//            openFragment(User.getCurrentUser().BaseModelstoString());
//
//        }

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnNew.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.user_add_new:
                openFragment(null);
                break;


        }
    }

    public void loadUser() {
        BaseModel param = createGetParam(ApiUtil.USERS(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listUser = new ArrayList<>(list);
                createRVUser(listUser);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }

    private void createRVUser(List<BaseModel> list) {
        adapter = new UserAdapter(list, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                openFragment(data);

            }
        });
        Util.createLinearRV(rvUser, adapter);

    }

    private void openFragment(String user) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER, user);
        changeFragment(new NewUpdateUserFragment(), bundle, true);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.user_parent);
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        } else if (mFragment != null && mFragment instanceof NewUpdateUserFragment) {
            getSupportFragmentManager().popBackStack();
            if (gotoUserDetail) {
                Transaction.gotoHomeActivityRight(true);
            }

        } else {
            Transaction.gotoHomeActivityRight(true);
        }

    }

    //data return from update user fragment
    @Override
    public void onResponse(BaseModel object){
        adapter.updateUser(object);
        if (User.getId() == object.getInt("id")){
            CustomSQL.setBaseModel(Constants.USER, object);
        }

    }
}
