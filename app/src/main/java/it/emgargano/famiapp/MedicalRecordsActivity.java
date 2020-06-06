package it.emgargano.famiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.emgargano.famiapp.models.MedicalRecord;
import it.emgargano.famiapp.sms.prova.R;

public class MedicalRecordsActivity extends AppCompatActivity {

    private static final String TAG = "MedicalRecordsActivity";
    private ImageView button_addTemperature;
    private ImageView button_addBloodPressure;
    private ImageView button_addGlycemia;
    private ImageView button_addHeartbeat;
    private ImageView button_addSymptoms;
    Button add1;
    Button add2;
    Button add3;
    Button add4;
    Button add5;
     CardView image_seeTemperatureStats;
     CardView image_seeBloodPressureStats;
     CardView image_seeGlycemiaStats;
     CardView image_seeHeartbeatStats;
     CardView image_seeSymptomsStats;
     CardView image_seePathologyStats;
    FloatingActionButton fabChat, fabBluetooth, fabNumbers;
    FloatingActionMenu fabMenu;
    private String uId;
    private String userClickedId;
    private FirebaseAuth mAuth;
    public Toolbar toolbar;
    DrawerLayout drawerLayout;
    LinearLayout linearLayout;
    ConstraintLayout expandableView1;
    ConstraintLayout expandableView2;
    ConstraintLayout expandableView3;
    ConstraintLayout expandableView4;
    ConstraintLayout expandableView5;
    TextInputLayout editValueParameter1;
    TextInputLayout editValueParameter2;
    TextInputLayout editValueParameter3;
    TextInputLayout editValueParameter4;
    TextInputLayout editValueParameter5;

    NestedScrollView nestedScrollView;
    boolean showed = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Called when the activity is starting.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records_material);

        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        linearLayout = findViewById(R.id.linear_medicalrecords);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fabMenu = findViewById(R.id.fab_menu);
        fabChat = findViewById(R.id.fabChat);
        fabBluetooth = findViewById(R.id.fabBluetooth);
        fabNumbers = findViewById(R.id.fabNumbers);

//        Behaviour of fab menu button
        fabMenu.setClosedOnTouchOutside(true);
        nestedScrollView = findViewById(R.id.nestScrollFAB);
        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (i1 > i3 && showed) {
//                Scroll down
                    fabMenu.hideMenuButton(true);
                    showed = false;
                } else if (i1 < i3 && !showed) {
//                Scroll up
                    fabMenu.showMenuButton(true);
                    showed = true;
                }
            }
        });

        expandableView1 = findViewById(R.id.expandableView1);
        expandableView2 = findViewById(R.id.expandableView2);
        expandableView3 = findViewById(R.id.expandableView3);
        expandableView4 = findViewById(R.id.expandableView4);
        expandableView5 = findViewById(R.id.expandableView5);

        editValueParameter1 = findViewById(R.id.editValueParameter1);
        editValueParameter2 = findViewById(R.id.editValueParameter2);
        editValueParameter3 = findViewById(R.id.editValueParameter3);
        editValueParameter4 = findViewById(R.id.editValueParameter4);
        editValueParameter5 = findViewById(R.id.editValueParameter5);

        add1 = findViewById(R.id.buttonSave1);
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter1.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "1");
                }
            }
        });
        add2 = findViewById(R.id.buttonSave2);
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter2.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "2");
                }
            }
        });
        add3 = findViewById(R.id.buttonSave3);
        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter3.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "3");
                }
            }
        });
        add4 = findViewById(R.id.buttonSave4);
        add4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter4.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "4");
                }
            }
        });
        add5 = findViewById(R.id.buttonSave5);
        add5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter5.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "6");
                }
            }
        });
        image_seeTemperatureStats = findViewById(R.id.image_seeTemperatureStats);
        image_seeBloodPressureStats = findViewById(R.id.image_seeBloodPressureStats);
        image_seeGlycemiaStats = findViewById(R.id.image_seeGlycemiaStats);
        image_seeHeartbeatStats = findViewById(R.id.image_seeHeartbeatStats);
        image_seeSymptomsStats = findViewById(R.id.image_seeSymptomsStats);
        image_seePathologyStats = findViewById(R.id.image_SeePathologyStats);

        button_addTemperature = findViewById(R.id.buttonAddTemperature);
        button_addBloodPressure = findViewById(R.id.buttonAddBloodPressure);
        button_addGlycemia = findViewById(R.id.buttonAddGlycemia);
        button_addHeartbeat = findViewById(R.id.buttonAddHeartbeat);
        button_addSymptoms = findViewById(R.id.buttonAddSymptoms);

        //Set a click listener on the imageButton objects
        image_seeTemperatureStats.setOnClickListener(image_seeTemperatureStats_listener);
        image_seeBloodPressureStats.setOnClickListener(image_seeBloodPressureStats_listener);
        image_seeGlycemiaStats.setOnClickListener(image_seeGlycemiaStats_listener);
        image_seeHeartbeatStats.setOnClickListener(image_seeHeartbeatStats_listener);
        image_seeSymptomsStats.setOnClickListener(image_seeSymptomsStats_listener);
        image_seePathologyStats.setOnClickListener(image_seePathologyStats_listener);

        //Set a click listener on the Button objects
        button_addTemperature.setOnClickListener(button_addTemperature_listener);
        button_addBloodPressure.setOnClickListener(button_addBloodPressure_listener);
        button_addGlycemia.setOnClickListener(button_addGlycemia_listener);
        button_addHeartbeat.setOnClickListener(button_addHeartbeat_listener);
        button_addSymptoms.setOnClickListener(button_addSymptoms_listener);

//        Behaviour of FAB buttons
        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(MedicalRecordsActivity.this,ChatActivity.class);
                startActivity(chat);
            }
        });
        fabBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openKit = new Intent(MedicalRecordsActivity.this, KitOpeningActivity.class);
                startActivity(openKit);
            }
        });
        fabNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent usefulNumbers = new Intent(MedicalRecordsActivity.this, UsefulNumbersActivity.class);
                startActivity(usefulNumbers);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
            }
        });

        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setCheckedItem(R.id.nav_medicalRecords);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_medicalRecords:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent medicalRecords = new Intent(getApplicationContext(), MedicalRecordsActivity.class);
                                startActivity(medicalRecords);
                                finish();
                            }
                        },150);
                        break;

                    case R.id.nav_centro_accoglienza:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent center = new Intent(getApplicationContext(), CentroAccoglienza.class);
                                startActivity(center);
                            }
                        },150);
                        break;

                    case R.id.nav_questionnaires:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent quest = new Intent(getApplicationContext(), QuestionnairesActivity.class);
                                startActivity(quest);
                            }
                        },150);
                        break;

                    case R.id.nav_personal_data:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent profile = new Intent(getApplicationContext(), PersonalDataActivity.class);
                                startActivity(profile);
                            }
                        },150);
                        break;

                    case R.id.nav_show_qr_code:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent qrcode = new Intent(getApplicationContext(), PopUpQrcodeActivity.class);
                                qrcode.putExtra("userId", uId);
                                startActivity(qrcode);
                            }
                        },150);
                        break;

                    case R.id.nav_documents:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent docs = new Intent(getApplicationContext(), BylawActivity.class);
                                startActivity(docs);
                            }
                        },150);
                        break;

                    case R.id.nav_video:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent video = new Intent(getApplicationContext(), VideoActivity.class);
                                startActivity(video);
                            }
                        },150);
                        break;

                    case R.id.nav_rating_app:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent rating = new Intent(getApplicationContext(), RatingActivity.class);
                                startActivity(rating);
                            }
                        },150);
                        break;

                    case R.id.nav_app_details:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent details = new Intent(getApplicationContext(), AppDetailsActivity.class);
                                startActivity(details);
                            }
                        },150);
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

//        Check if medical records are for a patient or for the user logged in.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        if (getIntent().getExtras() != null) {
            userClickedId = getIntent().getExtras().getString("user_clicked");
        } else {
            userClickedId = uId;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            fabMenu.close(true);
        }
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

    public void addNewMeasurement(String value, final String parameter) {
        //Get value to insert in DB
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String current_data = formatter.format(date);
        String pathology = "";

        //New Constructor
        MedicalRecord medicalRecord = new MedicalRecord(
                value,
                userClickedId,
                parameter,
                current_data,
                pathology
        );

        //Adding value to DB
        FirebaseDatabase.getInstance().getReference("medical_records").push().setValue(medicalRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    switch (parameter) {
                        case "1":
                            editValueParameter1.getEditText().setText("");
                            button_addTemperature.callOnClick();
                            break;

                        case "2":
                            editValueParameter2.getEditText().setText("");
                            button_addBloodPressure.callOnClick();
                            break;

                        case "3":
                            editValueParameter3.getEditText().setText("");
                            button_addGlycemia.callOnClick();
                            break;

                        case "4":
                            editValueParameter4.getEditText().setText("");
                            button_addHeartbeat.callOnClick();
                            break;

                        case "6":
                            editValueParameter5.getEditText().setText("");
                            button_addSymptoms.callOnClick();
                            break;
                    }
                    Toast.makeText(MedicalRecordsActivity.this, getString(R.string.addSuccessed), Toast.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.addFailed), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public View.OnClickListener button_addTemperature_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (expandableView1.getVisibility()== View.GONE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView1.setVisibility(View.VISIBLE);
                button_addTemperature.animate().rotation(180f).start();
            } else if (expandableView1.getVisibility()== View.VISIBLE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView1.setVisibility(View.GONE);
                button_addTemperature.animate().rotation(0f).setStartDelay(100).start();
            }
        }
    };
    public View.OnClickListener button_addBloodPressure_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (expandableView2.getVisibility()== View.GONE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView2.setVisibility(View.VISIBLE);
                button_addBloodPressure.animate().rotation(180f).start();
            } else if (expandableView2.getVisibility()== View.VISIBLE){
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView2.setVisibility(View.GONE);
                button_addBloodPressure.animate().rotation(0f).setStartDelay(100).start();
            }
        }
    };
    public View.OnClickListener button_addGlycemia_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (expandableView3.getVisibility()== View.GONE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView3.setVisibility(View.VISIBLE);
                button_addGlycemia.animate().rotation(180f).start();
            } else if (expandableView3.getVisibility()== View.VISIBLE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView3.setVisibility(View.GONE);
                button_addGlycemia.animate().rotation(0f).setStartDelay(100).start();
            }
        }
    };
    public View.OnClickListener button_addHeartbeat_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (expandableView4.getVisibility()== View.GONE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView4.setVisibility(View.VISIBLE);
                button_addHeartbeat.animate().rotation(180f).start();
            } else if (expandableView4.getVisibility()== View.VISIBLE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView4.setVisibility(View.GONE);
                button_addHeartbeat.animate().rotation(0f).setStartDelay(100).start();
            }
        }
    };
    public View.OnClickListener button_addSymptoms_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (expandableView5.getVisibility()== View.GONE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView5.setVisibility(View.VISIBLE);
                button_addSymptoms.animate().rotation(180f).start();
            } else if (expandableView5.getVisibility()== View.VISIBLE) {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView5.setVisibility(View.GONE);
                button_addSymptoms.animate().rotation(0f).setStartDelay(100).start();
            }
        }
    };

    //On Click Listener to see medical records
    public View.OnClickListener image_seeTemperatureStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_temperatureIntent = new Intent(MedicalRecordsActivity.this, ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_temperatureIntent.putExtra("user_clicked", userClickedId);
            see_temperatureIntent.putExtra("_parameter", "1");
            startActivity(see_temperatureIntent);
        }
    };
    public View.OnClickListener image_seeBloodPressureStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_bloodPressureIntent = new Intent(MedicalRecordsActivity.this, ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_bloodPressureIntent.putExtra("user_clicked", userClickedId);
            see_bloodPressureIntent.putExtra("_parameter", "2");
            startActivity(see_bloodPressureIntent);
        }
    };
    public View.OnClickListener image_seeGlycemiaStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_glycemiaIntent = new Intent(MedicalRecordsActivity.this, ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_glycemiaIntent.putExtra("user_clicked", userClickedId);
            see_glycemiaIntent.putExtra("_parameter", "3");
            startActivity(see_glycemiaIntent);
        }
    };
    public View.OnClickListener image_seeHeartbeatStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_heartbeatIntent = new Intent(MedicalRecordsActivity.this, ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_heartbeatIntent.putExtra("user_clicked", userClickedId);
            see_heartbeatIntent.putExtra("_parameter", "4");
            startActivity(see_heartbeatIntent);
        }
    };
    public View.OnClickListener image_seeSymptomsStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_symptomsIntent = new Intent(MedicalRecordsActivity.this, ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_symptomsIntent.putExtra("user_clicked", userClickedId);
            see_symptomsIntent.putExtra("_parameter", "6");
            startActivity(see_symptomsIntent);
        }
    };
    public View.OnClickListener image_seePathologyStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                Intent see_pathologyIntent = new Intent(MedicalRecordsActivity.this, ReadMedicalRecordsActivity.class);
                see_pathologyIntent.putExtra("user_clicked", userClickedId);
                see_pathologyIntent.putExtra("_parameter", "7");
                startActivity(see_pathologyIntent);
        }
    };

}
