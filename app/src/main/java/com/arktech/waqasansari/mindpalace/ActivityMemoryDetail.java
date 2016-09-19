package com.arktech.waqasansari.mindpalace;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityMemoryDetail extends AppCompatActivity {
    ViewPager viewPager;
    View[] views;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;

    Bitmap currentBitmap;

    int colorPrimary;

    List<String> imagesList;
    ClassMemoryDetail detailMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_detail);

        colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBar);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                        isShow = true;
                    } else if (isShow) {
                        toolbar.setBackgroundColor(Color.TRANSPARENT);
                        isShow = false;
                    }
                }
            });
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imagesList = new ArrayList<>();


        detailMemory = (ClassMemoryDetail) getIntent().getSerializableExtra("memoryObject");

        collapsingToolbarLayout.setTitle(detailMemory.getTitle());
        ((TextView) findViewById(R.id.txtDescription)).setText(detailMemory.getDescription());
        ((TextView) findViewById(R.id.txtTags)).setText(detailMemory.getTags());
        ((TextView) findViewById(R.id.txtDate)).setText(Utility.getFormattedDate(detailMemory.getDated()));

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.imageIndicatorLayout);


        String tempString = detailMemory.getImage();
        if(tempString == null) {
            imagesList = null;
            appBarLayout.setExpanded(false);
        }
        else {
            imagesList.addAll(Arrays.asList(tempString.trim().split(",")).subList(0, tempString.trim().split(",").length));
            currentBitmap = BitmapFactory.decodeFile(imagesList.get(0));
            views = new View[imagesList.size()];
            for(int i=0; i < imagesList.size(); i++){
                views[i] = addIndicatorView(i);
                linearLayout.addView(views[i]);
            }
        }


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter adapter = new CustomPagerAdapter(ActivityMemoryDetail.this, imagesList);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < imagesList.size(); i++) {
                    if (i == position)
                        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                            views[i].setBackgroundDrawable(ContextCompat.getDrawable(ActivityMemoryDetail.this, R.drawable.filled_circle));
                        else views[i].setBackground(ContextCompat.getDrawable(ActivityMemoryDetail.this, R.drawable.filled_circle));
                    else {
                        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                            views[i].setBackgroundDrawable(ContextCompat.getDrawable(ActivityMemoryDetail.this, R.drawable.holo_circle));
                        else views[i].setBackground(ContextCompat.getDrawable(ActivityMemoryDetail.this, R.drawable.holo_circle));
                    }

                    if(imagesList != null){
                        new AsyncGetPaletteColors().execute(position);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private View addIndicatorView(int id){

        int dpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int dpWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());


        View view = new View(getApplicationContext());
        view.setId(id);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpWidth, dpHeight);
        layoutParams.setMargins(0, 0, 10, 0);
        if(id==0){
            if(imagesList != null) {
                new AsyncGetPaletteColors().execute(0);
            }
            if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                view.setBackgroundDrawable(ContextCompat.getDrawable(ActivityMemoryDetail.this, R.drawable.filled_circle));
            else view.setBackground(ContextCompat.getDrawable(ActivityMemoryDetail.this, R.drawable.filled_circle));
        }
        else
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            view.setBackgroundDrawable(ContextCompat.getDrawable(ActivityMemoryDetail.this, R.drawable.holo_circle));
        else view.setBackground(ContextCompat.getDrawable(ActivityMemoryDetail.this, R.drawable.holo_circle));
        view.setLayoutParams(layoutParams);
        view.setClickable(true);
        return view;
    }

    private class AsyncGetPaletteColors extends AsyncTask<Integer, Palette, Palette> {

        @Override
        protected Palette doInBackground(Integer... params) {
            Palette palette = null;
            currentBitmap = Utility.decodeSampledBitmap(imagesList.get(params[0]), 350, 300);
            if (currentBitmap != null && !currentBitmap.isRecycled())
                palette = Palette.from(currentBitmap).generate();
            return palette;
        }

        @Override
        protected void onPostExecute(Palette palette) {
            super.onPostExecute(palette);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                List<Palette.Swatch> vibrant = null;

                Palette.Swatch populatedSwatch = null;

                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                if (palette != null)
                    vibrant = palette.getSwatches();

                int population = 0;
                if (vibrant != null) {
                    for(Palette.Swatch swatch : vibrant){
                        if(population < swatch.getPopulation()) {
                            population = swatch.getPopulation();
                            populatedSwatch = swatch;
                        }
                    }

                    if (populatedSwatch != null) {
                        collapsingToolbarLayout.setContentScrimColor(populatedSwatch.getRgb());
                    }

                    findViewById(R.id.fabEdit).setBackgroundTintList(ColorStateList.valueOf(populatedSwatch.getRgb()));

                    int color = populatedSwatch.getRgb();
                    color = darkerColor(color, 0.8f);
                    window.setStatusBarColor(color);
                }
            }
        }
    }


    public static int darkerColor(int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void editMemory(View view){
        Utility.isMemoryEdited = false;
        Intent intent = new Intent(ActivityMemoryDetail.this, ActivityAddMemory.class);
        intent.putExtra("memoryObject", detailMemory);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(Utility.isMemoryEdited)
            finish();
    }
}
