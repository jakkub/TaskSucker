package com.jakkub.tasksucker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.uottawa.notesgo.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	public final static String EXTRA_EDIT_ID = "com.example.tasksucker.EDIT_ID";
	public final static String EXTRA_ALARM_ID = "com.example.tasksucker.ALARM_ID";

	// Create list of tasks
    ArrayList<Task> tasks = new ArrayList<Task>();
    TaskAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	    // Populate tasks list
	    DbHelper mDbHelper = DbHelper.getInstance(this);
	    tasks = mDbHelper.getAllTasks();

	    // Get List View
		ListView lv = (ListView) findViewById(R.id.listView1);

		// Populate List View with tasks
		adapter = new TaskAdapter(this, tasks);
        lv.setAdapter(adapter);
        
        // Set an Item Click Listener
        lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				openEdit(position);
			}
        	});
        
        // Register floating context menu
        registerForContextMenu(lv);
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.action_delete_all:
	    		deleteAll();
	    		return true;
	        case R.id.action_new:
	            openNew();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

        // Get state of checkbox
        CheckBox done = (CheckBox)info.targetView.findViewById(R.id.cb_done);
        
        // Change the Done/Undone title
        if (done.isChecked()) 
        {
        	menu.findItem(R.id.context_set_as_done_undone).setTitle(R.string.context_set_as_undone);
        }
        else
        {
        	menu.findItem(R.id.context_set_as_done_undone).setTitle(R.string.context_set_as_done);
        }		
    }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    long position = info.id;
	    View view = info.targetView;
	    switch (item.getItemId()) {
	    	case R.id.context_set_as_done_undone:
	    		CheckBox done = (CheckBox)view.findViewById(R.id.cb_done);
	    		done.setChecked(!done.isChecked());
	    		return true;
	        case R.id.context_edit:
	        	openEdit((int) position);
	            return true;
	        case R.id.context_delete:
	        	deleteTask((int) position);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	private void openNew() {
		Intent intent = new Intent(this, NewTaskActivity.class);
	    startActivity(intent);
	}
	
	private void openEdit(int pos) {
		// Get task id
		int id = tasks.get(pos).GetId();
		
		// Create intent
		Intent intent = new Intent(this, EditTaskActivity.class);
		
		// Put id to extra
		intent.putExtra(EXTRA_EDIT_ID, id);
		
		// Start edit activity
	    startActivity(intent);
	}
	
	private void deleteTask(int pos) {
		// Remove task from database
		DbHelper mDbHelper = DbHelper.getInstance(this);
	    mDbHelper.deleteTask(tasks.get(pos));
	    
	    // Cancel alarm if any
	    if (tasks.get(pos).GetDeadline() != null)
	    {
			cancelAlarm(getApplicationContext(), tasks.get(pos).GetId());
	    }
	    
	    // Remove task from list
	    tasks.remove(pos);
	    
	    // Invalidate adapter data set
	    adapter.notifyDataSetChanged();
	}
	
	private void deleteAll() {
		// Initialize database helper
		DbHelper mDbHelper = DbHelper.getInstance(this);
		
		// Loop through the list
		for (int i = tasks.size()-1; i >= 0; i--) 
		{
			if(tasks.get(i).GetDone()) 
			{
				// Cancel Alarm if any
				if (tasks.get(i).GetDeadline() != null)
				{
					cancelAlarm(getApplicationContext(), tasks.get(i).GetId());
				}
				
				// Remove task from database
				mDbHelper.deleteTask(tasks.get(i));
				
				// Remove task from list
				tasks.remove(tasks.get(i));
			}
		}

		// Invalidate adapter data set
	    adapter.notifyDataSetChanged();
	}
	
	
	public static void setAlarm(Context context, Calendar deadline, long id) 
    {
        Intent notifier = new Intent(context, AlarmReceiver.class);
        notifier.putExtra(EXTRA_ALARM_ID, id);
        PendingIntent throwNotification = PendingIntent.getBroadcast(context,
                (int) id, notifier, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, deadline.getTimeInMillis(), throwNotification);
    }
	
	
	public static void cancelAlarm(Context context, int id) {
		Intent notifier = new Intent(context, AlarmReceiver.class);
        notifier.putExtra(EXTRA_ALARM_ID, id);
		AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		PendingIntent throwNotification = PendingIntent.getBroadcast(context,
                (int) id, notifier, PendingIntent.FLAG_CANCEL_CURRENT);
		alarm.cancel(throwNotification);
	}
	
	public static Calendar parseDateTime(Context context, String date, String time)
	{
		Calendar deadline = new GregorianCalendar();
		
		// If date and time was not set, deadline is null
	    if ((date == context.getString(R.string.new_task_set_date)) & (time == context.getString(R.string.new_task_set_time)))
	    {
	    	deadline = null;
	    }
	    // If date was set and time was not set, deadline is at the noon of that day
	    else if ((date != context.getString(R.string.new_task_set_date)) & (time == context.getString(R.string.new_task_set_time)))
	    {
	    	// Try to parse deadline
		    try 
			{
				Date d;
				d = new SimpleDateFormat("dd MMM yyyy H:mm", Locale.US).parse(date + " 12:00");
			    
			    deadline.setTime(d);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
	    }
	    // If the date was not set and time was set, deadline is in the next 24 hours
	    else if ((date == context.getString(R.string.new_task_set_date)) & (time != context.getString(R.string.new_task_set_time)))
	    {
	    	// Try to parse deadline
		    try 
			{
		    	// Get current day as a string
		    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
				String today = dateFormat.format(deadline.getTime());
				
				// Parse and set date with current day
				Date d;
				d = new SimpleDateFormat(DateFormat.is24HourFormat(context) ? "dd MMM yyyy H:mm" : "dd MMM yyyy h:mm a", Locale.US).parse(today + " " + time);
				deadline.setTime(d);
				
				Calendar now = new GregorianCalendar();
				
				// If deadline is in the past, add 1 day
				if (now.after(deadline))
				{
					deadline.add(Calendar.DAY_OF_MONTH, 1);
				}
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
	    }
	    else
	    {
	    	// Try to parse deadline
		    try 
			{
				Date d;
				d = new SimpleDateFormat(DateFormat.is24HourFormat(context) ? "dd MMM yyyy H:mm" : "dd MMM yyyy h:mm a", Locale.US).parse(date + " " + time);
			    
			    deadline.setTime(d);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
	    }
	    return deadline;
	}

}
