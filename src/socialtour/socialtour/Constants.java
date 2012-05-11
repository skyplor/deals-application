package socialtour.socialtour;

import java.io.File;
import java.net.URI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Constants {
	public static final String FACEBOOK_APPID = "Input your Facebook APP ID here";
	public static final String[] FACEBOOK_PERMISSION = { "user_birthday", "email", "publish_stream", "read_stream", "offline_access" };
//	public static final String UPLOAD_PATH = "C:/wamp/www/FYP/FYP/uploads/";
	public static final String UPLOAD_PATH = "/home/mingenlee/Dealsout/FYP/FYP/uploads/";
//	public static final String CONNECTIONSTRING = "http://192.168.1.80/FYP/";
//	public static final String CONNECTIONSTRING = "http://10.0.2.2/FYP/";
	//public static final String CONNECTIONSTRING = "http://172.22.177.204/FYP/";
//	public static final String CONNECTIONSTRING = "http://192.168.0.196/FYP/";
	public static final String CONNECTIONSTRING = "http://msm.cais.ntu.edu.sg/Dealsout/FYP/";
//	public static final String DOWNLOAD_PATH = "http://192.168.0.196/";
	public static final String DOWNLOAD_PATH = "http://msm.cais.ntu.edu.sg/dealsinterest/";
	public static final String SHARING_PATH = "http://www.dealsinterest.com/dealsinterest/";
	private static final int THUMBNAIL_SIZE = 4;
	
	public Bitmap getPreview(URI uri) {
	    File image = new File(uri);

	    BitmapFactory.Options bounds = new BitmapFactory.Options();
	    bounds.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(image.getPath(), bounds);
	    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
	        return null;

	    int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
	            : bounds.outWidth;
	    
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
	    return BitmapFactory.decodeFile(image.getPath(), opts);     
	}
}
