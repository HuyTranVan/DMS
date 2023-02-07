package wolve.dms.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import java.util.List;

import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.activities.AccountActivity;
import wolve.dms.activities.CustomerActivity;
import wolve.dms.activities.DistributorActivity;
import wolve.dms.activities.EditTempBillActivity;
import wolve.dms.activities.HomeActivity;
import wolve.dms.activities.ImportActivity;
import wolve.dms.activities.LoginActivity;
import wolve.dms.activities.MapsActivity;
import wolve.dms.activities.PrintBillActivity;
import wolve.dms.activities.PrintCustomerShippingActivity;
import wolve.dms.activities.ProductActivity;
import wolve.dms.activities.ProductGroupActivity;
import wolve.dms.activities.ScannerActivity;
import wolve.dms.activities.SettingActivity;
import wolve.dms.activities.ShopCartActivity;
import wolve.dms.activities.StatisticalActivity;
import wolve.dms.activities.CashFlowTypeActivity;
import wolve.dms.activities.UserActivity;
import wolve.dms.activities.WarehouseActivity;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackUri;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;


public class Transaction {

    public static void openGooglePlay() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri.Builder uriBuilder = Uri.parse("https://play.google.com/store/apps/details")
                .buildUpon()
                .appendQueryParameter("id", "wolve.dms")
                .appendQueryParameter("launch", "false");

// Optional parameters, such as referrer, are passed onto the launched
// instant app. You can retrieve these parameters using
// Activity.getIntent().getData().
        //uriBuilder.appendQueryParameter("referrer", "exampleCampaignId");

        intent.setData(uriBuilder.build());
        //intent.setPackage("com.android.vending");
        Util.getInstance().getCurrentActivity().startActivity(intent);
    }

    public static void gotoLoginActivityRight() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoHomeActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, HomeActivity.class);
//        intent.putExtra(Constants.LOGIN_SUCCESS, login);
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
        if (isFinnish) {
            ((AppCompatActivity) context).finish();
        }
    }

    public static void gotoMapsActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, MapsActivity.class);
        CustomSQL.setBoolean(Constants.ON_MAP_SCREEN, true);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();

    }

    public static void gotoWarehouseActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, WarehouseActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoAccountActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, AccountActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();

    }

    public static void gotoImportActivity(BaseModel curentwarehouse) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ImportActivity.class);
        intent.putExtra(Constants.WAREHOUSE, curentwarehouse.BaseModelstoString());
        //intent.putExtra(Constants.FLAG, flag);
        context.startActivityForResult(intent, Constants.RESULT_IMPORT_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


    }

    public static void gotoSettingActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoProductActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ProductActivity.class);
        context.startActivityForResult(intent, Constants.RESULT_IMPORT_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        context.startActivity(intent);
//        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        ((AppCompatActivity) context).finish();
    }

    public static void gotoProductGroupActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ProductGroupActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoUserActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, UserActivity.class);
        //intent.putExtra(Constants.FLAG, userdetail);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoCashFlowTypeActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, CashFlowTypeActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();

    }

    public static void gotoCustomerActivity() {
        if (User.getCurrentRoleId() != Constants.ROLE_WAREHOUSE) {
            Activity context = Util.getInstance().getCurrentActivity();
            Intent intent = new Intent(context, CustomerActivity.class);
            CustomSQL.setLong(Constants.CHECKIN_TIME, Util.CurrentTimeStamp());
            context.startActivityForResult(intent, Constants.RESULT_CUSTOMER_ACTIVITY);
            context.overridePendingTransition(R.anim.slide_in_up, R.anim.nothing);

        }

    }

    public static void returnCustomerActivity(String fromActivity, String data, int result) {
        Intent returnIntent = Util.getInstance().getCurrentActivity().getIntent();
        returnIntent.putExtra(fromActivity, data);
        Util.getInstance().getCurrentActivity().setResult(result, returnIntent);

        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    public static void gotoShopCartActivity(String debt) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ShopCartActivity.class);
        intent.putExtra(Constants.ALL_DEBT, debt);
        context.startActivityForResult(intent, Constants.RESULT_SHOPCART_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoEditTempBillActivity(int bill_id, int temp_id) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, EditTempBillActivity.class);
        intent.putExtra(Constants.BILL_ID, bill_id);
        intent.putExtra(Constants.TEMPBILL_ID, temp_id);
        context.startActivityForResult(intent, Constants.RESULT_SHOPCART_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void returnShopCartActivity(String fromActivity, String data, int result) {
        Intent returnIntent = Util.getInstance().getCurrentActivity().getIntent();
        returnIntent.putExtra(fromActivity, data);
        Util.getInstance().getCurrentActivity().setResult(result, returnIntent);

        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    public static void returnPreviousActivity(String fromActivity, BaseModel data, int result) {
        Intent returnIntent = Util.getInstance().getCurrentActivity().getIntent();
        returnIntent.putExtra(fromActivity, data.BaseModelstoString());
        Util.getInstance().getCurrentActivity().setResult(result, returnIntent);
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    public static void returnPreviousActivity() {
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Util.getInstance().getCurrentActivity().finish();

    }

    public static void checkInventoryBeforePrintBill(BaseModel bill, List<BaseModel> listproduct, int warehouse_id, CallbackBoolean submit) {
        if (Distributor.getImportFunction() == 1){
            DataUtil.checkInventory(listproduct, warehouse_id, new CallbackListObject(){
                @Override
                public void onResponse(List<BaseModel> list){
                    if (list.size() > 0) {

                        CustomCenterDialog.showListProductWithDifferenceQuantity(User.getCurrentUser().getBaseModel("warehouse").getString("name") + ": KHÔNG ĐỦ TỒN KHO ",
                                list,
                                new CallbackBoolean() {
                                    @Override
                                    public void onRespone(Boolean result) {
                                        if (result) {
                                            submit.onRespone(true);
                                            CustomSQL.setListBaseModel(Constants.PRODUCT_SUGGEST_LIST, listproduct);
                                            gotoImportActivity(User.getCurrentUser().getBaseModel("warehouse"));

                                        }else {
                                            submit.onRespone(false);

                                        }

                                    }
                                });

                    } else {
                        gotoPrintBillActivity(bill, false);
                    }
                }
            });

        }else {
            gotoPrintBillActivity(bill, false);
        }


    }

    public static void gotoPrintBillActivity(BaseModel bill, Boolean rePrint) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, PrintBillActivity.class);
        intent.putExtra(Constants.RE_PRINT, rePrint);
        intent.putExtra(Constants.BILL, bill.BaseModelstoString());
        context.startActivityForResult(intent, Constants.RESULT_PRINTBILL_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoPrintShippingActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, PrintCustomerShippingActivity.class);
//        intent.putExtra(Constants.RE_PRINT, rePrint);
//        intent.putExtra(Constants.BILL, bill.BaseModelstoString());
        context.startActivityForResult(intent, Constants.RESULT_PRINTSHIPPING_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoStatisticalActivity(String user) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, StatisticalActivity.class);
        intent.putExtra(Constants.USER, user);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void gotoScannerActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ScannerActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoDistributorActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, DistributorActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void sendEmailReport(String content) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse(String.format("mailto:%s?subject=%s&body=%s",
                Constants.DMS_EMAIL,
                "DMS Error Report!!",
                content));
        intent.setData(data);
        Util.getInstance().getCurrentActivity().startActivity(intent);

    }


    public static void shareViaOtherApp(String content) {
//        ShareCompat.IntentBuilder.from(Util.getInstance().getCurrentActivity())
//                .setType("text/plain")
//                .setChooserTitle("Chia sẻ thông qua")
//                .setText(content)
//                .startChooser();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,content);
//        sendIntent.setPackage("com.facebook.orca");
        sendIntent.setType("text/plain");

        switch (CustomFixSQL.getInt(Constants.PACKAGE_DEFAULT)){
            case 1:
                sendIntent.setPackage("com.zing.zalo");
                break;

            case 2:
                sendIntent.setPackage("com.viber.voip");
                break;

            case 3:
                sendIntent.setPackage("com.facebook.orca");
                break;
        }

        try {
            Util.getInstance().getCurrentActivity().startActivity(sendIntent);
        }
        catch (android.content.ActivityNotFoundException ex) {
            switch (CustomFixSQL.getInt(Constants.PACKAGE_DEFAULT)){
                case 1:
                    Util.showToast("Please Install Zalo");
                    break;

                case 2:
                    Util.showToast("Please Install Viber");
                    break;

                case 3:
                    Util.showToast("Please Install Facebook Messenger");
                    break;
            }

        }

    }

    public static void shareImage(Uri uri, BaseModel currentCustomer) {
        String message = currentCustomer == null? "" : String.format("%s %s ::: %s",
                Constants.shopName[currentCustomer.getInt("shopType")].toUpperCase(),
                currentCustomer.getString("signBoard").toUpperCase(),
                Util.CurrentMonthYearHourNotBlank());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("image/*");
//        sendIntent.setPackage("com.zing.zalo");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        switch (CustomFixSQL.getInt(Constants.PACKAGE_DEFAULT)){
            case 1:
                sendIntent.setPackage("com.zing.zalo");
                break;

            case 2:
                sendIntent.setPackage("com.viber.voip");
                break;

            case 3:
                sendIntent.setPackage("com.facebook.orca");
                break;
        }

        try {
            Util.getInstance().getCurrentActivity().startActivity(sendIntent);

        } catch (android.content.ActivityNotFoundException ex) {
            switch (CustomFixSQL.getInt(Constants.PACKAGE_DEFAULT)){
                case 1:
                    Util.showToast("Please Install Zalo");
                    break;

                case 2:
                    Util.showToast("Please Install Viber");
                    break;

                case 3:
                    Util.showToast("Please Install Facebook Messenger");
                    break;
            }
        }

    }

    public static void openGoogleMapRoute(double latitude, double longitude) {
        String uriBegin = String.format("google.navigation:q=%s,%s", String.valueOf(latitude), String.valueOf(longitude));
        Uri gmmIntentUri = Uri.parse(uriBegin);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(Util.getInstance().getCurrentActivity().getPackageManager()) != null) {
            Util.getInstance().getCurrentActivity().startActivity(mapIntent);

        } else {
            Util.showToast("Couldn't open map!");
        }

    }



    public static void startImageChooser(Fragment fragment, CallbackUri callbackuri) {
        Uri tempUri = Uri.fromFile(Util.createTempFileOutput("LOCAL", "Select"));
        callbackuri.uriRespone(tempUri);
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (fragment != null){
            fragment.startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);

        }else {
            context.startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);

        }


    }

    public static void startCamera(Fragment fragment, CallbackUri callbackuri) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
//            File photoFile = null;
//            photoFile = Util.createCustomImageFile();
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }

        Activity context = Util.getInstance().getCurrentActivity();
        Uri photoURI = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                Util.createTempFileOutput("CAMERA", "Camera"));
        callbackuri.uriRespone(photoURI);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        //context.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        if (fragment != null){
            fragment.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        }else {
            context.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        }

    }

//    public static void startCamera(Fragment fragment, CallbackUri callbackuri) {
//        Activity context = Util.getInstance().getCurrentActivity();
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
//            File photoFile = null;
//            photoFile = Util.createCustomImageFile();
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                callbackuri.uriRespone(photoURI);
//                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//
//    }


    @SuppressLint("WrongConstant")
    public static void openCallScreen(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(phone)));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (PermissionChecker.checkSelfPermission(Util.getInstance().getCurrentActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Util.getInstance().getCurrentActivity().startActivity(callIntent);


    }


}