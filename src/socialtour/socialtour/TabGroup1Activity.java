package socialtour.socialtour;

import android.content.Intent;
import android.os.Bundle;

public class TabGroup1Activity extends TabGroupActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent("socialtour.socialtour.MAIN");
        startChildActivity("Main", i);
    }
}