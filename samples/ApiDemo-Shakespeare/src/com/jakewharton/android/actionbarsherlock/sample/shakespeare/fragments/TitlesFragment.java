package com.jakewharton.android.actionbarsherlock.sample.shakespeare.fragments;

import com.jakewharton.android.actionbarsherlock.sample.shakespeare.R;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.Shakespeare;
import com.jakewharton.android.actionbarsherlock.sample.shakespeare.activities.DetailsActivity;
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
        this.setListAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, Shakespeare.TITLES));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = this.getActivity().findViewById(R.id.frame_details);
        this.mHasDetailsFrame = (detailsFrame != null) && (detailsFrame.getVisibility() == View.VISIBLE);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            this.mPositionChecked = savedInstanceState.getInt("curChoice", 0);
            this.mPositionShown = savedInstanceState.getInt("shownChoice", -1);
        }

        if (this.mHasDetailsFrame) {
            // In dual-pane mode, the list view highlights the selected item.
            this.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            this.showDetails(this.mPositionChecked);
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
        this.showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        this.mPositionChecked = index;

        if (this.mHasDetailsFrame) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            this.getListView().setItemChecked(index, true);

            if (this.mPositionShown != this.mPositionChecked) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                DetailsFragment df = DetailsFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                this.getFragmentManager()
                	.beginTransaction()
                	.replace(R.id.frame_details, df)
                	.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                	.commit();
                
                this.mPositionShown = index;
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra("index", index);
            this.startActivity(intent);
        }
    }
}
