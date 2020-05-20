package wolve.dms.libraries.connectapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import wolve.dms.callback.CallbackString;
import wolve.dms.utils.Util;

import static android.content.ContentValues.TAG;

/**
 * Created by tranhuy on 10/2/16.
 */
public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    private String mUrl;
    private CallbackString mListener;

    public  DownloadImage (String url, CallbackString listener){
        this.mUrl = url;
        this.mListener = listener;
    }

    @Override
    protected Bitmap doInBackground(String...URL) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(mUrl);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
        }
        return bm;
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        //mListener.Result(Util.storeImage(result));

    }
}
