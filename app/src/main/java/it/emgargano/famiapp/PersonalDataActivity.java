package it.emgargano.famiapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;


public class PersonalDataActivity extends AppCompatActivity {
    private static final String TAG = "PersonalDataActivity";
    private Button mSavePersonalData;
    private DatabaseReference mUserReference;

    private EditText mCell;
    private TextView mName;
    private TextView mEmail;
    private TextView mGender;
    private TextView mNation;
    private TextView mSurname;
    private TextView mBirthPlace;
    private TextView mDateOfBirth;
    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        // Defined personal data variable
        mName = findViewById(R.id.editTextName);
        mSurname = findViewById(R.id.editTextSurname);
        mDateOfBirth = findViewById(R.id.editTextBirthday);
        mBirthPlace = findViewById(R.id.editTextBirthPlace);
        mEmail = findViewById(R.id.editTextEmail);
        mCell = findViewById(R.id.editTextCell);
        mGender = findViewById(R.id.editTextGender);
        mNation = findViewById(R.id.editTextNation);

        mSavePersonalData = findViewById(R.id.buttonSavePersonalData);
        mSavePersonalData.setOnClickListener(save_data_listener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(uId);
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                mName.setText(user.name);
                mSurname.setText(user.surname);
                mDateOfBirth.setText(user.date_of_birth);
                mBirthPlace.setText(user.birth_place);
                mCell.setText(user.cell);
                mGender.setText(user.gender);
                mEmail.setText(user.getMail());
                mNation.setText(user.getNation());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(PersonalDataActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public View.OnClickListener save_data_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String newCell = mCell.getText().toString();
            if (!newCell.equals("") && newCell.length() > 5) {
                mUserReference.child("cell").setValue(newCell).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PersonalDataActivity.this,
                                    getString(R.string.addSuccessed),
                                    Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Snackbar.make(getWindow().getDecorView().getRootView(),
                                    getString(R.string.addFailed),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            } else {
                Snackbar.make(view, getString(R.string.failedNumberDigits), Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    //    Hide soft keyboard and clear focus of current view on screen touch.
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            View v = getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
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
