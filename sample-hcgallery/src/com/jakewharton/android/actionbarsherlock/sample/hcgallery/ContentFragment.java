/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jakewharton.android.actionbarsherlock.sample.hcgallery;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

public class ContentFragment extends Fragment {
    private View mContentView;

    // The bitmap currently used by ImageView
    private Bitmap mBitmap = null;

    // Current action mode (contextual action bar, a.k.a. CAB)
    private ActionMode mCurrentActionMode;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.content_welcome, null);
        final ImageView imageView = (ImageView) mContentView.findViewById(R.id.image);
        mContentView.setDrawingCacheEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mContentView.setOnDragListener(new View.OnDragListener() {
                public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_ENTERED:
                            mContentView.setBackgroundColor(
                                    getResources().getColor(R.color.drag_active_color));
                            break;

                        case DragEvent.ACTION_DRAG_EXITED:
                            mContentView.setBackgroundColor(Color.TRANSPARENT);
                            break;

                        case DragEvent.ACTION_DRAG_STARTED:
                            return processDragStarted(event);

                        case DragEvent.ACTION_DROP:
                            mContentView.setBackgroundColor(Color.TRANSPARENT);
                            return processDrop(event, imageView);
                    }
                    return false;
                }
            });
            
            // Keep the action bar visibility in sync with the system status bar. That is, when entering
            // 'lights out mode,' hide the action bar, and when exiting this mode, show the action bar.
        	
            final Activity activity = getActivity();
	        mContentView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    public void onSystemUiVisibilityChange(int visibility) {
                        ActionBar actionBar = activity.getActionBar();
                        if (actionBar != null) {
                            mContentView.setSystemUiVisibility(visibility);
                            if (visibility == View.STATUS_BAR_VISIBLE) {
                                actionBar.show();
                            } else {
                                actionBar.hide();
                            }
                        }
                    }
                }
	        );
	
	        // Show/hide the system status bar when single-clicking a photo. This is also called
	        // 'lights out mode.' Activating and deactivating this mode also invokes the listener
	        // defined above, which will show or hide the action bar accordingly.

	        mContentView.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                if (mContentView.getSystemUiVisibility() == View.STATUS_BAR_VISIBLE) {
	                    mContentView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	                } else {
	                    mContentView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
	                }
	            }
	        });
        }

        // When long-pressing a photo, activate the action mode for selection, showing the
        // contextual action bar (CAB).

        mContentView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (mCurrentActionMode != null) {
                    return false;
                }

                /*
                mCurrentActionMode = getActivity().startActionMode(
                        mContentSelectionActionModeCallback);
                */
                mContentView.setSelected(true);
                return true;
            }
        });

        return mContentView;
    }

    boolean processDragStarted(DragEvent event) {
        // Determine whether to continue processing drag and drop based on the
        // plain text mime type.
        ClipDescription clipDesc = event.getClipDescription();
        if (clipDesc != null) {
            return clipDesc.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
        }
        return false;
    }

    boolean processDrop(DragEvent event, ImageView imageView) {
        // Attempt to parse clip data with expected format: category||entry_id.
        // Ignore event if data does not conform to this format.
        ClipData data = event.getClipData();
        if (data != null) {
            if (data.getItemCount() > 0) {
                Item item = data.getItemAt(0);
                String textData = (String) item.getText();
                if (textData != null) {
                    StringTokenizer tokenizer = new StringTokenizer(textData, "||");
                    if (tokenizer.countTokens() != 2) {
                        return false;
                    }
                    int category = -1;
                    int entryId = -1;
                    try {
                        category = Integer.parseInt(tokenizer.nextToken());
                        entryId = Integer.parseInt(tokenizer.nextToken());
                    } catch (NumberFormatException exception) {
                        return false;
                    }
                    updateContentAndRecycleBitmap(category, entryId);
                    // Update list fragment with selected entry.
                    TitlesFragment titlesFrag = (TitlesFragment)
                            getFragmentManager().findFragmentById(R.id.frag_title);
                    titlesFrag.selectPosition(entryId);
                    return true;
                }
            }
        }
        return false;
    }

    void updateContentAndRecycleBitmap(int category, int position) {
        if (mCurrentActionMode != null) {
            mCurrentActionMode.finish();
        }

        if (mBitmap != null) {
            // This is an advanced call and should be used if you
            // are working with a lot of bitmaps. The bitmap is dead
            // after this call.
            mBitmap.recycle();
        }

        // Get the bitmap that needs to be drawn and update the ImageView
        mBitmap = Directory.getCategory(category).getEntry(position)
                .getBitmap(getResources());
        ((ImageView) getView().findViewById(R.id.image)).setImageBitmap(mBitmap);
    }

    void shareCurrentPhoto() {
        File externalCacheDir = getActivity().getExternalCacheDir();
        if (externalCacheDir == null) {
            Toast.makeText(getActivity(), "Error writing to USB/external storage.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Prevent media scanning of the cache directory.
        final File noMediaFile = new File(externalCacheDir, ".nomedia");
        try {
            noMediaFile.createNewFile();
        } catch (IOException e) {
        }

        // Write the bitmap to temporary storage in the external storage directory (e.g. SD card).
        // We perform the actual disk write operations on a separate thread using the
        // {@link AsyncTask} class, thus avoiding the possibility of stalling the main (UI) thread.

        final File tempFile = new File(externalCacheDir, "tempfile.jpg");

        new AsyncTask<Void, Void, Boolean>() {
            /**
             * Compress and write the bitmap to disk on a separate thread.
             * @return TRUE if the write was successful, FALSE otherwise.
             */
            protected Boolean doInBackground(Void... voids) {
                try {
                    FileOutputStream fo = new FileOutputStream(tempFile, false);
                    if (!mBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fo)) {
                        Toast.makeText(getActivity(), "Error writing bitmap data.",
                                Toast.LENGTH_SHORT).show();
                        return Boolean.FALSE;
                    }
                    return Boolean.TRUE;

                } catch (FileNotFoundException e) {
                    Toast.makeText(getActivity(), "Error writing to USB/external storage.",
                            Toast.LENGTH_SHORT).show();
                    return Boolean.FALSE;
                }
            }

            /**
             * After doInBackground completes (either successfully or in failure), we invoke an
             * intent to share the photo. This code is run on the main (UI) thread.
             */
            protected void onPostExecute(Boolean result) {
                if (result != Boolean.TRUE) {
                    return;
                }

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Share photo"));
            }
        }.execute();
    }

    /**
     * The callback for the 'photo selected' {@link ActionMode}. In this action mode, we can
     * provide contextual actions for the selected photo. We currently only provide the 'share'
     * action, but we could also add clipboard functions such as cut/copy/paste here as well.
     */
    /*
    private ActionMode.Callback mContentSelectionActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.setTitle(R.string.photo_selection_cab_title);

            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.photo_context_menu, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.share:
                    shareCurrentPhoto();
                    actionMode.finish();
                    return true;
            }
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            mContentView.setSelected(false);
            mCurrentActionMode = null;
        }
    };
    */
}
