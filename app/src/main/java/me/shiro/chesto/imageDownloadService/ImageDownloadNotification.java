package me.shiro.chesto.imageDownloadService;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.Random;

import me.shiro.chesto.ChestoApplication;
import me.shiro.chesto.R;

/**
 * Created by Shiro on 5/14/2016.
 * TODO: Consider using NotificationCompat.InboxStyle
 */
class ImageDownloadNotification {

    private static final int QUEUE_NOTIFICATION_ID = 0;
    private static final NotificationManager NOTIFICATION_MANAGER = initNotificationManager();

    private static NotificationManager initNotificationManager() {
        return (NotificationManager) ChestoApplication.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private final NotificationCompat.Builder mQueueNotifBuilder;
    private final NotificationCompat.Builder mFinishedNotifBuilder;
    private final Random random;
    private int queuedCount;
    private int finishedCount;

    ImageDownloadNotification(Context context) {
        mQueueNotifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Chesto")
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setOngoing(true);
        mFinishedNotifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Chesto")
                .setContentText("Download Finished")
                .setColor(ContextCompat.getColor(context, R.color.colorAccent));
        random = new Random();
        queuedCount = 0;
        finishedCount = 0;
    }

    void notifyStarted() {
        ++queuedCount;
        updateNotification();
    }

    void notifyFinished() {
        ++finishedCount;
        updateNotification();
    }

    private void updateNotification() {
        if (finishedCount == queuedCount) {
            mFinishedNotifBuilder
                    .setNumber(finishedCount)
                    .setWhen(System.currentTimeMillis());
            NOTIFICATION_MANAGER.notify(random.nextInt(), mFinishedNotifBuilder.build());

            finishedCount = 0;
            queuedCount = 0;
            NOTIFICATION_MANAGER.cancel(QUEUE_NOTIFICATION_ID);
        } else {
            mQueueNotifBuilder
                    .setContentText("Downloading Image" + (queuedCount > 1 ? "s" : ""))
                    .setProgress(queuedCount, finishedCount, finishedCount == 0)
                    .setContentInfo(finishedCount + "/" + queuedCount)
                    .setWhen(System.currentTimeMillis());
            NOTIFICATION_MANAGER.notify(QUEUE_NOTIFICATION_ID, mQueueNotifBuilder.build());
        }
    }
}
