package com.eatbang.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
import com.eatbang.util.EatBangUtil;

public class PartyCreateTimeActivity extends Activity {
	private String[] lv_arr;
	private boolean[] timeSlots;
	private Button btnPrev, btnNext, btnToday;
	private Map<String, boolean[]> timeSlotMap;

	private Calendar calToday = Calendar.getInstance();
	private List<Button> timeSlotViews = new ArrayList<Button>();

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		lv_arr = new String[24];
		timeSlots = new boolean[24];
		for (int i = 0; i < 24; i++) {
			if (i < 12) {
				lv_arr[i] = i + ":00 am";
			} else if (i == 12) {
				lv_arr[i] = "12:00 pm";
			} else if (i > 12) {
				lv_arr[i] = (i - 12) + ":00 pm";
			}
			timeSlots[i] = false;
		}
		timeSlotMap = new HashMap<String, boolean[]>();
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
		// Button title = createButton("", 47, 47);
		// title.setBackgroundResource(R.drawable.newpartybutton);
		// layTitle.setPadding(0, 18, 20, 0);
		// layTitle.setHorizontalGravity(Gravity.RIGHT);
		// layTitle.addView(title);
		layMain.addView(layTitle);

		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);
		layTopControls.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datetitle));
		generateTopButtons(layTopControls);
		layMain.addView(layTopControls);

		// title.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(PartyTimeActivity1.this,
		// PartyCreateActivity.class);
		// startActivity(intent);
		// }
		// });

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
				timeSlotMap.put(btnToday.getText().toString(), timeSlots);
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
			final int index = i;
			slot1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (timeSlots[index]) {
						timeSlots[index] = false;
						v.setBackgroundColor(Color.WHITE);
					} else {
						timeSlots[index] = true;
						v.setBackgroundColor(Color.GREEN);
					}
				}
			});
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
			slot2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (timeSlots[index + 12]) {
						timeSlots[index + 12] = false;
						v.setBackgroundColor(Color.WHITE);
					} else {
						timeSlots[index + 12] = true;
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

		Button btnNextStep = createButton("创 建", LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		btnNextStep.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!timeSlotMap.containsKey(btnToday.getText().toString())) {
					timeSlotMap.put(btnToday.getText().toString(), timeSlots);
				}
				Intent intent = new Intent(PartyCreateTimeActivity.this,
						PartySumActivity.class);
				ArrayList<NameValuePair> party = new ArrayList<NameValuePair>();
				List<String> dateList = new ArrayList<String>();
				for (String key : timeSlotMap.keySet()) {
					List<String> timeList = new ArrayList<String>();
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
							party.add(new BasicNameValuePair("timeSlot", String
									.valueOf(EatBangUtil
											.convertStringToLong(dateTime))));
						}
					}
					if (timeList.size() != 0) {
						String[] time = new String[timeList.size()];
						timeList.toArray(time);
						intent.putExtra(key, time);
						dateList.add(key);
					}
				}
				String[] date = new String[dateList.size()];
				dateList.toArray(date);
				intent.putExtra("date", date);
				intent.putExtras(getIntent());

				party.add(new BasicNameValuePair("name", getIntent()
						.getStringExtra("partyName")));
				party.add(new BasicNameValuePair("place", getIntent()
						.getStringExtra("partyLoc")));
				party.add(new BasicNameValuePair("description", getIntent()
						.getStringExtra("partyDesc")));
				EatBangDbSQLite sqlite = ((EatBangApplication) PartyCreateTimeActivity.this
						.getApplication()).getPrivacyDbSQLite();
				party.add(new BasicNameValuePair("sessionKey", sqlite
						.getSessionKey()));
				try {
					JSONObject json = EatBangUtil.JSONParsing(EatBangConnection
							.getInstance().connect("/party/create", "post",
									party));
					intent.putExtra("showCode", json.getString("s"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				startActivity(intent);
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
