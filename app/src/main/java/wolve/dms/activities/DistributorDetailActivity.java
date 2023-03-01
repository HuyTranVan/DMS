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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.suke.widget.SwitchButton;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.AdminsAdapter;
import wolve.dms.adapter.DistributorAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackLong;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.CallbackUri;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.apiconnect.apiserver.UploadCloudaryMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

/**
 * Created by macos on 9/16/17.
 */

public class DistributorDetailActivity extends BaseActivity implements View.OnClickListener, SwitchButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {
    private View vImport, vExpire;
    private ImageView btnBack;
    private RadioGroup rdImportActive;
    private RadioButton rdImportMaster, rdImportTemp;
    private CInputForm tvCompany, tvPhone, tvAddress, tvSite, tvThanks, tvName,
            tvProvince,  tvExpire, tvUsers;
    private Button btnSubmit;
    private TextView tvTitle, tvImport;
    private RelativeLayout rlImage;
    private ImageView image;

    //private ProgressBar progressBar;
    private SwitchButton swImport, swExpire;

    protected BaseModel currentDistributor;
    private Uri imageChangeUri;
    private String imgURL;
    private List<BaseModel> mProvinces = new ArrayList<>();
    private Fragment mFragment;

//    private CallbackObject onDataPass;
//    private AdminsAdapter adminAdapter;

    private boolean hasChange = false;


    @Override
    public int getResourceLayout() {
        return R.layout.activity_distributor_detail;
    }

    @Override
    public int setIdContainer() {
        return R.id.distributor_detail_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        tvCompany = findViewById(R.id.distributor_detail_company_name);
        tvAddress = findViewById(R.id.distributor_detail_company_add);
        tvPhone = findViewById(R.id.distributor_detail_company_hotline);
        tvSite = findViewById(R.id.distributor_detail_company_web);
        tvThanks = findViewById(R.id.distributor_detail_company_thanks);
        btnSubmit = findViewById(R.id.distributor_detail_submit);
        tvTitle = findViewById(R.id.distributor_detail_title);
        rlImage = findViewById(R.id.distributor_detail_image_parent);
        tvName = findViewById(R.id.distributor_detail_name);
        tvProvince = findViewById(R.id.distributor_detail_province);
        tvImport = findViewById(R.id.distributor_detail_import);
        image = findViewById(R.id.distributor_detail_image);
        rdImportActive = findViewById(R.id.distributor_detail_import_active_group);
        rdImportMaster = findViewById(R.id.distributor_detail_import_active_master);
        rdImportTemp = findViewById(R.id.distributor_detail_import_active_temp);
        tvExpire = findViewById(R.id.distributor_detail_expire_title);
        swExpire = findViewById(R.id.distributor_detail_expire_switch);
        vExpire = findViewById(R.id.distributor_detail_expire_switch_cover);
        swImport = findViewById(R.id.distributor_detail_import_switch);
        vImport = findViewById(R.id.distributor_detail_import_switch_cover);
        tvUsers = findViewById(R.id.distributor_detail_users);

    }


    @Override
    public void initialData() {
        int distributor_id = CustomSQL.getInt(Constants.DISTRIBUTOR_ID);
        if (distributor_id == 0) {
            currentDistributor = new BaseModel();
            currentDistributor.put("id", 0);
            currentDistributor.put("province_id", 0);

            updateView(currentDistributor);

        }else {
            loadDistributor(distributor_id);
        }


    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        rlImage.setOnClickListener(this);
        tvPhone.addTextChangePhone(null);
        vImport.setOnClickListener(this);
        vExpire.setOnClickListener(this);
        swImport.setOnCheckedChangeListener(this);
        rdImportActive.setOnCheckedChangeListener(this);

    }

    private void loadDistributor(int distributor_id) {
        BaseModel param = createGetParam(ApiUtil.DISTRIBUTOR_DETAIL() + distributor_id, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                currentDistributor = result;
                updateView(currentDistributor);

            }

            @Override
            public void onError(String error){

            }
        }, 1).execute();
    }

    protected void updateView(BaseModel distributor) {
        tvName.setBoldStyle();
        tvProvince.setBoldStyle();
        tvName.setText(distributor.getString("name"));
        tvProvince.setText(distributor.getBaseModel("province").getString("name"));
        tvCompany.setText(distributor.getString("company"));
        tvAddress.setText(distributor.getString("address"));
        tvPhone.setText(Util.FormatPhone(distributor.getString("phone")));
        tvSite.setText(distributor.getString("website"));
        tvThanks.setText(distributor.getString("thanks"));
        tvTitle.setText(distributor.getString("name"));
        tvUsers.setText(String.format("Nhân viên (%d)", distributor.getInt("count_user")));
        if (!Util.checkImageNull(distributor.getString("image"))){
            Glide.with(this).load(distributor.getString("image")).fitCenter().into(image);
            imgURL = distributor.getString("image");

        } else {
            Glide.with(this).load(R.drawable.lub_logo_red).fitCenter().into(image);
            imgURL = "";

        }
        updateDistributorStatus(distributor);
        switch (distributor.getInt("importFunction")){
            case 0:
                swImport.setChecked(false);
                rdImportActive.setVisibility(View.GONE);
                break;

            case 1:
                swImport.setChecked(true);
                rdImportActive.setVisibility(View.VISIBLE);
                rdImportTemp.setChecked(true);
                break;

            case 2:
                swImport.setChecked(true);
                rdImportActive.setVisibility(View.VISIBLE);
                rdImportMaster.setChecked(true);
                break;
        }
        //swImport.setChecked(distributor.getInt("importFunction") == 1? true : false);

        tvName.setFocusable(User.getId() != 2 ? false : true);
        tvProvince.setFocusable(User.getId() != 2 ? false : true);
        tvUsers.setFocusable(User.getId() != 2 ? false : true);
        tvExpire.setFocusable(false);
        tvUsers.setFocusable(false);
        if (User.getId() ==2){
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

            tvUsers.setDropdown(true, new CInputForm.ClickListener() {
                @Override
                public void onClick(View view) {
                    changeFragment(new AdminsFragment(), true);

                }
            });
        }




    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mFragment = getSupportFragmentManager().findFragmentById(R.id.distributor_detail_parent);
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        }  else if (mFragment != null && mFragment instanceof AdminsFragment) {
            getSupportFragmentManager().popBackStack();

        }else {
            Transaction.returnPreviousActivity(Constants.DISTRIBUTORDETAIL_ACTIVITY, new BaseModel(), Constants.RESULT_DISTRIBUTORDETAIL_ACTIVITY);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
//                if (hasChange){
//                    onDataPass.onResponse(currentDistributor);
//                }
//                Util.hideKeyboard(btnSubmit);
//                getActivity().onBackPressed();
                break;

            case R.id.distributor_detail_submit:
                submitDistributor();
                break;

            case R.id.distributor_detail_expire_switch_cover:
                if (User.getId() ==2){
                    activeDistributor();

                }else {
                    Util.showToast("Không hỗ trợ");
                }

                break;

            case R.id.distributor_detail_import_switch_cover:
                if (swImport.isChecked()){
                    swImport.setChecked(false);

                }else {
                    enableImportFunction();

                }
                break;

            case R.id.distributor_detail_image_parent:
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
                                Glide.with(DistributorDetailActivity.this).load(R.drawable.lub_logo_red).fitCenter().into(image);
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
                        imageLink,
                        currentDistributor.getInt("importFunction")),
                false,
                false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                hasChange = true;
                currentDistributor = result;
                if (result.getInt("id") == Distributor.getId()){
                    CustomSQL.setBaseModel(Constants.DISTRIBUTOR, result);
                }
                updateView(currentDistributor);
                onBackPressed();


//                if (result.getInt("count_user") < 1){
//                    CustomCenterDialog.alertWithCancelButton("Admin!!!",
//                            "Tạo tài khoản Admin để kích hoạt NPP " + currentDistributor.getString("name"),
//                            "đồng ý",
//                            "để sau", new CallbackBoolean() {
//                                @Override
//                                public void onRespone(Boolean result) {
//                                    if (!result){
////                                        onDataPass.onResponse(currentDistributor);
////                                        Util.hideKeyboard(btnSubmit);
////                                        getActivity().onBackPressed();
//                                    }
//                                }
//                            });
//
//                }else {
////                    onDataPass.onResponse(currentDistributor);
////                    Util.hideKeyboard(btnSubmit);
////                    getActivity().onBackPressed();
//                }




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
                        .start(DistributorDetailActivity.this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            CropImage.activity(imageChangeUri)
                    .setAspectRatio(1,1)
                    .setMaxZoom(10)
                    .setRequestedSize(512, 512)
                    .start(DistributorDetailActivity.this);

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





    private void activeDistributor(){
        if (!swExpire.isChecked()){
            CustomBottomDialog.selectDate(String.format("%s >>> ACTIVE", currentDistributor.getString("name")),
                    Util.CurrentTimeStamp(),
                    new CallbackLong() {
                        @Override
                        public void onResponse(Long value) {
                            if (value <= Util.CurrentTimeStamp()){
                                Util.showToast("Chọn sai thời gian");

                            }else {
                                String message = String.format("Kích hoạt tài khoản %s đến %s",
                                        currentDistributor.getString("name"),
                                        Util.DateHourString(value));
                                CustomCenterDialog.alertWithCancelButton(null,
                                        message,
                                        "đồng ý",
                                        "quay lại",
                                        new CallbackBoolean() {
                                            @Override
                                            public void onRespone(Boolean result) {
                                                if (result){
                                                    DistributorAdapter.updateActiveDistributor(currentDistributor,value,  new CallbackObject() {
                                                        @Override
                                                        public void onResponse(BaseModel object) {
                                                            currentDistributor.put("valid_to", object.getLong("valid_to"));
                                                            updateDistributorStatus(object);
                                                        }
                                                    });

                                                }
                                            }
                                        });
                            }

                        }
                    });




        }else {
            CustomCenterDialog.alertWithCancelButton("Deactive!!",
                    String.format("Tạm ngừng hoạt động %s từ %s", currentDistributor.getString("name"), Util.CurrentMonthYearHour()),
                    "đồng ý",
                    "quay lại",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                DistributorAdapter.updateActiveDistributor(currentDistributor,0,  new CallbackObject() {
                                    @Override
                                    public void onResponse(BaseModel object) {
                                        currentDistributor.put("valid_to", object.getLong("valid_to"));
                                        updateDistributorStatus(object);
                                    }
                                });

                            }
                        }
                    });


        }

    }

    private void updateDistributorStatus(BaseModel distributor){
        if (distributor.hasKey("valid_to")){
            if (distributor.getLong("valid_to") > Util.CurrentTimeStamp()){
                tvExpire.setText(String.format("Ngày hết hạn %s", Util.DateString(distributor.getLong("valid_to"))));
                swExpire.setChecked(true);

            }else {
                tvExpire.setText("Tải khoản hết hạn");
                swExpire.setChecked(false);

            }
        }else {
                tvExpire.setText("Tải khoản hết hạn");
                swExpire.setChecked(false);


        }
    }

    private void enableImportFunction(){
        swImport.setChecked(true);

    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (isChecked){
            rdImportActive.setVisibility(View.VISIBLE);
            if (!rdImportTemp.isChecked() && !rdImportMaster.isChecked()){
                rdImportTemp.setChecked(true);
                currentDistributor.put("importFunction", 1);
            }

        }else {
            rdImportActive.setVisibility(View.GONE);
            currentDistributor.put("importFunction", 0);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.distributor_detail_import_active_temp:
                currentDistributor.put("importFunction", 1);
                break;

            case R.id.distributor_detail_import_active_master:
                currentDistributor.put("importFunction", 2);
                break;
        }
    }
}
