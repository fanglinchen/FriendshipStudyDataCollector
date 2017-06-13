package edu.cmu.chimps.friendship_study.reminders;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.cmu.chimps.friendship_study.Constants;
import edu.cmu.chimps.friendship_study.ExceptionHandler;
import edu.cmu.chimps.friendship_study.QualtricsActivity;
import edu.cmu.chimps.friendship_study.R;


public class MissedSurveyListActivity extends ListActivity {

	private ArrayList<Reminder> reminders = new ArrayList<Reminder>();
	private ReminderAdapter mReminderAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this)); // Set up the default exception handler for unexpected exception
		this.setContentView(R.layout.activity_reminder_list);

		ReminderManager mReminderManager = new ReminderManager(this);
		reminders = mReminderManager.getAllReminders();

		mReminderAdapter = new ReminderAdapter(this, 0, reminders);
		this.getListView().setAdapter(mReminderAdapter);;
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
    	{
    		public void onItemClick(AdapterView parent, View v, int position, long id)
    		{
				final Intent intent = new Intent(MissedSurveyListActivity.this, QualtricsActivity.class);
				intent.putExtra(Constants.URL.KEY_SURVEY_URL, Constants.URL.DAILY_EMA_URL);
				startActivity(intent);
    		}
    	});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		ReminderManager mReminderManager = new ReminderManager(this);
		reminders = mReminderManager.getAllReminders();

		mReminderAdapter.notifyDataSetChanged();
	}
	
	private class ReminderAdapter extends ArrayAdapter<Reminder> {
		private Context mContext;
	    private LayoutInflater inflater;

	    ReminderAdapter(Context context, int resource,
						ArrayList<Reminder> objects) {
			super(context, resource, objects);
			mContext = context;
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

	    public int getCount() {
	        return reminders.size();
	    }

	    @NonNull
		public View getView(int position, View convertView, @NonNull ViewGroup parent) {
			View v;

			Reminder item = reminders.get(position);

			if (convertView == null) {
	            	v = inflater.inflate(R.layout.item_reminder, null);

			} else {
	                v = convertView;
			}

			TextView header = (TextView) v.findViewById(R.id.text1);

			SimpleTime sd = new SimpleTime(item.hour, item.minute);
			header.setText(sd.toString(true));

			return v;

	    }
		
		
	}
}
