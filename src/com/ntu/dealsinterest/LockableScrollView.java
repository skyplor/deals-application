package com.ntu.dealsinterest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class LockableScrollView extends ScrollView
{
	public LockableScrollView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public LockableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LockableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	// true if we can scroll (not locked)
	// false if we cannot scroll (locked)
	private boolean mScrollable = true;

	public void setIsScrollable(boolean scrollable)
	{
		mScrollable = scrollable;
	}

	public boolean getIsScrollable()
	{
		return mScrollable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			// if we can scroll pass the event to the superclass
			if (mScrollable)
			{
				Log.d("In LockableScrollView", Boolean.toString(mScrollable));
				return super.onTouchEvent(ev);
			}
			// only continue to handle the touch event if scrolling enabled
			Log.d("In LockableScrollView", Boolean.toString(mScrollable));
			return mScrollable; // mScrollable is always false at this point

		default:
			return super.onTouchEvent(ev);
		}
	}

}
