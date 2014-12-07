package com.jakkub.tasksucker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import com.uottawa.notesgo.R;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	Button button;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		int year;
		int month;
		int day;
		final Calendar c = Calendar.getInstance();
		
		button = (Button) getActivity().findViewById(R.id.new_task_set_date);

		// Check if the date was already set
		if (button.getText() != getString(R.string.new_task_set_date)) 
		{
			// Use date from button
			try 
			{
				Date date;
			    date = new SimpleDateFormat("dd MMM yyyy", Locale.US).parse((String) button.getText());
			    
			    c.setTime(date);
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
			}
		}
		
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
		
	public void onDateSet(DatePicker view, int year, int month, int day) {
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		
		SimpleDateFormat date_format = new SimpleDateFormat("dd MMM yyyy", Locale.US);
	    button.setText(date_format.format(c.getTime()));
	}
}