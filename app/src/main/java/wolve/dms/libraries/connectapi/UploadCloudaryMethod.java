package wolve.dms.libraries.connectapi;

import android.os.AsyncTask;
import android.webkit.URLUtil;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import wolve.dms.callback.CallbackString;

/**
 * Created by tranhuy on 7/22/16.
 */
public class UploadCloudaryMethod extends AsyncTask<String, Void, String> {
    private CallbackString mListener = null;
    private Cloudinary mCloud;
    private String uriPath;

    public UploadCloudaryMethod(String path, CallbackString listener) {
        mListener = listener;
        uriPath = path;
        Map config = new HashMap();
        config.put("cloud_name", "lubsolution");
        config.put("api_key", "482386522287271");
        config.put("api_secret", "Mh2EsnmYHBAsTAp7jsNLoJ5dXhk");
        mCloud = new Cloudinary(config);

    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Map map = mCloud.uploader().upload(uriPath, ObjectUtils.emptyMap());
            return map.get("url").toString();

        } catch (IOException e) {
            return e.toString();
        }

    }

    @Override
    protected void onPostExecute(String response) {
        if (URLUtil.isValidUrl(response)) {
            mListener.Result(response);

        } else {
            mListener.Result(null);


        }


    }
}


