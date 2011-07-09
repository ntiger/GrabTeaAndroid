package com.eatbang.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class PartySumActivity extends Activity {
	private String[] groups;
	private String[][] children;
	private Button btnTime;
	private Button btnParticipator;
	private MyOnClickListener onClickListener = new MyOnClickListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partysum);
		groups = getIntent().getStringArrayExtra("date");
		children = new String[groups.length][];
		for (int i = 0; i < groups.length; i++) {
			children[i] = getIntent().getStringArrayExtra(groups[i]);
		}
		btnParticipator = (Button) findViewById(R.id.btnParticipator);
		TextView tvPartyName = (TextView) findViewById(R.id.tvPartyName);
		TextView tvPartyLoc = (TextView) findViewById(R.id.tvPartyLoc);
		TextView tvPartyDesc = (TextView) findViewById(R.id.tvPartyDesc);
		tvPartyName.setText(getIntent().getStringExtra("partyName"));
		tvPartyLoc.setText(getIntent().getStringExtra("partyLoc"));
		tvPartyDesc.setText(getIntent().getStringExtra("partyDesc"));
		btnTime = (Button) findViewById(R.id.btnTime);
		btnParticipator = (Button) findViewById(R.id.btnParticipator);
		btnTime.setOnClickListener(onClickListener);
		btnParticipator.setOnClickListener(onClickListener);

		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.partyTime);
		expandableListView.setAdapter(new ExpandableAdapter(this));
	}

	// ExpandableListView的Adapter
	public class ExpandableAdapter extends BaseExpandableListAdapter {
		Activity activity;

		public ExpandableAdapter(Activity a) {
			activity = a;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return children[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return children[groupPosition].length;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			String string = children[groupPosition][childPosition];
			return getGenericView(string);
		}

		// group method stub
		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		public int getGroupCount() {
			return groups.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			String string = groups[groupPosition];
			return getGenericView(string);
		}

		// View stub to create Group/Children 's View
		public TextView getGenericView(String string) {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);
			TextView text = new TextView(activity);
			text.setLayoutParams(layoutParams);
			// Center the text vertically
			text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			text.setPadding(64, 0, 0, 0);
			text.setText(string);
			text.setTextColor(Color.BLACK);
			return text;
		}

		public boolean hasStableIds() {
			return false;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.equals(btnTime)) {
				Intent intent = new Intent(PartySumActivity.this,
						PartyEditTimeActivity.class);
				intent.putExtras(getIntent().getExtras());
				startActivity(intent);
			} else if (v.equals(btnParticipator)) {
				Intent intent = new Intent(PartySumActivity.this,
						ParticipatorActivity.class);
				intent.putExtras(getIntent().getExtras());
				startActivity(intent);
			}

		}
	}
}
