package wolve.dms.libraries.connectapi.sheetapi;

import android.os.AsyncTask;

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
import java.util.Collections;
import java.util.List;

import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.SheetConnect;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

public class GoogleSheetPostData extends AsyncTask<Void, Void, List<List<Object>>> {
    private String spreadsheetId;
    private String range;
    private NetHttpTransport HTTP_TRANSPORT;
    private JsonFactory JSON_FACTORY;
    private CallbackListList mListener;
    private List<String> SCOPES;
    private List<List<Object>> mParams;
    private String majorDimension;


    public static String SHEET_ROW = "ROWS";
    public static String SHEET_COLUM = "COLUMS";

    public interface CallbackListList {
        void onRespone(List<List<Object>> results);
    }

    public GoogleSheetPostData(String sheetId, String range, List<List<Object>> param, String major, CallbackListList callbackListList) {
        this.spreadsheetId = sheetId;
        this.range = range;
        this.mParams = param;
        this.HTTP_TRANSPORT = new NetHttpTransport();
        this.mListener = callbackListList;
        this.JSON_FACTORY = JacksonFactory.getDefaultInstance();
        this.SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
        this.majorDimension = major;
    }

    private Credential getCredentials() {
        InputStream in = SheetConnect.class.getResourceAsStream(Api_link.GOOGLESHEET_CREDENTIALS_FILE_PATH);
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(Util.createCustomFolder(Api_link.GOOGLESHEET_TOKENS_PATH)))
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
//        String spreadsheetId = Api_link.STATISTICAL_SHEET_KEY;
//        String range = Api_link.GOOGLESHEET_TAB;
        ValueRange valueRange = new ValueRange();

//        valueRange.setMajorDimension("ROWS");
        valueRange.setMajorDimension("COLUMNS");
        valueRange.setRange(range);
        valueRange.setValues(mParams);

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(Constants.DMS_NAME)
                .build();

        try {
            service.spreadsheets().values()
                    .update(spreadsheetId, range, valueRange)
                    .setValueInputOption("RAW")
                    .execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        List<List<Object>> values = new ArrayList<>();
//        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
//                .setApplicationName(Constants.DMS_NAME)
//                .build();
//        ValueRange response = null;
//        try {
//            response = service.spreadsheets().values().get(spreadsheetId, range).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        values = response.getValues();
//        if (values == null || values.isEmpty()) {
//            System.out.println("No data found.");
//        } else {
//            Log.e("name", values.toString());
//            for (List row : values) {
//                // Print columns A and E, which correspond to indices 0 and 4.
////                System.out.printf("%s, %s\n", row.get(0), row.get(4));
//            }
//        }

//        return values;
        return mParams;
    }

    @Override
    protected void onPostExecute(List<List<Object>> lists) {
//        super.onPostExecute(lists);
        mListener.onRespone(lists);
    }
}



