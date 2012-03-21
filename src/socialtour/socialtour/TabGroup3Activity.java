package socialtour.socialtour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class TabGroup3Activity extends TabGroupActivity implements OnClickListener{
	//ImageView home; 	
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        //home = Container.home;
	        //home.setOnClickListener(this);
	        Intent i = new Intent("socialtour.socialtour.SEARCH");
	        startChildActivity("Search", i);
	        //home.setOnClickListener(this);
	    }
	 
	    @Override
		public void onClick(View v) {
	    	//if (v==home){
	    	//	Intent i = getBaseContext().getPackageManager()
	  		//             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
	        //    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        //   startActivity(i);
	    	//}
	    }
}
