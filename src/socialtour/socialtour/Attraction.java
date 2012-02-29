package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
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


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Attraction extends Activity implements RadioGroup.OnCheckedChangeListener
{
	protected boolean _taken;
	protected String _path;
	protected ImageButton _image;
	protected Button edit;
	protected TextView _field, lblInput;
	protected RadioGroup inputmode;
	boolean isPercent = true;
	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;
	String result = null;
	protected String[] categories;
	String percentageStr = "";
	String disStr = "";
	String oriStr = "";
	String shopName = "";
	String shopAddress = "";

	protected static final String PHOTO_TAKEN = "photo_taken";
	private static final int CAMERA_PIC_REQUEST = 1337;
	Uri imageUri;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attraction);

		lblInput = (TextView) findViewById(R.id.lblInput);
		Button uploadProduct = (Button) findViewById(R.id.uploadProduct);
		TextView shopname = (TextView) findViewById(R.id.shopname);
		inputmode = (RadioGroup) findViewById(R.id.radioGroup1);
		inputmode.setOnCheckedChangeListener(this);
		edit = (Button) findViewById(R.id.editShop);

		Bundle bundle = getIntent().getExtras();
		imageUri = (Uri) bundle.get("pic");
		Bitmap bitmap = null;
		try
		{
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
		}
		catch (FileNotFoundException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		catch (IOException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// File lala = new File(imageUri);
		// Bitmap bitmap = (Bitmap) bundle.get("pic");
		// _path=Environment.getExternalStorageDirectory().getPath() +
		// "/DCIM/100ANDRO/";
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 4;

		// Bitmap bitmap2 =
		// BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()
		// + "/DCIM/100ANDRO/testing.jpg", options );
		bitmap = Bitmap.createScaledBitmap(bitmap, 132, 96, false);

		_image = (ImageButton) findViewById(R.id.imgPreview);
		_image.setImageBitmap(bitmap);

		// ----------------------------------------------------------------------

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("category", "get"));
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "database.php");
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
		String cat_name;
		try
		{
			jArray = new JSONArray(result);
			JSONObject json_data = null;
			categories = new String[jArray.length()];
			for (int i = 0; i < jArray.length(); i++)
			{
				json_data = jArray.getJSONObject(i);
				// ct_id=json_data.getInt("id");
				cat_name = json_data.getString("categoryname");
				categories[i] = cat_name;
			}
			SpinnerAdapter adapter = new ArrayAdapter<String>(this.getParent(), android.R.layout.simple_spinner_item, categories);
			// adapter.setDropDownViewResource(
			// android.R.layout.simple_spinner_dropdown_item );
			Spinner category = (Spinner) findViewById(R.id.categorylist);
			category.setAdapter(adapter);
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "No category Found", Toast.LENGTH_LONG).show();
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}

		// ----------------------------------------------------------------------
		shopname.setText(getIntent().getStringExtra("EMPLOYEE_NAME"));

		uploadProduct.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				EditText productname = (EditText) findViewById(R.id.txtProductname);
				EditText brand = (EditText) findViewById(R.id.txtBrand);
				EditText dp = (EditText) findViewById(R.id.txtPrice);
				EditText input = (EditText) findViewById(R.id.txtInput);
				double dis, ori, percentage;
				dis = Float.parseFloat(dp.getText().toString());
				if (isPercent)
				{
					percentage = Double.parseDouble(input.getText().toString());
					ori = (100 / (100 - percentage)) * dis;
				}
				else
				{
					ori = Integer.parseInt(input.getText().toString());
					percentage = (int) Math.round((Math.abs(dis - ori) / ori) * 100);
					// calculate percent discount
				}
				percentageStr = Double.toString(percentage);
				disStr = Double.toString(dis);
				oriStr = Double.toString(ori);
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("filename", productname.getText().toString() + ".jpg"));
				nameValuePairs.add(new BasicNameValuePair("sourcepath", Constants.UPLOAD_PATH));
				nameValuePairs.add(new BasicNameValuePair("url", Constants.CONNECTIONSTRING + "joomla/media/k2/items/src/" + DigestUtils.md5("Image") + ".jpg"));
				nameValuePairs.add(new BasicNameValuePair("type", "JPG"));
				int placeid = getIntent().getIntExtra("EMPLOYEE_ID", 0);
				nameValuePairs.add(new BasicNameValuePair("place_id", Integer.toString(placeid)));
				nameValuePairs.add(new BasicNameValuePair("dprice", disStr));
				Spinner categoryspin = (Spinner) findViewById(R.id.categorylist);
				nameValuePairs.add(new BasicNameValuePair("category", categoryspin.getSelectedItem().toString()));
				nameValuePairs.add(new BasicNameValuePair("brand", brand.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("percentdiscount", percentageStr));
				nameValuePairs.add(new BasicNameValuePair("oprice", oriStr));
				// http post
				try
				{
					HttpClient httpclient = new DefaultHttpClient();
					// HttpPost httppost = new
					// HttpPost("http://172.22.177.204/FYP/insert.php");

					HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "insert.php");
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String response = httpclient.execute(httppost, responseHandler);
					// HttpResponse response = httpclient.execute(httppost);

					// HttpEntity entity = response.getEntity();
					// is = entity.getContent();
					int lastid = Integer.parseInt(response);

					doFileUpload(productname.getText().toString().trim() + ".jpg", lastid);
					acknowledge();
					/*
					 * Intent i = new
					 * Intent("socialtour.socialtour.PRODUCTDETAIL");
					 * i.putExtra("lastproductid", lastid); startActivity(i);
					 */

					Intent i = new Intent(getParent(), Productdetail.class);
					i.putExtra("lastproductid", lastid);
					TabGroupActivity parentActivity = (TabGroupActivity) getParent();
					parentActivity.startChildActivity("Product Detail", i);

				}
				catch (Exception e)
				{
					Log.e("log_tag", "Error in http connection" + e.toString());
				}

				// startCameraActivity();
			}
		});

		_image.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				/*
				 * Intent i = new Intent("socialtour.socialtour.STARTCAMERA");
				 * i.putExtra("EMPLOYEE_ID",
				 * getIntent().getIntExtra("EMPLOYEE_NAME",0));
				 * i.putExtra("EMPLOYEE_NAME",
				 * getIntent().getStringExtra("EMPLOYEE_NAME"));
				 * i.putExtra("origin", "productdetails"); startActivity(i);
				 */

				Intent i = new Intent(getParent(), Startcamera.class);
				i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME", 0));
				i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
				i.putExtra("origin", "productdetails");
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("Camera", i);
			}
		});

		edit.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				/*
				 * Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
				 * i.putExtra("origin", "productdetails"); i.putExtra("pic",
				 * imageUri); startActivity(i);
				 */

				Intent i = new Intent(getParent(), Browseplace.class);
				i.putExtra("origin", "productdetails");
				i.putExtra("pic", imageUri);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("Choose Place", i);
			}
		});

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId)
	{
		if (checkedId == R.id.radPercent)
		{
			lblInput.setText("Percent Discount:");
			isPercent = true;
		}
		else if (checkedId == R.id.radOriginal)
		{
			lblInput.setText("Original Price:");
			isPercent = false;
		}
	}

	private void doFileUpload(String productname, int articleID)
	{
//		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		nameValuePairs.add(new BasicNameValuePair("articleID", Integer.toString(articleID)));
//		try
//		{
//			HttpClient httpclient = new DefaultHttpClient();
//
//			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "upload.php");
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//			ResponseHandler<String> responseHandler = new BasicResponseHandler();
//			String response = httpclient.execute(httppost, responseHandler);
//			// HttpResponse response = httpclient.execute(httppost);
//
//			// HttpEntity entity = response.getEntity();
//			// is = entity.getContent();
//			Boolean success = Boolean.parseBoolean(response);
//
//			/*
//			 * Intent i = new Intent("socialtour.socialtour.PRODUCTDETAIL");
//			 * i.putExtra("lastproductid", lastid); startActivity(i);
//			 */
//
//			if(!success)
//			{
//				Log.d("In doFileUpload: ", "Passing of last ArticleID is not successful");
//				return;
//			}
//
//		}
//		catch (Exception e)
//		{
//			Log.e("log_tag", "Error in http connection" + e.toString());
//		}
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;
		String existingFileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
		// String existingFileName =
		// Environment.getExternalStorageDirectory().getAbsolutePath() +
		// "/mypic.png";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String responseFromServer = "";
		// String urlString = "http://172.22.177.204/FYP/upload.php";
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
			}
			else
			{
				String[] proj =
				{ MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(imageUri, proj, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				pathinsd = cursor.getString(column_index);
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
			File currentfile = new File(pathinsd);
			// boolean passed = currentfile.renameTo(new File(existingFileName +
			// productname));
			// if (passed){
			// currentfile = new File(existingFileName + productname);
			// }
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
			dos.writeBytes(twoHyphens + boundary + lineEnd); // --*****\n
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + hashedimagename + "\"" + lineEnd);
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

	private void acknowledge()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle("Your deal has been shared.");

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
//		m.update(data, 0, data.length);
		m.update(data);
		BigInteger i = new BigInteger(1, m.digest());
		return String.format("%1$032X", i);
	}
}
