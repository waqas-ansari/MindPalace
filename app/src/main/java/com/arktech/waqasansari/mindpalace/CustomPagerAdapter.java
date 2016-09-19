package com.arktech.waqasansari.mindpalace;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

/**
 * Created by WaqasAhmed on 4/30/2016.
 */
public class CustomPagerAdapter extends PagerAdapter {
    Context context;
    List<String> imageList;

    public CustomPagerAdapter(Context context, List<String> imageList){
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        View viewItem = inflater.inflate(R.layout.image_item, container, false);
        ImageView imageView = (ImageView) viewItem.findViewById(R.id.imageView);


        if(imageList == null)
            imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        else {
            String fileName = new File(imageList.get(position)).getAbsolutePath();
            Bitmap bitmap;

            bitmap = Utility.decodeSampledBitmap(fileName, 150, 150);

            imageView.setImageBitmap(bitmap);
        }
        container.addView(viewItem);

        return viewItem;
    }

    @Override
    public int getCount() {
        if(imageList == null) return 1;
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
