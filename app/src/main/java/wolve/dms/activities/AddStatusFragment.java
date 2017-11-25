package wolve.dms.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.R;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.controls.CInputForm;
import wolve.dms.models.Status;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class AddStatusFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName;
    private Button btnSubmit;
    private TextView tvColorChoice, tvColor;
    private Status status;
    private StatusActivity mActivity;
    private RadioGroup rdgStatusDefault;
    private RadioButton rdDefault;
    private RadioButton rdNoDefault;

    private String currentColor = "#2196f3";
    private int defaultStatus = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_status,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        tvColorChoice.setFocusable(false);
        tvColor.setBackgroundColor(Color.parseColor(currentColor));
        rdNoDefault.setChecked(true);
        String bundle = getArguments().getString(Constants.STATUS);
        if (bundle != null){
            try {
                status = new Status(new JSONObject(bundle));
                edName.setText(status.getString("name"));
                tvColor.setBackgroundColor(Color.parseColor(status.getString("color")));
                if (status.getBoolean("defaultStatus") != null){
                    if (status.getBoolean("defaultStatus")){
                        rdDefault.setChecked(true);
                        rdNoDefault.setChecked(false);
                        defaultStatus = 1;
                    }else {
                        rdDefault.setChecked(false);
                        rdNoDefault.setChecked(true);
                        defaultStatus = 0;
                    }
                }else {
                    rdNoDefault.setChecked(true);
                    rdDefault.setChecked(false);
                    defaultStatus = 0;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvColorChoice.setOnClickListener(this);
        rdgStatusDefault.setOnCheckedChangeListener(this);
    }

    private void initializeView() {
        edName = (CInputForm) view.findViewById(R.id.add_status_name);
        tvColor = (TextView) view.findViewById(R.id.add_status_color);
        tvColorChoice = (TextView) view.findViewById(R.id.add_status_colorchoice);
        btnSubmit = (Button) view.findViewById(R.id.add_status_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        rdgStatusDefault = (RadioGroup) view.findViewById(R.id.add_status_radiogroup);
        rdDefault = (RadioButton) view.findViewById(R.id.add_status_radio_default);
        rdNoDefault = (RadioButton) view.findViewById(R.id.add_status_radio_nodefault);

        mActivity = (StatusActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                Util.hideKeyboard(v);
                break;

            case R.id.add_status_colorchoice:
                ColorChooserDialog dialog = new ColorChooserDialog(mActivity);
                dialog.setTitle("CHỌN MÀU");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        currentColor = String.format("#%06X", (0xFFFFFF & color));
                        tvColor.setBackgroundColor(Color.parseColor(currentColor));
                    }
                });
                dialog.show();
                break;

            case R.id.add_status_submit:
                if (edName.getText().toString().trim().equals("")){
                    CustomCenterDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {

                        }
                    });

                }else {
                    String param = String.format(Api_link.STATUS_CREATE_PARAM, status == null? "" : "id="+ status.getString("id") +"&",
                            Util.encodeString(edName.getText().toString()),
                            currentColor,
                            defaultStatus);


                    StatusConnect.CreateStatus(param, new CallbackJSONObject() {
                        @Override
                        public void onResponse(JSONObject result) {

                            getActivity().getSupportFragmentManager().popBackStack();
                            mActivity.loadStatus();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, true);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.add_status_radio_default:
                defaultStatus = 1;
                break;

            case R.id.add_status_radio_nodefault:
                defaultStatus = 0;
                break;
        }
    }
}
