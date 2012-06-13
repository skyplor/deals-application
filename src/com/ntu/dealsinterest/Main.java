package com.ntu.dealsinterest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

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

import com.fedorvlasov.lazylist.LazyAdapter;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.ntu.dealsinterest.models.Product;
import com.ntu.dealsinterest.models.Shop;
import com.ntu.dealsinterest.models.TestingClass;

import com.ntu.dealsinterest.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Main extends MapActivity implements OnClickListener
{

	public static DisplayMetrics metrics = new DisplayMetrics();
	private static GlobalVariable globalVar;
	
	public static LocationManager locationManager;
	public static GPSLocationListener locationListener;

	public static GeoPoint point = new GeoPoint(1304256, 103832538);

	public int currentState; // can be 1 for latest, 2 for hot, 3 for
								// nearby
	private boolean mapmode = false;

	Handler mHandler = new Handler();	

	protected String[] employees;
	protected Integer[] employeesid;

	static final int DIALOG_ERR_LOGIN = 0, INIT_NORM = 0, INIT_FB = 1, INIT_TWIT = 2;

	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;
	String result = null;

	Product[] arrPro;
	Shop[] shop;
	ImageView latest, nearby, hot, map;
	ImageView browse, share, search, settings;
	ListView searchResult;
	LazyAdapter adapter;
	RelativeLayout mapHolder;
	ProgressDialog progress;
	Shop shopresult;
	public static List<Shop> shoplist = new ArrayList<Shop>();

	private static MapView mapView;
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

		latest = Container.btn1;
		nearby = Container.btn3;
		hot = Container.btn2;
		map = Container.map;

		browse = Container.browse;
		share = Container.share;
		search = Container.search;
		settings = Container.settings;

		browse.setEnabled(false);
		searchResult = (ListView) findViewById(R.id.listBrowse);
		
		mapView = null;

		latest.setOnClickListener(this);
		nearby.setOnClickListener(this);
		hot.setOnClickListener(this);
		map.setOnClickListener(this);
		
		currentState = 1;
		latest.setEnabled(false);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new GPSLocationListener();

		globalVar = ((GlobalVariable) getApplicationContext()); 
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


		init();
		TestingClass.setEndTime();
		Log.d("Main Browsing activity", Long.toString(TestingClass.calculateTime()));

	}

	@Override
	public void onResume()
	{
		super.onResume();

		latest.setVisibility(View.VISIBLE);
		hot.setVisibility(View.VISIBLE);
		nearby.setVisibility(View.VISIBLE);
		map.setVisibility(View.VISIBLE);
		map.setImageResource(R.drawable.pin);
		browse.setEnabled(false);
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int type = metrics.densityDpi;
		if (type > metrics.DENSITY_HIGH){
			hot.getLayoutParams().width = 90;
		}else{
			hot.getLayoutParams().width = 65;
		}
		
		latest.setImageResource(R.drawable.latestbuttondynamic);
		hot.setImageResource(R.drawable.hotbuttondynamic);
		nearby.setImageResource(R.drawable.nearbybuttondynamic);

		if (currentState == 1)
		{
			latest.setEnabled(false);
			nearby.setEnabled(true);
			hot.setEnabled(true);
		}
		else if (currentState == 2)
		{
			hot.setEnabled(false);
			nearby.setEnabled(true);
			latest.setEnabled(true);
		}
		else if (currentState == 3)
		{

			if (CheckEnableGPS())
			{
				nearby.setEnabled(false);
				latest.setEnabled(true);
				hot.setEnabled(true);
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
			Intent intent2 = new Intent(getParent(), com.ntu.dealsinterest.Settings.class);
			startActivity(intent2);
			break;

		case R.id.mapviews:
			Toast.makeText(getParent(), "You pressed the Map!", Toast.LENGTH_SHORT).show();
			Intent intent3 = new Intent(getParent(), com.ntu.dealsinterest.MapResult.class);
			intent3.putExtra("main", true);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Map Result" + TabGroup1Activity.intentCount, intent3);
			TabGroup1Activity.intentCount++;
			break;
		}
		return true;
	}

	private void init()
	{
		globalVar = ((GlobalVariable) getApplicationContext()); 
		
		getProduct("Latest");
		// TODO Auto-generated method stub

		
	}

	@Override
	public void onClick(View v)
	{
		if (v == latest)
		{			
			getProduct("Latest");
			currentState = 1;
			latest.setEnabled(false);
			nearby.setEnabled(true);
			hot.setEnabled(true);
			if (mapmode == true)
			{
				map.setImageResource(R.drawable.pin2);
				switchToMapView();
			}
			else
			{
				map.setImageResource(R.drawable.pin);
				switchToBrowseView();
			}
		}
		else if (v == nearby)
		{
			if (CheckEnableGPS())
			{

				runDialog(2);
				getProduct("Nearby");
				currentState = 3;
				nearby.setEnabled(false);
				latest.setEnabled(true);
				hot.setEnabled(true);

				if (mapmode == true)
				{
					map.setImageResource(R.drawable.pin2);
					switchToMapView();
				}
				else
				{
					map.setImageResource(R.drawable.pin);
					switchToBrowseView();
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
		else if (v == hot)
		{
			runDialog(2);
			getProduct("Hot");
			currentState = 2;
			hot.setEnabled(false);
			nearby.setEnabled(true);
			latest.setEnabled(true);

			if (mapmode == true)
			{
				map.setImageResource(R.drawable.pin2);
				switchToMapView();
			}
			else
			{
				map.setImageResource(R.drawable.pin);
				switchToBrowseView();
			}
		}
		else if (v == map)
		{
			mapmode = !mapmode;
			if (currentState == 1)
			{
				latest.setEnabled(false);
				hot.setEnabled(true);
				nearby.setEnabled(true);
			}
			if (currentState == 2)
			{
				hot.setEnabled(false);
				latest.setEnabled(true);
				nearby.setEnabled(true);
			}
			if (currentState == 3)
			{
				nearby.setEnabled(false);
				latest.setEnabled(true);
				hot.setEnabled(true);
			}

			if (mapmode == true)
			{
				map.setImageResource(R.drawable.pin2);
				switchToMapView();
			}

			else
			{
				map.setImageResource(R.drawable.pin);
				switchToBrowseView();
			}
		}
	}

	private void switchToMapView()
	{
		// TODO Auto-generated method stub
		if (mapHolder != null)
		{
			Log.d("inswitchToMapView: ", "mapHolder is not null");
			mapHolder.removeView(mapView);
		}

		setContentView(R.layout.mapbrowse);

		if (mapView == null)
		{
			mapView = new MapView(this, this.getString(R.string.APIMapKey));

		}

		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(13);
		initStores();
		mapHolder = (RelativeLayout) findViewById(R.id.mapbrowseRelative1);
		mapHolder.addView(mapView);
	}

	private void switchToBrowseView()
	{
		// TODO Auto-generated method stub
		setContentView(R.layout.browse);
		searchResult = (ListView) findViewById(R.id.listBrowse);
		if (currentState == 1)
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

		if (!shoplist.isEmpty())
		{
			GeoPoint point = new GeoPoint((int) (Double.parseDouble(shoplist.get(0).getLat()) * 1E6), (int) (Double.parseDouble(shoplist.get(0).getLng()) * 1E6));

			mapController.animateTo(point);
		}
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
			String radius = "2000";
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
				arrPro[i].setUrl("");
				arrPro[i].setPercentdiscount(discountPercent);
				arrPro[i].setCategory(json_data.getString("category"));
				arrPro[i].setLikes(json_data.getInt("likes"));
				arrPro[i].setRemarks(json_data.getInt("remarks"));
				shop[i].setName(json_data.getString("shop_name"));
				shopresult = new Shop(json_data.getInt("shop_id"), json_data.getString("address"), json_data.getString("shop_name"), json_data.getString("lat"), json_data.getString("lng"));// ,
																																																// json_data.getString("shoptype"));
				shoplist.add(shopresult);
				shop[i].setDistance(Double.toString(json_data.getDouble("distance")));
			}
			adapter = new LazyAdapter(this, arrPro, shop);
			searchResult.setAdapter(adapter);
			
			searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> av, View v, int pos, long id)
				{
					runDialog(2);
					TestingClass.setStartTime();
					Intent intent = new Intent(getParent(), Productdetail.class);
					intent.putExtra("lastproductid", arrPro[pos].getId());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("Product Detail "+ TabGroup1Activity.intentCount, intent);
					TabGroup1Activity.intentCount++;
				}
			});
		}
		catch (JSONException e1)
		{
			setContentView(R.layout.noproducts);
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
	
	private void runDialog(final int seconds)
	{
	    	progress = ProgressDialog.show(getParent(), "", "Loading Product");

	    	new Thread(new Runnable(){
	    		public void run(){
	    			try {
				                Thread.sleep(seconds * 1000);
						progress.dismiss();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    	}).start();
	}

	private class GPSLocationListener implements LocationListener
	{

		@Override
		public void onLocationChanged(Location location)
		{
			if (location != null)
			{
				point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
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
			populate();

		}

		public void clear()
		{
			mOverlays.clear();
			populate();
		}

		@Override
		public boolean onBalloonTap(int index, OverlayItem item)
		{
			Intent myintent = new Intent(getParent(), Shopdetail.class);
			myintent.putExtra("shopid", shoplist.get(index).getId());
			myintent.putExtra("shopname", shoplist.get(index).getName());
			myintent.putExtra("shopaddress", shoplist.get(index).getAddress());
			myintent.putExtra("lat", shoplist.get(index).getLat());
			myintent.putExtra("long", shoplist.get(index).getLng());
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Shop Detail " + TabGroup1Activity.intentCount, myintent);
			TabGroup1Activity.intentCount++;
			return (super.onBalloonTap(index, item));
		}
	}
}
