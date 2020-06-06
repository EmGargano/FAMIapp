package it.emgargano.famiapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.Questionnaires;
import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;

public class QuestionnairesActivity extends AppCompatActivity {
    //variable declaration
    private static final String TAG = "QuestionnairesActivity";

    private Button btnSf12;
    private Button btnDemo;
    private Button btnHabits;
    private Button btnQuality;
    private DatabaseReference mUserReference;
    private DatabaseReference mQuestionnaires;
    private int mRole;
    private String uId;
    private String userClickedId;
    private String URI1 = "null", URI2 = "null", URI3 = "null", URI4 = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaires);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnSf12 = findViewById(R.id.btnSf12);
        btnDemo = findViewById(R.id.btnDemo);
        btnHabits = findViewById(R.id.btnHabits);
        btnQuality = findViewById(R.id.btnQuality);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Initialize variable to get extra value from other intent
        if (getIntent().getExtras() != null) {
            userClickedId = getIntent().getExtras().getString("user_clicked");
        }
        //Check the user id of caller, if Dottore or Richiedente asilo
        if (userClickedId != null) {
            uId = userClickedId;
        } else {
            uId = user.getUid();
        }
        // Initialize Database Reference
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(uId);
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object
                User user = dataSnapshot.getValue(User.class);
                mRole = user.getRole();
//              Set the link of questionnaries for the right user.
                setLinkQuest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting User Role failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                Toast.makeText(QuestionnairesActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnSf12.setOnClickListener(btnSf12_listener);
        btnDemo.setOnClickListener(btnDemo_listener);
        btnHabits.setOnClickListener(btnHabits_listener);
        btnQuality.setOnClickListener(btnQuality_listener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent refresh = new Intent(this, QuestionnairesActivity.class);
        startActivity(refresh);
        this.finish();

    }


    public View.OnClickListener btnSf12_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URI1));
            startActivity(i);
        }
    };

    public View.OnClickListener btnHabits_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URI2));
            startActivity(i);
        }
    };

    public View.OnClickListener btnDemo_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URI3));
            startActivity(i);
        }
    };

    public View.OnClickListener btnQuality_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URI4));
            startActivity(i);

        }
    };

    public void setLinkQuest() {
        if (mRole == 2) {    //role 2 = Richiedente asilo
            mQuestionnaires = FirebaseDatabase.getInstance().getReference("questionnaires").child("1");
            mQuestionnaires.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Questionnaires questionnaires = dataSnapshot.getValue(Questionnaires.class);
                    URI1 = String.valueOf(questionnaires.uri);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting questionnaires failed
                    Log.w(TAG, "loadQuestionnaires:onCancelled", databaseError.toException());
                    Toast.makeText(QuestionnairesActivity.this, "Failed to load questionnaires.",
                            Toast.LENGTH_SHORT).show();
                }


            });
            mQuestionnaires = FirebaseDatabase.getInstance().getReference("questionnaires").child("2");
            mQuestionnaires.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Questionnaires questionnaires = dataSnapshot.getValue(Questionnaires.class);
                    URI2 = String.valueOf(questionnaires.uri);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting questionnaires failed
                    Log.w(TAG, "loadQuestionnaires:onCancelled", databaseError.toException());
                    Toast.makeText(QuestionnairesActivity.this, "Failed to load questionnaires.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            mQuestionnaires = FirebaseDatabase.getInstance().getReference("questionnaires").child("3");
            mQuestionnaires.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Questionnaires questionnaires = dataSnapshot.getValue(Questionnaires.class);
                    URI3 = String.valueOf(questionnaires.uri);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting questionnaires failed
                    Log.w(TAG, "loadQuestionnaires:onCancelled", databaseError.toException());
                    Toast.makeText(QuestionnairesActivity.this, "Failed to load questionnaires.",
                            Toast.LENGTH_SHORT).show();
                }
            });

            mQuestionnaires = FirebaseDatabase.getInstance().getReference("questionnaires").child("4");
            mQuestionnaires.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Questionnaires questionnaires = dataSnapshot.getValue(Questionnaires.class);
                    URI4 = String.valueOf(questionnaires.uri);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting questionnaires failed
                    Log.w(TAG, "loadQuestionnaires:onCancelled", databaseError.toException());
                    Toast.makeText(QuestionnairesActivity.this, "Failed to load questionnaires.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (mRole == 3) { //role 3 = Dottore
            URI1 = "https://docs.google.com/forms/d/1wqZ8QBlmsvdBhkEgpl0RTJ8rtlG_is7EISbfPTn7MqY/edit#responses";
            URI2 = "https://docs.google.com/forms/d/1F-6byb6dDpGzT2wq5s2vPKflw6pq1u7PCKn4nf5lD_I/edit#responses";
            URI3 = "https://docs.google.com/forms/d/1_yNp6zPo-7ipKg1q2UIg7TP6fuwQD4rchTDJqa7FfQI/edit#responses";
            URI4 = "https://docs.google.com/forms/d/1x7PXgG_dvIDqGFIquZF3s_rZY-RmCNoOoLcnr2yqZIM/edit#responses";
        }
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
