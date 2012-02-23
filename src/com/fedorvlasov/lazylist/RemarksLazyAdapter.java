package com.fedorvlasov.lazylist;

import socialtour.socialtour.R;
import socialtour.socialtour.models.Product;
import socialtour.socialtour.models.Remark;
import socialtour.socialtour.models.Shop;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RemarksLazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private Product[] data;
    private Shop[] shop, tempShop;
    private Remark[] remarks;
    private static LayoutInflater inflater=null;
    public SimpleImageLoader imageLoader;
    private boolean isProduct;
    
    public RemarksLazyAdapter(Activity a, Remark[] r) {
        activity = a;
        remarks = r;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new SimpleImageLoader(activity.getApplicationContext());
        isProduct = true;
    }
    
    public int getCount() {
    	return remarks.length;
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
            vi = inflater.inflate(R.layout.remarksitem, null);

        TextView nameremarks = (TextView)vi.findViewById(R.id.txtnameremarks);
        TextView remarkscomments = (TextView)vi.findViewById(R.id.txtremarks);
        nameremarks.setText(remarks[position].getUsername());
        remarkscomments.setText(remarks[position].getDesc());
        return vi;
    }
}