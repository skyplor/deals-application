package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

import socialtour.socialtour.models.TestingClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class InputPrice extends Activity implements OnClickListener, RadioGroup.OnCheckedChangeListener
{
	private TextView txtInput;
	private EditText txtInput2, txtNameInput2, txtSecondInput;
	private ImageView percentImage;
	private Button btnSubmitdeal;
	private RadioGroup radGrp;
	boolean isPercent = true;
	Uri imageUri;
	String percentageStr = "";
	String disStr = "";
	String oriStr = "";
	String shopName = "";
	String shopAddress = "";
	String shopLat = "";
	String shopLng = "";
	ProgressDialog progress;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.step5);

		Container.btn1.setVisibility(View.INVISIBLE);
		Container.btn2.setVisibility(View.INVISIBLE);
		Container.btn3.setVisibility(View.INVISIBLE);
		Container.map.setVisibility(View.INVISIBLE);
		
		txtInput = (TextView) findViewById(R.id.txtInput);
		txtInput2 = (EditText) findViewById(R.id.txtPrice);
		txtSecondInput = (EditText) findViewById(R.id.txtPrice2);
		txtNameInput2 = (EditText) findViewById(R.id.txtNameInput2);
		btnSubmitdeal = (Button) findViewById(R.id.submitdealbutton);
		radGrp = (RadioGroup) findViewById(R.id.radSelection);
		percentImage = (ImageView) findViewById(R.id.percentimage);

		btnSubmitdeal.setOnClickListener(this);
		radGrp.setOnCheckedChangeListener(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Container.btn1.setVisibility(View.INVISIBLE);
		Container.btn2.setVisibility(View.INVISIBLE);
		Container.btn3.setVisibility(View.INVISIBLE);
		Container.map.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId)
	{
		if (arg0 == radGrp)
		{
			if (checkedId == R.id.radPercent)
			{
				txtInput.setText("Percentage Discount?");
				isPercent = true;
				percentImage.setVisibility(View.VISIBLE);
			}
			else if (checkedId == R.id.radOriginal)
			{
				txtInput.setText("Original Price?");
				isPercent = false;
				percentImage.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		boolean proceed = true;
		if (v == btnSubmitdeal)
		{
			runDialog(5);
			try{
			TestingClass.setStartTime();
			String oldname = txtNameInput2.getText().toString().trim() + ".jpg";
			String name = "";
			if (oldname.contains(" "))
			{
				name = oldname.replace(" ", "%20");
			}
			double dis, ori;
			int percentage;
			dis = Float.parseFloat(txtInput2.getText().toString());
			if (isPercent)
			{
				percentage = Integer.parseInt(txtSecondInput.getText().toString());
				ori = (100.0 / (100.0 - percentage)) * dis;
				if (percentage != Math.round(percentage) || (percentage < 0 || percentage > 100))
				{
					promptError("Please enter an integer value between 0 to 100 for percentage discount");
					proceed = false;
				}
			}
			else
			{
				ori = Double.parseDouble(txtSecondInput.getText().toString());
				if (dis >= ori)
				{
					promptError("Discounted price cannot be more than original price");
					proceed = false;
				}
				percentage = (int) Math.round((Math.abs(dis - ori) / ori) * 100.0);
				// calculate percent discount
			}
			if (proceed)
			{
				percentageStr = Integer.toString(percentage) + "%";
				DecimalFormat twoDForm = new DecimalFormat("#.##");
				dis = Double.valueOf(twoDForm.format(dis));
				ori = Double.valueOf(twoDForm.format(ori));
				disStr = "$" + Double.toString(dis);
				oriStr = "$" + Double.toString(ori);

				if (disStr.substring(disStr.indexOf(".") + 1).length() == 1)
				{
					disStr = disStr + "0";
				}

				if (oriStr.substring(oriStr.indexOf(".") + 1).length() == 1)
				{
					oriStr = oriStr + "0";
				}
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				SharedPreferences userDetails = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
				String userid = "", usertype = "", username = "";
				if (!userDetails.getString("userID", "").equals(""))
				{
					userid = userDetails.getString("userID", "");
				}
				
				nameValuePairs.add(new BasicNameValuePair("title", txtNameInput2.getText().toString()));
				String alias = txtNameInput2.getText().toString().trim().toLowerCase().replace(" ", "-");
				nameValuePairs.add(new BasicNameValuePair("alias", alias));
				nameValuePairs.add(new BasicNameValuePair("category", getIntent().getStringExtra("category")));
				nameValuePairs.add(new BasicNameValuePair("subcategory", getIntent().getStringExtra("subcategory")));
				nameValuePairs.add(new BasicNameValuePair("published", "1"));
				nameValuePairs.add(new BasicNameValuePair("introtext", ""));
				nameValuePairs.add(new BasicNameValuePair("fulltext", ""));
				shopName = getIntent().getStringExtra("SHOP_NAME");
				shopAddress = getIntent().getStringExtra("SHOP_ADDRESS");
				shopLat = getIntent().getStringExtra("SHOPLAT");
				shopLng = getIntent().getStringExtra("SHOPLNG");

				JSONObject extraObj = new JSONObject();
				JSONArray extraArray = new JSONArray();
				for (int i = 0; i < 5; i++)
				{
					try
					{
						extraObj = new JSONObject();
						extraObj.put("value", chooseExtraFieldValue(i + 1));
						extraObj.put("id", Integer.toString(i + 1));
						extraArray.put(extraObj);
						Log.d("In input Price, extra fields object: ", extraObj.toString());
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				nameValuePairs.add(new BasicNameValuePair("shop_id", Integer.toString(getIntent().getExtras().getInt(("SHOP_ID")))));
				nameValuePairs.add(new BasicNameValuePair("extra_fields", extraArray.toString()));
				nameValuePairs.add(new BasicNameValuePair("extra_fields_search", oriStr + " " + shopName + " " + shopAddress + " " + percentageStr + " " + disStr));
				Date now = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String formattedDate = sdf.format(now);
				nameValuePairs.add(new BasicNameValuePair("created", formattedDate));
				if(userid.startsWith("["))
				{
					userid = userid.substring(userid.indexOf("[")+2, userid.indexOf("]")-1);
				}
				nameValuePairs.add(new BasicNameValuePair("created_by", userid));
				nameValuePairs.add(new BasicNameValuePair("publish_up", formattedDate));
				nameValuePairs.add(new BasicNameValuePair("trash", "0"));
				nameValuePairs.add(new BasicNameValuePair("access", "1"));
				nameValuePairs.add(new BasicNameValuePair("featured", "0"));
				nameValuePairs.add(new BasicNameValuePair("featured_ordering", "0"));
				nameValuePairs.add(new BasicNameValuePair("hits", "0"));
				nameValuePairs.add(new BasicNameValuePair("params", paramJsonEncode(1)));
				nameValuePairs.add(new BasicNameValuePair("metadata", "robots=\nauthor="));
				nameValuePairs.add(new BasicNameValuePair("plugins", paramJsonEncode(2)));
				nameValuePairs.add(new BasicNameValuePair("language", "*"));
				// http post
				try
				{
					HttpClient httpclient = new DefaultHttpClient();

					HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "insert.php");
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String response = httpclient.execute(httppost, responseHandler);
					int lastid = Integer.parseInt(response);
					Log.d("in InputPrice, lastid: ", Integer.toString(lastid));
					doFileUpload(name, lastid);
					TestingClass.setEndTime();
					Log.d("Sharing a deal completed", Long.toString(TestingClass.calculateTime()));
					acknowledge(lastid);

				}
				catch (Exception e)
				{
					Log.e("log_tag", "Error in http connection" + e.toString());
				}
			}
		}catch (NumberFormatException e){
			promptError("Please enter an integer value between 0 to 100 for percentage discount");
		}
	}
	}

	private void runDialog(final int seconds)
	{
	    	progress = ProgressDialog.show(getParent(), "", "Uploading your deal...");

	    	new Thread(new Runnable(){
	    		public void run(){
	    			try {
				                Thread.sleep(seconds * 1000);
						progress.dismiss();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    	}).start();
	}
	
	private String paramJsonEncode(int type)
	{
		// TODO Auto-generated method stub
		JSONObject paramObject = new JSONObject();
		try
		{
			if (type == 1)
			{
				paramObject.put("itemK2Plugins", "");
				paramObject.put("itemRelatedImageGallery", "");
				paramObject.put("itemRelatedMedia", "");
				paramObject.put("itemRelatedAuthor", "");
				paramObject.put("itemRelatedFulltext", "");
				paramObject.put("itemRelatedIntrotext", "");
				paramObject.put("itemRelatedImageSize", "");
				paramObject.put("itemRelatedCategory", "");
				paramObject.put("itemRelatedTitle", "");
				paramObject.put("itemRelatedLimit", "");
				paramObject.put("itemRelated", "");
				paramObject.put("itemAuthorLatestLimit", "");
				paramObject.put("itemAuthorLatest", "");
				paramObject.put("itemAuthorEmail", "");
				paramObject.put("itemAuthorURL", "");
				paramObject.put("itemAuthorDescription", "");
				paramObject.put("itemAuthorImage", "");
				paramObject.put("itemAuthorBlock", "");
				paramObject.put("itemGooglePlusOneButton", "");
				paramObject.put("itemFacebookButton", "");
				paramObject.put("itemTwitterButton", "");
				paramObject.put("itemComments", "");
				paramObject.put("itemNavigation", "");
				paramObject.put("itemImageGallery", "");
				paramObject.put("itemVideoCredits", "");
				paramObject.put("itemVideoCaption", "");
				paramObject.put("itemVideoAutoPlay", "");
				paramObject.put("itemAudioHeight", "");
				paramObject.put("itemAudioWidth", "");
				paramObject.put("itemVideoHeight", "");
				paramObject.put("itemVideoWidth", "");
				paramObject.put("itemVideo", "");
				paramObject.put("itemAttachmentsCounter", "");
				paramObject.put("itemAttachments", "");
				paramObject.put("itemTags", "");
				paramObject.put("itemCategory", "");
				paramObject.put("itemHits", "");
				paramObject.put("itemDateModified", "");
				paramObject.put("itemExtraFields", "");
				paramObject.put("itemFullText", "");
				paramObject.put("itemIntroText", "");
				paramObject.put("itemImageMainCredits", "");
				paramObject.put("itemImageMainCaption", "");
				paramObject.put("itemImgSize", "");
				paramObject.put("itemImage", "");
				paramObject.put("itemRating", "");
				paramObject.put("itemCommentsAnchor", "");
				paramObject.put("itemImageGalleryAnchor", "");
				paramObject.put("itemVideoAnchor", "");
				paramObject.put("itemSocialButton", "");
				paramObject.put("itemEmailButton", "");
				paramObject.put("itemPrintButton", "");
				paramObject.put("itemFontResizer", "");
				paramObject.put("itemAuthor", "");
				paramObject.put("itemFeaturedNotice", "");
				paramObject.put("itemTitle", "");
				paramObject.put("itemDateCreated", "");
				paramObject.put("catItemK2Plugins", "");
				paramObject.put("catItemCommentsAnchor", "");
				paramObject.put("catItemReadMore", "");
				paramObject.put("catItemDateModified", "");
				paramObject.put("catItemImageGallery", "");
				paramObject.put("catItemVideoAutoPlay", "");
				paramObject.put("catItemAudioHeight", "");
				paramObject.put("catItemAudioWidth", "");
				paramObject.put("catItemVideoHeight", "");
				paramObject.put("catItemVideoWidth", "");
				paramObject.put("catItemVideo", "");
				paramObject.put("catItemAttachmentsCounter", "");
				paramObject.put("catItemAttachments", "");
				paramObject.put("catItemTags", "");
				paramObject.put("catItemCategory", "");
				paramObject.put("catItemHits", "");
				paramObject.put("catItemExtraFields", "");
				paramObject.put("catItemIntroText", "");
				paramObject.put("catItemImage", "");
				paramObject.put("catItemRating", "");
				paramObject.put("catItemDateCreated", "");
				paramObject.put("catItemAuthor", "");
				paramObject.put("catItemFeaturedNotice", "");
				paramObject.put("catItemTitleLinked", "");
				paramObject.put("catItemTitle", "");
			}
			else if (type == 2)
			{
				paramObject.put("k2GmapGmapItemLat", shopLat);
				paramObject.put("k2GmapGmapItemLon", shopLng);
				paramObject.put("k2GmapGmapItemzoom", "13");
				paramObject.put("k2GmapGmapItemWidth", "350");
				paramObject.put("k2GmapGmapItemHeight", "350");
				paramObject.put("k2GmapGmapItemIcon", "pansiyon");
				paramObject.put("k2GmapGmapItemkmz", "");
			}
			return paramObject.toString();

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private String chooseExtraFieldValue(int i)
	{
		// TODO Auto-generated method stub
		switch (i)
		{
		case 1:
			return oriStr;
		case 2:
			return shopName;
		case 3:
			return shopAddress;
		case 4:
			return percentageStr;
		case 5:
			return disStr;
		}
		return "";
	}

	private void doFileUpload(String productname, int articleID)
	{
		Log.d("In inputPrice, doFileupload articleID: ", Integer.toString(articleID));
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String responseFromServer = "";
		String urlString = Constants.CONNECTIONSTRING + "upload.php";
		try
		{
			// ------------------ CLIENT REQUEST
			Bundle bundle = getIntent().getExtras();
			imageUri = (Uri) bundle.get("pic");
			String pathinsd = "";
			if (imageUri.toString().startsWith("file"))
			{
				pathinsd = imageUri.toString().substring(7);
				Log.d("in Input Price, imageURI: ", imageUri.toString());
			}
			else
			{
				String[] proj =
				{ MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(imageUri, proj, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				pathinsd = cursor.getString(column_index);
				Log.d("in Input Price, imageURI else: ", imageUri.toString());
			}
			String hashedimagename = "";
			try
			{
				hashedimagename = MungPass("Image" + Integer.toString(articleID)) + ".jpg";
			}
			catch (NoSuchAlgorithmException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int lastind = pathinsd.lastIndexOf("/");
			String directoryname = pathinsd.substring(0, lastind + 1);
			File currentfile = new File(pathinsd);
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
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + directoryname + hashedimagename + "\"" + lineEnd);
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
			Log.e("Debug", "File is written");

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line = "0";
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			String result2 = sb.toString();
			Log.d("In InputPrice: ", result2);

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
		// ------------------ read the SERVER RESPONSE
		try
		{
			inStream = new DataInputStream(conn.getInputStream());
			String str;

			while ((str = inStream.readLine()) != null)
			{
				Log.e("Debug", "Server Response " + str);
			}
			inStream.close();

		}
		catch (IOException ioex)
		{
			Log.e("Debug", "error: " + ioex.getMessage(), ioex);
		}
	}

	private void acknowledge(int lastid)
	{
		final int lastid2 = lastid;
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle("Your deal has been shared.");

		dialog.setNeutralButton("OK", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
	           i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	           startActivity(i);
			}
		});
		dialog.show();
	}

	private void confirmationquit()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle("You are in midst of Sharing. Quit Sharing?");

		dialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);

			}
		});
		dialog.setNeutralButton("Cancel", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void promptError(String errormsg)
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle(errormsg);

		dialog.setNeutralButton("OK", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private static String MungPass(String pass) throws NoSuchAlgorithmException
	{
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.reset();
		byte[] data = pass.getBytes();
		m.update(data);
		BigInteger i = new BigInteger(1, m.digest());
		String result = String.format("%1$032X", i);
		return result.toLowerCase();
	}
}
