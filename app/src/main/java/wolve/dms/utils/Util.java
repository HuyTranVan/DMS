package wolve.dms.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.leolin.shortcutbadger.ShortcutBadger;
import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.activity.MapsActivity;
import wolve.dms.activity.StatisticalBillsFragment;
import wolve.dms.activity.StatisticalDashboardFragment;
import wolve.dms.activity.StatisticalProductFragment;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
import wolve.dms.models.Province;
import wolve.dms.models.Status;
import wolve.dms.models.User;

public class Util {
    private static Util util;
    private Activity currentActivity;
    private KProgressHUD cDialog;
    private KProgressHUD cDialogLocation;
    public Location currentLocation;
    public static MapsActivity mapsActivity;
    public static StatisticalDashboardFragment dashboardFragment;
    public static StatisticalBillsFragment billsFragment;
    public static StatisticalProductFragment productFragment;



    public static ArrayList<Status> mListStatus;
    public static ArrayList<Province> mListProvinces;
    public static ArrayList<String> mListDistricts;
    public static ArrayList<ProductGroup> mListProductGroup;
    public static ArrayList<Product> mListProduct;
    public static ArrayList<Product> mListPromotion;
    private DisplayMetrics windowSize;
    private static int PLAY_SERVICES_RESOLUTION_REQUEST = 1462;

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public static final Pattern DETECT_PHONE = Pattern.compile("(\\+84|0)\\d{9,10}");

    public static synchronized Util getInstance() {
        if (util == null)
            util = new Util();

        return util;
    }

    public void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public String formatDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    public static void showToast(String message){
        Toast.makeText(Util.getInstance().getCurrentActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showLoading() {
        showLoading("Đang xử lý...");
    }

    public void showLoading(final String message) {
        if (cDialog == null || !cDialog.isShowing()  ) {

            cDialog = KProgressHUD.create(getCurrentActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setDetailsLabel(message)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setBackgroundColor(Color.parseColor("#40000000"))
                    .setDimAmount(0.5f)
                    .show();
        }
    }

    public void showLocationLoading(final String message) {
        if (cDialogLocation == null) {
            cDialogLocation = KProgressHUD.create(getCurrentActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setDetailsLabel(message)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setBackgroundColor(Color.parseColor("#40000000"))
                    .setDimAmount(0.5f)
                    .show();
        }
    }

    public void stopLocationLoading() {
        if (cDialogLocation != null && cDialogLocation.isShowing()) {
            cDialogLocation.dismiss();
            cDialogLocation = null;
        }
    }

    public void stopLoading(Boolean stop) {
        if (stop){
            if (cDialog != null && cDialog.isShowing() || cDialog != null) {
                cDialog.dismiss();
                cDialog = null;
            }
        }

    }

    public static boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(Util.getInstance().getCurrentActivity());
        if (result != 0) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(Util.getInstance().getCurrentActivity(), result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

    public static void quickMessage(String content, String actionlabel, ActionClickListener actionListener) {
        Snackbar snb = Snackbar.with(Util.getInstance().getCurrentActivity().getApplicationContext())
                .type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
                .text(content) // text to be displayed
                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT);
        if (actionlabel != null) {
            snb.actionLabel(actionlabel).actionListener(actionListener);
        }

        SnackbarManager.show(snb, Util.getInstance().getCurrentActivity()); // where it is displayed
    }

    public static void setStore(String title, int nValue) {
        CustomSharedPrefer customSharedPrefer = new CustomSharedPrefer(Util.getInstance().getCurrentActivity());
        customSharedPrefer.setInt(title, nValue);
    }

    public static int getStore(String title) {
        CustomSharedPrefer customSharedPrefer = new CustomSharedPrefer(Util.getInstance().getCurrentActivity());
        return customSharedPrefer.getInt(title);
    }

    public static void setBadge(int nBadge) {
        try {
            ShortcutBadger.applyCount(Util.getInstance().getCurrentActivity(), nBadge); //for 1.1.4+
            // Badges.setBadge(Util.getInstance().getCurrentActivity(), nBadge);
        } catch (Exception e) {
            Log.e(Constants.DMS_LOGS, e.getMessage());
        }
        setStore(Constants.BADGE, nBadge);
    }

    public static String AssetJSONFile(Context context, String filename) {
        String json = null;
        try {

            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static boolean checkInternetConnection() {
        if (!isInternetAvailable()) {
            Util.quickMessage("Không có kết nối.", null, null);
        }

        return isInternetAvailable();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("http://propzy.vn");

            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

    public static DisplayMetrics getWindowSize() {
        DisplayMetrics display = getInstance().windowSize;
        if (display == null) {
            display = new DisplayMetrics();
            getInstance().getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
            getInstance().setWindowSize(display);
        }
        return getInstance().windowSize;
    }

    public static void changeFragmentHeight(Fragment fragment, int height) {
        ViewGroup.LayoutParams params = fragment.getView().getLayoutParams();
        params.height = height;
        fragment.getView().setLayoutParams(params);
    }

    public static float convertDp2Px(int dp) {
        Resources r = getInstance().getCurrentActivity().getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public void setWindowSize(DisplayMetrics windowSize) {
        this.windowSize = windowSize;
    }

    public static String formatTime(long seconds) {
        int minutes = (int) Math.ceil(seconds / 60.0);
        long hours = minutes / 60;
        minutes = minutes % 60;

        String returnStr = "";
        if (hours > 0) returnStr = hours + " giờ ";
        if (minutes > 0) returnStr += minutes + " phút";
        return returnStr;
    }

    public static String shortFormatTime(long seconds) {
        int minutes = (int) Math.ceil(seconds / 60.0);
        long hours = minutes / 60;
        minutes = minutes % 60;

        String returnStr = "";
        if (minutes > 0) returnStr += hours + ":" + minutes;
        return returnStr;
    }

    public static String formatDistance(long distance) {
        if (distance >= 1000) {
            return (distance / 1000.0) + " km";
        } else {
            return distance + " m";
        }
    }

    public static View.OnFocusChangeListener editTextFocusListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final InputMethodManager imm = (InputMethodManager) getInstance().getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        };
    }

    public static String format(long timeStamp) {
        return format(timeStamp, "dd-mm-yyyy");
    }

    public static String format(long timeStamp, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String dateStr = dateFormat.format(new Date(timeStamp));
        return dateStr;
    }

    public void showImage(String url) {
        Uri imageUri = Uri.parse(url);
        final Dialog builder = new Dialog(getCurrentActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(getCurrentActivity());
        //imageView.setImageURI(imageUri);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

    public static void showKeyboard(Context context, final EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showKeyboard(View view) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
            view.clearFocus();
        }
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) Util.getInstance().getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static void clearCacheThumbnail() {
        String cachePath = getCacheThumbnail();
        File cacheDir = new File(cachePath);
        deleteDir(cacheDir);
        cacheDir.delete();
    }
    public static String getCacheThumbnail() {
        String directory =  getInstance().currentActivity.getCacheDir() + "/thumbnail/";
        File directoryCheck = new File(directory);

        if(!directoryCheck.exists())
            directoryCheck.mkdirs();
        return directory;
    }
    public static String getThumbnail(String imageUrl) {
        if(imageUrl.startsWith("http")) {
            return imageUrl;
        }
        File bitmapFile = new File(imageUrl);
        String fileName = bitmapFile.getName();
        String cacheFilename = getCacheThumbnail() + fileName;
        File croppedFileThumbnail = new File(cacheFilename);
        if(croppedFileThumbnail.exists()) {
            return getCacheThumbnail() + fileName;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);

        final int IMAGE_SIZE = 500;
        boolean landscape = bitmap.getWidth() > bitmap.getHeight();

        float scale_factor;
        if (landscape) scale_factor = (float)IMAGE_SIZE / bitmap.getHeight();
        else scale_factor = (float)IMAGE_SIZE / bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale_factor, scale_factor);

        Bitmap croppedBitmap;
        if (landscape){
            int start = (bitmap.getWidth() - bitmap.getHeight()) / 2;
            croppedBitmap = Bitmap.createBitmap(bitmap, start, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
        } else {
            int start = (bitmap.getHeight() - bitmap.getWidth()) / 2;
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, start, bitmap.getWidth(), bitmap.getWidth(), matrix, true);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(cacheFilename);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cacheFilename;
    }

    public static ArrayMap<String, Object> JSON2ArrayMap(JSONObject jObject) {
        Iterator<?> keys = jObject.keys();
        ArrayMap<String, Object> jMap =  new ArrayMap<>();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            try {
                jMap.put(key, jObject.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jMap;
    }

    public static Location readGeoTagImage(String imagePath)
    {
        Location loc = new Location("");
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            float [] latlong = new float[2] ;
            if(exif.getLatLong(latlong)){
                loc.setLatitude(latlong[0]);
                loc.setLongitude(latlong[1]);
            }
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            loc.setTime(fmt_Exif.parse(date).getTime());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return loc;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static ArrayList<String> getAllShownImagesPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();

        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = Util.getInstance().getCurrentActivity().getContentResolver().query(uri, projection, null, null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            if(absolutePathOfImage.contains(".png") | absolutePathOfImage.contains(".PNG")
                    | absolutePathOfImage.contains(".jpg") | absolutePathOfImage.contains(".JPG")
                    | absolutePathOfImage.contains(".jpeg") | absolutePathOfImage.contains(".JPEG")){


                listOfAllImages.add(absolutePathOfImage);
            }

        }

//        ArrayList<String> tempArrayString = new ArrayList<String>(listOfAllImages);
        Collections.reverse(listOfAllImages);

        return listOfAllImages;
    }

    //using parse for local category
    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.IMAGES_DIRECTORY
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpeg");
    }

    public static String CurrentTimeStampString(){
        SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        long unixtime = 0;
        try
        {
            unixtime = dfm.parse(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())).getTime();
            unixtime=unixtime/1000;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return String.valueOf(unixtime);

    }

    public static long CurrentTimeStamp(){
        SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        long unixtime = 0;
        try
        {
            unixtime = dfm.parse(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime())).getTime();
            //unixtime=unixtime;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return unixtime;

    }

    public static long TimeStamp1(String ddMMyyyy){

        SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy");

        long unixtime = 0;
        try
        {
            unixtime = dfm.parse(ddMMyyyy).getTime();
            unixtime=unixtime;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return unixtime;

    }

    public static String DateString(long timestamp){
        String date ="";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;

    }

    public static String DateHourString(long timestamp){
        String date ="";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("dd-MM-yyyy (HH:mm)", cal).toString();

        return date;

    }

    public static String DateMonthString(long timestamp){
        String date ="";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("dd/MM", cal).toString();

        return date;

    }

    public static String YearString(long timestamp){
        String date ="";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("yyyy", cal).toString();

        return date;

    }

    public static String HourString(long timestamp){
        String date ="";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("HH:mm", cal).toString();

        return date;

    }

    public static String formatTimeDate(long timestamp){
        String date ="";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("HH:mm - dd/MM/yyyy", cal).toString();

        return date;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static String getRealPathFromURI(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = Util.getInstance().getCurrentActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public static ArrayList<JSONObject> sortTimeSchedule(ArrayList<JSONObject> data){
        ArrayList<JSONObject> listReult = new ArrayList<>();
        long currentTime = CurrentTimeStamp();
        try {
            for (int i=0 ; i<data.size(); i++){
                if (data.get(i).getLong("scheduleTime") > currentTime){
                    listReult.add(0,data.get(i));
                }else {
                    listReult.add(listReult.size(),data.get(i));
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listReult;
    }

    public static String encodeString(String value){
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value,"utf-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
        return encoded;
    }

    public static String FormatMoney(Double money){
        String moneyFormated="";

        moneyFormated = NumberFormat.getNumberInstance(Locale.US).format(money).replace(",",".");

//        if(money != null){
//            String mo = money.replace(",","");
//            if (money.equals("") | money.equals("null") | money.equals("0")){
//                moneyFormated = "0";
//            }else if(money.contains(".")) {
//
//                int i = Math.round(Float.parseFloat(mo));
//                moneyFormated = NumberFormat.getNumberInstance(Locale.US).format(i).replace(",",".");
//
//            }else {
//                moneyFormated = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(Quantity.Show(mo))).replace(",",".");
//            }
//        }else {
//            moneyFormated = "0";
//        }

        return moneyFormated;
    }

    public static ArrayList<String> arrayToList(String[] array){
        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<array.length; i++){
            list.add(array[i]);
        }

        return list;
    }

    public static void setStatusList(JSONArray result){
        mListStatus = new ArrayList<>();
        try {
            for (int i=0; i<result.length(); i++){
                Status status = new Status(result.getJSONObject(i));
                mListStatus.add(status);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setProvincesList(JSONArray result){
        mListProvinces = new ArrayList<>();
        try {
            for (int i=0; i<result.length(); i++){
                Province province = new Province(result.getJSONObject(i));
                mListProvinces.add(province);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setDistrictList(JSONArray result){
        mListDistricts = new ArrayList<>();
        mListDistricts.add(0, "Chọn quận");
        try {
            for (int i=0; i<result.length(); i++){
                JSONObject object = result.getJSONObject(i);
                if (!object.getString("name").contains(" ")){
                    mListDistricts.add(object.getString("type") + " " + object.getString("name"));
                }else {
                    mListDistricts.add(object.getString("name"));
                }
            }

            //Collections.sort(mListDistricts, String.CASE_INSENSITIVE_ORDER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setProductGroupList(JSONArray result){
        mListProductGroup = new ArrayList<>();
        try {
            for (int i=0; i<result.length(); i++){
                ProductGroup productGroup = new ProductGroup(result.getJSONObject(i));
                mListProductGroup.add(productGroup);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setProductList(JSONArray result){
        mListProduct = new ArrayList<>();
        try {
            for (int i=0; i<result.length(); i++){
                Product product = new Product(result.getJSONObject(i));
//                product.put("totalMoney", product.getDouble("unitPrice"));
//                product.put("quantity", 1);
//                product.put("discount", 0);
//                product.put("checked", false);
                mListProduct.add(product);
            }
            setPromotionList(mListProduct);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setPromotionList(ArrayList<Product> listProducts){
        mListPromotion = new ArrayList<>();
        for (int i=0; i<listProducts.size(); i++){
            if (listProducts.get(i).getBoolean("promotion")){
                mListPromotion.add(listProducts.get(i));
            }
        }
    }

    public static Double valueMoney(String money){
        return Double.parseDouble(money.replace(".",""));
    }


}
