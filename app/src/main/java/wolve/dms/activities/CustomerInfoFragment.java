package wolve.dms.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CButtonVertical;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static android.content.Context.CLIPBOARD_SERVICE;
import static wolve.dms.activities.BaseActivity.createPostParam;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerInfoFragment extends Fragment implements View.OnClickListener {
    private View view, vStatusColor;
    private CInputForm edAdress, edPhone, edName, edNote;
    private EditText edShopName;
    protected CButtonVertical mDirection, mRating, mCall, mInterest, mDelete, mShare;
    protected TextView tvLastCheckin, tvCheckin, tvHistory;
    private Spinner spShoptype;

    private CustomerActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_info, container, false);

        initializeView();

        addEvent();

        return view;
    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        mActivity.infoFragment = this;

        edAdress = (CInputForm) view.findViewById(R.id.customer_info_adress);
        edPhone = (CInputForm) view.findViewById(R.id.customer_info_phone);
        edName = (CInputForm) view.findViewById(R.id.customer_info_name);
        edShopName = (EditText) view.findViewById(R.id.customer_info_shopname_name);
        mDirection = view.findViewById(R.id.customer_info_direction);
        mShare = view.findViewById(R.id.customer_info_share);
        mInterest = view.findViewById(R.id.customer_info_interest);
        mCall = view.findViewById(R.id.customer_info_call);
        mRating = view.findViewById(R.id.customer_info_rating);
        mDelete = view.findViewById(R.id.customer_info_delete);
        edNote = view.findViewById(R.id.customer_info_note);
        tvLastCheckin = view.findViewById(R.id.customer_info_last_status);
        vStatusColor = view.findViewById(R.id.customer_info_status_color);
        tvCheckin = view.findViewById(R.id.customer_checkin);
        spShoptype = view.findViewById(R.id.customer_info_shoptype);
        tvHistory = view.findViewById(R.id.customer_history);

    }

    protected void reshowAddress(BaseModel address) {
        String add ="";
        edAdress.setFocusable(false);

        if (address.getString("district").equals("") && address.getString("province").equals("")){
            add ="Chưa có địa chỉ";
            edAdress.setTextColor(getResources().getColor(R.color.colorRedTransparent));
        }else {
            add = String.format("%s, %s, %s",
                    Util.isEmpty(address.getString("address")) ? "" : address.getString("address"),
                    //Util.isEmpty(address.getString("street")) ? "" : address.getString("street"),
                    address.getString("district"),
                    address.getString("province"));
            edAdress.setTextColor(getResources().getColor(R.color.black_text_color));

        }
        edAdress.setText(add);
        //mActivity.tvAddress.setText(add);

    }

    public void reloadInfo() {
        edPhone.setIconMoreText(mActivity.getResources().getString(R.string.icon_copy));
        edAdress.setIconMoreText(mActivity.getResources().getString(R.string.icon_edit_pen));
        edNote.setIconMoreText(mActivity.getResources().getString(R.string.icon_edit_pen));
        edNote.setFocusable(false);
        reshowAddress(mActivity.currentCustomer);
        setupShopTypeSpinner();
        spShoptype.setSelection(mActivity.currentCustomer.getInt("shopType"));

        mRating.setIconText(
                mActivity.currentCustomer.getDoubleValue("rating") > 1 ?
                Util.FormatMoney(mActivity.currentCustomer.getDouble("rating")) :
                Util.getStringIcon("1", "", R.string.icon_star));
        mRating.setText(String.format("%d Đánh giá", mActivity.currentCustomer.getInt("rating_count")));
        edPhone.setText(Util.FormatPhone(mActivity.currentCustomer.getString("phone")));
        edName.setText(mActivity.currentCustomer.getString("name"));
        edShopName.setText(mActivity.currentCustomer.getString("signBoard"));
        edNote.setText(getNoteText(mActivity.currentCustomer.getString("note")));

        if (mActivity.listCheckins.size() > 0) {
            String note = mActivity.listCheckins.get(mActivity.listCheckins.size() - 1).getInt("meetOwner") == 0 ?
                    "Không gặp chủ nhà. " + mActivity.listCheckins.get(mActivity.listCheckins.size() - 1).getString("note") :
                    mActivity.listCheckins.get(mActivity.listCheckins.size() - 1).getString("note");

            String checkin = String.format("%s %s", Util.DateHourString(mActivity.listCheckins.get(mActivity.listCheckins.size() - 1).getLong("createAt")), note);
            tvHistory.setVisibility(View.VISIBLE);
            tvLastCheckin.setVisibility(View.VISIBLE);
            tvLastCheckin.setText(checkin);
            tvHistory.setText(Util.getIconString(R.string.icon_history, "   ", String.format("Lịch sử ghé cửa hàng (%d)", mActivity.listCheckins.size())));
        } else {
            tvHistory.setVisibility(View.GONE);
            tvLastCheckin.setVisibility(View.GONE);

        }
        mCall.setVisibility(mActivity.currentCustomer.getString("phone").equals("") ? View.GONE : View.VISIBLE);
        updateStatus(mActivity.currentCustomer.getInt("status_id"));

//todo addevent after setdata
        phoneEvent();
        nameEvent();
        shopNameEvent();
        noteEvent();
    }

    private void addEvent() {
        tvHistory.setOnClickListener(this);
        mDirection.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mCall.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        tvCheckin.setOnClickListener(this);
        edAdress.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                mActivity.openEditMapFragment();

            }
        });
        edNote.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNote();

            }
        });
        edPhone.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", Util.getPhoneValue(edPhone));
                manager.setPrimaryClip(clipData);

                Util.showToast("Đã copy số điện thoại");
            }
        });

        tvCheckin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialogCheckin();
                return true;
            }
        });


    }

    private void setupShopTypeSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), R.layout.view_spinner_text, Constants.shopName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spShoptype.setAdapter(dataAdapter);

        spShoptype.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Util.hideKeyboard(view);
                return false;
            }
        });

        spShoptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mActivity.saveCustomerToLocal("shopType", i);
                updateTitle();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

    }

    private void updateStatus(int status) {
        if (status == 2) {
            //tvType.setTextColor(mActivity.getResources().getColor(R.color.black_text_color_hint));

            mInterest.setText("Không quan tâm");
            mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_grey));
            mInterest.setTextColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            mInterest.setIconText(mActivity.getResources().getString(R.string.icon_x));
            mDirection.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            mCall.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            vStatusColor.setBackgroundColor(mActivity.getResources().getColor(R.color.black_text_color_hint));

            edAdress.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edNote.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edName.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));
            edPhone.setIconColor(mActivity.getResources().getColor(R.color.black_text_color_hint));


            mActivity.btnShopCart.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_white_border_blue));
            mActivity.btnShopCart.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));


        } else {
            //tvType.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));

            mDirection.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            mCall.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));

            edAdress.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edNote.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edName.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));
            edPhone.setIconColor(mActivity.getResources().getColor(R.color.colorBlue));


            mActivity.btnShopCart.setBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_blue));
            mActivity.btnShopCart.setTextColor(mActivity.getResources().getColor(R.color.colorWhite));

            if (status == 4) {
                mInterest.setText("Dừng mua hàng");
                mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_purple));
                mInterest.setTextColor(mActivity.getResources().getColor(R.color.colorGrape));
                mInterest.setIconText(mActivity.getResources().getString(R.string.icon_warning));
                vStatusColor.setBackgroundColor(mActivity.getResources().getColor(R.color.colorGrape));

            } else if (status == 3) {
                mInterest.setText("Đã mua hàng");
                mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_blue));
                mInterest.setTextColor(mActivity.getResources().getColor(R.color.colorBlue));
                mInterest.setIconText(mActivity.getResources().getString(R.string.icon_check));
                vStatusColor.setBackgroundColor(mActivity.getResources().getColor(R.color.colorBlue));

            } else if (status == 1) {
                mInterest.setText("Có quan tâm");
                mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_orange));
                mInterest.setTextColor(mActivity.getResources().getColor(R.color.orange_dark));
                mInterest.setIconText(mActivity.getResources().getString(R.string.icon_heart));
                vStatusColor.setBackgroundColor(mActivity.getResources().getColor(R.color.orange_dark));

            } else if (status == 0) {
                mInterest.setText("Khách hàng mới");
                mInterest.setIconBackground(mActivity.getResources().getDrawable(R.drawable.btn_round_pink));
                mInterest.setTextColor(mActivity.getResources().getColor(R.color.colorPink));
                mInterest.setIconText(mActivity.getResources().getString(R.string.icon_star));
                vStatusColor.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPink));
            }

        }

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.customer_info_direction:
                openGoogleMap();

                break;

            case R.id.customer_info_share:
                Transaction.shareGoogleMapLocation(mActivity.currentCustomer);

                break;

            case R.id.customer_info_call:
                mActivity.checkPhonePermission();
                //Transaction.openCallScreen(mActivity.currentCustomer.getString("phone"));

                break;

            case R.id.customer_history:
                mActivity.changeFragment(new CustomerCheckinFragment(), true);

                break;

            case R.id.customer_checkin:
                if (!tvCheckin.getText().toString().toLowerCase().contains("checkin")) {
                    showDialogNote();

                } else {
                    showDialogCheckin();

                }

                break;

            case R.id.customer_info_delete:
//                mActivity.printCustomerShipping();
                mActivity.deleteCustomer();

                break;

        }
    }

    private void openGoogleMap() {
        Transaction.openGoogleMapRoute(mActivity.currentCustomer.getDouble("lat"), mActivity.currentCustomer.getDouble("lng"));
//        CustomCenterDialog.alertWithCancelButton("Chỉ đường", "Mở ứng dụng bản đồ để tiếp tục chỉ đường", "Tiếp tục", "Quay lại", new CallbackBoolean() {
//            @Override
//            public void onRespone(Boolean re) {
//                if (re) {
//                    Transaction.openGoogleMapRoute(mActivity.currentCustomer.getDouble("lat"), mActivity.currentCustomer.getDouble("lng"));
//
//                }
//
//            }
//        });
    }

    private void shopNameEvent() {
        Util.textEvent(edShopName, new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.saveCustomerToLocal("signBoard", s);
                updateTitle();

            }
        });
    }

    private void phoneEvent() {
        edPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {
                if (Util.isEmpty(s)){
                    edPhone.setIconMoreText(null);

                }else {
                    edPhone.setIconMoreText(mActivity.getResources().getString(R.string.icon_copy));
                }
                mActivity.saveCustomerToLocal("phone", s);
                mCall.setVisibility(mActivity.currentCustomer.getString("phone").equals("") ? View.GONE : View.VISIBLE);

            }
        });
    }

    private void nameEvent() {
        edName.addTextChangeName(new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.saveCustomerToLocal("name", s);

            }
        });
    }

    private void noteEvent() {
//        Util.textEvent(edNote, new CallbackString() {
//            @Override
//            public void Result(String s) {
//                mActivity.saveCustomerToLocal("note", getCustomerNote(edNote.getText().toString()));
//            }
//        });
    }

    private String getNoteText(String note) {
        if (!note.isEmpty()) {
            try {
                JSONObject object = new JSONObject(note.trim());
                return object.getString("note");


            } catch (JSONException e) {
                return note;
            }
        } else {
            return "";
        }

    }


    private void updateTitle() {
        mActivity.tvTitle.setText(String.format("%s %s",
                Constants.shopName[mActivity.currentCustomer.getInt("shopType")],
                mActivity.currentCustomer.getString("signBoard")));


    }

    protected void submitCheckin(String paramdetail, int status_id, CallbackBoolean listener) {
        BaseModel param = createPostParam(ApiUtil.CHECKIN_NEW(), paramdetail, false, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onRespone(true);
            }

            @Override
            public void onError(String error) {

            }
        },1).execute();

    }

    private void showDialogNote() {
        String currentNote = getNoteText(mActivity.currentCustomer.getString("note"));
        CustomCenterDialog.showReasonChoice("Ghi chú khách hàng",
                "Nhập nội dung ghi chú ",
                Util.isEmpty(currentNote) ? "" : currentNote + "\n",
                "quay lại",
                "lưu",
                false,
                false,
                new CallbackString() {
                    @Override
                    public void Result(String s) {
                        edNote.setText(s);
                        mActivity.saveCustomerToLocal("note", s);

                    }
                });
    }

    private void showDialogCheckin(){
        int currentRating = mActivity.listCheckins.size() > 0 ?
                mActivity.listCheckins.get(mActivity.listCheckins.size() - 1).getInt("rating") :
                1;
        CustomCenterDialog.showCheckinDialog("Chi tiết ghé thăm cửa hàng",
                mActivity.currentCustomer.getInt("id"),
                mActivity.currentCustomer.getInt("status_id"),
                mActivity.currentCustomer.getInt("rating"),
                new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        submitCheckin(object.getString("param"),
                                object.getInt("status"),
                                new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result){
                                if (result) {
                                    if (object.getBoolean("updateStatus")) {
                                        mActivity.currentCustomer.put("status_id", object.getInt("status"));
                                        mActivity.submitCustomer(mActivity.currentCustomer, mActivity.listCheckins.size() + 1, new CallbackBoolean() {
                                            @Override
                                            public void onRespone(Boolean result) {
                                                mActivity.returnPreviousScreen(mActivity.currentCustomer);

                                            }
                                        });

                                    } else {
                                        mActivity.returnPreviousScreen(mActivity.currentCustomer);

                                    }


                                }
                            }
                        });

                    }
                });
    }


}

