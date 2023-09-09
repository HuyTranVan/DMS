package wolve.dms.apiconnect.apiserver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import wolve.dms.callback.CallbackBitmap;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 10/2/16.
 */
public class DownloadImageMethod extends AsyncTask<String, Void, Bitmap> {
    private String mUrl;
    private CallbackBitmap mListener;

    public DownloadImageMethod(String url, CallbackBitmap listener) {
        this.mListener = listener;
        this.mUrl = url;
        UtilLoading.getInstance().showLoading(1);

    }

    @Override
    protected Bitmap doInBackground(String... URL) {
        Bitmap bm = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        try {
                if (!Util.checkImageNull(mUrl)) {
                    URL aURL = new URL(mUrl);
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    is = conn.getInputStream();
                    bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);

//                    Uri image_uri = Util.storeImage(
//                            bm,
//                            mGroupName,
//                            String.format("%s%s", mGroupName, mList.get(i).getString("id")),
//                            false);
//
//                    mList.get(i).put("image_uri", image_uri != null? Util.getRealPathFromImageURI(image_uri) : "");




            }

            if (bis != null && is != null){
                bis.close();
                is.close();
            }


        } catch (IOException e) {
            return null;
        }
        return bm;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        UtilLoading.getInstance().stopLoading();
        mListener.onResponse(result);

    }
}
