package wolve.dms.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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
import wolve.dms.libraries.Security;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.BuildConfig.SERVER_URL;

public class SplashScreenActivity extends BaseActivity {
    private ProgressBar progressBar;

    private int SPLASH_TIME_OUT = 1000;

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
//        JSONObject object = new JSONObject();
//        try {
//            object.put("huy", "abd");
//            object.put("isgay", true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//
//        BaseModel paramCustomer = new BaseModel();
//        paramCustomer.put("url", SERVER_URL + "user");
//        paramCustomer.put("method", "POST");
//        paramCustomer.put("isjson", false );
//        paramCustomer.put("param", object );
//
//
//
//        new CustomPostMethod(paramCustomer, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Log.e("result", result.BaseModelstoString());
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }).execute();




        openUri();
        new Handler().postDelayed(new Runnable() {
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

                            CustomSQL.setBaseModel(Constants.USER, object);
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
        Intent intent = getIntent();
        //String action = intent.getAction();
        Uri data = intent.getData();


        if (data != null) {
            if (data.getQuery().contains("id")){
                String id = Security.decrypt(data.getQueryParameter("id"));
                CustomSQL.setString(Constants.CUSTOMER_ID, id);

                Log.e("idabc" , id);
            }




        }


    }

}
