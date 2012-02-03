package socialtour.socialtour;

import java.util.List;

import socialtour.socialtour.models.Shop;

import com.facebook.SessionStore;
import com.facebook.android.Facebook;
import com.google.android.maps.GeoPoint;

import android.app.Application;

public class GlobalVariable extends Application {
	private static final String APP_ID = "222592464462347";
	private Facebook myFbState = new Facebook(APP_ID);
	
	private TwitterApp mTwitter;
	
	private Boolean twitBtn = false;
	private String name = "";
	private String password = "";
	private String email = "";
	private Integer searchType = 0;
	private GeoPoint gPoint;
	private List<Shop> shop = null;

	public Facebook getFBState()
	{
		return myFbState;
	}// End method

	public void setFbState(Facebook f)
	{
		myFbState = f;
	}// End method

	public TwitterApp getTwitState()
	{
		return mTwitter;
	}
	
	public void setTwitState(TwitterApp ta)
	{
		mTwitter = ta;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String n)
	{
		name = n;
	}

	public String getHashPw()
	{
		return password;
	}

	public void setHashPw(String p)
	{
		password = p;
	}

	public String getEm()
	{
		return email;
	}

	public void setEm(String em)
	{
		email = em;
	}

	public void setSearchType(Integer st)
	{
		searchType = st;
	}

	public Integer getSearchType()
	{
		return searchType;
	}

	public void setGeoPoint(GeoPoint point)
	{
		// TODO Auto-generated method stub
		gPoint = point;
	}

	public GeoPoint getGeoPoint()
	{
		return gPoint;
	}

	public void setTwitBtn(boolean b)
	{
		// TODO Auto-generated method stub
		twitBtn = b;
	}
	
	public Boolean getTwitBtn()
	{
		return twitBtn;
	}
	
	public void setShop(List<Shop> shops)
	{
		shop = shops;
	}
	
	public List<Shop> getShop()
	{
		return shop;
	}
}// End Class
