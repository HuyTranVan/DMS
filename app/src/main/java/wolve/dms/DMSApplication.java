package wolve.dms;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;


import com.cloudinary.android.MediaManager;

import org.acra.ACRA;
import org.acra.annotation.AcraMailSender;

import java.util.Locale;


/**
 * Created by macos on 7/7/17.
 */
@AcraMailSender(mailTo = "tranvanhuy112@gmail.com")
public class DMSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MediaManager.init(getApplicationContext());

//        Fabric.with(this, new Crashlytics());

        Locale locale = new Locale("vi", "VN");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
