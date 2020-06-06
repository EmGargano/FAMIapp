package it.emgargano.famiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;

public class LoginActivity extends AppCompatActivity {
    //Variable declaration
    private static final String TAG = "LoginActivity.class";
    private Button loginButton, resetPassword;
    private DatabaseReference mUserReference;
    private EditText mEmailView;
    private EditText mPasswordView;
    private FirebaseAuth mAuth;
    private String uId;
    private int mRole;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Defined variable
        mEmailView = (EditText) findViewById(R.id.input_email);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        resetPassword = (Button) findViewById(R.id.password_reset);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String email = mEmailView.getText().toString();
                if (email.isEmpty()) {
                    Snackbar.make(view,R.string.insert_email,4000).show();
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar.make(view,R.string.email_sent,2000).show();
                        }
                    });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String email = mEmailView.getText().toString();
                final String psw = mPasswordView.getText().toString();
                mAuth = FirebaseAuth.getInstance();

                if (email.isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Snackbar.make(view,R.string.loginFailed,Snackbar.LENGTH_LONG).show();
                } else if (psw.isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Snackbar.make(view,R.string.loginFailed,Snackbar.LENGTH_LONG).show();
                    } else {
                    progressBar.setVisibility(View.VISIBLE);
                    //Tries to sign in a user with the given email address and password
                    mAuth.signInWithEmailAndPassword(email, psw).
                            addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        onLoginSuccess(view);
                                    } else {
                                        onLoginFailed(view);

                                    }
                                }
                            });
                }
            }
        });

//        Hide/show status bar and nav bar when touched.
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hideSystemUI();
                                }
                            }, 1000);
                        }
                    }
                });

    }

//    Hide soft keyboard on screen touch.
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

//    Status bar and nav bar hided, fullscreen mode on.
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
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    };

    //Get role id to load correct homepage
    public void getRoleActivity(int role_id) {
        progressBar.setVisibility(View.INVISIBLE);
        if (role_id == 1) { //Centro di accoglienza Role
            Intent adminIntent = new Intent(LoginActivity.this, AdminActivity.class);
            startActivity(adminIntent);
            finish();
        } else if (role_id == 2) {  //Richiedente asilo Role
            Intent userIntent = new Intent(LoginActivity.this, MedicalRecordsActivity.class);
            startActivity(userIntent);
            finish();
        } else if (role_id == 3) {  //Dottore Role
            Intent doctorIntent= new Intent(LoginActivity.this, PatientListActivity.class);
            startActivity(doctorIntent);
            finish();
        }
    }

    public void onLoginSuccess(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(uId);
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            //called with a snapshot of the data at this location
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object
                User user = dataSnapshot.getValue(User.class);
                mRole = user.getRole();
                getRoleActivity(mRole);
            }
            @Override
            //triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase rules.
            public void onCancelled(DatabaseError databaseError) {
                // Getting User Role failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(LoginActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLoginFailed(View view) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar.make(view, R.string.loginFailed,Snackbar.LENGTH_SHORT).show();
    }
}
