package com.eatbang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eatbang.db.schema.Schema;

/**
 * Handle the SQLite database table creation.
 * 
 * @author Xiao Wang
 */
public class EatBangDbOpenHelper extends SQLiteOpenHelper {
	/**
	 * A constant value for database log tag.
	 */
	// private static final String LOG_TAG = "EatBang DB";

	/**
	 * Constructor for creating EatBang database helper.
	 * 
	 * @param context
	 *            The {@code Context} to use for connecting to the SQLite
	 *            database.
	 */
	public EatBangDbOpenHelper(final Context context) {
		super(context, Schema.DB_NAME, null, Schema.VERSION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(final SQLiteDatabase sqliteDB) {
		sqliteDB.execSQL(Schema.SESSION_TABLE_CREATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase sqliteDB, final int oldVersion,
			final int newVersion) {
		Log.d("EatBang", "No upgrades");
	}
}
