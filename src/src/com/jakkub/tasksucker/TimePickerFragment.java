package com.jakkub.tasksucker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;
import com.uottawa.notesgo.R;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	
	Button button;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		int hour;
		int minute;
		final Calendar c = Calendar.getInstance();
		
		button = (Button) getActivity().findViewById(R.id.new_task_set_time);
		
		// Check if the time was already set
		if (button.getText() != getString(R.string.new_task_set_time)) 
		{
			// Use time from activity
			try 
			{
				Date date;
				date = new SimpleDateFormat(DateFormat.is24HourFormat(getActivity()) ? "H:mm" : "h:mm a", Locale.US).parse((String) button.getText());
			    
			    c.setTime(date);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
		} 

		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}
		
	public void onTimeSet(TimePicker view, int hour, int minute) {
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		
		SimpleDateFormat date_format = new SimpleDateFormat(DateFormat.is24HourFormat(getActivity()) ? "H:mm" : "h:mm a", Locale.US);
	    button.setText(date_format.format(c.getTime()));
	}
}