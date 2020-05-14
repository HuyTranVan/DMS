package wolve.dms.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.google.api.client.auth.oauth.OAuthCallbackUrl;

import java.io.File;
import java.util.List;

import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.activities.CustomerActivity;
import wolve.dms.activities.WarehouseActivity;
import wolve.dms.activities.DistributorActivity;
import wolve.dms.activities.HomeActivity;
import wolve.dms.activities.ImportActivity;
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
import wolve.dms.activities.TestActivity;
import wolve.dms.activities.UserActivity;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackUri;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;

import static wolve.dms.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static wolve.dms.utils.Constants.REQUEST_IMAGE_CAPTURE;


public class Transaction {

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

    public static void gotoImportActivity(BaseModel curentwarehouse, boolean flag) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, ImportActivity.class);
        intent.putExtra(Constants.WAREHOUSE, curentwarehouse.BaseModelstoString());
        intent.putExtra(Constants.FLAG, flag);
        context.startActivityForResult(intent, Constants.RESULT_IMPORT_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


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

    public static void gotoUserActivity(boolean userdetail) {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(Constants.FLAG, userdetail);
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

    public static void gotoCustomerActivity() {
        if (User.getCurrentRoleId() != Constants.ROLE_WAREHOUSE) {
            Activity context = Util.getInstance().getCurrentActivity();
            Intent intent = new Intent(context, CustomerActivity.class);
            CustomSQL.setLong(Constants.CHECKIN_TIME, Util.CurrentTimeStamp());
            context.startActivityForResult(intent, Constants.RESULT_CUSTOMER_ACTIVITY);
            context.overridePendingTransition(R.anim.slide_up, R.anim.nothing);

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

    public static void checkInventoryBeforePrintBill(BaseModel bill, List<BaseModel> listproduct, int warehouse_id){
        DataUtil.checkInventory(listproduct, warehouse_id, new CallbackListObject() {
            @Override
            public void onResponse(List<BaseModel> list) {
                if (list.size() >0){
                    CustomCenterDialog.showListProductWithDifferenceQuantity( User.getCurrentUser().getBaseModel("warehouse").getString("name") +": KHÔNG ĐỦ TỒN KHO ",
                            list,
                            new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                CustomSQL.setListBaseModel(Constants.PRODUCT_SUGGEST_LIST, listproduct);
                                gotoImportActivity(User.getCurrentUser().getBaseModel("warehouse"), true);

                            }

                        }
                    });

                }else {
                    gotoPrintBillActivity(bill, false);
                }
            }
        });

    }

    public static void gotoPrintBillActivity(BaseModel bill, Boolean rePrint) {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, PrintBillActivity.class);
        intent.putExtra(Constants.RE_PRINT, rePrint);
        intent.putExtra(Constants.BILL, bill.BaseModelstoString());
        context.startActivityForResult(intent, Constants.RESULT_PRINTBILL_ACTIVITY);
        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void gotoStatisticalActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, StatisticalActivity.class);
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

    public static void gotoStatisticalCustomerActivity() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, StatisticalCustomerActivity.class);
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

    public static void sendEmailReport(String content) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse(String.format("mailto:%s?subject=%s&body=%s",
                Constants.DMS_EMAIL,
                "DMS Error Report!!",
                content));
        intent.setData(data);
        Util.getInstance().getCurrentActivity().startActivity(intent);

    }

    public static void shareViaZalo(String content) {
        ShareCompat.IntentBuilder.from(Util.getInstance().getCurrentActivity())
                .setType("text/plain")
                .setChooserTitle("Chia sẻ thông qua")
                .setText(content)
                .startChooser();

    }

    public static void shareImageViaZalo(Uri uri) {
//        File file = new File(path);
//        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        Util.getInstance().getCurrentActivity().startActivity(Intent.createChooser(intent, "Chia sẻ thông qua") );

    }

    public static void openGoogleMapRoute(double latitude, double longitude) {
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

        if (mapIntent.resolveActivity(Util.getInstance().getCurrentActivity().getPackageManager()) != null) {
            Util.getInstance().getCurrentActivity().startActivity(mapIntent);

        } else {
            Util.showToast("Couldn't open map!");
        }

    }

    public static void gotoTestActivity() {
        Context context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(context, TestActivity.class);
        context.startActivity(intent);
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ((AppCompatActivity) context).finish();
    }

    public static void startImageChooser() {
        Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        context.startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);

    }

    public static void startImageChooser(Fragment fragment) {
        //Activity context = Util.getInstance().getCurrentActivity();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);

    }

    public static void startCamera() {
        Activity context = Util.getInstance().getCurrentActivity();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            photoFile = Util.createCustomImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    public static void startCamera(Fragment fragment, CallbackUri callbackUrl) {
        Activity context = Util.getInstance().getCurrentActivity();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            photoFile = Util.createCustomImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                callbackUrl.uriRespone(photoURI);
                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }



    @SuppressLint("WrongConstant")
    public static void openCallScreen(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(phone)));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (PermissionChecker.checkSelfPermission(Util.getInstance().getCurrentActivity(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {
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