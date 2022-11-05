package ar.edu.itba.houseitba.Notifications;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ar.edu.itba.houseitba.Activities.MainActivity;
import ar.edu.itba.houseitba.R;

public class Notification {

    public enum notificationIDs{
        NEW_DEVICE,
        DELETE_DEVICE,
        NEW_FAV,
        DELETE_FAV,
        DEVICE_STATE_CHANGE,
        NEW_ROUTINE,
        DELETE_ROUTINE
    }

    public enum objectTypes{
        DEVICE,
        ROUTINE
    }

    public static String getChannelID(Context ctx, objectTypes type){
        switch(type){
            case DEVICE:
                return ctx.getString(R.string.notifications_channel_id_device);
            case ROUTINE:
                return ctx.getString(R.string.notifications_channel_id_routine);
            default:
                return "";
        }
    }

    public static String getChannelDescription(Context ctx, objectTypes type){
        switch(type){
            case DEVICE:
                return ctx.getString(R.string.notifications_channel_description_device);
            case ROUTINE:
                return ctx.getString(R.string.notifications_channel_description_routine);
            default:
                return "";
        }
    }

    public static String getChannelName(Context ctx, objectTypes type){
        switch(type){
            case DEVICE:
                return ctx.getString(R.string.notifications_channel_name_device);
            case ROUTINE:
                return ctx.getString(R.string.notifications_channel_name_routine);
            default:
                return "";
        }
    }

    public static void CreateCustomNotification(Context context, String channelId, String title, String text, notificationIDs notificationID, int navState){
        // Create an Intent for the activity you want to start
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("navState", navState);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(mainIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent mainPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(mainPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationID.ordinal(), builder.build());
    }

    public static void CreateCustomNotification(Context context, String channelId, String title, String text, notificationIDs notificationID, Intent intent){
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent mainPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(mainPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationID.ordinal(), builder.build());
    }
}
