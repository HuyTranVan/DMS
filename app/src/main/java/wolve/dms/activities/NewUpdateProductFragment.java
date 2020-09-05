package wolve.dms.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.CallbackUri;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.apiconnect.apiserver.UploadCloudaryMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static wolve.dms.activities.BaseActivity.createPostParam;
import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

//import wolve.dms.libraries.FileUploader;

/**
 * Created by macos on 9/16/17.
 */

public class NewUpdateProductFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName, edUnitPrice, edPurchasePrice, edGroup, edVolume,
            edIsPromotion, edBasePrice, edUnitInCarton;
    private Button btnSubmit;
    private CircleImageView imgProduct;

    private BaseModel product;
    private ProductActivity mActivity;
    private List<String> listBoolean = new ArrayList<>();
    private Uri imageChangeUri;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
//        FitScrollWithFullscreen.assistActivity(getActivity(), 1);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {

        for (int i = 0; i < mActivity.listProductGroup.size(); i++) {
            mActivity.listProductGroup.get(i).put("text", mActivity.listProductGroup.get(i).getString("name"));
        }

        listBoolean.add(0, Constants.IS_PROMOTION);
        listBoolean.add(1, Constants.NO_PROMOTION);

        edGroup.setText(mActivity.listProductGroup.get(mActivity.currentPosition).getString("name"));
        edGroup.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {

                CustomBottomDialog.choiceListObject("CHỌN NHÓM SẢN PHẨM", mActivity.listProductGroup, "text", new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edGroup.setText(object.getString("text"));

                    }
                }, null);

            }
        });

        edIsPromotion.setText(listBoolean.get(0));
        edIsPromotion.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomDialog.choiceTwoOption(mActivity.getResources().getString(R.string.icon_gift), Constants.IS_PROMOTION,
                        mActivity.getResources().getString(R.string.icon_empty), Constants.NO_PROMOTION,
                        new CustomBottomDialog.TwoMethodListener() {
                            @Override
                            public void Method1(Boolean one) {
                                edIsPromotion.setText(Constants.IS_PROMOTION);
                            }

                            @Override
                            public void Method2(Boolean two) {
                                edIsPromotion.setText(Constants.NO_PROMOTION);
                            }
                        });

            }
        });

        String bundle = getArguments().getString(Constants.PRODUCT);
        if (bundle != null) {
            product = new BaseModel(bundle);

            edName.setText(product.getString("name"));
            edUnitPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));
            edPurchasePrice.setText(Util.FormatMoney(product.getDouble("purchasePrice")));
            edBasePrice.setText(Util.FormatMoney(product.getDouble("basePrice")));

            edGroup.setText(product.getBaseModel("productGroup").getString("name"));
            edVolume.setText(product.getString("volume"));
            edUnitInCarton.setText(product.getString("unitInCarton"));
            edIsPromotion.setText(product.getBoolean("promotion") ? Constants.IS_PROMOTION : Constants.NO_PROMOTION);

            if (!Util.checkImageNull(product.getString("image"))) {
                Glide.with(NewUpdateProductFragment.this).load(product.getString("image")).centerCrop().into(imgProduct);

            } else {
                Glide.with(this).load(R.drawable.ic_wolver).centerCrop().into(imgProduct);

            }

        } else {
            product = new Product(new JSONObject());
            product.put("id", 0);

        }

        edBasePrice.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);

    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgProduct.setOnClickListener(this);
        edGroup.setOnClickListener(this);
        edUnitPrice.textMoneyEvent(null);
        edPurchasePrice.textMoneyEvent(null);
        edBasePrice.textMoneyEvent(null);

    }

    private void initializeView() {
        mActivity = (ProductActivity) getActivity();
        edName = (CInputForm) view.findViewById(R.id.add_product_name);
        edUnitPrice = (CInputForm) view.findViewById(R.id.add_product_unit_price);
        edPurchasePrice = (CInputForm) view.findViewById(R.id.add_product_purchase_price);
        edGroup = (CInputForm) view.findViewById(R.id.add_product_group);
        edVolume = (CInputForm) view.findViewById(R.id.add_product_volume);
        edIsPromotion = (CInputForm) view.findViewById(R.id.add_product_promotion);
        edBasePrice = (CInputForm) view.findViewById(R.id.add_product_distributor_price);
        edUnitInCarton = (CInputForm) view.findViewById(R.id.add_product_unit_in_carton);
        btnSubmit = (Button) view.findViewById(R.id.add_product_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        imgProduct = (CircleImageView) view.findViewById(R.id.add_product_image);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;

            case R.id.add_product_submit:
                submitProduct();
                break;

            case R.id.add_product_image:
                choiceGalleryCamera();
                break;

            case R.id.add_product_group:

                break;

        }
    }

    private void choiceGalleryCamera() {
        CustomBottomDialog.choiceTwoOption(getString(R.string.icon_image),
                "Chọn ảnh thư viện",
                getString(R.string.icon_camera),
                "Chụp ảnh",
                new CustomBottomDialog.TwoMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        Transaction.startImageChooser(NewUpdateProductFragment.this, new CallbackUri() {
                            @Override
                            public void uriRespone(Uri uri) {
                                imageChangeUri = uri;
                            }
                        });
                    }

                    @Override
                    public void Method2(Boolean two) {
                        Transaction.startCamera(NewUpdateProductFragment.this, new CallbackUri() {
                            @Override
                            public void uriRespone(Uri uri) {
                                imageChangeUri = uri;
                            }
                        });
                    }
                });
    }


//    private void startImageChooser() {
//        imageChangeUri = Uri.fromFile(Util.createTempFileOutput());
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
//
//    public void startCamera() {
////        imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
//        imageChangeUri = FileProvider.getUriForFile(
//                getActivity(),
//                BuildConfig.APPLICATION_ID + ".provider",
//                Util.createTempFileOutput());
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageChangeUri);
//        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//
//    }

    private int defineGroupId(String groupName) {
        int id = 0;
        for (int i = 0; i < mActivity.listProductGroup.size(); i++) {
            if (mActivity.listProductGroup.get(i).getString("name").equals(groupName)) {
                id = mActivity.listProductGroup.get(i).getInt("id");
            }
        }
        return id;
    }

    private void submitProduct() {
        if (edName.getText().toString().trim().equals("")
                || edUnitPrice.getText().toString().trim().equals("")
                || edPurchasePrice.getText().toString().trim().equals("")
                || edBasePrice.getText().toString().trim().equals("")
                || edUnitInCarton.getText().toString().trim().equals("")) {
            CustomCenterDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {

                }
            });

        } else {
            if (imageChangeUri != null) {
                new UploadCloudaryMethod(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
                    @Override
                    public void Result(String url) {
                        updateProduct(url);
                    }

                }).execute();

//                SystemConnect.uploadImage(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
//                    @Override
//                    public void Result(String s) {
//                        updateProduct(s);
//
//                    }
//                });

            } else {
                if (product.getInt("id") == 0) {
                    updateProduct("");
                } else if (product.getString("image").startsWith("http")) {
                    updateProduct(product.getString("image"));
                } else {
                    updateProduct("");
                }

            }


        }
    }

    private void updateProduct(String urlImage) {
        BaseModel param = createPostParam(ApiUtil.PRODUCT_NEW(),
                String.format(ApiUtil.PRODUCT_CREATE_PARAM,
                        product.getInt("id") == 0 ? "" : "id=" + product.getString("id") + "&",
                        Util.encodeString(edName.getText().toString().trim()),
                        edIsPromotion.getText().toString().trim().equals(Constants.IS_PROMOTION) ? 1 : 0,
                        Util.valueMoney(edUnitPrice.getText().toString()),
                        Util.valueMoney(edPurchasePrice.getText().toString()),
                        edVolume.getText().toString().trim().replace(",", ""),
                        defineGroupId(edGroup.getText().toString().trim()),
                        urlImage,
                        Util.valueMoney(edBasePrice.getText().toString()),
                        edUnitInCarton.getText().toString().trim().replace(",", "")),
                false, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                getActivity().getSupportFragmentManager().popBackStack();
                mActivity.reupdateProduct(result);
            }

            @Override
            public void onError(String error) {

            }
        },1).execute();
//        ProductConnect.CreateProduct(param, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                getActivity().getSupportFragmentManager().popBackStack();
//                mActivity.reupdateProduct(result);
//            }
//
//            @Override
//            public void onError(String error){
//
//            }
//        }, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CHOOSE_IMAGE) {
//            if (data != null) {
//                Crop.of(Uri.parse(data.getData().toString()), imageChangeUri).asSquare().withMaxSize(400, 400).start(getActivity(), NewUpdateProductFragment.this);
//
//            }
//
//        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
//            Crop.of(imageChangeUri, imageChangeUri).asSquare().withMaxSize(400, 400).start(getActivity(), NewUpdateProductFragment.this);
//
//        } else if (data != null && requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            Glide.with(this).load(imageChangeUri).centerCrop().into(imgProduct);
//
//        } else if (requestCode == Crop.REQUEST_CROP) {
//            if (resultCode == RESULT_OK) {
//                Glide.with(this).load(imageChangeUri).centerCrop().into(imgProduct);
//
//            } else if (resultCode == Crop.RESULT_ERROR) {
//                Util.showToast(Crop.getError(data).getMessage());
//
//            }
//        }

        if (requestCode == REQUEST_CHOOSE_IMAGE) {
            if (data != null) {
                CropImage.activity(Uri.parse(data.getData().toString()))
                        .setAspectRatio(1,1)
                        .setMaxZoom(10)
                        .setRequestedSize(512, 512)
                        .start(mActivity,  NewUpdateProductFragment.this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            CropImage.activity(imageChangeUri)
                    .setAspectRatio(1,1)
                    .setMaxZoom(10)
                    .setRequestedSize(512, 512)
                    .start(mActivity,  NewUpdateProductFragment.this);

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageChangeUri = result.getUri();
                Glide.with(this).load(imageChangeUri).centerCrop().into(imgProduct);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Util.showToast(result.getError().getMessage());

            }
        }



    }


}
