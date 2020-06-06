package it.emgargano.famiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.emgargano.famiapp.adapter.PatientAdapter;
import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;

public class PatientListActivity extends AppCompatActivity {

    private static final String TAG = "PatientListActivity";
    private DatabaseReference mUserReference;
    private TextInputLayout searchText;
    private List<User> mUserList;
    private PatientAdapter patientAdapter;
    private RecyclerView recyclerView;
    public Toolbar toolbar;
    DrawerLayout drawerLayout;
    FloatingActionButton fabQRcode, fabBluetooth;
    FloatingActionMenu fabMenu;
    FirebaseAuth mAuth;
    LinearLayout linearLayout;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Called when the activity is starting.
        super.onCreate(savedInstanceState);
        //Set the activity content from a layout resource.
        setContentView(R.layout.activity_patient_list);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        fabMenu = findViewById(R.id.fab_menu);
        searchText = findViewById(R.id.searchText);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        linearLayout = findViewById(R.id.linear_patients_list);

        fabMenu.setClosedOnTouchOutside(true);
        fabBluetooth = findViewById(R.id.fabBluetooth);
        fabQRcode = findViewById(R.id.fabQRcode);

        fabQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrcode = new Intent(PatientListActivity.this, ScanActivity.class);
                startActivity(qrcode);
            }
        });

        fabBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kitOpening = new Intent(PatientListActivity.this, KitOpeningActivity.class);
                startActivity(kitOpening);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.userList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setCheckedItem(R.id.nav_patients_list);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_patients_list:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent medicalRecords = new Intent(getApplicationContext(), PatientListActivity.class);
                                startActivity(medicalRecords);
                                finish();
                            }
                        }, 150);
                        break;

                    case R.id.nav_chat:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent chat = new Intent(getApplicationContext(), ChatActivity.class);
                                startActivity(chat);
                            }
                        }, 150);
                        break;

                    case R.id.nav_video:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent video = new Intent(getApplicationContext(), VideoActivity.class);
                                startActivity(video);
                            }
                        }, 150);
                        break;

                    case R.id.nav_questionnaires:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent quest = new Intent(getApplicationContext(), QuestionnairesActivity.class);
                                quest.putExtra("user_clicked", user);
                                startActivity(quest);
                            }
                        }, 150);
                        break;

                    case R.id.nav_app_details:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent details = new Intent(getApplicationContext(), AppDetailsActivity.class);
                                startActivity(details);
                            }
                        }, 150);
                        break;

                    case R.id.nav_logout:
                        mAuth.signOut();
                        Intent signout = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(signout);
                        finish();
                        break;
                }
                return false;
            }
        });

    }

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
    protected void onStart() {   //Called when the activity had been stopped.
        super.onStart();
        readUserList();
    }

    public void readUserList() {
        //ArrayList variable
        mUserList = new ArrayList<>();
        //Initialize Database Reference
        mUserReference = FirebaseDatabase.getInstance().getReference("user");
        //Used to synchronize data
        mUserReference.keepSynced(true);
        //Add value
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            //called with a snapshot of the data at this location
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserList.clear();
                searchText.getEditText().setText("");
//                Log.d(TAG,"dentro onDataChange");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d(TAG,"dentro for datasnapshot");
                    final User user = snapshot.getValue(User.class);
                    if (user.getRole() == 2) {
//                        Log.d(TAG,"dentro if utenteRuolo = 2");
                        mUserList.add(user);
                    }
                } if(mUserList.isEmpty()) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.emptyPatientList), Snackbar.LENGTH_LONG).show();
                } else {
                    patientAdapter = new PatientAdapter(PatientListActivity.this, mUserList);
                    recyclerView.setAdapter(patientAdapter);
                }
            }

            @Override
            //triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase rules.
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting user failed
//                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(PatientListActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        searchText.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG,"dentro onTextChanged: " + charSequence);
                if (patientAdapter != null) {
                    patientAdapter.getFilter().filter(charSequence.toString());
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            fabMenu.close(true);
        }
    }

}
