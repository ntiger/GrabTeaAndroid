package com.eatbang.activity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.eatbang.application.EatBangApplication;
import com.eatbang.connector.EatBangConnection;
import com.eatbang.db.EatBangDbSQLite;
import com.eatbang.model.LocationTag;
import com.eatbang.model.Party;
import com.eatbang.model.TimeSlot;
import com.eatbang.util.EatBangUtil;

public class PartyDetailActivity extends Activity {
	private String[] timeList;
	private Button btnModify;
	private Party party;

	private MyOnClickListener onClickListener = new MyOnClickListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partydetail);

		TextView tvPartyName = (TextView) findViewById(R.id.tvPartyName);
		TextView tvPartyLoc = (TextView) findViewById(R.id.tvPartyLoc);
		TextView tvPartyDesc = (TextView) findViewById(R.id.tvPartyDesc);
		btnModify = (Button) findViewById(R.id.btnModify);
		try {
			String showCode = getIntent().getStringExtra("showCode");
			EatBangDbSQLite sqlite = ((EatBangApplication) PartyDetailActivity.this
					.getApplication()).getPrivacyDbSQLite();
			String sessionKey = sqlite.getSessionKey();
			party = EatBangUtil.parseParty(EatBangUtil
					.JSONParsing(EatBangConnection.getInstance().connect(
							"/party/show/" + showCode + "?sessionKey="
									+ sessionKey, "get", null)));
			tvPartyName.setText(party.name);
			StringBuilder place = new StringBuilder();
			Iterator<LocationTag> iTag = party.locationTags.iterator();
			while (iTag.hasNext()) {
				place.append(iTag.next().name);
			}
			TimeSlot[] timeSlotArray = new TimeSlot[party.timeSlots.size()];
			party.timeSlots.toArray(timeSlotArray);
			long[] tsa = new long[timeSlotArray.length];
			for (int i = 0; i < timeSlotArray.length; i++) {
				tsa[i] = timeSlotArray[i].timeInLong;
			}
			Arrays.sort(tsa);
			timeList = new String[tsa.length];
			for (int i = 0; i < tsa.length; i++) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(tsa[i]);
				String tmp = cal.get(Calendar.YEAR) + "/"
						+ (cal.get(Calendar.MONTH) + 1) + "/"
						+ cal.get(Calendar.DAY_OF_MONTH);
				if (cal.get(Calendar.AM_PM) == 0) {
					tmp += " " + cal.get(Calendar.HOUR) + ":00 AM";
				} else {
					tmp += " " + cal.get(Calendar.HOUR) + ":00 PM";
				}
				timeList[i] = tmp;
			}
			tvPartyLoc.setText(place.toString());
			tvPartyDesc.setText(party.description);
			ListView ListView = (ListView) findViewById(R.id.partyTime);
			ListView.setAdapter(new TimeListAdapter(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
		btnModify.setOnClickListener(onClickListener);
	}

	// ExpandableListView的Adapter
	public class TimeListAdapter extends BaseAdapter {
		Activity activity;

		public TimeListAdapter(Activity a) {
			activity = a;
		}

		// View stub to create Group/Children 's View
		public TextView getGenericView(String string) {
			// Layout parameters for the ListView
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);
			TextView text = new TextView(activity);
			text.setLayoutParams(layoutParams);
			// Center the text vertically
			text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			text.setPadding(30, 0, 0, 0);
			text.setText(string);
			text.setTextColor(Color.BLACK);
			return text;
		}

		@Override
		public int getCount() {
			return timeList.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getGenericView(timeList[position]);
		}
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (party != null && party.editCode == null) {
				new AlertDialog.Builder(PartyDetailActivity.this)
						.setMessage("不是创建者无法修改信息。")
						.setPositiveButton("OK", null).show();
			} else {
				if (v.equals(btnModify)) {
					Intent intent = new Intent(PartyDetailActivity.this,
							PartyTabActivity.class);
					intent.putExtra("showCode",
							getIntent().getStringExtra("showCode"));
					startActivity(intent);
				}
			}

		}
	}
}
