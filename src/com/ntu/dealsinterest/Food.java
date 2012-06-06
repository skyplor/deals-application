package com.ntu.dealsinterest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.ntu.dealsinterest.R;


public class Food extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the Food tab");
        setContentView(textview);
    }
}