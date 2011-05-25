package android.support.v4.view;

import android.content.Context;

public class MenuInflaterHoneycombWrapper extends android.view.MenuInflater {
	private final android.view.MenuInflater mMenuInflater;
	
	public MenuInflaterHoneycombWrapper(Context context, android.view.MenuInflater menuInflater) {
		super(context);
		mMenuInflater = menuInflater;
	}

	@Override
	public void inflate(int menuRes, android.view.Menu menu) {
		mMenuInflater.inflate(menuRes, ((MenuHoneycombWrapper)menu).unwrap());
	}
}
