package edu.cmu.chimps.friendship_study.reminders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Reminder {


	public Integer id;
	public String notifTitle; // Title to show on notification
	public String notifText;  // Text of notification
	public Integer hour;  // Hour of the day to deliver reminder
	public Integer minute; // Minute of the hour to deliver reminder
	public String url;
	public Integer type;
	public boolean answeredToday;

	private static final String KEY_ID = "id";
	private static final String KEY_HOUR = "hour";
	private static final String KEY_MIN = "minute";
	private static final String KEY_TITLE = "title";
	private static final String KEY_TEXT = "text";
	private static final String KEY_URL = "url";
	private static final String KEY_ANSWERED_TODAY = "answered_today";
	private static final String KEY_TYPE = "type";
	
	public Reminder(){
		Random r = new Random();
		this.id = r.nextInt();
	}
	
	public JSONObject toJson(){
		
		JSONObject json = new JSONObject();
		try {
			json.put(KEY_ID, id);
			json.put(KEY_HOUR, hour);
			json.put(KEY_MIN, minute);
			json.put(KEY_TITLE, notifTitle);
			json.put(KEY_TEXT, notifText);
			json.put(KEY_URL,url);
			json.put(KEY_TYPE, type);
			json.put(KEY_ANSWERED_TODAY, answeredToday);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public void fromJson(JSONObject json){
		try {
			this.id = json.getInt(KEY_ID);
			this.hour = json.getInt(KEY_HOUR);
			this.minute = json.getInt(KEY_MIN);
			this.notifTitle = json.getString(KEY_TITLE);
			this.notifText = json.getString(KEY_TEXT);
			this.url = json.getString(KEY_URL);
			this.answeredToday = json.getBoolean(KEY_ANSWERED_TODAY);
			this.type = json.getInt(KEY_TYPE);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
