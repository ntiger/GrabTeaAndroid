package com.eatbang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.eatbang.application.EatBangApplication;
import com.eatbang.connector.EatBangConnection;
import com.eatbang.db.EatBangDbSQLite;
import com.eatbang.model.Party;
import com.eatbang.util.EatBangUtil;

public class PartyInfoActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partyinfo);

		EditText etPartyName = (EditText) findViewById(R.id.etPartyName);
		EditText etPartyBring = (EditText) findViewById(R.id.etPartyBring);
		EditText etPartyNotBring = (EditText) findViewById(R.id.etPartyNotBring);
		EditText etPartyInvite = (EditText) findViewById(R.id.etPartyInvite);
		EditText etPartyDesc = (EditText) findViewById(R.id.etPartyDesc);
		try {
			String showCode = getIntent().getStringExtra("showCode");
			EatBangDbSQLite sqlite = ((EatBangApplication) PartyInfoActivity.this
					.getApplication()).getPrivacyDbSQLite();
			String sessionKey = sqlite.getSessionKey();
			Party party = EatBangUtil.parseParty(EatBangUtil
					.JSONParsing(EatBangConnection.getInstance().connect(
							"/party/show/" + showCode + "?sessionKey="
									+ sessionKey, "get", null)));
			etPartyName.setText(party.name != null ? party.name : "");
			etPartyBring.setText(party.bring != null ? party.bring : "");
			etPartyNotBring.setText(party.notBring != null ? party.notBring
					: "");
			etPartyInvite.setText(String.valueOf(party.guestMaxNumber));
			etPartyDesc.setText(party.description != null ? party.description
					: "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
