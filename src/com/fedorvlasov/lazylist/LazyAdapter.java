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
        ImageView typelogo=(ImageView)vi.findViewById(R.id.typeLogo);
        TextView percent = (TextView)vi.findViewById(R.id.percentTesting);
        TextView desc = (TextView)vi.findViewById(R.id.txtDescription);
        String productname = data[position].getFilename().substring(0,data[position].getFilename().lastIndexOf("."));
        
        text.setText(productname);
        imageLoader.DisplayImage(data[position].getUrl(), activity, image);
        typelogo.setImageResource(shop[position].getIcon());
        percent.setText(data[position].getPercentdiscount() + "%");
        desc.setText(shop[position].getName());
        return vi;
    }
}