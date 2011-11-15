package com.actionbarsherlock.sample.demos.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.OnCreateOptionsMenuListener;
import com.actionbarsherlock.app.OnOptionsItemSelectedListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;


public class ThrottledLoaderListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, OnCreateOptionsMenuListener, OnOptionsItemSelectedListener {
    static final String TAG = "LoaderThrottle";

    // Menu identifiers
    static final int POPULATE_ID = Menu.FIRST;
    static final int CLEAR_ID = Menu.FIRST+1;

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    // Task we have running to populate the database.
    AsyncTask<Void, Void, Void> mPopulatingTask;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("No data.  Select 'Populate' to fill with data from Z to A at a rate of 4 per second.");
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[] { MainTable.COLUMN_NAME_DATA },
                new int[] { android.R.id.text1 }, 0);
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(Menu.NONE, POPULATE_ID, 0, "Populate")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, CLEAR_ID, 0, "Clear")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        final ContentResolver cr = getActivity().getContentResolver();

        switch (item.getItemId()) {
            case POPULATE_ID:
                if (mPopulatingTask != null) {
                    mPopulatingTask.cancel(false);
                }
                mPopulatingTask = new AsyncTask<Void, Void, Void>() {
                    @Override protected Void doInBackground(Void... params) {
                        for (char c='Z'; c>='A'; c--) {
                            if (isCancelled()) {
                                break;
                            }
                            StringBuilder builder = new StringBuilder("Data ");
                            builder.append(c);
                            ContentValues values = new ContentValues();
                            values.put(MainTable.COLUMN_NAME_DATA, builder.toString());
                            cr.insert(MainTable.CONTENT_URI, values);
                            // Wait a bit between each insert.
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                            }
                        }
                        return null;
                    }
                };
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    mPopulatingTask.execute((Void[])null);
                } else {
                    AsyncTaskCompatHoneycomb.executeOnExecutor(mPopulatingTask);
                }
                return true;

            case CLEAR_ID:
                if (mPopulatingTask != null) {
                    mPopulatingTask.cancel(false);
                    mPopulatingTask = null;
                }
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override protected Void doInBackground(Void... params) {
                        cr.delete(MainTable.CONTENT_URI, null, null);
                        return null;
                    }
                };
                task.execute((Void[])null);
                return true;

            default:
                return false;
        }
    }
    
    static class AsyncTaskCompatHoneycomb {
        static void executeOnExecutor(AsyncTask<Void, Void, Void> task) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        }
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Log.i(TAG, "Item clicked: " + id);
    }

    // These are the rows that we will retrieve.
    static final String[] PROJECTION = new String[] {
        MainTable._ID,
        MainTable.COLUMN_NAME_DATA,
    };

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = new CursorLoader(getActivity(), MainTable.CONTENT_URI,
                PROJECTION, null, null, null);
        cl.setUpdateThrottle(2000); // update at most every 2 seconds.
        return cl;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

/**
 * Definition of the contract for the main table of our provider.
 */
final class MainTable implements BaseColumns {
    public static final String AUTHORITY = "com.actionbarsherlock.sample.demos.apis.LoaderThrottle";

    // This class cannot be instantiated
    private MainTable() {}

    /**
     * The table name offered by this provider
     */
    public static final String TABLE_NAME = "main";

    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI =  Uri.parse("content://" + AUTHORITY + "/main");

    /**
     * The content URI base for a single row of data. Callers must
     * append a numeric row id to this Uri to retrieve a row
     */
    public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse("content://" + AUTHORITY + "/main/");

    /**
     * The MIME type of {@link #CONTENT_URI}.
     */
    public static final String CONTENT_TYPE
            = "vnd.android.cursor.dir/vnd.example.api-demos-throttle";

    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single row.
     */
    public static final String CONTENT_ITEM_TYPE
            = "vnd.android.cursor.item/vnd.example.api-demos-throttle";
    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "data COLLATE LOCALIZED ASC";

    /**
     * Column name for the single column holding our data.
     * <P>Type: TEXT</P>
     */
    public static final String COLUMN_NAME_DATA = "data";
}