package com.spawn.ai.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.spawn.ai.R;
import com.spawn.ai.SpawnBotActivity;
import com.spawn.ai.utils.JsonFileReader;

public class SpawnSplashScreen extends AppCompatActivity {

    public LottieAnimationView spawnLogo;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spawn_splash_screen);
        context = this;
        //JsonFileReader.getInstance().readFile(this);
        spawnLogo = (LottieAnimationView) findViewById(R.id.spawn_logo);
        spawnLogo.setRepeatMode(-1);
        spawnLogo.playAnimation();
        spawnLogo.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                spawnLogo.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, SpawnBotActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
