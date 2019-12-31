package wolve.dms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnSuccessListener;

import wolve.dms.activities.NewUpdateProductGroupFragment;
import wolve.dms.activities.NewUpdateProductFragment;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;


public abstract class BaseActivity extends AppCompatActivity {
    protected LocationManager mLocationManager;
    protected FusedLocationProviderClient mFusedLocationClient;
    private boolean doubleBackToExitPressedOnce = false;

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

    public void showFragmentDialog(DialogFragment fragment){
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
        if(Util.getInstance().isLoading()){
            Util.getInstance().stopLoading(true);

        }else {
            switch (Util.getInstance().getCurrentActivity().getLocalClassName()){
                case "activities.HomeActivity":
                    if (doubleBackToExitPressedOnce) {
                        finish();
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Util.showToast("Ấn Back để thoát khỏi ứng dụng");

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);

                    break;

                case "activities.StatisticalActivity":
                    finish();
                    Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    break;

                case "activities.StatisticalCustomerActivity":
                    finish();
                    Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    break;

                case "activities.ProductGroupActivity":
                    Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
                    if(mFragment != null && mFragment instanceof NewUpdateProductGroupFragment
                            ||mFragment != null && mFragment instanceof NewUpdateProductFragment) {
                        getSupportFragmentManager().popBackStack();
                    }else {
                        Transaction.gotoHomeActivityRight(true);
                    }

                    break;


                case "activities.ProductActivity":
                    mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
                    if(mFragment != null && mFragment instanceof NewUpdateProductGroupFragment
                            ||mFragment != null && mFragment instanceof NewUpdateProductFragment) {
                        getSupportFragmentManager().popBackStack();
                    }else {
                        Transaction.gotoHomeActivityRight(true);
                    }

                    break;

                case "activities.StatusActivity":
                    mFragment = getSupportFragmentManager().findFragmentById(R.id.product_parent);
                    if(mFragment != null && mFragment instanceof NewUpdateProductGroupFragment
                            ||mFragment != null && mFragment instanceof NewUpdateProductFragment) {
                        getSupportFragmentManager().popBackStack();
                    }else {
                        Transaction.gotoHomeActivityRight(true);
                    }

                    break;



            }
        }

    }


    public void getCurrentLocation(final LocationListener mListener) {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    mListener.onLocationChanged(location);

                }
            }
        });
    }

}
