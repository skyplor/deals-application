package socialtour.socialtour;

import java.io.BufferedReader;
//import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
//import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.commons.codec.binary.Base64;
//import org.json.JSONArray;
//import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fedorvlasov.lazylist.LazyAdapter;

import socialtour.socialtour.models.Product;
import socialtour.socialtour.models.Shop;
//import org.json.JSONTokener;

//import com.facebook.android.Util;

//import android.os.Looper;
import android.net.ParseException;
import android.util.Log;

public class ConnectDB
{

	// private JSONArray jArray;
	// private Boolean flag;
	String result = "";
	String salt = "";
	String name = "";
	InputStream is = null;
	String hpw = "";
	HttpClient client;
	String userID = "";
	String userName = "";
	String userEmail = "";
	String userTwitID = "";

	List<Shop> shop;
	Shop[] shopArray;

	public Shop[] getShopArray()
	{
		return shopArray;
	}

	public void setShopArray(Shop[] shopArray)
	{
		this.shopArray = shopArray;
	}

	public Product[] getArrPro()
	{
		return arrPro;
	}

	public void setArrPro(Product[] arrPro)
	{
		this.arrPro = arrPro;
	}

	Product[] arrPro;
	LazyAdapter adapter;

	public ConnectDB(String email, String password, Integer type)
	{
		if (type == 0)
		{
			// the data to send
			ArrayList<NameValuePair> checkSalt = new ArrayList<NameValuePair>();
			checkSalt.add(new BasicNameValuePair("email", email));

			// String response = "";
			BufferedReader inSalt = null;

			try
			{
				client = new DefaultHttpClient();
				HttpPost emailRequest = new HttpPost(Constants.CONNECTIONSTRING + "getSalt.php");
				emailRequest.setEntity(new UrlEncodedFormEntity(checkSalt));
				HttpResponse emailResponse = client.execute(emailRequest);
				HttpEntity saltEntity = emailResponse.getEntity();
				inSalt = new BufferedReader(new InputStreamReader(saltEntity.getContent(), "iso-8859-1"), 8);
				String bsalt = inSalt.readLine();
				// JSONTokener tokener = new JSONTokener(bsalt);
				// JSONArray finalResult = new JSONArray(tokener);
				JSONObject jsonObj = new JSONObject(bsalt);
				Log.d("Salt: ", jsonObj.getString("salt"));
				salt = jsonObj.getString("salt");
				// name = jsonObj.getString("name");
				// Log.d("Name: ", name);
				inSalt.close();

				String hashedPass = new String(getHash(1000, password, salt.getBytes()), "UTF8");
				Log.d("Hashed Password: ", hashedPass);

				authenticateUser(email, hashedPass);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			authenticateUser(email, password);
		}
	}

	private void authenticateUser(String email, String hashedPass)
	{

		BufferedReader in = null;
		// TODO Auto-generated method stub
		try
		{

			client = new DefaultHttpClient();
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("password", hashedPass));

			Log.d("email: ", email);
			Log.d("pass: ", hashedPass);
			HttpPost request = new HttpPost(Constants.CONNECTIONSTRING + "authenticateUser.php");
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			in = new BufferedReader(new InputStreamReader(entity.getContent()), 8);
			// StringBuffer sb = new StringBuffer("");
			// String line = "";
			// String NL = System.getProperty("line.separator");
			// while ((line = in.readLine()) != null)
			// {
			// sb.append(line + NL);
			// }
			result = in.readLine();
			if (!result.equals("null"))
			{
				Log.d("HashedPass: ", hashedPass);
				try
				{
					JSONArray jArray = new JSONArray(result);
					JSONObject json_data = null;
					for (int i = 0; i < jArray.length(); i++)
					{
						json_data = jArray.getJSONObject(i);
						userID = json_data.getString("id");
						userName = json_data.getString("name");
						userEmail = json_data.getString("email");
						hpw = json_data.getString("password");
					}

				}
				catch (JSONException e1)
				{
					Log.e("JSONException: ", e1.getMessage());
				}
				catch (ParseException e1)
				{
					e1.printStackTrace();
				}
			}
			else
			{
				result = "0";
			}

			in.close();
			client.getConnectionManager().shutdown();
		}

		catch (Exception exc)
		{
			Log.e("log_tag", "Error converting result " + exc.toString());
		}

		// parse json data
		// try
		// {
		// jArray = new JSONArray(result);
		// for (int i = 0; i < jArray.length(); i++)
		// {
		// JSONObject json_data = jArray.getJSONObject(i);
		// //Log.i("log_tag", "id: " + json_data.getInt("id") + ", name: " +
		// json_data.getString("name") + ", sex: " + json_data.getInt("sex") +
		// ", birthyear: " + json_data.getInt("birthyear"));
		// }
		// }
		// catch (JSONException je)
		// {
		// Log.e("log_tag", "Error parsing data " + je.toString());
		// }
	}

	// if (result == null)
	// {
	// result = "0";
	// }
	// else
	// {
	// userName = result;
	// hpw = hashedPass;
	// }
	// in.close();
	// client.getConnectionManager().shutdown();
	// Log.d("Result: ", result);
	// // result = sb.toString();
	//
	// // result = result.replaceAll("\\s+", "");
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }

	public ConnectDB(String name, String emailortwitID, String password, String userType) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String hashedPass = "";

		// the data to send
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("userType", userType));

		if (userType.equals("user_norm"))
		{
			// Uses a secure Random not a simple Random
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			byte[] salt = new byte[8];
			random.nextBytes(salt);

			nameValuePairs.add(new BasicNameValuePair("email", emailortwitID));
			hashedPass = new String(getHash(1000, password, Base64.encodeBase64(salt)), "UTF8");
			nameValuePairs.add(new BasicNameValuePair("password", hashedPass));
			nameValuePairs.add(new BasicNameValuePair("salt", new String(Base64.encodeBase64(salt), "UTF8")));

			for (int i = 0; i < 4; i++)
			{
				Log.d(Integer.toString(i), nameValuePairs.get(i).getName() + ": " + nameValuePairs.get(i).getValue());
			}

		}

		else if (userType.equals("user_fb"))
		{
			nameValuePairs.add(new BasicNameValuePair("email", emailortwitID));
		}

		else if (userType.equals("user_twit"))
		{
			nameValuePairs.add(new BasicNameValuePair("twitID", emailortwitID));
		}
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "insertUser.php");

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}

		catch (Exception ex)
		{
			Log.e("log_tag", "Error in http connection " + ex.toString());
		}
		// convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			// StringBuilder sb = new StringBuilder();
			String line = null;
			line = reader.readLine();
			// while ((line = reader.readLine()) != null)
			// {
			// sb.append(line + "\n");
			// }
			is.close();

			result = line;// sb.toString();
			Log.d("Result in ConnectDB: ", result);
			if (!result.equals("null"))
			{
				try
				{
					JSONArray jArray = new JSONArray(result);
					JSONObject json_data = null;
					for (int i = 0; i < jArray.length(); i++)
					{
						json_data = jArray.getJSONObject(i);
						userID = json_data.getString("id");
						userName = json_data.getString("name");
						if (userType.equals("user_twit"))
						{
							userTwitID = json_data.getString("twitID");
						}
						else if (userType.equals("user_norm"))
						{
							Log.d("HashedPass: ", hashedPass);
							hpw = json_data.getString("password");
							userEmail = json_data.getString("email");
						}
						else
						{
							userEmail = json_data.getString("email");
						}
					}
				}
				catch (JSONException e1)
				{
					Log.e("JSONException: ", e1.getMessage());
				}
				catch (ParseException e1)
				{
					e1.printStackTrace();
				}
			}
			else
			{
				result = "0";
			}
		}

		catch (Exception exc)
		{
			Log.e("log_tag", "Error converting result " + exc.toString());
		}

		// parse json data
		// try
		// {
		// jArray = new JSONArray(result);
		// for (int i = 0; i < jArray.length(); i++)
		// {
		// JSONObject json_data = jArray.getJSONObject(i);
		// //Log.i("log_tag", "id: " + json_data.getInt("id") + ", name: " +
		// json_data.getString("name") + ", sex: " + json_data.getInt("sex") +
		// ", birthyear: " + json_data.getInt("birthyear"));
		// }
		// }
		// catch (JSONException je)
		// {
		// Log.e("log_tag", "Error parsing data " + je.toString());
		// }
	}

	public ConnectDB(String address, String name)
	{
		// the data to send
		// ArrayList<NameValuePair> nameValuePairs = new
		// ArrayList<NameValuePair>();
		// nameValuePairs.add(new BasicNameValuePair("address", address));
		// nameValuePairs.add(new BasicNameValuePair("name", name));
		JSONObject json = new JSONObject();
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "storeLocations.php");
			json.put("address", address);
			json.put("name", name);
			httppost.setHeader("json", json.toString());
			StringEntity se = new StringEntity(json.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httppost.setEntity(se);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}

		catch (Exception ex)
		{
			Log.e("log_tag", "Error in http connection " + ex.toString());
		}
		// convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			while (line != null)
			{
				sb.append(line).append("\n");
				line = reader.readLine();
			}
			String result1 = sb.toString();
			Log.d("testing line: ", result1);
			JSONObject jsonObj = null;// new JSONObject(line);
			shop = new ArrayList<Shop>();
			JSONArray jArray = new JSONArray(result1);
			for (int i = 0; i < jArray.length(); i++)
			{
				jsonObj = jArray.getJSONObject(i);
				shop.add(new Shop(jsonObj.getInt("id"), jsonObj.getString("address"), jsonObj.getString("name"), jsonObj.getString("lat"), jsonObj.getString("lng"), jsonObj.getString("shoptype")));
			}
			// JSONArray values = jsonObj.getJSONArray("row");
			// for (int i = 0; i < values.length(); i++)
			// {
			// Log.d("address: ", values.getJSONObject(i).getString("address"));
			// Log.d("name: ", values.getJSONObject(i).getString("name"));
			// Log.d("lat: ", values.getJSONObject(i).getString("lat"));
			// Log.d("lng: ", values.getJSONObject(i).getString("lng"));
			// Log.d("distance: ",
			// values.getJSONObject(i).getString("distance"));
			// }
			// while ((line = reader.readLine()) != null)
			// {
			// sb.append(line + "\n");
			// }
			is.close();

			result = jsonObj.getString("address");// sb.toString();
			Log.d("Result in ConnectDB for shopSearch: ", result);
			// if (result.equals("1"))
			// {
			// storeLocResult(1);
			// }
			// else
			// {
			// storeLocResult(0);
			// }
		}

		catch (Exception exc)
		{
			Log.e("log_tag", "Error converting result " + exc.toString());
		}
	}

	public ConnectDB(final Double lat, final Double lng, final String searchType, String searchTerms, final Integer radius)
	{
		// Thread t = new Thread()
		// {
		// public void run()
		// {
		// Looper.prepare(); // For Preparing Message Pool for the child Thread

		// the data to send
		JSONObject json = new JSONObject();
		// ArrayList<NameValuePair> nameValuePairs = new
		// ArrayList<NameValuePair>();
		//
		// nameValuePairs.add(new BasicNameValuePair("lat",
		// Double.toString(lat)));
		// nameValuePairs.add(new BasicNameValuePair("lng",
		// Double.toString(lng)));
		// nameValuePairs.add(new BasicNameValuePair("type",
		// Integer.toString(searchType)));
		// nameValuePairs.add(new BasicNameValuePair("radius",
		// Integer.toString(radius)));
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000); // Timeout
																						// Limit
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "storeLocations.php");
			json.put("lat", Double.toString(lat));
			json.put("lng", Double.toString(lng));
			json.put("type", searchType);
			json.put("searchTerms", searchTerms);
			json.put("radius", Integer.toString(radius));
			httppost.setHeader("json", json.toString());
			StringEntity se = new StringEntity(json.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httppost.setEntity(se);
			// Log.d("se: ", new BufferedReader(new
			// InputStreamReader(se.getContent())).readLine());
			HttpResponse response = httpclient.execute(httppost);
			if (response != null)
			{
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}
		}

		catch (Exception ex)
		{
			Log.e("log_tag", "Error in http connection " + ex.toString());
		}
		// convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			while (line != null)
			{
				sb.append(line).append("\n");
				line = reader.readLine();
			}
			String result1 = sb.toString();
			Log.d("testing line: ", result1);
			JSONObject jsonObj = null;// new JSONObject(line);
			shop = new ArrayList<Shop>();
			JSONArray jArray = new JSONArray(result1);
			arrPro = new Product[jArray.length()];
			shopArray = new Shop[jArray.length()];
			for (int i = 0; i < jArray.length(); i++)
			{
				jsonObj = jArray.getJSONObject(i);
				if (searchType == "store_locations")
				{
					shop.add(new Shop(jsonObj.getString("address"), jsonObj.getString("name"), jsonObj.getString("lat"), jsonObj.getString("lng"), jsonObj.getString("distance")));
					Log.d("Name" + Integer.toString(i) + ": ", jsonObj.getString("name"));
				}
				else
				{
					arrPro[i] = new Product();
					shopArray[i] = new Shop();
					arrPro[i].setId(jsonObj.getInt("id"));
					arrPro[i].setFilename(jsonObj.getString("filename"));
					arrPro[i].setUrl(jsonObj.getString("url"));
					arrPro[i].setPercentdiscount(jsonObj.getInt("percentdiscount"));
					shopArray[i].setName(jsonObj.getString("name"));
				}
			}
			// JSONArray values = jsonObj.getJSONArray("row");
			// for (int i = 0; i < values.length(); i++)
			// {
			// Log.d("address: ",
			// values.getJSONObject(i).getString("address"));
			// Log.d("name: ",
			// values.getJSONObject(i).getString("name"));
			// Log.d("lat: ", values.getJSONObject(i).getString("lat"));
			// Log.d("lng: ", values.getJSONObject(i).getString("lng"));
			// Log.d("distance: ",
			// values.getJSONObject(i).getString("distance"));
			// }
			// while ((line = reader.readLine()) != null)
			// {
			// sb.append(line + "\n");
			// }
			is.close();

			result = jsonObj.getString("address");// sb.toString();
			Log.d("Result in ConnectDB for locations: ", result);
			// if (result.equals("1"))
			// {
			// storeLocResult(1);
			// }
			// else
			// {
			// storeLocResult(0);
			// }
		}

		catch (Exception exc)
		{
			Log.e("log_tag", "Error converting result " + exc.toString());
		}
		// Loop in the message queue
		// Looper.loop();
		// }
		// };
		// t.start();
	}

	// public String getName()
	// {
	// return result;
	// }

	public String getPassword()
	{
		return hpw;
	}

	public Boolean inputResult()
	{
		if (result == "0")
		{
			name = "";
			return false;
		}
		else
		{
			name = result;
			return true;
		}
	}

	public String storeLocResult()
	{
		if (result != null)
		{
			return result;
		}
		else
		{
			return "null";
		}
	}

	public byte[] getHash(int iterationNb, String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < iterationNb; i++)
		{
			digest.reset();
			input = digest.digest(input);
		}
		return Base64.encodeBase64(input);
	}

	public List<Shop> getShop()
	{
		return shop;
	}

	public String getUserID()
	{
		// TODO Auto-generated method stub
		return userID;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getUserEmail()
	{
		return userEmail;
	}
}
