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

import wolve.dms.callback.CallbackListObject;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 10/2/16.
 */
public class DownloadImageMethod extends AsyncTask<String, Void, List<BaseModel>> {
    private List<BaseModel> mList;
    private String mKey, mGroupName;
    private CallbackListObject mListener;

    public DownloadImageMethod(List<BaseModel> list, String keyurl, String groupName, CallbackListObject listener) {
        this.mList = list;
        this.mListener = listener;
        this.mKey = keyurl;
        this.mGroupName = groupName;
    }

    @Override
    protected List<BaseModel> doInBackground(String... URL) {
        Bitmap bm = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        try {
            for (int i = 0; i < mList.size(); i++) {
                if (!Util.checkImageNull(mList.get(i).getString(mKey))) {
                    URL aURL = new URL(mList.get(i).getString(mKey));
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    is = conn.getInputStream();
                    bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);

                    Uri image_uri = Util.storeImage(
                            bm,
                            mGroupName,
                            String.format("%s%s", mGroupName, mList.get(i).getString("id")),
                            false);

                    mList.get(i).put("image_uri", Util.getRealPathFromImageURI(image_uri));

                }

            }

            if (bis != null && is != null) {
                bis.close();
                is.close();
            }


        } catch (IOException e) {
            Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
        }
        return mList;
    }

    @Override
    protected void onPostExecute(List<BaseModel> result) {
        mListener.onResponse(result);

    }
}
