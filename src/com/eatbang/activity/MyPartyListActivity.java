package com.eatbang.activity;

import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.eatbang.util.EatBangUtil;

public class MyPartyListActivity extends Activity {
	private ListView lv;
	private Button btnDayView, btnListView, btnMonthView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
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
				Intent intent = new Intent(MyPartyListActivity.this,
						PartyCreateActivity.class);
				startActivity(intent);
			}
		});

		lv = new ListView(this);

		try {
			EatBangDbSQLite sqlite = ((EatBangApplication) this
					.getApplication()).getPrivacyDbSQLite();
			JSONArray json = EatBangUtil.JSONArrayParsing(EatBangConnection
					.getInstance().connect(
							"/user/parties?sessionKey="
									+ sqlite.getSessionKey(), "get", null));

			final List<Party> parties = EatBangUtil.parseParties(json);
			lv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 680));
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
					Intent intent = new Intent(MyPartyListActivity.this,
							PartyDetailActivity.class);
					intent.putExtra("showCode", parties.get(position).showCode);
					startActivity(intent);
				}
			});

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

		layFooter.setPadding(iHorPadding, iVerPadding, 0, 0);

		btnListView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnListView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datelistviewbutton));

		btnDayView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnDayView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datedayviewbutton1));

		btnMonthView = createButton("",
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 46);
		btnMonthView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.datemonthviewbutton1));

		btnDayView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MyPartyListActivity.this,
						MyPartyDayActivity.class);
				startActivity(intent);
			}
		});

		btnMonthView.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MyPartyListActivity.this,
						MyPartyMonthActivity.class);
				startActivity(intent);
			}
		});

		layFooter.setGravity(Gravity.CENTER_HORIZONTAL);
		layFooter.addView(btnListView);
		layFooter.addView(btnDayView);
		layFooter.addView(btnMonthView);
	}
}
