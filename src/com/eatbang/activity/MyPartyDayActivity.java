package com.eatbang.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.eatbang.application.EatBangApplication;
import com.eatbang.connector.EatBangConnection;
import com.eatbang.db.EatBangDbSQLite;
import com.eatbang.model.Party;
import com.eatbang.model.TimeSlot;
import com.eatbang.util.EatBangUtil;

public class MyPartyDayActivity extends Activity {
	private String[] lv_arr;
	private boolean[] timeSlots;
	private Button btnPrev, btnNext, btnToday;
	private ListView lvTimeSlots;
	private Map<String, boolean[]> timeSlotMap;
	private MyOnClickListener onClickListener = new MyOnClickListener();
	private Button btnDayView, btnListView, btnMonthView;

	private Calendar calToday = Calendar.getInstance();
	private List<Button> timeSlotViews = new ArrayList<Button>();

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		lv_arr = new String[24];
		timeSlots = new boolean[24];
		int j = 0;
		for (int i = 6; i < 30; i++) {
			if (i < 12) {
				lv_arr[j] = i + ":00 am";
			} else if (i == 12) {
				lv_arr[j] = "12:00 pm";
			} else if (i > 12 && i < 24) {
				lv_arr[j] = (i - 12) + ":00 pm";
			} else if (i == 24) {
				lv_arr[j] = "12:00 am";
			} else if (i > 24) {
				lv_arr[j] = (i - 24) + ":00 am";
			}
			timeSlots[j] = false;
			j++;
		}
		timeSlotMap = new HashMap<String, boolean[]>();
		try {
			EatBangDbSQLite sqlite = ((EatBangApplication) this
					.getApplication()).getPrivacyDbSQLite();
			JSONArray json = EatBangUtil.JSONArrayParsing(EatBangConnection
					.getInstance().connect(
							"/user/parties?sessionKey="
									+ sqlite.getSessionKey(), "get", null));

			final List<Party> parties = EatBangUtil.parseParties(json);
			for (Party party : parties) {
				Iterator<TimeSlot> i = party.timeSlots.iterator();
				while (i.hasNext()) {
					TimeSlot ts = i.next();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(ts.timeInLong);
					String key = cal.get(Calendar.YEAR) + "/"
							+ (cal.get(Calendar.MONTH) + 1) + "/"
							+ cal.get(Calendar.DAY_OF_MONTH);
					if (timeSlotMap.containsKey(key)) {
						boolean[] times = timeSlotMap.get(key);
						times[cal.get(Calendar.HOUR_OF_DAY)] = true;
					} else {
						boolean[] times = new boolean[24];
						times[cal.get(Calendar.HOUR_OF_DAY)] = true;
						timeSlotMap.put(key, times);
					}
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		setContentView(generateContentView());
	}

	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this);
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);
		return lay;
	}

	private Button createButton(String sText, int iWidth, int iHeight) {
		Button btn = new Button(this);
		btn.setText(sText);
		btn.setLayoutParams(new LayoutParams(iWidth, iHeight));
		return btn;
	}

	private void generateTopButtons(LinearLayout layTopControls) {
		final int iHorPadding = 24;
		btnToday = createButton("", 68 * 7 - 120,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		btnToday.setPadding(iHorPadding, 0, iHorPadding, 0);
		btnToday.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datetitle));
		btnToday.setTextSize(20);
		btnToday.setTextColor(Color.WHITE);

		btnToday.setText(calToday.get(Calendar.YEAR) + "/"
				+ (calToday.get(Calendar.MONTH) + 1) + "/"
				+ calToday.get(Calendar.DAY_OF_MONTH));

		btnPrev = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		btnPrev.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.left));

		btnNext = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		btnNext.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.right));

		// set events
		btnPrev.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				setPrevViewItem();
			}
		});
		btnToday.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				setTodayViewItem();
			}
		});
		btnNext.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				setNextViewItem();
			}
		});

		layTopControls.setGravity(Gravity.CENTER_HORIZONTAL);
		layTopControls.addView(btnPrev);
		layTopControls.addView(btnToday);
		layTopControls.addView(btnNext);
	}

	private View generateContentView() {
		LinearLayout layMain = createLayout(LinearLayout.VERTICAL);
		LinearLayout layTitle = createLayout(LinearLayout.HORIZONTAL);
		layTitle.setBackgroundResource(R.drawable.mypartytitle);
		Button title = createButton("", 47, 47);
		title.setBackgroundResource(R.drawable.newpartybutton);
		layTitle.setPadding(0, 18, 20, 0);
		layTitle.setHorizontalGravity(Gravity.RIGHT);
		layTitle.addView(title);
		layMain.addView(layTitle);

		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);
		layTopControls.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datetitle));
		generateTopButtons(layTopControls);
		layMain.addView(layTopControls);

		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyPartyDayActivity.this,
						PartyCreateActivity.class);
				startActivity(intent);
			}
		});
		lvTimeSlots = new ListView(this);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lvTimeSlots.setLayoutParams(lp);
		lvTimeSlots.setOnItemClickListener(onClickListener);

		lvTimeSlots.setAdapter(new TimeSlotsListAdapter(this,
				android.R.layout.simple_list_item_1, lv_arr));

		// layMain.addView(lvTimeSlots);
		layMain.addView(generateTimeSlotsView());

		LinearLayout layFooter = createLayout(LinearLayout.HORIZONTAL);
		layFooter.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datefooter));
		generateFooterButtons(layFooter);
		layMain.addView(layFooter);
		return layMain;
	}

	private void refreshTimeSlotsView() {
		int j = 0;
		for (int i = 0; i < 24; i++) {
			if (i % 2 == 0) {
				j = i / 2 + 6;
			} else {
				j = i / 2 + 12 + 6;
			}
			if (j > 23) {
				j = j - 24;
			}
			if (timeSlotMap.get(btnToday.getText()) != null
					&& timeSlotMap.get(btnToday.getText())[j]) {
				timeSlotViews.get(i).setBackgroundColor(Color.GREEN);
			} else {
				timeSlotViews.get(i).setBackgroundColor(Color.WHITE);
			}
		}
	}

	private View generateTimeSlotsView() {
		LinearLayout timeSlotsView = createLayout(LinearLayout.VERTICAL);
		for (int i = 0; i < 12; i++) {
			LinearLayout timeSlot = createLayout(LinearLayout.HORIZONTAL);

			LayoutParams lp = new LayoutParams(
					EatBangUtil.getScreenWidth(this) / 2,
					(EatBangUtil.getScreenHeight(this) - 250) / 12);
			lp.setMargins(1, 1, 1, 1);
			Button slot1 = new Button(this);
			slot1.setLayoutParams(lp);
			slot1.setText(lv_arr[i]);
			slot1.setTextSize(13);
			slot1.setTextColor(Color.BLACK);
			if (timeSlotMap.get(btnToday.getText()) != null
					&& timeSlotMap.get(btnToday.getText())[i]) {
				slot1.setBackgroundColor(Color.GREEN);
			} else {
				slot1.setBackgroundColor(Color.WHITE);
			}
			Button slot2 = new Button(this);
			slot2.setLayoutParams(lp);
			slot2.setText(lv_arr[i + 12]);
			slot2.setTextSize(13);
			slot2.setTextColor(Color.BLACK);
			if (timeSlotMap.get(btnToday.getText()) != null
					&& timeSlotMap.get(btnToday.getText())[i + 12]) {
				slot2.setBackgroundColor(Color.GREEN);
			} else {
				slot2.setBackgroundColor(Color.WHITE);
			}
			timeSlot.addView(slot1);
			timeSlot.addView(slot2);
			timeSlotViews.add(slot1);
			timeSlotViews.add(slot2);
			timeSlotsView.addView(timeSlot);
		}
		return timeSlotsView;
	}

	private void generateFooterButtons(LinearLayout layFooter) {
		final int iHorPadding = 14;
		final int iVerPadding = 11;

		layFooter.setPadding(iHorPadding, iVerPadding, 0, 0);

		btnListView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnListView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datelistviewbutton1));

		btnDayView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnDayView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datedayviewbutton));

		btnMonthView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnMonthView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datemonthviewbutton1));

		btnListView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MyPartyDayActivity.this,
						MyPartyListActivity.class);
				startActivity(intent);
			}
		});

		btnMonthView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MyPartyDayActivity.this,
						MyPartyMonthActivity.class);
				startActivity(intent);
			}
		});

		layFooter.setGravity(Gravity.CENTER_HORIZONTAL);
		layFooter.addView(btnListView);
		layFooter.addView(btnDayView);
		layFooter.addView(btnMonthView);
	}

	private void setPrevViewItem() {
		calToday.add(Calendar.DAY_OF_MONTH, -1);
		btnToday.setText(calToday.get(Calendar.YEAR) + "/"
				+ (calToday.get(Calendar.MONTH) + 1) + "/"
				+ calToday.get(Calendar.DAY_OF_MONTH));
		refreshTimeSlotsView();
	}

	private void setTodayViewItem() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		btnToday.setText(calToday.get(Calendar.YEAR) + "/"
				+ (calToday.get(Calendar.MONTH) + 1) + "/"
				+ calToday.get(Calendar.DAY_OF_MONTH));
	}

	private void setNextViewItem() {
		calToday.add(Calendar.DAY_OF_MONTH, 1);
		btnToday.setText(calToday.get(Calendar.YEAR) + "/"
				+ (calToday.get(Calendar.MONTH) + 1) + "/"
				+ calToday.get(Calendar.DAY_OF_MONTH));
		refreshTimeSlotsView();
	}

	/**
	 * This class is the listener for the ListView item clicks and button
	 * clicks.
	 * 
	 * @author rnm743
	 */
	@Deprecated
	public class MyOnClickListener implements OnItemClickListener,
			OnClickListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.widget.AdapterView.OnItemClickListener#onItemClick(android
		 * .widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (timeSlots[position]) {
				timeSlots[position] = false;
				view.setBackgroundColor(Color.TRANSPARENT);
			} else {
				timeSlots[position] = true;
				view.setBackgroundColor(Color.GRAY);
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			if (btnNext.equals(v)) {
				if (!timeSlotMap.containsKey(btnToday.getText().toString())) {
					timeSlotMap.put(btnToday.getText().toString(), timeSlots);
				}
				calToday.add(Calendar.DAY_OF_MONTH, 1);
				timeSlots = new boolean[16];
				btnToday.setText(EatBangUtil.convertCalendarToString(calToday));
				lvTimeSlots.setAdapter(new TimeSlotsListAdapter(
						MyPartyDayActivity.this,
						android.R.layout.simple_list_item_1, lv_arr));
			} else if (btnPrev.equals(v)) {
				if (!timeSlotMap.containsKey(btnToday.getText().toString())) {
					timeSlotMap.put(btnToday.getText().toString(), timeSlots);
				}
				calToday.add(Calendar.DAY_OF_MONTH, -1);
				timeSlots = new boolean[16];
				btnToday.setText(EatBangUtil.convertCalendarToString(calToday));
				lvTimeSlots.setAdapter(new TimeSlotsListAdapter(
						MyPartyDayActivity.this,
						android.R.layout.simple_list_item_1, lv_arr));
			} else if (btnToday.equals(v)) {
				startActivityForResult(new Intent(MyPartyDayActivity.this,
						PartyDateActivity.class), 1);
			}
		}
	}

	private class TimeSlotsListAdapter extends ArrayAdapter<String> {

		public TimeSlotsListAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			if (timeSlotMap.containsKey(btnToday.getText().toString())) {
				timeSlots = timeSlotMap.get(btnToday.getText().toString());
			}
			if (timeSlots[position]) {
				v.setBackgroundColor(Color.GRAY);
			} else {
				v.setBackgroundColor(Color.TRANSPARENT);
			}
			return v;
		}

	}
}
