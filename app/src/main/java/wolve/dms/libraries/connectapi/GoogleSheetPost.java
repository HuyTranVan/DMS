package wolve.dms.libraries.connectapi;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wolve.dms.apiconnect.Api_link;

public class GoogleSheetPost extends AsyncTask<Void, Void, List<String>> {
    private Sheets mService = null;
    private Exception mLastError = null;

    public GoogleSheetPost(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Android Quickstart")
                .build();
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            postData();
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    private List<String> getDataFromApi() throws IOException {
        String spreadsheetId = "13MC4CB_UOP10HwDm7vJQDgBd2vHqie7kn9r3-mdFvU8";
//            String range = "Class Data!A2:E";
        String range = "DISTRICT!A1:B";
        List<String> results = new ArrayList<String>();
        ValueRange response = this.mService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();


        List<List<Object>> values = response.getValues();
        if (values != null) {
//                results.add("Name, Major");
            for (List row : values) {
                results.add(row.get(0) + ", " + row.get(1));
            }
        }
        return results;
    }





//    @Override
//    protected void onPreExecute() {
//        mOutputText.setText("");
//        mProgress.show();
//    }

    @Override
    protected void onPostExecute(List<String> output) {
//        mProgress.hide();
//        if (output == null || output.size() == 0) {
//            mOutputText.setText("No results returned.");
//        } else {
//            output.add(0, "Data retrieved using the Google Sheets API:");
//            mOutputText.setText(TextUtils.join("\n", output));
//        }
    }

    @Override
    protected void onCancelled() {
//        mProgress.hide();
//        if (mLastError != null) {
//            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                showGooglePlayServicesAvailabilityErrorDialog(
//                        ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                .getConnectionStatusCode());
//            } else if (mLastError instanceof UserRecoverableAuthIOException) { startActivityForResult(
//                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                        TestGoogleSheet.REQUEST_AUTHORIZATION);
//            } else {
//                mOutputText.setText("The following error occurred:\n"
//                        + mLastError.getMessage());
//            }
//        } else {
//            mOutputText.setText("Request cancelled.");
//        }
    }

    private void postData() throws IOException {
        String spreadsheetId = Api_link.GOOGLESHEET_KEY;
        String range = Api_link.GOOGLESHEET_TAB;
        List<List<Object>> values = new ArrayList<>();

        List<Object> data1 = new ArrayList<>();
        data1.add("objA");
        data1.add("objB");

        List<Object> data2 = new ArrayList<>();
        data2.add("Quận N");
        data2.add("Quận M");

        //There are obviously more dynamic ways to do these, but you get the picture
        values.add(data1);
        values.add(data2);

        //Create the valuerange object and set its fields
        ValueRange valueRange = new ValueRange();

        valueRange.setMajorDimension("ROWS");
        valueRange.setRange(range);
        valueRange.setValues(values);

        //then gloriously execute this copy-pasted code ;)
        this.mService.spreadsheets().values()
                .update(spreadsheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute();

    }
}
