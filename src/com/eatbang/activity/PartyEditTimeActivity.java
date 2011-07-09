package com.eatbang.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.eatbang.application.EatBangApplication;
import com.eatbang.connector.EatBangConnection;
import com.eatbang.db.EatBangDbSQLite;
import com.eatbang.model.Party;
import com.eatbang.model.TimeSlot;
import com.eatbang.util.EatBangUtil;

public class PartyEditTimeActivity extends Activity {
	private String[] lv_arr;
	private boolean[] timeSlots;
	private Button btnPrev, btnNext, btnToday;
	private Map<String, boolean[]> timeSlotMap;
	private List<String> removeList = new ArrayList<String>();

	private Calendar calToday = Calendar.getInstance();
	private List<Button> timeSlotViews = new ArrayList<Button>();

	private Party party;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		lv_arr = new String[24];
		for (int i = 0; i < 24; i++) {
			if (i < 12) {
				lv_arr[i] = i + ":00 am";
			} else if (i == 12) {
				lv_arr[i] = "12:00 pm";
			} else if (i > 12) {
				lv_arr[i] = (i - 12) + ":00 pm";
			}
		}
		timeSlotMap = new HashMap<String, boolean[]>();
		try {
			String showCode = getIntent().getStringExtra("showCode");
			EatBangDbSQLite sqlite = ((EatBangApplication) PartyEditTimeActivity.this
					.getApplication()).getPrivacyDbSQLite();
			String sessionKey = sqlite.getSessionKey();
			party = EatBangUtil.parseParty(EatBangUtil
					.JSONParsing(EatBangConnection.getInstance().connect(
							"/party/show/" + showCode + "?sessionKey="
									+ sessionKey, "get", null)));
			long min = Long.MAX_VALUE;
			Iterator<TimeSlot> i = party.timeSlots.iterator();
			while (i.hasNext()) {
				TimeSlot ts = i.next();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(ts.timeInLong);
				if (ts.timeInLong < min)
					min = ts.timeInLong;
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
			calToday.setTimeInMillis(min);
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
		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);
		layTopControls.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datetitle));
		generateTopButtons(layTopControls);
		layMain.addView(layTopControls);

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
				j = i / 2;
			} else {
				j = i / 2 + 12;
			}
			if (!timeSlotMap.containsKey(btnToday.getText().toString())) {
				timeSlots = new boolean[24];
				timeSlotViews.get(i).setBackgroundColor(Color.WHITE);
			} else {
				timeSlots = timeSlotMap.get(btnToday.getText().toString());
				if (timeSlots[j]) {
					timeSlotViews.get(i).setBackgroundColor(Color.GREEN);
				} else {
					timeSlotViews.get(i).setBackgroundColor(Color.WHITE);
				}
			}
		}
	}

	private View generateTimeSlotsView() {
		LinearLayout timeSlotsView = createLayout(LinearLayout.VERTICAL);
		for (int i = 0; i < 12; i++) {
			LinearLayout timeSlot = createLayout(LinearLayout.HORIZONTAL);

			LayoutParams lp = new LayoutParams(
					EatBangUtil.getScreenWidth(this) / 2,
					(EatBangUtil.getScreenHeight(this) - 280) / 12);
			lp.setMargins(1, 1, 1, 1);
			Button slot1 = new Button(this);
			slot1.setLayoutParams(lp);
			slot1.setText(lv_arr[i]);
			slot1.setTextSize(13);
			slot1.setTextColor(Color.BLACK);
			if (timeSlotMap.containsKey(btnToday.getText().toString())) {
				timeSlots = timeSlotMap.get(btnToday.getText().toString());
				if (timeSlots[i]) {
					slot1.setBackgroundColor(Color.GREEN);
				} else {
					slot1.setBackgroundColor(Color.WHITE);
				}
			}
			slot1.setTag(i);
			slot1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (timeSlots[Integer.parseInt(v.getTag().toString())]) {
						timeSlots[Integer.parseInt(v.getTag().toString())] = false;
						v.setBackgroundColor(Color.WHITE);
					} else {
						timeSlots[Integer.parseInt(v.getTag().toString())] = true;
						v.setBackgroundColor(Color.GREEN);
					}
				}
			});
			Button slot2 = new Button(this);
			slot2.setLayoutParams(lp);
			slot2.setText(lv_arr[i + 12]);
			slot2.setTextSize(13);
			slot2.setTextColor(Color.BLACK);
			if (timeSlotMap.containsKey(btnToday.getText().toString())) {
				timeSlots = timeSlotMap.get(btnToday.getText().toString());
				if (timeSlots[i + 12]) {
					slot2.setBackgroundColor(Color.GREEN);
				} else {
					slot2.setBackgroundColor(Color.WHITE);
				}
			}
			slot2.setTag(i + 12);
			slot2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (timeSlots[Integer.parseInt(v.getTag().toString())]) {
						timeSlots[Integer.parseInt(v.getTag().toString())] = false;
						v.setBackgroundColor(Color.WHITE);
					} else {
						timeSlots[Integer.parseInt(v.getTag().toString())] = true;
						v.setBackgroundColor(Color.GREEN);
					}
				}
			});
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

		Button btnNextStep = createButton("更 新", LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		btnNextStep.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!timeSlotMap.containsKey(btnToday.getText().toString())) {
					timeSlotMap.put(btnToday.getText().toString(), timeSlots);
				}
				ArrayList<NameValuePair> partyParams = new ArrayList<NameValuePair>();
				List<String> dateList = new ArrayList<String>();
				for (String key : timeSlotMap.keySet()) {
					List<String> timeList = new ArrayList<String>();
					boolean empty = true;
					for (int i = 0; i < 24; i++) {
						if (timeSlotMap.get(key)[i]) {
							String time = null;
							if (i < 12) {
								time = i + ":00 am";
							} else if (i == 12) {
								time = "12:00 pm";
							} else if (i > 12) {
								time = (i - 12) + ":00 pm";
							}
							timeList.add(time);
							String dateTime = key + " " + time;
							partyParams.add(new BasicNameValuePair("timeSlot",
									String.valueOf(EatBangUtil
											.convertStringToLong(dateTime))));
							empty = false;
						}
					}
					if (empty) {
						removeList.add(key);
					}
					if (timeList.size() != 0) {
						String[] time = new String[timeList.size()];
						timeList.toArray(time);
						dateList.add(key);
					}
				}
				for (String str : removeList) {
					timeSlotMap.remove(str);
				}
				String[] date = new String[dateList.size()];
				dateList.toArray(date);

				partyParams.add(new BasicNameValuePair("name", party.name));

				partyParams.add(new BasicNameValuePair("description",
						party.description));
				EatBangDbSQLite sqlite = ((EatBangApplication) PartyEditTimeActivity.this
						.getApplication()).getPrivacyDbSQLite();
				partyParams.add(new BasicNameValuePair("sessionKey", sqlite
						.getSessionKey()));

				String editCode = "";
				if (party != null) {
					editCode = party.editCode;
				}
				try {
					String response = EatBangUtil
							.convertStreamToString(EatBangConnection
									.getInstance().connect(
											"/party/update/" + editCode,
											"post", partyParams));
					new AlertDialog.Builder(PartyEditTimeActivity.this)
							.setMessage("更新时间成功").setNeutralButton("OK", null)
							.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		layFooter.setGravity(Gravity.CENTER_HORIZONTAL);
		layFooter.addView(btnNextStep);
	}

	private void setPrevViewItem() {
		if (!timeSlotMap.containsKey(btnToday.getText().toString())) {
			timeSlotMap.put(btnToday.getText().toString(), timeSlots);
		}
		calToday.add(Calendar.DAY_OF_MONTH, -1);
		btnToday.setText(calToday.get(Calendar.YEAR) + "/"
				+ (calToday.get(Calendar.MONTH) + 1) + "/"
				+ calToday.get(Calendar.DAY_OF_MONTH));
		refreshTimeSlotsView();
	}

	private void setTodayViewItem() {
		if (!timeSlotMap.containsKey(btnToday.getText().toString())) {
			timeSlotMap.put(btnToday.getText().toString(), timeSlots);
		}
		calToday.setTimeInMillis(System.currentTimeMillis());
		btnToday.setText(calToday.get(Calendar.YEAR) + "/"
				+ (calToday.get(Calendar.MONTH) + 1) + "/"
				+ calToday.get(Calendar.DAY_OF_MONTH));
	}

	private void setNextViewItem() {
		if (!timeSlotMap.containsKey(btnToday.getText().toString())) {
			timeSlotMap.put(btnToday.getText().toString(), timeSlots);
		}
		calToday.add(Calendar.DAY_OF_MONTH, 1);
		btnToday.setText(calToday.get(Calendar.YEAR) + "/"
				+ (calToday.get(Calendar.MONTH) + 1) + "/"
				+ calToday.get(Calendar.DAY_OF_MONTH));
		refreshTimeSlotsView();
	}
}
