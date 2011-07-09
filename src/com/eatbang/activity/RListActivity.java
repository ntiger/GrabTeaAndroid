package com.eatbang.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RListActivity extends Activity {
	private Button map;
	private MyOnClickListener myOnClickListener = new MyOnClickListener();
	private ListView lv;
	private List<HashMap<String, Object>> listItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rlist);
		map = (Button) findViewById(R.id.map);
		map.setOnClickListener(myOnClickListener);
		lv = (ListView) findViewById(R.id.lvRlist);

		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("itemTime", "123");
		hashMap.put("itemMinute", "分钟");
		hashMap.put("itemName", " 王府井腐败大厦");
		hashMap.put("itemAddress", "  地址：王府井");
		hashMap.put("itemDistance", "  1.5公里");

		listItem = new ArrayList<HashMap<String, Object>>();
		listItem.add(hashMap);

		lv.setOnItemClickListener(myOnClickListener);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.listitem, new String[] { "itemTime", "itemMinute",
						"itemName", "itemAddress", "itemDistance" }, new int[] {
						R.id.itemTime, R.id.itemMinute, R.id.itemName,
						R.id.itemAddress, R.id.itemDistance });
		lv.setAdapter(listItemAdapter);
	}

	private class MyOnClickListener implements OnClickListener,
			OnItemClickListener {

		@Override
		public void onClick(View v) {
			if (v.equals(map)) {
				startActivity(new Intent(RListActivity.this, RMapActivity.class));
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int positon,
				long id) {
			startActivity(new Intent(RListActivity.this, RInfoActivity.class));
		}
	}
}