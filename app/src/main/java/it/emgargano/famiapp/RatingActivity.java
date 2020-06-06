package it.emgargano.famiapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import it.emgargano.famiapp.models.Rating;
import it.emgargano.famiapp.sms.prova.R;

public class RatingActivity extends AppCompatActivity {
    //variable declaration
    private static final String TAG = "RatingActivity";
    private Button mSubmitRating;
    private DrawerLayout drawer;
    private EditText mComment;
    private Float avgRating;
    private RatingBar mRatingApp;
    private RatingBar mRatingCity;
    private RatingBar mRatingCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Called when the activity is starting.
        super.onCreate(savedInstanceState);
        //Set the activity content from a layout resource.
        setContentView(R.layout.activity_rating);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mComment = findViewById(R.id.editTextComment);
        mRatingApp = findViewById(R.id.ratingBarApp);
        mRatingCity = findViewById(R.id.ratingBarCity);
        mRatingCenter = findViewById(R.id.ratingBarCenter);
        mSubmitRating = findViewById(R.id.buttonSubmit);

        //Set a click listener on the button objects
        mSubmitRating.setOnClickListener(mSubmitRating_listener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //receive result from activity

        Intent refresh = new Intent(this, RatingActivity.class);
        startActivity(refresh);
        this.finish();
    }

    //Set on click listener
    public View.OnClickListener mSubmitRating_listener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            avgRating = (mRatingCity.getRating() + mRatingCenter.getRating() + mRatingApp.getRating()) / 3;
            String comment = mComment.getText().toString();
            //New Constructor
            Rating rating = new Rating(
                    userId,
                    avgRating,
                    comment
            );

            //Adding value to DB
            FirebaseDatabase.getInstance().getReference("rating").push().setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //success message
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.ratingAddedComplete),
                            Toast.LENGTH_SHORT).show();
                    finish();
                    } else {
                        //failure message
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.ratingAddedFailed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
