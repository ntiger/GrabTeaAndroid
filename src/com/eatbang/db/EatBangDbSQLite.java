package com.eatbang.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.util.Log;

import com.eatbang.db.schema.Schema;

/**
 * Class for handling SQLite database operations.
 * 
 * @author Xiao Wang
 */
public class EatBangDbSQLite {
	/**
	 * A constant value for database log tag
	 */
	private static final String LOG_TAG = "EatBang DB";

	private static final String NULL_COLUMN_HACK = "eatbang";

	/**
	 * Helper for accessing the database, managing the connection, and creating
	 * the privacy table if necessary.
	 */
	private final EatBangDbOpenHelper dbHelper;

	/**
	 * A sqlite database operation instance. Got created when this class
	 * initialized.
	 */
	private SQLiteDatabase sqliteDB = null;

	/**
	 * Create a table and get the database instance.
	 * 
	 * @param context
	 *            The {@code Context} to use for connecting to the SQLite
	 *            database.
	 * @throws SQLiteException
	 */
	public EatBangDbSQLite(final Context context) throws SQLiteException {
		dbHelper = new EatBangDbOpenHelper(context);
	}

	public void setSessionKey(String sessionKey) {
		StringBuilder sb = new StringBuilder(sessionKey);
		sqliteDB = dbHelper.getWritableDatabase();
		sqliteDB.beginTransaction();
		ContentValues columns = new ContentValues();
		columns.put(Schema.SessionTableColumns.SESSION_KEY, sb.toString());
		try {
			sqliteDB.insertOrThrow(Schema.SESSION_TABLE_NAME, NULL_COLUMN_HACK,
					columns);
			sqliteDB.setTransactionSuccessful();
		} catch (SQLiteConstraintException e) {
			Log.w(LOG_TAG, "Session key already exists.");
			sqliteDB.update(Schema.SESSION_TABLE_NAME, columns, null, null);
			sqliteDB.setTransactionSuccessful();
		} catch (SQLiteFullException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (SQLiteException e) {
			Log.e(LOG_TAG, e.getMessage());
		} finally {
			sqliteDB.endTransaction();
		}
		return;
	}

	public String getSessionKey() {
		sqliteDB = dbHelper.getReadableDatabase();
		String sessionKey = null;
		Cursor sessionCursor = sqliteDB.query(Schema.SESSION_TABLE_NAME, null,
				null, null, null, null, null);
		int i = sessionCursor.getCount();
		if (sessionCursor.moveToFirst()) {
			sessionKey = sessionCursor
					.getString(sessionCursor
							.getColumnIndexOrThrow(Schema.SessionTableColumns.SESSION_KEY));
		}
		sessionCursor.close();
		return sessionKey;
	}

	/**
	 * Shutdown the database
	 */
	public synchronized void shutdown() {
		sqliteDB.close();
		dbHelper.close();
	}
}
