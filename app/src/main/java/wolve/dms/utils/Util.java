package wolve.dms.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wolve.dms.R;
import wolve.dms.activities.MapsActivity;
import wolve.dms.activities.ShopCartActivity;
import wolve.dms.activities.StatisticalBillsFragment;
import wolve.dms.activities.StatisticalPaymentFragment;
import wolve.dms.activities.StatisticalCheckinFragment;
import wolve.dms.activities.StatisticalDebtFragment;
import wolve.dms.activities.StatisticalDashboardFragment;
import wolve.dms.activities.StatisticalProductFragment;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackString;
import wolve.dms.customviews.CInputForm;
import wolve.dms.libraries.ItemDecorationGridSpace;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.Product;
import wolve.dms.models.Province;
import wolve.dms.models.User;

import static wolve.dms.utils.Constants.REQUEST_GOOGLE_PLAY_SERVICES;

public class Util {
    private static Util util;
    private Activity currentActivity;
    private KProgressHUD cDialog;
    private KProgressHUD cDialogLocation;
    public Location currentLocation;
    public static MapsActivity mapsActivity;
    public static ShopCartActivity shopCartActivity;
    public static StatisticalDashboardFragment dashboardFragment;
    public static StatisticalBillsFragment billsFragment;
    public static StatisticalProductFragment productFragment;
    public static StatisticalCheckinFragment checkinFragment;
    public static StatisticalPaymentFragment paymentFragment;
    public static StatisticalDebtFragment debtFragment;

    public static ArrayList<Province> mListProvinces;
    private DisplayMetrics windowSize;
    private static int PLAY_SERVICES_RESOLUTION_REQUEST = 1462;
    public static Toast currentToast;


    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

//    public static final Pattern DETECT_PHONE = Pattern.compile("(\\+84|0)\\d{9,10}");
    public static final String DETECT_PHONE = "(\\+84|0)\\d{9,10}";
    public static final String DETECT_NUMBER = "^[0-9]*$";

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

    public boolean isLoading() {
        if (cDialog != null && cDialog.isShowing())
            return true;
        return false;
    }

    public void showLoading() {
        showLoading("Đang xử lý...");
    }

    public void showLoading(boolean show) {
        if (show){
            showLoading("Đang xử lý...");
        }

    }

    public void showLoading(final String message) {
        if (cDialog == null || !cDialog.isShowing()) {

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

    public void stopLoading(Boolean stop) {
        if (stop) {
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

    public static void showSnackbar(String content, String actionlabel, ActionClickListener actionListener) {
        Snackbar snb = Snackbar.with(Util.getInstance().getCurrentActivity().getApplicationContext())
                .type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
                .text(content) // text to be displayed
                .actionColor(Color.parseColor("#2196f3"))
                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT);
        if (actionlabel != null) {
            snb.actionLabel(actionlabel).actionListener(actionListener);
        }

        SnackbarManager.show(snb, Util.getInstance().getCurrentActivity()); // where it is displayed
    }

    public static void showSnackbarError(String content) {
        Snackbar snb = Snackbar.with(Util.getInstance().getCurrentActivity().getApplicationContext())
                .type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
                .text(content) // text to be displayed
                .actionColor(Color.parseColor("#2196f3"))
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .actionLabel("REPORT")
                .actionColor(Util.getInstance().getCurrentActivity().getResources().getColor(R.color.colorBlue))
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        Log.e("click", "yes");

                        Transaction.sendEmailReport(String.format("User: %s\nPhone: %s\nCurrentTime: %s\n\nContent: %s",
                                User.getFullName(),
                                User.getPhone(),
                                CurrentMonthYearHour(),
                                content));

                    }
                });

        SnackbarManager.show(snb, Util.getInstance().getCurrentActivity()); // where it is displayed
    }



    public static void showSnackbar(String content, String actionlabel, boolean isIndefinite, ActionClickListener actionListener) {
        Snackbar snb = Snackbar.with(Util.getInstance().getCurrentActivity().getApplicationContext())
                .type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
                .text(content) // text to be displayed
                .actionColor(Color.parseColor("#2196f3"))
                .duration(isIndefinite ? Snackbar.SnackbarDuration.LENGTH_INDEFINITE : Snackbar.SnackbarDuration.LENGTH_SHORT);
        if (actionlabel != null) {
            snb.actionLabel(actionlabel).actionListener(actionListener);
        }


        SnackbarManager.show(snb, Util.getInstance().getCurrentActivity()); // where it is displayed
    }

    public static void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(Util.getInstance().getCurrentActivity(), message, Toast.LENGTH_SHORT);
        currentToast.setGravity(Gravity.BOTTOM, 0, 40);
        currentToast.show();
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
            Util.showSnackbar("Không có kết nối.", null, null);
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

    public static int convertPx2Dp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int convertDp2PxInt(int dp) {
        Resources r = getInstance().getCurrentActivity().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
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

    public static void showKeyboardDelay(final View view){
        new Handler().postDelayed (new Runnable() {
            @Override
            public void run() {
                Util.showKeyboard(view);
            }}, 500);
    }

    public static void showKeyboardEditTextDelay(EditText view){
        new Handler().postDelayed (new Runnable() {
            @Override
            public void run() {
                Util.showKeyboard(view);
                view.setSelection(view.getText().toString().trim().length());
            }}, 500);
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
        String directory = getInstance().currentActivity.getCacheDir() + "/thumbnail/";
        File directoryCheck = new File(directory);

        if (!directoryCheck.exists())
            directoryCheck.mkdirs();
        return directory;
    }

    public static String getThumbnail(String imageUrl) {
        if (imageUrl.startsWith("http")) {
            return imageUrl;
        }
        File bitmapFile = new File(imageUrl);
        String fileName = bitmapFile.getName();
        String cacheFilename = getCacheThumbnail() + fileName;
        File croppedFileThumbnail = new File(cacheFilename);
        if (croppedFileThumbnail.exists()) {
            return getCacheThumbnail() + fileName;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);

        final int IMAGE_SIZE = 500;
        boolean landscape = bitmap.getWidth() > bitmap.getHeight();

        float scale_factor;
        if (landscape) scale_factor = (float) IMAGE_SIZE / bitmap.getHeight();
        else scale_factor = (float) IMAGE_SIZE / bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale_factor, scale_factor);

        Bitmap croppedBitmap;
        if (landscape) {
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
        ArrayMap<String, Object> jMap = new ArrayMap<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                jMap.put(key, jObject.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jMap;
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
            if (absolutePathOfImage.contains(".png") | absolutePathOfImage.contains(".PNG")
                    | absolutePathOfImage.contains(".jpg") | absolutePathOfImage.contains(".JPG")
                    | absolutePathOfImage.contains(".jpeg") | absolutePathOfImage.contains(".JPEG")) {


                listOfAllImages.add(absolutePathOfImage);
            }

        }

//        ArrayList<String> tempArrayString = new ArrayList<String>(listOfAllImages);
        Collections.reverse(listOfAllImages);

        return listOfAllImages;
    }

    public static File createCustomFolder(String name) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Constants.APP_DIRECTORY);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator + name);
    }

    public static File createCustomImageFile(String name) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Constants.APP_DIRECTORY);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File imageFile = new File(mediaStorageDir.getPath() + File.separator + "Images");
        if (!imageFile.exists()) {
            if (!imageFile.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        return new File(imageFile.getPath() + File.separator + String.format("IMG_%s_%s.png", name, timeStamp));
    }

    //using parse for local category
    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.IMAGES_DIRECTORY
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpeg");
    }

    public static File getOutputMediaFile(String name) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), name);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath());
    }

    public static File getOutputMediaFileLogo() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.IMAGES_DIRECTORY);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        CustomSQL.setString("logo", mediaStorageDir.getPath() + File.separator + "logo.jpeg");

        return new File(mediaStorageDir.getPath() + File.separator + "logo.jpeg");
    }

    public static String CurrentTimeStampString() {
        SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        long unixtime = 0;
        try {
            unixtime = dfm.parse(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime())).getTime();
            unixtime = unixtime / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(unixtime);

    }

    public static long CurrentTimeStamp() {
        SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        long unixtime = 0;
        try {
            unixtime = dfm.parse(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime())).getTime();
            //unixtime=unixtime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixtime;
    }

    public static String CurrentMonthYear() {
        return new SimpleDateFormat("MM-yyyy").format(Calendar.getInstance().getTime());
    }

    public static String Current01MonthYear() {
        return new SimpleDateFormat("01-MM-yyyy").format(Calendar.getInstance().getTime());
    }

    public static String Next01MonthYear() {
        int mMonth = Calendar.getInstance().get(Calendar.MONTH) + 2;
        int mYear = Calendar.getInstance().get(Calendar.YEAR);

        String month;
        String year ;

        if (mMonth <10){
            month = "0"+ String.valueOf(mMonth);
            year = String.valueOf(mYear);
        }else if (mMonth>12){
            month = "01";
            year = String.valueOf(mYear +1);
        }else {
            month = String.valueOf(mMonth);
            year = String.valueOf(mYear);
        }



        return String.format("01-%s-%s",month, year);
        //return new SimpleDateFormat(monthFormat).format(Calendar.getInstance().getTime());
    }

    public static String CurrentDayMonthYear() {
        return new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    }

    public static String CurrentMonthYearHour() {
        return new SimpleDateFormat("dd-MM-yyyy  HH:mm").format(Calendar.getInstance().getTime());
    }

    public static String CurrentMonthYearHourNotBlank() {
        return new SimpleDateFormat("dd_MM_yyyy_HH_mm").format(Calendar.getInstance().getTime());
    }


    public static long TimeStamp1(String ddMMyyyy) {

        SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy");

        long unixtime = 0;
        try {
            unixtime = dfm.parse(ddMMyyyy).getTime();
            unixtime = unixtime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixtime;

    }

    public static long TimeStamp2(String ddMMyyyyHHmmss) {

        SimpleDateFormat dfm = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        long unixtime = 0;
        try {
            unixtime = dfm.parse(ddMMyyyyHHmmss).getTime();
            unixtime = unixtime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixtime;

    }

    public static String DateString(long timestamp) {
        String date = "";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("dd-MM-yyyy", cal).toString();

        return date;

    }

    public static String DateHourString(long timestamp) {
        String date = "";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("dd-MM-yyyy (HH:mm)", cal).toString();

        return date;

    }

    public static String DateMonthString(long timestamp) {
        String date = "";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("dd/MM", cal).toString();

        return date;

    }

    public static String DateMonthYearString(long timestamp) {
        String date = "";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("dd/MM/yy", cal).toString();

        return date;

    }

    public static String YearString(long timestamp) {
        String date = "";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("yyyy", cal).toString();

        return date;

    }

    public static int YearInt(String year) {
        SimpleDateFormat dfm = new SimpleDateFormat("yyyy");

        long unixtime = 0;
        try {
            unixtime = dfm.parse(year).getTime();
            unixtime = unixtime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) unixtime;

    }

    public static String HourString(long timestamp) {
        String date = "";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("HH:mm", new Date(timestamp)).toString();

        return date;

    }

    public static String MinuteString(long timestamp) {
        String date = "";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
        date = DateFormat.format("mm:ss", new Date(timestamp)).toString();

        return date;

    }

    public static int countDay(long timestamp){
        long time = CurrentTimeStamp() - timestamp;

        return (int) (time/(1000*24*60*60));
    }

    public static String HourStringNatural(long timestamp) {
        String date = "";

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        //cal.setTimeInMillis(timestamp*1000);
        cal.setTimeInMillis(timestamp);
//        String hour = Integer.parseInt(DateFormat.format("HH", new Date(timestamp)).toString()) ==0 ?
//                "" : String.format("%dh", Integer.parseInt(DateFormat.format("HH", new Date(timestamp)).toString()));
        String minute = Integer.parseInt(DateFormat.format("mm", new Date(timestamp)).toString()) ==0 ?
                "" : String.format("%dp", Integer.parseInt(DateFormat.format("mm", new Date(timestamp)).toString()));
        String second =  String.format("%ds", Integer.parseInt(DateFormat.format("ss", new Date(timestamp)).toString()));

        return String.format("%s%s",  minute, second);

    }

    public static String formatTimeDate(long timestamp) {
        String date = "";

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
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public static String encodeString(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
        return encoded;
    }

    public static String decodeString(String value) {
        String encoded = null;
        try {
            encoded = URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
        return encoded;
    }

    public static String FormatMoney(Double money) {
        String moneyFormated = "";

        moneyFormated = NumberFormat.getNumberInstance(Locale.US).format(money).replace(",", ".");

        return moneyFormated;
    }

    public static ArrayList<String> arrayToList(String[] array) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }

        return list;
    }

    public static void setProvincesList(JSONArray result) {
        mListProvinces = new ArrayList<>();
        try {
            for (int i = 0; i < result.length(); i++) {
                Province province = new Province(result.getJSONObject(i));
                mListProvinces.add(province);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Double valueMoney(String money) {
        return Double.parseDouble(money.replaceAll(",|\\s|\\.", ""));
    }

    public static Double valueMoney(EditText edText) {
        if (edText.getText().toString().equals("0") || edText.getText().toString().equals(""))
            return 0.0;
        return Double.parseDouble(edText.getText().toString().trim().replaceAll(",|\\s|\\.", ""));
    }

    public static String valueMoneyString(EditText edText) {
        if (edText.getText().toString().equals("0") || edText.getText().toString().equals(""))
            return "0";
        return edText.getText().toString().trim().replaceAll(",|\\s|\\.", "");
    }

    public static Double valueMoney(TextView edText) {
        if (edText.getText().toString().equals("0") || edText.getText().toString().equals(""))
            return 0.0;
        return Double.parseDouble(edText.getText().toString().trim().replaceAll(",|\\s|\\.", ""));
    }

    public static Double getTotalMoney(List<BaseModel> list) {
        Double money = 0.0;
        for (int i = 0; i < list.size(); i++) {
            money += list.get(i).getDouble("total");
        }
        return money;
    }

    public static Double getTotalMoney1(List<JSONObject> list) {
        Double money = 0.0;
        try {
            for (int i = 0; i < list.size(); i++) {
                money += list.get(i).getDouble("total");

            }
        } catch (JSONException e) {
            return money;
        }
        return money;
    }

    public static Double getTotalPaid(List<Bill> list) {
        Double money = 0.0;
        for (int i = 0; i < list.size(); i++) {
            money += list.get(i).getDouble("paid");
        }
        return money;
    }

    public static double getTotalDebt(List<BaseModel> list) {
        Double money = 0.0;
        for (int i = 0; i < list.size(); i++) {
            money += list.get(i).getDouble("debt");
        }
        return money;
    }

    public static BaseModel getTotal(List<BaseModel> list) {
        BaseModel object = new BaseModel();
        Double total = 0.0;
        Double paid = 0.0;
        Double debt =0.0;
        for (int i = 0; i < list.size(); i++) {
            if (!Util.isBillReturn(list.get(i))){
                total += list.get(i).getDouble("total");
                paid += list.get(i).getDouble("paid");
                debt += list.get(i).getDouble("debt");
            }
        }

        object.put("total", total);
        object.put("paid", paid);
        object.put("debt", debt);
        object.put("size", list.size());

        return object;
    }

    public static JSONObject getTotal(JSONArray array) {
        JSONObject object = new JSONObject();
        Double total = 0.0;
        Double paid = 0.0;
        Double debt =0.0;
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                total += ob.getDouble("total");
                paid += ob.getDouble("paid");
                debt += ob.getDouble("debt");
            }

            object.put("total", total);
            object.put("paid", paid);
            object.put("debt", debt);
            object.put("size", array.length());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static Double getTotalProfit(List<Bill> list) {
        Double profit = 0.0;
        try {
            for (int i = 0; i < list.size(); i++) {
                JSONArray arrayDetail = list.get(i).getJSONArray("billDetails");
                if (arrayDetail.length() > 0) {
                    for (int j = 0; j < arrayDetail.length(); j++) {
                        JSONObject objectDetail = arrayDetail.getJSONObject(j);
                        profit += (objectDetail.getDouble("unitPrice") - objectDetail.getDouble("discount") - objectDetail.getDouble("purchasePrice"))
                                * objectDetail.getDouble("quantity");
                    }
                }
            }
        } catch (JSONException e) {
            return profit;
        }
        return profit;
    }

    public static double getProfit(BaseModel bill) {
        Double profit = 0.0;
        List<BaseModel> billdetails = DataUtil.array2ListBaseModel(bill.getJSONArray("billDetails"));
        for (int i = 0; i < billdetails.size(); i++) {
            profit += (billdetails.get(i).getDouble("unitPrice") - billdetails.get(i).getDouble("discount") - billdetails.get(i).getDouble("purchasePrice"))
                    * billdetails.get(i).getDouble("quantity");
        }


        return profit;
    }

    public static double getProfitByPayment(BaseModel bill, double paid) {
        Double profit = 0.0;
        List<BaseModel> billdetails = DataUtil.array2ListBaseModel(bill.getJSONArray("billDetails"));
        for (int i = 0; i < billdetails.size(); i++) {
            profit += (billdetails.get(i).getDouble("unitPrice") - billdetails.get(i).getDouble("discount") - billdetails.get(i).getDouble("purchasePrice"))
                    * billdetails.get(i).getDouble("quantity");
        }

        double va =(paid * profit /  bill.getDouble("total"));

        return Math.round(va);
    }


    public static boolean isBillReturn(BaseModel bill){
        boolean id = false;
        if (!bill.getString("note").equals("") && bill.getString("note").matches(Util.DETECT_NUMBER)){
            id = true;
        }

        return id;
    }

    public static Double getTotalMoneyProduct(List<Product> list) {
        Double sum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).getDouble("totalMoney");
        }
        return sum;
    }

    public static class CurrencyUtil {

        public static String format(double v, String unit) {
            return String.format(Locale.getDefault(), "%,.0f" + " " + unit, v).replace(",", ".");
        }

        public static String format(double v) {
            return String.format(Locale.getDefault(), "%,.0f", v).replace(",", ".");
        }

        public static String format(long v) {
            return String.format(Locale.getDefault(), "%,d", v).replace(",", ".");
        }

        public static String format(String v) {
            return String.format(Locale.getDefault(), "%,d", Long.valueOf(v)).replace(",", ".");
        }

        public static String convertDecimalToString(String price) {
            return convertDecimalToString(BigDecimal.valueOf(Double.valueOf(price)));
        }

        public static String convertDecimalToString(BigDecimal price) {
            DecimalFormatSymbols fsymbol = new DecimalFormatSymbols();
            fsymbol.setDecimalSeparator(',');
            fsymbol.setGroupingSeparator('.');
            DecimalFormat dmf = new DecimalFormat("#,#.#", fsymbol);
            dmf.setGroupingSize(3);
            return dmf.format(price);
        }
    }

    public static Boolean checkImageNull(String imglink) {
        Boolean check = true;
        if (imglink != null && !imglink.equals("null") && !imglink.equals("http://lubsolution.com/mydms/staticnull") && !imglink.equals("http://lubsolution.com/mydms/static") && !imglink.equals("")) {
            check = false;
        } else {
            check = true;
        }

        return check;
    }

    public static String FormatPhone(String phone) {
        String currentPhone = "";

        switch (phone.replace(".", "").length()) {
            case 5:
                currentPhone = phone.subSequence(0, 4) + "." + phone.subSequence(4, 5);
                break;

            case 6:
                currentPhone = phone.subSequence(0, 4) + "." + phone.subSequence(4, 6);
                break;

            case 7:
                currentPhone = phone.subSequence(0, 4) + "." + phone.subSequence(4, 7);
                break;

            case 8:
                currentPhone = phone.subSequence(0, 4) + "." + phone.subSequence(4, 7) + "." + phone.subSequence(7, 8);
                break;

            case 9:
                currentPhone = phone.subSequence(0, 4) + "." + phone.subSequence(4, 7) + "." + phone.subSequence(7, 9);
                break;

            case 10:
                currentPhone = phone.subSequence(0, 4) + "." + phone.subSequence(4, 7) + "." + phone.subSequence(7, 10);
                break;

            case 11:
                currentPhone = phone.subSequence(0, 5) + "." + phone.subSequence(5, 8) + "." + phone.subSequence(8, 11);
                break;

            default:
                currentPhone = phone;
                break;
        }

        return currentPhone;
    }

    public static boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static boolean isEmpty(String value) {
        if (value.trim().length() > 0)
            return false;

        return true;
    }

    public static Double moneyValue(EditText editText) {
        if (isEmptyValue(editText))
            return 0.0;
        try {
            return Double.parseDouble(editText.getText().toString().trim().replaceAll(",|\\s|\\.", ""));
        } catch (NumberFormatException NaN) {
            return 0.0;
        }
    }


    public static boolean isEmptyValue(EditText editText) {
        if (editText.getText().toString().trim().equals("") || editText.getText().toString().trim().equals(""))
            return true;

        return false;
    }

    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "");
    }

    public static void textMoneyEvent(final EditText edText, final Double limitMoney, final CallbackDouble mlistener) {
        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edText.removeTextChangedListener(this);
                try {
                    if (limitMoney == null){
                        edText.setText(Util.FormatMoney(Util.valueMoney(edText)));
                        edText.setSelection(edText.getText().toString().length());

                        mlistener.Result(Util.valueMoney(edText));
                        edText.addTextChangedListener(this);

                    }else if (limitMoney >0){
                        if (Util.valueMoney(edText) > limitMoney){
                            Util.showToast("Số tiền lớn hơn giới hạn");
                            String text = Util.valueMoneyString(edText).replaceFirst(".$","");

                            edText.setText(Util.FormatMoney(Double.valueOf(text)));
                            edText.setSelection(edText.getText().toString().length());

                            mlistener.Result(Util.valueMoney(edText));
                            edText.addTextChangedListener(this);

                        }else {
                            edText.setText(Util.FormatMoney(Util.valueMoney(edText)));
                            edText.setSelection(edText.getText().toString().length());

                            mlistener.Result(Util.valueMoney(edText));
                            edText.addTextChangedListener(this);

                        }

                    }else if (limitMoney <0){
                        if (Util.valueMoney(edText) > limitMoney*-1){
                            Util.showToast("Số tiền nhỏ hơn giới hạn");
                            String text = Util.valueMoneyString(edText).replaceFirst(".$","");

                            edText.setText(Util.FormatMoney(Double.valueOf(text)));
                            edText.setSelection(edText.getText().toString().length());

                            mlistener.Result(Util.valueMoney(edText)*-1);
                            edText.addTextChangedListener(this);

                        }else {
                            edText.setText(Util.FormatMoney(Util.valueMoney(edText)));
                            edText.setSelection(edText.getText().toString().length());

                            mlistener.Result(Util.valueMoney(edText)*-1);
                            edText.addTextChangedListener(this);

                        }
                    }



                } catch (Exception ex) {
                    ex.printStackTrace();
                    edText.addTextChangedListener(this);
                }

            }
        });


    }

    public static void textPhoneEvent(final EditText edText, final CallbackString mlistener) {
        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                edText.removeTextChangedListener(this);
                mlistener.Result(edText.getText().toString().replace(".", ""));
                try {
                    edText.setText(Util.FormatPhone(edText.getText().toString().replace(".", "")));
                    edText.setSelection(edText.getText().toString().length());

                    //listener.Result(edInput.getText().toString().replace(".", ""));
                    edText.addTextChangedListener(this);





                } catch (Exception ex) {
//                    ex.printStackTrace();
                    edText.addTextChangedListener(this);
                }

            }
        });
    }

    public static void textViewEvent(final TextView tvText, final CallbackString mlistener) {
        tvText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mlistener.Result(s.toString());

            }
        });
    }

    public static String getPhoneValue(EditText edText){
        return edText.getText().toString().replace(".", "");
    }

    public static String getPhoneValue(CInputForm edText){
        return edText.getText().toString().replace(".", "");
    }


    public static void textEvent(final EditText edText, final CallbackString mlistener) {
        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                mlistener.Result(text);

            }
        });
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(Util.getInstance().getCurrentActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();

    }

    public static String convertToUtf(String s) {

        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static boolean checkForVirtualDevice() {
        if (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")){
            return true;
        }else {
            return false;
        }

    }

    public static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    public static int getNavigationBarHeight() {
        Resources resources = getInstance().getCurrentActivity().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void createLinearRV(RecyclerView recyclerView, RecyclerView.Adapter adapter){
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    public static void createHorizontalRV(RecyclerView recyclerView, RecyclerView.Adapter adapter){
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Util.getInstance().getCurrentActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    public static void createGridRV(RecyclerView recyclerView, RecyclerView.Adapter adapter, int gridCount){
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(Util.getInstance().getCurrentActivity(), gridCount);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemDecorationGridSpace((int) Util.convertDp2Px(1),gridCount));
    }

    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

    public static String combine2String(String text1, String text2, int width){
        String space ="";
        int length = width - (text1.length() + text2.length());
        for (int i=0; i<length; i++){
            space+=" ";
        }

        return text1+ space + text2;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static boolean isJSONObject(String text) {
        try {
            new JSONObject(text);
            return true;
        } catch (JSONException ex) {
            return false;

        }

    }

    public static boolean isJSONArray(String text) {
        try {
            new JSONArray(text);
            return true;
        } catch (JSONException ex) {
            return false;

        }

    }

    public static String atCurrentLine() {
        int level = 3;
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        return (traces[level].toString() );

//        return new Exception().getStackTrace()[0].getFileName();
    }

    public static void smoothImageRotation(ImageView img, float degree){
        img.animate().rotationBy(degree);

    }
    public static String firstTwo(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }

    public static int convertSdpToInt(int sdp){
        //sdp : R.dimen._sdp
        return getInstance().getCurrentActivity().getResources().getDimensionPixelSize(sdp);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static boolean isAdmin(){
        return CustomSQL.getBoolean(Constants.IS_ADMIN);
    }

    public static String getIcon(int name){
        return getInstance().getCurrentActivity().getResources().getString(name);

    }

    public static String getStringIcon(String text,String blank, int icon){
        return text+ "   "+ getInstance().getCurrentActivity().getResources().getString(icon);

    }

    public static String getIconString( int icon, String blank, String text){
        return  getInstance().getCurrentActivity().getResources().getString(icon)+ blank+ text;

    }

    public static void clearSQL(){
        List<BaseModel> listUser = CustomSQL.getListObject(Constants.USER_LIST);
        int versionCode = CustomSQL.getInt(Constants.VERSION_CODE);
        CustomSQL.clear();
        CustomSQL.setListBaseModel(Constants.USER_LIST, listUser);
        CustomSQL.setInt(Constants.VERSION_CODE, versionCode);
    }

}
