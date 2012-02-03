package socialtour.socialtour;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import socialtour.socialtour.TwitterApp.TwDialogListener;

import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
import com.facebook.SessionStore;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Settings extends Activity
{

	private Button fbBtn, twitBtn, logoutBtn;
	private GlobalVariable globalVar;
	private Handler mHandler = new Handler();
	private Facebook facebook;
	private TwitterApp mTwitter;
	private Boolean fbacc = false, twitacc = false;
	private static final String APP_ID = "222592464462347";
	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";
	private final int LOGOUT = 1, FB = 2, TWIT = 3;

	FbConnect fbConnect;

	private ProgressDialog mProgress;

	private String fnameS;
	private String lnameS;
	private String userName;
	private String userEmail;
	// private String genderS;
	// private String bdayS;
	private String uid;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);
		// TODO Auto-generated method stub
		logoutBtn = (Button) findViewById(R.id.lgoutBtn);
		fbBtn = (Button) findViewById(R.id.fbBtnAdd);
		twitBtn = (Button) findViewById(R.id.twitBtnAdd);

		globalVar = ((GlobalVariable) getApplicationContext());
		mProgress = new ProgressDialog(this);
		// fbBtnStat = globalVar.getfbBtn();
		// twitBtnStat = globalVar.getTwitBtn();
		facebook = globalVar.getFBState();
		if (facebook == null)
		{
			Log.d("Facebook state", "null");
		}
		else
		{
			Log.d("Facebook state", "not null");
		}

		mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);
		globalVar.setTwitState(mTwitter);

		logoutBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				doLogout(LOGOUT);
			}

		});
		initButtons();
		fbBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (facebook.isSessionValid())
				{
					doLogout(FB);
				}
				else
				{
					doLogin(FB);
				}
			}
		});

		twitBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (mTwitter.hasAccessToken())
				{
					doLogout(TWIT);
				}
				else
				{
					doLogin(TWIT);
				}
			}
		});
	}

	private Boolean detectNormalLogin()
	{
		SharedPreferences userDetails = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
		String Uname = userDetails.getString("emailLogin", "");
		String pass = userDetails.getString("pwLogin", "");
		if (Uname == "" && pass.equals(""))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private void initButtons()
	{
		// TODO Auto-generated method stub
		Log.d("facebook session in Settings", Boolean.toString(facebook.isSessionValid()));
		if (facebook.isSessionValid() || mTwitter.hasAccessToken())
		{
			if(facebook.isSessionValid() && mTwitter.hasAccessToken())
			{
				fbacc = true;
				Drawable d = findViewById(R.id.fbBtnAdd).getBackground();
				PorterDuffColorFilter filter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
				d.setColorFilter(filter);
				fbBtn.setText("Disconnect");
				
				twitacc = true;
				Drawable d1 = findViewById(R.id.twitBtnAdd).getBackground();
				PorterDuffColorFilter filter1 = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
				d1.setColorFilter(filter1);
				twitBtn.setText("Disconnect");
			}
			else if (facebook.isSessionValid() && !mTwitter.hasAccessToken())
			{
				fbacc = true;
				Drawable d2 = findViewById(R.id.fbBtnAdd).getBackground();
				PorterDuffColorFilter filter2 = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
				d2.setColorFilter(filter2);
				fbBtn.setText("Disconnect");
				// fbBtn.setBackgroundColor(Color.MAGENTA);
				twitacc = false;
				twitBtn.setText("Add Twitter");
				Drawable d3 = findViewById(R.id.twitBtnAdd).getBackground();
				findViewById(R.id.twitBtnAdd).invalidateDrawable(d3);
				d3.clearColorFilter();
			}
			else
			{
				twitacc = true;
				Drawable d4 = findViewById(R.id.twitBtnAdd).getBackground();
				PorterDuffColorFilter filter3 = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
				d4.setColorFilter(filter3);
				twitBtn.setText("Disconnect");
				// twitBtn.setBackgroundColor(Color.MAGENTA);
				fbacc = false;
				fbBtn.setText("Add Facebook");
				Drawable d5 = findViewById(R.id.fbBtnAdd).getBackground();
				findViewById(R.id.fbBtnAdd).invalidateDrawable(d5);
				d5.clearColorFilter();
			}
		}

		else
		{
			fbacc = false;
			twitacc = false;
			fbBtn.setText("Add Facebook");
			Drawable d = findViewById(R.id.fbBtnAdd).getBackground();
			findViewById(R.id.fbBtnAdd).invalidateDrawable(d);
			d.clearColorFilter();
			// fbBtn.setBackgroundColor(Color.GRAY);
			twitBtn.setText("Add Twitter");
			Drawable d2 = findViewById(R.id.twitBtnAdd).getBackground();
			findViewById(R.id.twitBtnAdd).invalidateDrawable(d2);
			d2.clearColorFilter();
			// twitBtn.setBackgroundColor(Color.GRAY);
		}
	}

	protected void doLogin(int type)
	{
		// TODO Auto-generated method stub
		switch (type)
		{
		case FB:
			Log.d("in doLogin:", "before fbconnect");
			fbConnect = new FbConnect(APP_ID, Settings.this, getApplicationContext());
			break;
		case TWIT:
			mTwitter.authorize();
			break;
		}
	}

	protected void doLogout(int type)
	{
		if (type == LOGOUT)
		{
			// Logout logic here...
			globalVar.setName("");
			globalVar.setHashPw("");
			globalVar.setEm("");

			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			SharedPreferences.Editor editor = login.edit();
			editor.putString("userFBname", null);
			editor.putString("userName", null);
			editor.putString("userFBID", null);
			editor.putString("userDB_FBID", null);
			editor.putString("userID", null);
			editor.putString("emailLogin", null);
			editor.putString("emailFB_Login", null);
			editor.putString("pwLogin", null);
			editor.putBoolean("FacebookLoggedOut", true);
			editor.commit();

			Facebook mFacebook = globalVar.getFBState();
			SessionEvents.onLogoutBegin();
			if (mFacebook != null)
			{
				AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
				asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
			}

			mTwitter.resetAccessToken();
			globalVar.setTwitBtn(false);

			// Return to the login activity
			Intent intent = new Intent(this, LoginPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		else if (type == FB)
		{
			// Go to LoginPage
			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			SharedPreferences.Editor editor = login.edit();
			editor.putString("userFBname", null);
//			editor.putString("userName", null);
//			editor.putString("userID", null);
			editor.putString("userFBID", null);
			editor.putString("userDB_FBID", null);
			editor.putString("emailFB_Login", null);
			editor.putBoolean("FacebookLoggedOut", true);
			editor.commit();

			Facebook mFacebook = globalVar.getFBState();
//			globalVar.setfbBtn(false);
			SessionEvents.onLogoutBegin();
			AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
			asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());

			// initButtons();
		}
		else if (type == TWIT)
		{
			mTwitter.resetAccessToken();
			globalVar.setTwitBtn(false);
			initButtons();
			twitacc = false;
			if(!fbacc && !detectNormalLogin())
			{
				Intent intent = new Intent(this, LoginPage.class);
				startActivity(intent);
			}
		}

	}

	// private void refreshBtn(int type)
	// {
	// // TODO Auto-generated method stub
	//
	// }

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener()
	{
		@Override
		public void onComplete(String value)
		{
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;
			initButtons();
			twitacc = true;
			// SharedPreferences sharedPref =
			// getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			// name.setText("Hello " + username + ",");
			// name.setText("Hello " + sharedPref.getString("user_name", "") +
			// ",");
			// mTwitterBtn.setText("  Twitter  (" + username + ")");
			// mTwitterBtn.setChecked(true);
			// mTwitterBtn.setTextColor(Color.WHITE);

			// Toast.makeText(TestConnect.this, "Connected to Twitter as " +
			// username, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(String value)
		{
			// mTwitterBtn.setChecked(false);
			//
			// Toast.makeText(TestConnect.this, "Twitter connection failed",
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel()
		{
			// Return to the activity
			// Intent intent = new Intent(Settings.this, LoginPage.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
		}
	};

	public class LogoutRequestListener extends BaseRequestListener
	{
		public void onComplete(String response, final Object state)
		{

			// callback should be run in the original thread,
			// not the background thread
			mHandler.post(new Runnable()
			{
				public void run()
				{
					SessionEvents.onLogoutFinish();
					initButtons();
					fbacc = false;
					SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
					SharedPreferences.Editor editor = login.edit();
					editor.putBoolean("FacebookLoggedOut", true);
					editor.commit();
					if(!twitacc && !detectNormalLogin())
					{
						Intent intent = new Intent(Settings.this, LoginPage.class);
						startActivity(intent);
					}
				}
			});
		}
	}

	public class FbConnect
	{

		private final String[] FACEBOOK_PERMISSION =
		{ "user_birthday", "email", "publish_stream", "read_stream", "offline_access" };

		private Context context;
		private Activity activity;
		// private Handler mHandler;
		private Facebook facebook;
		GlobalVariable FbState = ((GlobalVariable) getApplicationContext());

		private SharedPreferences sharedPref;
		private Editor editor;

		// private SessionListener mSessionListener = new SessionListener();

		public FbConnect(String appId, Activity activity, Context context)
		{

			this.context = context;
			// this.mHandler = new Handler();
			this.activity = activity;

			sharedPref = context.getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);

			editor = sharedPref.edit();
			globalVar = ((GlobalVariable) getApplicationContext());

			// SharedPreferences prefs=
			// PreferenceManager.getDefaultSharedPreferences(SearchShops.this);
			// String access_token = prefs.getString("access_token", null);
			// Long expires = prefs.getLong("access_expires", -1);
			// String sharedName = prefs.getString("name", "");
			//
			//
			// if (access_token != null && expires != -1)
			// {
			// facebook.setAccessToken(access_token);
			// facebook.setAccessExpires(expires);
			// }

			// if (!facebook.isSessionValid() || sharedName.equals(""))
			// {
			// facebook.authorize(activity, FACEBOOK_PERMISSION, new
			// LoginDialogListener());
			// }
			// else
			// {
			// name.setText("Hello " + sharedName + ",");
			// }
			facebook = FbState.getFBState();

			// if (!facebook.isSessionValid())
			// {
			// facebook = new Facebook(APP_ID);
			// FbState.setFbState(facebook);
			Log.d("in Settings, FBConnect:", "before login()");
			login();
			// }
			// else
			// {
			// SessionStore.restore(facebook, context);
			// }

			// SessionEvents.addAuthListener(mSessionListener);
			// SessionEvents.addLogoutListener(mSessionListener);

		}

		public void login()
		{
			// GlobalVariable fbBtn = ((GlobalVariable)
			// getApplicationContext());
			// Boolean fbButton = fbBtn.getfbBtn();
			// if (fbButton == true)
			// {
			if (!facebook.isSessionValid())
			{
				Log.d("in Settings, FBConnect:", "before authorizing");
				facebook.authorize(activity, FACEBOOK_PERMISSION, new LoginDialogListener());
			}
			// }
			else
			{
				// globalVar = ((GlobalVariable) getApplicationContext());
				// name.setText("Hello " + sharedPref.getString("facebookName",
				// "") + ",");
				mProgress.dismiss();
			}
		}

		private final class LoginDialogListener implements DialogListener
		{
			public void onComplete(Bundle values)
			{
				Log.d("in Settings, FBConnect:", "in onComplete");
				SessionEvents.onLoginSuccess();

				mProgress.setMessage("Finalizing ...");
				mProgress.show();
				AsyncFacebookRunner syncRunner = new AsyncFacebookRunner(facebook);
				syncRunner.request("me", new LoginRequestListener());
			}

			public void onFacebookError(FacebookError error)
			{
				Log.d("in Settings, FBConnect:", "in onFacebookError");
				SessionEvents.onLoginError(error.getMessage());
			}

			public void onError(DialogError error)
			{
				Log.d("in Settings, FBConnect:", "in onError");
				SessionEvents.onLoginError(error.getMessage());
			}

			public void onCancel()
			{
				Log.d("in Settings, FBConnect:", "in onCancel");
				SessionEvents.onLoginError("Action Canceled");
				// Intent intent = new Intent(context, LoginPage.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
			}
		}

		public class LoginRequestListener extends BaseRequestListener
		{
			public void onComplete(String response, final Object state)
			{
				try
				{
					// process the response here: executed in background thread
					Log.d("Facebook-Container", "Response: " + response.toString());
					JSONObject json = Util.parseJson(response);
					fnameS = json.getString("first_name");
					lnameS = json.getString("last_name");
					userName = fnameS + " " + lnameS;
					userEmail = json.getString("email");
					// genderS = json.getString("gender");
					// bdayS = json.getString("birthday");
					uid = json.getString("id");

					editor.putString("userFBname", userName);
					editor.putString("userFBID", uid);
					editor.putBoolean("FacebookLoggedOut", false);
					editor.commit();
					Log.d("Facebook", fnameS);
					// userS = new UserParticulars(fnameS, lnameS, userEmail,
					// genderS, bdayS);

					// callback should be run in the original thread,
					// not the background thread
					mHandler.post(new Runnable()
					{
						public void run()
						{
							ConnectDB connectCheck;
							try
							{
								connectCheck = new ConnectDB(userName, userEmail, "", "user_fb");

								editor.putString("userName", connectCheck.getUserName());
								editor.putString("emailFB_Login", connectCheck.getUserEmail());
								editor.putString("userDB_FBID", connectCheck.getUserID());
								editor.commit();

								// editor.commit();
								// name.setText("Hello " + nameS + ",");
								initButtons();
								fbacc = true;
								mProgress.dismiss();
								// Log.d("Facebook session 2: ",
								// Boolean.toString(facebook.isSessionValid()));
							}
							catch (NoSuchAlgorithmException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (UnsupportedEncodingException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// globalVar = ((GlobalVariable)
							// getApplicationContext());
							// globalVar.setName(nameS);
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// String token = facebook.getAccessToken();
							// long token_expires = facebook.getAccessExpires();

							// SharedPreferences prefs=
							// PreferenceManager.getDefaultSharedPreferences(SearchShops.this);
							//
							// prefs.edit().putLong("access_expires",
							// token_expires).commit();
							//
							// prefs.edit().putString("access_token",
							// token).commit();
							//
							// prefs.edit().putString("name", nameS).commit();
							// fname.setText(fnameS);
							// lname.setText(lnameS);
							// email.setText(emailS);
							// if (genderS.equals("male"))
							// {
							// male.setChecked(true);
							// female.setChecked(false);
							// }
							// else if (genderS.equals("female"))
							// {
							// female.setChecked(true);
							// male.setChecked(false);
							// }
							//
							// for (int i = 0; i < 3; i++)
							// {
							// bdayInt[i] = getBday(bdayS)[i];
							// }
							// bday.setText("Your Birthdate is: " + bdayInt[0]
							// +"/" + bdayInt[1] + "/" + bdayInt[2]);
						}
					});
				}
				catch (JSONException e)
				{
					Log.w("Facebook-Example", "JSON Error in response");
				}
				catch (FacebookError e)
				{
					Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
				}
			}
		}

		public Facebook getFacebook()
		{
			return this.facebook;
		}

	}
}
