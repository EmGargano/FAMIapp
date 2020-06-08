package it.emgargano.famiapp;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.Acceptance;
import it.emgargano.famiapp.models.Necessities;
import it.emgargano.famiapp.models.User;
import famiapp.R;


public class AddFoodActivity extends AppCompatActivity {
    //Variable declaration
    private static final String TAG = "PopUpTempActivity";

    private Button submitNecessities;
    private EditText mMall;
    private EditText mPharmacy;
    private long idCity;
    String mUserID;
    String acceptanceId;
    String necessitiesID;
    String oldMall, oldPharmacy, newMall, newPharmacy;
    DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMall = findViewById(R.id.editTextMallLocation);
        mPharmacy = findViewById(R.id.editTextPharmacyLocation);
        submitNecessities = findViewById(R.id.button_submit);
        submitNecessities.setOnClickListener(submitNecessities_listener);

        mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(mUserID);
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                acceptanceId = dataSnapshot.getValue(User.class).getAcceptanceId();
                searchCityId(acceptanceId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void searchCityId (String acceptanceId) {
        DatabaseReference cityRef = FirebaseDatabase.getInstance().getReference("acceptance").child(acceptanceId);
        cityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idCity = dataSnapshot.getValue(Acceptance.class).city;
                searchNecessities(idCity);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void searchNecessities (final long idCity) {
        //        Retrieve last updated mall and pharmacy address for current user.
        DatabaseReference necessitiesRef = FirebaseDatabase.getInstance().getReference("basic_necessities");
        necessitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long idCitySnapshot;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Necessities necessities = ds.getValue(Necessities.class);
                    idCitySnapshot = necessities.getCity();
                    if ( idCitySnapshot == idCity) {
                        oldMall = necessities.getMall();
                        oldPharmacy = necessities.getPharmacy();
                        necessitiesID = ds.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

//    Check which addresses are to be updated
    public View.OnClickListener submitNecessities_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            newMall = mMall.getText().toString();
            newPharmacy = mPharmacy.getText().toString();
            if (newPharmacy.isEmpty() && newMall.isEmpty()) {
                Snackbar.make(view,
                        getString(R.string.emptyFieldsAdd),
                        Snackbar.LENGTH_LONG)
                        .show();
            } else if (newPharmacy.isEmpty()) {
                updateFindingGoods(newMall, oldPharmacy);
            } else if (newMall.isEmpty()) {
                updateFindingGoods(oldMall, newPharmacy);
            } else {
                updateFindingGoods(newMall, newPharmacy);
            }
        }
    };

    public void updateFindingGoods(String mall , String pharmacy) {
        FirebaseDatabase.getInstance().getReference("basic_necessities").
                child(necessitiesID)
                .child("mall")
                .setValue(mall).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //success message
                    Toast.makeText(AddFoodActivity.this, getString(R.string.addSuccessed), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //failure message
                    Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.addFailed), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        FirebaseDatabase.getInstance().getReference("basic_necessities").
                child(necessitiesID)
                .child("pharmacy")
                .setValue(pharmacy).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //success message
                    Toast.makeText(AddFoodActivity.this, getString(R.string.addSuccessed), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //failure message
                    Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.addFailed), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    //    Hide soft keyboard on screen touch.
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }
        return super.dispatchTouchEvent(ev);
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
