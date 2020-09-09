package com.craft.notify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      // setContentView(R.layout.activity_splash_screen);
//        Animation textViewAnimation =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
//        textViewAnimation.setDuration(700);
//        TextView textViewTitle = findViewById(R.id.textViewTitle);
//        textViewTitle.setAnimation(textViewAnimation);
//        textViewAnimation.start();
//        ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
//        Animation animationImageView = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
//        animationImageView.setDuration(700);
//        imageViewLogo.setAnimation(animationImageView);
//        animationImageView.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                startActivity(new Intent(SplashScreen.this,MainActivity.class));
//                finish();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        animationImageView.start();

        startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();

    }
}
