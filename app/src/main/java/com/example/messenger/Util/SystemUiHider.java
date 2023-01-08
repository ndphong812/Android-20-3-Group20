package com.example.messenger.Util;

import android.app.Activity;
import android.os.Build;
import android.view.View;


public abstract class SystemUiHider {
	/**
	 * When this flag is set, the
	 * {@link android.view.WindowManager.LayoutParams#FLAG_LAYOUT_IN_SCREEN}
	 * flag will be set on older devices, making the status bar "float" on top
	 * of the activity layout. This is most useful when there are no controls at
	 * the top of the activity layout.
	 * <p>
	 * This flag isn't used on newer devices because the <a
	 * href="http://developer.android.com/design/patterns/actionbar.html">action
	 * bar</a>, the most important structural element of an Android app, should
	 * be visible and not obscured by the system UI.
	 */
	public static final int FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES = 0x1;

	/**
	 * When this flag is set, {@link #show()} and {@link #hide()} will toggle
	 * the visibility of the status bar. If there is a navigation bar, show and
	 * hide will toggle low profile mode.
	 */
	public static final int FLAG_FULLSCREEN = 0x2;

	/**
	 * When this flag is set, {@link #show()} and {@link #hide()} will toggle
	 * the visibility of the navigation bar, if it's present on the device and
	 * the device allows hiding it. In cases where the navigation bar is present
	 * but cannot be hidden, show and hide will toggle low profile mode.
	 */
	public static final int FLAG_HIDE_NAVIGATION = FLAG_FULLSCREEN | 0x4;

	/**
	 * The activity associated with this UI hider object.
	 */
	protected Activity mActivity;

	/**
	 * The view on which {@link View#setSystemUiVisibility(int)} will be called.
	 */
	protected View mAnchorView;

	/**
	 * The current UI hider flags.
	 * 
	 * @see #FLAG_FULLSCREEN
	 * @see #FLAG_HIDE_NAVIGATION
	 * @see #FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES
	 */
	protected int mFlags;

	/**
	 * The current visibility callback.
	 */
	protected OnVisibilityChangeListener mOnVisibilityChangeListener = sDummyListener;


	public static SystemUiHider getInstance(Activity activity, View anchorView,
			int flags) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return new SystemUiHiderHoneycomb(activity, anchorView, flags);
		} else {
			return new SystemUiHiderBase(activity, anchorView, flags);
		}
	}

	protected SystemUiHider(Activity activity, View anchorView, int flags) {
		mActivity = activity;
		mAnchorView = anchorView;
		mFlags = flags;
	}


	public abstract void setup();

	/**
	 * Returns whether or not the system UI is visible.
	 */
	public abstract boolean isVisible();

	/**
	 * Hide the system UI.
	 */
	public abstract void hide();

	/**
	 * Show the system UI.
	 */
	public abstract void show();

	/**
	 * Toggle the visibility of the system UI.
	 */
	public void toggle() {
		if (isVisible()) {
			hide();
		} else {
			show();
		}
	}

	/**
	 * Registers a callback, to be triggered when the system UI visibility
	 * changes.
	 */
	public void setOnVisibilityChangeListener(
			OnVisibilityChangeListener listener) {
		if (listener == null) {
			listener = sDummyListener;
		}

		mOnVisibilityChangeListener = listener;
	}

	/**
	 * A dummy no-op callback for use when there is no other listener set.
	 */
	private static OnVisibilityChangeListener sDummyListener = new OnVisibilityChangeListener() {
		@Override
		public void onVisibilityChange(boolean visible) {
		}
	};

	/**
	 * A callback interface used to listen for system UI visibility changes.
	 */
	public interface OnVisibilityChangeListener {
		/**
		 * Called when the system UI visibility has changed.
		 * 
		 * @param visible
		 *            True if the system UI is visible.
		 */
		public void onVisibilityChange(boolean visible);
	}
}
