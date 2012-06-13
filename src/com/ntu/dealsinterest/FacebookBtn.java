package com.ntu.dealsinterest;

import com.ntu.dealsinterest.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

import com.facebook.SessionEvents;
import com.facebook.SessionStore;
import com.facebook.android.Facebook;
import com.facebook.SessionEvents.AuthListener;
import com.facebook.SessionEvents.LogoutListener;

public class FacebookBtn extends ImageButton {

	private Facebook facebook;
	private Context context;

	private SessionListener mSessionListener = new SessionListener();

	public FacebookBtn(Context context)
	{
		super(context);
	}

	public FacebookBtn(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public FacebookBtn(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void init(final Activity activity, final Facebook fb, Context con)
	{
		setBackgroundColor(Color.TRANSPARENT);
		setAdjustViewBounds(true);
		setImageResource(fb.isSessionValid() ? R.drawable.logout_button : R.drawable.fb_login_button);
		drawableStateChanged();

		context = con;
		SessionEvents.addAuthListener(mSessionListener);
		SessionEvents.addLogoutListener(mSessionListener);
	}

	private class SessionListener implements AuthListener, LogoutListener {

		public void onAuthSucceed()
		{
			if(context == null)
			{
				Log.d("Session Listener: ", "context is null");
			}
			GlobalVariable FbState = ((GlobalVariable) context);
			setImageResource(R.drawable.logout_button);
			facebook = FbState.getFBState();
			if (facebook.isSessionValid())
			{
				SessionStore.save(facebook, context);
			}
		}

		public void onAuthFail(String error)
		{}

		public void onLogoutBegin()
		{}

		public void onLogoutFinish()
		{
			SessionStore.clear(context);

			setImageResource(R.drawable.fb_login_button);
		}
	}
	
}
