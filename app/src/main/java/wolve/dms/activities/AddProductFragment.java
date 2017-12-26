package wolve.dms.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.controls.CInputForm;
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
    private ArrayList<String> listGroup = new ArrayList<>();
    private ArrayList<String> listBoolean = new ArrayList<>();
    private Uri imageChangeUri ;


    private Handler backgroundHandler;

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
            listGroup.add(mActivity.listProductGroup.get(i).getString("name"));
        }

        listBoolean.add(0,Constants.IS_PROMOTION);
        listBoolean.add(1,Constants.NO_PROMOTION);

        edGroup.setDropdownList(listGroup);
        edGroup.setText(listGroup.get(mActivity.currentPosition));

        edIsPromotion.setDropdownList(listBoolean);
        edIsPromotion.setText(listBoolean.get(0));

        String bundle = getArguments().getString(Constants.PRODUCT);
        try {
            if (bundle != null){

                product = new Product(new JSONObject(bundle));

                edName.setText(product.getString("name"));
                edUnitPrice.setText(product.getString("unitPrice"));
                edPurchasePrice.setText(product.getString("purchasePrice"));
                edGroup.setText(new JSONObject(product.getString("productGroup")).getString("name"));
                edVolume.setText(product.getString("volume"));
                edIsPromotion.setText(product.getBoolean("promotion")? Constants.IS_PROMOTION :Constants.NO_PROMOTION);

                if (!Util.checkImageNull(product.getString("image"))){
                    Glide.with(this).load(product.getString("image")).centerCrop().into(imgProduct);

                }else {
                    Glide.with(this).load( R.drawable.ic_wolver).centerCrop().into(imgProduct);

                }

            }else {
                product = new Product(new JSONObject());
                product.put("id",0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                break;

            case R.id.add_product_image:
                choiceGalleryCamera();
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
                edUnitPrice.getText().toString().trim().replace(",",""),
                edPurchasePrice.getText().toString().trim().replace(",",""),
                edVolume.getText().toString().trim().replace(",",""),
                defineGroupId(edGroup.getText().toString().trim()),
                urlImage);

        ProductConnect.CreateProduct(param, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                Log.e("ket qua", result.toString());
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
        else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
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
        MediaManager.get().upload(imageChangeUri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {

                // your code here
            }
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                // example code starts here
                Double progress = (double) bytes/totalBytes;
                // post progress to app UI (e.g. progress bar, notification)
                // example code ends here
            }
            @Override
            public void onSuccess(String requestId, Map resultData) {
                mListener.Result(resultData.get("url").toString());

                // your code here
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                // your code here
                Util.getInstance().stopLoading(true);
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                // your code here
                Util.getInstance().stopLoading(true);
            }}).dispatch();
    }

    private void unitPriceEvent(){

    }

    private void purchasePriceEvent(){

    }

}
