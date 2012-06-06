package com.ntu.dealsinterest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ntu.dealsinterest.R;


public class Splash extends Activity {
	private ProgressBar prog;
	private final int DISPLAY_LENGTH = 5000;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        prog = (ProgressBar) findViewById(R.id.progress);
        prog.setVisibility(ProgressBar.VISIBLE);
        
        new Handler().postDelayed(new Runnable() {
        	@Override
        	public void run() {
        	Intent login = new Intent(Splash.this, Container.class);

        	startActivity(login);
        	prog.setVisibility(ProgressBar.INVISIBLE);
        	Splash.this.finish();
        	}

        	}, DISPLAY_LENGTH);
        
    }
}