package com.eatbang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyShareActivity extends Activity {
	Button map;
	MyOnClickListener myOnClickListener = new MyOnClickListener();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myshare);

		map = (Button) findViewById(R.id.map);
		map.setOnClickListener(myOnClickListener);
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.equals(map)) {
				startActivity(new Intent(MyShareActivity.this,
						RMapActivity.class));
			}
		}

	}
}
