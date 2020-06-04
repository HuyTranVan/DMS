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
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.SheetConnect;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

public class GoogleSheetCreateUpdateTab extends AsyncTask<Void, Void, List<List<Object>>> {
    private String spreadsheetId;
    private NetHttpTransport HTTP_TRANSPORT;
    private JsonFactory JSON_FACTORY;
    private CallbackListList mListener;
    private List<String> SCOPES;
    private List<Request> mRequestList;
    private List<List<Object>> mParams;

    public interface CallbackListList {
        void onRespone(List<List<Object>> results);
    }

    public GoogleSheetCreateUpdateTab(String sheetId, List<Request> requestlist, CallbackListList callbackListList) {
        this.spreadsheetId = sheetId;
        //this.mTitle = tabtitle;
        mRequestList = requestlist;
        this.HTTP_TRANSPORT = new NetHttpTransport();
        this.mListener = callbackListList;
        this.JSON_FACTORY = JacksonFactory.getDefaultInstance();
        this.SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
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
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(Constants.DMS_NAME)
                .build();

//        List<Request> requests = new ArrayList<>();
//        Request request = new Request();
//        request.setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(mTitle)));
//
//        requests.add(request);


        try {
            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(mRequestList);
            BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(spreadsheetId, body).execute();

            Log.e("res", response.getReplies().toString());
            //FindReplaceResponse findReplaceResponse = response.getReplies().get(1).getFindReplace();
            //System.out.printf("%d replacements made.", findReplaceResponse.getOccurrencesChanged());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mParams;
    }

    @Override
    protected void onPostExecute(List<List<Object>> lists) {
//        super.onPostExecute(lists);
        mListener.onRespone(lists);
    }
}



