/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.actionbarsherlock.widget;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.actionbarsherlock.R;
import com.actionbarsherlock.view.CollapsibleActionView;

import java.lang.reflect.Method;
import java.util.WeakHashMap;

import static com.actionbarsherlock.widget.SuggestionsAdapter.getColumnString;

/**
 * A widget that provides a user interface for the user to enter a search query and submit a request
 * to a search provider. Shows a list of query suggestions or results, if available, and allows the
 * user to pick a suggestion or result to launch into.
 *
 * <p>
 * When the SearchView is used in an ActionBar as an action view for a collapsible menu item, it
 * needs to be set to iconified by default using {@link #setIconifiedByDefault(boolean)
 * setIconifiedByDefault(true)}. This is the default, so nothing needs to be done.
 * </p>
 * <p>
 * If you want the search field to always be visible, then call setIconifiedByDefault(false).
 * </p>
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For information about using {@code SearchView}, read the
 * <a href="{@docRoot}guide/topics/search/index.html">Search</a> developer guide.</p>
 * </div>
 *
 * @see android.view.MenuItem#SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
 * @attr ref android.R.styleable#SearchView_iconifiedByDefault
 * @attr ref android.R.styleable#SearchView_imeOptions
 * @attr ref android.R.styleable#SearchView_inputType
 * @attr ref android.R.styleable#SearchView_maxWidth
 * @attr ref android.R.styleable#SearchView_queryHint
 */
public class SearchView extends LinearLayout implements CollapsibleActionView {

  private static final boolean DBG = false;
  private static final String LOG_TAG = "SearchView";

  /**
   * Private constant for removing the microphone in the keyboard.
   */
  private static final String IME_OPTION_NO_MICROPHONE = "nm";

  private OnQueryTextListener mOnQueryChangeListener;
  private OnCloseListener mOnCloseListener;
  private OnFocusChangeListener mOnQueryTextFocusChangeListener;
  private OnSuggestionListener mOnSuggestionListener;
  private OnClickListener mOnSearchClickListener;

  private boolean mIconifiedByDefault;
  private boolean mIconified;
  private CursorAdapter mSuggestionsAdapter;
  private View mSearchButton;
  private View mSubmitButton;
  private View mSearchPlate;
  private View mSubmitArea;
  private ImageView mCloseButton;
  private View mSearchEditFrame;
  private SearchAutoComplete mQueryTextView;
  private View mDropDownAnchor;
  private ImageView mSearchHintIcon;
  private boolean mSubmitButtonEnabled;
  private CharSequence mQueryHint;
  private boolean mClearingFocus;
  private int mMaxWidth;
  private CharSequence mOldQueryText;
  private CharSequence mUserQuery;
  private boolean mExpandedInActionView;
  private int mCollapsedImeOptions;

  private Bundle mAppSearchData;

  /*
  * SearchView can be set expanded before the IME is ready to be shown during
  * initial UI setup. The show operation is asynchronous to account for this.
  */
  private Runnable mShowImeRunnable = new Runnable() {
    public void run() {
      InputMethodManager imm = (InputMethodManager)
          getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

      if (imm != null) {
        showSoftInputUnchecked(SearchView.this, imm, 0);
      }
    }
  };

  private Runnable mUpdateDrawableStateRunnable = new Runnable() {
    public void run() {
      updateFocusedState();
    }
  };

  private Runnable mReleaseCursorRunnable = new Runnable() {
    public void run() {
      if (mSuggestionsAdapter != null && mSuggestionsAdapter instanceof SuggestionsAdapter) {
        mSuggestionsAdapter.changeCursor(null);
      }
    }
  };

  // For voice searching
  //private final Intent mVoiceWebSearchIntent;
  //private final Intent mVoiceAppSearchIntent;

  // A weak map of drawables we've gotten from other packages, so we don't load them
  // more than once.
  private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache =
      new WeakHashMap<String, Drawable.ConstantState>();

  /**
   * Callbacks for changes to the query text.
   */
  public interface OnQueryTextListener {

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     *
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    boolean onQueryTextSubmit(String query);

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     *
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    boolean onQueryTextChange(String newText);
  }

  public interface OnCloseListener {

    /**
     * The user is attempting to close the SearchView.
     *
     * @return true if the listener wants to override the default behavior of clearing the
     * text field and dismissing it, false otherwise.
     */
    boolean onClose();
  }

  /**
   * Callback interface for selection events on suggestions. These callbacks
   * are only relevant when a SearchableInfo has been specified by {@link #setSearchableInfo}.
   */
  public interface OnSuggestionListener {

    /**
     * Called when a suggestion was selected by navigating to it.
     * @param position the absolute position in the list of suggestions.
     *
     * @return true if the listener handles the event and wants to override the default
     * behavior of possibly rewriting the query based on the selected item, false otherwise.
     */
    boolean onSuggestionSelect(int position);

    /**
     * Called when a suggestion was clicked.
     * @param position the absolute position of the clicked item in the list of suggestions.
     *
     * @return true if the listener handles the event and wants to override the default
     * behavior of launching any intent or submitting a search query specified on that item.
     * Return false otherwise.
     */
    boolean onSuggestionClick(int position);
  }

  public SearchView(Context context) {
    this(context, null);
  }

  public SearchView(Context context, AttributeSet attrs) {
    super(context, attrs);

    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.abs__search_view, this, true);

    mSearchButton = findViewById(R.id.abs__search_button);
    mQueryTextView = (SearchAutoComplete) findViewById(R.id.abs__search_src_text);
    mQueryTextView.setSearchView(this);

    mSearchEditFrame = findViewById(R.id.abs__search_edit_frame);
    mSearchPlate = findViewById(R.id.abs__search_plate);
    mSubmitArea = findViewById(R.id.abs__submit_area);
    mSubmitButton = findViewById(R.id.abs__search_go_btn);
    mCloseButton = (ImageView) findViewById(R.id.abs__search_close_btn);
    mSearchHintIcon = (ImageView) findViewById(R.id.abs__search_mag_icon);

    mSearchButton.setOnClickListener(mOnClickListener);
    mCloseButton.setOnClickListener(mOnClickListener);
    mSubmitButton.setOnClickListener(mOnClickListener);
    mQueryTextView.setOnClickListener(mOnClickListener);

    mQueryTextView.addTextChangedListener(mTextWatcher);
    mQueryTextView.setOnEditorActionListener(mOnEditorActionListener);
    mQueryTextView.setOnItemClickListener(mOnItemClickListener);
    mQueryTextView.setOnItemSelectedListener(mOnItemSelectedListener);
    mQueryTextView.setOnKeyListener(mTextKeyListener);
    // Inform any listener of focus changes
    mQueryTextView.setOnFocusChangeListener(new OnFocusChangeListener() {

      public void onFocusChange(View v, boolean hasFocus) {
        if (mOnQueryTextFocusChangeListener != null) {
          mOnQueryTextFocusChangeListener.onFocusChange(SearchView.this, hasFocus);
        }
      }
    });

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SherlockSearchView, 0, 0);
    setIconifiedByDefault(a.getBoolean(R.styleable.SherlockSearchView_iconifiedByDefault, true));
    int maxWidth = a.getDimensionPixelSize(R.styleable.SherlockSearchView_android_maxWidth, -1);
    if (maxWidth != -1) {
      setMaxWidth(maxWidth);
    }
    CharSequence queryHint = a.getText(R.styleable.SherlockSearchView_queryHint);
    if (!TextUtils.isEmpty(queryHint)) {
      setQueryHint(queryHint);
    }
    int imeOptions = a.getInt(R.styleable.SherlockSearchView_android_imeOptions, -1);
    if (imeOptions != -1) {
      setImeOptions(imeOptions);
    }
    int inputType = a.getInt(R.styleable.SherlockSearchView_android_inputType, -1);
    if (inputType != -1) {
      setInputType(inputType);
    }

    a.recycle();

    boolean focusable = true;

    a = context.obtainStyledAttributes(attrs, R.styleable.SherlockView, 0, 0);
    focusable = a.getBoolean(R.styleable.SherlockView_android_focusable, focusable);
    a.recycle();
    setFocusable(focusable);

    // Save voice intent for later queries/launching
    //mVoiceWebSearchIntent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
    //mVoiceWebSearchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //mVoiceWebSearchIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
    //    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

    //mVoiceAppSearchIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    //mVoiceAppSearchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    mDropDownAnchor = findViewById(mQueryTextView.getDropDownAnchor());
    if (mDropDownAnchor != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        mDropDownAnchor.addOnLayoutChangeListener(new OnLayoutChangeListener() {
          @Override
          public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                     int oldLeft, int oldTop, int oldRight, int oldBottom) {
            adjustDropDownSizeAndPosition();
          }
        });
      } else {
        mDropDownAnchor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            adjustDropDownSizeAndPosition();
          }
        });
      }
    }

    updateViewsVisibility(mIconifiedByDefault);
    updateQueryHint();
  }

  /**
   * Sets the APP_DATA for legacy SearchDialog use.
   * @param appSearchData bundle provided by the app when launching the search dialog
   * @hide
   */
  public void setAppSearchData(Bundle appSearchData) {
    mAppSearchData = appSearchData;
  }

  /**
   * Sets the IME options on the query text field.
   *
   * @see TextView#setImeOptions(int)
   * @param imeOptions the options to set on the query text field
   *
   * @attr ref android.R.styleable#SearchView_imeOptions
   */
  public void setImeOptions(int imeOptions) {
    mQueryTextView.setImeOptions(imeOptions);
  }

  /**
   * Sets the input type on the query text field.
   *
   * @see TextView#setInputType(int)
   * @param inputType the input type to set on the query text field
   *
   * @attr ref android.R.styleable#SearchView_inputType
   */
  public void setInputType(int inputType) {
    mQueryTextView.setInputType(inputType);
  }

  /** @hide */
  @Override
  public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
    // Don't accept focus if in the middle of clearing focus
    if (mClearingFocus) return false;
    // Check if SearchView is focusable.
    if (!isFocusable()) return false;
    // If it is not iconified, then give the focus to the text field
    if (!isIconified()) {
      boolean result = mQueryTextView.requestFocus(direction, previouslyFocusedRect);
      if (result) {
        updateViewsVisibility(false);
      }
      return result;
    } else {
      return super.requestFocus(direction, previouslyFocusedRect);
    }
  }

  /** @hide */
  @Override
  public void clearFocus() {
    mClearingFocus = true;
    setImeVisibility(false);
    super.clearFocus();
    mQueryTextView.clearFocus();
    mClearingFocus = false;
  }

  /**
   * Sets a listener for user actions within the SearchView.
   *
   * @param listener the listener object that receives callbacks when the user performs
   * actions in the SearchView such as clicking on buttons or typing a query.
   */
  public void setOnQueryTextListener(OnQueryTextListener listener) {
    mOnQueryChangeListener = listener;
  }

  /**
   * Sets a listener to inform when the user closes the SearchView.
   *
   * @param listener the listener to call when the user closes the SearchView.
   */
  public void setOnCloseListener(OnCloseListener listener) {
    mOnCloseListener = listener;
  }

  /**
   * Sets a listener to inform when the focus of the query text field changes.
   *
   * @param listener the listener to inform of focus changes.
   */
  public void setOnQueryTextFocusChangeListener(OnFocusChangeListener listener) {
    mOnQueryTextFocusChangeListener = listener;
  }

  /**
   * Sets a listener to inform when a suggestion is focused or clicked.
   *
   * @param listener the listener to inform of suggestion selection events.
   */
  public void setOnSuggestionListener(OnSuggestionListener listener) {
    mOnSuggestionListener = listener;
  }

  /**
   * Sets a listener to inform when the search button is pressed. This is only
   * relevant when the text field is not visible by default. Calling {@link #setIconified
   * setIconified(false)} can also cause this listener to be informed.
   *
   * @param listener the listener to inform when the search button is clicked or
   * the text field is programmatically de-iconified.
   */
  public void setOnSearchClickListener(OnClickListener listener) {
    mOnSearchClickListener = listener;
  }

  /**
   * Returns the query string currently in the text field.
   *
   * @return the query string
   */
  public CharSequence getQuery() {
    return mQueryTextView.getText();
  }

  /**
   * Sets a query string in the text field and optionally submits the query as well.
   *
   * @param query the query string. This replaces any query text already present in the
   * text field.
   * @param submit whether to submit the query right now or only update the contents of
   * text field.
   */
  public void setQuery(CharSequence query, boolean submit) {
    mQueryTextView.setText(query);
    if (query != null) {
      mQueryTextView.setSelection(query.length());
      mUserQuery = query;
    }

    // If the query is not empty and submit is requested, submit the query
    if (submit && !TextUtils.isEmpty(query)) {
      onSubmitQuery();
    }
  }

  /**
   * Sets the hint text to display in the query text field. This overrides any hint specified
   * in the SearchableInfo.
   *
   * @param hint the hint text to display
   *
   * @attr ref android.R.styleable#SearchView_queryHint
   */
  public void setQueryHint(CharSequence hint) {
    mQueryHint = hint;
    updateQueryHint();
  }

  /**
   * Sets the default or resting state of the search field. If true, a single search icon is
   * shown by default and expands to show the text field and other buttons when pressed. Also,
   * if the default state is iconified, then it collapses to that state when the close button
   * is pressed. Changes to this property will take effect immediately.
   *
   * <p>The default value is true.</p>
   *
   * @param iconified whether the search field should be iconified by default
   *
   * @attr ref android.R.styleable#SearchView_iconifiedByDefault
   */
  public void setIconifiedByDefault(boolean iconified) {
    if (mIconifiedByDefault == iconified) return;
    mIconifiedByDefault = iconified;
    updateViewsVisibility(iconified);
    updateQueryHint();
  }

  /**
   * Returns the default iconified state of the search field.
   * @return
   */
  public boolean isIconfiedByDefault() {
    return mIconifiedByDefault;
  }

  /**
   * Iconifies or expands the SearchView. Any query text is cleared when iconified. This is
   * a temporary state and does not override the default iconified state set by
   * {@link #setIconifiedByDefault(boolean)}. If the default state is iconified, then
   * a false here will only be valid until the user closes the field. And if the default
   * state is expanded, then a true here will only clear the text field and not close it.
   *
   * @param iconify a true value will collapse the SearchView to an icon, while a false will
   * expand it.
   */
  public void setIconified(boolean iconify) {
    if (iconify) {
      onCloseClicked();
    } else {
      onSearchClicked();
    }
  }

  /**
   * Returns the current iconified state of the SearchView.
   *
   * @return true if the SearchView is currently iconified, false if the search field is
   * fully visible.
   */
  public boolean isIconified() {
    return mIconified;
  }

  /**
   * Enables showing a submit button when the query is non-empty. In cases where the SearchView
   * is being used to filter the contents of the current activity and doesn't launch a separate
   * results activity, then the submit button should be disabled.
   *
   * @param enabled true to show a submit button for submitting queries, false if a submit
   * button is not required.
   */
  public void setSubmitButtonEnabled(boolean enabled) {
    mSubmitButtonEnabled = enabled;
    updateViewsVisibility(isIconified());
  }

  /**
   * Returns whether the submit button is enabled when necessary or never displayed.
   *
   * @return whether the submit button is enabled automatically when necessary
   */
  public boolean isSubmitButtonEnabled() {
    return mSubmitButtonEnabled;
  }

  /**
   * You can set a custom adapter if you wish. Otherwise the default adapter is used to
   * display the suggestions from the suggestions provider associated with the SearchableInfo.
   */
  public void setSuggestionsAdapter(CursorAdapter adapter) {
    mSuggestionsAdapter = adapter;

    mQueryTextView.setAdapter(mSuggestionsAdapter);
  }

  /**
   * Returns the adapter used for suggestions, if any.
   * @return the suggestions adapter
   */
  public CursorAdapter getSuggestionsAdapter() {
    return mSuggestionsAdapter;
  }

  /**
   * Makes the view at most this many pixels wide
   *
   * @attr ref android.R.styleable#SearchView_maxWidth
   */
  public void setMaxWidth(int maxpixels) {
    mMaxWidth = maxpixels;

    requestLayout();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Let the standard measurements take effect in iconified state.
    if (isIconified()) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      return;
    }

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);

    switch (widthMode) {
      case MeasureSpec.AT_MOST:
        // If there is an upper limit, don't exceed maximum width (explicit or implicit)
        if (mMaxWidth > 0) {
          width = Math.min(mMaxWidth, width);
        } else {
          width = Math.min(getPreferredWidth(), width);
        }
        break;
      case MeasureSpec.EXACTLY:
        // If an exact width is specified, still don't exceed any specified maximum width
        if (mMaxWidth > 0) {
          width = Math.min(mMaxWidth, width);
        }
        break;
      case MeasureSpec.UNSPECIFIED:
        // Use maximum width, if specified, else preferred width
        width = mMaxWidth > 0 ? mMaxWidth : getPreferredWidth();
        break;
    }
    widthMode = MeasureSpec.EXACTLY;
    super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode), heightMeasureSpec);
  }

  private int getPreferredWidth() {
    return getContext().getResources()
        .getDimensionPixelSize(R.dimen.abs__search_view_preferred_width);
  }

  private void updateViewsVisibility(final boolean collapsed) {
    mIconified = collapsed;
    // Visibility of views that are visible when collapsed
    final int visCollapsed = collapsed ? VISIBLE : GONE;
    // Is there text in the query
    final boolean hasText = !TextUtils.isEmpty(mQueryTextView.getText());

    mSearchButton.setVisibility(visCollapsed);
    updateSubmitButton(hasText);
    mSearchEditFrame.setVisibility(collapsed ? GONE : VISIBLE);
    mSearchHintIcon.setVisibility(mIconifiedByDefault ? GONE : VISIBLE);
    updateCloseButton();
    updateSubmitArea();
  }

  private boolean isSubmitAreaEnabled() {
    return mSubmitButtonEnabled && !isIconified();
  }

  private void updateSubmitButton(boolean hasText) {
    int visibility = GONE;
    if (mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus()
        && hasText) {
      visibility = VISIBLE;
    }
    mSubmitButton.setVisibility(visibility);
  }

  private void updateSubmitArea() {
    int visibility = GONE;
    if (isSubmitAreaEnabled()
        && mSubmitButton.getVisibility() == VISIBLE) {
      visibility = VISIBLE;
    }
    mSubmitArea.setVisibility(visibility);
  }

  private void updateCloseButton() {
    final boolean hasText = !TextUtils.isEmpty(mQueryTextView.getText());
    // Should we show the close button? It is not shown if there's no focus,
    // field is not iconified by default and there is no text in it.
    final boolean showClose = hasText || (mIconifiedByDefault && !mExpandedInActionView);
    mCloseButton.setVisibility(showClose ? VISIBLE : GONE);
    mCloseButton.getDrawable().setState(hasText ? ENABLED_STATE_SET : EMPTY_STATE_SET);
  }

  private void postUpdateFocusedState() {
    post(mUpdateDrawableStateRunnable);
  }

  private void updateFocusedState() {
    boolean focused = mQueryTextView.hasFocus();
    mSearchPlate.getBackground().setState(focused ? FOCUSED_STATE_SET : EMPTY_STATE_SET);
    mSubmitArea.getBackground().setState(focused ? FOCUSED_STATE_SET : EMPTY_STATE_SET);
    invalidate();
  }

  @Override
  protected void onDetachedFromWindow() {
    removeCallbacks(mUpdateDrawableStateRunnable);
    post(mReleaseCursorRunnable);
    super.onDetachedFromWindow();
  }

  private void setImeVisibility(final boolean visible) {
    if (visible) {
      post(mShowImeRunnable);
    } else {
      removeCallbacks(mShowImeRunnable);
      InputMethodManager imm = (InputMethodManager)
          getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

      if (imm != null) {
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
      }
    }
  }

  /**
   * Called by the SuggestionsAdapter
   * @hide
   */
  /* package */void onQueryRefine(CharSequence queryText) {
    setQuery(queryText);
  }

  private final OnClickListener mOnClickListener = new OnClickListener() {

    public void onClick(View v) {
      if (v == mSearchButton) {
        onSearchClicked();
      } else if (v == mCloseButton) {
        onCloseClicked();
      } else if (v == mSubmitButton) {
        onSubmitQuery();
      } else if (v == mQueryTextView) {
        forceSuggestionQuery();
      }
    }
  };

  /**
   * React to the user typing "enter" or other hardwired keys while typing in
   * the search box. This handles these special keys while the edit box has
   * focus.
   */
  View.OnKeyListener mTextKeyListener = new View.OnKeyListener() {
    public boolean onKey(View v, int keyCode, KeyEvent event) {
      if (DBG) {
        Log.d(LOG_TAG, "mTextListener.onKey(" + keyCode + "," + event + "), selection: "
            + mQueryTextView.getListSelection());
      }

      // If a suggestion is selected, handle enter, search key, and action keys
      // as presses on the selected suggestion
      if (mQueryTextView.isPopupShowing()
          && mQueryTextView.getListSelection() != ListView.INVALID_POSITION) {
        return onSuggestionsKey(v, keyCode, event);
      }

      // If there is text in the query box, handle enter, and action keys
      // The search key is handled by the dialog's onKeyDown().
      if (!mQueryTextView.isEmpty() && KeyEventCompat.hasNoModifiers(event)) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
          if (keyCode == KeyEvent.KEYCODE_ENTER) {
            v.cancelLongPress();

            // Launch as a regular search.
            launchQuerySearch(KeyEvent.KEYCODE_UNKNOWN, null, mQueryTextView.getText()
                .toString());
            return true;
          }
        }
      }
      return false;
    }
  };

  /**
   * React to the user typing while in the suggestions list. First, check for
   * action keys. If not handled, try refocusing regular characters into the
   * EditText.
   */
  private boolean onSuggestionsKey(View v, int keyCode, KeyEvent event) {
    if (mSuggestionsAdapter == null) {
      return false;
    }
    if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEventCompat.hasNoModifiers(event)) {
      // First, check for enter or search (both of which we'll treat as a
      // "click")
      if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SEARCH
          || keyCode == KeyEvent.KEYCODE_TAB) {
        int position = mQueryTextView.getListSelection();
        return onItemClicked(position, KeyEvent.KEYCODE_UNKNOWN, null);
      }

      // Next, check for left/right moves, which we use to "return" the
      // user to the edit view
      if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        // give "focus" to text editor, with cursor at the beginning if
        // left key, at end if right key
        // TODO: Reverse left/right for right-to-left languages, e.g.
        // Arabic
        int selPoint = (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) ? 0 : mQueryTextView
            .length();
        mQueryTextView.setSelection(selPoint);
        mQueryTextView.setListSelection(0);
        mQueryTextView.clearListSelection();
        ensureImeVisible(mQueryTextView, true);

        return true;
      }

      // Next, check for an "up and out" move
      if (keyCode == KeyEvent.KEYCODE_DPAD_UP && 0 == mQueryTextView.getListSelection()) {
        // TODO: restoreUserQuery();
        // let ACTV complete the move
        return false;
      }
    }
    return false;
  }

  private int getSearchIconId() {
    TypedValue outValue = new TypedValue();
    getContext().getTheme().resolveAttribute(R.attr.searchViewSearchIcon,
        outValue, true);
    return outValue.resourceId;
  }

  private CharSequence getDecoratedHint(CharSequence hintText) {
    // If the field is always expanded, then don't add the search icon to the hint
    if (!mIconifiedByDefault) return hintText;

    SpannableStringBuilder ssb = new SpannableStringBuilder("   "); // for the icon
    ssb.append(hintText);
    Drawable searchIcon = getContext().getResources().getDrawable(getSearchIconId());
    int textSize = (int) (mQueryTextView.getTextSize() * 1.25);
    searchIcon.setBounds(0, 0, textSize, textSize);
    ssb.setSpan(new ImageSpan(searchIcon), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return ssb;
  }

  private void updateQueryHint() {
    if (mQueryHint != null) {
      mQueryTextView.setHint(getDecoratedHint(mQueryHint));
    } else {
      mQueryTextView.setHint(getDecoratedHint(""));
    }
  }

  private final OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {

    /**
     * Called when the input method default action key is pressed.
     */
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
      onSubmitQuery();
      return true;
    }
  };

  private void onTextChanged(CharSequence newText) {
    CharSequence text = mQueryTextView.getText();
    mUserQuery = text;
    boolean hasText = !TextUtils.isEmpty(text);
    updateSubmitButton(hasText);
    updateCloseButton();
    updateSubmitArea();
    if (mOnQueryChangeListener != null && !TextUtils.equals(newText, mOldQueryText)) {
      mOnQueryChangeListener.onQueryTextChange(newText.toString());
    }
    mOldQueryText = newText.toString();
  }

  private void onSubmitQuery() {
    CharSequence query = mQueryTextView.getText();
    if (query != null && TextUtils.getTrimmedLength(query) > 0) {
      if (mOnQueryChangeListener == null
          || !mOnQueryChangeListener.onQueryTextSubmit(query.toString())) {
        dismissSuggestions();
      }
    }
  }

  private void dismissSuggestions() {
    mQueryTextView.dismissDropDown();
  }

  private void onCloseClicked() {
    CharSequence text = mQueryTextView.getText();
    if (TextUtils.isEmpty(text)) {
      if (mIconifiedByDefault) {
        // If the app doesn't override the close behavior
        if (mOnCloseListener == null || !mOnCloseListener.onClose()) {
          // hide the keyboard and remove focus
          clearFocus();
          // collapse the search field
          updateViewsVisibility(true);
        }
      }
    } else {
      mQueryTextView.setText("");
      mQueryTextView.requestFocus();
      setImeVisibility(true);
    }

  }

  private void onSearchClicked() {
    updateViewsVisibility(false);
    mQueryTextView.requestFocus();
    setImeVisibility(true);
    if (mOnSearchClickListener != null) {
      mOnSearchClickListener.onClick(this);
    }
  }

  void onTextFocusChanged() {
    updateViewsVisibility(isIconified());
    // Delayed update to make sure that the focus has settled down and window focus changes
    // don't affect it. A synchronous update was not working.
    postUpdateFocusedState();
    if (mQueryTextView.hasFocus()) {
      forceSuggestionQuery();
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasWindowFocus) {
    super.onWindowFocusChanged(hasWindowFocus);

    postUpdateFocusedState();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onActionViewCollapsed() {
    clearFocus();
    updateViewsVisibility(true);
    mQueryTextView.setImeOptions(mCollapsedImeOptions);
    mExpandedInActionView = false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onActionViewExpanded() {
    if (mExpandedInActionView) return;

    mExpandedInActionView = true;
    mCollapsedImeOptions = mQueryTextView.getImeOptions();
    mQueryTextView.setImeOptions(mCollapsedImeOptions | EditorInfo.IME_FLAG_NO_FULLSCREEN);
    mQueryTextView.setText("");
    setIconified(false);
  }

  private void adjustDropDownSizeAndPosition() {
    if (mDropDownAnchor.getWidth() > 1) {
      Resources res = getContext().getResources();
      int anchorPadding = mSearchPlate.getPaddingLeft();
      Rect dropDownPadding = new Rect();
      int iconOffset = mIconifiedByDefault
          ? res.getDimensionPixelSize(R.dimen.abs__dropdownitem_icon_width)
          + res.getDimensionPixelSize(R.dimen.abs__dropdownitem_text_padding_left)
          : 0;
      mQueryTextView.getDropDownBackground().getPadding(dropDownPadding);
      mQueryTextView.setDropDownHorizontalOffset(-(dropDownPadding.left + iconOffset)
          + anchorPadding);
      mQueryTextView.setDropDownWidth(mDropDownAnchor.getWidth() + dropDownPadding.left
          + dropDownPadding.right + iconOffset - (anchorPadding));
    }
  }

  private boolean onItemClicked(int position, int actionKey, String actionMsg) {
    if (mOnSuggestionListener == null
        || !mOnSuggestionListener.onSuggestionClick(position)) {
      launchSuggestion(position, KeyEvent.KEYCODE_UNKNOWN, null);
      setImeVisibility(false);
      dismissSuggestions();
      return true;
    }
    return false;
  }

  private boolean onItemSelected(int position) {
    if (mOnSuggestionListener == null
        || !mOnSuggestionListener.onSuggestionSelect(position)) {
      rewriteQueryFromSuggestion(position);
      return true;
    }
    return false;
  }

  private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

    /**
     * Implements OnItemClickListener
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      if (DBG) Log.d(LOG_TAG, "onItemClick() position " + position);
      onItemClicked(position, KeyEvent.KEYCODE_UNKNOWN, null);
    }
  };

  private final OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {

    /**
     * Implements OnItemSelectedListener
     */
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      if (DBG) Log.d(LOG_TAG, "onItemSelected() position " + position);
      SearchView.this.onItemSelected(position);
    }

    /**
     * Implements OnItemSelectedListener
     */
    public void onNothingSelected(AdapterView<?> parent) {
      if (DBG)
        Log.d(LOG_TAG, "onNothingSelected()");
    }
  };

  /**
   * Query rewriting.
   */
  private void rewriteQueryFromSuggestion(int position) {
    CharSequence oldQuery = mQueryTextView.getText();
    Cursor c = mSuggestionsAdapter.getCursor();
    if (c == null) {
      return;
    }
    if (c.moveToPosition(position)) {
      // Get the new query from the suggestion.
      CharSequence newQuery = mSuggestionsAdapter.convertToString(c);
      if (newQuery != null) {
        // The suggestion rewrites the query.
        // Update the text field, without getting new suggestions.
        setQuery(newQuery);
      } else {
        // The suggestion does not rewrite the query, restore the user's query.
        setQuery(oldQuery);
      }
    } else {
      // We got a bad position, restore the user's query.
      setQuery(oldQuery);
    }
  }

  /**
   * Launches an intent based on a suggestion.
   *
   * @param position The index of the suggestion to create the intent from.
   * @param actionKey The key code of the action key that was pressed,
   *        or {@link KeyEvent#KEYCODE_UNKNOWN} if none.
   * @param actionMsg The message for the action key that was pressed,
   *        or <code>null</code> if none.
   * @return true if a successful launch, false if could not (e.g. bad position).
   */
  private boolean launchSuggestion(int position, int actionKey, String actionMsg) {
    Cursor c = mSuggestionsAdapter.getCursor();
    if ((c != null) && c.moveToPosition(position)) {

      Intent intent = createIntentFromSuggestion(c, actionKey, actionMsg);

      // launch the intent
      launchIntent(intent);

      return true;
    }
    return false;
  }

  /**
   * Launches an intent, including any special intent handling.
   */
  private void launchIntent(Intent intent) {
    if (intent == null) {
      return;
    }
    try {
      // If the intent was created from a suggestion, it will always have an explicit
      // component here.
      getContext().startActivity(intent);
    } catch (RuntimeException ex) {
      Log.e(LOG_TAG, "Failed launch activity: " + intent, ex);
    }
  }

  /**
   * Sets the text in the query box, without updating the suggestions.
   */
  private void setQuery(CharSequence query) {
    setText(mQueryTextView, query, true);
    // Move the cursor to the end
    mQueryTextView.setSelection(TextUtils.isEmpty(query) ? 0 : query.length());
  }

  private void launchQuerySearch(int actionKey, String actionMsg, String query) {
    String action = Intent.ACTION_SEARCH;
    Intent intent = createIntent(action, null, null, query, actionKey, actionMsg);
    getContext().startActivity(intent);
  }

  /**
   * Constructs an intent from the given information and the search dialog state.
   *
   * @param action Intent action.
   * @param data Intent data, or <code>null</code>.
   * @param extraData Data for {@link SearchManager#EXTRA_DATA_KEY} or <code>null</code>.
   * @param query Intent query, or <code>null</code>.
   * @param actionKey The key code of the action key that was pressed,
   *        or {@link KeyEvent#KEYCODE_UNKNOWN} if none.
   * @param actionMsg The message for the action key that was pressed,
   *        or <code>null</code> if none.
   * @return The intent.
   */
  private Intent createIntent(String action, Uri data, String extraData, String query,
                              int actionKey, String actionMsg) {
    // Now build the Intent
    Intent intent = new Intent(action);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // We need CLEAR_TOP to avoid reusing an old task that has other activities
    // on top of the one we want. We don't want to do this in in-app search though,
    // as it can be destructive to the activity stack.
    if (data != null) {
      intent.setData(data);
    }
    intent.putExtra(SearchManager.USER_QUERY, mUserQuery);
    if (query != null) {
      intent.putExtra(SearchManager.QUERY, query);
    }
    if (extraData != null) {
      intent.putExtra(SearchManager.EXTRA_DATA_KEY, extraData);
    }
    if (mAppSearchData != null) {
      intent.putExtra(SearchManager.APP_DATA, mAppSearchData);
    }
    if (actionKey != KeyEvent.KEYCODE_UNKNOWN) {
      intent.putExtra(SearchManager.ACTION_KEY, actionKey);
      intent.putExtra(SearchManager.ACTION_MSG, actionMsg);
    }
    // TODO intent.setComponent(mSearchable.getSearchActivity());
    return intent;
  }

  /**
   * When a particular suggestion has been selected, perform the various lookups required
   * to use the suggestion.  This includes checking the cursor for suggestion-specific data,
   * and/or falling back to the XML for defaults;  It also creates REST style Uri data when
   * the suggestion includes a data id.
   *
   * @param c The suggestions cursor, moved to the row of the user's selection
   * @param actionKey The key code of the action key that was pressed,
   *        or {@link KeyEvent#KEYCODE_UNKNOWN} if none.
   * @param actionMsg The message for the action key that was pressed,
   *        or <code>null</code> if none.
   * @return An intent for the suggestion at the cursor's position.
   */
  private Intent createIntentFromSuggestion(Cursor c, int actionKey, String actionMsg) {
    try {
      // use specific action if supplied, or default action if supplied, or fixed default
      String action = getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_ACTION);

      if (action == null) {
        action = Intent.ACTION_SEARCH;
      }

      // use specific data if supplied, or default data if supplied
      String data = getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_DATA);
      // then, if an ID was provided, append it.
      if (data != null) {
        String id = getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        if (id != null) {
          data = data + "/" + Uri.encode(id);
        }
      }
      Uri dataUri = (data == null) ? null : Uri.parse(data);

      String query = getColumnString(c, SearchManager.SUGGEST_COLUMN_QUERY);
      String extraData = getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA);

      return createIntent(action, dataUri, extraData, query, actionKey, actionMsg);
    } catch (RuntimeException e ) {
      int rowNum;
      try {                       // be really paranoid now
        rowNum = c.getPosition();
      } catch (RuntimeException e2 ) {
        rowNum = -1;
      }
      Log.w(LOG_TAG, "Search Suggestions cursor at row " + rowNum +
          " returned exception" + e.toString());
      return null;
    }
  }

  private void forceSuggestionQuery() {
    try {
      Method before = SearchAutoComplete.class.getMethod("doBeforeTextChanged");
      Method after = SearchAutoComplete.class.getMethod("doAfterTextChanged");
      before.setAccessible(true);
      after.setAccessible(true);
      before.invoke(mQueryTextView);
      after.invoke(mQueryTextView);
    } catch (Exception e) {
      // Oh well...
    }
  }

  static boolean isLandscapeMode(Context context) {
    return context.getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_LANDSCAPE;
  }

  /**
   * Callback to watch the text field for empty/non-empty
   */
  private TextWatcher mTextWatcher = new TextWatcher() {

    public void beforeTextChanged(CharSequence s, int start, int before, int after) { }

    public void onTextChanged(CharSequence s, int start,
                              int before, int after) {
      SearchView.this.onTextChanged(s);
    }

    public void afterTextChanged(Editable s) {
    }
  };

  /**
   * Local subclass for AutoCompleteTextView.
   * @hide
   */
  public static class SearchAutoComplete extends AutoCompleteTextView {

    private int mThreshold;
    private SearchView mSearchView;

    public SearchAutoComplete(Context context) {
      super(context);
      mThreshold = getThreshold();
    }

    public SearchAutoComplete(Context context, AttributeSet attrs) {
      super(context, attrs);
      mThreshold = getThreshold();
    }

    public SearchAutoComplete(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      mThreshold = getThreshold();
    }

    void setSearchView(SearchView searchView) {
      mSearchView = searchView;
    }

    @Override
    public void setThreshold(int threshold) {
      super.setThreshold(threshold);
      mThreshold = threshold;
    }

    /**
     * Returns true if the text field is empty, or contains only whitespace.
     */
    private boolean isEmpty() {
      return TextUtils.getTrimmedLength(getText()) == 0;
    }

    /**
     * We override this method to avoid replacing the query box text when a
     * suggestion is clicked.
     */
    @Override
    protected void replaceText(CharSequence text) {
    }

    /**
     * We override this method to avoid an extra onItemClick being called on
     * the drop-down's OnItemClickListener by
     * {@link AutoCompleteTextView#onKeyUp(int, KeyEvent)} when an item is
     * clicked with the trackball.
     */
    @Override
    public void performCompletion() {
    }

    /**
     * We override this method to be sure and show the soft keyboard if
     * appropriate when the TextView has focus.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
      super.onWindowFocusChanged(hasWindowFocus);

      if (hasWindowFocus && mSearchView.hasFocus() && getVisibility() == VISIBLE) {
        InputMethodManager inputManager = (InputMethodManager) getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(this, 0);
        // If in landscape mode, then make sure that
        // the ime is in front of the dropdown.
        if (isLandscapeMode(getContext())) {
          ensureImeVisible(this, true);
        }
      }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
      super.onFocusChanged(focused, direction, previouslyFocusedRect);
      mSearchView.onTextFocusChanged();
    }

    /**
     * We override this method so that we can allow a threshold of zero,
     * which ACTV does not.
     */
    @Override
    public boolean enoughToFilter() {
      return mThreshold <= 0 || super.enoughToFilter();
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
        // special case for the back key, we do not even try to send it
        // to the drop down list but instead, consume it immediately
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
          KeyEvent.DispatcherState state = getKeyDispatcherState();
          if (state != null) {
            state.startTracking(event, this);
          }
          return true;
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
          KeyEvent.DispatcherState state = getKeyDispatcherState();
          if (state != null) {
            state.handleUpEvent(event);
          }
          if (event.isTracking() && !event.isCanceled()) {
            mSearchView.clearFocus();
            mSearchView.setImeVisibility(false);
            return true;
          }
        }
      }
      return super.onKeyPreIme(keyCode, event);
    }

  }

  private static void ensureImeVisible(AutoCompleteTextView view, boolean visible) {
    try {
      Method method = AutoCompleteTextView.class.getMethod("ensureImeVisible", boolean.class);
      method.setAccessible(true);
      method.invoke(view, visible);
    } catch (Exception e) {
      //Oh well...
    }
  }

  private static void showSoftInputUnchecked(View view, InputMethodManager imm, int flags) {
    try {
      Method method = imm.getClass().getMethod("showSoftInputUnchecked", int.class, ResultReceiver.class);
      method.setAccessible(true);
      method.invoke(imm, flags, null);
    } catch (Exception e) {
      //Fallback to public API which hopefully does mostly the same thing
      imm.showSoftInput(view, flags);
    }
  }

  private static void setText(AutoCompleteTextView view, CharSequence text, boolean filter) {
    try {
      Method method = AutoCompleteTextView.class.getMethod("setText", CharSequence.class, boolean.class);
      method.setAccessible(true);
      method.invoke(view, text, filter);
    } catch (Exception e) {
      //Fallback to public API which hopefully does mostly the same thing
      view.setText(text);
    }
  }
}
