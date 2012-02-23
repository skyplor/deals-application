package socialtour.socialtour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

public class Startcamera extends TabGroupActivity{
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int GALLERY_REQUEST = 1500;
	private static final int CROP_FROM_CAMERA = 1999;
	private static String _path = "";
	Uri outputFileUri;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openAddPhoto();
        //startCameraActivity();
        
        
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
    
    protected void startGallery(){
    	try {
            // Launch picker to choose photo for selected contact
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            //intent.putExtra("crop", "true");
            //intent.putExtra("aspectX", aspectX);
            //intent.putExtra("aspectY", aspectY);
            //intent.putExtra("outputX", outputX);	
            //intent.putExtra("outputY", outputY);
            //intent.putExtra("scale", scale);
            //intent.putExtra("return-data", true);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile("gallery")));
            //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            //intent.putExtra("noFaceDetection",!faceDetection); // lol, negative boolean noFaceDetection
            //if (circleCrop) {
            //	intent.putExtra("circleCrop", true);
            //}
            
            startActivityForResult(intent, GALLERY_REQUEST);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(thiz, "Im in", Toast.LENGTH_LONG).show();
        }
    }
    
    protected void startCameraActivity()
    {

    	_path=Environment.getExternalStorageDirectory().getPath();
    	//Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	//  File file = new File(_path, "test.jpg");
    	//  Uri outputFileUri = Uri.fromFile(file);
    	//  intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
    	//  startActivityForResult(intent, 0);
    	String currentFile = "cameraFile.jpg";
    	//String currentDateTimeString = DateFormat.getDateInstance().format(new Date()) + ".jpg";
    	//Log.i("MakeMachine", "startCameraActivity()" );
    	File file = new File( _path, currentFile);
    	
    	try {
            if(file.exists() == false) {
                file.createNewFile();
            }

        } catch (IOException e) {
        }
    	
        outputFileUri = Uri.fromFile(file);
        
        
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    	//intent.setType("image/*");
    	//intent.putExtra("crop", "true");
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
		{ "\u662F", "\u5426" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle("\u4FEE\u6539\u56FE\u50CF");

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
					}
					File picFile = new File(realpath);
					Uri newUri = Uri.fromFile(picFile);
					
					Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
	        		i.putExtra("pic", newUri);
			     	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
			     	startChildActivity("Browse Place", i);
					//more than 400kb
					//Intent i = new Intent("FYP2.FYP2.BROWSEPLACE");
	        		//i.putExtra("picfile", picFile);
	        		//startActivity(i);
	        		
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
    		_path=Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
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
    	/*Log.i( "MakeMachine", "resultCode: " + resultCode );
    	switch( resultCode )
    	{
    		case 0:
    			Log.i( "MakeMachine", "User cancelled" );
    			break;
    			
    		case -1:
    			onPhotoTaken();
    			break;
    	}*/
    }
    
    private void openAddPhoto() {

        String[] addPhoto=new String[]{ "Camera" , "Gallery" };
        AlertDialog.Builder dialog=new AlertDialog.Builder(getParent());
        dialog.setTitle("Get your picture from");

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
            	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
            	Intent i = new Intent(Startcamera.this, Container.class);
            	startActivity(i);
            }});
        dialog.show();
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
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        
        int size = list.size();
        
        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        	
            return;
        } else {
        	intent.setData(outputFileUri);
        	//intent.putExtra("crop", "true");
            //intent.putExtra("outputX", 160);
            //intent.putExtra("outputY", 160);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile("camera")));
            //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        	if (size == 1) {
        		Intent i 		= new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop App");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            public void onClick( DialogInterface dialog, int item ) {
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (outputFileUri != null ) {
		                    getContentResolver().delete(outputFileUri, null, null );
		                    outputFileUri = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        }
	}
    
}
