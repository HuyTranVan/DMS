package wolve.dms.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.CustomerConnect;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackBoolean;
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
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.BuildConfig.SERVER_URL;

public class SplashScreenActivity extends BaseActivity {
    private ProgressBar progressBar;

    private int SPLASH_TIME_OUT = 500;

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
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Util.isEmpty(CustomSQL.getString(Constants.USER_USERNAME)) && !Util.isEmpty(User.getToken())){
                    checkLogin(new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            progressBar.setVisibility(View.GONE);
                            if (result){
                                Transaction.gotoHomeActivity();

                            }else {
                                gotoLoginScreen();

                            }
                        }
                    });

                }else {
                    progressBar.setVisibility(View.GONE);
                    gotoLoginScreen();


                }

            }
        }, SPLASH_TIME_OUT);

        getFCMToken();
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

    private void checkLogin(CallbackBoolean listener){
        SystemConnect.getCheckLogin(new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                if (result.getBoolean("success")){
                    listener.onRespone(true);
                }else {
                    listener.onRespone(false);
                }
            }

            @Override
            public void onError(String error) {

            }
        }, false);



    }

    private void getFCMToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("tag", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.e("token", token);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                });

    }

//    private void openUri(){
//        Intent intent = getIntent();
//        Uri data = intent.getData();
//
//
//        if (data != null) {
//            if (data.getQuery().contains("id")){
//                String id = Security.decrypt(data.getQueryParameter("id"));
//                CustomSQL.setString(Constants.CUSTOMER_ID, id);
//
//                Log.e("idabc" , id);
//            }
//        }
//
//
//    }


}
