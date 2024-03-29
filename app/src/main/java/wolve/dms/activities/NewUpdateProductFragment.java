package wolve.dms.activities;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.suke.widget.SwitchButton;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.CallbackUri;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.apiconnect.apiserver.UploadCloudaryMethod;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Product;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomDropdow;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;
import static wolve.dms.activities.BaseActivity.createGetParam;
import static wolve.dms.activities.BaseActivity.createPostParam;
import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;

//import wolve.dms.libraries.FileUploader;

/**
 * Created by macos on 9/16/17.
 */

public class NewUpdateProductFragment extends Fragment implements View.OnClickListener {
    private View view, vCover, vSwitch;
    private ImageView btnBack;
    private CInputForm edName, edUnitPrice, edPurchasePrice, edGroup, edVolume,
            edIsPromotion, edBasePrice, edUnitInCarton, edNote, edUnit;
    private TextView tvTitle;
    private Button btnSubmit;
    private CircleImageView imgProduct;
    private SwitchButton swActive;
    private RelativeLayout lnActiveParent;

    private BaseModel product, group, unit;
    private ProductActivity mActivity;
    private List<BaseModel> mGroup = null;
    private List<BaseModel> mUnits = null;
//    private List<String> listBoolean = new ArrayList<>();
    private Uri imageChangeUri;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
        FitScrollWithFullscreen.assistActivity(getActivity(), 1);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void initializeView() {
        mActivity = (ProductActivity) getActivity();
        edName = (CInputForm) view.findViewById(R.id.add_product_name);
        edNote = (CInputForm) view.findViewById(R.id.add_product_note);
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
        swActive = view.findViewById(R.id.add_product_active_sw);
        vCover = view.findViewById(R.id.add_product_cover);
        vSwitch = view.findViewById(R.id.add_product_active);
        lnActiveParent = view.findViewById(R.id.add_product_active_parent);
        edUnit = view.findViewById(R.id.add_product_unit);

    }

    private void intitialData() {
//        listBoolean.add(0, Constants.IS_PROMOTION);
//        listBoolean.add(1, Constants.NO_PROMOTION);
        group = BaseModel.copyToNewBaseModel(mActivity.mGroup);
        edGroup.setText(group.getString("name"));
        edIsPromotion.setText(Constants.NO_PROMOTION);

        String bundle = getArguments().getString(Constants.PRODUCT);
        if (bundle != null) {
            product = new BaseModel(bundle);

            edName.setText(product.getString("name"));
            edUnit.setText(product.getBaseModel("unit").getString("name"));
            edNote.setText(product.getString("note"));
            edUnitPrice.setText(Util.FormatMoney(product.getDouble("unitPrice")));
            edPurchasePrice.setText(Util.FormatMoney(product.getDouble("purchasePrice")));
            edBasePrice.setText(Util.FormatMoney(product.getDouble("basePrice")));

            //edGroup.setText(product.getBaseModel("productGroup").getString("name"));
            edVolume.setText(product.getString("volume"));
            edUnitInCarton.setText(product.getString("unitInCarton"));
            edIsPromotion.setText(product.getInt("promotion") == 1 ? Constants.IS_PROMOTION : Constants.NO_PROMOTION);

            if (!Util.checkImageNull(product.getString("image"))) {
                Glide.with(NewUpdateProductFragment.this).load(product.getString("image")).centerCrop().into(imgProduct);

            } else {
                Glide.with(this).load(R.drawable.lub_logo_grey).centerCrop().into(imgProduct);

            }

        } else {
            product = new Product(new JSONObject());
            product.put("id", 0);

        }

        edBasePrice.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
        lnActiveParent.setVisibility(Util.isAdmin() && product.getInt("id") != 0? View.VISIBLE : View.GONE);
        swActive.setChecked(product.getInt("deleted") == 0 ? true: false);
        btnSubmit.setVisibility(product.getInt("deleted") == 0 ? View.VISIBLE : View.GONE);
        vCover.setVisibility(product.getInt("deleted") == 0 ? View.GONE : View.VISIBLE);

    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgProduct.setOnClickListener(this);
        edUnitPrice.textMoneyEvent(null);
        edPurchasePrice.textMoneyEvent(null);
        edBasePrice.textMoneyEvent(null);
        vSwitch.setOnClickListener(this);
        edName.textEvent();
        edVolume.textEvent();
        edUnitInCarton.textEvent();
        groupEvent();
        unitEvent();
        promotionEvent();


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

            case R.id.add_product_active:
                switchEvent();
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

    private int defineGroupId(String groupName) {
        int id = 0;
//        for (int i = 0; i < mActivity.listProductGroup.size(); i++) {
//            if (mActivity.listProductGroup.get(i).getString("name").equals(groupName)) {
//                id = mActivity.listProductGroup.get(i).getInt("id");
//            }
//        }
        return id;
    }

    private void submitProduct() {
        if (edName.getText().toString().trim().equals("")
                || edUnitPrice.getText().toString().trim().equals("")
                || edPurchasePrice.getText().toString().trim().equals("")
                || edBasePrice.getText().toString().trim().equals("")
                || edUnitInCarton.getText().toString().trim().equals("")
                || edUnit.getText().toString().trim().equals("")) {
            CustomCenterDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, null);

        } else {
            if (imageChangeUri != null) {
                new UploadCloudaryMethod(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
                    @Override
                    public void Result(String url) {
                        updateProduct(url);
                    }

                }).execute();

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
                        group.getInt("id"),
                        urlImage,
                        Util.valueMoney(edBasePrice.getText().toString()),
                        edUnitInCarton.getText().toString().trim().replace(",", ""),
                        Util.encodeString(edNote.getText().toString()),
                        unit.getInt("id")),
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void switchEvent(){
        if (swActive.isChecked()){
            CustomCenterDialog.alertWithCancelButton("Ẩn sản phẩm!",
                    "Bạn muốn ẩn sản phẩm này khỏi danh sách",
                    "đồng ý",
                    "hủy",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                updateProductActive(product.getInt("id"), 1);

                            }
                        }
                    });

        }else {
            CustomCenterDialog.alertWithCancelButton("Hiện sản phẩm!",
                    "Bạn muốn khôi phục sản phẩm này vào danh sách",
                    "đồng ý",
                    "hủy",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                updateProductActive(product.getInt("id"), 0);

                            }
                        }
                    });

        }

    }

    private void updateProductActive(int product_id, int deleted){
        BaseModel param = createPostParam(ApiUtil.PRODUCT_ACTIVE(),
                String.format(ApiUtil.PRODUCT_ACTIVE_PARAM, product_id, deleted),
                false, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                mActivity.reupdateProduct(result);
                if (result.getInt("deleted") == 1){
                    swActive.setChecked(false);
                    vCover.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.GONE);

                }else {
                    swActive.setChecked(true);
                    vCover.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }
                //getActivity().getSupportFragmentManager().popBackStack();

            }

            @Override
            public void onError(String error) {

            }
        },1).execute();

    }

    private void groupEvent(){
        edGroup.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                loadGroups();
            }
        });

    }

    private void unitEvent(){
        edUnit.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                loadUnits();
            }
        });

    }

    private void loadGroups(){
        if (mGroup != null){
            selectGroup();

        }else {
            BaseModel param = createGetParam(ApiUtil.PRODUCT_GROUPS(), true);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    mGroup = list;
                    selectGroup();
                }

                @Override
                public void onError(String error) {

                }
            },1).execute();

        }

    }

    private void loadUnits(){
        if (mUnits != null){
            selectUnit();

        }else {
            BaseModel param = createGetParam(ApiUtil.PRODUCT_UNITS(), true);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    mUnits = list;
                    selectUnit();
                }

                @Override
                public void onError(String error) {

                }
            },1).execute();

        }

    }

    private void selectGroup(){
        CustomDropdow.createListDropdown(edGroup.getMoreButton(), DataUtil.listObject2ListString(mGroup, "name"), 0, false, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                edGroup.setText(data);
                group = mGroup.get(position);

            }
        });
    }
    private void selectUnit(){
        CustomDropdow.createListDropdown(edUnit.getMoreButton(), DataUtil.listObject2ListString(mUnits, "name"), 0, false, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {
                edUnit.setText(data);
                unit = mUnits.get(position);

            }
        });
    }

    private void promotionEvent(){
        edIsPromotion.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomDropdow.createListDropdown(
                        edIsPromotion.getMoreButton(),
                        DataUtil.createList2String(Util.getIconString(R.string.icon_gift, "  ", Constants.IS_PROMOTION),
                                Util.getIconString(R.string.icon_empty, "   ", Constants.NO_PROMOTION)),
                        0,
                        false,
                        new CallbackClickAdapter() {
                            @Override
                            public void onRespone(String data, int pos) {
                                if (pos ==0){
                                    edIsPromotion.setText(Constants.IS_PROMOTION);

                                }else if(pos ==1) {
                                    edIsPromotion.setText(Constants.NO_PROMOTION);

                                }

                            }

                        });

            }
        });
    }

}
