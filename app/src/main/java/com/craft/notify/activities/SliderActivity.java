package com.craft.notify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.craft.notify.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class SliderActivity extends AppCompatActivity {
    ViewPager viewPagerSlider;
    private int[] layouts;
    ExtendedFloatingActionButton fabOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
    //    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        fabOver = findViewById(R.id.floatingActionButtonEnd);
        viewPagerSlider = findViewById(R.id.ViewPagerSlider);
        layouts = new int[]{R.layout.how_to_3};
        PagerAdapter pagerAdapter = new PagerAdapter();
        viewPagerSlider.setAdapter(pagerAdapter);
        fabOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPagerSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
               if(position==0){
                   fabOver.setVisibility(View.VISIBLE);

               }
               else {
                   fabOver.setVisibility(View.GONE);
               }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    class PagerAdapter extends androidx.viewpager.widget.PagerAdapter{
        public PagerAdapter() {
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(layouts[position],container,false);
            container.addView(view);
            return  view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View)object;
            container.removeView(view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

    }

    @Override
    protected void onDestroy() {

            super.onDestroy();

    }
}
