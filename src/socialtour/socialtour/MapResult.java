package socialtour.socialtour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import socialtour.socialtour.models.Shop;

import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.android.maps.GeoPoint;
//import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

//import android.widget.Toast;

public class MapResult extends MapActivity
{

	private static final Integer INIT_NORM = 0, INIT_FB = 1;
	// private static final Integer MY_POINT = 0, STORES_LOC = 1;
	// private LocationManager locationManager;
	// private LocationListener locationListener;
	private MapView mapView;
	private MapController mapController;
	//private Button logout;
	private static GlobalVariable globalVar;
	Handler mHandler = new Handler();
	List<Overlay> listOfOverlays;

	Markers usermarker;
	Markers itemmarker;
	Drawable drawableUser;
	Drawable drawableItem;

//	private Boolean fbBtn;
	List<Shop> shoplist;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mapresult);

		shoplist = new ArrayList<Shop>();
		Intent intent = getIntent();
		if(intent.getBooleanExtra("main", false))
		{
			shoplist = Main.shoplist;
		}
		else if(intent.getBooleanExtra("search", false))
		{
		shoplist = Search.shoplist;
		}
		// shoplist = (ArrayList<Shop>) intent.getSerializableExtra("shoplist");

		// locationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		//
		// locationListener = new GPSLocationListener();
		//
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 0, 0, locationListener);

		mapView = (MapView) findViewById(R.id.mapView);
		//logout = (Button) findViewById(R.id.logoutButton);
		globalVar = ((GlobalVariable) getApplicationContext());
//		fbBtn = globalVar.getfbBtn();
		// Log.d("FbButton: ", fbBtn.toString());
		for (int i = 0; i < shoplist.size(); i++)
		{
			Log.d("shoplist", shoplist.get(i).getAddress());
		}

		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(13);

		initStores();
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

		// add marker
		// MapOverlay mapOverlay = new MapOverlay(MY_POINT);
		// mapOverlay.setPointToDraw(point);
		// listOfOverlays = mapView.getOverlays();
		// listOfOverlays.clear();
		// listOfOverlays.add(mapOverlay);
		// OverlayItem itemMyself = new OverlayItem(point, "Hello",
		// "You are here");
		// itemMyself.setMarker(getResources().getDrawable(R.drawable.location));
		// usermarker.addOverlay(itemMyself);
		listOfOverlays.clear();
		// listOfOverlays.add(usermarker);

		// Drawable drawable = getResources().getDrawable(R.drawable.red);
		searchStores();

		mapView.invalidate();
		// }
	}

	protected boolean isRouteDisplayed()
	{
		return false;
	}

	private void doLogout(int type)
	{
		if (type == INIT_NORM)
		{
			// Logout logic here...
			globalVar = ((GlobalVariable) getApplicationContext());
			globalVar.setName("");
//			globalVar.setfbBtn(false);
			globalVar.setHashPw("");
			globalVar.setEm("");

			SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
			SharedPreferences.Editor editor = login.edit();
			editor.putString("emailLogin", "");
			editor.putString("pwLogin", "");
			editor.commit();
		}
		else
		{
			// Go to login
			globalVar = ((GlobalVariable) getApplicationContext());
			Facebook mFacebook = globalVar.getFBState();
			SessionEvents.onLogoutBegin();
			AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
			asyncRunner.logout(getApplicationContext(), new LogoutRequestListener());
		}

		// Return to the login activity
		Intent intent = new Intent(this, LoginPage.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void searchStores()
	{
		drawableItem = getResources().getDrawable(R.drawable.pushpin);
		itemmarker = new Markers(drawableItem, mapView);
		// TODO Auto-generated method stub
		// if (globalVar.getSearchType() == 1)
		// {
		// ConnectDB connect = new ConnectDB(point.getLatitudeE6() / 1E6,
		// point.getLongitudeE6() / 1E6, "store_locations", "", 1000);

		// Log.d("Store Location Results in activity: ",
		// connect.storeLocResult());
		for (int sp = 0; sp < shoplist.size(); sp++)
		{
			GeoPoint p = new GeoPoint((int) (Double.parseDouble(shoplist.get(sp).getLat()) * 1E6), (int) (Double.parseDouble(shoplist.get(sp).getLng()) * 1E6));
			// Log.d("VO lat after geopoint: ",Integer.toString(((int)
			// (Double.parseDouble(vo.lat) * 1E6))));
			// OverlayItem item = new OverlayItem(p,"Testing Title",
			// "Testing Description");
			// item.setMarker(drawable);
			// usersMarker.addOverlay(item);
			// MapOverlay mapOverlay2 = new MapOverlay(STORES_LOC);
			// mapOverlay2.setPointToDraw(p);
			// listOfOverlays.add(mapOverlay2);
			OverlayItem item = new OverlayItem(p, shoplist.get(sp).getName(), shoplist.get(sp).getAddress());
			// item.setMarker(getResources().getDrawable(R.drawable.pushpin));
			itemmarker.addOverlay(item);

		}
		listOfOverlays.add(itemmarker);
		// }
	}

	// public String ConvertPointToLocation(GeoPoint point)
	// {
	// String address = "";
	// Geocoder geoCoder = new Geocoder(MapResult.this, Locale.getDefault());
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
	// address += addresses.get(0).getAddressLine(index) + " ";
	// }
	// }
	// // else
	// // {
	// // address = "Latitude: " + (point.getLatitudeE6() / 1E6) +
	// "\n Longtitude: " + (point.getLongitudeE6() / 1E6);
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
	// return address;
	// }

	// private class GPSLocationListener implements LocationListener {
	//
	// @Override
	// public void onLocationChanged(Location location)
	// {
	//
	// listOfOverlays = mapView.getOverlays();
	// for (Overlay overlay : listOfOverlays) {
	// if (overlay instanceof BalloonItemizedOverlay<?> ) {
	// if (((BalloonItemizedOverlay<?>) overlay).balloonView != null)
	// ((BalloonItemizedOverlay<?>)
	// overlay).balloonView.setVisibility(View.GONE);
	// }
	// }
	// listOfOverlays.clear();
	// drawableUser = getResources().getDrawable(R.drawable.location);
	// usermarker = new Markers(drawableUser, mapView);
	// if (location != null)
	// {
	//
	// GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int)
	// (location.getLongitude() * 1E6));
	//
	// mapController.animateTo(point);
	// mapController.setZoom(16);
	//
	// // add marker
	// // MapOverlay mapOverlay = new MapOverlay(MY_POINT);
	// // mapOverlay.setPointToDraw(point);
	// // listOfOverlays = mapView.getOverlays();
	// // listOfOverlays.clear();
	// // listOfOverlays.add(mapOverlay);
	// OverlayItem itemMyself = new OverlayItem(point, "Hello", "You are here");
	// // itemMyself.setMarker(getResources().getDrawable(R.drawable.location));
	// usermarker.addOverlay(itemMyself);
	// listOfOverlays.clear();
	// listOfOverlays.add(usermarker);
	//
	// String address = ConvertPointToLocation(point);
	// Log.d("Address: ", address);
	//
	// // Drawable drawable = getResources().getDrawable(R.drawable.red);
	// searchStores(point);
	//
	// mapView.invalidate();
	// }
	// }
	//
	// @Override
	// public void onProviderDisabled(String provider)
	// {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onProviderEnabled(String provider)
	// {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onStatusChanged(String provider, int status, Bundle extras)
	// {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// }

	// class MapOverlay extends Overlay {
	// private GeoPoint pointToDraw;
	// private Integer drawIcon;
	//
	// public MapOverlay(Integer type)
	// {
	// if (type == MY_POINT)
	// {
	// drawIcon = R.drawable.location;
	// }
	// else if (type == STORES_LOC)
	// {
	// drawIcon = R.drawable.pushpin;
	// }
	// }
	//
	// public void setPointToDraw(GeoPoint point)
	// {
	// pointToDraw = point;
	// }
	//
	// public GeoPoint getPointToDraw()
	// {
	// return pointToDraw;
	// }
	//
	// @Override
	// public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long
	// when)
	// {
	// super.draw(canvas, mapView, shadow);
	//
	// // convert point to pixels
	// Point screenPts = new Point();
	// mapView.getProjection().toPixels(pointToDraw, screenPts);
	//
	// // add marker
	// Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawIcon);
	// canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 24, null); // 24
	// // is
	// // the
	// // height
	// // of
	// // image
	// // Markers marker = new Markers(getResources().getDrawable(drawIcon),
	// MapResult.this);
	//
	// return true;
	// }

	// @Override
	// public boolean onTap(GeoPoint point, MapView mapView)
	// {
	// // super.onTap(point,mapView);
	//
	// // point = mapView.getProjection().fromPixels((int) event.getX(),
	// // (int) event.getY());
	// // mapController.animateTo(point);
	// // mapController.setZoom(18);
	// final double lat, lon;
	// lat = point.getLatitudeE6() / 1E6;
	// lon = point.getLongitudeE6() / 1E6;
	// return true;
	//
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
			//int icon = shoplist.get(index).getIcon();
			//myintent.putExtra("icon", icon);
			myintent.putExtra("lat", shoplist.get(index).getLat());
			myintent.putExtra("long", shoplist.get(index).getLng());
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Shop Detail", myintent);
			return (super.onBalloonTap(index, item));
		}
	}

}
