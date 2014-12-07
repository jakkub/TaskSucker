package com.jakkub.tasksucker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	
	// Singleton instance of the DbHelper class
	private static DbHelper sInstance = null;
	

    // Database version
    public static final int DATABASE_VERSION = 5;
    
    // Database name
    public static final String DATABASE_NAME = "FeedReader.db";
    
	// Table name
	public static final String TABLE_NAME = "tasks";
	
	// Column names
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_DEADLINE = "deadline";
	public static final String COLUMN_NAME_PRIORITY = "priority";
	public static final String COLUMN_NAME_DONE = "done";
	public static final String COLUMN_NAME_VIDEO = "video";
	
	// All column names
	public static final String[] COLUMNS = new String[] { COLUMN_NAME_ID, COLUMN_NAME_TITLE, COLUMN_NAME_DEADLINE, COLUMN_NAME_PRIORITY, COLUMN_NAME_DONE, COLUMN_NAME_VIDEO };
    
	// Create table SQL query
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + TABLE_NAME + " (" +
        COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_NAME_TITLE + " TEXT, "+
        COLUMN_NAME_DEADLINE + " INTEGER, " +
        COLUMN_NAME_PRIORITY + " INTEGER, " +
        COLUMN_NAME_DONE + " INTEGER, " +
        COLUMN_NAME_VIDEO + " TEXT )";

    // Drop table SQL query
    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + TABLE_NAME;
    
    // Get singleton instance of the class
    public static DbHelper getInstance(Context context) 
    {
    	// Use Application context instead of Activity context
        if (sInstance == null) 
        {
          sInstance = new DbHelper(context.getApplicationContext());
        }

        return sInstance;
      }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    // Create table
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(SQL_CREATE_ENTRIES);
    }
    
    // Upgrade table
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    
    // Add new task
    public long addTask(Task task)
    {
    	// Get reference to writable database
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	// Get values to insert
    	ContentValues values = new ContentValues();
    	values.put(COLUMN_NAME_TITLE, task.GetTitle());
    	values.put(COLUMN_NAME_DEADLINE, task.GetUnixDeadline());
    	values.put(COLUMN_NAME_PRIORITY, task.GetIntPriority());
    	values.put(COLUMN_NAME_DONE, task.GetIntDone());
    	values.put(COLUMN_NAME_VIDEO, task.GetVideo());
    	
    	// Insert values
    	long id = db.insert(TABLE_NAME, null, values);

        // Close database
        db.close(); 
        
        // Return added Id
        return id;
    }
    
    
    // Get a single task
    public Task getTask(long id){

    	// Get reference to readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Build query
        Cursor cursor = db.query(TABLE_NAME, // table
                		 		 COLUMNS, // column names
                		 		 " id = ?", // selections 
                		 		 new String[] { String.valueOf(id) }, // selections args
                		 		 null, // group by
                		 		 null, // having
                		 		 null, // order by
                		 		 null); // limit

        // If there are results, get the first one
        if (cursor != null)
            cursor.moveToFirst();
 
        // Build task object
        Task item = new Task();
        
        item.SetId(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID)));
		item.SetTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TITLE)));
		item.SetUnixDeadline(cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_DEADLINE)));
		item.SetIntPriority(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PRIORITY)));
		item.SetIntDone(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DONE)));
		item.SetVideo(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_VIDEO)));
		
		// Close cursor
		cursor.close();
				
		// Close database
        db.close(); 
		
        // Return task
        return item;
    }
    
    
    // Get all tasks
    public ArrayList<Task> getAllTasks() 
    {
    	// Get reference to readable database
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	// Create new list of tasks
		ArrayList<Task> tasks = new ArrayList<Task>();

		// Create cursor with all rows
		Cursor cursor = db.query(TABLE_NAME, // table
								 COLUMNS, // column names
								 null, // selection
								 null, // selections args
								 null, // group by
								 null, // having
								 COLUMN_NAME_DEADLINE + " ASC"); // order by

		// Go over each row, build task and add it to the list
		if (cursor.moveToFirst()) 
		{
			while (!cursor.isAfterLast()) 
			{
				Task item = new Task();

				item.SetId(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID)));
				item.SetTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TITLE)));
				item.SetUnixDeadline(cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_DEADLINE)));
				item.SetIntPriority(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PRIORITY)));
				item.SetIntDone(cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_DONE)));
				item.SetVideo(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_VIDEO)));
				
				tasks.add(item);
				
				cursor.moveToNext();
			}
		}
		
		// Close cursor
		cursor.close();
		
		// Close database
        db.close(); 
        
        // Return all tasks
		return tasks;
	}
    
    
    // Update single task
    public int updateTask(Task task) {
 
    	// Get reference to writable database
        SQLiteDatabase db = this.getWritableDatabase();
 
        // Get values to update
        ContentValues values = new ContentValues();
    	values.put(COLUMN_NAME_TITLE, task.GetTitle());
    	values.put(COLUMN_NAME_DEADLINE, task.GetUnixDeadline());
    	values.put(COLUMN_NAME_PRIORITY, task.GetIntPriority());
    	values.put(COLUMN_NAME_DONE, task.GetIntDone());
    	values.put(COLUMN_NAME_VIDEO, task.GetVideo());
 
        // Update row
        int i = db.update(TABLE_NAME, //table
        				  values, // column/value
        				  COLUMN_NAME_ID + " = ?", // selections
        				  new String[] { String.valueOf(task.GetId()) }); //selection args
 
        // Close database
        db.close();
 
        return i;
    }
    
    
    // Delete single task
    public void deleteTask(Task task) {
 
    	// Get reference to writable database
        SQLiteDatabase db = this.getWritableDatabase();
 
        // Delete
        db.delete(TABLE_NAME,
        		  COLUMN_NAME_ID + " = ?",
        		  new String[] { String.valueOf(task.GetId()) });
 
        // Close database
        db.close();
    }
}