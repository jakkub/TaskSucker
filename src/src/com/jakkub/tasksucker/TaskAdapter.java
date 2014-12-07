package com.jakkub.tasksucker;

import java.util.ArrayList;
 
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.uottawa.notesgo.R;
 
public class TaskAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<Task> data;
    private static LayoutInflater inflater = null;
 
    public TaskAdapter(Activity a, ArrayList<Task> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ViewHolder holder;
    	
        if(convertView == null) 
        {
        	convertView = inflater.inflate(R.layout.list_row, null);
        	
        	holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.deadline = (TextView) convertView.findViewById(R.id.tv_deadline);
            holder.done = (CheckBox) convertView.findViewById(R.id.cb_done);

            convertView.setTag(holder);
        }
        else
        {
        	holder = (ViewHolder) convertView.getTag();
        }

        // Save Checkbox Position
        holder.done.setTag(position);
        
        // Checkbox Change Listener
        holder.done.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // Update object
                data.get((Integer) buttonView.getTag()).SetDone(isChecked);
                
                // Update database
                DbHelper mDbHelper = DbHelper.getInstance(activity);
                mDbHelper.updateTask(data.get((Integer) buttonView.getTag()));
                
                // Strike thru text
                View v = (View) buttonView.getParent();
                TextView title = (TextView)v.findViewById(R.id.tv_title);
                TextView deadline = (TextView)v.findViewById(R.id.tv_deadline);
                
                if (isChecked) {
                	title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                	deadline.setPaintFlags(deadline.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                	title.setPaintFlags(title.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                	deadline.setPaintFlags(deadline.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
        
        // Create new task object
        Task task = new Task();
        
        // Get task data
        task = data.get(position);
 
        // Set all values in listview
        holder.title.setText(task.GetTitle());
        holder.done.setChecked(task.GetDone());
        
        if(task.GetDeadline() != null)
        {
        	holder.deadline.setText(task.GetDeadlineAsString(DateFormat.is24HourFormat(activity)));
        }
        else
        {
        	holder.deadline.setText(activity.getString(R.string.no_deadline));
        }
        
        // Highligh if high priority is set
        if (task.GetPriority()) {
        	holder.title.setTextColor(Color.RED);
        	holder.title.setTypeface(null, Typeface.BOLD);
        } else {
        	holder.title.setTextColor(Color.BLACK);
        	holder.title.setTypeface(null, Typeface.NORMAL);
        }

        return convertView;
    }
    
    private static class ViewHolder {
        public TextView title;
        public TextView deadline;
        public CheckBox done;
    }
  

    
}