package org.fest.assertions.api.com.actionbarsherlock.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import org.fest.assertions.api.AbstractAssert;

import static org.fest.assertions.api.Assertions.assertThat;

/** Assertions for {@link MenuItem} instances. */
public class MenuItemAssert extends AbstractAssert<MenuItemAssert, MenuItem> {
  public MenuItemAssert(MenuItem actual) {
    super(actual, MenuItemAssert.class);
  }

  public MenuItemAssert hasActionView(View view) {
    isNotNull();
    View actualView = actual.getActionView();
    assertThat(actualView) //
        .overridingErrorMessage("Expected action view <%s> but was <%s>.", view, actualView) //
        .isSameAs(view);
    return this;
  }

  public MenuItemAssert hasAlphabeticShortcut(char shortcut) {
    isNotNull();
    char actualShortcut = actual.getAlphabeticShortcut();
    assertThat(actualShortcut) //
        .overridingErrorMessage("Expected alphabetic shortcut <%s> but was <%s>.", shortcut,
            actualShortcut) //
        .isEqualTo(shortcut);
    return this;
  }

  public MenuItemAssert hasGroupId(int id) {
    isNotNull();
    int actualId = actual.getGroupId();
    assertThat(actualId) //
        .overridingErrorMessage("Expected group ID <%s> but was <%s>.", id, actualId) //
        .isEqualTo(id);
    return this;
  }

  public MenuItemAssert hasIcon(Drawable icon) {
    isNotNull();
    Drawable actualIcon = actual.getIcon();
    assertThat(actualIcon) //
        .overridingErrorMessage("Expected icon <%s> but was <%s>.", icon, actualIcon) //
        .isSameAs(icon);
    return this;
  }

  public MenuItemAssert hasIntent(Intent intent) {
    isNotNull();
    Intent actualIntent = actual.getIntent();
    assertThat(actualIntent) //
        .overridingErrorMessage("Expected intent <%s> but was <%s>.", intent, actualIntent) //
        .isEqualTo(intent);
    return this;
  }

  public MenuItemAssert hasItemId(int id) {
    isNotNull();
    int actualId = actual.getItemId();
    assertThat(actualId) //
        .overridingErrorMessage("Expected item ID <%s> but was <%s>.", id, actualId) //
        .isEqualTo(id);
    return this;
  }

  public MenuItemAssert hasNumericShortcut(char shortcut) {
    isNotNull();
    char actualShortcut = actual.getNumericShortcut();
    assertThat(actualShortcut) //
        .overridingErrorMessage("Expected numeric shortcut <%s> but was <%s>.", shortcut,
            actualShortcut) //
        .isEqualTo(shortcut);
    return this;
  }

  public MenuItemAssert hasOrder(int order) {
    isNotNull();
    int actualOrder = actual.getOrder();
    assertThat(actualOrder) //
        .overridingErrorMessage("Expected order <%s> but was <%s>.", order, actualOrder) //
        .isEqualTo(order);
    return this;
  }

  public MenuItemAssert hasTitle(CharSequence title) {
    isNotNull();
    CharSequence actualTitle = actual.getTitle();
    assertThat(actualTitle) //
        .overridingErrorMessage("Expected title <%s> but was <%s>.", title, actualTitle) //
        .isEqualTo(title);
    return this;
  }

  public MenuItemAssert hasCondensedTitle(CharSequence title) {
    isNotNull();
    CharSequence actualTitle = actual.getTitleCondensed();
    assertThat(actualTitle) //
        .overridingErrorMessage("Expected condensed title <%s> but was <%s>.", title,
            actualTitle) //
        .isEqualTo(title);
    return this;
  }

  public MenuItemAssert hasSubMenu() {
    isNotNull();
    assertThat(actual.hasSubMenu()) //
        .overridingErrorMessage("Expected to have sub-menu but sub-menu was not present.") //
        .isTrue();
    return this;
  }

  public MenuItemAssert hasNoSubMenu() {
    isNotNull();
    assertThat(actual.hasSubMenu()) //
        .overridingErrorMessage("Expected to not have a sub-menu but sub-menu was present.") //
        .isFalse();
    return this;
  }

  public MenuItemAssert isActionViewExpanded() {
    isNotNull();
    assertThat(actual.isActionViewExpanded()) //
        .overridingErrorMessage("Expected expanded action view but action view was collapsed.") //
        .isTrue();
    return this;
  }

  public MenuItemAssert isActionViewCollapsed() {
    isNotNull();
    assertThat(actual.isActionViewExpanded()) //
        .overridingErrorMessage("Expected collapsed action view but action view was expanded.") //
        .isFalse();
    return this;
  }

  public MenuItemAssert isCheckable() {
    isNotNull();
    assertThat(actual.isCheckable()) //
        .overridingErrorMessage("Expected to be checkable but was not checkable.") //
        .isTrue();
    return this;
  }

  public MenuItemAssert isNotCheckable() {
    isNotNull();
    assertThat(actual.isCheckable()) //
        .overridingErrorMessage("Expected to not be checkable but was checkable.") //
        .isFalse();
    return this;
  }

  public MenuItemAssert isChecked() {
    isNotNull();
    assertThat(actual.isChecked()) //
        .overridingErrorMessage("Expected to be checked but was not checked.") //
        .isTrue();
    return this;
  }

  public MenuItemAssert isNotChecked() {
    isNotNull();
    assertThat(actual.isChecked()) //
        .overridingErrorMessage("Expected to not be checked but was checked.") //
        .isFalse();
    return this;
  }

  public MenuItemAssert isEnabled() {
    isNotNull();
    assertThat(actual.isEnabled()) //
        .overridingErrorMessage("Expected to be enabled but was disabled.") //
        .isTrue();
    return this;
  }

  public MenuItemAssert isDisabled() {
    isNotNull();
    assertThat(actual.isEnabled()) //
        .overridingErrorMessage("Expected to be disabled but was enabled.") //
        .isFalse();
    return this;
  }

  public MenuItemAssert isVisible() {
    isNotNull();
    assertThat(actual.isVisible()) //
        .overridingErrorMessage("Expected to be visible but was not visible.") //
        .isTrue();
    return this;
  }

  public MenuItemAssert isNotVisible() {
    isNotNull();
    assertThat(actual.isVisible()) //
        .overridingErrorMessage("Expected to not be visible but was visible.") //
        .isFalse();
    return this;
  }
}
