package com.slidingmenu.lib.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.slidingmenu.lib.R;
import com.slidingmenu.lib.SlidingMenu;

public class SlidingFragmentActivity extends SherlockFragmentActivity implements SlidingActivityBase {

	private SlidingMenu mSlidingMenu;
	private ViewGroup mContentView;
	private boolean mContentViewCalled = true;
	private boolean mBehindContentViewCalled = true;
	private SlidingMenuList mMenuList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// unregister the current content view
		getWindow().getDecorView().findViewById(android.R.id.content).setId(View.NO_ID);
		// register a new content view
		mContentView = new RelativeLayout(this);
		mContentView.setId(android.R.id.content);
		// set up the SlidingMenu
		mSlidingMenu = (SlidingMenu) getLayoutInflater().inflate(R.layout.slidingmenumain, null);
		mSlidingMenu.setViewAbove(mContentView);
		super.setContentView(mSlidingMenu);
	}

	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (!mContentViewCalled || !mBehindContentViewCalled) {
			throw new IllegalStateException("Both setContentView and " +
					"setBehindContentView must be called in onCreate.");
		}
	}

//	public Window getWindow() {
//		if (mWindowHelper == null) {
//			return super.getWindow();
//		} else {
//			return mWindowHelper;
//		}
//	}
//
//	private Window getWindowInternal() {
//		return super.getWindow();
//	}

	@Override
	public void setContentView(int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	public void setContentView(View v) {
		setContentView(v, null);
	}

	public void setContentView(View v, LayoutParams params) {
		if (!mContentViewCalled) {
			mContentViewCalled = true;
		}
		mContentView.addView(v);
	}

	public void setBehindContentView(int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	public void setBehindContentView(View v) {
		setBehindContentView(v, null);
	}

	public void setBehindContentView(View v, LayoutParams params) {
		if (!mBehindContentViewCalled) {
			mBehindContentViewCalled = true;
		}
		mSlidingMenu.setViewBehind(v);
	}

	public SlidingMenu getSlidingMenu() {
		return mSlidingMenu;
	}

	@Override
	public View findViewById(int id) {
		return mSlidingMenu.findViewById(id);
	}

	public void toggle() {
		//		if (isStatic()) return;
		if (mSlidingMenu.isBehindShowing()) {
			showAbove();
		} else {
			showBehind();
		}
	}

	public void showAbove() {
		mSlidingMenu.showAbove();
	}

	public void showBehind() {
		mSlidingMenu.showBehind();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mSlidingMenu.isBehindShowing()) {
			showAbove();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void addMenuListItem(MenuListItem mli) {
		mMenuList.add(mli);
	}

	public static class SlidingMenuList extends ListView {
		public SlidingMenuList(final Context context) {
			super(context);
			setAdapter(new SlidingMenuListAdapter(context));
			setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					OnClickListener listener = ((SlidingMenuListAdapter)getAdapter()).getItem(position).mListener;
					if (listener != null) listener.onClick(view);
				}				
			});
		}
		public void add(MenuListItem mli) {
			((SlidingMenuListAdapter)getAdapter()).add(mli);
		}
	}

	public static class SlidingMenuListAdapter extends ArrayAdapter<MenuListItem> {

		public SlidingMenuListAdapter(Context context) {
			super(context, 0);
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView != null) {
				v = convertView;
			} else {
				LayoutInflater inflater = 
						(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.slidingmenurow, null);
			}
			MenuListItem item = getItem(position);
			ImageView icon = (ImageView) v.findViewById(R.id.slidingmenurowicon);
			icon.setImageDrawable(item.mIcon);
			TextView title = (TextView) v.findViewById(R.id.slidingmenurowtitle);
			title.setText(item.mTitle);
			return v;
		}
	}

	public class MenuListItem {
		private Drawable mIcon;
		private String mTitle;
		private OnClickListener mListener;
		public MenuListItem(String title) {
			mTitle = title;
		}
		public void setTitle(String title) {
			mTitle = title;
		}
		public void setOnClickListener(OnClickListener listener) {
			mListener = listener;
		}
		public View toListViewRow() {
			View v = LayoutInflater.from(SlidingFragmentActivity.this).
					inflate(R.layout.slidingmenurow, null);
			((TextView)v.findViewById(R.id.slidingmenurowtitle)).setText(mTitle);
			((ImageView)v.findViewById(R.id.slidingmenurowicon)).setImageDrawable(mIcon);
			return v;
		}
	}
}
