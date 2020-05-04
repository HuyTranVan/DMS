package wolve.dms.libraries.connectapi;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomPostMethod extends AsyncTask<String, Void, String> {
    private CallbackCustom mListener ;
    private BaseModel mParam;

    public CustomPostMethod(BaseModel param, CallbackCustom listener) {
        this.mListener = listener;
        this.mParam = param;

    }

    @Override protected String doInBackground(String... params){
        Log.d("url: ", mParam.getString("url"));
        Log.d("params: ", mParam.getString("param"));

        StringBuffer response = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(mParam.getString("url")).openConnection();
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
//            con.setRequestProperty("Content-Type",mParam.getBoolean("isjson")? "application/json":"application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Type",mParam.getBoolean("isjson")?  "application/json;charset=ISO-8859-1":"application/x-www-form-urlencoded");
            con.setRequestProperty("x-wolver-accesstoken", User.getToken());
            con.setRequestProperty("x-wolver-accessid", User.getUserId());
            con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

            //String token = User.getToken() + User.getUserId();

            String urlParameters = mParam.getString("param");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            wr.close();

            return String.valueOf(response);

        } catch (MalformedURLException e) {
            return e.toString();
        } catch (ProtocolException e) {
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }

    @Override protected void onPostExecute(String response) {
        if (Util.isJSONObject(response)){
            mListener.onResponse(new BaseModel(response));

        }else {
            mListener.onError(response);

        }

    }
}


