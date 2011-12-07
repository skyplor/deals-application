package socialtour.socialtour;

import android.content.Intent;
import android.os.Bundle;

public class TabGroup3Activity extends TabGroupActivity{
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Intent i = new Intent("socialtour.socialtour.SEARCH");
	        startChildActivity("Search", i);
	    }
}
