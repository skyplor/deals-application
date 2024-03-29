package com.ntu.dealsinterest;

import com.ntu.dealsinterest.models.CategoryList;

import com.ntu.dealsinterest.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class ChooseCategory extends Activity implements OnClickListener{
	private ImageView cat1,cat2,cat3,cat4,cat5,cat6,cat7,cat8,cat9;
	private String categoryname, subcategory;
	ImageView backtomain;
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.step4);
		
		Container.btn1.setVisibility(View.INVISIBLE);
		Container.btn2.setVisibility(View.INVISIBLE);
		Container.btn3.setVisibility(View.INVISIBLE);
		Container.map.setVisibility(View.INVISIBLE);
		
		cat1 = (ImageView)findViewById(R.id.category1);
		cat2 = (ImageView)findViewById(R.id.category2);
		cat3 = (ImageView)findViewById(R.id.category3);
		cat4 = (ImageView)findViewById(R.id.category4);
		cat5 = (ImageView)findViewById(R.id.category5);
		cat6 = (ImageView)findViewById(R.id.category6);
		cat7 = (ImageView)findViewById(R.id.category7);
		cat8 = (ImageView)findViewById(R.id.category8);
		cat9 = (ImageView)findViewById(R.id.category9);
		
		cat1.setOnClickListener(this);
		cat2.setOnClickListener(this);
		cat3.setOnClickListener(this);
		cat4.setOnClickListener(this);
		cat5.setOnClickListener(this);
		cat6.setOnClickListener(this);
		cat7.setOnClickListener(this);
		cat8.setOnClickListener(this);
		cat9.setOnClickListener(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Container.btn1.setVisibility(View.INVISIBLE);
		Container.btn2.setVisibility(View.INVISIBLE);
		Container.btn3.setVisibility(View.INVISIBLE);
		Container.map.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		if (v==cat1 || v==cat2 || v==cat3 || v==cat4 || v==cat5 || v==cat6 || v==cat7 || v==cat8 || v==cat9){
		CharSequence[] items = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
		builder.setTitle("Choose a sub-category");
		if(v==cat1) {
			categoryname = "Men";
			items = CategoryList.Men;
		}else if (v==cat2){
			categoryname = "Women";
			items = CategoryList.Women;
		}else if (v==cat3){
			categoryname = "Cosmetics";
			items = CategoryList.Cosmetics;
		}else if (v==cat4){
			categoryname = "Digital";
			items = CategoryList.Digital;
		}else if (v==cat5){
			categoryname = "Household";
			items = CategoryList.Household;
		}else if (v==cat6){
			categoryname = "Kids";
			items = CategoryList.Kids;
		}else if (v==cat7){
			categoryname = "Groceries";
			items = CategoryList.Groceries;
		}else if (v==cat8){
			categoryname = "Entertainment";
			items = CategoryList.Entertainment;
		}else if (v==cat9){
			categoryname = "Others";
			items = CategoryList.Others;
		}
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	
		    	if (categoryname.equals("Men"))
		    		subcategory = CategoryList.Men[item];
		    	else if (categoryname.equals("Women"))
		    		subcategory = CategoryList.Women[item];
		    	else if (categoryname.equals("Cosmetics"))
		    		subcategory = CategoryList.Cosmetics[item];
		    	else if (categoryname.equals("Digital"))
		    		subcategory = CategoryList.Digital[item];
		    	else if (categoryname.equals("Household"))
		    		subcategory = CategoryList.Household[item];
		    	else if (categoryname.equals("Kids"))
		    		subcategory = CategoryList.Kids[item];
		    	else if (categoryname.equals("Groceries"))
		    		subcategory = CategoryList.Groceries[item];
		    	else if (categoryname.equals("Entertainment"))
		    		subcategory = CategoryList.Entertainment[item];
		    	else if (categoryname.equals("Others"))
		    		subcategory = CategoryList.Others[item];
		    	
		    	TabGroupActivity parentActivity = (TabGroupActivity)getParent();
				Intent i = new Intent(getParent(), InputPrice.class);
				Bundle bundle=getIntent().getExtras();
				Uri imageUri = (Uri) bundle.get("pic");
				int tempShopId = bundle.getInt("SHOP_ID");
				String tempShopName = bundle.getString("SHOP_NAME");
				String tempShopAddress = bundle.getString("SHOP_ADDRESS");
				String tempShopLat = bundle.getString("SHOPLAT");
				String tempShopLng = bundle.getString("SHOPLNG");
				i.putExtra("SHOP_ID", tempShopId);
		        i.putExtra("SHOP_NAME", tempShopName);
		        i.putExtra("SHOP_ADDRESS", tempShopAddress);
		        Log.d("Shop Lat and Lng: ", tempShopLat + " and " + tempShopLng );
		        i.putExtra("SHOPLAT", tempShopLat);
		        i.putExtra("SHOPLNG", tempShopLng);
		        i.putExtra("category", categoryname);
		        i.putExtra("subcategory", subcategory);
				i.putExtra("pic", imageUri);
				
				parentActivity.startChildActivity("Step 4 " + TabGroup1Activity.intentCount, i);
				TabGroup1Activity.intentCount++;
		    }
		});
		builder.setNeutralButton("Cancel", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		builder.show();
		}
	}
	
}
