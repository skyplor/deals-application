package socialtour.socialtour;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import socialtour.socialtour.TwitterApp.TwDialogListener;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

//import android.widget.TabHost.OnTabChangeListener;

@SuppressWarnings("deprecation")
public class Container extends TabActivity
{

	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int GALLERY_REQUEST = 1500;
	private static String _path = "";
	Uri outputFileUri;

	static final int DIALOG_ERR_LOGIN = 0, INIT_NORM = 0, INIT_FB = 1, INIT_TWIT = 2;
	static int TYPE = 0;
	private static final String APP_ID = "222592464462347";
	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";
	FbConnect fbConnect;
	private static GlobalVariable globalVar;

	private ProgressDialog mProgress;
	Handler mHandler = new Handler();

	private Boolean fbBtn, twitBtn;
	private Facebook facebook;
	private TwitterApp mTwitter;

	private String fnameS;
	private String lnameS;
	private String userName;
	private String userEmail;

	Button logout;

	Intent intent; // Reusable Intent for each tab
	Resources res; // Resource object to get Drawables
	TabHost tabHost; // The activity TabHost
	TabSpec spec; // Resusable TabSpec for each tab

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container);

		// tabHost = (TabHost) findViewById(R.id.tabhost);
		res = getResources(); // Resource object to get Drawables
		tabHost = getTabHost(); // The activity TabHost

		if (APP_ID == null)
		{
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see Example.java");
		}

		if (!haveInternet(this))
		{
			AlertDialog alertDialog = new AlertDialog.Builder(Container.this).create();
			alertDialog.setMessage("Unable to connect to the Internet.");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					KillProcess();
				}

			});

			alertDialog.show();
		}
		else
		{
			logout = (Button) findViewById(R.id.logoutBtn1);

			/*
			 * tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			 * 
			 * @Override public void onTabChanged(String arg0) {
			 * 
			 * } });
			 */

			globalVar = ((GlobalVariable) getApplicationContext());
			fbBtn = globalVar.getfbBtn();
			twitBtn = globalVar.getTwitBtn();

			facebook = globalVar.getFBState();
			mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key);
			mTwitter.setListener(mTwLoginDialogListener);
			globalVar.setTwitState(mTwitter);

			mProgress = new ProgressDialog(this);

			Log.d("FbButton: ", fbBtn.toString());

			if (fbBtn || facebook.isSessionValid())
			{
				fbConnect = new FbConnect(APP_ID, this, getApplicationContext());
				TYPE = INIT_FB;
				init(TYPE);
			}

			else if (twitBtn || mTwitter.hasAccessToken())
			{
				TYPE = INIT_TWIT;
				init(TYPE);
				if (mTwitter.hasAccessToken())
				{
					// name.setText("Hello " + sharedPref.getString("user_name",
					// "")
					// + ",");
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
						// name.setText("Hello " + connectCheck.getName() +
						// ",");
						TYPE = INIT_NORM;
						init(TYPE);
					}
					else
					{
						// do something else
						Log.d("Authenticate User: ", "False");
						showDialog(DIALOG_ERR_LOGIN);
					}
				}
			}

			// Create an Intent to launch an Activity for the tab (to be reused)
			intent = new Intent().setClass(this, TabGroup1Activity.class);

			// Initialize a TabSpec for each tab and add it to the TabHost
			spec = tabHost.newTabSpec("home").setIndicator("Browse", res.getDrawable(R.drawable.ic_tab_artists)).setContent(intent);
			tabHost.addTab(spec);

			// Do the same for the other tabs
			intent = new Intent().setClass(this, TabGroup2Activity.class);
			spec = tabHost.newTabSpec("attraction").setIndicator("Share", res.getDrawable(R.drawable.ic_tab_artists)).setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent().setClass(this, TabGroup3Activity.class);
			spec = tabHost.newTabSpec("checkin").setIndicator("Search", res.getDrawable(R.drawable.ic_tab_artists)).setContent(intent);
			tabHost.addTab(spec);

			tabHost.setCurrentTab(0);

			tabHost.setOnTabChangedListener(new OnTabChangeListener()
			{
				@Override
				public void onTabChanged(String arg0)
				{
					int index = tabHost.getCurrentTab();
					if (index == 1)
					{
						openAddPhoto();
						// Activity current =
						// getLocalActivityManager().getCurrentActivity();
						// current.finish();
						// Intent i = new
						// Intent("socialtour.socialtour.STARTCAMERA");
						// startActivity(i);
					}

					// TabGroupActivity parentActivity =
					// (TabGroupActivity)getParent();
					// parentActivity.
				}
			});
		}
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

	private void init(final int type)
	{

		logout.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				doLogout(type);
			}
		});
	}

	private void doLogout(int type)
	{
		if (type == INIT_NORM)
		{
			// Logout logic here...
			globalVar = ((GlobalVariable) getApplicationContext());
			globalVar.setName("");
			globalVar.setHashPw("");
			globalVar.setEm("");

			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			SharedPreferences.Editor editor = login.edit();
			editor.putString("userName", null);
			editor.putString("userID", null);
			editor.putString("emailLogin", null);
			editor.putString("pwLogin", null);
			editor.commit();
		}
		else if (type == INIT_FB)
		{
			// Go to LoginPage
			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			SharedPreferences.Editor editor = login.edit();
			editor.putString("userName", null);
			editor.putString("userID", null);
			editor.putString("emailLogin", null);
			editor.commit();
			globalVar = ((GlobalVariable) getApplicationContext());
			Facebook mFacebook = globalVar.getFBState();
			globalVar.setfbBtn(false);
			SessionEvents.onLogoutBegin();
			AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
			asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
		}
		else
		{
			mTwitter.resetAccessToken();
			globalVar.setTwitBtn(false);
		}

		// Return to the login activity
		Intent intent = new Intent(this, LoginPage.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	// @Override
	// protected void onResume()
	// {
	// super.onResume();
	//
	// setContentView(R.layout.container);
	//
	// res = getResources();
	// tabHost = getTabHost();
	// init(TYPE);
	// }

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { super.onActivityResult(requestCode,
	 * resultCode, data); // if (requestCode != 0) // {
	 * fbConnect.getFacebook().authorizeCallback(requestCode, resultCode, data);
	 * // } }
	 */

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
					Log.d("Facebook-Example", "Response: " + response.toString());
					JSONObject json = Util.parseJson(response);
					fnameS = json.getString("first_name");
					lnameS = json.getString("last_name");
					userName = fnameS + " " + lnameS;
					userEmail = json.getString("email");
					// genderS = json.getString("gender");
					// bdayS = json.getString("birthday");
					Log.d("Facebook", fnameS);
					// userS = new UserParticulars(fnameS, lnameS, emailS,
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
								editor.putString("emailLogin", connectCheck.getUserEmail());
								editor.putString("userID", connectCheck.getUserID());
								editor.commit();

								editor.commit();
								// name.setText("Hello " + nameS + ",");
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
							// globalVar = ((GlobalVariable)
							// getApplicationContext());
							// globalVar.setName(nameS);

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

	protected void startCameraActivity()
	{
		_path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
		// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// File file = new File(_path, "test.jpg");
		// Uri outputFileUri = Uri.fromFile(file);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		// startActivityForResult(intent, 0);

		// Log.i("MakeMachine", "startCameraActivity()" );
		File file = new File(_path, "testing.jpg");

		try
		{
			if (file.exists() == false)
			{
				file.createNewFile();
			}

		}
		catch (IOException e)
		{}

		outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);

		// Uri outputFileUri = Uri.fromFile( file );

		// Intent intent = new
		// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
		// intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

		startActivityForResult(intent, CAMERA_PIC_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAMERA_PIC_REQUEST)
		{
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 2;

			// Bitmap bitmap = BitmapFactory.decodeFile( _path + "test.jpg",
			// options );
			// _image = (ImageView) findViewById(R.id.imageView2);
			// _image.setImageBitmap(bitmap);
			// Bundle bundle=data.getExtras();
			// Bitmap pic = (Bitmap) bundle.get("pic");
			// Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			_path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
			File file = new File(_path, "testing.jpg");
			outputFileUri = Uri.fromFile(file);
			Bundle bundle = getIntent().getExtras();
			if (bundle == null)
			{
				/*
				 * Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
				 * i.putExtra("pic", outputFileUri); startActivity(i);
				 */

				Intent i = new Intent(this, TabGroup2Activity.class);
				i.putExtra("pic", outputFileUri);
				// TabGroupActivity parentActivity =
				// (TabGroupActivity)getParent();
				startActivity(i);
			}
			else
			{
				/*
				 * Intent i = new Intent("socialtour.socialtour.ATTRACTION");
				 * i.putExtra("pic", outputFileUri); i.putExtra("EMPLOYEE_ID",
				 * getIntent().getIntExtra("EMPLOYEE_NAME",0));
				 * i.putExtra("EMPLOYEE_NAME",
				 * getIntent().getStringExtra("EMPLOYEE_NAME"));
				 * startActivity(i);
				 */

				Intent i = new Intent(this, Attraction.class);
				i.putExtra("pic", outputFileUri);
				i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME", 0));
				i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
				// TabGroupActivity parentActivity =
				// (TabGroupActivity)getParent();
				startActivity(i);
			}

			// _image = (ImageView) findViewById(R.id.imageView2);
			// _image.setImageBitmap(thumbnail);
		}
		else if (requestCode == GALLERY_REQUEST)
		{
			Bundle bundle = getIntent().getExtras();
			if (bundle == null)
			{
				/*
				 * Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
				 * outputFileUri = data.getData(); i.putExtra("pic",
				 * outputFileUri); startActivity(i);
				 */
				Intent i = new Intent(this, TabGroup2Activity.class);
				if (data != null)
				{
					outputFileUri = data.getData();
					i.putExtra("pic", outputFileUri);
				}
				// TabGroupActivity parentActivity =
				// (TabGroupActivity)getParent();
				startActivity(i);
			}
			else
			{
				/*
				 * Intent i = new Intent("socialtour.socialtour.ATTRACTION");
				 * outputFileUri = data.getData(); i.putExtra("pic",
				 * outputFileUri); i.putExtra("EMPLOYEE_ID",
				 * getIntent().getIntExtra("EMPLOYEE_NAME",0));
				 * i.putExtra("EMPLOYEE_NAME",
				 * getIntent().getStringExtra("EMPLOYEE_NAME"));
				 * startActivity(i);
				 */

				Intent i = new Intent(this, Attraction.class);
				outputFileUri = data.getData();
				i.putExtra("pic", outputFileUri);
				i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME", 0));
				i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
				// TabGroupActivity parentActivity =
				// (TabGroupActivity)getParent();
				startActivity(i);
			}

		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
			// if (requestCode != 0)
			// {
			fbConnect.getFacebook().authorizeCallback(requestCode, resultCode, data);
			// }
		}
		/*
		 * Log.i( "MakeMachine", "resultCode: " + resultCode ); switch(
		 * resultCode ) { case 0: Log.i( "MakeMachine", "User cancelled" );
		 * break;
		 * 
		 * case -1: onPhotoTaken(); break; }
		 */
	}

	private void openAddPhoto()
	{

		String[] addPhoto = new String[]
		{ "Camera", "Gallery" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Get your picture from");

		dialog.setItems(addPhoto, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				if (id == 0)
				{
					startCameraActivity();
				}
				if (id == 1)
				{
					startGallery();
				}
			}
		});

		dialog.setNeutralButton("Cancel", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// TabGroupActivity parentActivity =
				// (TabGroupActivity)getParent();
				// Intent i = new Intent(this, Container.class);
				// startActivity(i);
				tabHost.setCurrentTab(0);
			}
		});
		dialog.show();
	}

	protected void startGallery()
	{
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, GALLERY_REQUEST);
	}

}
