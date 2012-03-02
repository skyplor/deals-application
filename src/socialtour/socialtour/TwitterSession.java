package socialtour.socialtour;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

import twitter4j.auth.AccessToken;

public class TwitterSession {
	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String TWEET_AUTH_KEY = "auth_key";
	private static final String TWEET_AUTH_SECRET_KEY = "auth_secret_key";
	private static final String TWEET_USER_NAME = "userName";
	private static final String TWEET_USER_ID = "twitID";
	private static final String USER_TID = "userDB_TWITID";
	private static final String USER_ID = "userID";
	private static final String SHARED = "com.ntu.fypshop";

	public TwitterSession(Context context)
	{
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);

		editor = sharedPref.edit();
	}

	public void storeAccessToken(AccessToken accessToken, String username, String userid, String tid, String id)
	{
		editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
		editor.putString(TWEET_USER_NAME, username);
		editor.putString(TWEET_USER_ID, userid);
		editor.putString(USER_TID, tid);
		editor.putString(USER_ID, id);

		editor.commit();
	}

	public void resetAccessToken()
	{
		editor.putString(TWEET_AUTH_KEY, null);
		editor.putString(TWEET_AUTH_SECRET_KEY, null);
		editor.putString(TWEET_USER_NAME, null);
		editor.putString(TWEET_USER_ID, null);
		editor.putString(USER_TID, null);
//		editor.putString(USER_ID, null);

		editor.commit();
	}

	public String getUsername()
	{
		return sharedPref.getString(TWEET_USER_NAME, "");
	}

	public AccessToken getAccessToken()
	{
		String token = sharedPref.getString(TWEET_AUTH_KEY, null);
		String tokenSecret = sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);

		if (token != null && tokenSecret != null)
			return new AccessToken(token, tokenSecret);
		else
			return null;
	}
}
