/**
 * @author Mantas Miksys
 * 
 * The AnimatedActionItem contains 
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
	MenuItem refreshItem;
	int groupId = 0;
	int order = 0;
	int itemId = 12345;
	RadioGroup rg;

	/**
	 * 
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == itemId) {
			Toast.makeText(this, "You selected refresh button!",
					Toast.LENGTH_SHORT).show();
			animate();
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

		refreshItem = menu.findItem(itemId);

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(SampleList.THEME); // Used for theme switching in samples
		super.onCreate(savedInstanceState);
		setContentView(R.layout.animated_action_view);

		TextView explTV = (TextView) findViewById(R.id.textView1);
		explTV.setText(R.string.animated_action_item_content);

		rg = (RadioGroup) findViewById(R.id.radioGroup1);

	}

	/**
	 * Starts the animation from the action bar item.
	 * 
	 * This method attaches a rotating imageview instead of current ActionBar
	 * item.
	 * 
	 */
	public void animate() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(
				R.layout.animated_imageview, null);
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		if (isLight)
			iv.setImageResource(R.drawable.ic_refresh_inverse);

		int id = rg.getCheckedRadioButtonId();

		int animationId = 0;

		switch (id) {
		case R.id.radio0:
			animationId = R.anim.animation_scale;
			break;
		case R.id.radio1:
			animationId = R.anim.animation_translate;
			break;
		case R.id.radio2:
			animationId = R.anim.animation_alpha;
			break;
		}

		Animation animation = AnimationUtils.loadAnimation(this, animationId);

		animation.setRepeatCount(3);
		iv.startAnimation(animation);
		refreshItem.setActionView(iv);
		bacgroundProcess();

	}

	private void bacgroundProcess() {

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
		    completeAnimation();
		  }
		}, 3000);

	}

	/**
	 * Removes the animation from the action bar item.
	 * 
	 */
	public void completeAnimation() {
		refreshItem.getActionView().clearAnimation();
		refreshItem.setActionView(null);
	}

}
