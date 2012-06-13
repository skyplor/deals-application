package com.ntu.dealsinterest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import com.ntu.dealsinterest.models.Shop;

import com.ntu.dealsinterest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class Browseplace extends Activity implements OnClickListener{
        
		Shop[] shop;
		SimpleLazyAdapter adapter;
        JSONArray jArray;
        String result = null;
        InputStream is = null;
        StringBuilder sb=null;
        Button search;
        ImageView addplace, backtomain;
        ListView list;
        float coordinates[] = {0,0};
        protected Cursor cursor;
        //protected ListAdapter adapter;
        
        double latitude = 0;
        double longitude = 0;
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeselection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Container.btn1.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setVisibility(ImageView.VISIBLE);
        Container.btn3.setVisibility(ImageView.INVISIBLE);
		Container.map.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setImageResource(R.drawable.addplace);
        Container.btn2.getLayoutParams().width = 85;
		addplace = Container.btn2;
        search = (Button) findViewById(R.id.searchButton);
        list = (ListView) findViewById(R.id.list);
        addplace.setOnClickListener(this);
        
        Bundle bundle=getIntent().getExtras();
        Uri photoUri = (Uri) bundle.get("pic");
        
        if (bundle !=null){
        	try{
        	ExifInterface exif = new ExifInterface(photoUri.getPath());
        	exif.getLatLong(coordinates);
        	
        	}catch (IOException e){
        		
        	}
        }
              getShop("",true);
            
		search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	EditText shopname = (EditText) findViewById(R.id.searchText);
            	boolean passed = validate(shopname.getText().toString().trim());
            	if (passed){
            		ImageView searchresults = (ImageView) findViewById(R.id.imgsearchresults);
            		searchresults.setImageResource(R.drawable.searchresults);
            		getShop(shopname.getText().toString(), false);
            	}else{
            		promptError();
            	}
            }
        });
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		        
		        Intent intent = new Intent(getParent(), ChooseCategory.class);
		        intent.putExtra("SHOP_ID", shop[pos].getId());
		        intent.putExtra("SHOP_NAME", shop[pos].getName());
		        intent.putExtra("SHOP_ADDRESS", shop[pos].getAddress());
		        Log.d("Shop Lat and Lng: ", shop[pos].getLat() + " and " + shop[pos].getLng() );
		        intent.putExtra("SHOPLAT", shop[pos].getLat());
		        intent.putExtra("SHOPLNG", shop[pos].getLng());
		        Bundle bundle=getIntent().getExtras();
		        Uri pic = (Uri) bundle.get("pic");
		        intent.putExtra("pic", pic);
   		     	TabGroupActivity parentActivity = (TabGroupActivity)getParent();
   		     	parentActivity.startChildActivity("Choose Category " + TabGroup1Activity.intentCount, intent);
   		     	TabGroup1Activity.intentCount++;
		    }
		}); 
    }
        
	@Override
	public void onResume(){
		super.onResume();
		Container.btn1.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setVisibility(ImageView.VISIBLE);
        Container.btn3.setVisibility(ImageView.INVISIBLE);
		Container.map.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setImageResource(R.drawable.addplace);
		addplace = Container.btn2;
        addplace.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v==addplace){
			Intent i = new Intent(getParent(), Addplace.class);
		     	Bundle bundle=getIntent().getExtras();
		     	Uri pic = (Uri) bundle.get("pic");
		     	i.putExtra("pic", pic);
		     	TabGroupActivity parentActivity = (TabGroupActivity)getParent();
		     	parentActivity.startChildActivity("Add Place " + TabGroup1Activity.intentCount, i);
		     	TabGroup1Activity.intentCount++;
		}
	}
	
    public void getShop(String shopname, boolean start){
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	String path = "";
    	if (start){
    		nameValuePairs.add(new BasicNameValuePair("lat",Float.toString(coordinates[0])));
    		nameValuePairs.add(new BasicNameValuePair("long",Float.toString(coordinates[1])));
    		path = Constants.CONNECTIONSTRING + "coordinates.php";
    	}else{
    		nameValuePairs.add(new BasicNameValuePair("shop",shopname));
    		path = Constants.CONNECTIONSTRING + "database.php";
    	}
    	//http post
    	try{
    	     HttpClient httpclient = new DefaultHttpClient();
    	     HttpPost httppost = new HttpPost(path);
    	     
    	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	     HttpResponse response = httpclient.execute(httppost);
    	     HttpEntity entity = response.getEntity();
    	     is = entity.getContent();
    	     }catch(Exception e){
    	         Log.e("log_tag", "Error in http connection"+e.toString());
    	    }
    	//convert response to string
    	try{
    	      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
    	       sb = new StringBuilder();
    	       sb.append(reader.readLine() + "\n");
    	       String line="0";
    	       while ((line = reader.readLine()) != null) {
    	                      sb.append(line + "\n");
    	        }
    	        is.close();
    	        result=sb.toString();
    	        }catch(Exception e){
    	              Log.e("log_tag", "Error converting result "+e.toString());
    	        }
    	try{
    	      jArray = new JSONArray(result);
    	      JSONObject json_data=null;
    	      shop = new Shop[jArray.length()];
    	      for(int i=0;i<jArray.length();i++){
    	             json_data = jArray.getJSONObject(i);
    	             shop[i] = new Shop();
    	             shop[i].setId(json_data.getInt("id"));
    	             shop[i].setName(json_data.getString("name"));
    	             shop[i].setAddress(json_data.getString("address"));
    	             shop[i].setLat(json_data.getString("lat"));
    	             shop[i].setLng(json_data.getString("lng"));
    	         }
    	      	adapter=new SimpleLazyAdapter(this, shop);
    	        ListView employeeList = (ListView) findViewById(R.id.list);
    	        employeeList.setAdapter(adapter);
    	      }
    	      catch(JSONException e1){
    	    	  Toast.makeText(getBaseContext(), "No shop found" ,Toast.LENGTH_LONG).show();
    	      } catch (ParseException e1) {
    				e1.printStackTrace();
    		}
    	}
    
    public boolean validate(String shop){
    	if (shop.length() < 2){
    		return false;
    	}
    	return true;
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
}
