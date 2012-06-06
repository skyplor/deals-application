package com.ntu.dealsinterest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.ntu.dealsinterest.R;

public class TabGroup2Activity extends Activity implements OnClickListener{
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int GALLERY_REQUEST = 1500;
	private static final int CROP_FROM_CAMERA = 1999;
	private static String _path = Environment.getExternalStorageDirectory().getPath();
	private String latString, longString;
	private String latDir, longDir;
	private String type;
	public static Uri outputFileUri;
	Button btnShare;
	//ImageView home;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        Container.btn1.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setVisibility(ImageView.INVISIBLE);
        Container.btn3.setVisibility(ImageView.INVISIBLE);
		Container.map.setVisibility(ImageView.INVISIBLE);
        //home = Container.home;
        //home.setOnClickListener(this);
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
        	//Intent i = new Intent("com.ntu.dealsinterest.STARTCAMERA");
        	//Intent i = new Intent("com.ntu.dealsinterest.BROWSEPLACE");
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
		Container.map.setVisibility(ImageView.INVISIBLE);
        //Container.home.setOnClickListener(this);
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
    		//Intent i = new Intent("com.ntu.dealsinterest.STARTCAMERA");
    		//startChildActivity("Start Camera", i);
    		imageOptions();
    	}
    	//else if (v==home){
    	//	Intent i = getBaseContext().getPackageManager()
  		//             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        //       i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //       startActivity(i);
    	//}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	if (resultCode != RESULT_OK) return;
    	switch (requestCode) {
    	case CAMERA_PIC_REQUEST:
    		type="camera";
    		doCrop();
    		//activateCrop("camera");

    		break;
    		
    	case GALLERY_REQUEST:
    		outputFileUri = data.getData();
    		type = "gallery";
    		doCrop();
    		//activateCrop("gallery");

            break;
            
    	case CROP_FROM_CAMERA:
    		Bitmap photo = null;
    		Bundle extras2 = data.getExtras();
	        if (extras2 != null) {	        	
				photo = extras2.getParcelable("data");
				String newfilepath = postProcessing(photo);
				if (type.equals("camera")){
				try{
					calculateDMS(Main.point.getLatitudeE6() / 1E6, Main.point.getLongitudeE6() / 1E6);
					ExifInterface exif = new ExifInterface(newfilepath);
					exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latString);
					exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latDir);
					exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longString);
					exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longDir);
					exif.saveAttributes();
				}catch(IOException e){
					e.fillInStackTrace();
				}
				}
				File picFile2 = new File(newfilepath);
				Uri newUri2 = Uri.fromFile(picFile2);
				Intent i2 = new Intent(getParent(), Browseplace.class);
        		i2.putExtra("pic", newUri2);
        		TabGroupActivity parentActivity = (TabGroupActivity)getParent();
   		     	parentActivity.startChildActivity("Browse Place "+ TabGroup1Activity.intentCount, i2);
   		     TabGroup1Activity.intentCount++;
	        }
    		break;
    	}
    	/*
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
    			
    			Intent i = new Intent("com.ntu.dealsinterest.BROWSEPLACE");
        		i.putExtra("pic", outputFileUri);
        		startActivity(i);
        		
        		Intent i = new Intent(this, Browseplace.class);
        		i.putExtra("pic", outputFileUri);
		     	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
		     	startChildActivity("Browse Place", i);
    		}*/
    		
    		//_image = (ImageView) findViewById(R.id.imageView2);
    		//_image.setImageBitmap(thumbnail);
    		/*
    	}else if (requestCode == GALLERY_REQUEST){
    		Bundle bundle=getIntent().getExtras();
    		if (bundle == null){
    			
    			Intent i = new Intent("com.ntu.dealsinterest.BROWSEPLACE");
        		outputFileUri = data.getData();
        		i.putExtra("pic", outputFileUri);
        		startActivity(i);
        		
        		Intent i = new Intent(this, Browseplace.class);
        		outputFileUri = data.getData();
        		i.putExtra("pic", outputFileUri);
		     	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
		     	startChildActivity("Browse Place", i);
    		}
    	}*/
    }
    
    private void imageOptions(){
		String[] addPhoto = new String[]
		{ "Camera", "Gallery" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle("Choose picture from");

		dialog.setItems(addPhoto, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.dismiss();
				if (id == 0)
				{
					startCameraActivity();
				}
				if (id == 1)
				{
					startGallery();
				}
			}
		});

		dialog.setNeutralButton("Cancel", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
	}
    /*
    protected void startGallery(){
    	Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(intent, GALLERY_REQUEST);
    }*/
    
    protected void startGallery(){
    	try {
            // Launch picker to choose photo for selected image
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            getParent().startActivityForResult(intent, GALLERY_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.fillInStackTrace();
        }
    }
    
    /*
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
    */
    
    protected void startCameraActivity()
    {

    	_path=Environment.getExternalStorageDirectory().getPath();
    	String currentFile = "cameraFile.jpg";
    	File file = new File( _path, currentFile);
    	try {
            if(file.exists() == false) {
                file.createNewFile();
            }
        }catch (IOException e) {
        	e.printStackTrace();
        }
    	
        outputFileUri = Uri.fromFile(file);
        
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
    	try{	
    	intent.putExtra("return-data", true);
    	getParent().startActivityForResult( intent, CAMERA_PIC_REQUEST);
    	}catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    private void activateCrop(String source){
		final String finalsource = source;
		String[] addPhoto = new String[]
		{ "Yes", "No" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle("Crop your image?");

		dialog.setItems(addPhoto, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.dismiss();
				if (id == 0)
				{	
					doCrop();
				}
				if (id == 1)
				{
					String realpath = "";
					if (finalsource.equals("gallery")){
						realpath = getRealPathFromURI(outputFileUri);
					}else{
						realpath = outputFileUri.getPath();
						try{
							calculateDMS(Main.point.getLatitudeE6() / 1E6, Main.point.getLongitudeE6() / 1E6);
							ExifInterface exif = new ExifInterface(realpath);
							exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latString);
							exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latDir);
							exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longString);
							exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longDir);
							exif.saveAttributes();
						}catch(IOException e){
							e.fillInStackTrace();
						}
					}
					File picFile = new File(realpath);
					Uri newUri = Uri.parse("file://"+realpath);
					Intent i = new Intent("com.ntu.dealsinterest.BROWSEPLACE");
	        		i.putExtra("pic", newUri);
			     	TabGroupActivity parentActivity = (TabGroupActivity)getParent();
	   		     	parentActivity.startChildActivity("Browse Place " + TabGroup1Activity.intentCount, i);
	   		     TabGroup1Activity.intentCount++;
				}
			}
		});

		dialog.setNeutralButton("Cancel", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// TabGroupActivity parentActivity =
				// (TabGroupActivity)getParent();
				// Intent i = new Intent(this, Container.class);
				// startActivity(i);
			}
		});
		dialog.show();
	}
    
    public void calculateDMS(double latitude, double longitude){
    	//latitude
    	int degrees = (int)latitude;
    	double minutes = latitude - degrees;
    	minutes = minutes * 60;
    	int intMinutes = (int)minutes;
    	double seconds = minutes - intMinutes;
    	seconds = seconds * 60;
    	int intSeconds = (int)seconds;
    	latString =  degrees + "/1," + intMinutes + "/1," + intSeconds + "/1";
    	if (latitude < 0){
    		latDir = "S";
    	}else{
    		latDir = "N";
    	}
    	//longitude
    	int degrees2 = (int)longitude;
    	double minutes2 = longitude - degrees2;
    	minutes2 = minutes2 * 60;
    	int intMinutes2 = (int)minutes2;
    	double seconds2 = minutes2 - intMinutes2;
    	seconds2 = seconds2 * 60;
    	int intSeconds2 = (int)seconds2;
    	longString =  degrees2 + "/1," + intMinutes2 + "/1," + intSeconds2 + "/1";
    	if (longitude < 0){
    		longDir = "W";
    	}else{
    		longDir = "E";
    	}
    }
    
	public String getRealPathFromURI(Uri contentUri){
		String[] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery(contentUri,proj,null,null,null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		
		return cursor.getString(column_index);
	}
	
    private String postProcessing(Bitmap photo){
    	String dirname = Environment.getExternalStorageDirectory().getPath();
		String newfilepath = "";
		try {
			newfilepath = dirname + "/croppedImage.jpg";
		  FileOutputStream fos = new FileOutputStream(newfilepath);
		  photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		  fos.flush();
		  fos.close();
		  Log.d("done","done");
		} catch (FileNotFoundException e) {
		  e.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		}
		
		File f = new File(outputFileUri.getPath());            
        
        if (f.exists()) f.delete();
        
        return newfilepath;
    }
    
    private void doCrop() {
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        int size = list.size();
        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
            return;
        } else {
        	intent.setData(outputFileUri);
            intent.putExtra("outputX", 350);
            intent.putExtra("outputY", 350);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
        	if (size == 1) {
        		Intent i = new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	i.setComponent(new ComponentName(res.activityInfo.packageName, 
	        			res.activityInfo.name));
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} 
        }
	}
    
    @Override
    public void onBackPressed() {
    	Container.share.setEnabled(true);
    }
    /*
    @Override
    public void onBackPressed() {
    	LocalActivityManager manager = getLocalActivityManager();
    	Activity currentact = manager.getCurrentActivity();
    	if (currentact ==null){
    		this.finish();
    	}else{
    		String currentclass = manager.getCurrentActivity().getClass().toString();
    		if (currentclass.equals("class com.ntu.dealsinterest.TabGroup2Activity")){
    			this.finish();
    		}else if (currentclass.equals("class com.ntu.dealsinterest.Productdetail")){
    			//restart to TabGroup2Activity
    			Intent i = new Intent(this, TabGroup2Activity.class);
    			startChildActivity("Back to Main", i);
    		}else if (currentclass.equals("class com.ntu.dealsinterest.Browseplace")){
    			//restart to TabGroup2Activity
    			String currentTag = Container.tabHost.getCurrentTabTag();
    			manager.destroyActivity("share", true);
    			manager.startActivity("share", new Intent(getParent(), TabGroup2Activity.class));
    			//getLocalActivityManager().startActivity(id, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    			//currentact.finish();
    			//Intent i = new Intent(this, TabGroup2Activity.class);
    			//startChildActivity("Back to Main", i);
    		}
    	}
    }*/
}
