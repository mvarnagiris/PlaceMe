package com.placeme.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;

import com.placeme.db.Tables;

public abstract class AbstractProvider extends ContentProvider
{
	protected static final String	CONTENT_URI_BASE	= "content://";

	protected static final String	TYPE_LIST_BASE		= "vnd.android.cursor.dir/vnd.gdg.";
	protected static final String	TYPE_ITEM_BASE		= "vnd.android.cursor.item/vnd.gdg.";

	protected final static String	TAG					= "Provider";

	protected UriMatcher			uriMatcher;

	protected SQLiteDatabase		db;

	@Override
	public boolean onCreate()
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		db = DBHelper.getInstance(getContext()).getWritableDatabase();
		return (db == null) ? false : true;
	}

	// Protected methods
	// --------------------------------------------------------------------------------------------------------------------------

	protected long doUpdateOrInsert(String tableName, ContentValues values, boolean returnNewId)
	{
		final String idColumn = BaseColumns._ID;
		final String serverIdColumn = tableName + "_" + Tables.SERVER_ID_SUFFIX;

		final Long id = values.getAsLong(idColumn);
		final Long serverId = values.getAsLong(serverIdColumn);
		long newId = id != null ? id : 0;

		// Find value to check for update
		final String columnToCheck;
		Long valueToCheck;
		if (serverId != null && serverId > 0)
		{
			columnToCheck = serverIdColumn;
			valueToCheck = serverId;
		}
		else
		{
			columnToCheck = idColumn;
			valueToCheck = id != null ? id : 0;
		}

		// Update or insert
		if (db.update(tableName, values, columnToCheck + "=?", new String[] { String.valueOf(valueToCheck) }) == 0)
		{
			newId = db.insert(tableName, null, values);
			if (newId <= 0)
				throw new SQLException("Failed to insert row");
		}

		// Get local ID if necessary
		if (newId == 0 && returnNewId && serverId != null && serverId > 0)
			newId = getLocalId(tableName, serverId);

		return newId;
	}

	protected int doArrayInsert(String tableName, ContentValues[] valuesArray)
	{
		int count = 0;
		for (int i = 0; i < valuesArray.length; i++)
		{
			doUpdateOrInsert(tableName, valuesArray[i], false);
			count++;
		}
		return count;
	}

	protected int doBulkInsert(String tableName, ContentValues[] valuesArray)
	{
		int count = 0;
		try
		{
			db.beginTransaction();
			count = doArrayInsert(tableName, valuesArray);
			db.setTransactionSuccessful();
		}
		catch (SQLiteException e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			db.endTransaction();
		}

		return count;
	}

	protected long getLocalId(String tableName, long serverId)
	{
		long localId = 0;
		Cursor c = null;
		try
		{
			c = db.query(tableName, new String[] { BaseColumns._ID }, tableName + "_" + Tables.SERVER_ID_SUFFIX, new String[] { String.valueOf(serverId) },
							null, null, null);
			if (c != null && c.moveToFirst())
				localId = c.getLong(0);
		}
		finally
		{
			if (c != null && !c.isClosed())
				c.close();
		}
		return localId;
	}

	// Static protected methods
	// --------------------------------------------------------------------------------------------------------------------------

	protected static String getAuthority(Context context, Class<?> clss)
	{
		return context.getPackageName() + ".providers." + clss.getSimpleName();
	}
}