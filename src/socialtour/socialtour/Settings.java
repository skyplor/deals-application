package socialtour.socialtour;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import socialtour.socialtour.TwitterApp.TwDialogListener;

import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Settings extends Activity
{

	protected static final int DISPLAY_DLG = 0;
	private Button fbBtn, twitBtn, logoutBtn;
	private GlobalVariable globalVar;
	private Handler mHandler = new Handler();
	private Facebook facebook;
	private TwitterApp mTwitter;
	private Boolean fbacc = false, twitacc = false;
	private static final String APP_ID = "Input your Facebook APP ID here";
	private static final String twitter_consumer_key = "ujpcXzdHq3DzSpzMtcciQ";
	private static final String twitter_secret_key = "atr8AHAP1ajzcdIwXjp81Mz0QDBXHmdIZ7RgM1THlKs";
	private final int LOGOUT = 1, FB = 2, TWIT = 3, SETTINGS = 1;

	FbConnect fbConnect;

	private ProgressDialog mProgress;

	private String fnameS;
	private String lnameS;
	private String userName;
	private String userEmail;
	private String uid;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);
		
		Container.btn1.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setVisibility(ImageView.INVISIBLE);
        Container.btn3.setVisibility(ImageView.INVISIBLE);
		Container.map.setVisibility(ImageView.INVISIBLE);
		
		// TODO Auto-generated method stub
		logoutBtn = (Button) findViewById(R.id.lgoutBtn);
		fbBtn = (Button) findViewById(R.id.fbBtnAdd);
		twitBtn = (Button) findViewById(R.id.twitBtnAdd);

		globalVar = ((GlobalVariable) getApplicationContext());
		mProgress = new ProgressDialog(getParent());		
		facebook = globalVar.getFBState();
		if (facebook == null)
		{
			Log.d("Facebook state", "null");
		}
		else
		{
			Log.d("Facebook state", "not null");
		}

		mTwitter = new TwitterApp(getParent(), twitter_consumer_key, twitter_secret_key, SETTINGS);
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
	
    @Override
    public void onBackPressed() {
    	Container.settings.setEnabled(true);
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
				PorterDuffColorFilter filter = new PorterDuffColorFilter(0xEAEAEA, PorterDuff.Mode.SRC_ATOP);
				d.setColorFilter(filter);
				fbBtn.setText("Disconnect");
				
				twitacc = true;
				Drawable d1 = findViewById(R.id.twitBtnAdd).getBackground();
				PorterDuffColorFilter filter1 = new PorterDuffColorFilter(0xEAEAEA, PorterDuff.Mode.SRC_ATOP);
				d1.setColorFilter(filter1);
				twitBtn.setText("Disconnect");
			}
			else if (facebook.isSessionValid() && !mTwitter.hasAccessToken())
			{
				fbacc = true;
				Drawable d2 = findViewById(R.id.fbBtnAdd).getBackground();
				PorterDuffColorFilter filter2 = new PorterDuffColorFilter(0xEAEAEA, PorterDuff.Mode.SRC_ATOP);
				d2.setColorFilter(filter2);
				fbBtn.setText("Disconnect");
				twitacc = false;
				twitBtn.setText("Connect to Twitter");
				Drawable d3 = findViewById(R.id.twitBtnAdd).getBackground();
				findViewById(R.id.twitBtnAdd).invalidateDrawable(d3);
				d3.clearColorFilter();
			}
			else
			{
				twitacc = true;
				Drawable d4 = findViewById(R.id.twitBtnAdd).getBackground();
				PorterDuffColorFilter filter3 = new PorterDuffColorFilter(0xEAEAEA, PorterDuff.Mode.SRC_ATOP);
				d4.setColorFilter(filter3);
				twitBtn.setText("Disconnect");
				fbacc = false;
				fbBtn.setText("Connect to Facebook");
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
			twitBtn.setText("Add Twitter");
			Drawable d2 = findViewById(R.id.twitBtnAdd).getBackground();
			findViewById(R.id.twitBtnAdd).invalidateDrawable(d2);
			d2.clearColorFilter();
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
			editor.putString("userDB_NMID", null);
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

			editor.putString("userFBID", null);
			editor.putString("userDB_FBID", null);
			editor.putString("emailFB_Login", null);
			editor.putBoolean("FacebookLoggedOut", true);
			editor.commit();

			Facebook mFacebook = globalVar.getFBState();
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

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener()
	{
		@Override
		public void onComplete(String value)
		{
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;
			initButtons();
			twitacc = true;
		}

		@Override
		public void onError(String value)
		{
			
		}

		@Override
		public void onCancel()
		{
			
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


		private Facebook facebook;
		GlobalVariable FbState = ((GlobalVariable) getApplicationContext());

		private SharedPreferences sharedPref;
		private Editor editor;

		public FbConnect(String appId, Activity activity, Context context)
		{

			sharedPref = context.getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);

			editor = sharedPref.edit();
			globalVar = ((GlobalVariable) getApplicationContext());

			facebook = FbState.getFBState();

			Log.d("in Settings, FBConnect:", "before login()");
			login();
			
		}

		public void login()
		{
			if (!facebook.isSessionValid())
			{
				Log.d("in Settings, FBConnect:", "before authorizing");
				facebook.authorize(getParent(), FACEBOOK_PERMISSION, new LoginDialogListener());
			}
			else
			{
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
					uid = json.getString("id");

					editor.putString("userFBname", userName);
					editor.putString("userFBID", uid);
					editor.putBoolean("FacebookLoggedOut", false);
					editor.commit();
					Log.d("Facebook", fnameS);

					// callback should be run in the original thread,
					// not the background thread
					mHandler.post(new Runnable()
					{
						public void run()
						{
							ConnectDB connectCheck;
							try
							{
								Log.d("in settings: uid", uid);
								connectCheck = new ConnectDB(userName, userEmail, uid, "", "user_fb", SETTINGS, Settings.this);
								Log.d("userName: ", connectCheck.getUserName());
								editor.putString("userName", connectCheck.getUserName());
								editor.putString("emailFB_Login", connectCheck.getUserEmail());
								editor.putString("userDB_FBID", connectCheck.getUserFbTwNmID());
								editor.putString("userID", connectCheck.getUserID());
								editor.commit();

								initButtons();
								fbacc = true;
								mProgress.dismiss();
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
