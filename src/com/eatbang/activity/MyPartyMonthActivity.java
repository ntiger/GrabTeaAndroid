package com.eatbang.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.eatbang.application.EatBangApplication;
import com.eatbang.calendar.DateWidgetDayCell;
import com.eatbang.calendar.DateWidgetDayHeader;
import com.eatbang.calendar.DayStyle;
import com.eatbang.connector.EatBangConnection;
import com.eatbang.db.EatBangDbSQLite;
import com.eatbang.model.Party;
import com.eatbang.util.EatBangUtil;

public class MyPartyMonthActivity extends Activity {
	private ArrayList<DateWidgetDayCell> days = new ArrayList<DateWidgetDayCell>();
	// private SimpleDateFormat dateMonth = new SimpleDateFormat("MMMM yyyy");
	private Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	LinearLayout layContent = null;
	Button btnPrev = null;
	Button btnToday = null;
	Button btnNext = null;
	Button btnDayView, btnListView, btnMonthView, btnTodayView;
	private int iFirstDayOfWeek = Calendar.MONDAY;
	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	private int iDayCellSize;
	private static final int iDayHeaderHeight = 30;
	private int iTotalWidth;

	private ListView lv;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		iFirstDayOfWeek = Calendar.MONDAY;
		iDayCellSize = EatBangUtil.getScreenWidth(this) / 7;
		iTotalWidth = (iDayCellSize * 7);
		setContentView(generateContentView());
		calStartDate = getCalendarStartDate();
		DateWidgetDayCell daySelected = updateCalendar();
		if (daySelected != null)
			daySelected.requestFocus();
	}

	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this);
		lay.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
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
		final int iSmallButtonWidth = 60;
		btnToday = createButton("", iTotalWidth - iSmallButtonWidth
				- iSmallButtonWidth,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		btnToday.setPadding(iHorPadding, 0, iHorPadding, 0);
		btnToday.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datetitle));
		btnToday.setTextSize(20);
		btnToday.setTextColor(Color.WHITE);

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
				String s = calToday.get(Calendar.YEAR) + "/"
						+ (calToday.get(Calendar.MONTH) + 1);
				btnToday.setText(s);
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

		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyPartyMonthActivity.this,
						PartyCreateActivity.class);
				startActivity(intent);
			}
		});

		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);
		layTopControls.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datetitle));
		layContent = createLayout(LinearLayout.VERTICAL);
		generateTopButtons(layTopControls);
		generateCalendar(layContent);

		layMain.addView(layTopControls);
		layMain.addView(layContent);

		lv = new ListView(this);

		try {
			EatBangDbSQLite sqlite = ((EatBangApplication) this
					.getApplication()).getPrivacyDbSQLite();
			JSONArray json = EatBangUtil.JSONArrayParsing(EatBangConnection
					.getInstance().connect(
							"/user/parties?sessionKey="
									+ sqlite.getSessionKey(), "get", null));

			final List<Party> parties = EatBangUtil.parseParties(json);
			LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			lv.setLayoutParams(lp);

			String lv_arr[] = new String[parties.size()];
			for (Party party : parties) {
				lv_arr[parties.indexOf(party)] = party.name;
			}
			lv.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, lv_arr));

			layMain.addView(lv);

			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					Intent intent = new Intent(MyPartyMonthActivity.this,
							PartyDetailActivity.class);
					intent.putExtra("showCode", parties.get(position).showCode);
					startActivity(intent);
				}
			});

			// btnNewDine = createButton("新建聚餐", LayoutParams.FILL_PARENT,
			// LayoutParams.WRAP_CONTENT);
			// btnNewDine.setTextSize(20);
			// layMain.addView(btnNewDine);
			// btnNewDine.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// startActivity(new Intent(MyPartyActivity.this,
			// PartyCreateActivity.class));
			// }
			// });

			LinearLayout layFooter = createLayout(LinearLayout.HORIZONTAL);
			layFooter.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.datefooter));
			generateFooterButtons(layFooter);
			layMain.addView(layFooter);

		} catch (Exception e) {
			e.getMessage();
		}
		return layMain;
	}

	private void generateFooterButtons(LinearLayout layFooter) {
		final int iHorPadding = 14;
		final int iVerPadding = 11;

		layFooter.setPadding(iHorPadding, iVerPadding, 80, 0);

		btnTodayView = new Button(this);
		LayoutParams lp = new LayoutParams(92, 46);
		lp.setMargins(0, 0, 80, 0);
		btnTodayView.setLayoutParams(lp);
		btnTodayView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datetodaybutton1));

		btnListView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnListView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datelistviewbutton1));

		btnDayView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnDayView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datedayviewbutton1));

		btnMonthView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnMonthView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datemonthviewbutton));

		// set events
		btnTodayView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				btnTodayView.setBackgroundResource(R.drawable.datetodaybutton1);
				setTodayViewItem();
				String s = calToday.get(Calendar.YEAR) + "/"
						+ (calToday.get(Calendar.MONTH) + 1);
				btnToday.setText(s);
			}
		});

		btnTodayView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				btnTodayView.setBackgroundResource(R.drawable.datetodaybutton);
				return false;
			}
		});

		btnDayView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MyPartyMonthActivity.this,
						MyPartyDayActivity.class);
				startActivity(intent);
			}
		});

		btnListView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MyPartyMonthActivity.this,
						MyPartyListActivity.class);
				startActivity(intent);
			}
		});

		layFooter.setGravity(Gravity.CENTER_HORIZONTAL);
		layFooter.addView(btnTodayView);
		layFooter.addView(btnListView);
		layFooter.addView(btnDayView);
		layFooter.addView(btnMonthView);
	}

	private View generateCalendarRow() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayCell dayCell = new DateWidgetDayCell(this,
					iDayCellSize, iDayCellSize);
			dayCell.setItemClick(mOnDayCellClick);
			days.add(dayCell);
			layRow.addView(dayCell);
		}
		return layRow;
	}

	private View generateCalendarHeader() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayHeader day = new DateWidgetDayHeader(this,
					iDayCellSize, iDayHeaderHeight);
			final int iWeekDay = DayStyle.getWeekDay(iDay, iFirstDayOfWeek);
			day.setData(iWeekDay);
			layRow.addView(day);
		}
		return layRow;
	}

	private void generateCalendar(LinearLayout layContent) {
		layContent.addView(generateCalendarHeader());
		days.clear();
		for (int iRow = 0; iRow < 6; iRow++) {
			layContent.addView(generateCalendarRow());
		}
	}

	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		UpdateStartDateForMonth();

		return calStartDate;
	}

	private DateWidgetDayCell updateCalendar() {
		DateWidgetDayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
		for (int i = 0; i < days.size(); i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			DateWidgetDayCell dayCell = days.get(i);
			// check today
			boolean bToday = false;
			if (calToday.get(Calendar.YEAR) == iYear)
				if (calToday.get(Calendar.MONTH) == iMonth)
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay)
						bToday = true;
			// check holiday
			boolean bHoliday = false;
			if ((iDayOfWeek == Calendar.SATURDAY)
					|| (iDayOfWeek == Calendar.SUNDAY))
				bHoliday = true;
			if ((iMonth == Calendar.JANUARY) && (iDay == 1))
				bHoliday = true;

			dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
					iMonthViewCurrentMonth);
			bSelected = false;
			if (bIsSelection)
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
						&& (iSelectedYear == iYear)) {
					bSelected = true;
				}
			dayCell.setSelected(bSelected);
			if (bSelected)
				daySelected = dayCell;
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		layContent.invalidate();
		return daySelected;
	}

	private void UpdateStartDateForMonth() {
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		UpdateCurrentMonthDisplay();
		// update days for week
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
	}

	private void UpdateCurrentMonthDisplay() {
		String s = calStartDate.get(Calendar.YEAR) + "/"
				+ (calStartDate.get(Calendar.MONTH) + 1);// dateMonth.format(calCalendar.getTime());
		btnToday.setText(s);
	}

	private void setPrevViewItem() {
		iMonthViewCurrentMonth--;
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		UpdateStartDateForMonth();
		updateCalendar();
	}

	private void setTodayViewItem() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);
		calStartDate.setTimeInMillis(calToday.getTimeInMillis());
		calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		UpdateStartDateForMonth();
		updateCalendar();
	}

	private void setNextViewItem() {
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		UpdateStartDateForMonth();
		updateCalendar();

	}

	private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {
		public void OnClick(DateWidgetDayCell item) {
			calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
			item.setSelected(true);
			updateCalendar();
		}
	};

}
