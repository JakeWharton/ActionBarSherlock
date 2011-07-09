package com.actionbarsherlock.internal.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.view.menu.MenuView;

public class ActionMenuItemView extends LinearLayout implements MenuView.ItemView, View.OnClickListener {
	private ImageButton mImageButton;
	private MenuItemImpl mItemData;
	private MenuBuilder.ItemInvoker mItemInvoker;
	private Button mTextButton;
	private CharSequence mTitle;

	public ActionMenuItemView(Context context) {
		this(context, null);
	}
	public ActionMenuItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public ActionMenuItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public MenuItemImpl getItemData() {
		return mItemData;
	}
	
	public boolean hasText() {
		return mTextButton.getVisibility() != View.GONE;
	}

	@Override
	public void initialize(MenuItemImpl itemData, int menuType) {
		mItemData = itemData;
		setIcon(itemData.getIcon());
		setTitle(itemData.getTitleForItemView(this));
		setId(itemData.getItemId());
		if (itemData.isVisible()) {
			setVisibility(View.VISIBLE);
			setEnabled(itemData.isEnabled());
		}
	}

	@Override
	public void onClick(View v) {
		if (mItemInvoker != null) {
			mItemInvoker.invokeItem(mItemData);
		}
	}
	
	@Override
	public void onFinishInflate() {
		mImageButton = (ImageButton)findViewById(R.id.imageButton);
		mImageButton.setOnClickListener(this);
		mTextButton = (Button)findViewById(R.id.textButton);
		mTextButton.setOnClickListener(this);
	}

	@Override
	public boolean prefersCondensedTitle() {
		return true;
	}

	@Override
	public void setCheckable(boolean checkable) {
		//No op
	}

	@Override
	public void setChecked(boolean checked) {
		//No op
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mImageButton.setEnabled(enabled);
		mTextButton.setEnabled(enabled);
	}

	@Override
	public void setIcon(Drawable icon) {
		mImageButton.setImageDrawable(icon);
		if (icon != null) {
			mImageButton.setVisibility(View.VISIBLE);
			if (!mItemData.showsTextAsAction()) {
				mTextButton.setVisibility(View.GONE);
			}
		} else {
			mImageButton.setVisibility(View.GONE);
		}
	}
	
	public void setItemInvoker(MenuBuilder.ItemInvoker itemInvoker) {
		mItemInvoker = itemInvoker;
	}

	@Override
	public void setShortcut(boolean showShortcut, char shortcutKey) {
		//No op
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		setContentDescription(title);
		if ((mImageButton.getDrawable() != null) || mItemData.showsTextAsAction()) {
			mTextButton.setText(mTitle);
			mTextButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean showsIcon() {
		return true;
	}
}
