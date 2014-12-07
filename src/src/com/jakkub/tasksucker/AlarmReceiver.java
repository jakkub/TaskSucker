package com.jakkub.tasksucker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import com.uottawa.notesgo.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    	// Get Task Id
    	long id = intent.getLongExtra(MainActivity.EXTRA_ALARM_ID, 1);
    	
    	// Get Task
	    DbHelper mDbHelper = DbHelper.getInstance(context);
	    Task task = mDbHelper.getTask(id);
	    
	    // Build Notification
	    NotificationCompat.Builder mBuilder =
	            new NotificationCompat.Builder(context)
	            .setSmallIcon(R.drawable.ic_stat_notify_alert)
	            .setContentTitle(task.GetTitle())
	            .setContentText(task.GetDeadlineAsString(DateFormat.is24HourFormat(context)));
	    
	    // Set Auto Cancel after click
	    mBuilder.setAutoCancel(true);
	    
	    // Set default sounds, vibrations and LED indicator
	    mBuilder.setDefaults(Notification.DEFAULT_ALL);
	    
	    // Creates an explicit intent for an Activity in your app
	    Intent resultIntent = new Intent(context, MainActivity.class);

	    // The stack builder object will contain an artificial back stack for the
	    // started Activity.
	    // This ensures that navigating backward from the Activity leads out of
	    // your application to the Home screen.
	    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
	    // Adds the back stack for the Intent (but not the Intent itself)
	    stackBuilder.addParentStack(MainActivity.class);
	    // Adds the Intent that starts the Activity to the top of the stack
	    stackBuilder.addNextIntent(resultIntent);
	    PendingIntent resultPendingIntent =
	            stackBuilder.getPendingIntent(
	                0,
	                PendingIntent.FLAG_UPDATE_CURRENT
	            );
	    mBuilder.setContentIntent(resultPendingIntent);
	    NotificationManager mNotificationManager =
	        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    // mId allows you to update the notification later on.
	    mNotificationManager.notify((int) id, mBuilder.build());

    }
}