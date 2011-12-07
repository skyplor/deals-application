package socialtour.socialtour;

//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.google.android.maps.GeoPoint;
import socialtour.socialtour.TwitterApp.TwDialogListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ProductPage extends Activity {

	private static final String APP_DOWNLOAD_LINK = "https://www.facebook.com/apps/application.php?id=222592464462347";
	private static GlobalVariable globalVar;
	private ImageButton fbshareBtn, twitshareBtn;
	private TextView shopinfo;
	private Facebook mFacebook;
	private TwitterApp mTwitter;
	AsyncFacebookRunner asyncRunner;
	private String status;

	private static final String twitter_consumer_key = "L0UuqLWRkQ0r9LkZvMl0Zw";
	private static final String twitter_secret_key = "CelQ7Bvl0mLGGKw6iiV3cDcuP0Lh1XAI6x0fCF0Pd4";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);

		globalVar = ((GlobalVariable) getApplicationContext());
		mFacebook = globalVar.getFBState();

		fbshareBtn = (ImageButton) findViewById(R.id.fbShareBtn);
		twitshareBtn = (ImageButton) findViewById(R.id.twitShareBtn);
		shopinfo = (TextView) findViewById(R.id.shopInfo);

		mTwitter = new TwitterApp(this, twitter_consumer_key, twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);
		globalVar.setTwitState(mTwitter);

		status = "Check out this promotion!\nPromotion: MyTestingApp";
		fbshareBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{

				// post on user's wall.
				// mFacebook.dialog(ProductPage.this, "feed", new
				// PostDialogListener());
				postWithDialog(ProductPage.this, "https://a248.e.akamai.net/assets.github.com/images/modules/header/logov6.png");
			}
		});

		twitshareBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{

				if (!mTwitter.hasAccessToken())
				{
					mTwitter.authorize();
				}
				else
				{
					final Dialog twitDialog = new Dialog(ProductPage.this);

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

		shopinfo.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				GeoPoint point = new GeoPoint(1301282, 103839630);
				globalVar.setGeoPoint(point);
				Intent intent = new Intent(v.getContext(), Shopdetail.class);
				TabGroupActivity parentActivity = (TabGroupActivity)getParent();
				parentActivity.startChildActivity("Shop Details", intent);
				
				
			}
		});
	}

	private void postReview(String review)
	{
		// post to server

		Toast.makeText(this, "Review posted", Toast.LENGTH_SHORT).show();
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

			Toast.makeText(ProductPage.this, text, Toast.LENGTH_SHORT).show();
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
			Toast.makeText(ProductPage.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel()
		{
			// Return to main activity

		}
	};

	/**
	 * This method posts a on the user's wall without displaying a dialog.
	 * 
	 * @param message
	 *            - the message to post on the wall
	 * @param link
	 *            - a link to post on the wall
	 * @param name
	 *            - the link's name
	 * @param caption
	 *            - the link's description
	 * @param imageUrl
	 *            - a link to an image to post on the wall
	 */
	public void postWithoutDialog(String message, String link, String name, String caption, String imageUrl)
	{
		Bundle params = new Bundle();
		params.putString("message", message);
		params.putString("link", link);
		params.putString("name", name);
		params.putString("caption", caption);
		params.putString("picture", imageUrl);

		asyncRunner = new AsyncFacebookRunner(mFacebook);
		// asyncRunner.request("me/feed", params, "POST", new
		// WallPostRequestListener() {
		// public void onMalformedURLException(MalformedURLException e) {
		// e.printStackTrace();
		// }
		// public void onIOException(IOException e) {
		// e.printStackTrace();
		// }
		// public void onFileNotFoundException(FileNotFoundException e) {
		// e.printStackTrace();
		// }
		// public void onFacebookError(FacebookError e) {
		// e.printStackTrace();
		// }
		// public void onComplete(String response) {
		// Log.i("WALL", "wallpost ended successfully");
		// }
		// });
	}

	/**
	 * This method displays a dialog that allows the user to post on his wall.
	 * 
	 * @param context
	 *            - the current context
	 * @param imageUrl
	 *            - a link to an image to post on the wall
	 */
	public void postWithDialog(Context context, String imageUrl)
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
		parameters.putString("attachment", "{\"name\":\"MyTestingApp\"," + "\"href\":\"" + APP_DOWNLOAD_LINK + "\"," + "\"description\":\"Uploaded via android emulator using MyTestingApp =) \"," + "\"media\":[{\"type\":\"image\",\"src\":\"" + imageUrl + "\",\"href\":\"" + APP_DOWNLOAD_LINK + "\"}]" + "}");
		// display the user dialog
		mFacebook.dialog(context, "stream.publish", parameters, new WallPostDialogListener());
	}

	public class WallPostRequestListener extends BaseRequestListener {

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
			ProductPage.this.runOnUiThread(new Runnable()
			{
				public void run()
				{
					// mText.setText(text);
				}
			});
		}
	}

	public class WallPostDeleteListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state)
		{
			if (response.equals("true"))
			{
				Log.d("Facebook-Example", "Successfully deleted wall post");
				ProductPage.this.runOnUiThread(new Runnable()
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

	public class WallPostDialogListener extends BaseDialogListener {

		public void onComplete(Bundle values)
		{
			final String postId = values.getString("post_id");
			if (postId != null)
			{
				Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);

				asyncRunner = new AsyncFacebookRunner(mFacebook);
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
}
