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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.emgargano.famiapp.adapter.MedicalRecordAdapter;
import it.emgargano.famiapp.models.MedicalRecord;
import it.emgargano.famiapp.sms.prova.R;

public class ReadMedicalRecordsActivity extends AppCompatActivity {
    private static final String TAG = "ReadMedRecordsActivity";
    private DatabaseReference mUserReference;
    private String uId;
    private String userClickedId;
    private String parameter;
    ActionBar actionBar;

    private RecyclerView recyclerView;
    private DatabaseReference mMedicalRecordReference;
    private DatabaseReference parameterReference;

    private MedicalRecordAdapter medicalRecordAdapter;
    private List<MedicalRecord> mMedicalRecordsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_medical_records);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.medicalRecordsList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (getIntent().getExtras() != null) {
            userClickedId = getIntent().getExtras().getString("user_clicked");
            parameter = getIntent().getExtras().getString("_parameter");
        }

//        Check if medical records are for the patient or for the user logged.
        if (userClickedId != null) {
            readMedicalRecords(userClickedId, parameter);
        } else {
//        Get userId Logged
            uId = user.getUid();
            readMedicalRecords(uId, parameter);
        }

        setActionBarTitle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bundle extras = getIntent().getExtras();
        String userClickedId = extras.getString("user_clicked");
        String parameter = extras.getString("_parameter");
        Intent refresh = new Intent(this, ReadMedicalRecordsActivity.class);
        refresh.putExtra("user_clicked", userClickedId);
        refresh.putExtra("_parameter", parameter);
        startActivity(refresh);
        this.finish();

    }

    public void readMedicalRecords(final String userId, final String parameter) {
        Log.d(TAG, "dentro readMedicalRecords");
        //ArrayList variable
        mMedicalRecordsList = new ArrayList<>();
        //Initialize Database Reference
        mMedicalRecordReference = FirebaseDatabase.getInstance().getReference("medical_records");
        //Used to synchronize data
        mMedicalRecordReference.keepSynced(true);
        //Add value
        mMedicalRecordReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dentro onDataChange");
                // Clear ArrayList
                mMedicalRecordsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get obj medicalRecord
                    MedicalRecord medicalRecord = snapshot.getValue(MedicalRecord.class);
                    if(medicalRecord.get_user() != null) {
                        if (medicalRecord.get_user().equals(userId) && medicalRecord.get_parameter().equals(parameter)) {
                            // Add obj to ArrayList
                            mMedicalRecordsList.add(medicalRecord);
                        }
                    }
                }
                if (mMedicalRecordsList.isEmpty()) {
                    Log.d(TAG, "dentro lista vuota");
                    Snackbar.make(findViewById(R.id.readmedicalrecords),
                            getString(R.string.noMedicalRecords),
                            Snackbar.LENGTH_INDEFINITE).show();
                } else {
                    medicalRecordAdapter = new MedicalRecordAdapter(ReadMedicalRecordsActivity.this, mMedicalRecordsList);
                    recyclerView.setAdapter(medicalRecordAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Medical Record failed
                Log.w(TAG, "loadMedicalRecord:onCancelled", databaseError.toException());
                Toast.makeText(ReadMedicalRecordsActivity.this, "Failed to load medical record.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setActionBarTitle() {
        switch (parameter) {
            case "1":
                actionBar.setTitle(R.string.temperature);
                break;

            case "2":
                actionBar.setTitle(R.string.bloodPressure);
                break;

            case "3":
                actionBar.setTitle(R.string.glycemia);
                break;

            case "4":
                actionBar.setTitle(R.string.heartbeat);
                break;

            case "6":
                actionBar.setTitle(R.string.symptoms);
                break;

            case "7":
                actionBar.setTitle(R.string.pathology);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
