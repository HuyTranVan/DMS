package wolve.dms.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cloudinary.Api;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
//import wolve.dms.libraries.FileUploader;
import wolve.dms.libraries.connectapi.CustomPostMultiPart;
import wolve.dms.libraries.connectapi.UploadCloudaryMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

/**
 * Created by macos on 9/16/17.
 */

public class AddProductFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName, edUnitPrice, edPurchasePrice, edGroup, edVolume, edIsPromotion;
    private Button btnSubmit ;
    private CircleImageView imgProduct;

    private Product product;
    private ProductActivity mActivity;
    //private List<String> listGroup = new ArrayList<>();
    private List<String> listBoolean = new ArrayList<>();
    private Uri imageChangeUri ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {

        for (int i=0; i<mActivity.listProductGroup.size(); i++){
            //listGroup.add(mActivity.listProductGroup.get(i).getString("name"));
            mActivity.listProductGroup.get(i).put("text", mActivity.listProductGroup.get(i).getString("name"));
        }

        listBoolean.add(0,Constants.IS_PROMOTION);
        listBoolean.add(1,Constants.NO_PROMOTION);

        edGroup.setText(mActivity.listProductGroup.get(mActivity.currentPosition).getString("name"));
        edGroup.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {

                CustomBottomDialog.choiceListObject("CHỌN NHÓM SẢN PHẨM", mActivity.listProductGroup, new CallbackBaseModel() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edGroup.setText(object.getString("text"));
                    }

                    @Override
                    public void onError() {

                    }
                });

            }
        });

//        edIsPromotion.setDropdownList(listBoolean);
        edIsPromotion.setText(listBoolean.get(0));
        edIsPromotion.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomDialog.choiceTwoOption(mActivity.getResources().getString(R.string.icon_gift),Constants.IS_PROMOTION,
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
//                CustomBottomDialog.choiceList("TÙY CHỌN", listBoolean, new CustomBottomDialog.StringListener() {
//                    @Override
//                    public void onResponse(String content) {
//                        edIsPromotion.setText(content);
//                    }
//                });
            }
        });

        String bundle = getArguments().getString(Constants.PRODUCT);
        try {
            if (bundle != null){

                product = new Product(new JSONObject(bundle));

                edName.setText(product.getString("name"));
                edUnitPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));
//                edUnitPrice.textMoneyEvent(new CallbackDouble() {
//                    @Override
//                    public void Result(Double d) {
//
//                    }
//                });

                edPurchasePrice.setText(Util.FormatMoney(product.getDouble("purchasePrice")));
//                edPurchasePrice.textMoneyEvent(new CallbackDouble() {
//                    @Override
//                    public void Result(Double d) {
//
//                    }
//                });


                edGroup.setText(new JSONObject(product.getString("productGroup")).getString("name"));
                edVolume.setText(product.getString("volume"));
                edIsPromotion.setText(product.getBoolean("promotion")? Constants.IS_PROMOTION :Constants.NO_PROMOTION);

                if (!Util.checkImageNull(product.getString("image"))){
                    Glide.with(AddProductFragment.this).load(product.getString("image")).centerCrop().into(imgProduct);

                }else {
                    Glide.with(this).load( R.drawable.ic_wolver).centerCrop().into(imgProduct);

                }

            }else {
                product = new Product(new JSONObject());
                product.put("id",0);

            }

            edUnitPrice.textMoneyEvent(new CallbackDouble() {
                @Override
                public void Result(Double d) {

                }
            });
            edPurchasePrice.textMoneyEvent(new CallbackDouble() {
                @Override
                public void Result(Double d) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkPermission();

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
        imgProduct.setOnClickListener(this);
        edGroup.setOnClickListener(this);
        unitPriceEvent();
        purchasePriceEvent();

    }

    private void initializeView() {
        mActivity = (ProductActivity) getActivity();
        edName = (CInputForm) view.findViewById(R.id.add_product_name);
        edUnitPrice = (CInputForm) view.findViewById(R.id.add_product_unit_price);
        edPurchasePrice = (CInputForm) view.findViewById(R.id.add_product_purchase_price);
        edGroup = (CInputForm) view.findViewById(R.id.add_product_group);
        edVolume = (CInputForm) view.findViewById(R.id.add_product_volume);
        edIsPromotion = (CInputForm) view.findViewById(R.id.add_product_promotion);
        btnSubmit = (Button) view.findViewById(R.id.add_product_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        imgProduct = (CircleImageView) view.findViewById(R.id.add_product_image);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                Util.hideKeyboard(v);
                break;

            case R.id.add_product_submit:
                submitProduct();
//                uploadImageNew();
                break;

            case R.id.add_product_image:
                choiceGalleryCamera();
                break;

            case R.id.add_product_group:

                break;

        }
    }

    private void choiceGalleryCamera(){
        CustomBottomDialog.choiceTwoOption(getString(R.string.icon_image),
                "Chọn ảnh thư viện",
                getString(R.string.icon_camera),
                "Chụp ảnh",
                new CustomBottomDialog.TwoMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        startImageChooser();
                    }

                    @Override
                    public void Method2(Boolean two) {
                        startCamera();
                    }
                });
    }


    private void startImageChooser() {
        imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
        if (Build.VERSION.SDK_INT <= 19) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, REQUEST_CHOOSE_IMAGE);

        } else if (Build.VERSION.SDK_INT > 19) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
        }
    }

    public void startCamera() {
//        imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
        imageChangeUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", Util.getOutputMediaFile());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageChangeUri );
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

    private int defineGroupId(String groupName){
        int id=0;
        for (int i=0; i<mActivity.listProductGroup.size(); i++){
            if (mActivity.listProductGroup.get(i).getString("name").equals(groupName)){
                id = mActivity.listProductGroup.get(i).getInt("id");
            }
        }
        return id;
    }

    private void submitProduct(){
        if (edName.getText().toString().trim().equals("")
                || edUnitPrice.getText().toString().trim().equals("")
                || edPurchasePrice.getText().toString().trim().equals("")){
            CustomCenterDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {

                }
            });

        }else {
            if (imageChangeUri != null){
                uploadImage(new CallbackString() {
                    @Override
                    public void Result(String s) {
                        updateProduct(s);

                    }
                });

            }else {
                if (product.getInt("id") ==0){
                    updateProduct("");
                }else if (product.getString("image").startsWith("http")){
                    updateProduct(product.getString("image"));
                }else {
                    updateProduct("");
                }

            }



        }
    }

    private void updateProduct(String urlImage){
        String param = String.format(Api_link.PRODUCT_CREATE_PARAM,
                product.getInt("id") == 0? "" : "id="+ product.getString("id") +"&",
                Util.encodeString(edName.getText().toString().trim()),
                edIsPromotion.getText().toString().trim().equals(Constants.IS_PROMOTION) ? true: false,
                Util.valueMoney(edUnitPrice.getText().toString()),
                Util.valueMoney(edPurchasePrice.getText().toString()),
                edVolume.getText().toString().trim().replace(",",""),
                defineGroupId(edGroup.getText().toString().trim()),
                urlImage);

        ProductConnect.CreateProduct(param, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                getActivity().getSupportFragmentManager().popBackStack();
                mActivity.loadProduct();
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE ){
            if (data != null){
                Crop.of(Uri.parse(data.getData().toString()), imageChangeUri).asSquare().withMaxSize(200,200).start(getActivity(), AddProductFragment.this);

            }

        }else if (requestCode == REQUEST_IMAGE_CAPTURE){
            Crop.of(imageChangeUri, imageChangeUri).asSquare().withMaxSize(200,200).start(getActivity(), AddProductFragment.this);

        }
        else if (data != null && requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            Glide.with(this).load(imageChangeUri).centerCrop().into(imgProduct);

        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(imageChangeUri).centerCrop().into(imgProduct);

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

//        String  uri = Util.getRealPathFromURI(imageChangeUri);
//        try {
//            Api_link.getImageCloud().uploader().upload(imageChangeUri, ObjectUtils.emptyMap());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        MediaManager.get().upload(imageChangeUri).callback(new UploadCallback() {
//            @Override
//            public void onStart(String requestId) {
//
//                // your code here
//            }
//            @Override
//            public void onProgress(String requestId, long bytes, long totalBytes) {
//                // example code starts here
//                Double progress = (double) bytes/totalBytes;
//                // post progress to app UI (e.g. progress bar, notification)
//                // example code ends here
//            }
//            @Override
//            public void onSuccess(String requestId, Map resultData) {
//                mListener.Result(resultData.get("url").toString());
//
//                // your code here
//            }
//            @Override
//            public void onError(String requestId, ErrorInfo error) {
//                // your code here
//                Util.getInstance().stopLoading(true);
//            }
//            @Override
//            public void onReschedule(String requestId, ErrorInfo error) {
//                // your code here
//                Util.getInstance().stopLoading(true);
//            }}).dispatch();
    }

    private void uploadImageNew(){
        //new CustomPostMultiPart(Api_link.IMAGES, imageChangeUri);
//        String[] result = {Util.getRealPathFromURI(imageChangeUri)};
//        try {
//            new FileUploader(imageChangeUri).main(null);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        }

    }

    private void unitPriceEvent(){

    }

    private void purchasePriceEvent(){

    }

}
