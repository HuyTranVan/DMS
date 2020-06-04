package wolve.dms.libraries.connectapi;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wolve.dms.callback.CallbackListCustom;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomGetListMethod extends AsyncTask<String, Void, List<String>> {
    private CallbackListCustom mListener = null;
    private String[] listURL;

    public CustomGetListMethod(String[] listUrl, CallbackListCustom listener) {
        this.mListener = listener;
        this.listURL = listUrl;

    }

    @Override
    protected List<String> doInBackground(String... params) {
        List<String> listRespone = new ArrayList<>();

        for (int i = 0; i < listURL.length; i++) {
            try {
                URL obj = new URL(listURL[i]);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("x-wolver-accesstoken", User.getToken());
                con.setRequestProperty("x-wolver-accessid", User.getUserId());
                con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

                int responseCode = con.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                listRespone.add(String.valueOf(response));

            } catch (MalformedURLException e) {
                return null;
            } catch (ProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            }

        }


        return listRespone;
    }

    @Override
    protected void onPostExecute(List<String> responses) {
        List<String> listResult = new ArrayList<>();
        if (responses == null) {
            mListener.onError("Lỗi kết nối server ");
            Util.getInstance().showSnackbar("Lỗi kết nối server ", null, null);

        } else {
            for (int i = 0; i < responses.size(); i++) {
                try {
                    JSONObject object = new JSONObject(responses.get(i));
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


