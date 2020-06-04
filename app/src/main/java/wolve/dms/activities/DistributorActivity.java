package wolve.dms.activities;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

/**
 * Created by macos on 9/16/17.
 */

public class DistributorActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private CInputForm tvCompany, tvPhone, tvAddress, tvSite, tvThanks;
    private Button btnSubmit;
    private TextView tvTitle;
    private RelativeLayout rlImage;
    private ImageView image;

    private BaseModel currentDistributor;
    private Uri imageChangeUri;
    private String imgURL;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_distributor;
    }

    @Override
    public int setIdContainer() {
        return R.id.distributor_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        tvCompany = findViewById(R.id.distributor_company_name);
        tvAddress = findViewById(R.id.distributor_company_add);
        tvPhone = findViewById(R.id.distributor_company_hotline);
        tvSite = findViewById(R.id.distributor_company_web);
        tvThanks = findViewById(R.id.distributor_company_thanks);
        btnSubmit = findViewById(R.id.distributor_submit);
        tvTitle = findViewById(R.id.distributor_title);
        rlImage = findViewById(R.id.distributor_image_parent);
        image = findViewById(R.id.distributor_image);

    }

    @Override
    public void initialData() {
        SystemConnect.GetDistributorDetail(Distributor.getDistributorId(), new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                currentDistributor = result;
                updateView(currentDistributor);

            }

            @Override
            public void onError(String error) {

            }
        }, true);

    }

    private void updateView(BaseModel content) {
        tvCompany.setText(content.getString("company"));
        tvAddress.setText(content.getString("address"));
        tvPhone.setText(Util.FormatPhone(content.getString("phone")));
        tvSite.setText(content.getString("website"));
        tvThanks.setText(content.getString("thanks"));
        tvTitle.setText(content.getString("name"));
        if (!Util.checkImageNull(content.getString("image"))) {
            Glide.with(this).load(content.getString("image")).fitCenter().into(image);
            imgURL = content.getString("image");

        } else {
            Glide.with(this).load(R.drawable.lub_logo_red).fitCenter().into(image);
            imgURL = "";

        }

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        rlImage.setOnClickListener(this);
        phoneEvent();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Transaction.gotoHomeActivityRight(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.distributor_submit:
                submitDistributor();
                break;

            case R.id.distributor_image_parent:
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
                                Transaction.startCamera();
                            }

                            @Override
                            public void Method3(Boolean three) {
                                imgURL = "";
                                imageChangeUri = FileProvider.getUriForFile(DistributorActivity.this, BuildConfig.APPLICATION_ID + ".provider", Util.getOutputMediaFile());

                                Glide.with(DistributorActivity.this).load(R.drawable.lub_logo_red).fitCenter().into(image);
                            }
                        });
                break;


        }
    }

    private void submitDistributor() {
        if (imageChangeUri != null) {
            SystemConnect.uploadImage(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
                @Override
                public void Result(String s) {
                    updateDistributor(s);

                }
            });

        } else {
            updateDistributor(imgURL);

        }

    }

    private void updateDistributor(String imageLink) {
        String param = String.format(Api_link.DISTRIBUTOR_CREATE_PARAM, currentDistributor.getInt("id") == 0 ? "" : String.format("id=%s&", currentDistributor.getString("id")),
                Util.encodeString(tvCompany.getText().toString()),
                Util.encodeString(tvAddress.getText().toString()),
                Util.encodeString(tvPhone.getText().toString().replace(".", "")),
                Util.encodeString(tvSite.getText().toString()),
                Util.encodeString(tvThanks.getText().toString()),
                imageLink);

        SystemConnect.CreateDistributor(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Transaction.gotoHomeActivityRight(true);
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE) {
            if (data != null) {
                Crop.of(Uri.parse(data.getData().toString()), imageChangeUri).asSquare().withMaxSize(512, 512).start(this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Crop.of(imageChangeUri, imageChangeUri).asSquare().withMaxSize(512, 512).start(this);

        } else if (data != null && requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            Glide.with(this).load(imageChangeUri).centerCrop().into(image);

        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(imageChangeUri).centerCrop().into(image);

            } else if (resultCode == Crop.RESULT_ERROR) {
                Util.showToast(Crop.getError(data).getMessage());

            }
        }

    }

//    private void startImageChooser() {
//        imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
//        if (Build.VERSION.SDK_INT <= 19) {
//            Intent i = new Intent();
//            i.setType("image/*");
//            i.setAction(Intent.ACTION_GET_CONTENT);
//            i.addCategory(Intent.CATEGORY_OPENABLE);
//            startActivityForResult(i, REQUEST_CHOOSE_IMAGE);
//
//        } else if (Build.VERSION.SDK_INT > 19) {
//            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
//        }
//    }

//    public void startCamera() {
//        imageChangeUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", Util.getOutputMediaFile());
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageChangeUri );
//        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//
//    }


    private void phoneEvent() {
        tvPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });
    }


}
