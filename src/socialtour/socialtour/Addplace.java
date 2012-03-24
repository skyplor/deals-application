package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import socialtour.socialtour.MapResult.Markers;
import socialtour.socialtour.models.Shop;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Addplace extends MapActivity implements OnClickListener
{

	private MapView mapView;
	private MapController mapController;
	private GeoPoint startingPoint;
	private AutoCompleteTextView mapSearchBox;
	private EditText searchAddress;
	private GlobalVariable globalVar;
	private Button search, btnAdd;
	List<Overlay> listOfOverlays;
	MapOverlay lockScrollOverlay;
	private LockableScrollView scroll;
	private LinearLayout scrollChildLinearLayout;

	List<String> addrList = null;
	List<String> shopDetail;
	HashMap<GeoPoint, List<String>> addrMap;
	String address = "";

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addplace);

		Container.btn1.setVisibility(View.GONE);
		Container.btn2.setVisibility(View.GONE);
		Container.btn3.setVisibility(View.GONE);
		
		mapView = (MapView) findViewById(R.id.addShopMap);
		mapSearchBox = (AutoCompleteTextView) findViewById(R.id.txtShopname);
		searchAddress = (EditText) findViewById(R.id.txtShopaddress);
		search = (Button) findViewById(R.id.searchShopsBtn);
		scroll = (LockableScrollView) findViewById(R.id.ScrollView01);
		scrollChildLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout02);
		
		final ArrayAdapter<String> Arradapter = new ArrayAdapter<String>(this, R.layout.item_list);
		Arradapter.setNotifyOnChange(true);
		mapSearchBox.setAdapter(Arradapter);
		mapSearchBox.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if (count%3 == 1)
				{
					//we don't want to make an insanely large array, so we clear it each time
					Arradapter.clear();
					try
					{
						URL googlePlaces = new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Singapore+"+URLEncoder.encode(s.toString(),"UTF-8")+"&location=1.3667,103.8&radius=50000&sensor=true&key=AIzaSyAxAq7cT-RTcHdT7ccVc1LQCK85J133hsg");
						URLConnection tc = googlePlaces.openConnection();
						Log.d("Gotta go:", URLEncoder.encode(s.toString()));
						BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
						
						String line;
						StringBuffer sb = new StringBuffer();
						while((line = in.readLine())!= null)
						{
							sb.append(line);
						}
						JSONObject predictions = new JSONObject(sb.toString());
						JSONArray ja = new JSONArray(predictions.getString("predictions"));
						
						for (int i=0; i<ja.length(); i++)
						{
							JSONObject jo = (JSONObject) ja.get(i);
							Arradapter.add(jo.getString("description"));
						}
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
			}

			@Override
			public void afterTextChanged(Editable arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub
				
			}
		});

		btnAdd = (Button) findViewById(R.id.uploadProduct);

		btnAdd.setOnClickListener(this);

		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(12);

		startingPoint = new GeoPoint(1303999, 103832731);
		mapController.setCenter(startingPoint);

		lockScrollOverlay = new MapOverlay();
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(lockScrollOverlay);

		init();
	}

	private void init()
	{
		// TODO Auto-generated method stub
		globalVar = ((GlobalVariable) getApplicationContext());

		search.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);
				if (listOfOverlays != null)
				{
					clearBalloon();
					listOfOverlays.clear();
					listOfOverlays.add(lockScrollOverlay);
				}
				if (!doSearch(mapSearchBox.getText().toString().trim(), searchAddress.getText().toString().trim()))
				{
					Toast.makeText(Addplace.this, "Unable to find any result. Please try again.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					listOfOverlays.add(lockScrollOverlay);
					mapController.animateTo(startingPoint);
				}
				// new
				// SearchClicked(mapSearchBox.getText().toString());//.execute();
			}
		});
		mapSearchBox.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Log.d("in OnClickListener", Boolean.toString(onSearchRequested()));

			}
		});
		scrollChildLinearLayout.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					scroll.setIsScrollable(true);
					Log.d("in linearlayout", "true");
					return true;
				}
				return false;
			}

		});

		// mapSearchBox.setOnEditorActionListener(new
		// TextView.OnEditorActionListener()
		// {
		// public boolean onEditorAction(TextView v, int actionId, KeyEvent
		// event)
		// {
		// if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId ==
		// EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||
		// event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() ==
		// KeyEvent.KEYCODE_ENTER)
		// {
		//
		// // hide virtual keyboard
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);
		//
		// new SearchClicked(mapSearchBox.getText().toString()).execute();
		// mapSearchBox.setText("", TextView.BufferType.EDITABLE);
		// return true;
		// }
		// return false;
		// }
		// });
	}

	private class SearchClicked
	{// extends AsyncTask<Void, Void, Boolean> {
		private String toSearch;
		private Address address;

		public SearchClicked(String toSearch)
		{
			this.toSearch = toSearch;
			// doSearch();
		}

		// protected Boolean doSearch()
		// {
		// try
		// {
		// Geocoder geocoder = new Geocoder(Addplace.this, Locale.ENGLISH);
		// List<Address> results = geocoder.getFromLocationName(toSearch, 20);
		// Drawable drawableItem =
		// getResources().getDrawable(R.drawable.pushpin);
		// Markers itemmarker = new Markers(drawableItem, mapView);
		//
		// if (results.size() == 0)
		// {
		// return false;
		// }
		//
		// for (int i = 0; i < results.size(); i++)
		// {
		// address = results.get(i);
		//
		// // Now do something with this GeoPoint:
		// GeoPoint p = new GeoPoint((int) (address.getLatitude() * 1E6), (int)
		// (address.getLongitude() * 1E6));
		// OverlayItem item = new OverlayItem(p, "", "");
		// // item.setMarker(getResources().getDrawable(R.drawable.pushpin));
		// itemmarker.addOverlay(item);
		// }
		//
		// }
		// catch (Exception e)
		// {
		// Log.e("", "Something went wrong: ", e);
		// Toast.makeText(Addplace.this,
		// "Oops Google Maps Service is not available at this moment.",
		// Toast.LENGTH_SHORT);
		// return false;
		// }
		// return true;
		// }

		// @Override
		// protected Boolean doInBackground(Void... voids)
		// {
		//
		// try
		// {
		// Geocoder geocoder = new Geocoder(Addplace.this, Locale.ENGLISH);
		// List<Address> results = geocoder.getFromLocationName(toSearch, 20);
		// Drawable drawableItem =
		// getResources().getDrawable(R.drawable.pushpin);
		// Markers itemmarker = new Markers(drawableItem, mapView);
		//
		// if (results.size() == 0)
		// {
		// return false;
		// }
		//
		// for (int i = 0; i < results.size(); i++)
		// {
		// address = results.get(i);
		//
		// // Now do something with this GeoPoint:
		// GeoPoint p = new GeoPoint((int) (address.getLatitude() * 1E6), (int)
		// (address.getLongitude() * 1E6));
		// OverlayItem item = new OverlayItem(p, "", "");
		// // item.setMarker(getResources().getDrawable(R.drawable.pushpin));
		// itemmarker.addOverlay(item);
		// }
		//
		// }
		// catch (Exception e)
		// {
		// Log.e("", "Something went wrong: ", e);
		// Toast.makeText(Addplace.this,
		// "Oops Google Maps Service is not available at this moment.",
		// Toast.LENGTH_SHORT);
		// return false;
		// }
		// return true;
		// }
	}

	private Boolean doSearch(String shopSearch, String addrSearch)
	{
		// String addressSearch;
		Drawable drawableItem = getResources().getDrawable(R.drawable.pushpin);
		// Double lowerLeftLat = 1.253715;
		// Double lowerLeftLng = 103.613434;
		// Double upperRigLat = 1.482302;
		// Double upperRigLng = 104.003448;
		OverlayItem item = null;
		// String addr;

		try
		{
			listOfOverlays = mapView.getOverlays();
			listOfOverlays.clear();
			Markers itemmarker = new Markers(drawableItem, mapView);
			// Geocoder geocoder = new Geocoder(Addplace.this, Locale.ENGLISH);

			String formatSearch = shopSearch.replace(' ', '+') + "+" + addrSearch.replace(' ', '+');

			String stringUrl = "https://maps.googleapis.com/maps/api/place/search/json?location=1.3667,103.8&radius=50000&sensor=true&key=AIzaSyAxAq7cT-RTcHdT7ccVc1LQCK85J133hsg&keyword='" + formatSearch + "'";

			// InputStream is = new URL(stringUrl).openStream();
			// try {
			// BufferedReader rd = new BufferedReader(new InputStreamReader(is,
			// Charset.forName("UTF-8")));
			// StringBuilder sb = new StringBuilder();
			// int cp;
			// while ((cp = rd.read()) != -1) {
			// sb.append((char) cp);
			// }
			//
			// String jsonOutput = sb.toString();
			//
			// Log.d("jsonOutput", jsonOutput);
			// } finally {
			// is.close();
			// }

			URL url = new URL(stringUrl);

			HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
			StringBuilder response = new StringBuilder();

			if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));// ,8192);
				String strLine = null;
				while ((strLine = input.readLine()) != null)
				{
					response.append(strLine);
				}
				input.close();
			}
			String jsonOutput = response.toString();
			String[] temp;
			if (jsonOutput.split("location") == null)
			{
				return false;
			}
			else
			{
				temp = jsonOutput.split("location");
				for (int i = 0; i < temp.length; i++)
				{
					Log.d("jsonOutput", temp[i]);
				}
				JSONObject jObj = new JSONObject(jsonOutput);
				JSONArray jArray = jObj.getJSONArray("results");
				JSONObject jsonResult = null;
				Log.d("jArray length", Integer.toString(jArray.length()));
				List<Shop> shoplist = new ArrayList<Shop>();
				for (int i = 0; i < jArray.length(); i++)
				{
					jsonResult = jArray.getJSONObject(i);
					String geometry = jsonResult.getString("geometry");
					JSONObject jGeo = new JSONObject(geometry);
					String location = jGeo.getString("location");
					JSONObject jloc = new JSONObject(location);
					String lat = jloc.getString("lat");
					String lng = jloc.getString("lng");
					String name = jsonResult.getString("name");
					String address = jsonResult.getString("vicinity");
					Log.d("name", name);
					Shop shopResult = new Shop(address, name, lat, lng);
					shoplist.add(shopResult);
				}

				// List<Address> results = geocoder.getFromLocationName(toSearch
				// + " Singapore", 100, lowerLeftLat, lowerLeftLng,
				// upperRigLat, upperRigLng);

				// for (int l = 0; l < results.size(); l++) {
				// Log.d("Results: ", results.get(l).toString());
				// }
				// addrList = new ArrayList<String>();
				addrMap = new HashMap<GeoPoint, List<String>>();
				// if (results.size() == 0) {
				// return false;
				// }

				// else {
				startingPoint = new GeoPoint((int) (Double.parseDouble(shoplist.get(0).getLat()) * 1E6), (int) (Double.parseDouble(shoplist.get(0).getLng()) * 1E6));
				for (int i = 0; i < shoplist.size(); i++)
				{
					// addressSearch = shoplist.get(i).getAddress();
					// addr = "";
					shopDetail = new ArrayList<String>();
					// String geopoint = shoplist.get(i).getLat() + " " +
					// shoplist.get(i).getLng();
					// Now do something with this GeoPoint:
					GeoPoint p = new GeoPoint((int) (Double.parseDouble(shoplist.get(i).getLat()) * 1E6), (int) (Double.parseDouble(shoplist.get(i).getLng()) * 1E6));
					// if (addressSearch.getMaxAddressLineIndex() == -1) {
					// } else {
					// for (int j = 1; j <=
					// addressSearch.getMaxAddressLineIndex() - 1; j++)
					// {
					// addr += addressSearch.getAddressLine(j) + " ";
					// }
					// addr +=
					// addressSearch.getAddressLine(addressSearch.getMaxAddressLineIndex());
					shopDetail.add(0, shoplist.get(i).getName());
					shopDetail.add(1, shoplist.get(i).getAddress());
					// shopDetail[0] = addressSearch.getAddressLine(0);
					// shopDetail[1] = addr;
					setAddress(p, shopDetail);
					item = new OverlayItem(p, shoplist.get(i).getName(), shoplist.get(i).getAddress());
					itemmarker.addOverlay(item);
					// }
				}
				if (itemmarker != null)
				{
					listOfOverlays.add(itemmarker);
				}

				mapView.invalidate();
			}
		}
		catch (Exception e)
		{
			Log.e("", "Something went wrong: ", e);
			// Toast.makeText(Addplace.this,
			// "Oops Google Maps Service is not available at this moment.",
			// Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// public String ConvertPointToLocation(GeoPoint point)
	// {
	// String addressresult = "";
	// Geocoder geoCoder = new Geocoder(Addplace.this, Locale.getDefault());
	// try
	// {
	// List<Address> addresses = geoCoder.getFromLocation(point.getLatitudeE6()
	// / 1E6, point.getLongitudeE6() / 1E6, 1);
	//
	// if (addresses.size() > 0)
	// {
	// Log.d("In if: ", "Hello");
	// for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex();
	// index++)
	// {
	// addressresult += addresses.get(0).getAddressLine(index) + " ";
	// }
	// }
	// // else
	// // {
	// // address = "Latitude: " + (point.getLatitudeE6() / 1E6) +
	// // "\n Longtitude: " + (point.getLongitudeE6() / 1E6);
	// // }
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// Log.d("address = 0: ", Double.toString(point.getLatitudeE6() / 1E6));
	// address = "Latitude: " + (point.getLatitudeE6() / 1E6) + "\nLongtitude: "
	// + (point.getLongitudeE6() / 1E6);
	// }
	//
	// return addressresult;
	// }

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

			// this.addressP = addressListPassed;
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
			clearBalloon();

			for (GeoPoint key : addrMap.keySet())
			{
				// Log.d("key/value: ", key + "/" + addrMap.get(key).get(0) +
				// " " + addrMap.get(key).get(1));
				for (String strings : addrMap.get(key))
				{
					Log.d("List of addresses: ", strings + ", ");
				}
			}
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
			// Intent myintent = new Intent(Addplace.this, ShopPage.class);
			// startActivity(myintent);
			Log.d("Global Variable", globalVar.getGeoPoint().toString());
			address = getAddress(globalVar.getGeoPoint()).get(1);
			Log.d("Address: ", address);
			searchAddress.setText(address);
			mapSearchBox.setText(getAddress(globalVar.getGeoPoint()).get(0));
			if (address != null)
			{
				clearBalloon();
			}
			return (super.onBalloonTap(index, item));
		}
	}

	public void setAddress(GeoPoint key, List<String> value)
	{
		addrMap.put(key, value);
	}

	public List<String> getAddress(GeoPoint key)
	{
		return addrMap.get(key);
	}

	public void clearBalloon()
	{
		if (listOfOverlays != null)
		{
			for (Overlay overlay : listOfOverlays)
			{
				if (overlay instanceof BalloonItemizedOverlay<?>)
				{
					if (((BalloonItemizedOverlay<?>) overlay).balloonView != null)
						((BalloonItemizedOverlay<?>) overlay).balloonView.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v == btnAdd)
		{
			addShop();
		}
	}

	private void addShop()
	{
		String shopname = mapSearchBox.getText().toString().trim();
		String address = searchAddress.getText().toString().trim();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("name", shopname));
		nameValuePairs.add(new BasicNameValuePair("address", address));
		String lat = Double.toString(globalVar.getGeoPoint().getLatitudeE6() / 1E6);
		nameValuePairs.add(new BasicNameValuePair("lat", lat));
		String lng = Double.toString(globalVar.getGeoPoint().getLongitudeE6() / 1E6);
		nameValuePairs.add(new BasicNameValuePair("long", Double.toString(globalVar.getGeoPoint().getLongitudeE6() / 1E6)));
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost httppost = new
			// HttpPost("http://172.22.177.204/FYP/insert.php");

			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "insertshop.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(httppost, responseHandler);
			// HttpResponse response = httpclient.execute(httppost);

			// HttpEntity entity = response.getEntity();
			// is = entity.getContent();
			int lastid = Integer.parseInt(response);
			acknowledge();
			/*
			 * Intent i = new Intent("socialtour.socialtour.ATTRACTION"); Bundle
			 * bundle=getIntent().getExtras(); Uri pic = (Uri)
			 * bundle.get("pic"); i.putExtra("pic", pic);
			 * i.putExtra("EMPLOYEE_ID", lastid); i.putExtra("EMPLOYEE_NAME",
			 * shopname); startActivity(i);
			 */

			Intent intent = new Intent(getParent(), ChooseCategory.class);
			intent.putExtra("SHOP_ID", lastid);
	        intent.putExtra("SHOP_NAME", shopname);
	        intent.putExtra("SHOP_ADDRESS", address);

	        Log.d("Shop Lat and Lng: ", lat + " and " + lng);
	        intent.putExtra("SHOPLAT", lat);
	        intent.putExtra("SHOPLNG", lng);
			Bundle bundle = getIntent().getExtras();
			Uri pic = (Uri) bundle.get("pic");
			intent.putExtra("pic", pic);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Add Product", intent);			

		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error in http connection" + e.toString());
		}
	}

	private void acknowledge()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle("Shop has been added.");

		dialog.setNeutralButton("OK", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

				dialog.dismiss();
			}
		});
		dialog.show();
	}

	class MapOverlay extends com.google.android.maps.Overlay
	{
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapview)
		{

			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				Log.d("Inside MapView", "true");
				scroll.setIsScrollable(false);

				mapview.requestDisallowInterceptTouchEvent(true);
			}
			return false;
		}
	}

}
