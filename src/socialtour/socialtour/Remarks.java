package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;

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

import com.fedorvlasov.lazylist.RemarksLazyAdapter;
import com.fedorvlasov.lazylist.SimpleLazyAdapter;

import socialtour.socialtour.models.Remark;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class Remarks extends Activity implements OnClickListener{
	
	ListView commentListview;
	Button submitcomments;
	EditText inputComments;
	ImageView img;
	int productid;
	
	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;
	String result = null;
	RemarksLazyAdapter adapter;
	private Remark[] listremarks = null;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remarks);
		
		Bundle bundle = getIntent().getExtras();
		productid = bundle.getInt("productid");
		
		img = (ImageView) findViewById(R.id.imgComments);
		commentListview = (ListView) findViewById(R.id.listComments);
		inputComments = (EditText) findViewById(R.id.txtEditremarks);
		submitcomments = (Button) findViewById(R.id.btnsubmitcomments);
		
		submitcomments.setOnClickListener(this);
		
		importRemarks(productid);
	}
	
	@Override
	public void onClick(View v){
		if (v==submitcomments){
			String remarks = inputComments.getText().toString().trim();
			insertRemarks(remarks);
		}
	}
	
	private void importRemarks(int lastinsertedid)
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("productid", Integer.toString(lastinsertedid)));
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost httppost = new
			// HttpPost("http://172.22.177.204/FYP/database.php");
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "remarks.php");
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
		// paring data
		// int ct_id;
		if (!result.contains("null")){
		try
		{
			jArray = new JSONArray(result);
			JSONObject json_data = null;
			listremarks = new Remark[jArray.length()];
			for (int i = 0; i < jArray.length(); i++)
			{
				json_data = jArray.getJSONObject(i);
				listremarks[i] = new Remark();
				listremarks[i].setId(json_data.getInt("id"));
				listremarks[i].setProductid(json_data.getInt("productid"));
				listremarks[i].setUserid(json_data.getInt("userid"));
				listremarks[i].setDesc(json_data.getString("desc"));
				listremarks[i].setCreated(Date.valueOf(json_data.getString("remark_created")));
			}
			
			adapter = new RemarksLazyAdapter(this, listremarks);
			
			commentListview.setAdapter(adapter);
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "Error! Having trouble reading json results for remarks", Toast.LENGTH_LONG).show();
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}
	}
	}
	
	private void insertRemarks(String remarks)
	{
		SharedPreferences userDetails = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
    	String userid = "",usertype="";
    	if (!userDetails.getString("userID", "").equals("")){
    		userid = userDetails.getString("userID", "");
    		usertype = "user_norm";
    	}else if (!userDetails.getString("userDB_FBID", "").equals("")){
    		userid = userDetails.getString("userDB_FBID", "");
    		usertype = "user_fb";
    	}else if (!userDetails.getString("userDB_TWITID", "").equals("")){
    		userid = userDetails.getString("userDB_TWITID", "");
    		usertype = "user_twit";
    	}
		
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid));
		nameValuePairs.add(new BasicNameValuePair("usertype", usertype));
		nameValuePairs.add(new BasicNameValuePair("productid", Integer.toString(productid)));
		nameValuePairs.add(new BasicNameValuePair("desc", remarks));

		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "insertremarks.php");

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(httppost, responseHandler);

		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error in http connection" + e.toString());
		}
	}
	
	

}
