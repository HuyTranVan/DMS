package wolve.dms.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.ScannerCodeAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.SheetConnect;
import wolve.dms.customviews.CInputForm;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetGetData;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

import static wolve.dms.libraries.connectapi.sheetapi.GoogleSheetPostData.SHEET_ROW;

/**
 * Created by macos on 9/15/17.
 */

public class ScannerActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rvCode;
    private TextView tvTitle, tvCount, tvDelete;
    private EditText editText;
    private Button btnSubmit, btnPush2Server;
    private ImageView btnBack;
    private CInputForm ipDistributor;

    private List<String> listDistributor = new ArrayList<>();
    private List<String> listCodes = new ArrayList<>();
    private TextToSpeech textToSpeech;
    private ScannerCodeAdapter adapter;
    private String mCurrentText = "";
    private Handler mHandlerText = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_scanner;
    }

    @Override
    public int setIdContainer() {
        return 0;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        btnSubmit = findViewById(R.id.scanner_submit);
        btnPush2Server = findViewById(R.id.scanner_push);
        ipDistributor = findViewById(R.id.scanner_distributor);
        tvCount = findViewById(R.id.scanner_count);
        editText = findViewById(R.id.scanner_text);
        rvCode = findViewById(R.id.scanner_rvList);
        tvDelete= findViewById(R.id.scanner_delete);

    }

    @Override
    public void initialData() {
        getAllDistributor();

        if (!CustomSQL.getString(Constants.BARCODE).equals("")){
            listCodes = new Gson().fromJson(CustomSQL.getString(Constants.BARCODE), List.class);
        }
        createRVCode(listCodes);
        submitVisibleEvent(listCodes.size());


    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        btnPush2Server.setOnClickListener(this);
        distributorEvent();
        textEvent();

    }

    private void getAllDistributor(){
        SheetConnect.getALlValue(Api_link.SCANNER_SHEET_KEY, Api_link.SCANNER_DISTRIBUTOR_TAB, new GoogleSheetGetData.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                listDistributor = new ArrayList<>();
                if (results != null){
                    for (int i=0; i<results.size(); i++){
                        listDistributor.add(results.get(i).get(0).toString());
                    }
                }

            }
        }, true);
    }

    private void createRVCode(List<String> list){
        adapter = new ScannerCodeAdapter(list, new ScannerCodeAdapter.CountListener() {
            @Override
            public void onRespone(int count) {
                submitVisibleEvent(count);
                editText.setText("");
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
                editText.requestFocus();


            }
        });
        Util.createLinearRV(rvCode, adapter);
    }


    private void speakText(final String text) {
        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=textToSpeech.setLanguage(Locale.UK);
                    if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Util.showToast("This Language is not supported");

                    } else{
                        if(text==null||"".equals(text)) {
                            textToSpeech.speak("Content not available", TextToSpeech.QUEUE_FLUSH, null);
                        }else
                            textToSpeech.speak(text , TextToSpeech.QUEUE_FLUSH, null);
                    }

                } else
                    Util.showToast("Initilization Failed!");
            }
        });

    }

    private void insertCode(String code){
        adapter.addItem(code);

    }

    private void textEvent(){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())){
                    if (!s.toString().isEmpty() && s.toString().length()>1){
                        mCurrentText = s.toString();
                        mHandlerText.removeCallbacks(delayForText);
                        mHandlerText.postDelayed(delayForText, 200);

                    }

                }
            }
        });

    }

    private Runnable delayForText = new Runnable() {
        @Override
        public void run() {
            if (!Util.isEmpty(editText)){
                insertCode(editText.getText().toString().trim());
            }
        }
    };


    private void distributorEvent() {
//        if (listDistributor.size() >0){
            ipDistributor.setDropdown(true, new CInputForm.ClickListener() {
                @Override
                public void onClick(View view) {
//                    CustomBottomDialog.choiceList("CHỌN NHÀ PHÂN PHỐI",listDistributor , new CustomBottomDialog.StringListener() {
//                        @Override
//                        public void onResponse(String content) {
//                            ipDistributor.setText(content);
//                        }
//                    });

                }
            });

//        }else {
//            Util.showToast("Chưa tạo nhà phân phối");
//        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.scanner_delete:
                adapter.deleteAllItem();

                break;


            case R.id.scanner_submit:
                if (!editText.getText().toString().equals("")){
                    insertCode(editText.getText().toString().trim());

                }

            break;

            case R.id.scanner_push:
                if (!ipDistributor.getText().toString().equals("")){
                    pushAllToServer();

                }else {
                    Util.showToast("Vui lòng chọn nhà phân phối");
                }

                break;




        }
    }

    @Override
    public void onBackPressed() {
        backEvent();
    }

    private void backEvent(){
        Transaction.gotoHomeActivityRight(true);

    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED ) {
                Util.showToast("Cấp quyền truy cập không thành công");

            }else {
                Transaction.gotoMapsActivity();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void submitVisibleEvent(int count){
        if (count >0){
            speakText(String.valueOf(count));
            tvCount.setText(String.valueOf(count));
        }else if (count ==0){
            speakText("Empty");
            tvCount.setText(String.valueOf(count));
        }else if (count == -1){
            speakText("Exist");
        }

        btnPush2Server.setVisibility(count>0 ? View.VISIBLE : View.GONE);
        tvDelete.setVisibility(count>0 ? View.VISIBLE : View.GONE);

    }

    private void pushAllToServer(){
        SheetConnect.getALlValue(Api_link.SCANNER_SHEET_KEY, String.format(Api_link.SCANNER_CODE_TAB,3), new GoogleSheetGetData.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                int pos = 2;
                if (results != null){
                    pos = results.size()+2;
                }

                String range = String.format(Api_link.SCANNER_CODE_TAB, pos);
                SheetConnect.postValue(Api_link.SCANNER_SHEET_KEY, range, getListValueExportToSheet(adapter.getAllItem()),SHEET_ROW, new GoogleSheetGetData.CallbackListList() {
                    @Override
                    public void onRespone(List<List<Object>> results) {
                        Util.showToast("Thành công");
                        adapter.deleteAllItem();
                        ipDistributor.setText("");

                    }
                }, true);

            }
        }, false);

    }

    private List<List<Object>> getListValueExportToSheet(List<String> listValue){
        List<List<Object>> values = new ArrayList<>();
        for (int i=0; i<listValue.size(); i++){
            List<Object> data = new ArrayList<>();
            data.add(Util.CurrentMonthYearHour());
            data.add(listValue.get(i));
            data.add(ipDistributor.getText().toString());

            values.add(data);


        }

        return values;
    }

}
