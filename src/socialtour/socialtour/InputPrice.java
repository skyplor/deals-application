package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class InputPrice extends Activity implements OnClickListener, RadioGroup.OnCheckedChangeListener{
	private TextView txtInput;
	private EditText txtInput2, txtNameInput2, txtSecondInput;
	private ImageView percentImage;
	private Button btnSubmitdeal;
	private RadioGroup radGrp;
	boolean isPercent = true;
	Uri imageUri;
	ImageView addplace, backtomain;
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.step5);
		
		Container.btn1.setVisibility(View.INVISIBLE);
        Container.btn2.setImageResource(R.drawable.quitsharing);
        Container.btn3.setImageResource(R.drawable.addplace);
		backtomain = Container.btn2;
		addplace = Container.btn3;
		
		txtInput = (TextView) findViewById(R.id.txtInput);
		txtInput2 = (EditText) findViewById(R.id.txtPrice);
		txtSecondInput = (EditText) findViewById(R.id.txtPrice2);
		txtNameInput2 = (EditText) findViewById(R.id.txtNameInput2);
		btnSubmitdeal = (Button) findViewById(R.id.submitdealbutton);
		radGrp = (RadioGroup) findViewById(R.id.radSelection);
		
		btnSubmitdeal.setOnClickListener(this);
		radGrp.setOnCheckedChangeListener(this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Container.btn1.setVisibility(View.INVISIBLE);
        Container.btn2.setImageResource(R.drawable.quitsharing);
        Container.btn3.setImageResource(R.drawable.addplace);
		backtomain = Container.btn2;
		addplace = Container.btn3;
		backtomain.setOnClickListener(this);
		addplace.setOnClickListener(this);
	}
	
    @Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
    	if (arg0 == radGrp){
		if (checkedId == R.id.radPercent){
			txtInput.setText("Percentage Discount?");
			isPercent = true;
		}else if (checkedId == R.id.radOriginal){
			txtInput.setText("Original Price?");
			isPercent = false;
		}
    	}
	}
	
	@Override
	public void onClick(View v) {
		if(v==btnSubmitdeal) {
			String name = txtNameInput2.getText().toString().trim() + ".jpg";
			doFileUpload(name);
			if (name.contains(" ")){
				name = name.replace(" ", "%20");
			}
			double dis,ori,percentage;
			dis = Float.parseFloat(txtInput2.getText().toString());
        	if (isPercent){
        		percentage = Double.parseDouble(txtSecondInput.getText().toString());
        		ori = (100 / (100 - percentage)) * dis;
        	}else{
        		ori = Integer.parseInt(txtSecondInput.getText().toString());
        		percentage = (int) Math.round((Math.abs(dis - ori) / ori) * 100);
        		//calculate percent discount
        	}
        	String percentageStr = Double.toString(percentage);
        	String disStr = Double.toString(dis);
        	String oriStr = Double.toString(ori);
        	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        	
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

        	nameValuePairs.add(new BasicNameValuePair("userid",userid));
        	nameValuePairs.add(new BasicNameValuePair("usertype",usertype));
        	nameValuePairs.add(new BasicNameValuePair("filename",txtNameInput2.getText().toString() + ".jpg"));
        	nameValuePairs.add(new BasicNameValuePair("sourcepath",Constants.UPLOAD_PATH));
        	nameValuePairs.add(new BasicNameValuePair("url",Constants.CONNECTIONSTRING + "FYP/uploads/" + name));
        	nameValuePairs.add(new BasicNameValuePair("type","JPG"));
        	int placeid = getIntent().getIntExtra("EMPLOYEE_ID",0);
        	nameValuePairs.add(new BasicNameValuePair("place_id",Integer.toString(placeid)));
        	nameValuePairs.add(new BasicNameValuePair("dprice",disStr));
        	nameValuePairs.add(new BasicNameValuePair("category",getIntent().getStringExtra("category")));
        	nameValuePairs.add(new BasicNameValuePair("subcategory",getIntent().getStringExtra("subcategory")));
        	nameValuePairs.add(new BasicNameValuePair("percentdiscount",percentageStr));
        	nameValuePairs.add(new BasicNameValuePair("oprice",oriStr));
        	
        	//http post
        	try{
        	     HttpClient httpclient = new DefaultHttpClient();
        	     //HttpPost httppost = new HttpPost("http://172.22.177.204/FYP/insert.php");
        	     
        	     HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "insert.php");
        	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	     
        	     ResponseHandler<String> responseHandler=new BasicResponseHandler();
        	     String response = httpclient.execute(httppost, responseHandler);
        	     //HttpResponse response = httpclient.execute(httppost);
        	               	     
        	     //HttpEntity entity = response.getEntity();
        	     //is = entity.getContent();
        	     int lastid = Integer.parseInt(response);
        	     acknowledge(lastid);
        	     /*
        	     Intent i = new Intent("socialtour.socialtour.PRODUCTDETAIL");
        	     i.putExtra("lastproductid", lastid);
 		         startActivity(i);*/
 		         
 		         
        	
        	}catch(Exception e){
    	         Log.e("log_tag", "Error in http connection"+e.toString());
    	    }
		}else if (v==addplace){
			Intent i = new Intent(getParent(), Addplace.class);
	     	Bundle bundle=getIntent().getExtras();
	     	Uri pic = (Uri) bundle.get("pic");
	     	i.putExtra("pic", pic);
	     	TabGroupActivity parentActivity = (TabGroupActivity)getParent();
	     	parentActivity.startChildActivity("Add Place", i);
		}else if (v==backtomain){
			confirmationquit();
		}
	}
	
	private void doFileUpload(String productname){
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        //String existingFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mypic.png";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String responseFromServer = "";
        //String urlString = "http://172.22.177.204/FYP/upload.php";
        String urlString = Constants.CONNECTIONSTRING + "upload.php";
        try
        {
         //------------------ CLIENT REQUEST
        Bundle bundle=getIntent().getExtras();
    	imageUri = (Uri) bundle.get("pic");
        String pathinsd = "";
    	if (imageUri.toString().startsWith("file")){
    		pathinsd = imageUri.toString().substring(7);
    	}else{
    		String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(imageUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            pathinsd= cursor.getString(column_index);
    	}
    	int lastind = pathinsd.lastIndexOf("/");
    	String directoryname = pathinsd.substring(0, lastind+1);
        File currentfile = new File(pathinsd);
        //boolean passed = currentfile.renameTo(new File(existingFileName + productname));
        //if (passed){
        //	currentfile = new File(existingFileName + productname);
        //}
        FileInputStream fileInputStream = new FileInputStream(currentfile);
         // open a URL connection to the Servlet
         URL url = new URL(urlString);
         // Open a HTTP connection to the URL
         conn = (HttpURLConnection) url.openConnection();
         // Allow Inputs
         conn.setDoInput(true);
         // Allow Outputs
         conn.setDoOutput(true);
         // Don't use a cached copy.
         conn.setUseCaches(false);
         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
         dos = new DataOutputStream( conn.getOutputStream() );
         dos.writeBytes(twoHyphens + boundary + lineEnd);
         dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + directoryname + productname + "\"" + lineEnd);
         dos.writeBytes(lineEnd);
         // create a buffer of maximum size
         bytesAvailable = fileInputStream.available();
         bufferSize = Math.min(bytesAvailable, maxBufferSize);
         buffer = new byte[bufferSize];
         // read file and write it into form...
         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         while (bytesRead > 0)
         {
          dos.write(buffer, 0, bufferSize);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         }
         // send multipart form data necesssary after file data...
         dos.writeBytes(lineEnd);
         dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
         // close streams
         Log.e("Debug","File is written");
         
         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
  	       StringBuilder sb = new StringBuilder();
  	       sb.append(reader.readLine() + "\n");
  	       String line="0";
  	       while ((line = reader.readLine()) != null) {
  	                      sb.append(line + "\n");
  	        }
  	     String result2=sb.toString();
         
         fileInputStream.close();
         dos.flush();
         dos.close();
        }
        catch (MalformedURLException ex)
        {
             Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
        catch (IOException ioe)
        {
             Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }
        //------------------ read the SERVER RESPONSE
        try {
              inStream = new DataInputStream ( conn.getInputStream() );
              String str;
             
              while (( str = inStream.readLine()) != null)
              {
                   Log.e("Debug","Server Response "+str);
              }
              inStream.close();

        }
        catch (IOException ioex){
             Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
      }
    
    private void acknowledge(int lastid){
    	final int lastid2 = lastid;
	     AlertDialog.Builder dialog=new AlertDialog.Builder(getParent());
	        dialog.setTitle("Your deal has been shared.");

	        dialog.setNeutralButton("OK",new android.content.DialogInterface.OnClickListener(){
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                Intent i = new Intent(getParent(), Productdetail.class);
	 		         i.putExtra("lastproductid", lastid2);
	 	     		 TabGroupActivity parentActivity = (TabGroupActivity)getParent();
	 	     		 parentActivity.startChildActivity("Product Detail", i);
	            }});
	        dialog.show();
    }
    
    private void confirmationquit(){
	     AlertDialog.Builder dialog=new AlertDialog.Builder(getParent());
	        dialog.setTitle("You are in the midst of sharing. Quit Sharing?");
	        
	        dialog.setPositiveButton("OK",new android.content.DialogInterface.OnClickListener(){
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                Intent i = new Intent(getParent(), TabGroup2Activity.class);
	   		     	TabGroupActivity parentActivity = (TabGroupActivity)getParent();
	   		     	parentActivity.startChildActivity("Back to Main", i);
	                
	            }});
	        dialog.setNeutralButton("Cancel",new android.content.DialogInterface.OnClickListener(){
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();               
	            }});
	        dialog.show();
 }
}
