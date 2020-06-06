package it.emgargano.famiapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import it.emgargano.famiapp.sms.prova.R;

public class AdminActivity extends AppCompatActivity {

    CardView card_view_addFood;
    CardView card_view_addUser;
    CardView card_view_readRatings;
    private FirebaseAuth mAuth;
    CardView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Called when the activity is starting.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        card_view_addFood = findViewById(R.id.card_addFood);
        card_view_addUser = findViewById(R.id.card_addUser);
        card_view_readRatings = findViewById(R.id.card_readRatings);

        mAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.sign_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent signout = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(signout);
                finish();
            }
        });

        card_view_addUser.setOnClickListener(card_view_addUser_listener);
        card_view_addFood.setOnClickListener(card_view_addFood_listener);
        card_view_readRatings.setOnClickListener(card_view_readRatings_listener);
        //Set a click listener on the FloatingActionButton object
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent refresh = new Intent(this, AdminActivity.class);
        startActivity(refresh);
        this.finish();
    }

    public View.OnClickListener card_view_addUser_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent add_userIntent = new Intent(AdminActivity.this, AddUserActivity.class);
            startActivity(add_userIntent);
        }
    };

    public View.OnClickListener card_view_addFood_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent add_foodIntent = new Intent(AdminActivity.this, AddFoodActivity.class);
            startActivity(add_foodIntent);
        }
    };

    public View.OnClickListener card_view_readRatings_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent read_ratingsIntent = new Intent(AdminActivity.this, ReadRatingsActivity.class);
            startActivity(read_ratingsIntent);
        }
    };

}
