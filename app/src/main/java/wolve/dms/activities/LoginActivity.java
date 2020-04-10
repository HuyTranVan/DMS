package wolve.dms.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.BuildConfig;
import wolve.dms.DMSApplication;
import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.SystemConnect;
import wolve.dms.apiconnect.UserConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/13/17.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText edUsername, edPassword;
    private Button btnSubmit;
    private TextView btnKeyboard;
    private Boolean detectNumberKeyboard = true;

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
        btnKeyboard = findViewById(R.id.login_keyboard);

    }

    @Override
    public void initialData() {
        edUsername.setInputType(InputType.TYPE_CLASS_NUMBER);
        edUsername.setText(CustomSQL.getString(Constants.USER_USERNAME));

    }

    @Override
    public void addEvent() {
        btnSubmit.setOnClickListener(this);
        btnKeyboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.login_submit:
                submitLogin();

                break;

            case R.id.login_keyboard:
                if (!detectNumberKeyboard){
                    edUsername.setInputType(InputType.TYPE_CLASS_NUMBER);

                }else {
                    edUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                detectNumberKeyboard = !detectNumberKeyboard;

                break;
        }

    }

    private void submitLogin(){
        if (Util.isEmpty(edUsername) || Util.isEmpty(edPassword)){
            Util.showToast("Vui lòng nhập đủ thông tin");
        }else {
            SystemConnect.getFCMToken(new CallbackString() {
                @Override
                public void Result(String s) {
                    if (!s.equals("")){
                        UserConnect.doLogin(edUsername.getText().toString().trim(),
                                edPassword.getText().toString().trim(),
                                s,
                                new CallbackBoolean() {
                                    @Override
                                    public void onRespone(Boolean result) {
                                        if (result){
                                            Util.showToast("Đăng nhập thành công");
                                            Transaction.gotoHomeActivity();
                                            CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, true);

                                        }
                                    }
                                });
                    }else {
                        Util.showSnackbar("Đăng nhập thất bại", "Thử lại", new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                submitLogin();
                            }
                        });
                    }
                }
            });

        }
    }

}
