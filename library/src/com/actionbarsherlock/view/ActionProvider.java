package com.actionbarsherlock.view;

import android.content.Context;
import android.view.View;

public abstract class ActionProvider {
	/**
	 * Creates a new instance.
	 * 
	 * @param context Context for accessing resources.
	 */
	public ActionProvider(Context context) {
		
	}
	
	public boolean hasSubMenu() {
		return false;
	}
	
	public abstract View createActionView();
	
	public boolean onPerformDefaultAction() {
		return false;
	}
	
	public void onPrepareSubMenu(SubMenu subMenu) {
		//
	}
}
