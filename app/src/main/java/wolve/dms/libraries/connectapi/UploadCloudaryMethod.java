package wolve.dms.libraries.connectapi;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import wolve.dms.apiconnect.Api_link;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class UploadCloudaryMethod extends AsyncTask<String, Void, String> {
    private CallbackCustom mListener = null;
    private Cloudinary mCloud;
    private Uri mUri;

    public UploadCloudaryMethod(Uri uri, CallbackCustom listener) {
        mListener = listener;
        mUri = uri;
        Map config = new HashMap();
        config.put("cloud_name", "lubsolution");
        config.put("api_key", "482386522287271");
        config.put("api_secret", "Mh2EsnmYHBAsTAp7jsNLoJ5dXhk");
        mCloud = new Cloudinary(config);

    }

    @Override protected String doInBackground(String... params) {
        JSONObject object = null;
        try {
            Map map = mCloud.uploader().upload(Util.getRealPathFromURI(mUri), ObjectUtils.emptyMap());

            object = new JSONObject();
            object.put("url", map.get("url").toString());

        } catch (IOException e) {
            return e.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return object.toString();
    }

    @Override protected void onPostExecute(String response) {
        if (Util.isJSONObject(response)){
            mListener.onResponse(new BaseModel(response));

        }else {
            mListener.onError(response);

        }

    }
}


