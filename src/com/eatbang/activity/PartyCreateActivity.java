package com.eatbang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PartyCreateActivity extends Activity {
	private EditText partyName;
	private EditText partyLoc;
	private EditText partyDesc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partycreate);
		Button btnNextStep = (Button) findViewById(R.id.nextStep);
		partyName = (EditText) findViewById(R.id.partyName);
		partyLoc = (EditText) findViewById(R.id.partyLoc);
		partyDesc = (EditText) findViewById(R.id.partyDesc);

		btnNextStep.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PartyCreateActivity.this,
						PartyCreateTimeActivity.class);
				intent.putExtra("partyName", partyName.getText().toString());
				intent.putExtra("partyLoc", partyLoc.getText().toString());
				intent.putExtra("partyDesc", partyDesc.getText().toString());
				startActivity(intent);
			}
		});

	}
}