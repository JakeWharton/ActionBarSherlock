package org.fest.assertions.api.com.actionbarsherlock.app;

import com.actionbarsherlock.app.ActionBar;
import org.fest.assertions.api.android.Utils;
import java.util.ArrayList;
import java.util.List;
import org.fest.assertions.api.AbstractAssert;

import static com.actionbarsherlock.app.ActionBar.DISPLAY_HOME_AS_UP;
import static com.actionbarsherlock.app.ActionBar.DISPLAY_SHOW_CUSTOM;
import static com.actionbarsherlock.app.ActionBar.DISPLAY_SHOW_HOME;
import static com.actionbarsherlock.app.ActionBar.DISPLAY_SHOW_TITLE;
import static com.actionbarsherlock.app.ActionBar.DISPLAY_USE_LOGO;
import static com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_LIST;
import static com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_STANDARD;
import static com.actionbarsherlock.app.ActionBar.NAVIGATION_MODE_TABS;
import static org.fest.assertions.api.Assertions.assertThat;

/** Assertions for {@link ActionBar} instances. */
public class ActionBarAssert extends AbstractAssert<ActionBarAssert, ActionBar> {
  public ActionBarAssert(ActionBar actual) {
    super(actual, ActionBarAssert.class);
  }

  public ActionBarAssert hasDisplayOptions(int options) {
    isNotNull();
    final int actualOptions = actual.getDisplayOptions();
    assertThat(actualOptions) //
        .overridingErrorMessage("Expected display options <%s> but was <%s>.",
            displayOptionsToString(options), displayOptionsToString(actualOptions)) //
        .isEqualTo(options);
    return this;
  }

  public ActionBarAssert hasHeight(int height) {
    isNotNull();
    int actualHeight = actual.getHeight();
    assertThat(actualHeight) //
        .overridingErrorMessage("Expected height <%s> but was <%s>.", height, actualHeight) //
        .isEqualTo(height);
    return this;
  }

  public ActionBarAssert hasNavigationItemCount(int count) {
    isNotNull();
    int actualCount = actual.getNavigationItemCount();
    assertThat(actualCount) //
        .overridingErrorMessage("Expected count <%s> but was <%s>.", count, actualCount) //
        .isEqualTo(count);
    return this;
  }

  public ActionBarAssert hasNavigationMode(int mode) {
    isNotNull();
    int actualMode = actual.getNavigationMode();
    assertThat(actualMode) //
        .overridingErrorMessage("Expected mode <%s> but was <%s>.", navigationModeToString(mode),
            navigationModeToString(actualMode)) //
        .isEqualTo(mode);
    return this;
  }

  public ActionBarAssert hasSelectedNavigationIndex(int index) {
    isNotNull();
    int actualIndex = actual.getSelectedNavigationIndex();
    assertThat(actualIndex) //
        .overridingErrorMessage("Expected selected index <%s> but was <%s>.", index, actualIndex) //
        .isEqualTo(index);
    return this;
  }

  public ActionBarAssert hasSubtitle(CharSequence subtitle) {
    isNotNull();
    CharSequence actualSubtitle = actual.getSubtitle();
    assertThat(actualSubtitle) //
        .overridingErrorMessage("Expected subtitle <%s> but was <%s>.", subtitle, actualSubtitle) //
        .isEqualTo(subtitle);
    return this;
  }

  public ActionBarAssert hasSubtitle(int resId) {
    return hasSubtitle(actual.getThemedContext().getString(resId));
  }

  public ActionBarAssert hasTabCount(int count) {
    isNotNull();
    int actualCount = actual.getTabCount();
    assertThat(actualCount) //
        .overridingErrorMessage("Expected tab count of <%s> but was <%s>.", count, actualCount) //
        .isEqualTo(count);
    return this;
  }

  public ActionBarAssert hasTitle(CharSequence title) {
    isNotNull();
    CharSequence actualTitle = actual.getTitle();
    assertThat(actualTitle) //
        .overridingErrorMessage("Expected title <%s> but was <%s>.", title, actualTitle) //
        .isEqualTo(title);
    return this;
  }

  public ActionBarAssert hasTitle(int resId) {
    return hasTitle(actual.getThemedContext().getString(resId));
  }

  public ActionBarAssert isShowing() {
    isNotNull();
    assertThat(actual.isShowing()) //
        .overridingErrorMessage("Expected to be showing but was not showing.") //
        .isTrue();
    return this;
  }

  public ActionBarAssert isNotShowing() {
    isNotNull();
    assertThat(actual.isShowing()) //
        .overridingErrorMessage("Expected to be not showing but was showing.") //
        .isFalse();
    return this;
  }

  private static String navigationModeToString(int mode) {
    switch (mode) {
      case NAVIGATION_MODE_LIST:
        return "list";
      case NAVIGATION_MODE_STANDARD:
        return "standard";
      case NAVIGATION_MODE_TABS:
        return "tabs";
      default:
        throw new IllegalArgumentException("Unknown navigation mode: " + mode);
    }
  }

  private static String displayOptionsToString(int options) {
    if (options == 0) {
      return "none";
    }
    List<String> parts = new ArrayList<String>();
    if ((options & DISPLAY_HOME_AS_UP) != 0) {
      parts.add("homeAsUp");
    }
    if ((options & DISPLAY_SHOW_CUSTOM) != 0) {
      parts.add("showCustom");
    }
    if ((options & DISPLAY_SHOW_HOME) != 0) {
      parts.add("showHome");
    }
    if ((options & DISPLAY_SHOW_TITLE) != 0) {
      parts.add("showTitle");
    }
    if ((options & DISPLAY_USE_LOGO) != 0) {
      parts.add("useLogo");
    }
    return Utils.join(parts);
  }
}
