package com.eatbang.db.schema;

/**
 * Privacy database schema.
 * 
 * @author Xiao Wang
 */
public class Schema {
	public static final int VERSION = 1;

	public static final String DB_NAME = "eatbang";

	public static final String SESSION_TABLE_NAME = "session";

	public static final String SESSION_TABLE_CREATE = "CREATE TABLE "
			+ SESSION_TABLE_NAME + " (" + SessionTableColumns.SESSION_KEY
			+ " VARCHAR(128) NOT NULL PRIMARY KEY);";

	public static final class SessionTableColumns {
		public static final String SESSION_KEY = "sessionKey";
	}
}
