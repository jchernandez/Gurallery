package com.rojoxpress.gurallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class DetailGalleryActivity extends AppCompatActivity {


    ViewPager pager;
    ArrayList<GalleryItem> galleryList;
    private TextView countItems;
    TextView textView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_gallery);

        toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        countItems = (TextView) findViewById(R.id.count_items);
        pager = (ViewPager) findViewById(R.id.pager);
        textView = (TextView) findViewById(R.id.title);



        int currentItem = getIntent().getIntExtra("current",0);
        galleryList = (ArrayList<GalleryItem>) getIntent().getSerializableExtra("galleryList");

        textView.setText(galleryList.get(currentItem).getTitle());
        FragmentPagerAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentItem);

        countItems.setText(currentItem+"/"+galleryList.size());

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textView.setText(galleryList.get(position).getTitle());
                countItems.setText(position + "/" + galleryList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FragmentAdapter extends FragmentPagerAdapter{

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment detailFragment = new DetailGalleryFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", galleryList.get(position));
            detailFragment.setArguments(bundle);
            return detailFragment;
        }

        @Override
        public int getCount() {
            return galleryList.size();
        }
    }


    public Toolbar getToolbar(){
        return toolbar;
    }
}
