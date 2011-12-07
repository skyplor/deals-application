package socialtour.socialtour;

import java.io.File;
import java.net.URI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Constants {
	public static final String FACEBOOK_APPID = "197606583640677";
	public static final String FACEBOOK_PERMISSION = "publish_stream";
	public static final String UPLOAD_PATH = "C:/wamp/www/FYP/FYP/uploads/";
//	public static final String CONNECTIONSTRING = "http://192.168.1.80/FYP/";
	public static final String CONNECTIONSTRING = "http://10.0.2.2/FYP/";
	//public static final String CONNECTIONSTRING = "http://172.22.177.204/FYP/";
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
