package com.actionbarsherlock.app;

import android.support.v4.app.Fragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class SherlockFragment extends Fragment implements OnCreateOptionsMenuListener, OnPrepareOptionsMenuListener, OnOptionsItemSelectedListener {
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Nothing
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //Nothing
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
    
    @Override
    public final void onCreateOptionsMenu(android.view.Menu menu, android.view.MenuInflater inflater) {
        throw new RuntimeException("How did you get here?");
    }
    @Override
    public final boolean onOptionsItemSelected(android.view.MenuItem item) {
        throw new RuntimeException("How did you get here?");
    }
    @Override
    public final void onPrepareOptionsMenu(android.view.Menu menu) {
        throw new RuntimeException("How did you get here?");
    }
}
