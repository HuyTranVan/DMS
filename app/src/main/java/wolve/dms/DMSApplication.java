package wolve.dms;

import android.app.Application;


import java.util.Locale;


/**
 * Created by macos on 7/7/17.
 */

public class DMSApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        Fabric.with(this, new Crashlytics());
        Locale locale = new Locale("vi", "VN");
        Locale.setDefault(locale);

//        Fresco.initialize(getApplicationContext());
    }
}