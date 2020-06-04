package wolve.dms.libraries.connectapi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import wolve.dms.callback.CallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomGetMethod extends AsyncTask<String, Void, String> {
    private CallbackCustom mListener;
    private String baseUrl;

    public CustomGetMethod(String url, CallbackCustom listener) {
        this.mListener = listener;
        this.baseUrl = url;

    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("url: ", baseUrl);

        StringBuffer response = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(baseUrl).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("x-wolver-accesstoken", User.getToken());
            con.setRequestProperty("x-wolver-accessid", User.getUserId());
            con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return String.valueOf(response);

        } catch (MalformedURLException e) {
            return e.toString();
        } catch (ProtocolException e) {
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (Util.isJSONObject(response)) {
            mListener.onResponse(new BaseModel(response));

        } else {
            mListener.onError(response);

        }

    }
}


