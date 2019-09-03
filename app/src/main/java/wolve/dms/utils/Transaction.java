package wolve.dms.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;

import wolve.dms.R;
import wolve.dms.activities.CustomerActivity;
import wolve.dms.activities.HomeActivity;
import wolve.dms.activities.LoginActivity;
import wolve.dms.activities.MapsActivity;
import wolve.dms.activities.PrintBillActivity;
import wolve.dms.activities.ProductActivity;
import wolve.dms.activities.ProductGroupActivity;
import wolve.dms.activities.ScannerActivity;
import wolve.dms.activities.ShopCartActivity;
import wolve.dms.activities.StatisticalActivity;
import wolve.dms.activities.StatisticalCustomerActivity;
import wolve.dms.activities.StatusActivity;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.libraries.Security;
import wolve.dms.models.Customer;
import wolve.dms.models.User;

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
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, MapsActivity.class);
        CustomSQL.setBoolean(Constants.ON_MAP_SCREEN, true);
//        if (customerid == null){
//
//
//        }else if (!customerid.equals("")){
//            intent.putExtra(Constants.CUSTOMER_ID, customerid);
//            CustomSQL.setString(Constants.CUSTOMER_ID, "");
//        }

        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //((AppCompatActivity) context).finish();

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

    public static void gotoCustomerActivity( Boolean isCheckin) {
        if (!User.getRole().equals(Constants.ROLE_WAREHOUSE)){
            Activity context = Util.getInstance().getCurrentActivity();
            Intent intent = new Intent(context, CustomerActivity.class);
            //CustomSQL.setString(Constants.CUSTOMER, customer);
            CustomSQL.setBoolean(Constants.CHECKIN_FLAG, isCheckin);
            CustomSQL.setLong(Constants.CHECKIN_TIME, Util.CurrentTimeStamp() );

            context.startActivityForResult(intent, Constants.RESULT_CUSTOMER_ACTIVITY);
            context.overridePendingTransition(R.anim.slide_up, R.anim.nothing);
        }

    }

    public static void returnCustomerActivity(String fromActivity, String data, int result){
        Intent returnIntent = Util.getInstance().getCurrentActivity().getIntent();
        returnIntent.putExtra(fromActivity, data);
        Util.getInstance().getCurrentActivity().setResult(result,returnIntent);

        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    public static void gotoShopCartActivity(String customer, String debt) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ShopCartActivity.class);
        intent.putExtra(Constants.CUSTOMER, customer);
        intent.putExtra(Constants.ALL_DEBT, debt);
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

    public static void gotoPrintBillActivity(String customer, String bill,String debt, Boolean rePrint) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, PrintBillActivity.class);
        intent.putExtra(Constants.RE_PRINT, rePrint);
        intent.putExtra(Constants.CUSTOMER, customer);
        intent.putExtra(Constants.BILLS, bill);
        intent.putExtra(Constants.ALL_DEBT, debt);
        context.startActivityForResult(intent, Constants.RESULT_PRINTBILL_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoStatisticalActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, StatisticalActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoScannerActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ScannerActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoStatisticalCustomerActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, StatisticalCustomerActivity.class);
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

    public static void sendEmailReport(String content){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse(String.format("mailto:%s?subject=%s&body=%s",
                Constants.DMS_EMAIL,
                "DMS Error Report!!",
                content));
        intent.setData(data);
        Util.getInstance().getCurrentActivity().startActivity(intent);

    }

    public static void shareViaZalo(String id){
        String param = String.format(Api_link.LUB_LINK_PARAM, Security.encrypt(id));
        //String param = String.format(Api_link.LUB_LINK_PARAM, id);
        ShareCompat.IntentBuilder.from(Util.getInstance().getCurrentActivity())
                .setType("text/plain")
                .setChooserTitle("Chia sẻ thông qua")
                .setText(Api_link.LUB_LINK + param)
//                .setText(String.format("www.google.com/maps/search/?api=1&query=%s,%s", lat, lng))
                .startChooser();

    }

    public static void openGoogleMap( double latitude, double longitude){
//        String uriBegin = "geo:" + latitude + "," + longitude;
//        String query = latitude + "," + longitude + "(" + label + ")";
//        String encodedQuery = Uri.encode(query);
//        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
//        Uri uri = Uri.parse(uriString);
//        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
//        Util.getInstance().getCurrentActivity().startActivity(mapIntent);

        String uriBegin = String.format("google.navigation:q=%s,%s", String.valueOf(latitude), String.valueOf(longitude));
        Uri gmmIntentUri = Uri.parse(uriBegin);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(Util.getInstance().getCurrentActivity().getPackageManager()) != null){
            Util.getInstance().getCurrentActivity().startActivity(mapIntent);

        }else {
            Util.showToast("Couldn't open map!");
        }

    }


}