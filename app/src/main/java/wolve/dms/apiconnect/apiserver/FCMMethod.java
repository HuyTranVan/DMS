package wolve.dms.apiconnect.apiserver;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;

public class FCMMethod {
    public static void getFCMToken(CallbackString listener) {
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            listener.Result("");
//                            Log.e("tag", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        listener.Result(task.getResult().getToken());
//
//
//                    }
//
//                });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            listener.Result("");
                            Log.e("tag", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        listener.Result(task.getResult());
//                        String token = task.getResult();
//
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
