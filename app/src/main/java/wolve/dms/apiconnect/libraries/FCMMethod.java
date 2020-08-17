package wolve.dms.apiconnect.libraries;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import wolve.dms.callback.CallbackString;

public class FCMMethod {
    public static void getFCMToken(CallbackString listener) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            listener.Result("");
                            Log.e("tag", "getInstanceId failed", task.getException());
                            return;
                        }

                        listener.Result(task.getResult().getToken());


                    }

                });

    }
}
