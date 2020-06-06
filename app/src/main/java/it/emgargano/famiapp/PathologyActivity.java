package it.emgargano.famiapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.Pathology;
import it.emgargano.famiapp.sms.prova.R;

public class PathologyActivity extends AppCompatActivity {
    //Variable declaration
    private static final String TAG = "PathologyActivity";
    private DatabaseReference mUserReference;
    private DatabaseReference mPathologyReference;
    private TextView mName;
    private TextView mLifestyle;
    private TextView mMedicines;
    private TextView mDescription;
    private TextView mNutritional;
    private String pathology_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the activity content from a layout resource.
        setContentView(R.layout.activity_pathology);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Defined personal data variables
        mName = findViewById(R.id.edit_name_pathology);
        mLifestyle = findViewById(R.id.edit_lifestyle_pathology);
        mMedicines = findViewById(R.id.edit_medicines_pathology);
        mNutritional = findViewById(R.id.edit_nutritional_pathology);
        mDescription = findViewById(R.id.edit_description_pathology);

        //Initialize variable to get extra value from other intent
        if (getIntent().getExtras() != null) {
            pathology_id = getIntent().getExtras().getString("pathology_clicked");
        }

        mPathologyReference = FirebaseDatabase.getInstance().getReference("pathology").child(pathology_id);
        mPathologyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                Pathology pathology = dataSnapshot.getValue(Pathology.class);
                mName.setText(pathology.name);
                mDescription.setText(pathology.description);
                mNutritional.setText(pathology.nutritional);
                mLifestyle.setText(pathology.lifestyle);
                mMedicines.setText(pathology.medicine);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Pathology failed, log a message
                Log.w(TAG, "loadPathology:onCancelled", databaseError.toException());
                Toast.makeText(PathologyActivity.this, "Failed to load pathology.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent refresh = new Intent(this, PathologyActivity.class);
        startActivity(refresh);
        this.finish();
    }

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
