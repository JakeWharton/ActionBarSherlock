package com.jakewharton.android.actionbarsherlock;

import android.graphics.drawable.Drawable;

/**
 * Wrapper for {@link android.app.ActionBar.Tab} which allows us to provide a
 * common API to both the native and the third-party action bars.
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 */
public class ActionBarTab {
	/** Tab icon. */
	private Drawable mIcon;
	
	/** Tab text. */
	private CharSequence mText;
	
	/** Tab object. */
	private Object mTag;
	
	
	/**
	 * Return the icon associated with this tab.
	 * 
	 * @return The tab's icon.
	 */
	public Drawable getIcon() {
		return this.mIcon;
	}
	
	/**
	 * Set the icon displayed on this tab.
	 * 
	 * @param icon The drawable to use as an icon.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarTab setIcon(Drawable icon) {
		this.mIcon = icon;
		return this;
	}
	
	/**
	 * Return the text of this tab.
	 * 
	 * @return The tab's text.
	 */
	public CharSequence getText() {
		return this.mText;
	}
	
	/**
	 * Set the text displayed on this tab. Text may be truncated if there is
	 * not room to display the entire string.
	 * 
	 * @param text A resource ID referring to the text that should be
	 * displayed.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarTab setText(CharSequence text) {
		this.mText = text;
		return this;
	}
	
	/**
	 * Return the tag associated with this tab.
	 * 
	 * @return This Tab's tag object.
	 */
	public Object getTag() {
		return this.mTag;
	}
	
	/**
	 * Give this Tab an arbitrary object to hold for later use.
	 * 
	 * @param tag Object to store.
	 * @return Current instance for builder pattern.
	 */
	public ActionBarTab setTag(Object tag) {
		this.mTag = tag;
		return this;
	}
}
