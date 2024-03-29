package wolve.dms.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.libraries.CustomSwitchButton;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomFixSQL;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

//import wolve.dms.libraries.FileUploader;

/**
 * Created by macos on 9/16/17.
 */


public class SettingActivity extends BaseActivity implements View.OnClickListener, CallbackObject, CompoundButton.OnCheckedChangeListener {
    private ImageView btnBack;
    private TextView tvDisplayname, tvRole, tvChangePassword, tvChangeUserTitle, tvChangeUser,
            tvLogout, tvInfo, tvContact, tvShare, tvShareDetail,
            tvMapStyle, tvSaveImage;
    private CircleImageView imgUser, imgFirstImage;
    private Fragment mFragment;
    private SwitchCompat swSaveContact, swMapStyle, swSaveImage;
    private RelativeLayout lnShare;

    private BaseModel currentUser, firstUser;
    private List<BaseModel> users;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public int setIdContainer() {
        return R.id.user_option_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        tvChangePassword = findViewById(R.id.user_option_change_password);
        tvChangeUserTitle = findViewById(R.id.user_option_change_user_title);
        imgFirstImage = findViewById(R.id.user_option_firstuser);
        tvChangeUser = findViewById(R.id.user_option_change_user);
        tvLogout = findViewById(R.id.user_option_logout);
        tvDisplayname = findViewById(R.id.user_option_username);
        tvRole = findViewById(R.id.user_option_role);
        tvContact = findViewById(R.id.user_option_change_contact);
        imgUser = findViewById(R.id.user_option_image);
        tvInfo = findViewById(R.id.user_option_change_info);
        swSaveContact = findViewById(R.id.user_option_change_contact_sw);
        tvMapStyle = findViewById(R.id.user_option_mapstyle);
        swMapStyle = findViewById(R.id.user_option_mapstyle_sw);
        tvSaveImage = findViewById(R.id.user_option_saveimage);
        swSaveImage = findViewById(R.id.user_option_saveimage_sw);
        tvShare = findViewById(R.id.user_option_share);
        tvShareDetail= findViewById(R.id.user_option_share_detail);
        lnShare = findViewById(R.id.user_option_share_parent);
    }

    @Override
    public void initialData() {
        currentUser = User.getCurrentUser();
        users = DataUtil.removeObjectFromList(CustomFixSQL.getListObject(Constants.USER_LIST), User.getCurrentUser(), "id");
        tvDisplayname.setText(User.getFullName());
        String role = User.getCurrentRoleString();
        tvRole.setText(  role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase());
        if (!Util.checkImageNull(User.getImage())) {
            Glide.with(this).load(User.getImage()).centerCrop().into(imgUser);
        }
        tvChangePassword.setText(Util.getIconString(R.string.icon_key, "      ", "Đổi mật khẩu"));
        tvChangeUserTitle.setText(Util.getIconString(R.string.icon_group, "    ", "Chuyển tài khoản"));

        tvLogout.setText(Util.getIconString(R.string.icon_logout, "    ", "Đăng xuất"));
        tvInfo.setText(Util.getIconString(R.string.icon_info, "      ", "Thông tin tài khoản"));
        tvSaveImage.setText(Util.getIconString(R.string.icon_image, "      ", "Lưu hình sản phẩm"));
        tvMapStyle.setText(Util.getIconString(R.string.icon_edit_map, "      ", "Định dạng bản đồ Google Map"));
        tvContact.setText(Util.getIconString(R.string.icon_contact, "      ", "Tự động lưu danh bạ"));
        tvShare.setText(Util.getIconString(R.string.icon_share, "      ", "Chia sẻ mặc định"));
        swSaveContact.setChecked(CustomFixSQL.getBoolean(Constants.AUTO_SAVE_CONTACT));
        swSaveImage.setChecked(CustomFixSQL.getBoolean(Constants.SAVE_PRODUCT_IMAGE));
        swMapStyle.setChecked(CustomFixSQL.getBoolean(Constants.SET_DEFAULT_MAPSTYLE));
        if (users.size() > 0){
            imgFirstImage.setVisibility(View.VISIBLE);
            firstUser = User.getFirstUser(users);
            Glide.with(this).load(firstUser.getString("image")).centerCrop().into(imgFirstImage);

            imgFirstImage.setOnClickListener(this);
        }else {
            imgFirstImage.setVisibility(View.GONE);
        }
        switch (CustomFixSQL.getInt(Constants.PACKAGE_DEFAULT)){
            case 0:
                tvShareDetail.setText("Tùy chọn");
                break;

            case 1:
                tvShareDetail.setText("Zalo");
                break;

            case 2:
                tvShareDetail.setText("Facebook Messenger");
                break;

        }
    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvInfo.setOnClickListener(this);
        tvChangePassword.setOnClickListener(this);
        tvChangeUser.setOnClickListener(this);
        tvChangeUserTitle.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        imgFirstImage.setOnClickListener(this);
        //swSaveContact.setOnCheckedChangeListener(this);

        lnShare.setOnClickListener(this);
        mapStyleEvent();
        saveContactEvent();
        saveProductImageEvent();
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();

                break;

            case R.id.user_option_change_password:
                changePassword();
                break;

            case R.id.user_option_change_user:
                changeUser();
                break;

            case R.id.user_option_change_user_title:
                changeUser();
                break;

            case R.id.user_option_firstuser:
                login(firstUser.getString("phone"),
                        firstUser.getString("password_local"),
                        firstUser.getString("token"),
                        new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                if (result) {
                                    CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, true);
                                    Util.showToast("Đăng nhập thành công");
                                    onBackPressed();

                                } else {
                                    Util.showToast("Đăng nhập thất bại!");

                                }
                            }
                        });
                break;

            case R.id.user_option_logout:
                showLogoutDialog();
                break;

            case R.id.user_option_change_info:
                Bundle bundle = new Bundle();
                bundle.putString(Constants.USER, User.getCurrentUserString());
                changeFragment(new NewUpdateUserFragment(), bundle, true);
                break;

            case R.id.user_option_share_parent:
                CustomBottomDialog.choiceListObject("Chọn ứng dụng chia sẻ mặc định",
                        Constants.listSharePackage(),
                        "text",
                        new CallbackObject() {
                            @Override
                            public void onResponse(BaseModel object) {
                                tvShareDetail.setText(object.getString("text"));
                                CustomFixSQL.setInt(Constants.PACKAGE_DEFAULT, object.getInt("position"));
                            }
                        }, null);
                break;


        }
    }



    private void changeUser() {
        CustomCenterDialog.dialogChangeUser("Đổi sang tài khoản",
                users,
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
        CustomCenterDialog.alertWithCancelButton(null,
                String.format("Đăng xuất tài khoản %s", User.getFullName()),
                "ĐỒNG Ý",
                "HỦY",
                new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    logout(null);
                }

            }
        });


    }

    private void showReloginDialog(BaseModel user){
        String title = user == null ? "Đăng nhập tài khoản" : String.format("Đăng nhập tài khoản %s", user.getString("displayName"));
        CustomCenterDialog.showDialogRelogin(title, user, new CallbackBoolean() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRespone(Boolean result) {
                if (result) {
                    CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, true);
                    Util.showToast("Đăng nhập thành công");
                    onBackPressed();
                    //mActivity.initialData();


                } else {
                    Util.showToast("Đăng nhập thất bại!");

                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        mFragment = getSupportFragmentManager().findFragmentById(R.id.user_option_parent);

        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        }else if (mFragment != null && mFragment instanceof NewUpdateUserFragment) {
            getSupportFragmentManager().popBackStack();

        }else {
            Transaction.gotoHomeActivityRight(true);
        }


    }


    @Override
    public void onResponse(BaseModel object) {
        CustomSQL.setBaseModel(Constants.USER, object);
        initialData();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        CustomFixSQL.setInt(Constants.AUTO_SAVE_CONTACT, b? 1 : 0);
    }

    private void mapStyleEvent(){
        swMapStyle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CustomFixSQL.setBoolean(Constants.SET_DEFAULT_MAPSTYLE, isChecked);
            }
        });

    }

    private void saveContactEvent(){
        swSaveContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CustomFixSQL.setBoolean(Constants.AUTO_SAVE_CONTACT, isChecked);
            }
        });

    }

    private void saveProductImageEvent(){
        swSaveImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CustomFixSQL.setBoolean(Constants.SAVE_PRODUCT_IMAGE, isChecked);
            }
        });

    }
}
