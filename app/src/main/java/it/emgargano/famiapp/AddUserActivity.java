package it.emgargano.famiapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;

public class AddUserActivity extends AppCompatActivity {
    //Variable declaration
    private static final String TAG = "AddUserActivity";

    private Button submitButton;
    private DatabaseReference mUserReference;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextInputLayout editTextCell;
    private TextInputLayout editTextMail;
    private TextInputLayout editTextName;
    private TextInputLayout editTextNation;
    private TextInputLayout editTextSurname;
    private TextInputLayout editTextBirthday;
    private TextInputLayout editTextPassword;
    private TextInputLayout editTextBirthPlace;
    private FirebaseAuth mAuth;
    private String idAcceptance;
    private RadioButton maschio, femmina,richiedenteAsilo;
    private String role[] = {"Admin", "User", "Doctor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_user);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextName = findViewById(R.id.editTextName);
        editTextCell = findViewById(R.id.editTextCell);
        editTextMail = findViewById(R.id.editTextEmail);
        editTextNation = findViewById(R.id.editTextNation);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        editTextBirthPlace = findViewById(R.id.editTextBirthPlace);
        richiedenteAsilo = findViewById(R.id.roleRichiedente);
        maschio = findViewById(R.id.display_genderM);
        femmina = findViewById(R.id.display_genderF);
        submitButton = findViewById(R.id.buttonSubmit);

        mAuth = FirebaseAuth.getInstance();

        //Set a click listener on the button object
        submitButton.setOnClickListener(submitButton_listener);

        //Set a click listener on the editText object
        editTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddUserActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog, dateSetListener, year
                        , month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                editTextBirthday.getEditText().setText(date);
            }
        };

        String user_id = mAuth.getCurrentUser().getUid();

        //Take the acceptanceID of the current user.
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(user_id);
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                idAcceptance = user.acceptanceId;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(AddUserActivity.this, "Failed to load user information.",
                        Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent refresh = new Intent(this, AddUserActivity.class);
        startActivity(refresh);
        this.finish();

    }


    public View.OnClickListener submitButton_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (editTextName.getEditText().getText().toString().equals("") ||
                    editTextSurname.getEditText().getText().toString().equals("") ||
                    editTextCell.getEditText().getText().toString().equals("") ||
                    editTextBirthPlace.getEditText().getText().toString().equals("") ||
                    editTextBirthday.getEditText().getText().toString().equals("") ||
                    editTextMail.getEditText().getText().toString().equals("") ||
                    editTextPassword.getEditText().getText().toString().equals("") ||
                    editTextNation.getEditText().getText().toString().equals("")
            ) {
                Snackbar.make(view, getString(R.string.campi_vuoti), Snackbar.LENGTH_LONG).show();
            } else {
                registerUser();
            }
        }
    };

    //Methods that let the admin to register a new user
    private void registerUser() {
        final String name = editTextName.getEditText().getText().toString().trim();
        final String surname = editTextSurname.getEditText().getText().toString().trim();
        final String cell = editTextCell.getEditText().getText().toString().trim();
        final String birthPlace = editTextBirthPlace.getEditText().getText().toString().trim();
        final String eMail = editTextMail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();
        final String gender;
        if (maschio.isChecked()) {
            gender = maschio.getText().toString();
        }else {
            gender = femmina.getText().toString();
        }
        final int int_role;
        if (richiedenteAsilo.isChecked()) {
            int_role = 2;
        }else {
            int_role = 3;
        }
        final String dateOfBirth = editTextBirthday.getEditText().getText().toString().trim();
        final String nation = editTextNation.getEditText().getText().toString();

        mAuth.createUserWithEmailAndPassword(eMail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(
                                    mAuth.getUid(),
                                    name,
                                    surname,
                                    dateOfBirth,
                                    birthPlace,
                                    cell,
                                    gender,
                                    idAcceptance,
                                    int_role,
                                    eMail,
                                    nation
                            );

                            //The object user is entered in the DB
                            FirebaseDatabase.getInstance().getReference("user")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddUserActivity.this, "Registration success", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        //failure message
                                        Toast.makeText(AddUserActivity.this, "Addedd failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(AddUserActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
