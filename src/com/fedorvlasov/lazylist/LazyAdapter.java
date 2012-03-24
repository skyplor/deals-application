package com.fedorvlasov.lazylist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import socialtour.socialtour.R;
import socialtour.socialtour.models.Product;
import socialtour.socialtour.models.Shop;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private Product[] data;
    private Shop[] shop;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, Product[] d, Shop[] s) {
    	shop = s;
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
        
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.imageitem, null);
        TextView text=(TextView)vi.findViewById(R.id.text);;
        ImageView image=(ImageView)vi.findViewById(R.id.image2);
        TextView percent = (TextView)vi.findViewById(R.id.percentTesting);
        TextView desc = (TextView)vi.findViewById(R.id.txtDescription);
        TextView date = (TextView)vi.findViewById(R.id.txtDate);
        TextView productprice = (TextView)vi.findViewById(R.id.productprice);
        TextView noLikes = (TextView)vi.findViewById(R.id.lblmainlikes);
        TextView noRemarks = (TextView)vi.findViewById(R.id.lblmaincomments);
        //double price = data[position].getDprice() * 1000;
        //long tempprice = Math.round(price);
        //double finalprice = tempprice / 1000;
        Date prodDate = data[position].getCreated();
        DateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
        String finalDate = df2.format(prodDate);
        date.setText(finalDate);
        productprice.setText(data[position].getDprice());
        String productname = data[position].getFilename();//.substring(0,data[position].getFilename().lastIndexOf("."));
        text.setText(productname);
        noLikes.setText(Integer.toString(data[position].getLikes()));
        noRemarks.setText(Integer.toString(data[position].getRemarks()));
        TextView distance = (TextView)vi.findViewById(R.id.txtHownear);
        if (shop[position].getDist() !=null){
        	Double currDistance = Double.valueOf(shop[position].getDist());
            long intDistance = Math.round(currDistance);
            if (intDistance > 1000){
            	long kmDistance = intDistance / 1000;
            	distance.setText("(" + Long.toString(kmDistance) + " km away)");
            }else{
            	distance.setText("(" + Long.toString(intDistance) + " m away)");
            }
            
        }
        imageLoader.DisplayImage(Integer.toString(data[position].getId()), activity, image);
        percent.setText(data[position].getPercentdiscount());
        desc.setText(shop[position].getName());
        return vi;
    }
}