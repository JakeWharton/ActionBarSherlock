package org.fest.assertions.api;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.fest.assertions.api.com.actionbarsherlock.app.ActionBarAssert;
import org.fest.assertions.api.com.actionbarsherlock.view.ActionModeAssert;
import org.fest.assertions.api.com.actionbarsherlock.view.MenuAssert;
import org.fest.assertions.api.com.actionbarsherlock.view.MenuItemAssert;

/** Assertions for testing ActionBarSherlock classes. */
public class ACTIONBARSHERLOCK {
  public static ActionBarAssert assertThat(ActionBar actual) {
    return new ActionBarAssert(actual);
  }
  public static ActionModeAssert assertThat(ActionMode actual) {
    return new ActionModeAssert(actual);
  }
  public static MenuAssert assertThat(Menu actual) {
    return new MenuAssert(actual);
  }
  public static MenuItemAssert assertThat(MenuItem actual) {
    return new MenuItemAssert(actual);
  }
}
