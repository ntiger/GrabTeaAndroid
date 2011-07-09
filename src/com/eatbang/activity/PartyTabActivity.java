package com.eatbang.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class PartyTabActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TabHost tabHost = getTabHost();

		Intent tab1 = new Intent(this, PartyInfoActivity.class);
		tab1.putExtras(getIntent().getExtras());
		Intent tab2 = new Intent(this, ParticipatorActivity.class);
		tab2.putExtras(getIntent().getExtras());
		Intent tab3 = new Intent(this, ParticipatorActivity.class);
		tab3.putExtras(getIntent().getExtras());
		Intent tab4 = new Intent(this, PartyEditTimeActivity.class);
		tab4.putExtras(getIntent().getExtras());

		tabHost.addTab(tabHost.newTabSpec("one").setIndicator("具体信息")
				.setContent(tab1));
		tabHost.addTab(tabHost.newTabSpec("two").setIndicator("聚会标签")
				.setContent(tab3));
		tabHost.addTab(tabHost.newTabSpec("three").setIndicator("邀请列表")
				.setContent(tab3));
		tabHost.addTab(tabHost.newTabSpec("four").setIndicator("时间信息")
				.setContent(tab4));

	}
}
