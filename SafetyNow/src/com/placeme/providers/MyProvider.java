package com.placeme.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class MyProvider extends AbstractProvider
{
	@Override
	public String getType(Uri uri)
	{
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String order)
	{
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		return 0;
	}
}