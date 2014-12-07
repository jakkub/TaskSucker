package com.jakkub.tasksucker;


import java.util.Calendar;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import com.uottawa.notesgo.R;


public class NewTaskActivity extends Activity {
	
	Menu myMenu;
	Boolean firstTime = true;
	
	// Video Control Variables
	static final int REQUEST_VIDEO_CAPTURE = 1;
	VideoView videoView;
	TextView noVideoTextView;
	ImageView playVideoImageView;
	Uri videoUri;
	String video = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_task);
		
		if(savedInstanceState == null)
		  firstTime = false;
		
		// Show the Up button in the action bar.
		setupActionBar();

		// Set up Text Change Listener
		EditText ed = (EditText) findViewById(R.id.new_task_title);
		ed.addTextChangedListener(new TextWatcher() {

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        }
	        
	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	        	// Do nothing if this is the first time
	        	if (firstTime)
	        	{
	        		firstTime = false;
	        		return;
	        	}
	        	
	        	// Disable Save button if title is empty
	        	if (s == null || s.length() == 0) {
	        		myMenu.findItem(R.id.action_save_new).setEnabled(false);
	            }
	            else {
	            	myMenu.findItem(R.id.action_save_new).setEnabled(true);
	            }
	        }
	    });
		
		// Load views
		videoView = (VideoView)findViewById(R.id.new_task_videoview);
		noVideoTextView = (TextView)findViewById(R.id.new_task_novideo);
		playVideoImageView = (ImageView)findViewById(R.id.new_task_playvideo);
		
		// Set Touch listener for video view
		videoView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				videoPausePlay();
				return false;
			}

        });
		
		// Set looping for video
		videoView.setOnPreparedListener (new OnPreparedListener() {                    
		    @Override
		    public void onPrepared(MediaPlayer mp) {
		        mp.setLooping(true);
		    }
		});

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_task, menu);
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// Save reference to the menu
		myMenu = menu;
				
		// Get title from EditText
		EditText ed = (EditText) findViewById(R.id.new_task_title);
		
		// Enable/Disable New Task Button
		if (ed.getText() == null || ed.getText().length() == 0) 
		{
			menu.findItem(R.id.action_save_new).setEnabled(false);
		}
		else
		{
			menu.findItem(R.id.action_save_new).setEnabled(true);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.action_save_new:
			addNewTask();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	// Setup video recorder
	public void showVideoRecorder(View v) {
	    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
	    }
	}
	
	// Load video when it's done
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		    if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
		        videoUri = intent.getData();
		        videoView.setVideoURI(videoUri);
		        videoView.seekTo(1); 
		        videoView.setVisibility(View.VISIBLE);
		        noVideoTextView.setVisibility(View.INVISIBLE);
		        playVideoImageView.setVisibility(View.VISIBLE);
		        
		    }
		}
	
	public void videoPausePlay() {
		if (videoView.isPlaying())
		{
			playVideoImageView.setVisibility(View.VISIBLE);
			videoView.pause();
		}
		else
		{
			playVideoImageView.setVisibility(View.INVISIBLE);
			videoView.seekTo(videoView.getCurrentPosition());
			videoView.start();  
		}
	}
	
	public void discardVideo(View v) {
		videoView.stopPlayback();  
		videoView.setVideoURI(null);
	    videoUri = null;
	    videoView.setVisibility(View.INVISIBLE);
	    noVideoTextView.setVisibility(View.VISIBLE);
	    playVideoImageView.setVisibility(View.INVISIBLE);
	}


	public void addNewTask() 
	{
		// Get title from view
	    EditText titleEt = (EditText) findViewById(R.id.new_task_title);
	    String title = titleEt.getText().toString();
	    
	    // Get priority from view
	    CheckBox priorityCb = (CheckBox) findViewById(R.id.new_task_priority);
	    Boolean priority = priorityCb.isChecked();
	    
	    // Get date from view
	    Button dateBtn = (Button) findViewById(R.id.new_task_set_date);
	    String date = dateBtn.getText().toString();
	    
	    // Get time from view
	    Button timeBtn = (Button) findViewById(R.id.new_task_set_time);
	    String time = timeBtn.getText().toString();
	    
	    // Parse date and time
	    Calendar deadline = MainActivity.parseDateTime(this, date, time);
	    
	    // Get video URI
	    if (videoUri != null)
	    {
	    	video = videoUri.toString();
	    }
	    
	    // Generate new task object
	    Task newTask = new Task();
	    
	    newTask.SetTitle(title);
	    newTask.SetDeadline(deadline);
	    newTask.SetPriority(priority);
	    newTask.SetDone(false);
	    newTask.SetVideo(video);
	    
	    // Add task to database
	    DbHelper mDbHelper = DbHelper.getInstance(this);
	    long id = mDbHelper.addTask(newTask);
	    
	    // Set alarm
	    if (deadline != null)
	    {
		    MainActivity.setAlarm(getApplicationContext(), deadline, id);
	    }
	    
		// Create new intent
		Intent intent = new Intent(this, MainActivity.class);

	    // Go back to main screen
	    startActivity(intent);
	}
	
	

}



