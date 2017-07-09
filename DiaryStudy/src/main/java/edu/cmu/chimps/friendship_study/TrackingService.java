package edu.cmu.chimps.friendship_study;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.github.privacystreams.accessibility.AccEvent;
import com.github.privacystreams.commons.arithmetic.ArithmeticOperators;
import com.github.privacystreams.commons.item.ItemOperators;
import com.github.privacystreams.communication.Message;
import com.github.privacystreams.core.Function;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.io.IOOperators;
import com.github.privacystreams.utils.AccessibilityUtils;
import com.github.privacystreams.utils.Duration;
import com.github.privacystreams.utils.Globals;
import com.github.privacystreams.utils.Logging;

import edu.cmu.chimps.friendship_study.reminders.ReminderManager;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class TrackingService extends Service {
    private  static final int NOTIFICATION_ID = 1234;

    private static String participantId;
    UQI uqi;
    ReminderManager reminderManager;


    private void setupDropbox(){
        Globals.DropboxConfig.accessToken = uqi.getContext()
                .getResources().getString(R.string.dropbox_access_token);
        Globals.DropboxConfig.leastSyncInterval = Duration.minutes(1);
        Globals.DropboxConfig.onlyOverWifi = true;
        participantId = Utils.getParticipantID(this);
        if(participantId==null){
            Toast.makeText(this,"Please fill in your participant id then start tracking. ", Toast.LENGTH_LONG).show();
        }
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.heart);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Friendship study is running")
                .setSmallIcon(R.drawable.heart64)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(NOTIFICATION_ID, notification);

    }


    public void collectData(){
        Logging.debug("Collecting Data..");

        collectTextEntry();
        collectUIAction();
        collectIM();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        uqi = new UQI(this);
        reminderManager = new ReminderManager(this);
        if(intent!=null
                && intent.getAction()!=null
                && intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)){
            Logging.debug("start collecting..");
            showNotification();
            setupDropbox();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    collectData();

                }
            }).start();
            reminderManager.initialize();
        }

        return START_REDELIVER_INTENT;
    }


    public void collectUIAction(){
        uqi.getData(AccEvent.asUIActions(), Purpose.FEATURE("Love Study UIAction Collection"))
                .setField(AccEvent.ROOT_NODE, new Function<Item, AccessibilityUtils.SerializedAccessibilityNodeInfo>() {
                    @Override
                    public AccessibilityUtils.SerializedAccessibilityNodeInfo apply(UQI uqi, Item input) {
                        AccessibilityNodeInfo node = input.getValueByField(AccEvent.ROOT_NODE);
                        return AccessibilityUtils.serialize(node);
                    }
                })
                .setField(AccEvent.SOURCE_NODE, new Function<Item, AccessibilityUtils.SerializedAccessibilityNodeInfo>() {
                    @Override
                    public AccessibilityUtils.SerializedAccessibilityNodeInfo apply(UQI uqi, Item input) {
                        AccessibilityNodeInfo node = input.getValueByField(AccEvent.SOURCE_NODE);
                        return AccessibilityUtils.serialize(node);
                    }
                })
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(AccEvent.TIME_CREATED, Duration.minutes(20))))
                .localGroupBy("time_round")
                .forEach(IOOperators.uploadToDropbox(new Function<Item, String>() {
                    @Override
                    public String apply(UQI uqi, Item input) {
                        return participantId + "/UIAction_" + input.getValueByField(Item.TIME_CREATED) + ".json";
                    }
                }, true));
    }


    public void collectIM(){
        uqi.getData(Message.asUpdatesInIM(), Purpose.FEATURE("LoveStudy Message Collection"))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(Message.TIME_CREATED, Duration.minutes(1))))
                .localGroupBy("time_round")
                .forEach(IOOperators.uploadToDropbox(new Function<Item, String>() {
                    @Override
                    public String apply(UQI uqi, Item input) {
                        return participantId + "/IM_" + input.getValueByField(Item.TIME_CREATED) + ".json";
                    }
                }, true));
    }

    public void collectTextEntry(){
        uqi.getData(AccEvent.asTextEntries(), Purpose.FEATURE("Love Study Text Entry Collection"))
                .setField(AccEvent.ROOT_NODE, new Function<Item, AccessibilityUtils.SerializedAccessibilityNodeInfo>() {
                    @Override
                    public AccessibilityUtils.SerializedAccessibilityNodeInfo apply(UQI uqi, Item input) {
                        AccessibilityNodeInfo node = input.getValueByField(AccEvent.ROOT_NODE);
                        return AccessibilityUtils.serialize(node);
                    }
                })
                .setField(AccEvent.SOURCE_NODE, new Function<Item, AccessibilityUtils.SerializedAccessibilityNodeInfo>() {
                    @Override
                    public AccessibilityUtils.SerializedAccessibilityNodeInfo apply(UQI uqi, Item input) {
                        AccessibilityNodeInfo node = input.getValueByField(AccEvent.SOURCE_NODE);
                        return AccessibilityUtils.serialize(node);
                    }
                })
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(AccEvent.TIME_CREATED, Duration.minutes(10))))
                .localGroupBy("time_round")
                .forEach(IOOperators.uploadToDropbox(new Function<Item, String>() {
                    @Override
                    public String apply(UQI uqi, Item input) {
                        return participantId + "/TextEntry_" + input.getValueByField(Item.TIME_CREATED) + ".json";
                    }
                }, true));

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
