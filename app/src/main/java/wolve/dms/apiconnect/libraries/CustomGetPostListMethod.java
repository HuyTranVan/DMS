package wolve.dms.apiconnect.libraries;

import android.os.AsyncTask;
import android.view.View;

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

import wolve.dms.callback.CallbackCustomList;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

import static wolve.dms.apiconnect.ApiUtil.getResponeArraySuccess;
import static wolve.dms.apiconnect.ApiUtil.getResponeObjectSuccess;
import static wolve.dms.apiconnect.ApiUtil.responeIsSuccess;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomGetPostListMethod extends AsyncTask<String, Void, List<String>> {
    private CallbackCustomList mListener = null;
    private List<BaseModel> mParams;
    private CustomGetPostListMethod main;

    public CustomGetPostListMethod(List<BaseModel> listParams, CallbackCustomList listener, boolean showLoading) {
        this.mListener = listener;
        this.mParams = listParams;
        this.main = this;

        UtilLoading.getInstance().showLoading(showLoading);

        if (!Util.checkInternetConnection()){
            main.cancel(true);
            Util.showLongSnackbar("No internet!",
                    "Try again",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new CustomGetPostListMethod(mParams, mListener, showLoading);
                        }
                    });

        }
    }

    @Override
    protected List<String> doInBackground(String... s) {
        List<String> listResult = new ArrayList<>();

        for (int i = 0; i < mParams.size(); i++) {
            switch (mParams.get(i).getString("method")) {
                case "POST":
                    try {
                        URL obj = new URL(mParams.get(i).getString("url"));
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        con.setReadTimeout(5000);
                        con.setConnectTimeout(5000);
                        con.setRequestMethod("POST");
                        con.setRequestProperty("User-Agent", "Mozilla/5.0");
                        con.setRequestProperty("Content-Type", mParams.get(i).getBoolean("isjson") ? "application/json" : "application/x-www-form-urlencoded");
                        con.setRequestProperty("x-wolver-accesstoken", User.getToken());
                        con.setRequestProperty("x-wolver-accessid", User.getUserId());
                        con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

                        DataOutputStream wr = null;
                        BufferedReader in = null;
                        con.setDoOutput(true);


                        wr = new DataOutputStream(con.getOutputStream());
                        wr.writeBytes(mParams.get(i).getString("param"));
                        wr.flush();

                        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        listResult.add(String.valueOf(response));


                        in.close();
                        wr.close();

                    } catch (MalformedURLException e) {
                        listResult.add(e.toString());
                    } catch (ProtocolException e) {
                        listResult.add(e.toString());
                    } catch (IOException e) {
                        listResult.add(e.toString());
                    }

                    break;

                case "GET":
                    try {
                        URL obj = new URL(mParams.get(i).getString("url"));
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                        con.setReadTimeout(5000);
                        con.setConnectTimeout(5000);
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

                        listResult.add(String.valueOf(response));

                    } catch (MalformedURLException e) {
                        listResult.add(e.toString());
                    } catch (ProtocolException e) {
                        listResult.add(e.toString());
                    } catch (IOException e) {
                        listResult.add(e.toString());
                    }

                    break;
            }

        }

        return listResult;
    }

    @Override
    protected void onPostExecute(List<String> responses) {
        UtilLoading.getInstance().stopLoading();

        List<BaseModel> listResult = new ArrayList<>();
        for (int i = 0; i < responses.size(); i++) {
            if (Util.isJSONObject(responses.get(i))) {
                listResult.add(new BaseModel(responses.get(i)));

            } else {
                Constants.throwError(String.format("#%d error"));
                listResult.add(null);


            }

        }

        mListener.onResponse(listResult);

    }

}


