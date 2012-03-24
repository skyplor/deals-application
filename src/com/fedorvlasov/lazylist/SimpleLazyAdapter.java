package com.fedorvlasov.lazylist;

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

public class SimpleLazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private Product[] data;
    private Shop[] shop, tempShop;
    private static LayoutInflater inflater=null;
    public SimpleImageLoader imageLoader;
    private boolean isProduct;
    
    public SimpleLazyAdapter(Activity a, Product[] d, Shop[] s) {
        activity = a;
        tempShop = s;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new SimpleImageLoader(activity.getApplicationContext());
        isProduct = true;
    }
    
    public SimpleLazyAdapter(Activity a, Shop[] s) {
    	shop = s;
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new SimpleImageLoader(activity.getApplicationContext());
        isProduct = false;
    }
    
    public SimpleLazyAdapter(Activity a, Product[] d){
    	activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new SimpleImageLoader(activity.getApplicationContext());
        isProduct = true;
    }

    public int getCount() {
    	if (isProduct){
    		return data.length;
    	}else{
    		return shop.length;
    	}
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
            vi = inflater.inflate(R.layout.item, null);

        TextView text=(TextView)vi.findViewById(R.id.text2);;
        ImageView image=(ImageView)vi.findViewById(R.id.image);
        TextView desc = (TextView)vi.findViewById(R.id.txtDescription2);
        TextView percent = (TextView)vi.findViewById(R.id.percent);
        
        if (isProduct){
        	String productname = data[position].getFilename();//.substring(0,data[position].getFilename().lastIndexOf("."));
        	text.setText(productname);
        	if (tempShop !=null){
        		desc.setText(tempShop[position].getName());
        	}else{
        		desc.setText("");
        	}
        	imageLoader.DisplayImage(Integer.toString(data[position].getId()), activity, image);
        	percent.setText(data[position].getPercentdiscount() + " Off");
        }else{
        	//int drawable = data[position].getIcon();
        	//image.setImageResource(drawable);
        	image.setVisibility(View.GONE);
        	percent.setVisibility(View.GONE);
        	text.setText(shop[position].getName());
        	desc.setText(shop[position].getAddress());
        	text.getLayoutParams().width = 300;
        	desc.getLayoutParams().width = 300;
        }
        
        
        
        
        
        return vi;
    }
}