package wolve.dms;

import android.app.Application;
import android.content.res.Configuration;


import com.cloudinary.android.MediaManager;

import java.util.Locale;


/**
 * Created by macos on 7/7/17.
 */

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
}
