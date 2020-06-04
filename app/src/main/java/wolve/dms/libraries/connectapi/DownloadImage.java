package wolve.dms.libraries.connectapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import wolve.dms.callback.CallbackString;

/**
 * Created by tranhuy on 10/2/16.
 */
public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    private String mUrl;
    private CallbackString mListener;

    public DownloadImage(String url, CallbackString listener) {
        this.mUrl = url;
        this.mListener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... URL) {
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
            Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
        }
        return bm;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        //mListener.Result(Util.storeImage(result));

    }
}
