package it.emgargano.famiapp;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import it.emgargano.famiapp.sms.prova.R;

public class AppDetailsActivity extends AppCompatActivity {

    private Animation cerchioRotation;
    private Animation androidZoomInAnimation;
    private Animation androidZoomOutAnimation;
    private Animation fadingAppname;
    private ImageView logo;
    private ImageView cerchio;
    private TextView appname, appDetails, team1, team2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Called when the activity is starting.
        super.onCreate(savedInstanceState);
        //Set the activity content from a layout resource.
        setContentView(R.layout.activity_app_details);

        logo = findViewById(R.id.splash_logo);
        cerchio = findViewById(R.id.cerchio);
        cerchio.setVisibility(View.INVISIBLE);
        appname = findViewById(R.id.splash_appname);
        appname.setVisibility(View.INVISIBLE);
        appDetails = findViewById(R.id.app_details);
        appDetails.setVisibility(View.INVISIBLE);
        team1 = findViewById(R.id.team1);
        team1.setVisibility(View.INVISIBLE);
        team2 = findViewById(R.id.team2);
        team2.setVisibility(View.INVISIBLE);

        cerchioRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation_cerchio_anim);
        androidZoomInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_anim);
        androidZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_anim);
        fadingAppname = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_anim_activity);
        fadingAppname.setStartOffset(500L);

        androidZoomInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logo.setAnimation(androidZoomOutAnimation);
                appname.setVisibility(View.VISIBLE);
                appname.startAnimation(fadingAppname);
                appDetails.setVisibility(View.VISIBLE);
                appDetails.startAnimation(fadingAppname);
                team1.setVisibility(View.VISIBLE);
                team1.startAnimation(fadingAppname);
                team2.setVisibility(View.VISIBLE);
                team2.startAnimation(fadingAppname);
                cerchio.startAnimation(cerchioRotation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        logo.startAnimation(androidZoomInAnimation);
    }

    //    Navigation bar hided and fullscreen mode on
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    public void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
