package wolve.dms.libraries.connectapi;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackList;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomDeleteListMethod extends AsyncTask<String, Void, List<String>> {
    private CallbackList mListener = null;
    private List<String> listURL;

    public CustomDeleteListMethod(List<String> listurl,  CallbackList listener) {
        mListener = listener;
        this.listURL = listurl;

    }

    @Override
        protected List<String> doInBackground(String... params) {
//        Log.d("url: ", baseUrl);
//        Log.d("params: ", mParams.toString());

        List<String> listResult = new ArrayList<>();
        for (int i=0; i<listURL.size(); i++) {

            try {
                URL obj = new URL(listURL.get(i));
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("DELETE");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("x-wolver-accesstoken", User.getToken());
                con.setRequestProperty("x-wolver-accessid", User.getUserId());
                con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

                int responseCode = con.getResponseCode();
                StringBuffer response = null;

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                //return "errorCode: 01";
            } catch (ProtocolException e) {
                e.printStackTrace();
                //return "errorCode: 02";
            } catch (IOException e) {
                e.printStackTrace();
                //return "errorCode: 03";
            }
        }

        return listResult;

    }

    @Override
    protected void onPostExecute(List<String> response) {
        List<String> listResult = new ArrayList<>();
        if (response == null){
            mListener.onError("Lỗi kết nối server ");
            Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);

        }else {
            for (int i=0; i<response.size(); i++) {
                try {
                    JSONObject object = new JSONObject(response.get(i));
                    if (object.getInt("status") == 200) {
                        listResult.add(object.getString("data"));

                    } else {
                        Util.getInstance().showSnackbar("Lỗi dữ liệu", null, null);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            mListener.onResponse(listResult);
        }

    }
}


