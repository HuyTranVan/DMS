package wolve.dms.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.BaseActivity;
import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.FitScrollWithFullscreen;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/13/17.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText edUsername, edPassword;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitScrollWithFullscreen.assistActivity(this, 2);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_login;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        edUsername = (EditText) findViewById(R.id.login_username);
        edPassword = (EditText) findViewById(R.id.login_password);
        btnSubmit = (Button) findViewById(R.id.login_submit);

    }

    @Override
    public void initialData() {
        if (BuildConfig.DEBUG_FLAG ){
            edUsername.setText(CustomSQL.getString(Constants.USER_USERNAME));
            edPassword.setText(CustomSQL.getString(Constants.USER_PASSWORD));
            if (!edUsername.getText().toString().trim().equals("") && !edPassword.getText().toString().trim().equals("")){
                doLogin();
            }

        }
    }

    @Override
    public void addEvent() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_submit:
                if (Util.isEmpty(edUsername) || Util.isEmpty(edPassword)){
                    Util.showToast("Vui lòng nhập đủ thông tin");
                }else {
                    doLogin();
                }

                break;
        }

    }

    private void doLogin(){
        String params = String.format(Api_link.LOGIN_PARAM,edUsername.getText().toString(), edPassword.getText().toString());

        UserConnect.Login(params, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                try {
                    User user = new User(result);
                    Distributor distributor = new Distributor(result.getJSONObject("distributor"));

                    CustomSQL.setObject(Constants.USER, user);
                    CustomSQL.setObject(Constants.DISTRIBUTOR, distributor);
                    CustomSQL.setString(Constants.USER_USERNAME, edUsername.getText().toString().trim());
                    CustomSQL.setString(Constants.USER_PASSWORD, edPassword.getText().toString().trim());

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Transaction.gotoHomeActivity(true);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        }, true);
    }
}
