package wolve.dms.libraries;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 9/30/16.
 */
public class BitmapView {

//    public static void saveToSD(String fileName,Bitmap outputImage){
//        File storagePath = new File(Environment.getExternalStorageDirectory() + "/Tinhtiendidong/");
//        storagePath.mkdirs();
//
//        File myImage = new File(storagePath, fileName + ".png");
//
//        try {
//            FileOutputStream out = new FileOutputStream(myImage);
//            outputImage.compress(Bitmap.CompressFormat.PNG, 80, out);
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static Uri saveImageToSD(String imageName, Bitmap outputImage){
//        File storagePath = new File(Environment.getExternalStorageDirectory() + "/Tinhtiendidong/");
//        storagePath.mkdirs();

        File myImage = Util.createCustomImageFile(imageName);



        try {
            FileOutputStream out = new FileOutputStream(myImage);
            outputImage.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return myImage.getAbsolutePath();
        return Uri.parse(myImage.getPath());
    }

    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }


    public static Bitmap ResizeBitMapDependWidth(Bitmap bitmap, int w){

//        float originalWidth = bitmap.getWidth();
//        float originalHeight = bitmap.getHeight();
//        double scale = width/originalWidth;
//        double height = scale * originalHeight;
//
//        Bitmap background = Bitmap.createBitmap(width, (int)height, Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(background);
//        double xTranslation = 0.0f, yTranslation = (height - originalHeight * scale)/2.0f;
//        Matrix transformation = new Matrix();
//        transformation.postTranslate((float) xTranslation, (float)yTranslation);
//        transformation.preScale((float)scale, (float)scale);
//        Paint paint = new Paint();
//        paint.setFilterBitmap(false);
//        paint.setAntiAlias(true);
//        canvas.drawBitmap(bitmap, transformation, paint);
//        return background;
        Boolean ischecked = false;
        Bitmap BitmapOrg = bitmap;
        Bitmap resizedBitmap = null;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        if (width<=w) {
            return bitmap;
        }
        if (!ischecked) {
            int newWidth = w;
            int newHeight = height*w/width;

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // if you want to rotate the Bitmap
            // matrix.postRotate(45);
            resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                    height, matrix, true);
        }else {
            resizedBitmap=Bitmap.createBitmap(BitmapOrg, 0, 0, w, height);
        }

        return resizedBitmap;
    }


    public Bitmap getbmpfromURL(String surl){
        try {
            URL url = new URL(surl);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();
            Bitmap mIcon = BitmapFactory.decodeStream(in);
            return  mIcon;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}
