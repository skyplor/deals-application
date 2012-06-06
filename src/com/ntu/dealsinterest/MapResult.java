package com.ntu.dealsinterest;

import java.util.ArrayList;
import java.util.List;

import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.ntu.dealsinterest.R;
import com.ntu.dealsinterest.models.Shop;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class MapResult extends MapActivity
{

	private MapView mapView;
	private MapController mapController;
	private static GlobalVariable globalVar;
	Handler mHandler = new Handler();
	List<Overlay> listOfOverlays;

	Markers usermarker;
	Markers itemmarker;
	Drawable drawableUser;
	Drawable drawableItem;

	List<Shop> shoplist;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mapresult);

		shoplist = new ArrayList<Shop>();
		Intent intent = getIntent();
		if (intent.getBooleanExtra("main", false))
		{
			shoplist = Main.shoplist;
		}
		else if (intent.getBooleanExtra("search", false))
		{
			shoplist = Search.shoplist;
		}
		
		mapView = (MapView) findViewById(R.id.mapView);
		globalVar = ((GlobalVariable) getApplicationContext());
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

		GeoPoint point = new GeoPoint((int) (Double.parseDouble(shoplist.get(0).getLat()) * 1E6), (int) (Double.parseDouble(shoplist.get(0).getLng()) * 1E6));

		mapController.animateTo(point);
		mapController.setZoom(13);

		listOfOverlays.clear();
		searchStores();

		mapView.invalidate();
		// }
	}

	protected boolean isRouteDisplayed()
	{
		return false;
	}

	private void searchStores()
	{
		drawableItem = getResources().getDrawable(R.drawable.pushpin);
		itemmarker = new Markers(drawableItem, mapView);
		// TODO Auto-generated method stub
		for (int sp = 0; sp < shoplist.size(); sp++)
		{
			GeoPoint p = new GeoPoint((int) (Double.parseDouble(shoplist.get(sp).getLat()) * 1E6), (int) (Double.parseDouble(shoplist.get(sp).getLng()) * 1E6));
			OverlayItem item = new OverlayItem(p, shoplist.get(sp).getName(), shoplist.get(sp).getAddress());
			itemmarker.addOverlay(item);

		}
		listOfOverlays.add(itemmarker);
	}


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
			parentActivity.startChildActivity("Shop Detail "+ TabGroup1Activity.intentCount, myintent);
			TabGroup1Activity.intentCount++;
			return (super.onBalloonTap(index, item));
		}
	}

}
