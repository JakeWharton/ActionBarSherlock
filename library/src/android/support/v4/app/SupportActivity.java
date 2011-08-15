package android.support.v4.app;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ActionMode;

public interface SupportActivity {
    //Activity asActivity();

    ActionBar getSupportActionBar();
    FragmentManager getSupportFragmentManager();
    LoaderManager getSupportLoaderManager();
    void invalidateOptionsMenu();
    void onActionModeFinished(ActionMode actionMode);
    //void onAttachFragment(Fragment fragment);
    void recreate();
    boolean requestWindowFeature(long featureId);
    void setProgressBarIndeterminateVisibility(Boolean visible);
    ActionMode startActionMode(final ActionMode.Callback callback);
    void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode);

    SupportInternalCallbacks getInternalCallbacks();
}

abstract class SupportInternalCallbacks {
    abstract void ensureSupportActionBarAttached();
    abstract Handler getHandler();
    abstract FragmentManagerImpl getFragments();
    abstract LoaderManagerImpl getLoaderManager(int index, boolean started, boolean create);
    abstract void invalidateSupportFragmentIndex(int index);
}
