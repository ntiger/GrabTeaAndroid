package com.eatbang.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.eatbang.application.EatBangApplication;
import com.eatbang.connector.EatBangConnection;
import com.eatbang.db.EatBangDbSQLite;
import com.eatbang.model.Friend;
import com.eatbang.model.Participator;
import com.eatbang.model.Party;
import com.eatbang.util.EatBangUtil;

public class ParticipatorActivity extends Activity {
	private LayoutInflater mInflater;
	private Map<Participator, Boolean> participatorMap;
	private ListView lvInvitation;
	private EditText invitationEmail;
	private Button btnInvite;
	private Button btnSubmit;
	private InvitationListAdapter invitationListAdapter;
	private Party party;

	public static final boolean DEFAULT_SELECTION = false;

	private ParticipateOnClickListener onClickListener = new ParticipateOnClickListener();

	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.participator);
		invitationEmail = (AutoCompleteTextView) findViewById(R.id.invitationEmail);
		lvInvitation = (ListView) findViewById(R.id.lvInvitation);
		btnInvite = (Button) findViewById(R.id.btnInvite);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnInvite.setOnClickListener(onClickListener);
		btnSubmit.setOnClickListener(onClickListener);
		participatorMap = new HashMap<Participator, Boolean>();
		Set<Participator> participators = getParticipators();
		if (participators != null) {
			for (Participator p : participators) {
				participatorMap.put(p, true);
			}
		}
		invitationListAdapter = new InvitationListAdapter(participatorMap);
		lvInvitation.setAdapter(invitationListAdapter);
	}

	private Set<Participator> getParticipators() {
		String showCode = getIntent().getStringExtra("showCode");
		EatBangDbSQLite sqlite = ((EatBangApplication) this.getApplication())
				.getPrivacyDbSQLite();
		String sessionKey = sqlite.getSessionKey();

		try {
			party = EatBangUtil.parseParty(EatBangUtil
					.JSONParsing(EatBangConnection.getInstance().connect(
							"/party/show/" + showCode + "?sessionKey="
									+ sessionKey, "get", null)));
		} catch (Exception e) {

		}
		return party.participators;
	}

	private List<Friend> getFriendsList() {
		EatBangDbSQLite sqlite = ((EatBangApplication) this.getApplication())
				.getPrivacyDbSQLite();
		List<Friend> friends = null;
		try {
			JSONArray json = EatBangUtil
					.JSONArrayParsing(EatBangConnection.getInstance()
							.connect(
									"/friend/list?sessionKey="
											+ sqlite.getSessionKey(), "get",
									null));
			friends = EatBangUtil.parseFriends(json);
		} catch (Exception e) {

		}
		return friends;
	}

	private class InvitationListAdapter extends BaseAdapter {
		private LinkedList<Participator> participators = new LinkedList<Participator>();

		public InvitationListAdapter(Map<Participator, Boolean> participatorMap) {
			super();
			mInflater = LayoutInflater.from(ParticipatorActivity.this);
			Iterator<Participator> i = participatorMap.keySet().iterator();
			while (i.hasNext()) {
				this.participators.add(i.next());
			}
		}

		private void addParticipator(Participator tmpUser) {
			this.participators.add(0, tmpUser);
		}

		@Override
		public int getCount() {
			return participators.size();
		}

		@Override
		public Object getItem(int position) {
			return this.participators.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.invitationitem, null);
			// ImageButton ibPortrait = (ImageButton) convertView
			// .findViewById(R.id.ibPortrait);
			TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
			CheckBox cbParticipate = (CheckBox) convertView
					.findViewById(R.id.cbParticipate);
			cbParticipate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					participatorMap.put(participators.get(position),
							((CheckBox) v).isChecked());
				}
			});
			tvName.setText(participators.get(position).name);
			cbParticipate.setChecked(participatorMap.get(participators
					.get(position)));
			return convertView;
		}
	}

	private class ParticipateOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.equals(btnInvite)) {
				if (EMAIL_ADDRESS_PATTERN.matcher(
						invitationEmail.getText().toString()).matches()) {
					Iterator<Participator> i = participatorMap.keySet()
							.iterator();
					boolean isExist = false;
					while (i.hasNext()) {
						if (invitationEmail.getText().toString()
								.equalsIgnoreCase(i.next().name)) {
							new AlertDialog.Builder(ParticipatorActivity.this)
									.setMessage("已存在")
									.setNeutralButton("OK", null).show();
							isExist = true;
							break;
						}
					}
					if (!isExist) {
						Participator tmpUser = new Participator();
						tmpUser.name = invitationEmail.getText().toString();
						tmpUser.email = invitationEmail.getText().toString();
						participatorMap.put(tmpUser, true);
						invitationListAdapter.addParticipator(tmpUser);
						lvInvitation.setAdapter(invitationListAdapter);
					}
				} else {
					new AlertDialog.Builder(ParticipatorActivity.this)
							.setMessage("非法电子邮箱地址。")
							.setNeutralButton("OK", null).show();
				}
			} else if (v.equals(btnSubmit)) {
				Iterator<Participator> i = participatorMap.keySet().iterator();
				while (i.hasNext()) {
					Participator p = i.next();
					if (participatorMap.get(p)) {
						EatBangDbSQLite sqlite = ((EatBangApplication) ParticipatorActivity.this
								.getApplication()).getPrivacyDbSQLite();
						ArrayList<NameValuePair> participator = new ArrayList<NameValuePair>();
						participator.add(new BasicNameValuePair("e",
								party.editCode));
						participator.add(new BasicNameValuePair("sessionKey",
								sqlite.getSessionKey()));
						if (p.id != null) {
							participator.add(new BasicNameValuePair("fid",
									String.valueOf(p.id)));
						} else {
							participator.add(new BasicNameValuePair("email",
									p.email));
						}
						try {
							EatBangConnection.getInstance().connect(
									"/party/invite", "post", participator);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
