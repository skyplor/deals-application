package socialtour.socialtour;

import socialtour.socialtour.Constants;
import com.facebook.SessionEvents;

import com.facebook.FacebookConnector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SocialTourActivity extends Activity {
    /** Called when the activity is first created. */
	private FacebookConnector facebookConnector;
	private ImageButton fbButton, twitButton;
	private Button signUp, login;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        fbButton = (ImageButton) findViewById(R.id.fblogin);
        twitButton = (ImageButton) findViewById(R.id.twtlogin);
        
        signUp = (Button)findViewById(R.id.register);
        login = (Button) findViewById(R.id.signin);
        
        this.facebookConnector = new FacebookConnector(Constants.FACEBOOK_APPID, this, getApplicationContext(), Constants.FACEBOOK_PERMISSION);
        
        fbButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	invokeFB();
            }
        });  
        
        signUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent("socialtour.socialtour.CONTAINER"));
			}
		});
    }
    
    public void invokeFB(){
    	if (facebookConnector.getFacebook().isSessionValid()) {
    		//direct to new page
    	}else{
    		SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {
				
				@Override
				public void onAuthSucceed() {
					
				}
				
				@Override
				public void onAuthFail(String error) {
					
				}
			};
			SessionEvents.addAuthListener(listener);
			facebookConnector.login();
    	}
    	if (facebookConnector.getFacebook().isSessionValid()) {
			startActivity(new Intent("socialtour.socialtour.CONTAINER"));
		}
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.facebookConnector.getFacebook().authorizeCallback(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
}