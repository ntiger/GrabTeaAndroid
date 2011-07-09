package com.eatbang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RMapActivity extends Activity {
	Button list;
	MyOnClickListener myOnClickListener = new MyOnClickListener();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmap);
		list = (Button) findViewById(R.id.list);
		list.setOnClickListener(myOnClickListener);
	}

	private class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v.equals(list)) {
				startActivity(new Intent(RMapActivity.this, RListActivity.class));
			}
		}
	}
}
