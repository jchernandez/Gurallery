package com.rojoxpress.gurallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<GalleryItem> galleryList;
    ImageLoader imageLoader;

    public ImageAdapter(Context context, ArrayList<GalleryItem> galleryList, ImageLoader imageLoader) {
        super(context, R.layout.item_image_list);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.galleryList = galleryList;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return  galleryList.size();
    }

    public static class ImageHolder {
        NetworkImageView imageView;
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ImageHolder placeHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_image_list, null);
            placeHolder = new ImageHolder();
            placeHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.item_image);
            placeHolder.textView = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(placeHolder);
        } else {
            placeHolder = (ImageHolder) convertView.getTag();
        }

        placeHolder.imageView.setDefaultImageResId(R.drawable.image_placeholder);
        placeHolder.imageView.setImageUrl(galleryList.get(position).getImageURL(), imageLoader);
        if(galleryList.get(position).getDescription()!=null && !galleryList.get(position).getDescription().equals("null"))
            placeHolder.textView.setText(galleryList.get(position).getDescription());
        return convertView;
    }
}
