package com.eatbang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class HomeActivity extends Activity {

	private GridView gv;
	private MyOnClickListener myOnClickListener = new MyOnClickListener();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		gv = (GridView) findViewById(R.id.gv);
		gv.setAdapter(new ImageAdapter());
		gv.setOnItemClickListener(myOnClickListener);
	}

	private class ImageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 9;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				LayoutInflater li = getLayoutInflater();
				view = li.inflate(R.layout.griditem, null);

				// TextView tv = (TextView) view.findViewById(R.id.itemText);
				// tv.setText("Item" + position);

				ImageView iv = (ImageView) view.findViewById(R.id.itemImage);
				iv.setImageResource(R.drawable.icon);
			}

			return view;
		}
	}

	private class MyOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				startActivity(new Intent(HomeActivity.this, RListActivity.class));
				break;
			case 1:
				startActivity(new Intent(HomeActivity.this,
						MyPartyMonthActivity.class));
				break;
			case 2:
				startActivity(new Intent(HomeActivity.this,
						ParticipatorActivity.class));
				break;
			case 3:
				startActivity(new Intent(HomeActivity.this,
						NewsFeedActivity.class));
				break;
			case 4:
				startActivity(new Intent(HomeActivity.this,
						MyShareActivity.class));
				break;
			case 5:
				startActivity(new Intent(HomeActivity.this,
						FriendActivity.class));
				break;
			case 6:
				startActivity(new Intent(HomeActivity.this,
						MessageActivity.class));
				break;
			case 7:
				startActivity(new Intent(HomeActivity.this,
						ProfileActivity.class));
				break;
			case 8:
				startActivity(new Intent(HomeActivity.this,
						PartyTabActivity.class));
				break;
			default:
				break;
			}
		}

	}
}