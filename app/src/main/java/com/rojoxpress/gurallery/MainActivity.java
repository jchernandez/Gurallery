package com.rojoxpress.gurallery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rojoxpress.gurallery.components.ObservableGridView;
import com.rojoxpress.gurallery.components.ObservableListView;
import com.rojoxpress.gurallery.components.ObservableScrollView;
import com.rojoxpress.gurallery.components.ObservableScrollViewCallbacks;
import com.rojoxpress.gurallery.utils.GurHeaders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, ObservableScrollViewCallbacks {

    private RequestQueue queue;
    private ProgressBar progressBar;
    private Context context;
    private ArrayList<GalleryItem> galleryList;
    private ImageAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private Spinner spinner;
    private boolean firstRequest = true;
    private Toolbar toolbar;
    private int listViewScroll = 0;
    private boolean showViral = false;
    private ObservableGridView gridView;
    private int headerViewCount = 1;
    String[] sort ={"viral","time","top"};
    String[] window ={"day","week","month","year","all"};
    int sortItem = 0;
    int windowItem = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        queue = Volley.newRequestQueue(context);


        gridView = (ObservableGridView) findViewById(R.id.grid_image);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        spinner = (Spinner) findViewById(R.id.spinner_type);
        toolbar = (Toolbar) findViewById(R.id.mToolbar);

        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        String[] section = new String[]{"hot","top","user"};
        spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, section));

        spinner.setOnItemSelectedListener(this);

        refreshLayout.setOnRefreshListener(this);
        gridView.setOnItemClickListener(this);
        View header  = new View(context);
        int toolBarSize = getResources().getDimensionPixelSize(android.support.v7.appcompat.R.dimen.abc_action_bar_default_height_material);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,toolBarSize);
        header.setLayoutParams(params);
        gridView.addHeaderView(header);
        refreshLayout.setProgressViewOffset(false, toolBarSize, toolBarSize * 2);
        gridView.setScrollViewCallbacks(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.show_viral: {
                showViral = !showViral;
                refreshLayout.setRefreshing(true);
                requestGallery();
            }
            break;
            case R.id.change_view: {
                if (gridView.getNumColumns() == 1)
                    gridView.setNumColumns(2);
                else
                    gridView.setNumColumns(1);
            }
            break;
            case R.id.sort:{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.sort);
                builder.setSingleChoiceItems(sort, sortItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sortItem = which;
                        dialog.dismiss();
                        requestGallery();
                    }
                });
                builder.show();
            }break;
            case R.id.window:{
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.window);
                builder.setSingleChoiceItems(window, windowItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        windowItem = which;
                        dialog.dismiss();
                        requestGallery();
                    }
                });
                builder.show();
            }break;
            default: {
                startActivity(new Intent(context, AboutActivity.class));
            }
        }

        return true;
    }


    public void requestGallery(){

        if(!firstRequest){
            refreshLayout.setRefreshing(true);
        }

        firstRequest = false;
        String url= "https://api.imgur.com/3/gallery/"
                +spinner.getAdapter().getItem(spinner.getSelectedItemPosition())
                +"/"+sort[sortItem]+"/"+window[windowItem]+"/"
                +"?showViral="+showViral;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("response", response.toString());
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                try {
                    JSONArray data = response.getJSONArray("data");
                    galleryList = new ArrayList<>();
                    for(int i=0;i<data.length();i++){
                        String link = data.getJSONObject(i).getString("link");
                        boolean isImage = link.contains(".jpg") ||
                                link.contains(".png");

                        if(isImage) {
                            GalleryItem galleryItem = new GalleryItem();
                            galleryItem.setImageURL(link);
                            galleryItem.setDescription(data.getJSONObject(i).getString("description"));
                            galleryItem.setTitle(data.getJSONObject(i).getString("title"));
                            galleryItem.setDowns(data.getJSONObject(i).getString("downs"));
                            galleryItem.setUps(data.getJSONObject(i).getString("ups"));
                            galleryItem.setScore(data.getJSONObject(i).getString("score"));
                            galleryList.add(galleryItem);
                        }
                    }
                    mApplication application = (mApplication) getApplication();
                    adapter = new ImageAdapter(context,galleryList,application.imageLoader);
                    gridView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                refreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                String message = error.getMessage();
                if(error.networkResponse!=null){
                    message = message+" code:"+error.networkResponse.statusCode;
                }
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return GurHeaders.getHeader();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));

        queue.add(request);
    }

    @Override
    public void onRefresh() {
        requestGallery();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(view, position);
    }
    public void selectItem(View view,int position){
        final int firstPosition = gridView.getFirstVisiblePosition() - headerViewCount;
        Intent intent = new Intent(context, DetailGalleryActivity.class);
        intent.putExtra("galleryList", galleryList);
        intent.putExtra("current", position-gridView.getNumColumns());



        Bundle b = null;
        int wantedChild = position - firstPosition;
        if (wantedChild < 0 || wantedChild >= gridView.getChildCount()) {
        }
        if (view != null) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            b = ActivityOptionsCompat.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
        }
        ActivityCompat.startActivity(this, intent, b);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        requestGallery();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float scrollDiff = scrollY - listViewScroll;
        float y = Math.min(0, Math.max(-toolbar.getHeight(), toolbar.getY() - scrollDiff));

        float abs = Math.abs(y);

        float toolBarHeight = getResources().getDimension(android.support.v7.appcompat.R.dimen.abc_action_bar_default_height_material);

        float perc = (abs * 100)/toolBarHeight;


        toolbar.setTranslationY(y);
        toolbar.setTranslationY(y);
        listViewScroll = scrollY;
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ObservableScrollView.ScrollState scrollState) {

    }
}
