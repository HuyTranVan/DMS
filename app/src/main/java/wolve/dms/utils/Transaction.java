package wolve.dms.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import wolve.dms.R;
import wolve.dms.activities.CustomerActivity;
import wolve.dms.activities.HomeActivity;
import wolve.dms.activities.LoginActivity;
import wolve.dms.activities.MapsActivity;
import wolve.dms.activities.PrintBillActivity;
import wolve.dms.activities.ProductActivity;
import wolve.dms.activities.ProductGroupActivity;
import wolve.dms.activities.ShopCartActivity;
import wolve.dms.activities.StatisticalActivity;
import wolve.dms.activities.StatusActivity;
import wolve.dms.models.Customer;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;


public class Transaction {

    public static void gotoLoginActivityRight() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoHomeActivity(Boolean login) {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, HomeActivity.class);
        //intent.putExtra(Constants.LOGIN_SUCCESS, login);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoHomeActivityRight(Boolean isFinnish) {
        CustomSQL.setBoolean(Constants.ON_MAP_SCREEN, false);

        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        if (isFinnish){
            ((AppCompatActivity) context).finish();
        }
    }

    public static void gotoMapsActivity() {
        CustomSQL.setBoolean(Constants.ON_MAP_SCREEN, true);

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

    public static void gotoProductGroupActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ProductGroupActivity.class);
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

    public static void gotoCustomerActivity(String customer, Boolean isCheckin) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, CustomerActivity.class);
        CustomSQL.setString(Constants.CUSTOMER, customer);
        CustomSQL.setBoolean(Constants.CHECKIN_FLAG, isCheckin);
        CustomSQL.setLong(Constants.CHECKIN_TIME, Util.CurrentTimeStamp() );

        context.startActivityForResult(intent, Constants.RESULT_CUSTOMER_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void returnCustomerActivity(String fromActivity, String data, int result){
        Intent returnIntent = Util.getInstance().getCurrentActivity().getIntent();
        returnIntent.putExtra(fromActivity, data);
        Util.getInstance().getCurrentActivity().setResult(result,returnIntent);

        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    public static void gotoShopCartActivity(String customer) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ShopCartActivity.class);
        intent.putExtra(Constants.CUSTOMER, customer);
        context.startActivityForResult(intent, Constants.RESULT_SHOPCART_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void returnShopCartActivity(String fromActivity, String data, int result){
        Intent returnIntent = Util.getInstance().getCurrentActivity().getIntent();
        returnIntent.putExtra(fromActivity, data);
        Util.getInstance().getCurrentActivity().setResult(result,returnIntent);

        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    public static void gotoPrintBillActivity(String customer, String bill, Boolean rePrint) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, PrintBillActivity.class);
        intent.putExtra(Constants.RE_PRINT, rePrint);
        intent.putExtra(Constants.CUSTOMER, customer);
        intent.putExtra(Constants.BILLS, bill);
        context.startActivityForResult(intent, Constants.RESULT_PRINTBILL_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoStatisticalActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, StatisticalActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public static void gotoImageChooser() {
        // Kiểm tra permission với android sdk >= 23
        //imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
        Activity context = Util.getInstance().getCurrentActivity();
        if (Build.VERSION.SDK_INT <= 19) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            context.startActivityForResult(i, REQUEST_CHOOSE_IMAGE);

        } else if (Build.VERSION.SDK_INT > 19) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            context.startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);

        }
    }


}