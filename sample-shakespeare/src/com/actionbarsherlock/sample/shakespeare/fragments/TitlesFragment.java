package com.actionbarsherlock.sample.shakespeare.fragments;

import com.actionbarsherlock.sample.shakespeare.R;
import com.actionbarsherlock.sample.shakespeare.Shakespeare;
import com.actionbarsherlock.sample.shakespeare.activities.DetailsActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TitlesFragment extends ListFragment {
    boolean mHasDetailsFrame;
    int mPositionChecked = 0;
    int mPositionShown = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Populate list with our static array of titles.
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Shakespeare.TITLES));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.frame_details);
        mHasDetailsFrame = (detailsFrame != null) && (detailsFrame.getVisibility() == View.VISIBLE);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mPositionChecked = savedInstanceState.getInt("curChoice", 0);
            mPositionShown = savedInstanceState.getInt("shownChoice", -1);
        }

        if (mHasDetailsFrame) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mPositionChecked);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putInt("curChoice", mPositionChecked);
        outState.putInt("shownChoice", mPositionShown);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        mPositionChecked = index;

        if (mHasDetailsFrame) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            if (mPositionShown != mPositionChecked) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                DetailsFragment df = DetailsFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager()
                	.beginTransaction()
                	.replace(R.id.frame_details, df)
                	.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                	.commit();
                
                mPositionShown = index;
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }
}
