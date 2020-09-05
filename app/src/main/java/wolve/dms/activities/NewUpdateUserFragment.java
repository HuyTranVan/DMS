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
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.CallbackUri;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.apiconnect.apiserver.UploadCloudaryMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomInputDialog;
import wolve.dms.utils.CustomSQL;
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

public class NewUpdateUserFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName, edPhone, edGender, edRole, edEmail, edWarehouse;
    private TextView tvTitle, tvPasswordDefault;
    private Button btnSubmit;
    private CircleImageView imgUser;

    private UserActivity mActivity;

    private Uri imageChangeUri = null;
    private BaseModel currentUser;
    private BaseModel currentWarehouse;
    private List<BaseModel> listWarehouse;
    private String displayWarehouseFormat = "Kho mặc định: %s";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_user, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        listWarehouse = new ArrayList<>();
        String bundle = getArguments().getString(Constants.USER);
        tvPasswordDefault.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
        phoneEvent();
        if (bundle != null) {
            currentUser = new BaseModel(bundle);
            currentWarehouse = currentUser.hasKey("warehouse") ? currentUser.getBaseModel("warehouse") : new BaseModel();
            edName.setText(currentUser.getString("displayName"));
            edPhone.setText(currentUser.getString("phone"));
            edRole.setText(User.getRoleString(currentUser.getInt("role")));
            edGender.setText(currentUser.getInt("gender") == 0 ? "NAM" : "NỮ");
            edEmail.setText(currentUser.getString("email"));
            edWarehouse.setText(currentUser.hasKey("warehouse") ? String.format(displayWarehouseFormat, currentUser.getBaseModel("warehouse").getString("name")) : "");

            if (!Util.checkImageNull(currentUser.getString("image"))) {
                String url = Util.remakeURL(currentUser.getString("image"));
                Glide.with(this).load(url).dontAnimate().centerCrop().into(imgUser);
//                Picasso.get().load(url).into(imgUser);


            }

        } else {
            currentUser = new BaseModel();
            currentWarehouse = new BaseModel();
            edRole.setText(User.getRoleString(4));
            edGender.setText("NAM");

        }

        if (!CustomSQL.getBoolean(Constants.IS_ADMIN)) {
            edPhone.setFocusable(false);
            edRole.setFocusable(false);
            edWarehouse.setFocusable(false);

        } else {
            edPhone.setFocusable(true);
            roleEvent();
            warehouseEvent();
        }

    }


    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgUser.setOnClickListener(this);
        tvPasswordDefault.setOnClickListener(this);
        genderEvent();


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
        edEmail = view.findViewById(R.id.add_user_email);
        edWarehouse = view.findViewById(R.id.add_user_warehouse);
        tvPasswordDefault = view.findViewById(R.id.add_user_password_default);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();

                break;

            case R.id.add_user_submit:
                submitUser();

                break;

            case R.id.add_user_password_default:
                submitPasswordDefault();

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
                                //imageChangeUri = Uri.fromFile(Util.createTempFileOutput());
                                Transaction.startImageChooser(NewUpdateUserFragment.this, new CallbackUri() {
                                    @Override
                                    public void uriRespone(Uri uri) {
                                        imageChangeUri = uri;
                                    }
                                });
                            }

                            @Override
                            public void Method2(Boolean two) {
                                Transaction.startCamera(NewUpdateUserFragment.this, new CallbackUri() {
                                    @Override
                                    public void uriRespone(Uri uri) {
                                        imageChangeUri = uri;
                                    }
                                });

                            }

                            @Override
                            public void Method3(Boolean three) {
                                imageChangeUri = null;
                                currentUser.put("image", "");
                                Glide.with(mActivity).load(R.drawable.ic_user).fitCenter().into(imgUser);
                            }
                        });

                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CHOOSE_IMAGE) {
//            if (data != null) {
//                Crop.of(Uri.parse(data.getData().toString()), imageChangeUri).asSquare().withMaxSize(512, 512).start(mActivity, NewUpdateUserFragment.this);
//
//            }
//
//        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
//            //Uri tempUri = imageChangeUri;
//            Crop.of(imageChangeUri, imageChangeUri).asSquare().withMaxSize(512, 512).start(mActivity, NewUpdateUserFragment.this);
//
//        } else if (data != null && requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            Glide.with(this).load(imageChangeUri).centerCrop().into(imgUser);
//
//        } else if (requestCode == Crop.REQUEST_CROP) {
//            if (resultCode == RESULT_OK) {
//                Glide.with(this).load(imageChangeUri).centerCrop().into(imgUser);
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
                        .start(mActivity,  NewUpdateUserFragment.this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            CropImage.activity(imageChangeUri)
                    .setAspectRatio(1,1)
                    .setMaxZoom(10)
                    .setRequestedSize(512, 512)
                    .start(mActivity,  NewUpdateUserFragment.this);

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageChangeUri = result.getUri();
                Glide.with(this).load(imageChangeUri).centerCrop().into(imgUser);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Util.showToast(result.getError().getMessage());

            }
        }

    }


    private void phoneEvent() {
        edPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {
                currentUser.put("phone", Util.getPhoneValue(edPhone));

            }
        });

    }

    private void genderEvent() {
        edGender.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                List<BaseModel> listGender = new ArrayList<>();
                listGender.add(BaseModel.putValueToNewObject("text", "NAM"));
                listGender.add(BaseModel.putValueToNewObject("text", "NỮ"));

                CustomBottomDialog.choiceListObject("CHỌN GIỚI TÍNH", listGender, "text", new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edGender.setText(object.getString("text"));

                    }
                }, null);

            }
        });
    }

    private void roleEvent() {
        edRole.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomDialog.choiceListObject("CHỌN CHỨC VỤ", User.listRole(), "text", new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edRole.setText(object.getString("text"));
                        currentUser.put("role", object.getInt("index"));
                    }
                }, null);

            }
        });
    }

    private void warehouseEvent() {
        edWarehouse.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                if (listWarehouse.size() == 0) {
                    BaseModel param = createGetParam(ApiUtil.WAREHOUSES(), true);
                    new GetPostMethod(param, new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {
                            listWarehouse = list;
                            choiceWarehouse(filterListWarehouse(listWarehouse));
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, 1).execute();



                } else {
                    choiceWarehouse(filterListWarehouse(listWarehouse));
                }

            }
        });
    }

    private void choiceWarehouse(List<BaseModel> list) {
        if (list.size() > 0) {
            CustomBottomDialog.choiceListObject("CHỌN KHO HÀNG", list, "name", new CallbackObject() {
                @Override
                public void onResponse(BaseModel object) {
                    currentWarehouse = object;
                    edWarehouse.setText(String.format(displayWarehouseFormat, object.getString("name")));


                }
            }, null);
        } else {
            CustomCenterDialog.alertWithCancelButton("",
                    "Chưa tồn tại kho hàng của nhân viên này. Vui lòng tạo kho hàng!",
                    "đồng ý",
                    "quay lại", new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result) {
                                CustomInputDialog.inputWarehouse(view, new CallbackString() {
                                    @Override
                                    public void Result(String s) {
                                        currentWarehouse.put("name", s);
                                        edWarehouse.setText(String.format(displayWarehouseFormat, s));

                                    }
                                });
                            }
                        }
                    });

        }


    }

    private void submitUser() {
        if (edName.getText().toString().trim().equals("")
                || edPhone.getText().toString().trim().equals("")
                || edEmail.getText().toString().trim().equals("")) {
            Util.showSnackbar("Chưa nhập đủ thông tin!", null, null);

        } else if (!Util.isEmailValid(edEmail.getText().toString())) {
            Util.showSnackbar("Sai định dạng email!", null, null);

        } else if (!currentWarehouse.hasKey("name")) {
            Util.showSnackbar("Chưa chọn kho hàng cho nhân viên", null, null);

        } else if (imageChangeUri != null) {
            new UploadCloudaryMethod(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
                @Override
                public void Result(String url) {
                    updateUser(url);
                }

            }).execute();

//            SystemConnect.uploadImage(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
//                @Override
//                public void Result(String url) {
//                    updateUser(url);
//
//                }
//            });

        } else {
            if (currentUser.getInt("id") == 0) {
                updateUser("");
            } else if (currentUser.getString("image").startsWith("http")) {
                updateUser(currentUser.getString("image"));
            } else {
                updateUser("");
            }

        }
    }

    private void updateUser(String url) {
        if (currentWarehouse.hasKey("id")) {
            currentUser.put("warehouse_id", currentWarehouse.getInt("id"));
            currentUser.put("warehouse_name", currentWarehouse.getString("name"));
        } else {
            currentUser.put("warehouse_id", 0);
            currentUser.put("warehouse_name", currentWarehouse.getString("name"));

        }
        BaseModel param = createPostParam(ApiUtil.USER_NEW(),
                String.format(ApiUtil.USER_CREATE_PARAM,
                currentUser.getInt("id") == 0 ? "" : "id=" + currentUser.getString("id") + "&",
                Util.encodeString(edName.getText().toString().trim()),
                edGender.getText().toString().equals("NAM") ? 0 : 1,
                edEmail.getText().toString(),
                Util.getPhoneValue(edPhone),
                User.getIndex(edRole.getText().toString()),
                url,
                currentUser.getInt("warehouse_id"),
                Util.encodeString(currentUser.getString("warehouse_name"))),
                false,
                false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                if (CustomSQL.getBoolean(Constants.IS_ADMIN)) {
                    if (result.getInt("id") == User.getId()) {
                        CustomSQL.setBaseModel(Constants.USER, result);
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                    mActivity.loadUser();

                } else {
                    Transaction.gotoHomeActivityRight(true);

                }
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

//        UserConnect.CreateUser(param, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                if (CustomSQL.getBoolean(Constants.IS_ADMIN)) {
//                    if (result.getInt("id") == User.getId()) {
//                        CustomSQL.setBaseModel(Constants.USER, result);
//                    }
//                    getActivity().getSupportFragmentManager().popBackStack();
//                    mActivity.loadUser();
//
//                } else {
//                    Transaction.gotoHomeActivityRight(true);
//
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//
//        }, true);

    }

    private List<BaseModel> filterListWarehouse(List<BaseModel> list) {
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel baseModel : list) {
            if (baseModel.getInt("isMaster") == 3 && baseModel.getInt("user_id") == currentUser.getInt("id")) {
                results.add(baseModel);

            }
        }
        return results;
    }

    private void submitPasswordDefault() {
        BaseModel param = createGetParam(String.format(ApiUtil.USER_DEFAULT_PASS(),currentUser.getInt("id")), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                Util.showToast("Đặt mật khẩu về mặc định thành công");
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }


}
