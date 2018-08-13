//package wolve.dms.activities;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.SheetsScopes;
//import com.google.api.services.sheets.v4.model.ValueRange;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Collections;
//import java.util.List;
//
//import wolve.dms.libraries.googleauth.AuthorizationCodeInstalledApp;
//import wolve.dms.utils.Util;
//
//public class TestGoogleSheet extends AsyncTask<Void, Void, Void>{
//    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//    private static final String TOKENS_DIRECTORY_PATH = "tokens";
//    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
//    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
//
//    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
//        InputStream in = TestGoogleSheet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(FileUtil.getOutputMediaFile(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
//
//        return credential;
//    }
//
//    /**
//     * Prints the names and majors of students in a sample spreadsheet:
//     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
//     */
//    public static void main(String... args){
//        // Build a new authorized API client service.
//        final NetHttpTransport HTTP_TRANSPORT;
//        try {
//
//            HTTP_TRANSPORT = new NetHttpTransport();
//
//            final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//            final String range = "Class Data!A2:E";
//            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                    .setApplicationName(APPLICATION_NAME)
//                    .build();
//            ValueRange response = service.spreadsheets().values()
//                    .get(spreadsheetId, range)
//                    .execute();
//            List<List<Object>> values = response.getValues();
//            if (values == null || values.isEmpty()) {
//                System.out.println("No data found.");
//            } else {
//                Util.showToast("Name, Major");
//                System.out.println("Name, Major");
//                for (List row : values) {
//                    // Print columns A and E, which correspond to indices 0 and 4.
//                    System.out.printf("%s, %s\n", row.get(0), row.get(4));
//                }
//            }
//
//        }  catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        final NetHttpTransport HTTP_TRANSPORT;
//        try {
//
//            HTTP_TRANSPORT = new NetHttpTransport();
//
//            final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//            final String range = "Class Data!A2:E";
//            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                    .setApplicationName(APPLICATION_NAME)
//                    .build();
//            ValueRange response = service.spreadsheets().values()
//                    .get(spreadsheetId, range)
//                    .execute();
//            List<List<Object>> values = response.getValues();
//            if (values == null || values.isEmpty()) {
//                System.out.println("No data found.");
//            } else {
//                Log.e("name", values.toString());
//                System.out.println("Name, Major");
//                for (List row : values) {
//                    // Print columns A and E, which correspond to indices 0 and 4.
//                    System.out.printf("%s, %s\n", row.get(0), row.get(4));
//                }
//            }
//
//        }  catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
//
//
