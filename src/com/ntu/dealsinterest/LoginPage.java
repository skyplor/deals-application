package com.ntu.dealsinterest;

import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
import com.facebook.SessionStore;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.ntu.dealsinterest.R;
import com.ntu.dealsinterest.TwitterApp.TwDialogListener;
//import com.facebook.android.Util;

import android.app.Activity;
import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.BroadcastReceiver;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
//import android.preference.PreferenceManager;
//import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class LoginPage extends Activity {

	private static final String APP_ID = "Input your Facebook APP ID here";
	private TwitterApp mTwitter;
	private Facebook mFacebook;

	private static final String twitter_consumer_key = "ujpcXzdHq3DzSpzMtcciQ";
	private static final String twitter_secret_key = "atr8AHAP1ajzcdIwXjp81Mz0QDBXHmdIZ7RgM1THlKs";

	private FacebookBtn fblogin;
	private static GlobalVariable globalVar;

	private ImageButton regBtn;
	private ImageButton mTwitterBtn;
	private Button login;
	private EditText email, password;

	Handler mHandler = new Handler();
	static final int DIALOG_ERR_LOGIN = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Go to login
		globalVar = ((GlobalVariable) getApplicationContext());
		mFacebook = globalVar.getFBState();
		if (mFacebook.isSessionValid())
		{
			SessionStore.restore(mFacebook, this);
		}
		else
		{
			mFacebook = new Facebook(APP_ID);
		}

		email = (EditText) findViewById(R.id.emailTxtBox);
		password = (EditText) findViewById(R.id.pwTxtBox);
		login = (Button) findViewById(R.id.loginBtn);
		fblogin = (FacebookBtn) findViewById(R.id.fbLoginBtn);
		regBtn = (ImageButton) findViewById(R.id.registerBtn);
		mTwitterBtn = (ImageButton) findViewById(R.id.twitBtn);

		loginInit();

		fblogin.init(this, mFacebook, getApplicationContext());
		fbInit();

		twitInit();
		mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key, 4);
		mTwitter.setListener(mTwLoginDialogListener);
		if (mTwitter.hasAccessToken())
		{

			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "Unknown" : username;

		}
		regInit();

	}

	private void twitInit()
	{
		// TODO Auto-generated method stub
		mTwitterBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mTwitter.hasAccessToken())
				{
					final AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);

					builder.setMessage("Delete current Twitter connection?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							mTwitter.resetAccessToken();
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							dialog.cancel();
						}
					});
					final AlertDialog alert = builder.create();

					alert.show();
				}
				else
				{
					Intent intent = new Intent(v.getContext(), Container.class);
					globalVar = ((GlobalVariable) getApplicationContext());
					globalVar.setTwitBtn(true);
					Log.d("Twitter Btn state: ", globalVar.getTwitBtn().toString());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, 1);
				}
			}
		});
	}

	private void loginInit()
	{
		// TODO Auto-generated method stub
		login.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Log.d("Password: ", password.getText().toString());
				ConnectDB connectCheck = new ConnectDB(email.getText().toString(), password.getText().toString(), 0);
				if (connectCheck.inputResult())
				{
					Log.d("Authenticate User: ", "True");
					globalVar = ((GlobalVariable) getApplicationContext());
					globalVar.setName(connectCheck.getUserName());
					globalVar.setHashPw(connectCheck.getPassword());
					globalVar.setEm(email.getText().toString());

					SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
					SharedPreferences.Editor editor = login.edit();
					editor.putString("emailLogin", connectCheck.getUserEmail());
					editor.putString("pwLogin", connectCheck.getPassword());
					editor.putString("userName", connectCheck.getUserName());
					editor.putString("userID", connectCheck.getUserID());
					editor.putString("userDB_NMID", connectCheck.getUserFbTwNmID());
					editor.commit();

					Intent intent = new Intent(v.getContext(), Container.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Log.d("GlobalVariable name: ", globalVar.getName());
					// startActivityForResult means that Activity1 can expect
					// info back from Activity2.
					startActivityForResult(intent, 0);
				}
				else
				{
					// do something else
					Log.d("Authenticate User: ", "False");
					showDialog(DIALOG_ERR_LOGIN);
				}
			}
		});
	}
	
	public void fbInit()
	{
		fblogin.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (!mFacebook.isSessionValid())
				{
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(), Container.class);
					SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
					SharedPreferences.Editor editor = login.edit();
					editor.putBoolean("FacebookLoggedOut", false);
					editor.commit();
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, 1);
				}
				else
				{
					SessionEvents.onLogoutBegin();
					AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
					asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
				}
			}
		});
	}

	public void regInit()
	{
		regBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), Registration.class);

				// startActivityForResult means that Activity1 can expect info
				// back from Activity2.
				startActivityForResult(intent, 0);
			}
		});
	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener()
	{
		@Override
		public void onComplete(String value)
		{
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;
		}

		@Override
		public void onError(String value)
		{

		}

		@Override
		public void onCancel()
		{
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		globalVar = ((GlobalVariable) getApplicationContext());
		mFacebook = globalVar.getFBState();
		fbInit();
	}

	@Override
	public void onBackPressed()
	{
		moveTaskToBack(true);
	}

	
	public class LogoutRequestListener extends BaseRequestListener {
		public void onComplete(String response, final Object state)
		{

			// callback should be run in the original thread,
			// not the background thread
			mHandler.post(new Runnable()
			{
				public void run()
				{
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}

	protected AlertDialog onCreateDialog(int id)
	{
		AlertDialog alertDialog;
		// do the work to define the error Dialog
		alertDialog = new AlertDialog.Builder(LoginPage.this).create();
		alertDialog.setTitle("Login Error");

		switch (id)
		{
			case DIALOG_ERR_LOGIN:
				alertDialog.setMessage("Could not authenticate you. Maybe your email/password is incorrect. Please try again.");
				break;

			default:
				alertDialog = null;
		}
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{

				// here you can add functions
				dialog.cancel();

			}
		});
		return alertDialog;
	}
}
