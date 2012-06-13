package com.ntu.dealsinterest;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fedorvlasov.lazylist.SimpleLazyAdapter;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.ntu.dealsinterest.models.Product;
import com.ntu.dealsinterest.models.Shop;

import com.ntu.dealsinterest.R;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Shopdetail extends MapActivity
{

	private MapView mapView;
	private MapController mapController;
	private GeoPoint gp;
	private GlobalVariable globalVar;
	private TextView shopTitle, shopAddress;

	List<Overlay> mapOverlays;
	Drawable drawable;
	MapItemizedOverlay itemizedOverlay;

	Product[] arrPro;
	TextView shopname, shopaddress;
	ListView detailBrowse;

	SimpleLazyAdapter adapter;

	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;
	String result = null;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shopdetail);

		mapView = (MapView) findViewById(R.id.shopMap);
		shopname = (TextView) findViewById(R.id.shopdetailName);
		shopaddress = (TextView) findViewById(R.id.lblDetailshopaddress);
		detailBrowse = (ListView) findViewById(R.id.listDetailBrowse);
		globalVar = ((GlobalVariable) getApplicationContext());

		Container.btn1.setVisibility(View.GONE);
		Container.btn2.setVisibility(View.GONE);
		Container.btn3.setVisibility(View.GONE);
		Container.map.setVisibility(View.GONE);
		
		Bundle bundle = getIntent().getExtras();
		String name = "", address = "";
		int iconid = -1, shopid = -1;
		Double latitude = 0.0, longitude = 0.0;
		if (bundle != null)
		{
			name = bundle.getString("shopname");
			address = bundle.getString("shopaddress");
			iconid = bundle.getInt("icon");
			shopid = bundle.getInt("shopid");
			latitude = Double.parseDouble(bundle.getString("lat"));
			longitude = Double.parseDouble(bundle.getString("long"));
			// get latitude and longitude
		}
		else
		{
			Shop shopDetail = globalVar.getShop().get(0);
			name = shopDetail.getName();
			address = shopDetail.getAddress();
			shopid = shopDetail.getId();
			latitude = Double.parseDouble(shopDetail.getLat());
			longitude = Double.parseDouble(shopDetail.getLng());
		}

		shopname.setText(name);
		shopaddress.setText(address);
		getProduct(shopid);

		gp = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));

		mapView.setBuiltInZoomControls(false);
		mapController = mapView.getController();
		mapController.setZoom(16);
		mapController.setCenter(gp);

		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.pushpin);
		itemizedOverlay = new MapItemizedOverlay(drawable);

		OverlayItem overlayitem = new OverlayItem(gp, "", "");
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.clear();
		mapOverlays.add(itemizedOverlay);
		mapView.invalidate();

		detailBrowse.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id)
			{
				Intent intent = new Intent(getParent(), Productdetail.class);
				intent.putExtra("lastproductid", arrPro[pos].getId());
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("Product Detail " + TabGroup1Activity.intentCount, intent);
				TabGroup1Activity.intentCount++;
			}
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Container.btn1.setVisibility(View.GONE);
		Container.btn2.setVisibility(View.GONE);
		Container.btn3.setVisibility(View.GONE);
		Container.map.setVisibility(View.GONE);
		
	}

	public void getProduct(int shopid)
	{
		detailBrowse.setAdapter(null);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("shopid", Integer.toString(shopid)));
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "shopproducts.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
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
		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		try
		{
			jArray = new JSONArray(result);
			JSONObject json_data = null;
			arrPro = new Product[jArray.length()];
			for (int i = 0; i < jArray.length(); i++)
			{
				json_data = jArray.getJSONObject(i);
				arrPro[i] = new Product();
				arrPro[i].setId(json_data.getInt("id"));
				arrPro[i].setFilename(json_data.getString("title"));
				arrPro[i].setUrl("");// json_data.getString("url"));

				JSONArray jextraArr = new JSONArray(json_data.getString("extra_fields"));
				JSONObject jextraObjPercent = jextraArr.getJSONObject(3);
				String discountPercent = jextraObjPercent.getString("value");
				arrPro[i].setPercentdiscount(discountPercent);
			}
			adapter = new SimpleLazyAdapter(this, arrPro);
			// ListAdapter adapter = new ArrayAdapter<String>(this,
			// android.R.layout.simple_list_item_1, employees);
			detailBrowse.setAdapter(adapter);
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "No products Found", Toast.LENGTH_LONG).show();
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}

	}

	class MapItemizedOverlay extends ItemizedOverlay<OverlayItem>
	{

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		public MapItemizedOverlay(Drawable defaultMarker)
		{
			super(boundCenterBottom(defaultMarker));
			// TODO Auto-generated constructor stub
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			// TODO Auto-generated method stub
			return mOverlays.get(i);
		}

		@Override
		public int size()
		{
			// TODO Auto-generated method stub
			return mOverlays.size();
		}

		public void addOverlay(OverlayItem overlay)
		{
			mOverlays.add(overlay);
			populate();
		}
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
