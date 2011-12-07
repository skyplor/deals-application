package socialtour.socialtour;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

import com.facebook.android.Facebook;
import socialtour.socialtour.SessionEvents.AuthListener;
import socialtour.socialtour.SessionEvents.LogoutListener;

public class FacebookBtn extends ImageButton {

	// private static final String APP_ID = "222592464462347";
	private Facebook facebook;
//	private static GlobalVariable applicationcontext;
	private Context context;
//	private String[] permissions;
//	private Handler mHandler;
//	private Activity activity;
//
//	private UserParticulars userS;
//	private String fnameS;
//	private String lnameS;
//	private String emailS;
//	private String genderS;
//	private String bdayS;

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
		// mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		setBackgroundColor(Color.TRANSPARENT);
		setAdjustViewBounds(true);
		setImageResource(fb.isSessionValid() ? R.drawable.logout_button : R.drawable.fb_login_button);
		drawableStateChanged();

		context = con;
		SessionEvents.addAuthListener(mSessionListener);
		SessionEvents.addLogoutListener(mSessionListener);
		// login();

		// setOnClickListener(new View.OnClickListener()
		// {
		// public void onClick(View v)
		// {
		// //facebookConnector = new FacebookConnector();//APP_ID, activity,
		// getApplicationContext(), permissions);
		// login();
		// }
		// });
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
	// public FacebookConnector(String appId, Activity activity, Context
	// context, String[] permissions)
	// {
	// //this.facebook = new Facebook(appId);
	// this.permissions = permissions;
	// this.activity = activity;
	// this.context = context;
	// this.mHandler = new Handler();
	// // SessionStore.restore(facebook, context);
	// SessionEvents.addAuthListener(mSessionListener);
	// SessionEvents.addLogoutListener(mSessionListener);
	//
	// }

	// public void login()
	// {
	// if (!facebook.isSessionValid())
	// {
	// facebook.authorize(activity, permissions, new LoginDialogListener());
	// }
	// else
	// {
	// logout();
	// }
	// }

	// public void logout()
	// {
	// SessionEvents.onLogoutBegin();
	// AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
	// asyncRunner.logout(this.context, new LogoutRequestListener());
	// }

	// public class LogoutRequestListener extends BaseRequestListener {
	// public void onComplete(String response, final Object state)
	// {
	// // callback should be run in the original thread,
	// // not the background thread
	// mHandler.post(new Runnable()
	// {
	// public void run()
	// {
	// SessionEvents.onLogoutFinish();
	// }
	// });
	// }
	// }
	//
	// private class SessionListener implements AuthListener, LogoutListener {
	//
	// public void onAuthSucceed()
	// {
	// SessionStore.save(facebook, context);
	// // AsyncFacebookRunner asyncFbRunner = new AsyncFacebookRunner(facebook);
	// // asyncFbRunner.request("me", new LoginRequestListener());
	// // Intent intent = new Intent(getBaseContext(), Registration.class);
	// // intent.putExtra("data", user);
	// // startActivityForResult(intent, 1);
	// }
	//
	// public void onAuthFail(String error)
	// {}
	//
	// public void onLogoutBegin()
	// {}
	//
	// public void onLogoutFinish()
	// {
	// SessionStore.clear(context);
	// }
	// }

	// private final class LoginDialogListener implements DialogListener {
	// public void onComplete(Bundle values)
	// {
	// SessionEvents.onLoginSuccess();
	//
	// AsyncFacebookRunner asyncFbRunner = new AsyncFacebookRunner(facebook);
	// asyncFbRunner.request("me", new LoginRequestListener());
	// // Intent intent = new Intent(getApplicationContext(),
	// Registration.class);
	// // //Log.d("Facebook in LDL: ",fname);
	// // intent.putExtra("data", user);
	// // startActivityForResult(intent, 1);
	// }
	//
	// public void onFacebookError(FacebookError error)
	// {
	// SessionEvents.onLoginError(error.getMessage());
	// }
	//
	// public void onError(DialogError error)
	// {
	// SessionEvents.onLoginError(error.getMessage());
	// }
	//
	// public void onCancel()
	// {
	// SessionEvents.onLoginError("Action Canceled");
	// }
	// }

	// public class LoginRequestListener extends BaseRequestListener {
	// public void onComplete(String response, final Object state)
	// {
	// try
	// {
	// // process the response here: executed in background thread
	// Log.d("Facebook-Example", "Response: " + response.toString());
	// JSONObject json = Util.parseJson(response);
	// fnameS = json.getString("first_name");
	// lnameS = json.getString("last_name");
	// emailS = json.getString("email");
	// genderS = json.getString("gender");
	// bdayS = json.getString("birthday");
	// Log.d("Facebook", fnameS);
	// userS = new UserParticulars(fnameS, lnameS, emailS, genderS, bdayS);
	// // callback should be run in the original thread,
	// // not the background thread
	// mHandler.post(new Runnable()
	// {
	// public void run()
	// {
	// // Intent intent = new Intent(getApplicationContext(),
	// // Registration.class);
	// // intent.putExtra("data", user);
	// // startActivityForResult(intent, 1);
	// }
	// });
	// }
	// catch (JSONException e)
	// {
	// Log.w("Facebook-Example", "JSON Error in response");
	// }
	// catch (FacebookError e)
	// {
	// Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
	// }
	// }
	// }

	// public Facebook getFacebook()
	// {
	// return facebook;
	// }
}
