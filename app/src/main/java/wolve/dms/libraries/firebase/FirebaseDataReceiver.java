package wolve.dms.libraries.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseDataReceiver extends WakefulBroadcastReceiver {
    private final String TAG = "FirebaseDataReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "FirebaseDataReceiver");

        //Util.setBadge(intent);
        Boolean bundle = intent.hasExtra("data");
        Boolean isTag = intent.hasExtra("tag");

        if (isTag) {
            try {
                JSONObject tagObject = new JSONObject(intent.getExtras().getString("tag"));
                //Util.setBadge(tagObject.getInt(Constants.BADGE));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}