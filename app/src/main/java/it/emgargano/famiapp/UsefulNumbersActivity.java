package it.emgargano.famiapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import famiapp.R;

public class UsefulNumbersActivity extends AppCompatActivity {

    Button btnViewPolice, btnViewAmbulance, btnFireBrigade, btnFinanceGuard, btnSeaEmergency, btnMilitaryPolice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the activity content from a layout resource.
        setContentView(R.layout.activity_usefulnumbers);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnViewAmbulance = findViewById(R.id.btnAmbulance);
        btnViewPolice = findViewById(R.id.btnPolice);
        btnMilitaryPolice = findViewById(R.id.btnMilitaryPolice);
        btnFinanceGuard = findViewById(R.id.btnFinanceGuard);
        btnSeaEmergency = findViewById(R.id.btnSeaEmergency);
        btnFireBrigade = findViewById(R.id.btnFireBrigade);

        //Set a click listener on the imageview objects
        btnViewAmbulance.setOnClickListener(imageViewAmbulance_listener);
        btnViewPolice.setOnClickListener(imageViewPolice_listener);
        btnMilitaryPolice.setOnClickListener(imageViewMilitaryPolice_listener);
        btnFinanceGuard.setOnClickListener(imageViewFinanceGuard_listener);
        btnSeaEmergency.setOnClickListener(imageViewSeaEmergency_listener);
        btnFireBrigade.setOnClickListener(imageViewFireBrigade_listener);

    }

    public View.OnClickListener imageViewAmbulance_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:118"));
            startActivity(intent);
        }
    };
    public View.OnClickListener imageViewPolice_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:113"));
            startActivity(intent);
        }
    };
    public View.OnClickListener imageViewMilitaryPolice_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:112"));
            startActivity(intent);
        }
    };
    public View.OnClickListener imageViewFinanceGuard_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:117"));
            startActivity(intent);
        }
    };
    public View.OnClickListener imageViewSeaEmergency_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1530"));
            startActivity(intent);
        }
    };
    public View.OnClickListener imageViewFireBrigade_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:115"));
            startActivity(intent);
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
