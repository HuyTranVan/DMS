package wolve.dms.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

public class SplashScreenActivity extends BaseActivity {
    private ProgressBar progressBar;

    private int SPLASH_TIME_OUT = 1500;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        progressBar = findViewById(R.id.splash_loading);
    }

    @Override
    public void initialData() {
        openUri();
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo_signup / company
             */

            @Override
            public void run() {
                if (CustomSQL.getString(Constants.USER_USERNAME).isEmpty()){
                    gotoLoginScreen();

                }else {
                    String params = String.format(Api_link.LOGIN_PARAM,CustomSQL.getString(Constants.USER_USERNAME), CustomSQL.getString(Constants.USER_PASSWORD));
                    progressBar.setVisibility(View.VISIBLE);
                    UserConnect.Login(params, new CallbackCustom() {
                        @Override public void onResponse(BaseModel object) {
                            progressBar.setVisibility(View.GONE);
                            Distributor distributor = new Distributor(object.getJsonObject("distributor"));

                            CustomSQL.setObject(Constants.USER, object);
                            CustomSQL.setObject(Constants.DISTRIBUTOR, distributor);

                            Util.showToast("Đăng nhập thành công");
                            Transaction.gotoHomeActivity(true);

                        }

                        @Override
                        public void onError(String error) {
                            progressBar.setVisibility(View.GONE);
                            gotoLoginScreen();
                        }

                    }, false,true);
                }

            }
        }, SPLASH_TIME_OUT);
//                    new CallbackJSONObject() {
//                        @Override
//                        public void onResponse(JSONObject result) {
//                            progressBar.setVisibility(View.GONE);
//                            try {
//                                User user = new User(result);
//                                Distributor distributor = new Distributor(result.getJSONObject("distributor"));
//
//                                CustomSQL.setObject(Constants.USER, user);
//                                CustomSQL.setObject(Constants.DISTRIBUTOR, distributor);
//
//                                Util.showToast("Đăng nhập thành công");
//                                Transaction.gotoHomeActivity(true);
//
//                            } catch (JSONException e) {
//                                gotoLoginScreen();
//                            }
//                        }
//
//                        @Override
//                        public void onError(String error) {
//                            progressBar.setVisibility(View.GONE);
//                            gotoLoginScreen();
//                        }
//                    }, false,true);

    }

    @Override
    public void addEvent() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void gotoLoginScreen(){
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        SplashScreenActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void openUri(){
        Intent intent = new Intent("com.dms.wolve");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        Bundle bundle = new Bundle();
        bundle.putString("msg_from_browser", "Launched from Browser");
        intent.putExtras(bundle);

        Log.e("mobikul-->", intent.toUri(Intent.URI_INTENT_SCHEME));
    }

}
