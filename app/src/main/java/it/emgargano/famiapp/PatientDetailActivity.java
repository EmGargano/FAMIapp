package it.emgargano.famiapp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;

public class PatientDetailActivity extends AppCompatActivity {
    private static final String TAG = "PatientDetailActivity";

    String patientID;
    String patientCompleteName;
    DatabaseReference mUserReference;

    ViewPager viewpager;
    BottomNavigationView bottomNavigationView;
    DashboardNavigationController dashboardNavigationController;
    PatientProfile patientProfile;
    PatientRecords patientRecords;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        viewpager = findViewById(R.id.pager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.records_bottom:
                        viewpager.setCurrentItem(0);
                        return true;
                    case R.id.profile_bottom:
                        viewpager.setCurrentItem(1);
                        return true;
                }
                return false;
            }
        });

//        Retrieve patient ID
        if (getIntent().getExtras() != null) {
            patientID = getIntent().getStringExtra("user_clicked");
        }
//        Retrieve patient name and surname
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(patientID);
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                patientCompleteName = user.getName() + " " + user.getSurname();
                if (actionBar != null) {
                    actionBar.setTitle(patientCompleteName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientDetailActivity.this, "Failed to load user information.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dashboardNavigationController = new PatientDetailActivity.DashboardNavigationController(bottomNavigationView);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                dashboardNavigationController.setNavigationTab(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        setupViewPager();
    }

    public void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        patientRecords = PatientRecords.newInstance(patientID);
        patientProfile = PatientProfile.newInstance(patientID);
        adapter.addFragment(patientRecords);
        adapter.addFragment(patientProfile);
        viewpager.setAdapter(adapter);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    public class DashboardNavigationController {

        private BottomNavigationView bottomNavigationView;
        private final TransitionSet transitionSet;
        private int activeTabPosition = 0;
        private static final long ACTIVE_ANIMATION_DURATION_MS = 115L;

        public DashboardNavigationController(BottomNavigationView bottomNavigationView) {
            this.bottomNavigationView = bottomNavigationView;

//XXX copy of BottomNavigationAnimationHelperIcs transition set
            transitionSet = new TransitionSet();
            transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
            transitionSet.setDuration(ACTIVE_ANIMATION_DURATION_MS);
            transitionSet.setInterpolator(new FastOutSlowInInterpolator());
            @SuppressLint("RestrictedApi") TransitionSet textScale = new TransitionSet();
            transitionSet.addTransition(textScale);
        }

        public void setNavigationTab(int position) {
//XXX copy of BottomNavigationMenuView.activateNewButton

            if (activeTabPosition == position) {
                return;
            } else {
                activeTabPosition = position;
            }

            TransitionManager.beginDelayedTransition(bottomNavigationView, transitionSet);

            bottomNavigationView.getMenu().getItem(position).setChecked(true);
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