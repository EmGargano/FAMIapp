package it.emgargano.famiapp;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;


public class SplashScreeen extends AppCompatActivity {

    private static int SPLASH = 2500; // Splash screen timer
    private Animation cerchioRotation;
    private Animation androidZoomInAnimation;
    private Animation androidZoomOutAnimation;
    private Animation fadingAppname;
    private ImageView logo;
    private ImageView cerchio;
    private TextView appname;

    private DatabaseReference mUserReference;
    private int mRole;
    private String uId;

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        logo = findViewById(R.id.splash_logo);
        cerchio = findViewById(R.id.cerchio);
        cerchio.setVisibility(View.INVISIBLE);
        appname = findViewById(R.id.splash_appname);
        appname.setVisibility(View.INVISIBLE);

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
                cerchio.startAnimation(cerchioRotation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoadCurrentUserRole();
            }
        }, SPLASH);
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

    public void onLoadCurrentUserRole() {
        //Get Current User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Get userId
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            uId = user.getUid();
            mUserReference = FirebaseDatabase.getInstance().getReference("user").child(uId);

            mUserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get User object
                    User user = dataSnapshot.getValue(User.class);
                    mRole = user.getRole();
//                    Log.d("TAG:splashscreen", "utente numero: " + mRole);
                    getRoleActivity(mRole);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting User Role failed, log a message
                    Toast.makeText(SplashScreeen.this, "Failed to load user.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getRoleActivity(int role_id) {
        if (role_id == 1) {
            //Centro di accoglienza Role
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
            finish();
        } else if (role_id == 2) {
            //Richidenente asilo Role
            Intent intent = new Intent(this, MedicalRecordsActivity.class);
            startActivity(intent);
            finish();
        } else if (role_id == 3) {
            //Dottore Role
            Intent intent = new Intent(this, PatientListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        logo.startAnimation(androidZoomInAnimation);
    }
}
