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

import socialtour.socialtour.TwitterApp.TwDialogListener;
import socialtour.socialtour.models.Shop;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Productdetail extends Activity implements OnClickListener
{
	InputStream is = null;
	StringBuilder sb = null;
	JSONArray jArray;
	String result = null;
	Bitmap bmImg;
	ImageView imagedisplay;
	Button btnLike, btnDislike, btnShare, btnBack;
	int productid = 0;
	String filename;
	TextView lbllikes, lbldislikes, lblbrand, lblcategory, lblproduct, lblshop, lbladdress, lblpercent, lblprice;
	GlobalVariable globalVar;	

	private TwitterApp mTwitter;
	
	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";

	int likes = 0, dislikes = 0, percent = 0;
	double dprice = 0;
	String category = "", productname = "", shopname = "", brand = "", address = "";

	private String status;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.productdetail);
		
		Container.btn1.setText("Like");
		Container.btn2.setText("Dislike");
		Container.btn3.setText("Share");
		Container.btn1.setEnabled(true);
		Container.btn2.setEnabled(true);
		Container.btn3.setEnabled(true);
		Container.btn1.setVisibility(View.VISIBLE);
		Container.btn2.setVisibility(View.VISIBLE);
		Container.btn3.setVisibility(View.VISIBLE);
		
		globalVar = ((GlobalVariable) getApplicationContext());
		Bundle bundle = getIntent().getExtras();
		productid = (Integer) bundle.get("lastproductid");
		filename = importData(productid);
		downloadFile(filename);
		btnLike = Container.btn1;
		btnDislike = Container.btn2;
		btnShare = Container.btn3;

		btnLike.setOnClickListener(this);
		btnDislike.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnShare.setOnClickListener(this);
	}

	/*
	 * @Override protected void onPause() { super.onPause();
	 * 
	 * unbindDrawables(findViewById(R.id.productdetailLayout)); System.gc(); }
	 * 
	 * private void unbindDrawables(View view) { if (view.getBackground() !=
	 * null) { view.getBackground().setCallback(null); } if (view instanceof
	 * ViewGroup) { for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
	 * { unbindDrawables(((ViewGroup) view).getChildAt(i)); } ((ViewGroup)
	 * view).removeAllViews(); } }
	 */
	
	@Override
	public void onResume(){
		super.onResume();
		Container.btn1.setVisibility(View.VISIBLE);
		Container.btn2.setVisibility(View.VISIBLE);
		Container.btn3.setVisibility(View.VISIBLE);
	}
	
	private String importData(int lastinsertedid)
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("productid", Integer.toString(lastinsertedid)));
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost httppost = new
			// HttpPost("http://172.22.177.204/FYP/database.php");
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

		try
		{
			jArray = new JSONArray(result);
			JSONObject json_data = null;
			for (int i = 0; i < jArray.length(); i++)
			{
				json_data = jArray.getJSONObject(i);
				likes = json_data.getInt("likes");
				dislikes = json_data.getInt("dislikes");
				percent = json_data.getInt("percentdiscount");
				category = json_data.getString("category");
				productname = json_data.getString("filename");
				shopname = json_data.getString("name");
				address = json_data.getString("address");
				brand = json_data.getString("brand");
				dprice = json_data.getDouble("dprice");
			}
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "Error! No JSON Record for this entry", Toast.LENGTH_LONG).show();
		}
		catch (ParseException e1)
		{
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

	void downloadFile(String filename)
	{

		URL myFileUrl = null;
		if (filename.contains(" "))
		{
			filename = filename.replace(" ", "%20");
		}
		try
		{
			// myFileUrl= new URL("http://172.22.177.204/FYP/FYP/uploads/" +
			// filename);
			myFileUrl = new URL(Constants.CONNECTIONSTRING + "FYP/uploads/" + filename);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			int length = conn.getContentLength();
			int[] bitmapData = new int[length];
			byte[] bitmapData2 = new byte[length];
			InputStream is = conn.getInputStream();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			bmImg = BitmapFactory.decodeStream(is, null, options);

			// bmImg = BitmapFactory.decodeStream(is);
			imagedisplay = (ImageView) findViewById(R.id.imgProduct);
			imagedisplay.setImageBitmap(null);
			imagedisplay.setImageBitmap(bmImg);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v)
	{
		TextView lbllikes, lbldislikes;
		ProgressDialog mProgress;
		
		if (v == btnLike)
		{
			lbllikes = (TextView) findViewById(R.id.lblLikes);
			int numberlikes = Integer.parseInt(lbllikes.getText().toString().substring(0, 1));
			numberlikes++;
			lbllikes.setText(Integer.toString(numberlikes) + " likes");
			updateComments("likes", productid, numberlikes);
			btnLike.setEnabled(false);
			btnDislike.setEnabled(false);

		}
		else if (v == btnDislike)
		{
			lbldislikes = (TextView) findViewById(R.id.lblDislikes);
			int numberdislikes = Integer.parseInt(lbldislikes.getText().toString().substring(0, 1));
			numberdislikes++;
			lbldislikes.setText(Integer.toString(numberdislikes) + " dislikes");
			updateComments("dislikes", productid, numberdislikes);
			btnLike.setEnabled(false);
			btnDislike.setEnabled(false);
		}
		else if (v == btnShare)
		{
			// Intent shareIntent = new Intent(getParent(), ProductPage.class);
			// TabGroupActivity parentActivity = (TabGroupActivity)getParent();
			// shareIntent.putExtra("imageURL", Constants.CONNECTIONSTRING +
			// "FYP/uploads/" + filename);
			// shareIntent.putExtra("productname", productname);
			// shareIntent.putExtra("shopname", shopname);
			// shareIntent.putExtra("discount", percent);
			// parentActivity.startChildActivity("Product Page", shareIntent);
			// startActivity(shareIntent);
//			showDialog(1);
			Dialog dialog = new Dialog(getParent(),R.style.shareDialogStyle);
			dialog.setContentView(R.layout.productsharing);
			dialog.setTitle("Sharing via?");
			dialog.setCancelable(true);
			
			ImageButton fbshare = (ImageButton) dialog.findViewById(R.id.fbShareBtn);
			ImageButton twitshare = (ImageButton) dialog.findViewById(R.id.twitShareBtn);
			
			mTwitter = new TwitterApp(getParent(), twitter_consumer_key, twitter_secret_key);
			mTwitter.setListener(mTwLoginDialogListener);
			globalVar.setTwitState(mTwitter);

			mProgress = new ProgressDialog(getParent());			

			status = "Check out this promotion!\n" + productname + " (" + Integer.toString(percent) + "% off) @ " + shopname;
			
			twitshare.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{

					if (!mTwitter.hasAccessToken())
					{
						mTwitter.authorize();
					}
					else
					{
						final Dialog twitDialog = new Dialog(getParent());

						twitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						twitDialog.setContentView(R.layout.twitter_post);
						// twitDialog.setTitle("Post to Twitter");

						Drawable icon = getResources().getDrawable(R.drawable.twitter_icon);

						TextView mTitle = (TextView) twitDialog.findViewById(R.id.titleDialog);

						mTitle.setText("Twitter");
						mTitle.setTextColor(Color.WHITE);
						mTitle.setTypeface(Typeface.DEFAULT_BOLD);
						mTitle.setBackgroundColor(0xFFbbd7e9);
						mTitle.setPadding(4 + 2, 4, 4, 4);
						mTitle.setCompoundDrawablePadding(4 + 2);
						mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
						twitDialog.setCancelable(true);
						final EditText statusPost = (EditText) twitDialog.findViewById(R.id.statusText);
						Log.d("Status: ", status);
						statusPost.setText(status);

						// set up button
						Button postbutton = (Button) twitDialog.findViewById(R.id.postBtn);
						postbutton.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								String review = statusPost.getText().toString();

								if (review.equals(""))
									return;

								postReview(review);

								postToTwitter(review);
								twitDialog.dismiss();
							}
						});

						Button cancelbutton = (Button) twitDialog.findViewById(R.id.cancelBtn);
						cancelbutton.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								twitDialog.cancel();
							}
						});

						twitDialog.show();
						// String review = reviewEdit.getText().toString();
						//
						// if (review.equals(""))
						// return;
						//
						// postReview(review);
						//
						// if (postToTwitter)
						// postToTwitter(review);
					}
				}
			});
			
			dialog.show();
		}
		else if (v == lblshop || v == lbladdress)
		{
			ConnectDB connect = new ConnectDB(lbladdress.getText().toString(), lblshop.getText().toString());
			List<Shop> shop = connect.getShop();
			globalVar = (GlobalVariable) getApplicationContext();
			globalVar.setShop(shop);
			Intent shopIntent = new Intent(getParent(), Shopdetail.class);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Shop Detail", shopIntent);
		}
		else if (v == btnBack)
		{
			Intent i = new Intent(getParent(), Container.class);
			startActivity(i);
		}
	}
	
	private void postReview(String review)
	{
		// post to server

		Toast.makeText(getParent(), "Review posted", Toast.LENGTH_SHORT).show();
	}
	
	private void postToTwitter(final String review)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				int what = 0;

				try
				{
					mTwitter.updateStatus(review);
				}
				catch (Exception e)
				{
					what = 1;
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			String text = (msg.what == 0) ? "Posted to Twitter" : "Post to Twitter failed";

			Toast.makeText(getParent(), text, Toast.LENGTH_SHORT).show();
		}
	};

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener()
	{
		@Override
		public void onComplete(String value)
		{
			String username = mTwitter.getUsername();
			username = (username.equals("")) ? "No Name" : username;
		}

		@Override
		public void onError(String value)
		{
			Toast.makeText(getParent(), "Twitter connection failed", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel()
		{
			// Return to main activity

		}
	};

//	protected Dialog onCreateDialog(int id)
//	{
//		// TODO Auto-generated method stub
//		Log.d("Entered onCreateDialog", "hi");
////		Context mContext = getApplicationContext();
//		Dialog shareMenu = new Dialog(Productdetail.this, R.style.shareDialogStyle);
//
//		switch (id)
//		{
//		case 1:
//			Log.d("in shareMenu", "in Case 1");
//			shareMenu.setContentView(R.layout.productsharing);
//			shareMenu.setTitle("Sharing via");
//			ImageButton fbshare = (ImageButton) shareMenu.findViewById(R.id.fbShareBtn);
//			ImageButton twitshare = (ImageButton) shareMenu.findViewById(R.id.twitShareBtn);
//			Log.d("in shareMenu", "Share Dialog created");
//			break;
//		default:
//			shareMenu = null;
//		}
//
//		return shareMenu;
//	}

	private void updateComments(String type, int id, int numberComments)
	{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", Integer.toString(id)));
		nameValuePairs.add(new BasicNameValuePair("comments", Integer.toString(numberComments)));
		if (type.equals("likes"))
		{
			nameValuePairs.add(new BasicNameValuePair("type", "likes"));
		}
		else if (type.equals("dislikes"))
		{
			nameValuePairs.add(new BasicNameValuePair("type", "dislikes"));
		}
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "update.php");

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = httpclient.execute(httppost, responseHandler);
			httppost = new HttpPost(Constants.CONNECTIONSTRING + "insertcomments.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			responseHandler = new BasicResponseHandler();
			response = httpclient.execute(httppost, responseHandler);

		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error in http connection" + e.toString());
		}
	}

}
