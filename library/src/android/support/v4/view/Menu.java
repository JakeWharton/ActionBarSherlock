package android.support.v4.view;

public interface Menu extends android.view.Menu {
	@Override
	MenuItem add(CharSequence title);

	@Override
	MenuItem add(int groupId, int itemId, int order, int titleRes);
	
	@Override
	MenuItem add(int titleRes);

	@Override
	MenuItem add(int groupId, int itemId, int order, CharSequence title);

	@Override
	public MenuItem findItem(int id);

	@Override
	public MenuItem getItem(int index);
}
