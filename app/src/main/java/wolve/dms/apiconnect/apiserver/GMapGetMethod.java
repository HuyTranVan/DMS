package wolve.dms.apiconnect.apiserver;

import android.os.AsyncTask;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class GMapGetMethod extends AsyncTask<String, Void, String> {
    private NewCallbackCustom mListener = null;
    private String mUrl;
    private GMapGetMethod main;

    public GMapGetMethod(double lat, double lng, NewCallbackCustom listener, int loadingtimes) {
        this.mListener = listener;
        this.main = this;
        this.mUrl = String.format(Locale.ENGLISH, ApiUtil.MAP_GET_ADDRESS(), lat, lng);

        UtilLoading.getInstance().showLoading(loadingtimes);

        if (!Util.checkInternetConnection()){
            main.cancel(true);
            Util.showLongSnackbar("No internet!",
                    "Try again",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new GMapGetMethod(lat, lng, mListener, loadingtimes);
                        }
                    });

        }else {

        }

    }

    @Override
    protected String doInBackground(String... s) {
        StringBuffer response = null;
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(mUrl);
            con = (HttpURLConnection) url.openConnection();

            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("x-wolver-accesstoken", User.getToken());
            con.setRequestProperty("x-wolver-accessid", User.getUserId());
            con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

            BufferedReader inp = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine1;
            response = new StringBuffer();

            while ((inputLine1 = inp.readLine()) != null) {
                response.append(inputLine1);
            }
            inp.close();

        } catch (IOException e) {
            return e.toString();
        }
        return String.valueOf(response);

    }
        @Override
    protected void onPostExecute(String result) {
        UtilLoading.getInstance().stopLoading();
        if (Util.isJSONObject(result)) {
            BaseModel respone = new BaseModel(result);
            if (respone.hasKey("results")){
                List<BaseModel> arrays = DataUtil.array2ListBaseModel(respone.getJSONArray("results"));
                mListener.onResponse(arrays.get(0), arrays);

            }

        }else {
            mListener.onError("Can not get address from Gmap API!");
            Constants.throwError(result);

        }

    }


}


