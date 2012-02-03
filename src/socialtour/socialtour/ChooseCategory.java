package socialtour.socialtour;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ChooseCategory extends Activity implements OnClickListener{
	private ImageButton cat1,cat2,cat3,cat4,cat5,cat6,cat7,cat8,cat9;
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.step4);
		
		Container.btn1.setVisibility(View.GONE);
		Container.btn2.setVisibility(View.GONE);
		Container.btn3.setVisibility(View.GONE);
		
		cat1 = (ImageButton)findViewById(R.id.category1);
		cat2 = (ImageButton)findViewById(R.id.category2);
		cat3 = (ImageButton)findViewById(R.id.category3);
		cat4 = (ImageButton)findViewById(R.id.category4);
		cat5 = (ImageButton)findViewById(R.id.category5);
		cat6 = (ImageButton)findViewById(R.id.category6);
		cat7 = (ImageButton)findViewById(R.id.category7);
		cat8 = (ImageButton)findViewById(R.id.category8);
		cat9 = (ImageButton)findViewById(R.id.category9);
		
		cat1.setOnClickListener(this);
		cat2.setOnClickListener(this);
		cat3.setOnClickListener(this);
		cat4.setOnClickListener(this);
		cat5.setOnClickListener(this);
		cat6.setOnClickListener(this);
		cat7.setOnClickListener(this);
		cat8.setOnClickListener(this);
		cat9.setOnClickListener(this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Container.btn1.setVisibility(View.GONE);
		Container.btn2.setVisibility(View.GONE);
		Container.btn3.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		String categoryname = "";
		TabGroupActivity parentActivity = (TabGroupActivity)getParent();
		Intent i = new Intent(getParent(), InputPrice.class);
		if(v==cat1) {
			categoryname = "Men";
		}else if (v==cat2){
			categoryname = "Woman";
		}else if (v==cat3){
			categoryname = "Cosmetics";
		}else if (v==cat4){
			categoryname = "Electronics";
		}else if (v==cat5){
			categoryname = "Household";
		}else if (v==cat6){
			categoryname = "Kids";
		}else if (v==cat7){
			categoryname = "Groceries";
		}else if (v==cat8){
			categoryname = "Games/Books";
		}else if (v==cat9){
			categoryname = "Others";
		}
		Bundle bundle=getIntent().getExtras();
		Uri imageUri = (Uri) bundle.get("pic");
		int tempShopId = bundle.getInt("EMPLOYEE_ID");
		String tempShopName = bundle.getString("EMPLOYEE_NAME");
		i.putExtra("EMPLOYEE_ID", tempShopId);
        i.putExtra("EMPLOYEE_NAME", tempShopName);
        i.putExtra("category", categoryname);
		i.putExtra("pic", imageUri);
		
		parentActivity.startChildActivity("Step 4", i);
	}
}
