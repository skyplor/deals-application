package socialtour.socialtour;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class TabGroup1Activity extends TabGroupActivity implements OnClickListener{
    ImageView browse, share, search, settings;
    private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int GALLERY_REQUEST = 1500;
	private static final int CROP_FROM_CAMERA = 1999;
	private static String _path = Environment.getExternalStorageDirectory().getPath();
	private String latString, longString;
	private String latDir, longDir;
	private String type;
	Uri outputFileUri;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        browse = Container.browse;
        share = Container.share;
        search = Container.search;
        settings = Container.settings;
        browse.setOnClickListener(this);
        share.setOnClickListener(this);
        search.setOnClickListener(this);
        settings.setOnClickListener(this);
        Intent i = new Intent("socialtour.socialtour.MAIN");
        startChildActivity("Main", i);
    }
    @Override
	public void onClick(View v) {
    	if (v ==browse){
    	    Intent i = getBaseContext().getPackageManager()
 		             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
           i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(i);
    	}else if (v==share){
    		browse.setEnabled(true);
    		share.setEnabled(false);
    		search.setEnabled(true);
    		settings.setEnabled(true);
    		Intent i = new Intent(this, TabGroup2Activity.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startChildActivity("Share", i);
    	}else if (v==search){
    		browse.setEnabled(true);
    		share.setEnabled(true);
    		search.setEnabled(false);
    		settings.setEnabled(true);
    		Intent i = new Intent("socialtour.socialtour.SEARCH");
    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startChildActivity("Search", i);
    	}else if (v==settings){
    		browse.setEnabled(true);
    		share.setEnabled(true);
    		search.setEnabled(true);
    		settings.setEnabled(false);
    		Intent i = new Intent(this, Settings.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startChildActivity("Settings", i);
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	if (resultCode != RESULT_OK) return;
    	switch (requestCode) {
    	case CAMERA_PIC_REQUEST:
    		type="camera";
    		activateCrop("camera");

    		break;
    		
    	case GALLERY_REQUEST:
    		outputFileUri = data.getData();
    		type = "gallery";
    		activateCrop("gallery");

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
				Intent i2 = new Intent("socialtour.socialtour.BROWSEPLACE");
        		i2.putExtra("pic", newUri2);
        		//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
        		startChildActivity("Browse Place", i2);
	        }
    		break;
    	}
    }
    
    private void activateCrop(String source){
		final String finalsource = source;
		String[] addPhoto = new String[]
		{ "Yes", "No" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
					Intent i = new Intent("socialtour.socialtour.BROWSEPLACE");
	        		i.putExtra("pic", newUri);
			     	//TabGroupActivity parentActivity = (TabGroupActivity)getParent();
	   		     	TabGroup1Activity.this.startChildActivity("Add Product", i);
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
            intent.putExtra("outputX", 320);
            intent.putExtra("outputY", 320);
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
}