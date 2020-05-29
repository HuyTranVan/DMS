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
import java.util.List;

import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 10/2/16.
 */
public class DownloadListImage extends AsyncTask<String, Void, List<BaseModel>> {
    private List<BaseModel> mList;
    private String mKey;
    private CallbackListObject mListener;

    public DownloadListImage(List<BaseModel> list, String keyurl, CallbackListObject listener){
        this.mList = list;
        this.mListener = listener;
        this.mKey = keyurl;

    }

    @Override
    protected List<BaseModel> doInBackground(String...URL) {
        Bitmap bm = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        try {
            for (int i=0; i< mList.size(); i++){
                if (!Util.checkImageNull(mList.get(i).getString("image"))){
                    URL aURL = new URL(mList.get(i).getString("image"));
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    is = conn.getInputStream();
                    bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);

                    String path = Util.storeImage(bm, mList.get(i).getString("id"));
                    mList.get(i).put("image_path", path);

                }

            }

            if (bis != null && is != null){
                bis.close();
                is.close();
            }


        } catch (IOException e) {
            Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
        }
        return mList;
    }
    @Override
    protected void onPostExecute(List<BaseModel> result) {
        mListener.onResponse(result);

    }
}
