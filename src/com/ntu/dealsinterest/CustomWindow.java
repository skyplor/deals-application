package com.ntu.dealsinterest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.ntu.dealsinterest.R;

public class CustomWindow extends Activity {
	protected TextView title;
	protected Button btn1,btn2,btn3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
		title = (TextView) findViewById(R.id.title);
		btn1 = (Button)findViewById(R.id.headerLatest);
		btn2 = (Button)findViewById(R.id.headerHot);
		btn3 = (Button)findViewById(R.id.headerNearby);
	}
}
