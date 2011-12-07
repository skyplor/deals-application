package socialtour.socialtour;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class TabGroup2Activity extends TabGroupActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getIntent().getExtras();
        if (bundle !=null){
        	Uri photoUri = (Uri) bundle.get("pic");
        	Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
        	i.putExtra("pic", photoUri);
        	startChildActivity("Camera", i);
        }
    }
}
