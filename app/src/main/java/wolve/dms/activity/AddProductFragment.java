package wolve.dms.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CInputForm;
import wolve.dms.libraries.CustomPostMultiPart;
import wolve.dms.libraries.RestClientHelper;
import wolve.dms.models.Product;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomDialog;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;

/**
 * Created by macos on 9/16/17.
 */

public class AddProductFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName, edUnitPrice, edPurchasePrice, edGroup, edVolume, edIsPromotion;
    private Button btnSubmit , btnLoadImage;
    private CircleImageView imgProduct;

    private Product product;
    private ProductActivity mActivity;
    private ArrayList<String> listGroup = new ArrayList<>();
    private ArrayList<String> listBoolean = new ArrayList<>();
    private String imageChangeUri ;

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
        edGroup.setText(listGroup.get(0));

        edIsPromotion.setDropdownList(listBoolean);
        edIsPromotion.setText(listBoolean.get(0));

        String bundle = getArguments().getString(Constants.PRODUCT);
        if (bundle != null){
            try {
                product = new Product(new JSONObject(bundle));

                edName.setText(product.getString("name"));
                edUnitPrice.setText(product.getString("unitPrice"));
                edPurchasePrice.setText(product.getString("purchasePrice"));
                edGroup.setText(new JSONObject(product.getString("productGroup")).getString("name"));
                edVolume.setText(product.getString("volume"));
                edIsPromotion.setText(product.getBoolean("promotion")? Constants.IS_PROMOTION :Constants.NO_PROMOTION);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE }, Constants.REQUEST_READ_PERMISSION);
            return;
        }

    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnLoadImage.setOnClickListener(this);
    }

    private void initializeView() {
        edName = (CInputForm) view.findViewById(R.id.add_product_name);
        edUnitPrice = (CInputForm) view.findViewById(R.id.add_product_unit_price);
        edPurchasePrice = (CInputForm) view.findViewById(R.id.add_product_purchase_price);
        edGroup = (CInputForm) view.findViewById(R.id.add_product_group);
        edVolume = (CInputForm) view.findViewById(R.id.add_product_volume);
        edIsPromotion = (CInputForm) view.findViewById(R.id.add_product_promotion);
        btnSubmit = (Button) view.findViewById(R.id.add_product_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        btnLoadImage = (Button) view.findViewById(R.id.add_product_choiceimage);
        imgProduct = (CircleImageView) view.findViewById(R.id.add_product_image);

        mActivity = (ProductActivity) getActivity();
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

            case R.id.add_product_choiceimage:
                startImageChooser();
                break;

        }
    }

    private void startImageChooser() {
        // Kiểm tra permission với android sdk >= 23
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
            CustomDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {

                }
            });

        }else {
            String param = String.format(Api_link.PRODUCT_CREATE_PARAM, product == null? "" : "id="+ product.getString("id") +"&",
                    Util.encodeString(edName.getText().toString()),
                    edIsPromotion.getText().toString().trim().equals(Constants.IS_PROMOTION) ? true: false,
                    Integer.parseInt(edUnitPrice.getText().toString().trim().replace(",","")),
                    Integer.parseInt(edPurchasePrice.getText().toString().trim().replace(",","")),
                    Integer.parseInt(edVolume.getText().toString().trim().replace(",","")),
                    defineGroupId(edGroup.getText().toString().trim()));


            ProductConnect.CreateProduct(param, new CallbackJSONObject() {
                @Override
                public void onResponse(JSONObject result) {

                    getActivity().getSupportFragmentManager().popBackStack();
                    mActivity.loadProductGroup();
                }

                @Override
                public void onError(String error) {

                }
            }, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE){
            if (data != null){
                imageChangeUri = Util.getRealPathFromURI(data.getData());
                Glide.with(this).load(imageChangeUri).fitCenter().into(imgProduct);
                uploadImage(data.getData());
//                Crop.of(Uri.parse(data.getData().toString()), Uri.fromFile(Util.getOutputMediaFile())).asSquare().start(getActivity(), this);

            }

        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            imageChangeUri = Util.getRealPathFromURI(data.getData());
            Glide.with(this).load(imageChangeUri).fitCenter().into(imgProduct);

        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                imageChangeUri = Util.getRealPathFromURI(data.getData());
                Glide.with(this).load(imageChangeUri).fitCenter().into(imgProduct);

            } else if (resultCode == Crop.RESULT_ERROR) {
                Toast.makeText(getActivity(), Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void uploadImage(Uri  uri){
        File file = new File(imageChangeUri);
//        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
//        os.close();
        Log.e("string", file.toString());
        Util.getInstance().showLoading();
        new CustomPostMultiPart(Api_link.PRODUCT_NEW, "image", file, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(true);
                try {
                    if (result.getInt("status") == 200) {
                        //listener.onResponse(result.getJSONArray("data"));

                    } else {
                        //listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    //listener.onError(e.toString());
                }
            }

            @Override
            public void onError(String error) {
                //listener.onError(error);
                Util.getInstance().stopLoading(true);
            }
        }).execute();



    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED ) {
                // close the app
                Toast.makeText(getActivity(), "Cấp quyền truy cập không thành công!", Toast.LENGTH_LONG).show();

            }
        }
    }


}
