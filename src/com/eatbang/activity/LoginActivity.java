package com.eatbang.activity;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.eatbang.application.EatBangApplication;
import com.eatbang.connector.EatBangConnection;
import com.eatbang.db.EatBangDbSQLite;
import com.eatbang.util.EatBangUtil;

public class LoginActivity extends Activity {
	private OnClickListener onClickListener = new MyOnClickListener();
	private Button login;
	private Button register;
	// private TextView tvAccount;
	// private TextView tvPassword;

	private EatBangDbSQLite sqlite;

	private static final String LOG_TAG = "LoginActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		sqlite = ((EatBangApplication) this.getApplication())
				.getPrivacyDbSQLite();
		String sessionKey = sqlite.getSessionKey();
		if (sessionKey != null) {
			startActivity(new Intent(LoginActivity.this, HomeActivity.class));
			finish();
		}
		login = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		// tvAccount = (TextView) findViewById(R.id.tvAccount);
		// tvPassword = (TextView) findViewById(R.id.tvPassword);
		login.setOnClickListener(onClickListener);
		register.setOnClickListener(onClickListener);
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			if (view.equals(login)) {
				try {
					List<NameValuePair> login = new ArrayList<NameValuePair>();
					login.add(new BasicNameValuePair("username",
							"bob@gmail.com"));
					login.add(new BasicNameValuePair("password", "secret"));
					login.add(new BasicNameValuePair("remember", "true"));
					JSONObject json = EatBangUtil.JSONParsing(EatBangConnection
							.getInstance().connect("/login", "post", login));
					if (json != null) {
						if (!json.isNull("error")) {
							new AlertDialog.Builder(LoginActivity.this)
									.setMessage("Error username or password.")
									.setPositiveButton("OK", null).show();
						} else if (!json.isNull("sessionKey")) {
							sqlite.setSessionKey(json.getString("sessionKey"));
							startActivity(new Intent(LoginActivity.this,
									HomeActivity.class));
						}
					}
				} catch (SocketException e) {
					new AlertDialog.Builder(LoginActivity.this)
							.setMessage(
									"No connection established. Please check your connection.")
							.setPositiveButton("OK", null).show();
				} catch (Exception e) {
					Log.e(LOG_TAG, e.getMessage());
				}

			} else if (view.equals(register)) {
				startActivity(new Intent(LoginActivity.this,
						RegisterActivity.class));
			}

		}
	}
}
