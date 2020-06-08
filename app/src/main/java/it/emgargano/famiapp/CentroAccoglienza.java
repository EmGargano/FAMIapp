package it.emgargano.famiapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.Lifecycle;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import famiapp.R;


public class CentroAccoglienza extends AppCompatActivity {

    ViewPager2 viewpager;
    BottomNavigationView bottomNavigationView;
    AcceptanceActivity acceptanceFragment;
    CityInfoActivity cityInfoFragment;
    RetrieveBasicNecessitiesActivity retrieveBasicNecessitiesFragment;
    DashboardNavigationController dashboardNavigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_accoglienza);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        viewpager = findViewById(R.id.pager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.info_bottom:
                        viewpager.setCurrentItem(0);
                        return true;
                    case R.id.service_bottom:
                        viewpager.setCurrentItem(1);
                        return true;
                    case R.id.goods_bottom:
                        viewpager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });

        dashboardNavigationController = new DashboardNavigationController(bottomNavigationView);
        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                dashboardNavigationController.setNavigationTab(i);
                String title = bottomNavigationView.getMenu().getItem(i).getTitle().toString();
                getSupportActionBar().setTitle(title);
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        setupViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int i = viewpager.getCurrentItem();
        getSupportActionBar().setTitle(bottomNavigationView.getMenu().getItem(i).getTitle().toString());
    }

    public void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        acceptanceFragment = new AcceptanceActivity();
        cityInfoFragment = new CityInfoActivity();
        retrieveBasicNecessitiesFragment = new RetrieveBasicNecessitiesActivity();
        adapter.addFragment(acceptanceFragment);
        adapter.addFragment(cityInfoFragment);
        adapter.addFragment(retrieveBasicNecessitiesFragment);
        viewpager.setAdapter(adapter);
    }

    public static class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm, Lifecycle lifecycle) {
            super(fm, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public int getItemCount() {
            return mFragmentList.size();
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

}
