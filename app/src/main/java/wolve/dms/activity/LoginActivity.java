package wolve.dms.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.BaseActivity;
import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSharedPrefer;
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

        if (BuildConfig.DEBUG_FLAG ){
//            edUsername.setText("0908136885");
            edUsername.setText("0916083773");
            edPassword.setText("123");


            doLogin();


        }
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
    public void initializeView() {
        edUsername = (EditText) findViewById(R.id.login_username);
        edPassword = (EditText) findViewById(R.id.login_password);
        btnSubmit = (Button) findViewById(R.id.login_submit);

    }

    @Override
    public void addEvent() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_submit:
                doLogin();
                break;
        }

    }

    private void doLogin(){
        String params = String.format(Constants.LOGIN_PARAM,edUsername.getText().toString(), edPassword.getText().toString());

        UserConnect.Login(params, new CallbackJSONObject() {
            @Override
            public void onResponse(JSONObject result) {
                try {
                    CustomSharedPrefer customShared = new CustomSharedPrefer(Util.getInstance().getCurrentActivity());
                    User user = new User(result);

                    user.setToken(result.getString("token"));
                    user.setId_user(result.getString("id"));

                    customShared.setObject(Constants.USER, user);

                    Transaction.gotoHomeActivity();
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

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
