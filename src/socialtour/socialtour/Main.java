package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
//import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;
import com.fedorvlasov.lazylist.LazyAdapter;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
//import com.google.android.maps.GeoPoint;
import socialtour.socialtour.TwitterApp.TwDialogListener;
import socialtour.socialtour.models.Product;
import socialtour.socialtour.models.Shop;
import socialtour.socialtour.models.TestingClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
//import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
//import android.os.Message;
//import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
//import android.widget.RadioGroup;
//import android.widget.TextView;
import android.widget.Toast;

public class Main extends MapActivity implements OnClickListener
{

	private static final String APP_ID = "222592464462347";
	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";
	// FbConnect fbConnect;

	private static GlobalVariable globalVar;

	// private UserParticulars userS;
	private String fnameS;
	private String lnameS;
	private String nameS;
	// private String emailS;
	private Boolean fbBtn = false, twitBtn;
	private Facebook facebook;

	private TwitterApp mTwitter;
	public static LocationManager locationManager;
	public static GPSLocationListener locationListener;

	public static GeoPoint point = new GeoPoint(1304256, 103832538);

	public int currentState; // can be 1 for latest, 2 for hot, 3 for
								// nearby
	private boolean mapmode = false;

	Handler mHandler = new Handler();
	private ProgressDialog mProgress;

	protected String[] employees;
	protected Integer[] employeesid;

	static final int DIALOG_ERR_LOGIN = 0, INIT_NORM = 0, INIT_FB = 1, INIT_TWIT = 2;

	// private String genderS;
	// private String bdayS;

	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;
	String result = null;

	Product[] arrPro;
	Shop[] shop;
	ImageView latest, nearby, hot, map;// , logout;
	ImageView browse, share, search, settings;
	ListView searchResult;
	LazyAdapter adapter;

	Shop shopresult;
	public static List<Shop> shoplist = new ArrayList<Shop>();

	// static final int DATE_DIALOG_ID = 0;
	// private TextView bday;
	// private Button btn;
	// private EditText fname;

	// private EditText lname;

	private MapView mapView;
	private MapController mapController;
	List<Overlay> listOfOverlays;

	Markers usermarker;
	Markers itemmarker;
	Drawable drawableUser;
	Drawable drawableItem;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		TestingClass.setStartTime();
		super.onCreate(savedInstanceState);

		setContentView(R.layout.browse);

		if (APP_ID == null)
		{
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " + "specified before running this example: see Example.java");
		}

		// Container.btn1.setText("Latest");
		// Container.btn2.setText("Hot");
		// Container.btn3.setText("Nearby");

		latest = Container.btn1;
		nearby = Container.btn3;
		hot = Container.btn2;
		map = Container.map;

		browse = Container.browse;
		share = Container.share;
		search = Container.search;
		settings = Container.settings;

		browse.setEnabled(false);
		// logout = (Button) findViewById(R.id.logoutBtn);
		searchResult = (ListView) findViewById(R.id.listBrowse);

		latest.setOnClickListener(this);
		nearby.setOnClickListener(this);
		hot.setOnClickListener(this);
		map.setOnClickListener(this);
		currentState = 1;
		latest.setEnabled(false);

		globalVar = ((GlobalVariable) getApplicationContext());
		// fbBtn = globalVar.getfbBtn();
		twitBtn = globalVar.getTwitBtn();

		facebook = globalVar.getFBState();
		mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key, 4);
		mTwitter.setListener(mTwLoginDialogListener);
		globalVar.setTwitState(mTwitter);

		mProgress = new ProgressDialog(this);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new GPSLocationListener();

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		// Log.d("FbButton: ", fbBtn.toString());
		// SharedPreferences sharedPref =
		// getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);

		if (fbBtn || facebook.isSessionValid())
		{
			// fbConnect = new FbConnect(APP_ID, this,
			// getApplicationContext());
			init(INIT_FB);
		}

		else if (twitBtn || mTwitter.hasAccessToken())
		{
			init(INIT_TWIT);
			// if(mTwitter.hasAccessToken())
			// {
			// // name.setText("Hello " + sharedPref.getString("user_name",
			// "")
			// + ",");
			// }
			// else
			// {
			// globalVar.setTwitBtn(false);
			// mTwitter.authorize();
			// }
		}

		else
		{
			init(INIT_NORM);
		}
		TestingClass.setEndTime();
		Log.d("Main Browsing activity", Long.toString(TestingClass.calculateTime()));

	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.d("in onResume:", "hi");
		latest.setVisibility(View.VISIBLE);
		hot.setVisibility(View.VISIBLE);
		nearby.setVisibility(View.VISIBLE);

		browse.setEnabled(false);
		hot.getLayoutParams().width = 45;
		latest.setImageResource(R.drawable.latestbuttondynamic);
		hot.setImageResource(R.drawable.hotbuttondynamic);
		nearby.setImageResource(R.drawable.nearbybuttondynamic);
		if (currentState == 1)
		{

			Log.d("in onResume:", "current State 1");
			if (globalVar.getMapmode() == true)
			{
				getProduct("Latest");
				currentState = 1;
				Log.d("in onResume:", "before intent 1");
				Intent intent = new Intent(getParent(), MapResult.class);
				intent.putExtra("main", true);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("map", intent);
				latest.setEnabled(false);
				nearby.setEnabled(true);
				hot.setEnabled(true);
				map.setEnabled(true);
			}
			else
			{
				latest.setEnabled(false);
				nearby.setEnabled(true);
				hot.setEnabled(true);
			}
		}
		else if (currentState == 2)
		{
			Log.d("in onResume:", "current State 2");

			if (globalVar.getMapmode() == true)
			{
				getProduct("Hot");
				currentState = 2;
				Log.d("in onResume:", "before intent 2");
				Intent intent = new Intent(getParent(), MapResult.class);
				intent.putExtra("main", true);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("map", intent);
				latest.setEnabled(true);
				nearby.setEnabled(true);
				hot.setEnabled(false);
				map.setEnabled(true);
			}
			else
			{
				hot.setEnabled(false);
				nearby.setEnabled(true);
				latest.setEnabled(true);
			}

		}
		else if (currentState == 3)
		{
			Log.d("in onResume:", "current State 3");
			if (CheckEnableGPS())
			{
				if (globalVar.getMapmode() == true)
				{
					getProduct("Nearby");
					currentState = 3;
					Log.d("in onResume:", "before intent 3");
					Intent intent = new Intent(getParent(), MapResult.class);
					intent.putExtra("main", true);
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("map", intent);
					latest.setEnabled(true);
					nearby.setEnabled(false);
					hot.setEnabled(true);
					map.setEnabled(true);
				}
				else
				{
					nearby.setEnabled(false);
					latest.setEnabled(true);
					hot.setEnabled(true);
				}
			}
			else
			{
				AlertDialog alertDialog = new AlertDialog.Builder(getParent()).create();
				alertDialog.setMessage("Please turn on your GPS");
				alertDialog.setCancelable(true);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// here you can add functions
						Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
						startActivity(intent);
					}
				});
				alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// here you can add functions
						latest.performClick();
					}
				});
				alertDialog.show();
			}
		}
		share.setEnabled(true);
		search.setEnabled(true);
		settings.setEnabled(true);
		latest.setOnClickListener(this);
		nearby.setOnClickListener(this);
		hot.setOnClickListener(this);
		map.setOnClickListener(this);
		if (globalVar.getCurrentProductType() == 1)
		{
			if (globalVar.getMapmode() == true)
			{
				getProduct("Latest");
				currentState = 1;
				Log.d("in onResume:", "before intent 1");
				Intent intent = new Intent(getParent(), MapResult.class);
				intent.putExtra("main", true);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("map", intent);
				latest.setEnabled(false);
				nearby.setEnabled(true);
				hot.setEnabled(true);
				map.setEnabled(true);
			}
		}
		else if (globalVar.getCurrentProductType() == 2)
		{
			if (globalVar.getMapmode() == true)
			{
				getProduct("Hot");
				currentState = 2;
				Log.d("in onResume:", "before intent 2");
				Intent intent = new Intent(getParent(), MapResult.class);
				intent.putExtra("main", true);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("map", intent);
				latest.setEnabled(true);
				nearby.setEnabled(true);
				hot.setEnabled(false);
				map.setEnabled(true);
			}
		}

		else if (globalVar.getCurrentProductType() == 3)
		{
			if (CheckEnableGPS())
			{
				if (globalVar.getMapmode() == true)
				{
					getProduct("Nearby");
					currentState = 3;
					Log.d("in onResume:", "before intent 3");
					Intent intent = new Intent(getParent(), MapResult.class);
					intent.putExtra("main", true);
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("map", intent);
					latest.setEnabled(true);
					nearby.setEnabled(false);
					hot.setEnabled(true);
					map.setEnabled(true);
				}
			}
			else
			{
				AlertDialog alertDialog = new AlertDialog.Builder(getParent()).create();
				alertDialog.setMessage("Please turn on your GPS");
				alertDialog.setCancelable(true);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// here you can add functions
						Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
						startActivity(intent);
					}
				});
				alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// here you can add functions
						latest.performClick();
					}
				});
				alertDialog.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.dashboard:
			Toast.makeText(getParent(), "You pressed the Dashboard!", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getParent(), Dashboard.class);
			startActivity(intent);
			break;
		case R.id.settings:
			Toast.makeText(getParent(), "You pressed the Settings!", Toast.LENGTH_SHORT).show();
			Intent intent2 = new Intent(getParent(), socialtour.socialtour.Settings.class);
			startActivity(intent2);
			break;

		case R.id.mapviews:
			Toast.makeText(getParent(), "You pressed the Map!", Toast.LENGTH_SHORT).show();
			Intent intent3 = new Intent(getParent(), socialtour.socialtour.MapResult.class);
			intent3.putExtra("main", true);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Map Result", intent3);
			break;
		}
		return true;
	}

	private void init(final int type)
	{
		globalVar = ((GlobalVariable) getApplicationContext());

		getProduct("Latest");
		// TODO Auto-generated method stub
		// logout.setOnClickListener(new View.OnClickListener()
		// {
		// public void onClick(View v)
		// {
		// doLogout(type);
		// }
		// });

		searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id)
			{
				// Intent intent = new Intent(Main.this, Productdetail.class);
				// intent.putExtra("lastproductid", employeesid[pos]);
				// startActivity(intent);
				TestingClass.setStartTime();
				Intent intent = new Intent(getParent(), Productdetail.class);
				intent.putExtra("lastproductid", arrPro[pos].getId());
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("Product Detail", intent);
			}
		});
	}

	// private void doLogout(int type)
	// {
	// if (type == INIT_NORM)
	// {
	// // Logout logic here...
	// globalVar = ((GlobalVariable) getApplicationContext());
	// globalVar.setName("");
	// globalVar.setHashPw("");
	// globalVar.setEm("");
	//
	// SharedPreferences login = getSharedPreferences("com.ntu.fypshop",
	// MODE_PRIVATE);
	// SharedPreferences.Editor editor = login.edit();
	// editor.putString("emailLogin", "");
	// editor.putString("pwLogin", "");
	// editor.commit();
	// }
	// else if (type == INIT_FB)
	// {
	// // Go to LoginPage
	// SharedPreferences login = getSharedPreferences("com.ntu.fypshop",
	// MODE_PRIVATE);
	// SharedPreferences.Editor editor = login.edit();
	// editor.putString("facebookName", "");
	// editor.commit();
	// globalVar = ((GlobalVariable) getApplicationContext());
	// Facebook mFacebook = globalVar.getFBState();
	// globalVar.setfbBtn(false);
	// SessionEvents.onLogoutBegin();
	// AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
	// asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
	// }
	// else
	// {
	// mTwitter.resetAccessToken();
	// globalVar.setTwitBtn(false);
	// }
	//
	// // Return to the login activity
	// Intent intent = new Intent(this, LoginPage.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// startActivity(intent);
	// }

	@Override
	public void onClick(View v)
	{
		if (v == latest)
		{
			
			// if (mapmode == true)
			// {
			// getProduct("Latest");
			// currentState = 1;
			// Intent intent = new Intent(getParent(), MapResult.class);
			// intent.putExtra("main", true);
			// TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			// parentActivity.startChildActivity("map", intent);
			// latest.setEnabled(false);
			// nearby.setEnabled(true);
			// hot.setEnabled(true);
			// map.setEnabled(true);
			// }
			//
			// else
			// {
			getProduct("Latest");
			currentState = 1;
			latest.setEnabled(false);
			nearby.setEnabled(true);
			hot.setEnabled(true);
			map.setEnabled(true);
			if(mapmode == true)
			{
				switchToMapView();
			}
			else
			{
				switchToBrowseView();
			}
			// }
		}
		else if (v == nearby)
		{
			if (CheckEnableGPS())
			{

				// if (mapmode == true)
				// {
				// getProduct("Nearby");
				// currentState = 3;
				// Intent intent = new Intent(getParent(), MapResult.class);
				// intent.putExtra("main", true);
				// TabGroupActivity parentActivity = (TabGroupActivity)
				// getParent();
				// parentActivity.startChildActivity("map", intent);
				// latest.setEnabled(true);
				// nearby.setEnabled(false);
				// hot.setEnabled(true);
				// map.setEnabled(true);
				// }
				// else
				// {
				getProduct("Nearby");
				currentState = 3;
				nearby.setEnabled(false);
				latest.setEnabled(true);
				hot.setEnabled(true);
				map.setEnabled(true);
				
				if(mapmode == true)
				{
					switchToMapView();
				}
				else
				{
					switchToBrowseView();
				}
				// }
			}
			else
			{
				AlertDialog alertDialog = new AlertDialog.Builder(getParent()).create();
				alertDialog.setMessage("Please turn on your GPS");
				alertDialog.setCancelable(true);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// here you can add functions
						Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
						startActivity(intent);
					}
				});
				alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// here you can add functions
						latest.performClick();
					}
				});
				alertDialog.show();
			}
		}
		else if (v == hot)
		{

			// if (mapmode == true)
			// {
			// getProduct("Hot");
			// currentState = 2;
			// Intent intent = new Intent(getParent(), MapResult.class);
			// intent.putExtra("main", true);
			// TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			// parentActivity.startChildActivity("map", intent);
			// latest.setEnabled(true);
			// nearby.setEnabled(true);
			// hot.setEnabled(false);
			// map.setEnabled(true);
			// }
			//
			// else
			// {

			getProduct("Hot");
			currentState = 2;
			hot.setEnabled(false);
			nearby.setEnabled(true);
			latest.setEnabled(true);
			map.setEnabled(true);
			
			if(mapmode == true)
			{
				switchToMapView();				
			}
			else
			{
				switchToBrowseView();	
			}
			// }
		}
		else if (v == map)
		{
			mapmode = !mapmode;

			hot.setEnabled(true);
			nearby.setEnabled(true);
			latest.setEnabled(true);
			map.setEnabled(true);

			if (mapmode == true)
			{
				// if (mapmode == true)
				// {
				// mapmode = false;
				// }
				// else
				// {
				// Intent intent = new Intent(getParent(), MapResult.class);
				// intent.putExtra("main", true);
				// TabGroupActivity parentActivity = (TabGroupActivity)
				// getParent();
				// parentActivity.startChildActivity("map", intent);

				switchToMapView();
			}
			
			else
			{
				switchToBrowseView();
			}
			// }
		}
	}

	private void switchToMapView()
	{
		// TODO Auto-generated method stub
		if(mapView == null)
		{
			mapView = new MapView(this, this.getString(R.string.APIMapKey));
		}
		
//		setContentView(R.layout.mapresult);
//		mapView = (MapView) findViewById(R.id.mapView);
		setContentView(mapView);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(13);
		initStores();
	}

	private void switchToBrowseView()
	{
		// TODO Auto-generated method stub
		setContentView(R.layout.browse);
		searchResult = (ListView) findViewById(R.id.listBrowse);
		if(currentState == 1)
		{
			getProduct("Latest");
			latest.setEnabled(false);
			nearby.setEnabled(true);
			hot.setEnabled(true);
			map.setEnabled(true);
		}
		else if (currentState == 2)
		{
			getProduct("Hot");
			hot.setEnabled(false);
			nearby.setEnabled(true);
			latest.setEnabled(true);
			map.setEnabled(true);					
		}
		else if (currentState == 3)
		{
			if (CheckEnableGPS())
			{
				
					getProduct("Nearby");
					latest.setEnabled(true);
					nearby.setEnabled(false);
					hot.setEnabled(true);
					map.setEnabled(true);
			}
			else
			{
				AlertDialog alertDialog = new AlertDialog.Builder(getParent()).create();
				alertDialog.setMessage("Please turn on your GPS");
				alertDialog.setCancelable(true);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// here you can add functions
						Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
						startActivity(intent);
					}
				});
				alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// here you can add functions
						latest.performClick();
					}
				});
				alertDialog.show();
			}					
		}
	}

	private void initStores()
	{
		// TODO Auto-generated method stub
		listOfOverlays = mapView.getOverlays();
		for (Overlay overlay : listOfOverlays)
		{
			if (overlay instanceof BalloonItemizedOverlay<?>)
			{
				if (((BalloonItemizedOverlay<?>) overlay).balloonView != null)
					((BalloonItemizedOverlay<?>) overlay).balloonView.setVisibility(View.GONE);
			}
		}
		listOfOverlays.clear();
		// drawableUser = getResources().getDrawable(R.drawable.location);
		// usermarker = new Markers(drawableUser, mapView);

		// if (location != null)
		// {

		GeoPoint point = new GeoPoint((int) (Double.parseDouble(shoplist.get(0).getLat()) * 1E6), (int) (Double.parseDouble(shoplist.get(0).getLng()) * 1E6));

		mapController.animateTo(point);
		mapController.setZoom(13);

		listOfOverlays.clear();
		searchStores();

		mapView.invalidate();
	}

	private void searchStores()
	{
		// TODO Auto-generated method stub
		drawableItem = getResources().getDrawable(R.drawable.pushpin);
		itemmarker = new Markers(drawableItem, mapView);
		for (int sp = 0; sp < shoplist.size(); sp++)
		{
			GeoPoint p = new GeoPoint((int) (Double.parseDouble(shoplist.get(sp).getLat()) * 1E6), (int) (Double.parseDouble(shoplist.get(sp).getLng()) * 1E6));

			OverlayItem item = new OverlayItem(p, shoplist.get(sp).getName(), shoplist.get(sp).getAddress());
			itemmarker.addOverlay(item);

		}
		listOfOverlays.add(itemmarker);
	}

	private Boolean CheckEnableGPS()
	{
		// TODO Auto-generated method stub
		String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("network") || provider.contains("gps"))
		{
			// GPS Enabled
			// Toast.makeText(AndroidEnableGPS.this, "GPS Enabled: " + provider,
			// Toast.LENGTH_LONG).show();
			return true;
		}
		else
		{
			return false;
		}
	}

	public void getProduct(String mode)
	{
		searchResult.setAdapter(null);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (mode.equals("Latest"))
		{
			String lat = Double.toString(point.getLatitudeE6() / 1E6);
			String lng = Double.toString(point.getLongitudeE6() / 1E6);
			nameValuePairs.add(new BasicNameValuePair("latest", "1"));
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lng", lng));
		}
		else if (mode.equals("Nearby"))
		{
			String lat = Double.toString(point.getLatitudeE6() / 1E6);
			String lng = Double.toString(point.getLongitudeE6() / 1E6);
			String radius = "1000";
			nameValuePairs.add(new BasicNameValuePair("nearby", "1"));
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lng", lng));
			nameValuePairs.add(new BasicNameValuePair("radius", radius));

		}
		else if (mode.equals("Hot"))
		{
			String lat = Double.toString(point.getLatitudeE6() / 1E6);
			String lng = Double.toString(point.getLongitudeE6() / 1E6);
			nameValuePairs.add(new BasicNameValuePair("hot", "1"));
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lng", lng));
		}
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "browse.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response != null)
			{
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}
		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error in http connection" + e.toString());
		}
		// convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = "0";
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.d("result: ", result);
		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		// paring data
		int ct_id;
		String ct_name;
		shoplist.clear();
		try
		{
			jArray = new JSONArray(result);
			JSONObject json_data = null;
			arrPro = new Product[jArray.length()];
			shop = new Shop[jArray.length()];
			employees = new String[jArray.length()];
			employeesid = new Integer[jArray.length()];
			for (int i = 0; i < jArray.length(); i++)
			{
				json_data = jArray.getJSONObject(i);
				arrPro[i] = new Product();
				shop[i] = new Shop();
				arrPro[i].setUser_name(json_data.getString("user_name"));

				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				arrPro[i].setCreated((Date) formatter.parse(json_data.getString("created")));

				JSONArray jextraArr = new JSONArray(json_data.getString("extra_fields"));
				JSONObject jextraObjPercent = jextraArr.getJSONObject(3);
				String discountPercent = jextraObjPercent.getString("value");
				JSONObject jextraObjPrice = jextraArr.getJSONObject(4);
				String discountPrice = jextraObjPrice.getString("value");

				arrPro[i].setDprice(discountPrice);
				arrPro[i].setId(json_data.getInt("prod_id"));
				arrPro[i].setFilename(json_data.getString("title"));
				arrPro[i].setUrl("");// json_data.getString("url"));
				arrPro[i].setPercentdiscount(discountPercent);
				arrPro[i].setCategory(json_data.getString("category"));
				arrPro[i].setLikes(json_data.getInt("likes"));
				arrPro[i].setRemarks(json_data.getInt("remarks"));
				shop[i].setName(json_data.getString("shop_name"));
				shopresult = new Shop(json_data.getInt("shop_id"), json_data.getString("address"), json_data.getString("shop_name"), json_data.getString("lat"), json_data.getString("lng"));// ,
																																																// json_data.getString("shoptype"));
				shoplist.add(shopresult);
				shop[i].setDistance(Double.toString(json_data.getDouble("distance")));
				// employees[i] = ct_name;
				// employeesid[i] = ct_id;
			}
			adapter = new LazyAdapter(this, arrPro, shop);
			// ListAdapter adapter = new ArrayAdapter<String>(this,
			// android.R.layout.simple_list_item_1, employees);
			searchResult.setAdapter(adapter);
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "No products Found", Toast.LENGTH_SHORT).show();
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}
		catch (java.text.ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			Intent intent = new Intent(Main.this, LoginPage.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	};

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle item selection
	// switch (item.getItemId()) {
	// case R.id.clothes:
	// showResults("Clothes");
	// return true;
	// case R.id.others:
	// showResults("Others");
	// return true;
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }

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

	private class GPSLocationListener implements LocationListener
	{

		@Override
		public void onLocationChanged(Location location)
		{
			if (location != null)
			{
				point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
				// add marker
				// MapOverlay mapOverlay = new MapOverlay(MY_POINT);
				// mapOverlay.setPointToDraw(point);
				// listOfOverlays = mapView.getOverlays();
				// listOfOverlays.clear();
				// listOfOverlays.add(mapOverlay);

				// String address = ConvertPointToLocation(point);
				// Log.d("Address: ", address);

				// Drawable drawable =
				// getResources().getDrawable(R.drawable.red);

				// searchStores(point);

				// mapView.invalidate();
			}
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			// TODO Auto-generated method stub
			String omg = "";
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public class Markers extends BalloonItemizedOverlay<OverlayItem>
	{

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		public Markers(Drawable defaultMarker, MapView mv)
		{

			super(boundCenter(defaultMarker), mv, globalVar);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			// TODO Auto-generated method stub
			return mOverlays.get(i);
		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView)
		{
			// TODO Auto-generated method stub
			return super.onTap(p, mapView);
		}

		@Override
		public int size()
		{
			// TODO Auto-generated method stub
			return mOverlays.size();
		}

		public void addOverlay(OverlayItem item)
		{
			mOverlays.add(item);
			// setLastFocusedIndex(-1);
			populate();

		}

		public void clear()
		{
			mOverlays.clear();
			// setLastFocusedIndex(-1);
			populate();
		}

		@Override
		public boolean onBalloonTap(int index, OverlayItem item)
		{
			Intent myintent = new Intent(getParent(), Shopdetail.class);
			myintent.putExtra("shopid", shoplist.get(index).getId());
			myintent.putExtra("shopname", shoplist.get(index).getName());
			myintent.putExtra("shopaddress", shoplist.get(index).getAddress());
			// int icon = shoplist.get(index).getIcon();
			// myintent.putExtra("icon", icon);
			myintent.putExtra("lat", shoplist.get(index).getLat());
			myintent.putExtra("long", shoplist.get(index).getLng());
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Shop Detail", myintent);
			return (super.onBalloonTap(index, item));
		}
	}
}
