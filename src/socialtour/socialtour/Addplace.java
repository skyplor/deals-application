package socialtour.socialtour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import socialtour.socialtour.MapResult.Markers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Addplace extends MapActivity implements OnClickListener{

	private MapView mapView;
	private MapController mapController;
	private GeoPoint startingPoint;
	private EditText mapSearchBox, searchAddress;
	private GlobalVariable globalVar;
	private Button search,btnAdd;
	List<Overlay> listOfOverlays;
    Spinner category;
	String[] categories = {"men","women","children",
							"unisex","bags","accessories",
							"shoes"};

	List<String> addrList = null;
	List<String> shopDetail; 
	HashMap<GeoPoint, List<String>> addrMap;
	String address = "";

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addplace);

		mapView = (MapView) findViewById(R.id.addShopMap);
		mapSearchBox = (EditText) findViewById(R.id.txtShopname);
		searchAddress = (EditText) findViewById(R.id.txtShopaddress);
		search = (Button) findViewById(R.id.searchShopsBtn);

        btnAdd = (Button)findViewById(R.id.uploadProduct);
		
		SpinnerAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
 		category = (Spinner) findViewById(R.id.shopcategorylist);
 		category.setAdapter(adapter);
 		btnAdd.setOnClickListener(this);

		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(12);

		startingPoint = new GeoPoint(1303999, 103832731);
		mapController.setCenter(startingPoint);

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
				if (listOfOverlays != null)
				{
					clearBalloon();
					listOfOverlays.clear();
				}
				if (!doSearch(mapSearchBox.getText().toString()))
				{
					Toast.makeText(Addplace.this, "Unable to find any result. Please try again.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					mapController.animateTo(startingPoint);
				}
				// new
				// SearchClicked(mapSearchBox.getText().toString());//.execute();
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

	private class SearchClicked {// extends AsyncTask<Void, Void, Boolean> {
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

	private Boolean doSearch(String toSearch)
	{
		Address addressSearch;
		Drawable drawableItem = getResources().getDrawable(R.drawable.pushpin);
		Double lowerLeftLat = 1.253715;
		Double lowerLeftLng = 103.613434;
		Double upperRigLat = 1.482302;
		Double upperRigLng = 104.003448;
		OverlayItem item = null;
		String addr;

		try
		{
			listOfOverlays = mapView.getOverlays();
			listOfOverlays.clear();
			Markers itemmarker = new Markers(drawableItem, mapView);
			Geocoder geocoder = new Geocoder(Addplace.this, Locale.ENGLISH);
			List<Address> results = geocoder.getFromLocationName(toSearch + " Singapore", 100, lowerLeftLat, lowerLeftLng, upperRigLat, upperRigLng);

			for (int l = 0; l < results.size(); l++)
			{
				Log.d("Results: ", results.get(l).toString());
			}
			// addrList = new ArrayList<String>();
			addrMap = new HashMap<GeoPoint, List<String>>();
			if (results.size() == 0)
			{
				return false;
			}

			else
			{
				startingPoint = new GeoPoint((int) (results.get(0).getLatitude() * 1E6), (int) (results.get(0).getLongitude() * 1E6));
				for (int i = 0; i < results.size(); i++)
				{
					addressSearch = results.get(i);
					addr = "";
					shopDetail = new ArrayList<String>();
					String geopoint = Double.toString(addressSearch.getLatitude()) + " " + Double.toString(addressSearch.getLongitude());
					// Now do something with this GeoPoint:
					GeoPoint p = new GeoPoint((int) (addressSearch.getLatitude() * 1E6), (int) (addressSearch.getLongitude() * 1E6));
					if (addressSearch.getMaxAddressLineIndex() == -1)
					{

					}
					else
					{
						for (int j = 1; j <= addressSearch.getMaxAddressLineIndex() - 1; j++)
						{
							addr += addressSearch.getAddressLine(j) + " ";
						}
						addr += addressSearch.getAddressLine(addressSearch.getMaxAddressLineIndex());
						shopDetail.add(0,addressSearch.getAddressLine(0));
						shopDetail.add(1,addr);
//						shopDetail[0] = addressSearch.getAddressLine(0);
//						shopDetail[1] = addr;
						setAddress(p, shopDetail);
						item = new OverlayItem(p, addressSearch.getAddressLine(0), addr);
						itemmarker.addOverlay(item);
					}
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
			Toast.makeText(Addplace.this, "Oops Google Maps Service is not available at this moment.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public String ConvertPointToLocation(GeoPoint point)
	{
		String addressresult = "";
		Geocoder geoCoder = new Geocoder(Addplace.this, Locale.getDefault());
		try
		{
			List<Address> addresses = geoCoder.getFromLocation(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);

			if (addresses.size() > 0)
			{
				Log.d("In if: ", "Hello");
				for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
				{
					addressresult += addresses.get(0).getAddressLine(index) + " ";
				}
			}
			// else
			// {
			// address = "Latitude: " + (point.getLatitudeE6() / 1E6) +
			// "\n Longtitude: " + (point.getLongitudeE6() / 1E6);
			// }
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.d("address = 0: ", Double.toString(point.getLatitudeE6() / 1E6));
			address = "Latitude: " + (point.getLatitudeE6() / 1E6) + "\nLongtitude: " + (point.getLongitudeE6() / 1E6);
		}

		return addressresult;
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public class Markers extends BalloonItemizedOverlay<OverlayItem> {

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
//				Log.d("key/value: ", key + "/" + addrMap.get(key).get(0) + " " + addrMap.get(key).get(1));
				for(String strings : addrMap.get(key))
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
	public void onClick(View v) {
		if(v==btnAdd) {
			addShop();
		}
	}
	
	private void addShop(){
		String shopname = mapSearchBox.getText().toString().trim();
		String address = searchAddress.getText().toString().trim();
		String selectedcategory = category.getSelectedItem().toString();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	
    	nameValuePairs.add(new BasicNameValuePair("name",shopname));
    	nameValuePairs.add(new BasicNameValuePair("address",address));
    	nameValuePairs.add(new BasicNameValuePair("shoptype",selectedcategory));
    	nameValuePairs.add(new BasicNameValuePair("lat",Double.toString(globalVar.getGeoPoint().getLatitudeE6()/1E6)));
    	nameValuePairs.add(new BasicNameValuePair("long",Double.toString(globalVar.getGeoPoint().getLongitudeE6()/1E6)));
    	//http post
    	try{
    	     HttpClient httpclient = new DefaultHttpClient();
    	     //HttpPost httppost = new HttpPost("http://172.22.177.204/FYP/insert.php");
    	     
    	     HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "insertshop.php");
    	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	     
    	     ResponseHandler<String> responseHandler=new BasicResponseHandler();
    	     String response = httpclient.execute(httppost, responseHandler);
    	     //HttpResponse response = httpclient.execute(httppost);
    	               	     
    	     //HttpEntity entity = response.getEntity();
    	     //is = entity.getContent();
    	     int lastid = Integer.parseInt(response);
    	     acknowledge();
    	     /*
    	     Intent i = new Intent("socialtour.socialtour.ATTRACTION");
    	     Bundle bundle=getIntent().getExtras();
		     Uri pic = (Uri) bundle.get("pic");
		     i.putExtra("pic", pic);
		     i.putExtra("EMPLOYEE_ID", lastid);
		     i.putExtra("EMPLOYEE_NAME", shopname);
		     startActivity(i);*/
		     
		     Intent i = new Intent(getParent(), Attraction.class);
		     Bundle bundle=getIntent().getExtras();
		     Uri pic = (Uri) bundle.get("pic");
		     i.putExtra("pic", pic);
		     i.putExtra("EMPLOYEE_ID", lastid);
		     i.putExtra("EMPLOYEE_NAME", shopname);
	     	 TabGroupActivity parentActivity = (TabGroupActivity)getParent();
	     	 parentActivity.startChildActivity("Add Product", i);
    	 
    	
    	}catch(Exception e){
	         Log.e("log_tag", "Error in http connection"+e.toString());
	    }
	}
	
    private void acknowledge(){
	     AlertDialog.Builder dialog=new AlertDialog.Builder(getParent());
	        dialog.setTitle("Shop has been added.");

	        dialog.setNeutralButton("OK",new android.content.DialogInterface.OnClickListener(){
	            @Override
	            public void onClick(DialogInterface dialog, int which) {

	                dialog.dismiss();               
	            }});
	        dialog.show();
   }

}
