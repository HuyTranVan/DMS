package wolve.dms.activities;

import static wolve.dms.activities.BaseActivity.createGetParam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.AdminsAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Util;

public class AdminsFragment extends Fragment implements View.OnClickListener {
    private View view;
    private DistributorDetailActivity mActivity;
    private RecyclerView rvAdmin;
    private LinearLayout btnAdminNew ;
    private TextView tvAddTitle;

    private List<BaseModel> mUsers = new ArrayList<>();
    private String TEXT_NEW_ADMIN = "tạo quản trị viên";
    private String TEXT_NEW_USER = "tạo nhân viên";
    private AdminsAdapter adminAdapter;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admins, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void initializeView() {
        mActivity = (DistributorDetailActivity) getActivity();
        btnAdminNew = view.findViewById(R.id.distributor_detail_add_admin);
        rvAdmin = view.findViewById(R.id.distributor_detail_rvadmin);
        tvAddTitle = view.findViewById(R.id.distributor_detail_add_admin_title);



    }

    private void intitialData(){
        adminAdapter = new AdminsAdapter();
        Util.createLinearRV(rvAdmin, adminAdapter);
        loadUsers();

    }


    private void addEvent() {
        btnAdminNew.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.distributor_detail_add_admin:
                dialogNewAdmin();

                break;

        }

    }

    private void loadUsers(){
        BaseModel param = createGetParam(String.format("%s?distributor_id=%d", ApiUtil.USERS(), mActivity.currentDistributor.getInt("id")), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                mUsers = list;
                adminAdapter.updateData(mUsers);
                tvAddTitle.setText(mUsers.size() >0? TEXT_NEW_USER : TEXT_NEW_ADMIN);


            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();
    }

    private void dialogNewAdmin(){
        CustomCenterDialog.showDialogNewAdmin(mUsers.size() >0 ? "Tạo nhân viên" : "Tạo quản trị viên",
                mActivity.currentDistributor.getInt("id"),
                mUsers.size() >0 ? 3 : 1,
                mUsers.size() >0?  false : true,
                new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object){
                        //mUsers.add(object);
                        adminAdapter.addItem(object);
                        mActivity.currentDistributor.put("count_user", adminAdapter.getItemCount());
                        mActivity.updateView(mActivity.currentDistributor);
                        Util.showToast("Tạo nhân viên thành công");


                    }
                });
    }

}
