package wolve.dms.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;

import wolve.dms.R;
import wolve.dms.utils.Util;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        Util.getInstance().setCurrentActivity(this);

        if (Util.getInstance().checkPlayServices()) {
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            //FirebaseApp.initializeApp(this);
//            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//            if (refreshedToken != null) {
//                Log.d("Device TOKEN", refreshedToken);
//                FirebaseMessaging.getInstance().subscribeToTopic("cs");
//            }

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo_signup / company
             */

                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashScreenActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                    return;

                }
            }, SPLASH_TIME_OUT);
        }

    }

    @Override
    protected void onDestroy() {
        //Util.getInstance().stopLoading();
        super.onDestroy();
    }


}
