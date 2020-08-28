package wolve.dms.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomFixSQL;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.CustomTopDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;


public abstract class BaseActivity extends AppCompatActivity {
    protected LocationManager mLocationManager;
    public FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayout());
        Util.getInstance().setCurrentActivity(this);
        findViewById();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initialData();

            }
        }, 200);

        addEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.getInstance().setCurrentActivity(this);

    }

    public abstract int getResourceLayout();

    public abstract int setIdContainer();

    public abstract void findViewById();

    public abstract void initialData();

    public abstract void addEvent();

    //change fragment with bundle
    public void changeFragment(Fragment fragment, Bundle bundle, boolean isAnimation) {

        String tag = fragment.getClass().getSimpleName();
        fragment.setArguments(bundle);
        FragmentTransaction manager = this.getSupportFragmentManager().beginTransaction();

        if (isAnimation) {
//            manager.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            manager.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        }

        manager.replace(setIdContainer(), fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    //change fragment
    public void changeFragment(Fragment fragment, boolean isAnimation) {
        String tag = fragment.getClass().getSimpleName();
        Fragment myFragment = getSupportFragmentManager().findFragmentByTag(tag);
        Fragment fragmentReplace;
        if (myFragment != null) {
            fragmentReplace = myFragment;
        } else {
            fragmentReplace = fragment;
        }
        FragmentTransaction manager = this.getSupportFragmentManager().beginTransaction();
        if (isAnimation) {
//            manager.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            manager.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        }


        manager.replace(setIdContainer(), fragmentReplace, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void showFragmentDialog(DialogFragment fragment) {
        String tag = fragment.getClass().getSimpleName();
        DialogFragment myFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
        DialogFragment fragmentReplace;
        if (myFragment != null) {
            fragmentReplace = myFragment;
        } else {
            fragmentReplace = fragment;
        }

        FragmentTransaction manager = this.getSupportFragmentManager().beginTransaction();
        fragmentReplace.show(manager, tag);
    }

    @Override
    public void onBackPressed() {
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        }

    }

    public void getCurrentLocation(final LocationListener mListener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    mListener.onLocationChanged(location);

                }
            }
        });
    }

    public void receiveBundleFormFCM(String tag) {
        if (CustomTopDialog.dialog != null && CustomTopDialog.dialog.isShowing()) {
            CustomTopDialog.dialog.dismiss();

        } else {
            if (Util.isJSONObject(tag)) {
                BaseModel content = new BaseModel(tag);
                CustomTopDialog.showTextNotify(Util.getIconString(R.string.icon_username, " ", content.getString("title")),
                        content.getString("message"), new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean result) {
                                String currentActivity = Util.getInstance().getCurrentActivity().getLocalClassName();
                                switch (currentActivity) {
                                    case "activities.HomeActivity":
                                        HomeActivity activityHome = (HomeActivity) Util.getInstance().getCurrentActivity();
                                        activityHome.onRefresh();
                                        break;

                                    case "activities.ImportActivity":
                                        ImportActivity activityImport = (ImportActivity) Util.getInstance().getCurrentActivity();
                                        activityImport.reloadListImport(true, true);
                                        break;

                                }

                            }
                        });

            } else {
                CustomTopDialog.showTextNotify("Notify", tag, null);
            }

        }




//        Intent intent = new Intent("LISTEN_FROM_GCM");
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

//        String currentActivity = Util.getInstance().getCurrentActivity().getLocalClassName();
//
//            if (currentActivity.equals("Activities.HomeActivity")){
//                Intent intent = new Intent("LISTEN_FROM_GCM");
//    ////            intent.putExtra("message", bundle.getString("message"));
//    ////            intent.putExtra("id", bundle.getString("id"));
//                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//            }
    }

    public void logout(CallbackBoolean listener){
        BaseModel param = createGetParam(ApiUtil.LOGOUT(), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                if (result.getBoolean("success")) {
                    Util.deleteAllImageExternalStorage();
                    CustomSQL.clear();
                    if (listener != null){
                        listener.onRespone(true);

                    }else {
                        Util.showToast("Đăng xuất thành công");
                        Transaction.gotoLoginActivityRight();

                    }


                } else {
                    Util.showSnackbar("Đăng xuất thất bại", null, null);
                }
            }

            @Override
            public void onError(String error) {

            }
        }, true).execute();

    }

    public void login(final String username, final String pass, String fcm_token, final CallbackBoolean mListener) {
        BaseModel param = createPostParam(ApiUtil.LOGIN(),
                String.format(ApiUtil.LOGIN_PARAM, username, pass, fcm_token),
                false,
                false);

        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel object, List<BaseModel> list) {
                CustomSQL.setString(Constants.DISTRIBUTOR, object.getString("distributor"));
                CustomSQL.setString(Constants.DISTRICT_LIST, object.getString("district"));
                CustomSQL.setString(Constants.USER_USERNAME, username);
                CustomSQL.setString(Constants.USER_PASSWORD, pass);
                CustomSQL.setInt(Constants.VERSION_CODE, CustomSQL.getInt(Constants.VERSION_CODE));
                object.removeKey("district");

                ///////////////saveUser;
                List<BaseModel> listUser = CustomFixSQL.getListObject(Constants.USER_LIST);
                if (!DataUtil.checkDuplicate(listUser, "id", object)) {
                    listUser.add(object);
                }
                CustomFixSQL.setListBaseModel(Constants.USER_LIST, listUser);
                /////////////
                object.removeKey("distributor");

                CustomSQL.setBaseModel(Constants.USER, object);
                if (object.getInt("role") == Constants.ROLE_ADMIN) {
                    CustomSQL.setBoolean(Constants.IS_ADMIN, true);
                } else {
                    CustomSQL.setBoolean(Constants.IS_ADMIN, false);
                }

                mListener.onRespone(true);
            }

            @Override
            public void onError(String error) {
                mListener.onRespone(false);
            }
        }, true).execute();


    }

    public static BaseModel createGetParam(String url, boolean resultIsList) {
        BaseModel paramResult = new BaseModel();
        paramResult.put("url", url);
        paramResult.put("method", "GET");
        paramResult.put("isjson", false);
        paramResult.put("param", null);
        paramResult.put("resultType", resultIsList? Constants.TYPE_ARRAY : Constants.TYPE_OBJECT);

        return paramResult;

    }

    public static BaseModel createPostParam(String url, String param, boolean paramIsJSON,  boolean resultIsList) {
        BaseModel paramResult = new BaseModel();
        paramResult.put("url", url);
        paramResult.put("method", "POST");
        paramResult.put("isjson", paramIsJSON);
        paramResult.put("param", param);
        paramResult.put("resultType", resultIsList? Constants.TYPE_ARRAY : Constants.TYPE_OBJECT);

        return paramResult;

    }

}
