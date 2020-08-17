package wolve.dms.libraries.connectapi.sheetapi;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.libraries.GDriveMethod;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;


public class GoogleSheetGetData extends AsyncTask<Void, Void, List<List<Object>>> {
    private String spreadsheetId;
    private String range;
    private NetHttpTransport HTTP_TRANSPORT;
    private JsonFactory JSON_FACTORY;
    private CallbackListList mListener;
    private List<String> SCOPES;

    public interface CallbackListList {
        void onRespone(List<List<Object>> results);
    }

    public GoogleSheetGetData(String sheetId, String sheetTab, CallbackListList callbackListList) {
        this.spreadsheetId = sheetId;
        this.range = sheetTab;
        this.HTTP_TRANSPORT = new NetHttpTransport();
        this.mListener = callbackListList;
        this.JSON_FACTORY = JacksonFactory.getDefaultInstance();
        this.SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    }

    private Credential getCredentials() {
        InputStream in = GDriveMethod.class.getResourceAsStream(ApiUtil.GOOGLESHEET_CREDENTIALS_FILE_PATH);
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(Util.createCustomFolder(ApiUtil.GOOGLESHEET_TOKENS_PATH)))
                    .setAccessType("offline")
                    .build();

            Credential credential = new wolve.dms.libraries.googleauth.AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

            return credential;

        } catch (IOException e) {
            return null;
        }

    }

    @Override
    protected List<List<Object>> doInBackground(Void... params) {
        List<List<Object>> values = new ArrayList<>();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(Constants.DMS_NAME)
                .build();
        try {
            ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();

//            Spreadsheet response1=   service.spreadsheets().get(spreadsheetId).execute();
//            List<Sheet> sheets = response1.getSheets();

            values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                Log.e("name", values.toString());
                for (List row : values) {

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return values;
    }

    @Override
    protected void onPostExecute(List<List<Object>> lists) {
        mListener.onRespone(lists);
    }
}

//Get all Tab
//            Spreadsheet response1= service.spreadsheets().get(spreadsheetId).setIncludeGridData (false).execute ();
//
//            List<Sheet> workSheetList = response1.getSheets();
//
//            for (Sheet sheet : workSheetList) {
//                System.out.println(sheet.getProperties().getTitle());
//            }



