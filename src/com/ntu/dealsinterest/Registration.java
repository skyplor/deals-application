package com.ntu.dealsinterest;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ntu.dealsinterest.R;
import com.ntu.dealsinterest.models.UserParticulars;

public class Registration extends Activity
{
	private static final int DIALOG_ERR_REG = 0;
	private static final int EMAIL_ERR_REG = 1;
	private static final int PW_ERR_REG = 2;

	private Button regBtn;
	private EditText name;
	private EditText email;
	private EditText password;
	private EditText cfmPassword;
	private static int REGISTRATION = 2;

	UserParticulars user;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);


		name = (EditText) findViewById(R.id.nameTxtBox);
		email = (EditText) findViewById(R.id.emailTxtBox);
		password = (EditText) findViewById(R.id.passwordTxtBox);
		cfmPassword = (EditText) findViewById(R.id.cfmPwTxtBox);
		regBtn = (Button) findViewById(R.id.regBtn);
		Log.d("in registration, oncreate", "true");
		reginit();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	public void reginit()
	{
		regBtn.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (isValidEmailAddress(email.getText().toString()) && passwordMatches(password.getText().toString(), cfmPassword.getText().toString()))
				{
					ConnectDB connect;
					try
					{
						SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
						Log.d("In registration: reg btn: ", "true");
						connect = new ConnectDB(name.getText().toString(), email.getText().toString(), "", password.getText().toString(), "user_norm", REGISTRATION, Registration.this);
						if (connect.inputResult())
						{
							GlobalVariable globalVar = ((GlobalVariable) getApplicationContext());
							globalVar.setName(name.getText().toString());
							globalVar.setHashPw(connect.getPassword());
							globalVar.setEm(email.getText().toString());

							SharedPreferences.Editor editor = login.edit();
							editor.putString("userID", connect.getUserID());
							editor.putString("userName", connect.getUserName());
							editor.putString("emailLogin", connect.getUserEmail());
							editor.putString("pwLogin", connect.getPassword());
							editor.commit();
							// TODO Auto-generated method stub
							Intent intent = new Intent(v.getContext(), Container.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivityForResult(intent, 2);
						}
						else
						{
							// give error etc.
							showDialog(DIALOG_ERR_REG);
						}
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
				}
				else if(!isValidEmailAddress(email.getText().toString()))
				{
					showDialog(EMAIL_ERR_REG);
				}
				else if (!passwordMatches(password.getText().toString(), cfmPassword.getText().toString()))
				{
					showDialog(PW_ERR_REG);
				}
			}
		});
	}

	public static boolean isValidEmailAddress(String emailAddress)
	{
		if (emailAddress == null || emailAddress == "")
			return false;
		Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher;
		matcher = pattern.matcher(emailAddress);
		return matcher.matches();
	}
	
	public static boolean passwordMatches(String pw, String cfmpw)
	{
		if (pw.equals(cfmpw))
		{
			return true;
		}
		else return false;
	}

	protected AlertDialog onCreateDialog(int id)
	{
		AlertDialog alertDialog;
		// do the work to define the error Dialog
		alertDialog = new AlertDialog.Builder(Registration.this).create();
		alertDialog.setTitle("Registration Error");

		switch (id)
		{
		case DIALOG_ERR_REG:
			alertDialog.setMessage("Unable to register. The email provided has already been registered. Please try again.");
			break;
		case EMAIL_ERR_REG:
			alertDialog.setMessage("The email provided is invalid. Please try again.");
			break;
		case PW_ERR_REG:
			alertDialog.setMessage("The passwords do not match. Please try again.");
			break;

		default:
			alertDialog = null;
		}
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{

				// here you can add functions
				dialog.cancel();

			}
		});
		return alertDialog;
	}
}
