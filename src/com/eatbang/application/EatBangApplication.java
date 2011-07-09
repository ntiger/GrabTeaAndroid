package com.eatbang.application;

import android.app.Application;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.eatbang.db.EatBangDbSQLite;

public class EatBangApplication extends Application {
	/**
	 * Database connection instance.
	 */
	private EatBangDbSQLite sqlite;

	/** The tag to pass into {@link Log} when logging things. */
	public static final String LOG_TAG = "PrivacyManagerService";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			sqlite = new EatBangDbSQLite(this);
		} catch (SQLiteException e) {
			Log.e(LOG_TAG, "Error initializing privacy db.", e);
		}
	}

	/**
	 * Get the database connection instance.
	 * 
	 * @return the instance of database connection.
	 */
	public EatBangDbSQLite getPrivacyDbSQLite() {
		return sqlite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		sqlite.shutdown();
	}
}
