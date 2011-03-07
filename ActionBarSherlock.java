/*
 * Copyright 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jakewharton.android.common.ui;

import com.jakewharton.android.virtualclient.R;
import greendroid.widget.GDActionBar;
import greendroid.widget.GDActionBar.OnActionBarListener;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * <p>Helper for implementing the action bar design pattern across all versions
 * of Android.</p>
 * 
 * <p>This class will automatically use the native ActionBar implementation on
 * Android 3.0 or later. For previous versions which do not include ActionBar,
 * a GreenDroid GDActionBar will automatically be wrapped around the layout.</p>
 * 
 * <p>All interaction with these action bars is handled through two static
 * classes which should be implemented in each Activity as inner-classes. The
 * two classes should extend from HoneycombActionBarHandler and
 * PreHoneycombActionBarHandler. Each will allow for overriding various methods
 * to handle the creation of and interaction with each type of action bar.</p>
 * 
 * <p>Example:
 * <code><pre>public class HelloActionBarActivity extends Activity {
 *     &#064;Override
 *     public void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         ActionBarSherlock.newInstance()
 *                 .setActivity(this, savedInstanceState)
 *                 .setLayout(R.layout.activity_hello)
 *                 .setTitle("Hello, ActionBar!")
 *                 .setHoneycombHandler(HelloHoneycombActionBarHandler.class)
 *                 .setPreHoneycombHandler(HelloPreHoneycombActionBarHandler.class);
 *     }
 *     
 *     public static final class HelloHoneycombActionBarHandler
 *             extends ActionBarSherlock.HoneycombActionBarHandler {
 *         &#064;Override
 *         public void onCreate(Bundle savedInstanceState) {
 *             Toast.makeText(
 *                 this.getActivity(),
 *                 "Hello, Honeycomb ActionBar!",
 *                 Toast.LENGTH_SHORT
 *             ).show();
 *         }
 *     }
 *     public static final class HelloPreHoneycombActionBarHandler
 *             extends ActionBarSherlock.PreHoneycombActionBarHandler {
 *         &#064;Override
 *         public void onCreate(Bundle savedInstanceState) {
 *             Toast.makeText(
 *                 this.getActivity(),
 *                 "Hello, Pre-Honeycomb ActionBar!",
 *                 Toast.LENGTH_SHORT
 *             ).show();
 *         }
 *     }
 * }</pre></code></p>
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 * @version 1.0.0
 */
public abstract class ActionBarSherlock {
	/**
	 * Parent Activity instance.
	 */
	protected Activity mActivity;
	/**
	 * Parent Activity's persisted instance state.
	 */
	protected Bundle mSavedInstanceState;
	
	/**
	 * Create a new instance of the handler. This will determine the appropriate
	 * implementation based off of your Android version.
	 * @return Implementing instance of ActionBarSherlock.
	 */
	public static ActionBarSherlock newInstance() {
		//Build.VERSION.SDK allows for all versions where SDK_INT is 1.6+ only.
		if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.HONEYCOMB) {
			return new PreHoneycomb();
		} else {
			return new Honeycomb();
		}
	}
	
	/**
	 * Inflate a resource as the content layout.
	 * @param layoutResourceId Layout resource identifier.
	 * @return Current instance
	 */
	public abstract ActionBarSherlock setLayout(int layoutResourceId);
	/**
	 * Set the content layout to an already instantiated View.
	 * @param view View.
	 * @return Current instance.
	 */
	public abstract ActionBarSherlock setLayout(View view);
	/**
	 * Convenience method to set the ActionBar title for both implementations.
	 * @param title String title
	 * @return Current instance.
	 */
	public abstract ActionBarSherlock setTitle(String title);
	/**
	 * Set the Activity to which the action bar should be bound. This should
	 * only be called once and should be done first.
	 * 
	 * @param activity Parent Activity.
	 * @return Current instance.
	 */
	public ActionBarSherlock setActivity(Activity activity) {
		assert this.mActivity == null : "Activity can only be set called once.";
		this.mActivity = activity;
		return this;
	}
	/**
	 * Set the Activity to which the action bar should be bound. This should
	 * only be called once and should be done first.
	 * 
	 * @param activity Parent Activity.
	 * @param savedInstanceState Persisted instance state.
	 * @return Current instance.
	 */
	public ActionBarSherlock setActivity(Activity activity, Bundle savedInstanceState) {
		this.setActivity(activity);
		this.mSavedInstanceState = savedInstanceState;
		return this;
	}
	/**
	 * Set the class which will handle callbacks for the Honeycomb ActionBar.
	 * @param handler Honeycomb handler class.
	 * @return Current instance.
	 */
	public ActionBarSherlock setHoneycombHandler(Class<? extends HoneycombActionBarHandler> handler) {
		return this;
	}
	/**
	 * Set the class which will handle callbacks for the pre-Honeycomb ActionBar.
	 * @param handler Pre-Honeycomb handler class.
	 * @return Current instance.
	 */
	public ActionBarSherlock setPreHoneycombHandler(Class<? extends PreHoneycombActionBarHandler> handler) {
		return this;
	}
	
	
	
	/**
	 * Implementation of the GreenDroid GDActionBar for pre-honeycomb devices.
	 * This will automatically wrap your layout to include this action bar
	 * on the top of the screen.
	 * 
	 * @author Jake Wharton <jakewharton@gmail.com>
	 */
	private static final class PreHoneycomb extends ActionBarSherlock {
		/**
		 * Instance of parent Activity's pre-honeycomb handler.
		 */
		private PreHoneycombActionBarHandler mHandler;
		/**
		 * GDActionBar instance.
		 */
	    private GDActionBar mActionBar;
	    /**
	     * Content FrameLayout instance.
	     */
	    private FrameLayout mContent;
	    
		@Override
		public ActionBarSherlock setActivity(Activity activity) {
			super.setActivity(activity);
			
			//Theme_GreenDroid_NoTitleBar does not work here
			this.mActivity.setTheme(R.style.Theme_GreenDroid);
			this.mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.mActivity.setContentView(R.layout.gd_content_normal);
			
			this.mActionBar = (GDActionBar)this.mActivity.findViewById(R.id.gd_action_bar);
			assert this.mActionBar != null : "Action bar view not found.";
			this.mContent = (FrameLayout)this.mActivity.findViewById(R.id.gd_action_bar_content_view);
			assert this.mContent != null : "Content view not found.";
			
			return this;
		}

		@Override
		public ActionBarSherlock setPreHoneycombHandler(Class<? extends PreHoneycombActionBarHandler> handler) {
			//Setting activity will also get action bar instance.
			assert this.mActivity != null : "Activity must first be set.";
			
			try {
				this.mHandler = handler.newInstance();
				this.mHandler.setActivity(this.mActivity);
				this.mHandler.setActionBar(this.mActionBar);
				this.mHandler.onCreate(this.mSavedInstanceState);
				return this;
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not instantiate handler.", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not instantiate handler.", e);
			}
		}

		@Override
		public ActionBarSherlock setLayout(int layoutResourceId) {
			//Setting activity will also get content instance.
			assert this.mActivity != null : "Activity must first be set.";
			
			LayoutInflater.from(this.mActivity).inflate(layoutResourceId, this.mContent);
			return this;
		}
		
		@Override
		public ActionBarSherlock setLayout(View view) {
			//Setting activity gets content instance.
			assert this.mActivity != null : "Activity must first be set.";
			
			this.mContent.removeAllViews();
			this.mContent.addView(view);
			return this;
		}

		@Override
		public ActionBarSherlock setTitle(String title) {
			//Setting activity gets action bar instance.
			assert this.mActivity != null : "Activity must first be set.";
			
			this.mActionBar.setTitle(title);
			return this;
		}
	}
	
	/**
	 * Implementation of the post-honeycomb native ActionBar. This class has
	 * no logic other than marshaling calls down to their native counterparts.
	 * 
	 * @author Jake Wharton <jakewharton@gmail.com>
	 */
	private static final class Honeycomb extends ActionBarSherlock {
		/**
		 * Instance of parent Activity's honeycomb handler.
		 */
		private HoneycombActionBarHandler mHandler;
		
		@Override
		public ActionBarSherlock setHoneycombHandler(Class<? extends HoneycombActionBarHandler> handler) {
			assert this.mActivity != null : "Activity must be first set.";
			assert this.mActivity.getActionBar() != null : "Layout must be first set.";
			
			try {
				this.mHandler = handler.newInstance();
				this.mHandler.setActivity(this.mActivity);
				this.mHandler.setActionBar(this.mActivity.getActionBar());
				this.mHandler.onCreate(this.mSavedInstanceState);
				return this;
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not instantiate handler.", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not instantiate handler.", e);
			}
		}

		@Override
		public ActionBarSherlock setLayout(int layoutResourceId) {
			assert this.mActivity != null;
			
			this.mActivity.setContentView(layoutResourceId);
			assert this.mActivity.getActionBar() != null :
				"ActionBar not created. Check targetSdkVersion >= 11.";
			
			return this;
		}
		
		@Override
		public ActionBarSherlock setLayout(View view) {
			assert this.mActivity != null;
			
			this.mActivity.setContentView(view);
			assert this.mActivity.getActionBar() != null :
				"ActionBar not created. Check targetSdkVersion >= 11.";
			
			return this;
		}
		
		@Override
		public ActionBarSherlock setTitle(String title) {
			assert this.mActivity != null : "Activity must be first set.";
			assert this.mActivity.getActionBar() != null : "Layout must be first set.";
			
			this.mActivity.getActionBar().setTitle(title);
			
			return this;
		}
	}
	
	
	
	/**
	 * Base class for an action bar handler. Contains all properties and methods
	 * which apply to both action bar types. Since we can't access the action
	 * bar itself at this level, everything will be related to the activity.
	 * 
	 * @author Jake Wharton <jakewharton@gmail.com>
	 */
	private static abstract class ActionBarHandler {
		/**
		 * Parent Activity instance.
		 */
		/*package*/ Activity mActivity;
		
		/**
		 * <p>Set the parent activity in which the action bar is contained.</p>
		 * 
		 * <p><em>Do not call this method in your implementing class</em>.</p>
		 * 
		 * @param activity Parent Activity.
		 */
		public void setActivity(Activity activity) {
			assert this.mActivity == null : "Activity may only be set once.";
			this.mActivity = activity;
		}
		
		/**
		 * Gets the parent Activity instance.
		 * 
		 * @return Activity instance.
		 */
		@SuppressWarnings("unused")
		public Activity getActivity() {
			return this.mActivity;
		}
		
		/**
		 * Callback for when the action bar is created. This should be used to
		 * layout the buttons, title, and state of the action bar.
		 */
		public abstract void onCreate(Bundle savedInstanceState);
	}
	
	/**
	 * <p>Base class for the pre-honeycomb action bar implementation handler.</p>
	 * 
	 * <p>This class should be extended as a static inner-class of your Activity
	 * and then passed to the ActionBarSherlock instance via the 
	 * setPreHoneycombHandler method.</p>
	 */
	public static abstract class PreHoneycombActionBarHandler extends ActionBarHandler {
		/**
		 * Parent Activity's GDActionBar instance.
		 */
		private GDActionBar mActionBar;
		
		/**
		 * <p>Set the GDActionBar instance so we can access it directly.</p>
		 * 
		 * <p><em>Do not call this method in your implementing class</em>.</p>
		 * 
		 * @param actionBar GDActionBar instance.
		 */
		public void setActionBar(GDActionBar actionBar) {
			assert this.mActionBar == null : "Action bar may only be set once.";
			
			this.mActionBar = actionBar;
			this.mActionBar.setOnActionBarListener(new OnActionBarListener() {
				@Override
				public void onActionBarItemClicked(int position) {
					if (position == OnActionBarListener.HOME_ITEM) {
						onHomeClicked();
					} else {
						onItemClicked(mActionBar.getItem(position).getItemId());
					}
				}
			});
		}
		
		/**
		 * Get the activity's action bar.
		 * 
		 * @return GDActionBar instance.
		 */
		public GDActionBar getActionBar() {
			return this.mActionBar;
		}
		
		/**
		 * Set whether or not to display the GDActionBar's home button.
		 * 
		 * @param visible Boolean indicating visibility.
		 */
		public void setIsHomeButtonVisible(boolean visible) {
			assert this.mActionBar != null : "Action bar must be first set.";
			
			//Sort of a hack. We know this will always be the home button and
			//its separator since we use the gd_content_normal layout above.
			this.mActionBar.getChildAt(0).setVisibility(visible ? View.VISIBLE : View.GONE);
			this.mActionBar.getChildAt(1).setVisibility(visible ? View.VISIBLE : View.GONE);
		}
		
		/**
		 * Callback for when the action bar's home button has been pressed.
		 */
		public void onHomeClicked() {
			Toast.makeText(this.mActivity, "Unhandled Event: Home clicked.", Toast.LENGTH_SHORT).show();
		}
		
		/**
		 * Callback for when an item on the action bar has been pressed.
		 * 
		 * @param itemId Id of item clicked.
		 */
		public void onItemClicked(int itemId) {
			Toast.makeText(this.mActivity, "Unhandled Event: Item id " + itemId + " clicked.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * <p>Base class for the honeycomb action bar implementation handler.</p>
	 * 
	 * <p>This class should be extended as a static inner-class of your Activity
	 * and then passed to the ActionBarSherlock instance via the 
	 * setHoneycombHandler method.</p>
	 */
	public static abstract class HoneycombActionBarHandler extends ActionBarHandler {
		/**
		 * Parent Activity's ActionBar instance.
		 */
		private ActionBar mActionBar;
		
		/**
		 * <p>Set the ActionBar instance so we can access it directly.</p>
		 * 
		 * <p><em>Do not call this method in your implementing class</em>.</p>
		 * 
		 * @param actionBar ActionBar instance.
		 */
		public void setActionBar(ActionBar actionBar) {
			assert this.mActionBar == null : "Action bar may only be set once.";
			this.mActionBar = actionBar;
		}
		
		/**
		 * Get the activity's action bar.
		 * 
		 * @return ActionBar instance.
		 */
		public ActionBar getActionBar() {
			return this.mActionBar;
		}
	}
}
