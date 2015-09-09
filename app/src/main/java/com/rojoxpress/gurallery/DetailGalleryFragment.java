package com.rojoxpress.gurallery;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class DetailGalleryFragment extends Fragment implements View.OnClickListener {

    LinearLayout info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery_detail,null);

        final ImageView imageView = (ImageView) view.findViewById(R.id.image);
        mApplication application = (mApplication) getActivity().getApplication();

        GalleryItem item = (GalleryItem) getArguments().getSerializable("item");
        TextView description,ups,downs,score;
        description = (TextView) view.findViewById(R.id.description);
        ups = (TextView) view.findViewById(R.id.up_votes);
        downs = (TextView) view.findViewById(R.id.down_votes);
        info = (LinearLayout) view.findViewById(R.id.info_container);
        score = (TextView) view.findViewById(R.id.score);


        if(item!=null){
            score.setText(item.getScore());
            ups.setText(item.getUps());
            downs.setText(item.getDowns());
            if(item.getDescription()!=null && !item.getDescription().equals("null"))
                description.setText(item.getDescription());

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                application.imageLoader.get(item.getImageURL(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            imageView.setImageBitmap(response.getBitmap());
                            imageView.setOnClickListener(DetailGalleryFragment.this);
                        } else {
                            Log.i("image null", response.getRequestUrl());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },width,height);
        }else {
            getActivity().finish();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Toolbar toolbar = ((DetailGalleryActivity)getActivity()).getToolbar();
        if(toolbar.getVisibility()==View.VISIBLE){
            toolbar.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
        }else {
            toolbar.setVisibility(View.VISIBLE);
            info.setVisibility(View.VISIBLE);
        }
    }


}
