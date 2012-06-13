package com.ntu.dealsinterest;

import java.io.BufferedReader;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fedorvlasov.lazylist.LazyAdapter;
import com.ntu.dealsinterest.models.Product;
import com.ntu.dealsinterest.models.Shop;


import android.net.ParseException;
import android.util.Log;

public class ConnectDB
{

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
	String userFbTwNmID = "";

	List<Shop> shop;
	Shop[] shopArray;

	private static int SETTINGS = 1, REGISTRATION = 2, PRODUCTDETAIL = 3, CONTAINER = 4, TWITTERAPP = 5, LOGINPAGE = 0;

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

	public ConnectDB(String email, String password, Integer LoginPageOrCONTAINER)
	{
		if (LoginPageOrCONTAINER == LOGINPAGE)
		{
			// the data to send
			ArrayList<NameValuePair> checkSalt = new ArrayList<NameValuePair>();
			checkSalt.add(new BasicNameValuePair("email", email));

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
				JSONObject jsonObj = new JSONObject(bsalt);
				Log.d("Salt: ", jsonObj.getString("salt"));
				salt = jsonObj.getString("salt");
				inSalt.close();

				String hashedPass = getHash(1, password, salt) + ":" + salt;
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
						userFbTwNmID = json_data.getString("acctid");
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

	}

	public ConnectDB(String name, String emailorScnName, String fbtwitID, String password, String userType, int classnumber, Context context) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		SharedPreferences sharedpref = context.getSharedPreferences("com.ntu.fypshop", Context.MODE_PRIVATE);
		Boolean twitter = (!sharedpref.getString("userDB_TWITID", "").equals(""));
		Boolean fb = (!sharedpref.getString("userDB_FBID", "").equals(""));
		Boolean norm = (!sharedpref.getString("userDB_NMID", "").equals(""));
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String hashedPass = "";

		// the data to send
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("userType", userType));
		Log.d("Class number: ", Integer.toString(classnumber));
		if (classnumber == SETTINGS || classnumber == PRODUCTDETAIL)
		{
			Log.d("in ConnectDB, norm/twitter/fb: ", Boolean.toString(norm)+ " " + Boolean.toString(twitter) + " " + Boolean.toString(fb));
			//nameValuePairs.add(new BasicNameValuePair("connectType", "settings"));
			if(classnumber == SETTINGS)
			{
				nameValuePairs.add(new BasicNameValuePair("connectType", "settings"));
			}
			else
			{
				nameValuePairs.add(new BasicNameValuePair("connectType", "productdetail"));
			}
			if (userType.equals("user_fb") && norm && twitter)
			{
				nameValuePairs.add(new BasicNameValuePair("accType", "1"));
				
			}
			else if (userType.equals("user_fb") && norm && !twitter)
			{
				nameValuePairs.add(new BasicNameValuePair("accType", "2"));
			}
			else if (userType.equals("user_fb") && !norm && twitter)
			{
				nameValuePairs.add(new BasicNameValuePair("accType", "3"));
			}
			else if (userType.equals("user_twit") && norm && fb)
			{
				nameValuePairs.add(new BasicNameValuePair("accType", "4"));
			}
			else if (userType.equals("user_twit") && norm && !fb)
			{
				nameValuePairs.add(new BasicNameValuePair("accType", "5"));
			}
			else if (userType.equals("user_twit") && !norm && fb)
			{
				nameValuePairs.add(new BasicNameValuePair("accType", "6"));
			}
			nameValuePairs.add(new BasicNameValuePair("userID", sharedpref.getString("userID", "")));
		}

		else if (classnumber == REGISTRATION)
		{
			nameValuePairs.add(new BasicNameValuePair("connectType", "registration"));
		}

		else if (classnumber == CONTAINER)
		{
			nameValuePairs.add(new BasicNameValuePair("connectType", "container"));
		}

		if (userType.equals("user_norm"))
		{
			// Uses a secure Random not a simple Random
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			byte[] salt = new byte[8];
			random.nextBytes(salt);

			nameValuePairs.add(new BasicNameValuePair("email", emailorScnName));
			String saltstr = new String(Base64.encodeBase64(salt), "UTF8");
			hashedPass = getHash(1, password, saltstr)+":"+saltstr;
			nameValuePairs.add(new BasicNameValuePair("password", hashedPass));

			for (int i = 0; i < 4; i++)
			{
				Log.d(Integer.toString(i), nameValuePairs.get(i).getName() + ": " + nameValuePairs.get(i).getValue());
			}

		}

		else if (userType.equals("user_fb"))
		{
			nameValuePairs.add(new BasicNameValuePair("email", emailorScnName));
			nameValuePairs.add(new BasicNameValuePair("fbID", fbtwitID));

		}

		else if (userType.equals("user_twit"))
		{
			nameValuePairs.add(new BasicNameValuePair("email", emailorScnName));
			nameValuePairs.add(new BasicNameValuePair("twitID", fbtwitID));
		}
		// http post
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + "insertUser.php");
			for (NameValuePair nvp : nameValuePairs)
			{
				Log.d("NameValuePairs: ", nvp.getName() + ": " + nvp.getValue());
			}
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
			String line = null;
			line = reader.readLine();
			is.close();

			result = line;
			if (result == null || result.equals("null"))
			{
				Log.d("Result in ConnectDB: ", "null");
			}
			if (!result.equals("null"))
			{
				Log.d("Result in ConnectDB: ", result);
				try
				{
					JSONArray jArray = new JSONArray(result);
					JSONObject json_data = null;
					for (int i = 0; i < jArray.length(); i++)
					{
						json_data = jArray.getJSONObject(i);
						userID = json_data.getString("id");
						userName = json_data.getString("name");
						userFbTwNmID = json_data.getString("acctid");
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
			Log.e("ConnectDB exception: ", "Error converting result " + exc.toString());
		}

	}

	public ConnectDB(String address, String name)
	{
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
			Log.d("in ConnectDB, shopresult: ", result1);
			JSONObject jsonObj = null;
			shop = new ArrayList<Shop>();
			JSONArray jArray = new JSONArray(result1);
			for (int i = 0; i < jArray.length(); i++)
			{
				jsonObj = jArray.getJSONObject(i);
				shop.add(new Shop(jsonObj.getInt("id"), jsonObj.getString("address"), jsonObj.getString("name"), jsonObj.getString("lat"), jsonObj.getString("lng")));
			}
			is.close();

			result = jsonObj.getString("address");
			Log.d("Result in ConnectDB for shopSearch: ", result);
		}

		catch (Exception exc)
		{
			Log.e("log_tag", "Error converting result " + exc.toString());
		}
	}

	public ConnectDB(final Double lat, final Double lng, final String searchType, String searchTerms, final Integer radius)
	{
		// the data to send
		JSONObject json = new JSONObject();
		
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
			JSONObject jsonObj = null;
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
					arrPro[i].setPercentdiscount(jsonObj.getString("percentdiscount"));
					shopArray[i].setName(jsonObj.getString("name"));
				}
			}
			is.close();

			result = jsonObj.getString("address");// sb.toString();
			Log.d("Result in ConnectDB for locations: ", result);
		}

		catch (Exception exc)
		{
			Log.e("log_tag", "Error converting result " + exc.toString());
		}
	}

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

	public String getHash(int iterationNb, String password, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		String result = null;
		String input = password + salt;
		byte[] source;
		try
		{
			// Get byte according by specified coding.
			source = input.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			source = input.getBytes();
		}
		char hexDigits[] =
		{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			// The result should be one 128 integer
			byte temp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++)
			{
				byte byte0 = temp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			result = new String(str);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
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

	public String getUserFbTwNmID()
	{
		return userFbTwNmID;
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
