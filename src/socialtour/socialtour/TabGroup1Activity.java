package socialtour.socialtour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class TabGroup1Activity extends TabGroupActivity implements OnClickListener{
    ImageView home;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        home = Container.home;
        home.setOnClickListener(this);
        Intent i = new Intent("socialtour.socialtour.MAIN");
        startChildActivity("Main", i);
    }
    @Override
	public void onClick(View v) {
    	if (v==home){
    		Intent i = getBaseContext().getPackageManager()
  		             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
    	}
    }
}