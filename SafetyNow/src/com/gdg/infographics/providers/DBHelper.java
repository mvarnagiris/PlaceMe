package com.gdg.infographics.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database creation and upgrades.
 * 
 * @author Mantas Varnagiris
 */
public class DBHelper extends SQLiteOpenHelper
{
	private static final String	NAME		= "infographics.db";
	private static final int	VERSION		= 1;

	private static DBHelper		instance	= null;

	/**
	 * Uses single instance to have only one connection to database.
	 * 
	 * @param context
	 *            Context.
	 * @return Database connection.
	 */
	public static DBHelper getInstance(Context context)
	{
		if (instance == null)
			instance = new DBHelper(context);
		return instance;
	}

	private DBHelper(Context context)
	{
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		try
		{
			// TODO Create tables
		}
		catch (SQLiteException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	}
}