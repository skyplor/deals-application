package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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

import socialtour.socialtour.models.Shop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Productdetail extends Activity implements OnClickListener{
	InputStream is = null;
	StringBuilder sb=null;
	JSONArray jArray;
	String result = null;
	Bitmap bmImg;
	ImageView imagedisplay;
	Button btnLike, btnDislike, btnShare, btnBack;
	int productid = 0;
	TextView lbllikes,lbldislikes,lblbrand,lblcategory,lblproduct,lblshop,lbladdress,lblpercent,lblprice;
	GlobalVariable globalVar;
	public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.productdetail);
		Bundle bundle=getIntent().getExtras();
        productid = (Integer) bundle.get("lastproductid");
		String filename = importData(productid);
		downloadFile(filename);
		btnLike = (Button)findViewById(R.id.btnlike);
		btnDislike = (Button)findViewById(R.id.btnDislike);
		btnShare = (Button)findViewById(R.id.btnShare);
		btnBack = (Button)findViewById(R.id.btnback);
		
		btnLike.setOnClickListener(this);
		btnDislike.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnBack.setOnClickListener(this);
	}
	
   /* @Override
    protected void onPause() {
    super.onPause();

    unbindDrawables(findViewById(R.id.productdetailLayout));
    System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
        ((ViewGroup) view).removeAllViews();
        }
    }*/
	
	private String importData(int lastinsertedid){
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("productid",Integer.toString(lastinsertedid)));
    	//http post
    	try{
    	     HttpClient httpclient = new DefaultHttpClient();
    	     //HttpPost httppost = new HttpPost("http://172.22.177.204/FYP/database.php");
    	     HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "database.php");
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
    	//paring data
    	//int ct_id;
    	String category = "", productname = "", shopname = "", brand = "",address = "";
    	int likes = 0,dislikes = 0,percent = 0;
    	double dprice=0;
    	try{
    	      jArray = new JSONArray(result);
    	      JSONObject json_data=null;
    	      for(int i=0;i<jArray.length();i++){
    	             json_data = jArray.getJSONObject(i);
    	             likes=json_data.getInt("likes");
    	             dislikes=json_data.getInt("dislikes");
    	             percent=json_data.getInt("percentdiscount");
    	             category=json_data.getString("category");
    	             productname=json_data.getString("filename");
    	             shopname=json_data.getString("name");
    	             address=json_data.getString("address");
    	             brand=json_data.getString("brand");
    	             dprice = json_data.getDouble("dprice");
    	         }
    	      }
    	      catch(JSONException e1){
    	    	  Toast.makeText(getBaseContext(), "Error! No JSON Record for this entry" ,Toast.LENGTH_LONG).show();
    	      } catch (ParseException e1) {
    				e1.printStackTrace();
    		}
    	      
    	      lbllikes = (TextView) findViewById(R.id.lblLikes);
    	      lbldislikes = (TextView) findViewById(R.id.lblDislikes);
    	      lblbrand = (TextView) findViewById(R.id.lblBrand);
    	      lblcategory = (TextView) findViewById(R.id.lblCategory);
    	      lblproduct = (TextView) findViewById(R.id.lblProductname);
    	      lblshop = (TextView) findViewById(R.id.shopname);
    	      lbladdress = (TextView) findViewById(R.id.lblshopaddress);
    	      lblpercent = (TextView) findViewById(R.id.lblPercent);
    	      lblprice = (TextView) findViewById(R.id.lblPrice);
    	      
		lblshop.setOnClickListener(this);
		lbladdress.setOnClickListener(this);
    	      lbllikes.setText(Integer.toString(likes) + " likes");
    	      lbldislikes.setText(Integer.toString(dislikes) + " dislikes");
    	      lblbrand.setText(brand);
    	      lblcategory.setText(category);
    	      lblproduct.setText(productname);
    	      lblshop.setText(shopname);
    	      lbladdress.setText(address);
    	      lblpercent.setText(Integer.toString(percent));
    	      lblprice.setText(Double.toString(dprice));
    	      return productname;
	}

	void downloadFile(String filename){

		URL myFileUrl =null; 
		try {
			//myFileUrl= new URL("http://172.22.177.204/FYP/FYP/uploads/" + filename);
		myFileUrl= new URL(Constants.CONNECTIONSTRING + "FYP/uploads/" + filename);
		} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		try {
		HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
		conn.setDoInput(true);
		conn.connect();
		int length = conn.getContentLength();
		int[] bitmapData =new int[length];
		byte[] bitmapData2 =new byte[length];
		InputStream is = conn.getInputStream();
		
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 8;
		bmImg = BitmapFactory.decodeStream(is, null, options);
		
		//bmImg = BitmapFactory.decodeStream(is);
		imagedisplay =(ImageView) findViewById(R.id.imgProduct);
		imagedisplay.setImageBitmap(null);
		imagedisplay.setImageBitmap(bmImg);
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		}
	
	@Override
	public void onClick(View v) {
		TextView lbllikes,lbldislikes;
		if(v==btnLike) {
			lbllikes = (TextView) findViewById(R.id.lblLikes);
			int numberlikes = Integer.parseInt(lbllikes.getText().toString().substring(0, 1));
			numberlikes++;
			lbllikes.setText(Integer.toString(numberlikes) + " likes");
			updateComments("likes", productid ,numberlikes);
			btnLike.setEnabled(false);
			btnDislike.setEnabled(false);
			
			
		} else if(v==btnDislike) {
			lbldislikes = (TextView) findViewById(R.id.lblDislikes);
			int numberdislikes = Integer.parseInt(lbldislikes.getText().toString().substring(0, 1));
			numberdislikes++;
			lbldislikes.setText(Integer.toString(numberdislikes) + " dislikes");
			updateComments("dislikes", productid ,numberdislikes);
			btnLike.setEnabled(false);
			btnDislike.setEnabled(false);
		}
		else if (v == btnShare)
		{
			Intent shareIntent = new Intent(getParent(), ProductPage.class);
			TabGroupActivity parentActivity = (TabGroupActivity)getParent();
			parentActivity.startChildActivity("Product Page", shareIntent);
			startActivity(shareIntent);
		}
		else if (v == lblshop || v == lbladdress)
		{
			ConnectDB connect = new ConnectDB(lbladdress.getText().toString(), lblshop.getText().toString());
			List<Shop> shop = connect.getShop();
			globalVar = (GlobalVariable) getApplicationContext();
			globalVar.setShop(shop);
			Intent shopIntent = new Intent(getParent(), Shopdetail.class);
			TabGroupActivity parentActivity = (TabGroupActivity)getParent();
     		parentActivity.startChildActivity("Shop Detail", shopIntent);
		}
		else if(v == btnBack)
		{
			Intent i = new Intent(getParent(), Container.class);
			startActivity(i);
		}
	}
	
	private void updateComments(String type, int id, int numberComments){
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id",Integer.toString(id)));
		nameValuePairs.add(new BasicNameValuePair("comments",Integer.toString(numberComments)));
		if (type.equals("likes")){
			nameValuePairs.add(new BasicNameValuePair("type","likes"));
		}else if (type.equals("dislikes")){
			nameValuePairs.add(new BasicNameValuePair("type","dislikes"));
		}
    	try{
   	     HttpClient httpclient = new DefaultHttpClient();
   	     HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "update.php");
   	     
   	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
   	     
   	     ResponseHandler<String> responseHandler=new BasicResponseHandler();
   	     String response = httpclient.execute(httppost, responseHandler);
   	     httppost = new HttpPost(Constants.CONNECTIONSTRING + "insertcomments.php");
   	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	     
	     responseHandler=new BasicResponseHandler();
	     response = httpclient.execute(httppost, responseHandler);
   	 
   	}catch(Exception e){
	         Log.e("log_tag", "Error in http connection"+e.toString());
	    }
	}

}
