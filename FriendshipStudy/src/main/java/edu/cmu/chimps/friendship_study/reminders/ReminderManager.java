package edu.cmu.chimps.friendship_study.reminders;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.privacystreams.utils.Duration;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import edu.cmu.chimps.friendship_study.Constants;
import edu.cmu.chimps.friendship_study.QualtricsActivity;
import edu.cmu.chimps.friendship_study.Utils;
import edu.cmu.chimps.friendship_study.R;


public class ReminderManager extends BroadcastReceiver {

	public static final int REMINDER_TYPE_DAILY = 1;
	public static final int REMINDER_TYPE_WEEKLY = 2;
	public static final int REMINDER_TYPE_DAILY_RANDOM = 3;


	public static final String KEY_REMINDER_ACTION = "REMINDER_ACTION";
	public static final String KEY_ALARM_TYPE = "alarm_type";
	public static final String KEY_REMINDER_ID = "reminder_id";
	public static final String ALARM_TYPE_REMINDER = "alarm_type_reminder";
	
	private static final String PREF_SAVED_REMINDERS = "preference_saved_reminders";


	private String reuInitial;
	private String nreuInitial;

	private Context mContext;

	public void deliverNotification(Intent intent){
		Reminder reminder = this.getReminder(intent.getExtras().getInt(KEY_REMINDER_ID));
		Intent surveyIntent = new Intent();
		surveyIntent.setClass(mContext, QualtricsActivity.class);
		surveyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this is required for calling an activity when outside of an activity
		surveyIntent.putExtra(Constants.URL.KEY_SURVEY_URL,reminder.url);
		surveyIntent.putExtra(KEY_REMINDER_ID,reminder.id);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext.getApplicationContext(),
				reminder.id, surveyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification noti = new Notification.Builder(mContext)
				.setContentTitle(reminder.notifTitle)
				.setContentText(reminder.notifText)
				.setSmallIcon(R.drawable.heart)
				.setDefaults(Notification.DEFAULT_ALL)
				.setAutoCancel(true)
				.setContentIntent(contentIntent)
				.build();

		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(reminder.id, noti);
	}
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			mContext = context;
			unscheduleAllReminders();
//			Thread.sleep(Duration.minutes(2));
			scheduleAllReminders();

			if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
				Utils.startTracking(context);
			} else if (intent.getAction().equals(KEY_REMINDER_ACTION)) {
				// Deliver a notification
				deliverNotification(intent);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public ReminderManager(){
		// This is only here for BroadcastReceiver
	}

	public ReminderManager(Context context){
		mContext = context;
		reuInitial = Utils.randomlySelectFriendInitial(true,context);
		nreuInitial = Utils.randomlySelectFriendInitial(false,context);

	}
	// Only called once.
	public void initialize(){
		removeAllReminders();
		saveAllReminders(setupSurveyReminders());
		// setup alarms
		scheduleAllReminders();
	}

	public ArrayList<Reminder> setupSurveyReminders(){

		ArrayList<Reminder> reminders = new ArrayList<>();

		// 20:00 pm everyday.
		Reminder endOfTheDaySurveyReminder = new Reminder();
		endOfTheDaySurveyReminder.hour = 20;
		endOfTheDaySurveyReminder.minute = 0;
		endOfTheDaySurveyReminder.type = REMINDER_TYPE_DAILY;
		endOfTheDaySurveyReminder.url = Constants.URL.END_OF_THE_DAY_EMA_URL+"&Source="+reuInitial+"&OldFriend="+nreuInitial;
		endOfTheDaySurveyReminder.notifText = "Self report";
		endOfTheDaySurveyReminder.notifTitle = "Survey";

		// Randomly timed.
		Reminder dailyRandomSurveyReminder = new Reminder();
		dailyRandomSurveyReminder.type = REMINDER_TYPE_DAILY_RANDOM;
		Random r = new Random();
//		dailyRandomSurveyReminder.hour = r.nextInt(22 - 10) + 10;
//		dailyRandomSurveyReminder.minute = r.nextInt(60);
		dailyRandomSurveyReminder.hour = 22;
		dailyRandomSurveyReminder.minute = 51;
		dailyRandomSurveyReminder.url = Constants.URL.DAILY_EMA_URL+"&Source="+reuInitial+"&OldFriend="+nreuInitial;
		dailyRandomSurveyReminder.notifText = "Self report";
		dailyRandomSurveyReminder.notifTitle = "Survey";

		// Sunday 20:00
		Reminder weeklySurveyReminder = new Reminder();
		weeklySurveyReminder.hour = 20;
		weeklySurveyReminder.minute = 0;
		weeklySurveyReminder.type = REMINDER_TYPE_WEEKLY;
		weeklySurveyReminder.url = Constants.URL.WEEKLY_EMA_URL+"&Source="+reuInitial+"&OldFriend="+nreuInitial;
		weeklySurveyReminder.notifText = "Self report";
		weeklySurveyReminder.notifTitle = "Survey";

		reminders.add(endOfTheDaySurveyReminder);
		reminders.add(dailyRandomSurveyReminder);
		reminders.add(weeklySurveyReminder);

		return reminders;
	}


	public void scheduleReminder(Reminder reminder){
		AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(mContext, ReminderManager.class);
		i.setAction(KEY_REMINDER_ACTION);
		i.putExtra(KEY_ALARM_TYPE, ALARM_TYPE_REMINDER);
		i.putExtra(KEY_REMINDER_ID, reminder.id);
		PendingIntent pi = PendingIntent.getBroadcast(mContext, reminder.id, i, PendingIntent.FLAG_UPDATE_CURRENT); // identified by reminder ID, so only one alarm per reminder

		Date deliveryTime = getNextOccurrence(reminder);

		switch (reminder.type){
			case REMINDER_TYPE_DAILY:
				mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, deliveryTime.getTime(), Duration.days(1), pi); // repeat daily
				break;
			case REMINDER_TYPE_WEEKLY:
				mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, deliveryTime.getTime(), Duration.days(7), pi); // repeat weekly
				break;
			case REMINDER_TYPE_DAILY_RANDOM:
				mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, deliveryTime.getTime(), Duration.days(1), pi); // repeat daily
				break;
			default:
				break;
		}

	}

	public void unscheduleAllReminders(){
		ArrayList<Reminder> reminders = getAllReminders();
		for(Reminder reminder: reminders){
			unscheduleReminder(reminder);
		}
	}

	public void unscheduleReminder(Reminder reminder){
		AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(mContext, ReminderManager.class);
		i.setAction(KEY_REMINDER_ACTION);
		i.putExtra(KEY_ALARM_TYPE, ALARM_TYPE_REMINDER);
		i.putExtra(KEY_REMINDER_ID, reminder.id);
		PendingIntent pi = PendingIntent.getBroadcast(mContext, reminder.id, i, PendingIntent.FLAG_UPDATE_CURRENT); // identified by reminder ID, so only one alarm per reminder

		mAlarmManager.cancel(pi);
	}
	
	private Date getNextOccurrence(Reminder reminder){
		Calendar setTo = Calendar.getInstance();
		setTo.set(Calendar.SECOND, 0);
		Calendar now = Calendar.getInstance();

		switch (reminder.type){
			case REMINDER_TYPE_DAILY:
				setTo.set(Calendar.HOUR_OF_DAY, reminder.hour);
				setTo.set(Calendar.MINUTE, reminder.minute);
				if (now.getTimeInMillis() > setTo.getTimeInMillis()){
					// previous time today, so set for tomorrow
					setTo.add(Calendar.DAY_OF_YEAR, 1);
				}
				Log.e("daily",setTo.getTime().toString());
				break;
			case REMINDER_TYPE_DAILY_RANDOM:
				setTo.set(Calendar.HOUR_OF_DAY, reminder.hour);
				setTo.set(Calendar.MINUTE, reminder.minute);
				if (now.getTimeInMillis() > setTo.getTimeInMillis()){
					// previous time today, so set for tomorrow
					setTo.add(Calendar.DAY_OF_YEAR, 1);
				}
				Log.e("random",setTo.getTime().toString());
				break;

			case REMINDER_TYPE_WEEKLY:
				setTo.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
				setTo.set(Calendar.HOUR_OF_DAY, reminder.hour);
				setTo.set(Calendar.MINUTE, reminder.minute);
				if (now.getTimeInMillis() > setTo.getTimeInMillis()){
					// set for next week.
					setTo.add(Calendar.DAY_OF_YEAR, 7);
				}
				Log.e("weekly",setTo.getTime().toString());
				break;
			default:
				break;
		}

		return setTo.getTime();
	}
	
	public Reminder getReminder(Integer id){
		ArrayList<Reminder> reminders =  getAllReminders();
		for(Reminder it : reminders){
			if (it.id.equals(id)){
				return it;
			}
		}
		return null;
	}


	public void removeReminder(Reminder reminder){
		ArrayList<Reminder> reminders = getAllReminders();
		for(Reminder it : reminders){
			if (it.id.equals(reminder.id)){
				this.unscheduleReminder(it);
				reminders.remove(it);
				break;
			}
		}
		saveAllReminders(reminders);
	}

//	private boolean reminderHappenedToday(){
//
//	}
//
//	public ArrayList<Reminder> showMissedSurveys(){
//
//		ArrayList<Reminder> missedReminders = new ArrayList<>();
//
//		ArrayList<Reminder> reminders = getAllReminders();
//		for(Reminder reminder:reminders){
//			if(!reminder.answeredToday && reminder.minute   ){
//				missedReminders.add()
//			}
//
//		}
//
//	}


	public void scheduleAllReminders(){
		ArrayList<Reminder> reminders = getAllReminders();
		for (Reminder it : reminders){
			scheduleReminder(it);
		}
	}

	public ArrayList<Reminder> getAllReminders(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		ArrayList<Reminder> reminders = new ArrayList<>();
		try {
			JSONArray jsons = new JSONArray(prefs.getString(PREF_SAVED_REMINDERS, "[]"));
			for(int i = 0; i < jsons.length(); i++){
				Reminder r = new Reminder();
				r.fromJson(jsons.getJSONObject(i));
				reminders.add(r);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return reminders;
	}

	private void saveAllReminders(ArrayList<Reminder> reminders){
		JSONArray jsons = new JSONArray();
		for(Reminder it : reminders){
			jsons.put(it.toJson());
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		prefs.edit().putString(PREF_SAVED_REMINDERS, jsons.toString()).apply();
		
	}

	public void removeAllReminders(){
		ArrayList<Reminder> reminders = getAllReminders();
		for(Reminder it : reminders){
			unscheduleReminder(it);
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		prefs.edit().putString(PREF_SAVED_REMINDERS, "[]").apply();
	}
	
}
