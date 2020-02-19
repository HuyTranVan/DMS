package wolve.dms.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.ProductAdapter;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.adapter.UserAdapter;
import wolve.dms.adapter.ViewpagerProductAdapter;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.libraries.MySwipeRefreshLayout;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class UserActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btnBack;
    private RecyclerView rvUser;
    private FloatingActionButton btnNew;


    private UserAdapter adapter;
    private List<BaseModel> listUser ;



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
        if (CustomSQL.getBoolean(Constants.IS_ADMIN)){
            loadUser();

        }else {
            openFragment(User.getCurrentUser().BaseModelstoString());

        }

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnNew.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.user_add_new:
                openFragment(null);
                break;


        }
    }

    public void loadUser() {
        UserConnect.ListUser(new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                listUser = new ArrayList<>(results);
                createRVUser(listUser);

            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    private void createRVUser(List<BaseModel> list){
        adapter = new UserAdapter(list, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                openFragment(data);

            }
        });
        Util.createLinearRV(rvUser, adapter);

    }

    private void openFragment(String user){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER, user);
        changeFragment(new NewUpdateUserFragment(), bundle,true);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.user_parent);
        if(Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);

        }else if(mFragment != null && mFragment instanceof NewUpdateUserFragment) {
            getSupportFragmentManager().popBackStack();
            if (!CustomSQL.getBoolean(Constants.IS_ADMIN)){
                Transaction.gotoHomeActivityRight(true);
            }

        }else {
            Transaction.gotoHomeActivityRight(true);
        }

    }
}
