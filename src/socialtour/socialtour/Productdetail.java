package socialtour.socialtour;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.facebook.BaseDialogListener;
import com.facebook.BaseRequestListener;
import com.facebook.SessionEvents;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;

import socialtour.socialtour.TwitterApp.TwDialogListener;
import socialtour.socialtour.models.Remark;
import socialtour.socialtour.models.Shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
	ImageView btnLike, btnShare, btnRemarks;
	Button btnShowComments;
	int productid = 0;
	String filename;
	String commentType;
	TextView lbllikes, lblremarks, lblcategory, lblproduct, lblshop, lbladdress, lblpercent, lblbeforeprice, lblprice, lblusername, lbluploaddate;
	GlobalVariable globalVar;
	FbConnect fbConnect;
	private Facebook facebook;
	private ProgressDialog mProgress;
	private Remark[] listremarks = null;

	private TwitterApp mTwitter;

	AsyncFacebookRunner asyncRunner;

	private static final String APP_DOWNLOAD_LINK = "https://market.android.com/";
	private static final String APP_ID = "222592464462347";
	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";
	private static final int PRODUCTDETAIL = 3;

	int likes = 0, remarks = 0, percent = 0;
	double dprice = 0, oprice = 0;
	String category = "", subcategory = "", productname = "", shopname = "", brand = "", address = "", username = "";
	Date createddate;
	private String fnameS;
	private String lnameS;
	private String userName;
	private String userEmail;
	// private String genderS;
	// private String bdayS;
	private String uid;
	private String status;

	private Dialog dialog;
	boolean existed = false;
	TextView latestcomments;
	TextView alreadylike;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.productdetail);

		btnLike = Container.btn1;
		btnRemarks = Container.btn2;
		btnShare = Container.btn3;

		btnLike.setEnabled(true);
		btnRemarks.setEnabled(true);
		btnShare.setEnabled(true);
		btnLike.setVisibility(View.VISIBLE);
		btnRemarks.setVisibility(View.VISIBLE);
		btnShare.setVisibility(View.VISIBLE);

		btnLike.setImageResource(R.drawable.likepicwhite);
		btnRemarks.setImageResource(R.drawable.remarkpic);
		btnShare.setImageResource(R.drawable.share2);

		btnShowComments = (Button) findViewById(R.id.btnshowmorecomments);

		globalVar = ((GlobalVariable) getApplicationContext());
		facebook = globalVar.getFBState();
		Bundle bundle = getIntent().getExtras();
		productid = (Integer) bundle.get("lastproductid");

		alreadylike = (TextView) findViewById(R.id.lbllikealready);

		SharedPreferences userDetails = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
		String userid = "", usertype = "";
		if (!userDetails.getString("userID", "").equals(""))
		{
			userid = userDetails.getString("userID", "");
			// usertype = "user_norm";
		}
		// else if (!userDetails.getString("userDB_FBID", "").equals(""))
		// {
		// userid = userDetails.getString("userDB_FBID", "");
		// usertype = "user_fb";
		// }
		// else if (!userDetails.getString("userDB_TWITID", "").equals(""))
		// {
		// userid = userDetails.getString("userDB_TWITID", "");
		// usertype = "user_twit";
		// }
		boolean existed = checkCount(userid, Integer.toString(productid));
		if (existed)
		{
			btnLike.setImageResource(R.drawable.likepicgray);
			btnLike.setEnabled(false);
			alreadylike = (TextView) findViewById(R.id.lbllikealready);
			alreadylike.setVisibility(View.VISIBLE);
		}

		filename = importData(productid);
		importRemarks(productid);
		latestcomments = (TextView) findViewById(R.id.latestcomments);
		if (listremarks != null)
		{
			latestcomments.setText("'" + listremarks[0].getDesc() + "'\r\n");
			for (int i = 1; i < 3; i++)
			{
				latestcomments.setText(latestcomments.getText() + "'" + listremarks[i].getDesc() + "'\r\n");
			}
		}
		else
		{
			latestcomments.setText("No comments currently");
			btnShowComments.setVisibility(View.GONE);
		}

		downloadFile(filename);
		mProgress = new ProgressDialog(getParent());

		btnLike.setOnClickListener(this);
		btnRemarks.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnShowComments.setOnClickListener(this);
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
	public void onResume()
	{
		super.onResume();

		btnLike.setImageResource(R.drawable.likepicwhite);
		btnRemarks.setImageResource(R.drawable.remarkpic);
		btnShare.setImageResource(R.drawable.share2);

		SharedPreferences userDetails = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
		String userid = "", usertype = "";
		if (!userDetails.getString("userID", "").equals(""))
		{
			userid = userDetails.getString("userID", "");
			// usertype = "user_norm";
		}
		// else if (!userDetails.getString("userDB_FBID", "").equals(""))
		// {
		// userid = userDetails.getString("userDB_FBID", "");
		// usertype = "user_fb";
		// }
		// else if (!userDetails.getString("userDB_TWITID", "").equals(""))
		// {
		// userid = userDetails.getString("userDB_TWITID", "");
		// usertype = "user_twit";
		// }

		boolean existed = checkCount(userid, Integer.toString(productid));
		if (existed)
		{
			btnLike.setImageResource(R.drawable.likepicgray);
			btnLike.setEnabled(false);
			alreadylike.setVisibility(View.VISIBLE);
		}

		btnShare.setEnabled(true);
		btnLike.setVisibility(View.VISIBLE);
		btnRemarks.setVisibility(View.VISIBLE);
		btnShare.setVisibility(View.VISIBLE);

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
				remarks = json_data.getInt("remarks");
				JSONArray jextraArr = new JSONArray(json_data.getString("extra_fields"));
				for (int j = 0; j < 5; j++)
				{
					JSONObject jextraObj = jextraArr.getJSONObject(j);
					if (j == 0)
					{
						oprice = jextraObj.getDouble("value");
					}
					else if (j == 1)
					{
						shopname = jextraObj.getString("value");
					}
					else if (j == 2)
					{
						address = jextraObj.getString("value");
					}
					else if (j == 3)
					{
						percent = (int) jextraObj.getDouble("value");
					}
					else if (j == 4)
					{
						dprice = jextraObj.getDouble("value");
					}
				}
				// percent = json_data.getInt("percentdiscount");
				category = json_data.getString("category");
				subcategory = json_data.getString("subcategory");
				productname = json_data.getString("title");
				// shopname = json_data.getString("name");
				// address = json_data.getString("address");
				// dprice = json_data.getDouble("dprice");
				// oprice = json_data.getDouble("oprice");

				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				createddate = (Date) formatter.parse(json_data.getString("created"));
				username = json_data.getString("user_name");
			}
		}
		catch (JSONException e1)
		{
			Toast.makeText(getBaseContext(), "Error! Having trouble reading JSON results for this deal", Toast.LENGTH_LONG).show();
		}
		catch (ParseException e1)
		{
			e1.printStackTrace();
		}
		catch (java.text.ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lbllikes = (TextView) findViewById(R.id.lblLikes);
		lblremarks = (TextView) findViewById(R.id.lblRemarks);
		lblcategory = (TextView) findViewById(R.id.lblCategory);
		lblproduct = (TextView) findViewById(R.id.lblProductname);
		lblshop = (TextView) findViewById(R.id.shopname);
		lbladdress = (TextView) findViewById(R.id.lblshopaddress);
		lblpercent = (TextView) findViewById(R.id.lblPercent);
		lblbeforeprice = (TextView) findViewById(R.id.lblbeforeprice);
		lblprice = (TextView) findViewById(R.id.lblPrice);
		lblusername = (TextView) findViewById(R.id.lblUploadedby);
		lbluploaddate = (TextView) findViewById(R.id.lblCreateddate);

		lbllikes.setText(Integer.toString(likes));
		lblremarks.setText(Integer.toString(remarks));
		lblcategory.setText("Category:  " + subcategory + " (" + category + ")");
		String productname2 = productname;// .substring(0,
											// productname.indexOf("."));
		lblproduct.setText(productname2);
		lblshop.setText(shopname);
		lbladdress.setText(address);
		lblpercent.setText(Integer.toString(percent) + "%");
		double oriprice = oprice * 1000;
		long tempprice2 = Math.round(oriprice);
		double finalprice2 = tempprice2 / 1000;
		lblbeforeprice.setText("Original Price: $" + Double.toString(finalprice2) + "0");
		double price = dprice * 1000;
		long tempprice = Math.round(price);
		double finalprice = tempprice / 1000;
		lblprice.setText("Discounted Price: $" + Double.toString(finalprice) + "0");
		lblusername.setText(username);
		java.util.Date prodDate = createddate;
		DateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
		String finalDate = df2.format(prodDate);
		lbluploaddate.setText(finalDate);

		lblshop.setClickable(true);
		lblshop.setEnabled(true);
		lblshop.setFocusable(true);
		lblshop.setFocusableInTouchMode(true);
		lbladdress.setClickable(true);
		lbladdress.setEnabled(true);
		lbladdress.setFocusable(true);
		lbladdress.setFocusableInTouchMode(true);

		lblshop.setOnClickListener(new AdapterView.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Log.d("In productdetail, shop touched: ", "lblshop");

				ConnectDB connect = new ConnectDB(lbladdress.getText().toString(), lblshop.getText().toString());
				List<Shop> shop = connect.getShop();
				// for (Shop result : shop)
				// {
				// Log.d("In productdetail, shop result: ", result.getName());
				// }
				globalVar = (GlobalVariable) getApplicationContext();
				globalVar.setShop(shop);
				Intent shopIntent = new Intent(getParent(), Shopdetail.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("Shop Detail", shopIntent);
			}
		});

		lbladdress.setOnClickListener(new AdapterView.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Log.d("In productdetail, shop touched: ", "lbladdress");

				ConnectDB connect = new ConnectDB(lbladdress.getText().toString(), lblshop.getText().toString());
				List<Shop> shop = connect.getShop();
				// for (Shop result : shop)
				// {
				// Log.d("In productdetail, shop result: ", result.getName());
				// }
				globalVar = (GlobalVariable) getApplicationContext();
				globalVar.setShop(shop);
				Intent shopIntent = new Intent(getParent(), Shopdetail.class);
				TabGroupActivity parentActivity = (TabGroupActivity) getParent();
				parentActivity.startChildActivity("Shop Detail", shopIntent);
			}
		});
		return productname;
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
		if (!result.contains("null"))
		{
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

					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					listremarks[i].setCreated((Date) formatter.parse(json_data.getString("remark_created")));
				}
			}
			catch (JSONException e1)
			{
				Toast.makeText(getBaseContext(), "Error! Having trouble reading json results for remarks", Toast.LENGTH_LONG).show();
			}
			catch (ParseException e1)
			{
				e1.printStackTrace();
			}
			catch (java.text.ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void downloadFile(String filename)
	{

		URL myFileUrl = null;

		if (filename.contains(" "))
		{
			filename = filename.replace(" ", "%20");
		}

		String hashedimagename = "";
		try
		{
			hashedimagename = MungPass("Image" + Integer.toString(productid)) + ".jpg";
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			// myFileUrl= new URL("http://172.22.177.204/FYP/FYP/uploads/" +
			// filename);
			myFileUrl = new URL(Constants.DOWNLOAD_PATH + "joomla25/media/k2/items/src/" + hashedimagename);
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
			options.inSampleSize = 2;
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

		SharedPreferences userDetails = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
		String userid = "", usertype = "";
		if (!userDetails.getString("userID", "").equals(""))
		{
			userid = userDetails.getString("userID", "");
			// usertype = "user_norm";
		}
		// else if (!userDetails.getString("userDB_FBID", "").equals(""))
		// {
		// userid = userDetails.getString("userDB_FBID", "");
		// usertype = "user_fb";
		// }
		// else if (!userDetails.getString("userDB_TWITID", "").equals(""))
		// {
		// userid = userDetails.getString("userDB_TWITID", "");
		// usertype = "user_twit";
		// }

		if (v == btnLike)
		{
			boolean existed = checkCount(userid, Integer.toString(productid));
			if (existed == true)
			{
				// prompt error dialog
				alertCommented();
			}
			else
			{
				lbllikes = (TextView) findViewById(R.id.lblLikes);
				int numberlikes = Integer.parseInt(lbllikes.getText().toString().substring(0, 1));
				numberlikes++;
				lbllikes.setText(Integer.toString(numberlikes) + " likes");
				alreadylike.setVisibility(View.VISIBLE);
				updateComments("likes", productid, numberlikes, userid);
			}
		}
		else if (v == btnRemarks)
		{
			Intent intent = new Intent(getParent(), Remarks.class);
			intent.putExtra("productid", productid);
			TabGroupActivity parentActivity = (TabGroupActivity) getParent();
			parentActivity.startChildActivity("Remarks", intent);
			// do something
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
			// showDialog(1);
			dialog = new Dialog(getParent(), R.style.shareDialogStyle);
			dialog.setContentView(R.layout.productsharing);
			dialog.setTitle("Sharing via?");
			dialog.setCancelable(true);

			ImageButton fbshare = (ImageButton) dialog.findViewById(R.id.fbShareBtn);
			ImageButton twitshare = (ImageButton) dialog.findViewById(R.id.twitShareBtn);

			mTwitter = new TwitterApp(getParent(), twitter_consumer_key, twitter_secret_key, PRODUCTDETAIL);
			mTwitter.setListener(mTwLoginDialogListener);
			globalVar.setTwitState(mTwitter);

			mProgress = new ProgressDialog(getParent());

			// status = "Check out this promotion!\n" + productname + " (" +
			// Integer.toString(percent) + "% off) @ " + shopname;
			String alias = productid+"-"+productname.trim().toLowerCase().replace(' ', '-');
			status = "Check out this promotion! " + productname + " (" + Integer.toString(percent) + "% off) @ " + shopname +"\n"+Constants.DOWNLOAD_PATH+"joomla25/index.php/site-map/articles/item/"+alias;
			fbshare.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					gotoFacebookConnect();
				}

			});
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
		// else if (v == lblshop || v == lbladdress)
		// {
		// if (v == lblshop)
		// {
		// Log.d("In productdetail, shop touched: ", "lblshop");
		// }
		// else if (v == lbladdress)
		// {
		// Log.d("In productdetail, shop touched: ", "lbladdress");
		// }
		// ConnectDB connect = new ConnectDB(lbladdress.getText().toString(),
		// lblshop.getText().toString());
		// List<Shop> shop = connect.getShop();
		// for (Shop result : shop)
		// {
		// Log.d("In productdetail, shop result: ", result.getName());
		// }
		// globalVar = (GlobalVariable) getApplicationContext();
		// globalVar.setShop(shop);
		// Intent shopIntent = new Intent(getParent(), Shopdetail.class);
		// TabGroupActivity parentActivity = (TabGroupActivity) getParent();
		// parentActivity.startChildActivity("Shop Detail", shopIntent);
		// }
		else if (v == btnShowComments)
		{

		}
	}

	protected void gotoFacebookConnect()
	{
		// TODO Auto-generated method stub
		fbConnect = new FbConnect(APP_ID, getParent(), getApplicationContext());
	}

	private void postToWall()
	{
		final Dialog fbDialog = new Dialog(getParent());

		fbDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		fbDialog.setContentView(R.layout.twitter_post);
		// twitDialog.setTitle("Post to Twitter");

		Drawable icon = getResources().getDrawable(R.drawable.facebook_icon);

		TextView mTitle = (TextView) fbDialog.findViewById(R.id.titleDialog);

		mTitle.setText("Facebook");
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(0xFFbbd7e9);
		mTitle.setPadding(4 + 2, 4, 4, 4);
		mTitle.setCompoundDrawablePadding(4 + 2);
		mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		fbDialog.setCancelable(true);
		final EditText statusPost = (EditText) fbDialog.findViewById(R.id.statusText);
		Log.d("Status: ", status);
		statusPost.setText(status);

		// set up button
		Button postbutton = (Button) fbDialog.findViewById(R.id.postBtn);
		postbutton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String review = statusPost.getText().toString();

				if (review.equals(""))
					return;

				postReview(review);
				
				postWithoutDialog(review, APP_DOWNLOAD_LINK, "SocialTourApp", "Hello", "");
//				postWithDialog(getParent(), "https://a248.e.akamai.net/assets.github.com/images/modules/header/logov6.png", review);
				fbDialog.dismiss();
			}
		});

		Button cancelbutton = (Button) fbDialog.findViewById(R.id.cancelBtn);
		cancelbutton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				fbDialog.cancel();
			}
		});

		fbDialog.show();
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

	public void postWithoutDialog(String message, String link, String name, String caption, String imageUrl)
	{
		// Bundle params = new Bundle();
		// params.putString("message", message);
		// params.putString("link", link);
		// params.putString("name", name);
		// params.putString("caption", caption);
		// params.putString("picture", imageUrl);
		//
		// asyncRunner = new AsyncFacebookRunner(mFacebook);
		// asyncRunner.request("me/feed", params, "POST", new
		// WallPostRequestListener(), null);
		Bundle parameters = new Bundle();
		parameters.putString("message", message);
		parameters.putString("description", "integrating stuff");
		parameters.putString("attachment", "{\"name\":\"My Test Image\"," + "\"href\":\"" + "http://www.google.com" + "\"," + "\"media\":[{\"type\":\"image\",\"src\":\"" + "http://www.google.com/logos/mucha10-hp.jpg" + "}");
		try
		{
			facebook.request("me");
			String response = facebook.request("me/feed", parameters, "POST");
			Log.d("Tests", "got response: " + response);
			if (response == null || response.equals("") || response.equals("false"))
			{}
			else
			{
				Log.d("Success", "Message posted to your facebook wall!");
			}
			finish();
		}
		catch (Exception e)
		{
			Log.d("Error", "Failed to post to wall!");
			e.printStackTrace();
			finish();
		}
	}

	/**
	 * This method displays a dialog that allows the user to post on his wall.
	 * 
	 * @param context
	 *            - the current context
	 * @param imageUrl
	 *            - a link to an image to post on the wall
	 */
	public void postWithDialog(Context context, String imageUrl, String message)
	{
		Bundle parameters = new Bundle();

		// set default message
		/*
		 * This field will be ignored on July 12, 2011 The message to prefill
		 * the text field that the user will type in. To be compliant with
		 * Facebook Platform Policies, your application may only set this field
		 * if the user manually generated the content earlier in the workflow.
		 * Most applications should not set this.
		 * parameters.putString("message",
		 * "I like this look, what do you think?");
		 */

		// set image, description and a link for downloading the application.
		parameters.putString("attachment", "{\"name\":\"" + message + "\"," + "\"href\":\"" + APP_DOWNLOAD_LINK + "\"," + "\"description\":\"Uploaded via android emulator using MyTestingApp =) \"," + "\"media\":[{\"type\":\"image\",\"src\":\"" + imageUrl + "\",\"href\":\"" + APP_DOWNLOAD_LINK
				+ "\"}]" + "}");
		// Message field ignored from 12th July 2011
		parameters.putString("message", message);
		// parameters.putString("attachment",
		// "{\"name\":\"Facebook Connect for Android\",\"href\":\"http://code.google.com/p/fbconnect-android/\",\"caption\":\"Caption\",\"description\":\"Description\",\"media\" {\"type\":\"image\",\"src\":\"http://img40.yfrog.com/img40/5914/iphoneconnectbtn.jpg\",\"href\":\"http://developers.facebook.com/connect.php?tab=iphone/\"}],\"properties\":{\"another link\":{\"text\":\"Facebook home page\",\"href\":\"http://www.facebook.com\"}}}");
		// parameters.putString("attachment",
		// "{\"name\":\"My Test Image\","+"\"href\":\""+"http://www.google.com"+"\","+"\"media\":[{\"type\":\"image\",\"src\":\""+"http://www.google.com/logos/mucha10-hp.jpg"+"}");
		// display the user dialog
		facebook.dialog(context, "stream.publish", parameters, new WallPostDialogListener());
	}

	public class WallPostRequestListener extends BaseRequestListener
	{

		public void onComplete(final String response, final Object state)
		{
			Log.d("Facebook-Example", "Got response: " + response);
			String message = "<empty>";
			try
			{
				JSONObject json = Util.parseJson(response);
				message = json.getString("message");
				Log.d("Message: ", message);
			}
			catch (JSONException e)
			{
				Log.w("Facebook-Example", "JSON Error in response");
			}
			catch (FacebookError e)
			{
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
			// final String text = "Your Wall Post: " + message;
			getParent().runOnUiThread(new Runnable()
			{
				public void run()
				{
					// mText.setText(text);
				}
			});
		}
	}

	public class WallPostDeleteListener extends BaseRequestListener
	{

		public void onComplete(final String response, final Object state)
		{
			if (response.equals("true"))
			{
				Log.d("Facebook-Example", "Successfully deleted wall post");
				getParent().runOnUiThread(new Runnable()
				{
					public void run()
					{
						// mDeleteButton.setVisibility(View.INVISIBLE);
						// mText.setText("Deleted Wall Post");
					}
				});
			}
			else
			{
				Log.d("Facebook-Example", "Could not delete wall post");
			}
		}
	}

	public class WallPostDialogListener extends BaseDialogListener
	{

		public void onComplete(Bundle values)
		{
			final String postId = values.getString("post_id");
			if (postId != null)
			{
				Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);

				asyncRunner = new AsyncFacebookRunner(facebook);
				asyncRunner.request(postId, new WallPostRequestListener());
				// mDeleteButton.setOnClickListener(new OnClickListener()
				// {
				// public void onClick(View v)
				// {
				// asyncRunner.request(postId, new Bundle(), "DELETE", new
				// WallPostDeleteListener(), null);
				// }
				// });
			}
			else
			{
				Log.d("Facebook-Example", "No wall post made");
			}
		}
	}

	private void updateComments(String type, int id, int numberComments, String userid)
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
			nameValuePairs.add(new BasicNameValuePair("userid", userid));
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

	private boolean checkCount(String userid, String prodid)
	{

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", userid));
		nameValuePairs.add(new BasicNameValuePair("prodid", prodid));
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost httppost = new
			// HttpPost("http://172.22.177.204/FYP/database.php");
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "commentscount.php");
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

		if (!result.contains("null"))
		{
			try
			{
				jArray = new JSONArray(result);
				JSONObject json_data = null;
				for (int i = 0; i < jArray.length(); i++)
				{
					json_data = jArray.getJSONObject(i);
					commentType = json_data.getString("category");
				}

				if (commentType != null)
				{
					return true;
				}
				else
				{
					return false;
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
		}

		return false;
	}

	private void alertCommented()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		String title = null;
		if (commentType.equals("likes"))
		{
			title = "You have already liked this deal previously";
		}
		else if (commentType.equals("dislike"))
		{
			title = "You have already disliked this deal previously";
		}
		dialog.setTitle(title);
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

	public class FbConnect
	{

		private final String[] FACEBOOK_PERMISSION =
		{ "user_birthday", "email", "publish_stream", "read_stream", "offline_access" };

		private Context context;
		private Activity activity;
		// private Handler mHandler;
		private Facebook facebook;
		GlobalVariable FbState = ((GlobalVariable) getApplicationContext());

		private SharedPreferences sharedPref;
		private Editor editor;

		// private SessionListener mSessionListener = new SessionListener();

		public FbConnect(String appId, Activity activity, Context context)
		{

			this.context = context;
			// this.mHandler = new Handler();
			this.activity = activity;

			sharedPref = context.getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);

			editor = sharedPref.edit();
			globalVar = ((GlobalVariable) getApplicationContext());

			// SharedPreferences prefs=
			// PreferenceManager.getDefaultSharedPreferences(SearchShops.this);
			// String access_token = prefs.getString("access_token", null);
			// Long expires = prefs.getLong("access_expires", -1);
			// String sharedName = prefs.getString("name", "");
			//
			//
			// if (access_token != null && expires != -1)
			// {
			// facebook.setAccessToken(access_token);
			// facebook.setAccessExpires(expires);
			// }

			// if (!facebook.isSessionValid() || sharedName.equals(""))
			// {
			// facebook.authorize(activity, FACEBOOK_PERMISSION, new
			// LoginDialogListener());
			// }
			// else
			// {
			// name.setText("Hello " + sharedName + ",");
			// }
			facebook = FbState.getFBState();

			// if (!facebook.isSessionValid())
			// {
			// facebook = new Facebook(APP_ID);
			// FbState.setFbState(facebook);
			Log.d("in Settings, FBConnect:", "before login()");
			login();
			// }
			// else
			// {
			// SessionStore.restore(facebook, context);
			// }

			// SessionEvents.addAuthListener(mSessionListener);
			// SessionEvents.addLogoutListener(mSessionListener);

		}

		public void login()
		{
			// GlobalVariable fbBtn = ((GlobalVariable)
			// getApplicationContext());
			// Boolean fbButton = fbBtn.getfbBtn();
			// if (fbButton == true)
			// {
			if (!facebook.isSessionValid())
			{
				Log.d("in Settings, FBConnect:", "before authorizing");
				facebook.authorize(activity, FACEBOOK_PERMISSION, new LoginDialogListener());
			}
			// }
			else
			{
				// globalVar = ((GlobalVariable) getApplicationContext());
				// name.setText("Hello " + sharedPref.getString("facebookName",
				// "") + ",");
				mProgress.dismiss();
				dialog.dismiss();
				// Post to wall with custom dialog
				postToWall();
				// Post to wall with FB dialog
				// postWithDialog(getParent(),
				// "https://a248.e.akamai.net/assets.github.com/images/modules/header/logov6.png",
				// status);
			}
		}

		private final class LoginDialogListener implements DialogListener
		{
			public void onComplete(Bundle values)
			{
				Log.d("in Settings, FBConnect:", "in onComplete");
				SessionEvents.onLoginSuccess();

				mProgress.setMessage("Finalizing ...");
				mProgress.show();
				AsyncFacebookRunner syncRunner = new AsyncFacebookRunner(facebook);
				syncRunner.request("me", new LoginRequestListener());
			}

			public void onFacebookError(FacebookError error)
			{
				Log.d("in Settings, FBConnect:", "in onFacebookError");
				SessionEvents.onLoginError(error.getMessage());
			}

			public void onError(DialogError error)
			{
				Log.d("in Settings, FBConnect:", "in onError");
				SessionEvents.onLoginError(error.getMessage());
			}

			public void onCancel()
			{
				Log.d("in Settings, FBConnect:", "in onCancel");
				SessionEvents.onLoginError("Action Canceled");
				// Intent intent = new Intent(context, LoginPage.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
			}
		}

		public class LoginRequestListener extends BaseRequestListener
		{
			public void onComplete(String response, final Object state)
			{
				try
				{
					// process the response here: executed in background thread
					Log.d("Facebook-Container", "Response: " + response.toString());
					JSONObject json = Util.parseJson(response);
					fnameS = json.getString("first_name");
					lnameS = json.getString("last_name");
					userName = fnameS + " " + lnameS;
					userEmail = json.getString("email");
					// genderS = json.getString("gender");
					// bdayS = json.getString("birthday");
					uid = json.getString("id");

					editor.putString("userFBname", userName);
					editor.putString("userFBID", uid);
					editor.putBoolean("FacebookLoggedOut", false);
					editor.commit();
					Log.d("Facebook", fnameS);
					// userS = new UserParticulars(fnameS, lnameS, userEmail,
					// genderS, bdayS);

					// callback should be run in the original thread,
					// not the background thread
					mHandler.post(new Runnable()
					{
						public void run()
						{
							ConnectDB connectCheck;
							try
							{
								connectCheck = new ConnectDB(userName, userEmail, uid, "", "user_fb", PRODUCTDETAIL, Productdetail.this);

								editor.putString("userName", connectCheck.getUserName());
								editor.putString("emailFB_Login", connectCheck.getUserEmail());
								editor.putString("userDB_FBID", connectCheck.getUserFbTwNmID());
								editor.putString("userID", connectCheck.getUserID());
								editor.commit();

								// editor.commit();
								// name.setText("Hello " + nameS + ",");
								mProgress.dismiss();
								dialog.dismiss();
								postWithDialog(getParent(), "https://a248.e.akamai.net/assets.github.com/images/modules/header/logov6.png", status);
								// postToWall();
								// Log.d("Facebook session 2: ",
								// Boolean.toString(facebook.isSessionValid()));
							}
							catch (NoSuchAlgorithmException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (UnsupportedEncodingException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// globalVar = ((GlobalVariable)
							// getApplicationContext());
							// globalVar.setName(nameS);
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// String token = facebook.getAccessToken();
							// long token_expires = facebook.getAccessExpires();

							// SharedPreferences prefs=
							// PreferenceManager.getDefaultSharedPreferences(SearchShops.this);
							//
							// prefs.edit().putLong("access_expires",
							// token_expires).commit();
							//
							// prefs.edit().putString("access_token",
							// token).commit();
							//
							// prefs.edit().putString("name", nameS).commit();
							// fname.setText(fnameS);
							// lname.setText(lnameS);
							// email.setText(emailS);
							// if (genderS.equals("male"))
							// {
							// male.setChecked(true);
							// female.setChecked(false);
							// }
							// else if (genderS.equals("female"))
							// {
							// female.setChecked(true);
							// male.setChecked(false);
							// }
							//
							// for (int i = 0; i < 3; i++)
							// {
							// bdayInt[i] = getBday(bdayS)[i];
							// }
							// bday.setText("Your Birthdate is: " + bdayInt[0]
							// +"/" + bdayInt[1] + "/" + bdayInt[2]);
						}
					});
				}
				catch (JSONException e)
				{
					Log.w("Facebook-Example", "JSON Error in response");
				}
				catch (FacebookError e)
				{
					Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
				}
			}
		}

		public Facebook getFacebook()
		{
			return this.facebook;
		}

	}

	private static String MungPass(String pass) throws NoSuchAlgorithmException
	{
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.reset();
		byte[] data = pass.getBytes();
//		m.update(data, 0, data.length);
		m.update(data);
		BigInteger i = new BigInteger(1, m.digest());
		String result = String.format("%1$032X", i);
		return result.toLowerCase();
	}
}
