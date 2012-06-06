package com.ntu.dealsinterest.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class UserParticulars implements Parcelable {
	private String fname;
	private String lname;
	private String email;
	private String gender;
	private String birthday;

	public UserParticulars(String fnm, String lnm, String eml, String gdr, String bday)
	{
		fname = fnm;
		lname = lnm;
		email = eml;
		gender = gdr;
		birthday = bday;
	}

	public String getfName()
	{
		return fname;
	}

	public String getlName()
	{
		return lname;
	}

	public String getEmail()
	{
		return email;
	}

	public String getGender()
	{
		return gender;
	}

	public Integer[] getBday()
	{
		int mth, day, year;
		Integer[] bday = new Integer[3];
		for (int i = 0; i < 3; i++)
		{
			bday[i] = 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try
		{
			Date date = sdf.parse(birthday);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			day = (cal.get(Calendar.DAY_OF_MONTH));
			mth = (cal.get(Calendar.MONTH));
			year = (cal.get(Calendar.YEAR));
			bday[0] = day;
			bday[1] = mth+1;
			bday[2] = year;
		}
		catch (ParseException pe)
		{
			Log.i("Parse Date error: ", "The date povided is invalid.");
		}
		return bday;
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		// TODO Auto-generated method stub
		dest.writeStringArray(new String[]
		{ fname, lname, email, gender, birthday });

	}

	// this is used to regenerate your object. All Parcelables must have a
	// CREATOR that implements these two methods
	public static final Parcelable.Creator<UserParticulars> CREATOR = new Parcelable.Creator<UserParticulars>()
	{
		public UserParticulars createFromParcel(Parcel in)
		{
			return new UserParticulars(in);
		}

		public UserParticulars[] newArray(int size)
		{
			return new UserParticulars[size];
		}
	};

	// example constructor that takes a Parcel and gives you an object populated
	// with it's values
	private UserParticulars(Parcel in)
	{
		String[] data = new String[5];
		in.readStringArray(data);
		this.fname = data[0];
		this.lname = data[1];
		this.email = data[2];
		this.gender = data[3];
		this.birthday = data[4];
	}
}
