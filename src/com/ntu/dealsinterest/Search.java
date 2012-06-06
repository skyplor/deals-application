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
import com.ntu.dealsinterest.R;
import com.ntu.dealsinterest.models.Product;
import com.ntu.dealsinterest.models.Shop;
import com.ntu.dealsinterest.models.TestingClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
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
	ImageView searchresultslbl;
	boolean isProduct;
	public static List<Shop> shoplist = new ArrayList<Shop>();

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		Container.btn1.setVisibility(View.GONE);
		Container.btn2.setVisibility(View.GONE);
		Container.btn3.setVisibility(View.GONE);
		Container.map.setVisibility(View.GONE);
		
		btnSearch = (Button) findViewById(R.id.btnSearch);
		txtSearch = (EditText) findViewById(R.id.txtSearch);
		radSearch = (RadioGroup) findViewById(R.id.radSearch);
		searchResult = (ListView) findViewById(R.id.listSearch);
		mapview = (Button) findViewById(R.id.btnMap);
		searchresultslbl = (ImageView) findViewById(R.id.imgsearchresults);
		btnSearch.setOnClickListener(this);
		mapview.setOnClickListener(this);
		
		searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id)
			{
				Intent intent = null;
				if (isProduct)
				{
					intent = new Intent(getParent(), Productdetail.class);
					intent.putExtra("lastproductid", arrPro[pos].getId());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("Product Detail " + TabGroup1Activity.intentCount, intent);
					TabGroup1Activity.intentCount++;
				}
				else
				{
					intent = new Intent(getParent(), Shopdetail.class);
					intent.putExtra("shopid", arrShop[pos].getId());
					intent.putExtra("shopname", arrShop[pos].getName());
					intent.putExtra("shopaddress", arrShop[pos].getAddress());
					intent.putExtra("lat", arrShop[pos].getLat());
					intent.putExtra("long", arrShop[pos].getLng());
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("Shop Detail " + TabGroup1Activity.intentCount, intent);
					TabGroup1Activity.intentCount++;
				}

			}
		});

	}
	@Override
	public void onResume(){
		super.onResume();
		Container.btn1.setVisibility(View.GONE);
		Container.btn2.setVisibility(View.GONE);
		Container.btn3.setVisibility(View.GONE);
		Container.map.setVisibility(View.GONE);
	}
	
    @Override
    public void onBackPressed() {
    	Container.search.setEnabled(true);
    }

    public boolean validate(String shop){
    	if (shop.length() < 2){
    		return false;
    	}
    	return true;
    }
	
	@Override
	public void onClick(View v)
	{
		String searchStr = txtSearch.getText().toString().trim();
		boolean passed = validate(searchStr);
		if (v == btnSearch)
		{
			TestingClass.setStartTime();
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
					if (passed){
						getProduct(searchStr, "Product", false);
						isProduct = true;
					}else{
						promptError();
					}
					break;
				case R.id.radio1:
					if (passed){
						getProduct(searchStr, "Shop", false);
						isProduct = false;
					}else{
						promptError();
					}
					break;
				}
			}
			TestingClass.setEndTime();
			Log.d("Search", Long.toString(TestingClass.calculateTime()));
		}
		else if (v == mapview)
		{
			Intent intent = new Intent(getParent(), MapResult.class);
			intent.putExtra("search", true);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Map Result " + TabGroup1Activity.intentCount, intent);
			TabGroup1Activity.intentCount++;
		}
	}
	
    private void promptError(){
	     AlertDialog.Builder dialog=new AlertDialog.Builder(getParent());
	        dialog.setTitle("Please search using at least 2 characters.");

	        dialog.setNeutralButton("OK",new android.content.DialogInterface.OnClickListener(){
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	            }});
	        dialog.show();
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
		// parsing data
		int ct_id;
		String ct_name;
		shoplist.clear();
		try
		{
			mapview.setEnabled(true);
			mapview.setTextColor(Color.parseColor("#FFFFFF"));
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
					arrPro[i].setFilename(json_data.getString("title"));
					arrPro[i].setUrl("");
					
					JSONArray jextraArr = new JSONArray(json_data.getString("extra_fields"));
					JSONObject jextraObjPercent = jextraArr.getJSONObject(3);
					String discountPercent = jextraObjPercent.getString("value");
					arrPro[i].setPercentdiscount(discountPercent);
					tempShop[i].setName(json_data.getString("name"));
					
					Shop shopResult = new Shop(json_data.getInt("id"), json_data.getString("address"), json_data.getString("name"), json_data.getString("lat"), json_data.getString("lng"));
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
					arrShop[i].setLat((String) json_data.get("lat"));
					arrShop[i].setLng((String) json_data.get("lng"));

					Shop shopResult = new Shop(json_data.getInt("id"), json_data.getString("address"), json_data.getString("name"), json_data.getString("lat"), json_data.getString("lng"));
					shoplist.add(shopResult);
				}
				adapter = new SimpleLazyAdapter(this, arrShop);
			}
			searchresultslbl.setVisibility(View.VISIBLE);
			searchResult.setAdapter(adapter);
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "No products Found", Toast.LENGTH_LONG).show();
			mapview.setEnabled(false);
			mapview.setTextColor(Color.parseColor("#000000"));
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}

	}

}
