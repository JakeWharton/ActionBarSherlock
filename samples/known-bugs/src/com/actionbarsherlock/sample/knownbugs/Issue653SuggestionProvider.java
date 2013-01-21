package com.actionbarsherlock.sample.knownbugs;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class Issue653SuggestionProvider extends ContentProvider   {
    
	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public String getType(Uri uri) {
		return "";
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    	String[] columns = {
    			BaseColumns._ID, 
    			SearchManager.SUGGEST_COLUMN_TEXT_1, 
    			SearchManager.SUGGEST_COLUMN_INTENT_DATA
    	};
    	MatrixCursor cursor = new MatrixCursor(columns);
    	cursor.addRow(new String[]{"0", "Jack", ""});
    	cursor.addRow(new String[]{"1", "Rose", ""});
    	cursor.addRow(new String[]{"2", "Obama", ""});
    	cursor.addRow(new String[]{"3", "Someone", ""});
    	return cursor;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
        throw new UnsupportedOperationException();
	}
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
        throw new UnsupportedOperationException();
	}
	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        throw new UnsupportedOperationException();
	}

}
