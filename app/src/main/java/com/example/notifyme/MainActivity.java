package com.example.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button update, cancel;
    private final String NOTI_CHANNEL_ID = "primary_notification_channel";
    private String ACTION_UPDATE = BuildConfig.APPLICATION_ID+"_UPDATE_NOTIFICATION";
    private static NotificationManager nManager;
    private static final int NOTI_IDINT = 1;
    UpdateReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new UpdateReceiver();
        this.registerReceiver(receiver, new IntentFilter(ACTION_UPDATE));

        button = findViewById(R.id.button);
        cancel = findViewById(R.id.button2);
        update = findViewById(R.id.button3);

        setButtonState(true, false, false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotifiction();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });

    }

    private void updateNotification() {
        if (nManager != null) {
            Bitmap andImg = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
            NotificationCompat.Builder builder = getNotiBuilder();
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(andImg)
                    .setBigContentTitle("This is updated titl."));
            nManager.notify(NOTI_IDINT, builder.build());
            setButtonState(false, false, true);
        }
    }

    private void setButtonState(Boolean one, Boolean two, Boolean three) {
        button.setEnabled(one);
        update.setEnabled(two);
        cancel.setEnabled(three);
    }

    private void cancelNotifiction() {
        if (nManager != null) {
            nManager.cancel(NOTI_IDINT);
            setButtonState(true, false, false);
        }

    }

    public void createNotification() {
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel channel = new NotificationChannel(NOTI_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.RED);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setDescription("Notification from Mascot");
            nManager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder getNotiBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTI_CHANNEL_ID)
                .setContentTitle("This is the title")
                .setContentText("This is description text.")
                .setSmallIcon(R.drawable.ic_stat_name);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTI_IDINT, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent).setAutoCancel(true);
        return builder;
    }

    public void sendNotification() {

        createNotification();
        NotificationCompat.Builder builder = getNotiBuilder();
        Intent intent = new Intent(ACTION_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTI_IDINT, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.addAction(R.drawable.ic_action_icons,"Updaet Notification", pendingIntent);
        nManager.notify(NOTI_IDINT, builder.build());

        setButtonState(false, true, true);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}