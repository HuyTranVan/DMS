package wolve.dms.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import wolve.dms.R;
import wolve.dms.activity.CustomerActivity;
import wolve.dms.activity.HomeActivity;
import wolve.dms.activity.MapsActivity;
import wolve.dms.activity.ProductActivity;
import wolve.dms.activity.StatusActivity;


public class Transaction {

    public static void gotoHomeActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoHomeActivityRight(Boolean isFinnish) {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        if (isFinnish){
            ((AppCompatActivity) context).finish();
        }
    }

    public static void gotoMapsActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, MapsActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoProductActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ProductActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoStatusActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, StatusActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoCustomerActivity(String customer) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, CustomerActivity.class);
        intent.putExtra(Constants.CUSTOMER, customer);
        context.startActivityForResult(intent, Constants.RESULT_CUSTOMER_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


    }
}