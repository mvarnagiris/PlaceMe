package com.gdg.infographics.db;

import android.provider.BaseColumns;

/**
 * Information about database tables.
 * 
 * @author Mantas Varnagiris
 */
public class Tables
{
	// Constants
	// --------------------------------------------------------------------------------------------------------------------------

	public static final String	SERVER_ID_SUFFIX	= "server_id";

	// Tables
	// --------------------------------------------------------------------------------------------------------------------------

	public static class Places
	{
		public static final String	TABLE_NAME		= "places";

		public static final String	ID				= BaseColumns._ID;
		public static final String	SERVER_ID		= TABLE_NAME + "_" + SERVER_ID_SUFFIX;

		public static final String	T_ID			= TABLE_NAME + "." + ID;

		public static final String	CREATE_SCRIPT	= "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, " + SERVER_ID
																	+ " integer);";
	}
}