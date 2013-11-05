/**
 * @author Mantas Miksys
 * 
 * The AnimatedActionItem contains 4 examples of how ActionBar action items 
 * could be animated. These examples include rotate, alpha, translation and scale animations
 * 
 */
package com.actionbarsherlock.sample.demos;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AnimatedActionItem extends SherlockActivity {
	MenuItem mMenuItem;
	int groupId = 0;
	int order = 0;
	int itemId = 12345;
	RadioGroup mRadioGroup;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == itemId) {
			animateActionItem();
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		menu.add(groupId, itemId, order, "Refresh")
				.setIcon(
						isLight ? R.drawable.ic_refresh_inverse
								: R.drawable.ic_refresh)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		mMenuItem = menu.findItem(itemId);

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SampleList.THEME); // Used for theme switching in samples
		super.onCreate(savedInstanceState);
		setContentView(R.layout.animated_action_view);

		TextView explTV = (TextView) findViewById(R.id.textView1);
		explTV.setText(R.string.animated_action_item_content);

		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

	}

	/**
	 * Starts the animation for the ActionBar item. The animation is done by
	 * inflating an ImageView and setting animation to it. Which of animation is
	 * chosen depends on which RadioButton of mRadioGroup is checked.
	 * 
	 */
	private void animateActionItem() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView mImageView = (ImageView) inflater.inflate(
				R.layout.animated_imageview, null);
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		if (isLight)
			mImageView.setImageResource(R.drawable.ic_refresh_inverse);

		int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();

		int animationId = -1;

		switch (checkedRadioButtonId) {
		case R.id.radio0:
			animationId = R.anim.animation_rotate;
			break;
		case R.id.radio1:
			animationId = R.anim.animation_translate;
			break;
		case R.id.radio2:
			animationId = R.anim.animation_alpha;
			break;
		case R.id.radio3:
			animationId = R.anim.animation_scale;
			break;
		}

		if (animationId > 0) {
			Animation animation = AnimationUtils.loadAnimation(this,
					animationId);

			animation.setRepeatCount(Animation.INFINITE);
			mImageView.startAnimation(animation);
			mMenuItem.setActionView(mImageView);
			backgroundProcess();
		}

	}

	/**
	 * Example method to illustrate that some work could possible be done during
	 * the ActionItem animation. This method waits for 5 seconds and then ends
	 * animation.
	 * 
	 */
	private void backgroundProcess() {

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				completeAnimation();
			}
		}, 5000);

	}

	/**
	 * This method removes the animation and the ImageView from the animated
	 * ActionBar item. This makes ActionBar item clickable again.
	 * 
	 */
	private void completeAnimation() {
		mMenuItem.getActionView().clearAnimation();
		mMenuItem.setActionView(null);
	}

}
