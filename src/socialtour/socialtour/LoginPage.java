package socialtour.socialtour;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import socialtour.socialtour.TwitterApp.TwDialogListener;
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

	private static final String APP_ID = "222592464462347";
	private TwitterApp mTwitter;
	private Facebook mFacebook;

	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";

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

		// if (APP_ID == null)
		// {
		// Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
		// "specified before running this example: see Example.java");
		// }

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

		// IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction("com.package.ACTION_LOGOUT");
		// registerReceiver(new BroadcastReceiver()
		// {
		//
		// @Override
		// public void onReceive(Context context, Intent intent)
		// {
		// Log.d("onReceive", "Logout in progress");
		// // At this point you should start the login activity and finish
		// // this one
		// finish();
		// }
		// }, intentFilter);

		loginInit();

		fblogin.init(this, mFacebook, getApplicationContext());
		fbInit();

		twitInit();
		mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);
		if (mTwitter.hasAccessToken())
		{
			// mTwitterBtn.setChecked(true);

			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "Unknown" : username;

			// mTwitterBtn.setText("  Twitter (" + username + ")");
			// mTwitterBtn.setTextColor(Color.WHITE);
		}

		// mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		regInit();

		// else
		// {
		// // Go to Next Activity
		// globalVar = ((GlobalVariable) getApplicationContext());
		// globalVar.setfbBtn(false);
		//
		// ConnectDB connectCheck = new ConnectDB(Uname, pass, 1);
		// if (connectCheck.inputResult())
		// {
		// globalVar.setName(connectCheck.getName());
		// Intent intent = new Intent(this, SearchShops.class);
		// Log.d("GlobalVariable name: ", globalVar.getName());
		// // startActivityForResult means that Activity1 can expect
		// // info
		// // back from Activity2.
		// startActivityForResult(intent, 0);
		// }
		// else
		// {
		// // do something else
		// Log.d("Authenticate User: ", "False");
		// showDialog(DIALOG_ERR);
		// }
		// }

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

							// mTwitterBtn.setChecked(false);
							// mTwitterBtn.setText("  Twitter (Not connected)");
							// mTwitterBtn.setTextColor(Color.GRAY);
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							dialog.cancel();

							// mTwitterBtn.setChecked(true);
						}
					});
					final AlertDialog alert = builder.create();

					alert.show();
				}
				else
				{
					// mTwitterBtn.setChecked(false);
					Intent intent = new Intent(v.getContext(), Container.class);
					globalVar = ((GlobalVariable) getApplicationContext());
					globalVar.setTwitBtn(true);
					Log.d("Twitter Btn state: ", globalVar.getTwitBtn().toString());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, 1);
//					mTwitter.authorize();
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
					globalVar.setfbBtn(false);
					globalVar.setHashPw(connectCheck.getPassword());
					globalVar.setEm(email.getText().toString());

					SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
					SharedPreferences.Editor editor = login.edit();
					editor.putString("emailLogin", connectCheck.getUserEmail());
					editor.putString("pwLogin", connectCheck.getPassword());
					editor.putString("userName", connectCheck.getUserName());
					editor.putString("userID", connectCheck.getUserID());
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

	// public void fbinit(final Activity activity, final Facebook fb, final
	// String[] permissions)
	// {
	// //mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	// // fblogin.setBackgroundColor(Color.TRANSPARENT);
	// // fblogin.setAdjustViewBounds(true);
	// // fblogin.setImageResource(fb.isSessionValid() ?
	// R.drawable.logout_button : R.drawable.login_button);
	// // fblogin.drawableStateChanged();
	//
	//
	// fblogin.setOnClickListener(new View.OnClickListener()
	// {
	// public void onClick(View v)
	// {
	// fblogin = new FacebookConnector(APP_ID, activity,
	// getApplicationContext(), permissions);
	// facebookConnector.login();
	// }
	// });
	// }
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
					globalVar = ((GlobalVariable) getApplicationContext());
					globalVar.setfbBtn(true);
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

				globalVar = ((GlobalVariable) getApplicationContext());
				globalVar.setfbBtn(false);
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
			// TODO Auto-generated method stub
			
		}
	};

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data)
	// {
	// if (requestCode != 0)
	// {
	// this.fblogin.getFacebook().authorizeCallback(requestCode, resultCode,
	// data);
	// }
	// }

	@Override
	protected void onResume()
	{
		super.onResume();
		globalVar = ((GlobalVariable) getApplicationContext());
		mFacebook = globalVar.getFBState();
		// updateLoginStatus();
		fbInit();
	}

	@Override
	public void onBackPressed()
	{
		moveTaskToBack(true);
	}

	// protected void onDestroy()
	// {
	// unregisterReceiver(null);
	// }

	// public class LoginRequestListener extends BaseRequestListener {
	// public void onComplete(String response, final Object state)
	// {
	// try
	// {
	// // process the response here: executed in background thread
	// Log.d("Facebook-Example", "Response: " + response.toString());
	// JSONObject json = Util.parseJson(response);
	// fname = json.getString("first_name");
	// lname = json.getString("last_name");
	// email = json.getString("email");
	// gender = json.getString("gender");
	// bday = json.getString("birthday");
	// Log.d("Facebook", fname);
	// user = new UserParticulars(fname, lname, email, gender, bday);
	// // callback should be run in the original thread,
	// // not the background thread
	// LoginPage.this.runOnUiThread(new Runnable()
	// {
	// public void run()
	// {
	// // Intent intent = new Intent(getApplicationContext(),
	// Registration.class);
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

			// case DIALOG_ERR:
			// alertDialog.setMessage("Could not authenticate you. Perhaps your details were not saved. Please login again.");
			// break;

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
