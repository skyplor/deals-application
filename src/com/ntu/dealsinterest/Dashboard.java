package com.ntu.dealsinterest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.ntu.dealsinterest.R;
import com.ntu.dealsinterest.models.UserParticulars;

public class Dashboard extends Activity
{
	// private UserParticulars user;
	private TextView userTxt;
	private SharedPreferences sharedPref;
	private ImageView userPic;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dashboard);

		// TODO Auto-generated method stub
		sharedPref = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);

		userTxt = (TextView) findViewById(R.id.txtuser);
		userTxt.setText("Hi " + sharedPref.getString("userName", ""));

		userPic = (ImageView) findViewById(R.id.imguser);
		URL img_value = null;
		try
		{
			img_value = new URL("http://graph.facebook.com/" + sharedPref.getString("userFBID", "") + "/picture");
			Bitmap userIcon = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
			userPic.setImageBitmap(userIcon);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
