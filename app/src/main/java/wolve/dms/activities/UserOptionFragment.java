package wolve.dms.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomFixSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.models.Product.getProductList;

//import wolve.dms.libraries.FileUploader;

/**
 * Created by macos on 9/16/17.
 */

public class UserOptionFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private TextView tvTitle, tvDisplayname, tvRole, tvChangePassword, tvChangeUser, tvLogout;
    private CircleImageView imgUser;
    private RelativeLayout lnUser;

    private HomeActivity mActivity;
    private BaseModel currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_option, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        currentUser = User.getCurrentUser();
        tvDisplayname.setText(User.getFullName());
        tvRole.setText(User.getCurrentRoleString());
        if (!Util.checkImageNull(User.getImage())) {
            Glide.with(this).load(User.getImage()).centerCrop().into(imgUser);

        }
        tvChangePassword.setText(Util.getIconString(R.string.icon_password, "      ", "Đổi mật khẩu"));
        tvChangeUser.setText(Util.getIconString(R.string.icon_group, "    ", "Tài khoản khác"));
        tvLogout.setText(Util.getIconString(R.string.icon_password, "      ", "Đăng xuất"));

    }


    private void addEvent() {
        btnBack.setOnClickListener(this);
        lnUser.setOnClickListener(this);
        tvChangePassword.setOnClickListener(this);
        tvChangeUser.setOnClickListener(this);
        tvLogout.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (HomeActivity) getActivity();
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        tvChangePassword = view.findViewById(R.id.user_option_change_password);
        tvChangeUser = view.findViewById(R.id.user_option_change_user);
        tvLogout = view.findViewById(R.id.user_option_logout);
        tvDisplayname = view.findViewById(R.id.user_option_username);
        tvRole = view.findViewById(R.id.user_option_role);
        lnUser = view.findViewById(R.id.user_option_user_parent);
        imgUser = view.findViewById(R.id.user_option_image);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();

                break;

            case R.id.user_option_change_password:
                changePassword();
                break;

            case R.id.user_option_change_user:
                changeUser();
                break;

            case R.id.user_option_logout:
                showLogoutDialog();
                break;

            case R.id.user_option_user_parent:
                Transaction.gotoUserActivity(true);
                break;


        }
    }

    private void changePassword() {
        CustomCenterDialog.showDialogChangePass("Đổi mật khẩu", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    CustomCenterDialog.alertWithButtonCanceled("", "Đổi mật khẩu thành công , vui lòng đăng nhập lại", "ĐỒNG Ý", false, new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result) {
                                mActivity.logout();
                            }

                        }
                    });

                }
            }
        });

    }

    private void changeUser() {
        List<BaseModel> users = CustomFixSQL.getListObject(Constants.USER_LIST);
        CustomCenterDialog.dialogChangeUser("ĐỔI SANG TÀI KHOẢN",
                DataUtil.removeObjectFromList(users, User.getCurrentUser(), "id"),
                new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        if (object == null) {
                            showReloginDialog(null);

                        } else {
                            showReloginDialog(object);

                        }
                    }
                });

    }


    private void showLogoutDialog() {
//        List<BaseModel> preProduct = getProductList();
//        ContentResolver resolver = Util.getInstance().getCurrentActivity().getContentResolver();
//        for (BaseModel model: preProduct){
//            if (!model.getString("image_uri").equals("")){
//                Uri uri = Uri.parse(model.getString("image_uri"));
//                resolver.delete(uri, null, null);
//            }
//
//        }
        String s = CustomFixSQL.getString(Constants.IMAGES);
        List<String> currentList = new ArrayList<String>(Arrays.asList(s.split(",")));


        CustomCenterDialog.alertWithCancelButton(null, String.format("Đăng xuất tài khoản %s", User.getFullName()), "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    mActivity.logout();
                }

            }
        });


    }

    private void showReloginDialog(BaseModel user) {
        String title = user == null ? "ĐĂNG NHẬP TÀI KHOẢN" : String.format("Đăng nhập tài khoản %s", user.getString("displayName"));
        CustomCenterDialog.showDialogRelogin(title, user, new CallbackBoolean() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    mActivity.onBackPressed();
                    mActivity.initialData();


                } else {
                    Util.showToast("Đăng nhập thất bại!");

                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CHOOSE_IMAGE ){
//            if (data != null){
//                Crop.of(Uri.parse(data.getData().toString()), imageChangeUri).asSquare().withMaxSize(512,512).start(mActivity, UserOptionFragment.this);
//
//            }
//
//        }else if (requestCode == REQUEST_IMAGE_CAPTURE){
//            //Uri tempUri = imageChangeUri;
//            Crop.of(imageChangeUri, imageChangeUri).asSquare().withMaxSize(512,512).start(mActivity, UserOptionFragment.this);
//
//        }else if (data != null && requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            Glide.with(this).load(imageChangeUri).centerCrop().into(imgUser);
//
//        } else if (requestCode == Crop.REQUEST_CROP){
//            if (resultCode == RESULT_OK) {
//                Glide.with(this).load(imageChangeUri).centerCrop().into(imgUser);
//
//            } else if (resultCode == Crop.RESULT_ERROR){
//                Util.showToast(Crop.getError(data).getMessage());
//
//            }
//        }

    }


}
