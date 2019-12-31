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
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.libraries.connectapi.UploadCloudaryMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
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
    private CInputForm edName, edPhone, edGender, edRole;
    private TextView tvTitle;
    private Button btnSubmit ;
    private CircleImageView imgUser;

    private UserActivity mActivity;

    private Uri imageChangeUri ;


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


    }

    @SuppressLint("WrongConstant")
    private void checkPermission(){
        if (PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                || PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE ,Manifest.permission.CAMERA }, Constants.REQUEST_READ_PERMISSION);

            return;
        }
    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgUser.setOnClickListener(this);


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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                Util.hideKeyboard(v);
                break;

            case R.id.add_user_submit:


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
                                Transaction.startImageChooser();
                            }

                            @Override
                            public void Method2(Boolean two) {
                                imageChangeUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", Util.getOutputMediaFile());
                                Transaction.startCamera(imageChangeUri);
                            }

                            @Override
                            public void Method3(Boolean three) {
                                //imgURL = "";

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
                Crop.of(Uri.parse(data.getData().toString()), imageChangeUri).asSquare().withMaxSize(200,200).start(getActivity(), NewUpdateUserFragment.this);

            }

        }else if (requestCode == REQUEST_IMAGE_CAPTURE){
            Crop.of(imageChangeUri, imageChangeUri).asSquare().withMaxSize(200,200).start(getActivity(), NewUpdateUserFragment.this);

        }
        else if (data != null && requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            //Glide.with(this).load(imageChangeUri).centerCrop().into(imgProduct);

        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                //Glide.with(this).load(imageChangeUri).centerCrop().into(imgProduct);

            } else if (resultCode == Crop.RESULT_ERROR) {
                Util.showToast(Crop.getError(data).getMessage());

            }
        }

    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED ) {
                // close the app
                Toast.makeText(getActivity(), "Cấp quyền truy cập không thành công!", Toast.LENGTH_LONG).show();

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



}
