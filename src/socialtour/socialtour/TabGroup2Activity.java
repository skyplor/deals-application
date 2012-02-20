package socialtour.socialtour;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class TabGroup2Activity extends TabGroupActivity implements OnClickListener{
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int GALLERY_REQUEST = 1500;
	private static String _path = "";
	Uri outputFileUri;
	Button btnShare;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        Container.btn1.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setVisibility(ImageView.INVISIBLE);
        Container.btn3.setVisibility(ImageView.INVISIBLE);
        //Container.btn1.setImageResource(R.drawable.hotbuttondynamic);
        //Container.btn2.setImageResource(R.drawable.nearbybuttondynamic);
        //Container.btn3.setImageResource(R.drawable.latestbuttondynamic);
        //Container.btn1.setEnabled(false);
        //Container.btn2.setEnabled(false);
        //Container.btn3.setEnabled(false);
        
        btnShare = (Button)findViewById(R.id.sharebutton);
        btnShare.setOnClickListener(this);
        //openAddPhoto();
        //if (bundle !=null){
        	//Uri photoUri = (Uri) bundle.get("pic");
        	//Intent i = new Intent("socialtour.socialtour.STARTCAMERA");
        	//Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
        	//i.putExtra("pic", photoUri);
        	//startChildActivity("Camera", i);
        //}
    }
    
	@Override
	public void onResume(){
		super.onResume();
		Container.btn1.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setVisibility(ImageView.INVISIBLE);
        Container.btn3.setVisibility(ImageView.INVISIBLE);
		//Container.btn1.setImageResource(R.drawable.hotbuttondynamic);
        //Container.btn2.setImageResource(R.drawable.nearbybuttondynamic);
        //Container.btn3.setImageResource(R.drawable.latestbuttondynamic);
        //Container.btn1.setImageResource(R.drawable.transparent);
        //Container.btn2.setImageResource(R.drawable.transparent);
        //Container.btn3.setImageResource(R.drawable.transparent);
        //Container.btn1.setEnabled(false);
        //Container.btn2.setEnabled(false);
        //Container.btn3.setEnabled(false);
	}
    
    @Override
	public void onClick(View v) {
    	if(v==btnShare) {
    		openAddPhoto();
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	if (requestCode == CAMERA_PIC_REQUEST) {
    	//BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 2;
    	
    	//Bitmap bitmap = BitmapFactory.decodeFile( _path + "test.jpg", options );
    	//_image = (ImageView) findViewById(R.id.imageView2);
    	//_image.setImageBitmap(bitmap);
    		//Bundle bundle=data.getExtras();
	        //Bitmap pic = (Bitmap) bundle.get("pic");
    		//Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
    		_path=Environment.getExternalStorageDirectory().getPath();
    		File file = new File( _path, "testing.jpg");
    		outputFileUri = Uri.fromFile(file);
    		Bundle bundle=getIntent().getExtras();
    		if (bundle == null){
    			/*
    			Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
        		i.putExtra("pic", outputFileUri);
        		startActivity(i);*/
        		
        		Intent i = new Intent(this, Browseplace.class);
        		i.putExtra("pic", outputFileUri);
		     	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
		     	startChildActivity("Browse Place", i);
    		}else{
    			/*
    			Intent i = new Intent("socialtour.socialtour.ATTRACTION");
        		i.putExtra("pic", outputFileUri);
        		i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME",0));
            	i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
        		startActivity(i);*/
        		
        		Intent i = new Intent(this, Attraction.class);
        		i.putExtra("pic", outputFileUri);
        		i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME",0));
            	i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
		     	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
		     	startChildActivity("Add Product", i);
    		}
    		
    		//_image = (ImageView) findViewById(R.id.imageView2);
    		//_image.setImageBitmap(thumbnail);
    	}else if (requestCode == GALLERY_REQUEST){
    		Bundle bundle=getIntent().getExtras();
    		if (bundle == null){
    			/*
    			Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
        		outputFileUri = data.getData();
        		i.putExtra("pic", outputFileUri);
        		startActivity(i);*/
        		
        		Intent i = new Intent(this, Browseplace.class);
        		outputFileUri = data.getData();
        		i.putExtra("pic", outputFileUri);
		     	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
		     	startChildActivity("Browse Place", i);
    		}else{
    			/*
    			Intent i = new Intent("socialtour.socialtour.ATTRACTION");
        		outputFileUri = data.getData();
        		i.putExtra("pic", outputFileUri);
        		i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME",0));
            	i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
        		startActivity(i);*/
        		
        		Intent i = new Intent(this, Attraction.class);
        		outputFileUri = data.getData();
        		i.putExtra("pic", outputFileUri);
        		i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME",0));
            	i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
		     	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
		     	startChildActivity("Add Product", i);
    		}
    		
    	}
    }
    
    private void openAddPhoto() {

        String[] addPhoto=new String[]{ "Camera" , "Gallery" };
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Choose picture from");

        dialog.setItems(addPhoto,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(id==0){
                	dialog.dismiss();
                    startCameraActivity();
                }
                if(id==1){
                	dialog.dismiss();
                    startGallery();
                }
            }
        });     

        dialog.setNeutralButton("Cancel",new android.content.DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();   
            }});
        dialog.show();
    }
    
    protected void startGallery(){
    	Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(intent, GALLERY_REQUEST);
    }
    
    protected void startCameraActivity()
    {
    	_path=Environment.getExternalStorageDirectory().getPath();
    	//_path=Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
    	//Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	//  File file = new File(_path, "test.jpg");
    	//  Uri outputFileUri = Uri.fromFile(file);
    	//  intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
    	//  startActivityForResult(intent, 0);
    	
    	//Log.i("MakeMachine", "startCameraActivity()" );
    	File file = new File( _path, "testing.jpg");
    	
    	try {
            if(file.exists() == false) {
                file.createNewFile();
            }

        } catch (IOException e) {
        }
    	
        outputFileUri = Uri.fromFile(file);
        
        
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,outputFileUri);
    	
    	
    	//Uri outputFileUri = Uri.fromFile( file );
    	
    	//Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	//intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
    	
    	this.startActivityForResult( intent, CAMERA_PIC_REQUEST);
    }
    
}
