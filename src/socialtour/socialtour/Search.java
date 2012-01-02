package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fedorvlasov.lazylist.LazyAdapter;
import com.fedorvlasov.lazylist.SimpleLazyAdapter;
import com.google.android.maps.GeoPoint;

import socialtour.socialtour.models.Product;
import socialtour.socialtour.models.Shop;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends Activity implements OnClickListener
{
	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;
	String result = null;

	Product[] arrPro;
	Shop[] arrShop;
	Button btnSearch, mapview;
	EditText txtSearch;
	RadioGroup radSearch;
	ListView searchResult;
	SimpleLazyAdapter adapter;
	boolean isProduct;
	private LocationManager locationManager;
	private GPSLocationListener locationListener;
	private GeoPoint point = new GeoPoint(1304256, 103832538);
	private List<GeoPoint> pointList;
	public static List<Shop> shoplist = new ArrayList<Shop>();
//	ArrayList<Shop> shoplist = new ArrayList<Shop>();

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		btnSearch = (Button) findViewById(R.id.btnSearch);
		txtSearch = (EditText) findViewById(R.id.txtSearch);
		radSearch = (RadioGroup) findViewById(R.id.radSearch);
		searchResult = (ListView) findViewById(R.id.listSearch);
		mapview = (Button) findViewById(R.id.btnMap);
		btnSearch.setOnClickListener(this);
		mapview.setOnClickListener(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new GPSLocationListener();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		// locSearch.setOnClickListener(new View.OnClickListener()
		// {
		// public void onClick(View v)
		// {
		// searchStores();
		// }
		// });

		searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id)
			{
				Intent intent = null;
				if (isProduct)
				{
					/*
					 * intent = new
					 * Intent("socialtour.socialtour.PRODUCTDETAIL");
					 * intent.putExtra("lastproductid", arrPro[pos].getId());
					 * startActivity(intent);
					 */

					intent = new Intent(getParent(), Productdetail.class);
					intent.putExtra("lastproductid", arrPro[pos].getId());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("Product Detail", intent);
				}
				else
				{
					/*
					 * intent = new Intent("socialtour.socialtour.SHOPDETAIL");
					 * intent.putExtra("shopid",arrShop[pos].getId()); intent
					 * .putExtra("shopname",arrShop[pos].getName()); intent
					 * .putExtra("shopaddress",arrShop[pos].getAddress ()); int
					 * icon = arrShop[pos].getIcon();
					 * intent.putExtra("icon",icon); startActivity(intent);
					 */

					intent = new Intent(getParent(), Shopdetail.class);
					intent.putExtra("shopid", arrShop[pos].getId());
					intent.putExtra("shopname", arrShop[pos].getName());
					intent.putExtra("shopaddress", arrShop[pos].getAddress());
					int icon = arrShop[pos].getIcon();
					intent.putExtra("icon", icon);
					intent.putExtra("lat", arrShop[pos].getLat());
					intent.putExtra("long", arrShop[pos].getLng());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("Shop Detail", intent);
				}

			}
		});

	}

	@Override
	public void onClick(View v)
	{
		String searchStr = txtSearch.getText().toString();
		if (v == btnSearch)
		{
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
			if (searchStr.equals(""))
			{
				// alert
			}
			else
			{
				int checkedRadioButton = radSearch.getCheckedRadioButtonId();
				switch (checkedRadioButton)
				{
				case R.id.radio0:
					getProduct(searchStr, "Product", false);
					isProduct = true;
					break;
				case R.id.radio1:
					getProduct(searchStr, "Shop", false);
					isProduct = false;
					break;
				}
			}
		}
		else if (v == mapview)
		{
			//Save the results' details into a list of shops
			//Put into bundles to be passed to the next intent
			Intent intent = new Intent(this, MapResult.class);
//			intent.putExtra("shoplist", shoplist);
			//Start new intent MapResult.
			startActivity(intent);
			
			
//			if (searchStr.equals(""))
//			{
//				// alert
//			}
//			else
//			{
//				int checkedRadioButton = radSearch.getCheckedRadioButtonId();
//				switch (checkedRadioButton)
//				{
//				case R.id.radio0:
//					getProduct(searchStr, "Product", true);
//					isProduct = true;
//					break;
//				case R.id.radio1:
//					getProduct(searchStr, "Shop", true);
//					isProduct = true;
//					break;
//				}
//			}
		}
	}

	public void getProduct(String searchstr, String mode, Boolean location)
	{
		searchResult.setAdapter(null);
		if (!location)
		{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if (mode.equals("Product"))
			{
				nameValuePairs.add(new BasicNameValuePair("product", "1"));
			}
			else if (mode.equals("Shop"))
			{
				nameValuePairs.add(new BasicNameValuePair("shop", "1"));
			}
			nameValuePairs.add(new BasicNameValuePair("search", searchstr));
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "search.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}
			catch (Exception e)
			{
				Log.e("log_tag", "Error in http connection" + e.toString());
			}
		}
//		else
//		{
//			if (point == null)
//			{
//				Toast.makeText(this, "Unable to get a gps connection. Is your gps service turned on?", Toast.LENGTH_SHORT);
//				return;
//			}
//			JSONObject json = new JSONObject();
//			try
//			{
//				HttpClient httpclient = new DefaultHttpClient();
//				HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000); // Timeout
//																							// Limit
//				HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "storeLocations.php");
//				json.put("lat", Double.toString(point.getLatitudeE6() / 1E6));
//				json.put("lng", Double.toString(point.getLongitudeE6() / 1E6));
//				if (mode.equals("Product"))
//				{
//					json.put("type", "product");
//				}
//				else if (mode.equals("Shop"))
//				{
//					json.put("type", "store_locations");
//				}
//				json.put("searchTerms", searchstr);
//				json.put("radius", "1000");
//				httppost.setHeader("json", json.toString());
//				StringEntity se = new StringEntity(json.toString());
//				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//				httppost.setEntity(se);
//				// Log.d("se: ", new BufferedReader(new
//				// InputStreamReader(se.getContent())).readLine());
//				HttpResponse response = httpclient.execute(httppost);
//				if (response != null)
//				{
//					HttpEntity entity = response.getEntity();
//					is = entity.getContent();
//				}
//			}
//
//			catch (Exception ex)
//			{
//				Log.e("log_tag", "Error in http connection " + ex.toString());
//			}
//		}
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
			Log.d("RESULT: ", result);
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
			mapview.setEnabled(true);
			jArray = new JSONArray(result);
			JSONObject json_data = null;
			if (mode.equals("Product"))
			{
				arrPro = new Product[jArray.length()];
				Shop[] tempShop = new Shop[jArray.length()];
				for (int i = 0; i < jArray.length(); i++)
				{
					json_data = jArray.getJSONObject(i);
					tempShop[i] = new Shop();
					arrPro[i] = new Product();
					arrPro[i].setId(json_data.getInt("productid"));
					arrPro[i].setFilename(json_data.getString("filename"));
					arrPro[i].setUrl(json_data.getString("url"));
					arrPro[i].setPercentdiscount(json_data.getInt("percentdiscount"));
					tempShop[i].setName(json_data.getString("name"));
					
					Shop shopResult = new Shop(json_data.getInt("id"), json_data.getString("address"), json_data.getString("name"), json_data.getString("lat"), json_data.getString("lng"), json_data.getString("shoptype"));
					shoplist.add(shopResult);
				}
				adapter = new SimpleLazyAdapter(this, arrPro, tempShop);
			}
			else if (mode.equals("Shop"))
			{
				
				arrShop = new Shop[jArray.length()];
				for (int i = 0; i < jArray.length(); i++)
				{
					json_data = jArray.getJSONObject(i);
					arrShop[i] = new Shop();
					arrShop[i].setId(json_data.getInt("id"));
					arrShop[i].setName(json_data.getString("name"));
					arrShop[i].setAddress(json_data.getString("address"));
					arrShop[i].setType(json_data.getString("shoptype"));
					arrShop[i].setLat((String) json_data.get("lat"));
					arrShop[i].setLng((String) json_data.get("lng"));

					Shop shopResult = new Shop(json_data.getInt("id"), json_data.getString("address"), json_data.getString("name"), json_data.getString("lat"), json_data.getString("lng"), json_data.getString("shoptype"));
					shoplist.add(shopResult);
				}
				adapter = new SimpleLazyAdapter(this, arrShop);
			}
			// ListAdapter adapter = new ArrayAdapter<String>(this,
			// android.R.layout.simple_list_item_1, employees);
			searchResult.setAdapter(adapter);
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "No products Found", Toast.LENGTH_LONG).show();
			mapview.setEnabled(false);
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}

	}

	private void searchStores(String type)
	{
		// TODO Auto-generated method stub
		// if (globalVar.getSearchType() == 1)
		// {
		if (point == null)
		{
			Toast.makeText(this, "Unable to get a gps connection. Is your gps service turned on?", Toast.LENGTH_SHORT);
			return;
		}
		ConnectDB connect = new ConnectDB(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, type, txtSearch.getText().toString(), 1000);

		pointList = new ArrayList<GeoPoint>();
		// Log.d("Store Location Results in activity: ",
		// connect.storeLocResult());

		if (type.equals("store_locations"))
		{
			for (Shop sp : connect.getShop())
			{
				// Log.d("Shop address: ", sp.address);
				// Log.d("Shop name: ", sp.name);
				// Log.d("Shop lat: ", sp.lat);
				// Log.d("Shop lng: ", sp.lng);
				// Log.d("Shop distance: ", sp.distance);

				GeoPoint p = new GeoPoint((int) (Double.parseDouble(sp.lat) * 1E6), (int) (Double.parseDouble(sp.lng) * 1E6));
				pointList.add(p);
				// Log.d("VO lat after geopoint: ",Integer.toString(((int)
				// (Double.parseDouble(vo.lat) * 1E6))));
				// OverlayItem item = new OverlayItem(p,"Testing Title",
				// "Testing Description");
				// item.setMarker(drawable);
				// usersMarker.addOverlay(item);
				// MapOverlay mapOverlay2 = new MapOverlay(STORES_LOC);
				// mapOverlay2.setPointToDraw(p);
				// listOfOverlays.add(mapOverlay2);
				// OverlayItem item = new OverlayItem(p, sp.name, sp.address);
				// item.setMarker(getResources().getDrawable(R.drawable.pushpin));

				// }
			}
			Log.d("PointList: ", Integer.toString(pointList.get(0).getLatitudeE6()));
		}
		else
		{
			adapter = new SimpleLazyAdapter(this, connect.getArrPro(), connect.getShopArray());
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

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}
	}

}
