package com.ntu.dealsinterest;

import java.io.File;
import java.io.IOException;

import com.ntu.dealsinterest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class TabGroup2Activity extends Activity implements OnClickListener{
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int GALLERY_REQUEST = 1500;
	private static String _path = Environment.getExternalStorageDirectory().getPath();
	public static Uri outputFileUri;
	Button btnShare;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        Container.btn1.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setVisibility(ImageView.INVISIBLE);
        Container.btn3.setVisibility(ImageView.INVISIBLE);
		Container.map.setVisibility(ImageView.INVISIBLE);
        
        btnShare = (Button)findViewById(R.id.sharebutton);
        btnShare.setOnClickListener(this);
    }
    
	@Override
	public void onResume(){
		super.onResume();
		Container.btn1.setVisibility(ImageView.INVISIBLE);
        Container.btn2.setVisibility(ImageView.INVISIBLE);
        Container.btn3.setVisibility(ImageView.INVISIBLE);
		Container.map.setVisibility(ImageView.INVISIBLE);
	}
    
    @Override
	public void onClick(View v) {
    	if(v==btnShare) {
    		imageOptions();
    	}
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
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            getParent().startActivityForResult(intent, GALLERY_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.fillInStackTrace();
        }
    }
      
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
       	    
    @Override
    public void onBackPressed() {
    	Container.share.setEnabled(true);
    }
}
