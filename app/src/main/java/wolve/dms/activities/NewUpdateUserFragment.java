package wolve.dms.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.libraries.connectapi.UploadCloudaryMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

//import wolve.dms.libraries.FileUploader;

/**
 * Created by macos on 9/16/17.
 */

public class NewUpdateUserFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName, edPhone, edGender, edRole, edEmail;
    private TextView tvTitle;
    private Button btnSubmit ;
    private CircleImageView imgUser;

    private UserActivity mActivity;

    private Uri imageChangeUri = null ;
    private BaseModel currentUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_user,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.USER);
        phoneEvent();
        if (bundle != null){
            currentUser = new BaseModel(bundle);
            edName.setText(currentUser.getString("displayName"));
            edPhone.setText(currentUser.getString("phone"));
            edRole.setText(User.getRoleString(currentUser.getInt("role")));
            edGender.setText(currentUser.getInt("gender") == 0? "NAM" : "NỮ");
            edEmail.setText(currentUser.getString("email"));

            if (!Util.checkImageNull(currentUser.getString("image"))){
                Glide.with(mActivity).load(currentUser.getString("image")).centerCrop().into(imgUser);

            }

        }else {
            currentUser = new BaseModel();
            edRole.setText(User.getRoleString(4));
            edGender.setText("NAM");

        }

        if (!CustomSQL.getBoolean(Constants.IS_ADMIN)){
            edPhone.setFocusable(false);
            edRole.setFocusable(false);

        }else {
            edPhone.setFocusable(true);
            roleEvent();
        }

    }


    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgUser.setOnClickListener(this);
        genderEvent();



    }

    private void initializeView() {
        mActivity = (UserActivity) getActivity();

        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        edName = view.findViewById(R.id.add_user_name);
        edPhone = view.findViewById(R.id.add_user_phone);
        edGender = view.findViewById(R.id.add_user_gender);
        edRole = view.findViewById(R.id.add_user_role);
        btnSubmit = view.findViewById(R.id.add_user_submit);
        imgUser = view.findViewById(R.id.add_user_image);
        tvTitle = view.findViewById(R.id.add_user_title);
        edEmail = view.findViewById(R.id.add_user_email);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                mActivity.onBackPressed();

                break;

            case R.id.add_user_submit:
                submitUser();

                break;

            case R.id.add_user_image:
                CustomBottomDialog.choiceThreeOption(getString(R.string.icon_image),
                        "Chọn ảnh thư viện",
                        getString(R.string.icon_camera),
                        "Chụp ảnh",
                        getString(R.string.icon_empty),
                        "Mặc định",
                        new CustomBottomDialog.ThreeMethodListener() {
                            @Override
                            public void Method1(Boolean one) {
                                imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
                                Transaction.startImageChooser(NewUpdateUserFragment.this);
                            }

                            @Override
                            public void Method2(Boolean two) {
                                imageChangeUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider" , Util.getOutputMediaFile());
                                Transaction.startCamera(imageChangeUri);

                            }

                            @Override
                            public void Method3(Boolean three) {
                                imageChangeUri = null;
                                currentUser.put("image", "");
                                Glide.with(mActivity).load( R.drawable.ic_user).fitCenter().into(imgUser);
                            }
                        });

                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE ){
            if (data != null){
                Crop.of(Uri.parse(data.getData().toString()), imageChangeUri).asSquare().withMaxSize(512,512).start(mActivity, NewUpdateUserFragment.this);

            }

        }else if (requestCode == REQUEST_IMAGE_CAPTURE){
            Crop.of(imageChangeUri, imageChangeUri).asSquare().withMaxSize(512,512).start(mActivity);

        }else if (data != null && requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            Glide.with(this).load(imageChangeUri).centerCrop().into(imgUser);

        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(imageChangeUri).centerCrop().into(imgUser);

            } else if (resultCode == Crop.RESULT_ERROR) {
                Util.showToast(Crop.getError(data).getMessage());

            }
        }

    }

    private void uploadImage(final CallbackString mListener){
        Util.getInstance().showLoading();
        new UploadCloudaryMethod(imageChangeUri, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                mListener.Result(result.get("url").toString());
            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Util.showSnackbarError(error);
            }
        }).execute();


    }

    private void phoneEvent(){
        edPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {
                currentUser.put("phone", Util.getPhoneValue(edPhone));

            }
        });
//        if (CustomSQL.getBoolean(Constants.IS_ADMIN)){
//
//
//        }else {
//            edPhone.setFocusable(false);
//        }

    }
    private void genderEvent(){
        edGender.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                List<BaseModel> listGender = new ArrayList<>();
                listGender.add(BaseModel.putValueToNewObject("text", "NAM"));
                listGender.add(BaseModel.putValueToNewObject("text", "NỮ"));

                CustomBottomDialog.choiceListObject("CHỌN GIỚI TÍNH", listGender, "text", new CallbackBaseModel() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edGender.setText(object.getString("text"));

                    }

                    @Override
                    public void onError() {

                    }
                });

            }
        });
    }

    private void roleEvent(){
        edRole.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomDialog.choiceListObject("CHỌN CHỨC VỤ", User.listRole(), "text", new CallbackBaseModel() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edRole.setText(object.getString("text"));
                        currentUser.put("role", object.getInt("index"));
                    }

                    @Override
                    public void onError() {

                    }
                });

            }
        });
    }

    private void submitUser(){
        if (edName.getText().toString().trim().equals("")
                || edPhone.getText().toString().trim().equals("")
                || edEmail.getText().toString().trim().equals("")){
            Util.showSnackbar("Chưa nhập đủ thông tin!", null, null);

        }else if (!Util.isEmailValid(edEmail.getText().toString())){
            Util.showSnackbar("Sai định dạng email!", null, null);

        }else if (imageChangeUri != null){
            uploadImage(new CallbackString() {
                @Override
                public void Result(String s) {
                    updateUser(s);

                }
            });

        }else {
            if (currentUser.getInt("id") ==0){
                updateUser("");
            }else if (currentUser.getString("image").startsWith("http")){
                updateUser(currentUser.getString("image"));
            }else {
                updateUser("");
            }

        }
    }

    private void updateUser(String url){
        String param = String.format(Api_link.USER_CREATE_PARAM,
                currentUser.getInt("id") == 0? "" : "id="+ currentUser.getString("id") +"&",
                Util.encodeString(edName.getText().toString().trim()),
                edGender.getText().toString().equals("NAM")? 0 :1,
                edEmail.getText().toString(),
                Util.getPhoneValue(edPhone),
                User.getIndex(edRole.getText().toString()),
                url);

        UserConnect.CreateUser(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                if (CustomSQL.getBoolean(Constants.IS_ADMIN)){
                    if (result.getInt("id") == User.getId()){
                        CustomSQL.setBaseModel(Constants.USER, result);
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                    mActivity.loadUser();

                }else {

                    Transaction.gotoHomeActivityRight(true);

                }
            }

            @Override
            public void onError(String error) {

            }

        }, true);




    }


}
