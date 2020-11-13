package wolve.dms.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.CallbackUri;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.apiconnect.apiserver.UploadCloudaryMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static wolve.dms.activities.BaseActivity.createGetParam;
import static wolve.dms.activities.BaseActivity.createPostParam;
import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

/**
 * Created by macos on 9/16/17.
 */

public class NewUpdateDistributorFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm tvCompany, tvPhone, tvAddress, tvSite, tvThanks, tvName, tvProvince;
    private Button btnSubmit;
    private TextView tvTitle;
    private RelativeLayout rlImage;
    private ImageView image;

    private BaseModel currentDistributor;
    private Uri imageChangeUri;
    private String imgURL;
    private List<BaseModel> mProvinces = new ArrayList<>();
    private CallbackObject onDataPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_add_distributor, container, false);
        findViewById();

        intitialData();

        addEvent();
        return view;
    }

    private void findViewById() {
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        tvCompany = view.findViewById(R.id.add_distributor_company_name);
        tvAddress = view.findViewById(R.id.add_distributor_company_add);
        tvPhone = view.findViewById(R.id.add_distributor_company_hotline);
        tvSite = view.findViewById(R.id.add_distributor_company_web);
        tvThanks = view.findViewById(R.id.add_distributor_company_thanks);
        btnSubmit = view.findViewById(R.id.add_distributor_submit);
        tvTitle = view.findViewById(R.id.add_distributor_title);
        rlImage = view.findViewById(R.id.add_distributor_image_parent);
        tvName = view.findViewById(R.id.add_distributor_name);
        tvProvince = view.findViewById(R.id.add_distributor_province);
        image = view.findViewById(R.id.add_distributor_image);

    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.DISTRIBUTOR);
        if (bundle != null) {
            currentDistributor = new BaseModel(bundle);
            updateView(currentDistributor);

        }else {
            currentDistributor = new BaseModel();
            currentDistributor.put("id", 0);
            currentDistributor.put("province_id", 0);
        }

//        BaseModel param = createGetParam(ApiUtil.DISTRIBUTOR_DETAIL() + Distributor.getDistributorId(), false);
//        new GetPostMethod(param, new NewCallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result, List<BaseModel> list) {
//                currentDistributor = result;
//                updateView(currentDistributor);
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, 1).execute();


    }

    private void updateView(BaseModel content) {
        tvName.setBoldStyle();
        tvProvince.setBoldStyle();
        tvName.setText(content.getString("name"));
        tvProvince.setText(content.getBaseModel("province").getString("name"));
        tvCompany.setText(content.getString("company"));
        tvAddress.setText(content.getString("address"));
        tvPhone.setText(Util.FormatPhone(content.getString("phone")));
        tvSite.setText(content.getString("website"));
        tvThanks.setText(content.getString("thanks"));
        tvTitle.setText(content.getString("name"));
        if (!Util.checkImageNull(content.getString("image"))){
            Glide.with(this).load(content.getString("image")).fitCenter().into(image);
            imgURL = content.getString("image");

        } else {
            Glide.with(this).load(R.drawable.lub_logo_red).fitCenter().into(image);
            imgURL = "";

        }


        tvName.setFocusable(User.getId() != 2 ? false : true);
        tvProvince.setFocusable(User.getId() != 2 ? false : true);
        tvProvince.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                if (mProvinces.size() >0){
                    chooseProvince(mProvinces);

                }else {{
                    getListProvince(new CallbackListObject() {
                        @Override
                        public void onResponse(List<BaseModel> list) {
                            mProvinces = list;
                            chooseProvince(mProvinces);
                        }
                    });

                }}


            }
        });


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        rlImage.setOnClickListener(this);
        phoneEvent();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                getActivity().onBackPressed();
                break;

            case R.id.add_distributor_submit:
                submitDistributor();
                break;

            case R.id.add_distributor_image_parent:
                CustomBottomDialog.choiceThreeOption(getString(R.string.icon_image),
                        "Chọn ảnh thư viện",
                        getString(R.string.icon_camera),
                        "Chụp ảnh",
                        getString(R.string.icon_empty),
                        "Mặc định",
                        new CustomBottomDialog.ThreeMethodListener() {
                            @Override
                            public void Method1(Boolean one) {
                                Transaction.startImageChooser(null, new CallbackUri() {
                                    @Override
                                    public void uriRespone(Uri uri) {
                                        imageChangeUri = uri;
                                    }
                                });
                            }

                            @Override
                            public void Method2(Boolean two) {
                                Transaction.startCamera(null, new CallbackUri() {
                                    @Override
                                    public void uriRespone(Uri uri) {
                                        imageChangeUri = uri;
                                    }
                                });
                            }

                            @Override
                            public void Method3(Boolean three) {
                                imgURL = "";
                                Glide.with(NewUpdateDistributorFragment.this).load(R.drawable.lub_logo_red).fitCenter().into(image);
                            }
                        });
                break;

        }
    }

    private void submitDistributor() {
        if (tvName.getText().toString().trim().equals("")){
            Util.showSnackbar("Chưa nhập tên nhà phân phối!", null, null);

        }else if (tvProvince.getText().toString().trim().equals("")){
            Util.showSnackbar("Chưa nhập tỉnh/ thành phố!", null, null);

        }else if (tvPhone.getText().toString().trim().equals("")){
            Util.showSnackbar("Chưa nhập hotline!", null, null);

        }else if (imageChangeUri != null) {
            new UploadCloudaryMethod(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
                @Override
                public void Result(String url) {
                    updateDistributor(url);
                }

            }).execute();

        } else {
            updateDistributor(imgURL);

        }

    }

    private void updateDistributor(String imageLink) {
        BaseModel param = createPostParam(ApiUtil.DISTRIBUTOR_NEW(),
                String.format(ApiUtil.DISTRIBUTOR_CREATE_PARAM, currentDistributor.getInt("id") == 0 ? "" : String.format("id=%s&", currentDistributor.getString("id")),
                        Util.encodeString(tvName.getText().toString()),
                        currentDistributor.getInt("province_id"),
                        Util.encodeString(tvCompany.getText().toString()),
                        Util.encodeString(tvAddress.getText().toString()),
                        Util.encodeString(tvPhone.getText().toString().replace(".", "")),
                        Util.encodeString(tvSite.getText().toString()),
                        Util.encodeString(tvThanks.getText().toString()),
                        imageLink),
                false,
                false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                if (result.getInt("id") == Distributor.getId()){
                    CustomSQL.setBaseModel(Constants.DISTRIBUTOR, result);

                }
                onDataPass.onResponse(result);
                getActivity().onBackPressed();
                //Transaction.gotoHomeActivityRight(true);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_IMAGE) {
            if (data != null) {
                CropImage.activity(Uri.parse(data.getData().toString()))
                        .setAspectRatio(1,1)
                        .setMaxZoom(10)
                        .setRequestedSize(512, 512)
                        .start(getActivity(), NewUpdateDistributorFragment.this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            CropImage.activity(imageChangeUri)
                    .setAspectRatio(1,1)
                    .setMaxZoom(10)
                    .setRequestedSize(512, 512)
                    .start(getActivity(), NewUpdateDistributorFragment.this);

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageChangeUri = result.getUri();
                Glide.with(this).load(imageChangeUri).centerCrop().into(image);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Util.showToast(result.getError().getMessage());

            }
        }

    }



    private void phoneEvent() {
        tvPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });
    }

    private void chooseProvince(List<BaseModel> list){
        CustomBottomDialog.choiceListObject("CHỌN TỈNH / THÀNH PHỐ", list, "name", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                tvProvince.setText(object.getString("name"));
                currentDistributor.put("province_id", object.getInt("provinceid"));

            }
        }, null);
    }

    private void getListProvince(CallbackListObject listener){
        BaseModel param = createGetParam(ApiUtil.PROVINCES(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }


}
