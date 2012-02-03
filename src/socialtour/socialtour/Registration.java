package socialtour.socialtour;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import socialtour.socialtour.models.UserParticulars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class Registration extends Activity {
	private static final int DIALOG_ERR_REG = 0;
	// private int mYear;
	// private int mMonth;
	// private int mDay;

	//private static GlobalVariable applicationcontext;
	//private UserParticulars userS;
	// private String nameS;
	// private String emailS;

	// static final int DATE_DIALOG_ID = 0;
	// private TextView bday;
	// private Button btn;
	// private EditText fname;

	private Button regBtn;
	private EditText name;
	private EditText email;
	private EditText password;


	UserParticulars user;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);

		// SessionStore.restore(facebook, getApplicationContext());

		// fbConnect.login();
		// facebook = new Facebook(APP_ID);
		// mAsyncRunner = new AsyncFacebookRunner(facebook);
		// fbConnect.init(this, facebook, FACEBOOK_PERMISSION);

		name = (EditText) findViewById(R.id.nameTxtBox);
		email = (EditText) findViewById(R.id.emailTxtBox);
		password = (EditText) findViewById(R.id.passwordTxtBox);
		regBtn = (Button) findViewById(R.id.regBtn);

		reginit();
		// Calendar cal = Calendar.getInstance();
		// mYear = cal.get(Calendar.YEAR);
		// mMonth = cal.get(Calendar.MONTH);
		// mDay = cal.get(Calendar.DAY_OF_MONTH);
		// DatePicker dp = (DatePicker) this.findViewById(R.id.bdatePick);
		// dp.init(mYear, mMonth, mDay, null);
		// Bundle data = getIntent().getExtras();
		// if (data != null)
		// {
		// user = data.getParcelable("data");
		// fname.setText(user.getfName());
		// lname.setText(user.getlName());
		// email.setText(user.getEmail());
		// if (user.getGender().equals("male"))
		// {
		// male.setChecked(true);
		// female.setChecked(false);
		// }
		// else if (user.getGender().equals("female"))
		// {
		// female.setChecked(true);
		// male.setChecked(false);
		// }
		// bday.setText("Your Birthdate is: " + user.getBday()[0] + "/" +
		// user.getBday()[1] + "/" + user.getBday()[2]);
		// }
		// btn.setOnClickListener(new OnClickListener()
		// {
		// public void onClick(View v)
		// {
		//
		// showDialog(DATE_DIALOG_ID);
		// }
		// });
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
				ConnectDB connect;
				try
				{
					connect = new ConnectDB(name.getText().toString(), email.getText().toString(), password.getText().toString(), "user_norm");
					if (connect.inputResult())
					{
						GlobalVariable globalVar = ((GlobalVariable) getApplicationContext());
						globalVar.setName(name.getText().toString());
//						globalVar.setfbBtn(false);
						globalVar.setHashPw(connect.getPassword());
						globalVar.setEm(email.getText().toString());

						SharedPreferences login = getSharedPreferences("com.ntu.fypshop", MODE_PRIVATE);
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
		});
	}
	// final OnDateSetListener odsl = new OnDateSetListener()
	// {
	// public void onDateSet(DatePicker view, int year, int month, int
	// dayOfMonth)
	// {
	// bday.setText("Your Birthdate is: " + dayOfMonth + "/" + (month+1) + "/" +
	// year);
	// }
	// };

	// @Override
	// protected Dialog onCreateDialog(int id)
	// {
	// switch (id)
	// {
	// case DATE_DIALOG_ID:
	// if (bdayInt[0] == 0 || bdayInt[1] == 0 || bdayInt[2] == 0)
	// {
	// return new DatePickerDialog(this, odsl, mYear, mMonth, mDay);
	// }
	// else
	// {
	// return new DatePickerDialog(this, odsl, bdayInt[2], bdayInt[1] - 1,
	// bdayInt[0]);
	// }
	// }
	// return null;
	// }

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
