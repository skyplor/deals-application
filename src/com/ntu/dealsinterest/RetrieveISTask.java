package com.ntu.dealsinterest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class RetrieveISTask extends AsyncTask<String, Void, String>
{
	String p = "", userType, hashedPass, connectType = "";
	ConnectDB connectDB;
	Main main;
	ArrayList<NameValuePair> nameValuePairs;
	InputStream is;
	String result = "";
	HttpClient httpclient;

	String TAG = "RETRIEVEISTASK";

	public RetrieveISTask()
	{
		// TODO Auto-generated constructor stub
	}

	public RetrieveISTask(String connectType)
	{
		this.connectType = connectType;
	}

	@Override
	protected String doInBackground(String... params)
	{
		try
		{
			httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.CONNECTIONSTRING + p);
			for (NameValuePair nvp : nameValuePairs)
			{
				Log.d("NameValuePairs: ", nvp.getName() + ": " + nvp.getValue());
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			if (response != null)
			{
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			}
			if (is != null)
			{
				try
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					StringBuilder sb = new StringBuilder();
					String line = null;
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
			}
			return result;
		}
		catch (Exception e)
		{
			Log.e(TAG, "Error in http connection" + e.toString());
			return null;
		}
	}

	protected void onPostExecute(String result)
	{
		// TODO: check this.exception
		// TODO: do something with the feed
		if (connectType.equals("insertUser"))
		{
			connectDB.setHpw(hashedPass);
			connectDB.setUserType(userType);
			connectDB.processIS(result, connectType);
		}
		else if (connectType.equals("browse"))
		{
			main.processIS(result);
			// BufferedReader reader;
			// try
			// {
			// reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			//
			// StringBuilder sb = new StringBuilder();
			// sb.append(reader.readLine() + "\n");
			// String line = "0";
			// while ((line = reader.readLine()) != null)
			// {
			// sb.append(line + "\n");
			// }
			// is.close();
			// String result = sb.toString();
			//
			// Log.d("Retrieve IS Task (MAIN browse): ", result);
			// }
			// catch (UnsupportedEncodingException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// catch (IOException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
		else if (connectType.equals("getSalt"))
		{
			connectDB.processIS(result, connectType);
		}
		else if (connectType.equals("authenticateUser"))
		{
			connectDB.processIS(result, connectType);
		}
		httpclient.getConnectionManager().shutdown();
		// is.close();
	}

	public String getP()
	{
		return p;
	}

	public void setP(String p)
	{
		this.p = p;
	}

	public String getUserType()
	{
		return userType;
	}

	public void setUserType(String userType)
	{
		this.userType = userType;
	}

	public String getHashedPass()
	{
		return hashedPass;
	}

	public void setHashedPass(String hashedPass)
	{
		this.hashedPass = hashedPass;
	}

	public ArrayList<NameValuePair> getNameValuePairs()
	{
		return nameValuePairs;
	}

	public void setNameValuePairs(ArrayList<NameValuePair> nameValuePairs)
	{
		this.nameValuePairs = nameValuePairs;
	}

	public ConnectDB getConnectDB()
	{
		return connectDB;
	}

	public void setConnectDB(ConnectDB connectDB)
	{
		this.connectDB = connectDB;
	}

	public Main getMain()
	{
		return main;
	}

	public void setMain(Main main)
	{
		this.main = main;
	}
}
