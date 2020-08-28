package wolve.dms.libraries.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import wolve.dms.R;
import wolve.dms.activities.BaseActivity;
import wolve.dms.activities.MapsActivity;
import wolve.dms.activities.SplashScreenActivity;
import wolve.dms.libraries.Contacts;
import wolve.dms.utils.Util;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Map<String, String> data = remoteMessage.getData();
            String tag = data.get("tag");
            sendNotification(tag);
//            String title = remoteMessage.getNotification().getTitle();
//            String message = remoteMessage.getNotification().getBody();


            //ShortcutBadger.applyCount(Util.getInstance().getCurrentActivity(), 1005);

        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String messageBody) {
        BaseActivity activity = (BaseActivity) Util.getInstance().getCurrentActivity();
        activity.receiveBundleFormFCM(messageBody);


//        int NOTIFICATION_ID = 1;
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.(this);
//        builder.setSmallIcon(R.drawable.ic_lub_notify);
//        builder.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_account));
//        builder.setContentTitle("Notification Actions");
//        builder.setContentText("Tap View to launch our website");
//        builder.setAutoCancel(true);
//        PendingIntent launchIntent = getLaunchIntent(NOTIFICATION_ID, getBaseContext());
//
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.journaldev.com"));
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        Intent buttonIntent = new Intent(getBaseContext(), NotificationReceiver.class);
//        buttonIntent.putExtra("notificationId", NOTIFICATION_ID);
//        PendingIntent dismissIntent = PendingIntent.getBroadcast(getBaseContext(), 0, buttonIntent, 0);
//
//        builder.setContentIntent(launchIntent);
//        builder.addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent);
//        builder.addAction(android.R.drawable.ic_delete, "DISMISS", dismissIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        // Will display the notification in the notification bar
//        notificationManager.notify(NOTIFICATION_ID, builder.build());


    }

    public PendingIntent getLaunchIntent(int notificationId, Context context) {

        Intent intent = new Intent(context, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("notificationId", notificationId);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }



//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, SplashScreenActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_icon_pos)
//                        .setContentTitle(getString(R.string.fcm_message))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//
//        BaseActivity activity = (BaseActivity) Util.getInstance().getCurrentActivity();
//        activity.receiveBundleFormFCM(messageBody);
//    }

}




