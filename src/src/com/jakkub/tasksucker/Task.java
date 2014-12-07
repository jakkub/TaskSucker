package com.jakkub.tasksucker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Task {
	
	private int Id;
	private String Title;
	private Calendar Deadline;
	private boolean Priority;
	private boolean Done;
	private String Video;
	
	public Task(int id, String title, Calendar deadline, boolean priority, boolean done, String video) {
		
		Id = id;
		Title = title;
		Deadline = deadline;
		Priority = priority;
		Done = done;
		Video = video;
		
	}
	
	public Task(String title, Calendar deadline, boolean priority, boolean done) {
		
		Title = title;
		Deadline = deadline;
		Priority = priority;
		Done = done;
		
	}
	
	public Task() {
		Deadline = new GregorianCalendar();
	}
	
	public int GetId() {
		return Id;
	}
	
	public void SetId(int id) {
		Id = id;
	}
	
	public String GetTitle() {
		return Title;
	}
	
	public void SetTitle(String title) {
		Title = title;
	}
	
	public String GetVideo() {
		return Video;
	}
	
	public void SetVideo(String video) {
		Video = video;
	}
	
	public Calendar GetDeadline() {
		return Deadline;
	}
	
	public void SetDeadline(Calendar deadline) {
		Deadline = deadline;
	}
	
	public boolean GetPriority() {
		return Priority;
	}
	
	public void SetPriority(boolean priority) {
		Priority = priority;
	}
	
	public boolean GetDone() {
		return Done;
	}
	
	public void SetDone(boolean done) {
		Done = done;
	}
	
	public long GetUnixDeadline() {
		if (Deadline != null)
			return Deadline.getTimeInMillis();
		else
			return -1;
	}
	
	public void SetUnixDeadline(long deadline) {
		if (deadline > -1)
			Deadline.setTimeInMillis(deadline);
		else
			Deadline = null;
	}
	
	public int GetIntPriority() {
		return Priority ? 1 : 0;
	}
	
	public void SetIntPriority(int priority) {
		if (priority >= 1)
			Priority = true;
		else
			Priority = false;
	}
	
	public int GetIntDone() {
		return Done ? 1 : 0;
	}
	
	public void SetIntDone(int done) {
		if (done >= 1)
			Done = true;
		else
			Done = false;
	}
	
	public String GetDeadlineAsString(boolean is24hrsFormat) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(is24hrsFormat ? "dd MMM yyyy H:mm" : "dd MMM yyyy h:mm a", Locale.US);
		return dateFormat.format(Deadline.getTime());
	}
	
	public String GetDateAsString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
		return dateFormat.format(Deadline.getTime());
	}
	
	public String GetTimeAsString(boolean is24hrsFormat) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(is24hrsFormat ? "H:mm" : "h:mm a", Locale.US);
		return dateFormat.format(Deadline.getTime());
	}



}
