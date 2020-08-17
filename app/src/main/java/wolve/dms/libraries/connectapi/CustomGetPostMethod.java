package wolve.dms.libraries.connectapi;

import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.customviews.CInputForm;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by tranhuy on 7/22/16.
 */
public class CustomGetPostMethod extends AsyncTask<String, Void, String> {
    private NewCallbackCustom mListener = null;
    private BaseModel mParam;
    private CustomGetPostMethod main;

    public CustomGetPostMethod(BaseModel param, NewCallbackCustom listener, boolean showLoading) {
        this.mListener = listener;
        this.mParam = param;
        this.main = this;

        LoadingEvent.getInstance().showLoading(showLoading);

        if (!Util.checkInternetConnection()){
            main.cancel(true);
            Util.showLongSnackbar("No internet!",
                    "Try again",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new CustomGetPostMethod(mParam, mListener, showLoading);
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
            switch (mParam.getString("method")) {
                case "POST":
                    url = new URL(mParam.getString("url"));
                    con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    con.setRequestProperty("Content-Type", mParam.getBoolean("isjson") ? "application/json" : "application/x-www-form-urlencoded");
                    con.setRequestProperty("x-wolver-accesstoken", User.getToken());
                    con.setRequestProperty("x-wolver-accessid", User.getUserId());
                    con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

                    DataOutputStream wr = null;
                    BufferedReader in = null;
                    con.setDoOutput(true);


                    wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(mParam.getString("param"));
                    wr.flush();

                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    wr.close();

                    break;

                case "GET":
                    url = new URL(mParam.getString("url"));
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

                    break;
                }

            } catch (IOException e) {
                return e.toString();
            }


        return String.valueOf(response);
    }

    @Override
    protected void onPostExecute(String result) {
        if (Util.isJSONObject(result)) {
            BaseModel respone = new BaseModel(result);

            if (responeIsSuccess(respone)){
                if (mParam.getString("resultType").equals(Constants.TYPE_OBJECT)){
                    mListener.onResponse(getResponeObjectSuccess(respone), null);
                    LoadingEvent.getInstance().stopLoading();

                }else if (mParam.getString("resultType").equals(Constants.TYPE_ARRAY)){
                    mListener.onResponse(null, getResponeArraySuccess(respone));
                    LoadingEvent.getInstance().stopLoading();

                }

            }else {
                Constants.throwError(respone.getString("message"));
                mListener.onError(respone.getString("message"));
                LoadingEvent.getInstance().stopLoading();

                if (respone.getInt("status") == 203){
                    doRelogin();

                }

            }

        }else {
            mListener.onError(result);
            LoadingEvent.getInstance().stopLoading();

        }

    }

    private boolean responeIsSuccess(BaseModel respone) {
        if (!respone.isNull("status") && respone.getInt("status") == 200) {
            return true;

        } else {
            return false;
        }
    }

    private BaseModel getResponeObjectSuccess(BaseModel respone) {
        return new BaseModel(respone.getJsonObject("data"));

    }

    private List<BaseModel> getResponeArraySuccess(BaseModel respone) {
        List<BaseModel> list = new ArrayList<>();
        JSONArray array = respone.getJSONArray("data");
        try {
            for (int i = 0; i < array.length(); i++) {
                list.add(new BaseModel(array.getJSONObject(i)));
            }

        } catch (JSONException e) {
            return new ArrayList<>();
        }

        return list;

    }

    private void doRelogin(){
        Util.showLongSnackbar("Lỗi đăng nhập",
                "Sai thông tin xác thực tài khoản, vui lòng đăng nhập lại",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CustomCenterDialog.alertWithButton("Lỗi đăng nhập",
                                "Sai thông tin xác thực tài khoản, vui lòng đăng nhập lại",
                                "đồng ý",
                                new CallbackBoolean() {
                                    @Override
                                    public void onRespone(Boolean result) {
                                        if (result){
                                            Util.deleteAllImageExternalStorage();
                                            CustomSQL.clear();
                                            Transaction.gotoLoginActivityRight();
                                        }
                                    }
                                });
                    }
                });

    }

}


