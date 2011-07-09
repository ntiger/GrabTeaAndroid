package com.eatbang.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.eatbang.model.Friend;
import com.eatbang.model.LocationTag;
import com.eatbang.model.Participator;
import com.eatbang.model.Party;
import com.eatbang.model.TimeSlot;
import com.eatbang.model.User;

public class EatBangUtil {
	private static final String LOG_TAG = "EatBangUtil";

	public static JSONObject JSONParsing(InputStream inputStream) {
		JSONObject json = null;
		try {
			json = new JSONObject(convertStreamToString(inputStream));
		} catch (JSONException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		return json;
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
		}
		return sb.toString();
	}

	public static JSONArray JSONArrayParsing(InputStream inputStream) {
		JSONArray entries = null;
		try {
			String jsontext = new String(convertStreamToString(inputStream));
			entries = new JSONArray(jsontext);
		} catch (JSONException e) {
			Log.w(LOG_TAG, "JSONArray parsing error.");
		}
		return entries;
	}

	public static List<Party> parseParties(JSONArray entries) {
		List<Party> parties = new ArrayList<Party>();
		for (int i = 0; i < entries.length(); i++) {
			try {
				parties.add(parseParty((JSONObject) entries.get(i)));
			} catch (JSONException e) {
				Log.w(LOG_TAG, "Party list parsing error.");
			}
		}
		return parties;
	}

	private static Set<TimeSlot> parseTimeSlots(JSONArray entries) {
		Set<TimeSlot> timeSlots = new HashSet<TimeSlot>();
		for (int i = 0; i < entries.length(); i++) {
			TimeSlot timeSlot = null;
			try {
				JSONObject jsonObject = (JSONObject) entries.get(i);
				timeSlot = new TimeSlot(jsonObject.getLong("timeInLong"));
			} catch (JSONException e) {
				Log.w(LOG_TAG, "TimeSlots parsing error.");
			}
			timeSlots.add(timeSlot);
		}
		return timeSlots;
	}

	private static Set<LocationTag> parseLocationTags(JSONArray entries) {
		Set<LocationTag> tags = new HashSet<LocationTag>();
		for (int i = 0; i < entries.length(); i++) {
			JSONObject jsonObject;
			LocationTag tag = null;
			try {
				jsonObject = (JSONObject) entries.get(i);
				tag = new LocationTag(jsonObject.getString("name"),
						jsonObject.getInt("type"));
			} catch (JSONException e) {
				Log.w(LOG_TAG, "Places parsing error.");
			}
			tags.add(tag);
		}
		return tags;
	}

	private static Set<Participator> parseParticipators(JSONArray entries) {
		Set<Participator> participators = new HashSet<Participator>();
		for (int i = 0; i < entries.length(); i++) {
			Participator participator = new Participator();
			try {
				JSONObject jsonObject = (JSONObject) entries.get(i);
				participator.name = jsonObject.getString("name");
				participator.status = jsonObject.getInt("status");
				participator.email = jsonObject.getString("email");
				participator.phone = jsonObject.getString("phone");
				if (jsonObject.getBoolean("isUser")) {
					participator.avatarUri = jsonObject.getString("avatarUri");
				}

			} catch (JSONException e) {
				Log.w(LOG_TAG, "Participators parsing error.");
			}
			participators.add(participator);
		}
		return participators;
	}

	public static Party parseParty(JSONObject jsonObject) {
		Party party = new Party();
		try {
			party.editCode = jsonObject.getString("e");
		} catch (JSONException e) {
			Log.w(LOG_TAG, "No edit code available");
		}
		try {
			party.inviteCode = jsonObject.getString("i");
		} catch (JSONException e) {
			Log.w(LOG_TAG, "No invite code available");
		}
		try {
			party.showCode = jsonObject.getString("s");
			party.name = jsonObject.getString("name");
			party.description = jsonObject.getString("description");
			party.cityCode = jsonObject.getInt("cityCode");
			party.periodType = jsonObject.getInt("periodType");
			party.isPublic = jsonObject.getBoolean("isPublic");
			party.locationTags = parseLocationTags(jsonObject
					.getJSONArray("locationTags"));
			if (jsonObject.getInt("parCount") != 0) {
				party.participators = parseParticipators(jsonObject
						.getJSONArray("pars"));
			}

			party.timeSlots = parseTimeSlots(jsonObject
					.getJSONArray("timeSlots"));
		} catch (JSONException e) {
			Log.w(LOG_TAG, "Party parsing error.");
		}
		return party;
	}

	public static long convertStringToLong(String str) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd K:mm a");
		long time = 0;
		try {
			time = dateFormat.parse(str).getTime();
		} catch (ParseException e) {
			Log.e(LOG_TAG, "Parse exception");
		}
		return time;
	}

	public static String convertCalendarToString(Calendar calToday) {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		return dateFormat.format(calToday.getTime());
	}

	public static int getScreenWidth(Activity a) {
		WindowManager manage = a.getWindowManager();
		Display display = manage.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Activity a) {
		WindowManager manage = a.getWindowManager();
		Display display = manage.getDefaultDisplay();
		return display.getHeight();
	}

	public static List<Friend> parseFriends(JSONArray entries) {
		List<Friend> friends = new ArrayList<Friend>();
		for (int i = 0; i < entries.length(); i++) {
			Friend friend = new Friend();
			try {
				JSONObject jsonObject = (JSONObject) entries.get(i);
				friend.id = jsonObject.getLong("fid");
				friend.user = new User();
				friend.user.id = jsonObject.getLong("uid");
				friend.friendOf = new User();
				friend.friendOf.id = jsonObject.getLong("friendOfID");
				friend.friendNickName = jsonObject.getString("name");
				friend.status = jsonObject.getInt("status");
			} catch (JSONException e) {
				Log.w(LOG_TAG, "Participators parsing error.");
			}
			friends.add(friend);
		}
		return friends;
	}
}
