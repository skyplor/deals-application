package com.ntu.dealsinterest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
import com.facebook.SessionStore;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.ntu.dealsinterest.TwitterApp.TwDialogListener;

import com.ntu.dealsinterest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

//import android.widget.TabHost.OnTabChangeListener;

@SuppressWarnings("deprecation")
public class Container extends TabActivity
{
	Uri outputFileUri;
	int currentTab = 0;
	static final int DIALOG_ERR_LOGIN = 0, INIT_NORM = 0, INIT_FB = 1, INIT_TWIT = 2;
	static int TYPE = 0;

	private final String TAG = "CONTAINER";
	
	private static final String APP_ID = "222592464462347";
	private static final String twitter_consumer_key = "ujpcXzdHq3DzSpzMtcciQ";
	private static final String twitter_secret_key = "atr8AHAP1ajzcdIwXjp81Mz0QDBXHmdIZ7RgM1THlKs";
	private static final int CONTAINER = 4;
	
	FbConnect fbConnect;
	private static GlobalVariable globalVar;

	private ProgressDialog mProgress;
	Handler mHandler = new Handler();

	private Boolean fblogout, twitBtn;
	private Facebook facebook;
	private TwitterApp mTwitter;

	private String fnameS;
	private String lnameS;
	private String userName;
	private String userEmail;
	private String genderS;
	private String bdayS;
	private String uid;
	public static ImageView btn1,btn2,btn3,map;
	Intent intent; // Reusable Intent for each tab
	Resources res; // Resource object to get Drawables
	public static TabHost tabHost; // The activity TabHost
	TabSpec spec; // Resusable TabSpec for each tab
	
	public static ImageView browse,share,search,settings;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container);
		
		btn1= (ImageView) findViewById(R.id.headerLatest);
		btn2= (ImageView) findViewById(R.id.headerHot);
		btn3= (ImageView) findViewById(R.id.headerNearby);
		map = (ImageView) findViewById(R.id.headerMap);
		
		browse = (ImageView) findViewById(R.id.headerBrowse);
		share = (ImageView) findViewById(R.id.headerCamera);
		search = (ImageView) findViewById(R.id.headerSearch);
		settings = (ImageView) findViewById(R.id.headerSettings);
		res = getResources(); // Resource object to get Drawables
		tabHost = getTabHost(); // The activity TabHost
		
		
		if (APP_ID == null)
		{
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see Example.java");
		}
		
		if (!haveInternet(this))
		{
				AlertDialog alertDialog = new AlertDialog.Builder(Container.this).create();
				alertDialog.setMessage("This app requires the use of the internet to function properly. Enable it now?");
				alertDialog.setCancelable(true);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
						startActivity(intent);
						finish();
					}
				});
				alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						finish();
					}
				});
				alertDialog.show();
		}
		else
		{ 

			globalVar = ((GlobalVariable) getApplicationContext());
			twitBtn = globalVar.getTwitBtn();

			facebook = globalVar.getFBState();

			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			fblogout = login.getBoolean("FacebookLoggedOut", true);

			mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key, CONTAINER);
			mTwitter.setListener(mTwLoginDialogListener);
			globalVar.setTwitState(mTwitter);

			mProgress = new ProgressDialog(this);

			Log.d("FB session in container: ", Boolean.toString(facebook.isSessionValid()));

			try
			{
				backupDatabase();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!fblogout)// || SessionStore.restore(facebook, this))// facebook.isSessionValid())
			{
				Log.d("in Container, fb:", Boolean.toString(SessionStore.restore(facebook,this)));
				fbConnect = new FbConnect(APP_ID, this, getApplicationContext());
				TYPE = INIT_FB;
			}

			else if (twitBtn || mTwitter.hasAccessToken())
			{
				TYPE = INIT_TWIT;
				if (mTwitter.hasAccessToken())
				{
				}
				else
				{
					globalVar.setTwitBtn(false);
					mTwitter.authorize();
				}
			}
			else
			{
				SharedPreferences userDetails = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
				String Uname = userDetails.getString("emailLogin", "");
				String pass = userDetails.getString("pwLogin", "");
				Log.d("Uname: ", Uname);
				Log.d("Password: ", pass);
				if (Uname == "" && pass.equals(""))
				{
					Intent launchLogin = new Intent(this, LoginPage.class);
					startActivity(launchLogin);
				}
				else
				{
					ConnectDB connectCheck = new ConnectDB(Uname, pass, 1);
					if (connectCheck.inputResult())
					{
						TYPE = INIT_NORM;
					}
					else
					{
						Log.d("Authenticate User: ", "False");
						showDialog(DIALOG_ERR_LOGIN);
					}
				}
			}

			// Create an Intent to launch an Activity for the tab (to be reused)
			intent = new Intent().setClass(this, TabGroup1Activity.class);

			// Initialize a TabSpec for each tab and add it to the TabHost
			spec = tabHost.newTabSpec("browse").setIndicator("Browse", res.getDrawable(R.drawable.browsebutton)).setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			tabHost.addTab(spec);

			tabHost.setCurrentTab(0);
			setTabColor(tabHost);
			tabHost.setOnTabChangedListener(new OnTabChangeListener(){
				@Override
				public void onTabChanged(String tabId) {
					setTabColor(tabHost);
				}});
			
		}
	}
	
	public static void setTabColor(TabHost tabhost) {
	    for(int i=0;i<tabhost.getTabWidget().getChildCount();i++)
	    {
	    	TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
	    	tv.setTextColor(Color.WHITE);
	        tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_bg); //unselected
	    }
	    TextView tv = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(android.R.id.title);
	    tv.setTextColor(Color.BLACK);
	}
	
	private void KillProcess()
	{
		// TODO Auto-generated method stub
		this.finish();
	}

	public static boolean haveInternet(Context ctx)
	{
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		if (info == null || !info.isConnected())
		{
			return false;
		}
		if (info.isRoaming())
		{
			// here is the roaming option you can change it if you want to
			// disable Internet while roaming, just return false
			return false;
		}
		return true;
	}

	public class FbConnect
	{

		private final String[] FACEBOOK_PERMISSION =
		{ "user_birthday", "email", "publish_stream", "read_stream", "offline_access" };

		private Context context;
		private Activity activity;
		private Facebook facebook;
		GlobalVariable FbState = ((GlobalVariable) getApplicationContext());

		private SharedPreferences sharedPref;
		private Editor editor;

		public FbConnect(String appId, Activity activity, Context context)
		{

			this.context = context;
			this.activity = activity;

			sharedPref = context.getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);

			editor = sharedPref.edit();
			globalVar = ((GlobalVariable) getApplicationContext());

			facebook = FbState.getFBState();

			Log.d("In FBConnect, fb session: ", Boolean.toString(facebook.isSessionValid()));
			if(!facebook.isSessionValid() && fblogout)
			{
				Intent launchLogin = new Intent(Container.this, LoginPage.class);
				startActivity(launchLogin);
			}
			else
			{
			login();
			}
		}

		public void login()
		{
			if (!facebook.isSessionValid())
			{
				facebook.authorize(activity, FACEBOOK_PERMISSION, new LoginDialogListener());
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
				SessionEvents.onLoginSuccess();

				mProgress.setMessage("Finalizing ...");
				mProgress.show();
				AsyncFacebookRunner syncRunner = new AsyncFacebookRunner(facebook);
				syncRunner.request("me", new LoginRequestListener());
			}

			public void onFacebookError(FacebookError error)
			{
				SessionEvents.onLoginError(error.getMessage());
			}

			public void onError(DialogError error)
			{
				SessionEvents.onLoginError(error.getMessage());
			}

			public void onCancel()
			{
				SessionEvents.onLoginError("Action Canceled");
				SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
				SharedPreferences.Editor editor = login.edit();
				editor.putBoolean("FacebookLoggedOut", true);
				editor.commit();
				Intent intent = new Intent(context, LoginPage.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
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
					genderS = json.getString("gender");
					bdayS = json.getString("birthday");
					uid = json.getString("id");
					
					editor.putString("userFBname", userName);
					editor.putString("userFBID", uid);
					editor.putBoolean("FacebookLoggedOut", false);
					editor.commit();
					Log.d("Facebook", fnameS);
					mHandler.post(new Runnable()
					{
						public void run()
						{
							ConnectDB connectCheck;
							try
							{
								connectCheck = new ConnectDB(userName, userEmail, uid, "", "user_fb", CONTAINER, Container.this);
								Log.d("username in container: ", connectCheck.getUserName());

								editor.putString("userName", connectCheck.getUserName());
								editor.putString("emailFB_Login", connectCheck.getUserEmail());
								editor.putString("userDB_FBID", connectCheck.getUserFbTwNmID());
								editor.putString("userID", connectCheck.getUserID());
								editor.commit();
								mProgress.dismiss();
								backupDatabase();
								Log.d("Facebook session 2: ", Boolean.toString(facebook.isSessionValid()));
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
							catch (IOException e)
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

	protected AlertDialog onCreateDialog(int id)
	{
		AlertDialog alertDialog;
		// do the work to define the error Dialog
		alertDialog = new AlertDialog.Builder(Container.this).create();
		alertDialog.setTitle("Login Error");

		switch (id)
		{
		case DIALOG_ERR_LOGIN:
			alertDialog.setMessage("Could not authenticate you. Perhaps your details were not saved. Please login again.");
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
				Intent launchLogin = new Intent(Container.this, LoginPage.class);
				startActivity(launchLogin);

			}
		});
		return alertDialog;
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
			// Return to the login activity
			Intent intent = new Intent(Container.this, LoginPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	};

	public class LogoutRequestListener extends BaseRequestListener
	{
		public void onComplete(String response, final Object state)
		{
			mHandler.post(new Runnable()
			{
				public void run()
				{
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}

	public static void backupDatabase() throws IOException
	{
		/*
		 * //Open your shared_prefs as the input stream String inFileName =
		 * "/dbdata/databases/com.ntu.dealsinterest/shared_prefs/com.ntu.fypshop.xml"
		 * ; File sharedPFile = new File(inFileName); FileInputStream fis = new
		 * FileInputStream(sharedPFile);
		 * 
		 * String inFileName2 =
		 * "/dbdata/databases/com.ntu.dealsinterest/shared_prefs/facebook-session.xml"
		 * ; File sharedPFile2 = new File(inFileName2); FileInputStream fis2 =
		 * new FileInputStream(sharedPFile2);
		 * 
		 * String outFileName =
		 * Environment.getExternalStorageDirectory()+"/com.ntu.fypshop.xml";
		 * String outFileName2 =
		 * Environment.getExternalStorageDirectory()+"/facebook-session.xml";
		 * //Open the empty db as the output stream OutputStream output = new
		 * FileOutputStream(outFileName); OutputStream output2 = new
		 * FileOutputStream(outFileName2); //transfer bytes from the inputfile
		 * to the outputfile byte[] buffer = new byte[1024]; int length; while
		 * ((length = fis.read(buffer))>0){ output.write(buffer, 0, length); }
		 * //Close the streams output.flush(); output.close(); fis.close();
		 * output2.flush(); output2.close(); fis2.close();
		 */
	}
}
