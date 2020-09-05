package wolve.dms.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.apiconnect.apiserver.UploadCloudaryMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.CallbackUri;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomInputDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static wolve.dms.activities.BaseActivity.createGetParam;
import static wolve.dms.activities.BaseActivity.createPostParam;
import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

//import wolve.dms.libraries.FileUploader;

/**
 * Created by macos on 9/16/17.
 */

public class ResetPasswordFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edPhone, edEmail;
    private Button btnSubmit;

    private LoginActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        Util.showKeyboardDelay(edPhone.getEdInput());

    }


    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        edPhone.addTextChangePhone(null);


    }

    private void initializeView() {
        mActivity = (LoginActivity) getActivity();
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        edPhone = view.findViewById(R.id.reset_pass_phone);
        edEmail = view.findViewById(R.id.reset_pass_email);
        btnSubmit = view.findViewById(R.id.reset_pass_submit);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();

                break;

            case R.id.reset_pass_submit:
                submitUser();

                break;



        }
    }

    private void submitUser() {
        if ( edPhone.getText().toString().trim().equals("") || edEmail.getText().toString().trim().equals("")) {
            Util.showSnackbar("Chưa nhập đủ thông tin!", null, null);

        } else if(Util.isPhoneFormat(edPhone.getText().toString().trim()) == null){
            Util.showSnackbar("Sai số điện thoại!", null, null);

        } else if (!Util.isEmailValid(edEmail.getText().toString())) {
            Util.showSnackbar("Sai định dạng email!", null, null);

        } else {
            BaseModel param = createPostParam(ApiUtil.RESET_PASSWORD(),
                    String.format(ApiUtil.RESET_PASSWORD_PARAM, Util.getPhoneValue(edPhone), edEmail.getText().toString().trim()),
                    false,
                    false);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    CustomCenterDialog.alertWithButtonCanceled("Đã cập nhật",
                            "Đã gửi email chứa mật khẩu đăng nhập đến " + edEmail.getText().toString(),
                            "Đồng ý",
                            false,
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    mActivity.onBackPressed();

                                }
                            });

                }

                @Override
                public void onError(String error) {

                }
            }, 1).execute();

        }
    }



}
