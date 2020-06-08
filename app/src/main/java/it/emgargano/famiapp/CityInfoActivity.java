package it.emgargano.famiapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.Acceptance;
import it.emgargano.famiapp.models.City;
import famiapp.R;

public class CityInfoActivity extends Fragment {
    //variable declaration
    private static final String TAG = "CityInfoActivity";

    private String uId;
    private String cityName;
    private String acceptance;

    private DatabaseReference mUserReference;
    private DatabaseReference mCityReference;
    private DatabaseReference mAcceptanceReference;
    private CardView card_view_School;
    private CardView card_view_Pharmacy;
    private CardView card_view_Ambulatory;
    private CardView card_view_Postoffice;
    private CardView card_view_Municipality;
    private CardView card_view_PlacesOfWorship;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.activity_cityinfo, container, false);

        card_view_Ambulatory = rootview.findViewById(R.id.card_ambulatory);
        card_view_Municipality = rootview.findViewById(R.id.card_municipality);
        card_view_Postoffice = rootview.findViewById(R.id.card_postoffice);
        card_view_PlacesOfWorship = rootview.findViewById(R.id.card_placesofworship);
        card_view_School = rootview.findViewById(R.id.card_school);
        card_view_Pharmacy = rootview.findViewById(R.id.card_pharmacy);

        card_view_Ambulatory.setOnClickListener(card_view_Ambulatory_listener);
        card_view_Municipality.setOnClickListener(card_view_Municipality_listener);
        card_view_Postoffice.setOnClickListener(card_view_Postoffice_listener);
        card_view_PlacesOfWorship.setOnClickListener(card_view_PlacesOfWorship_listener);
        card_view_School.setOnClickListener(card_view_School_listener);
        card_view_Pharmacy.setOnClickListener(card_view_Pharmacy_listener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(uId);
        mAcceptanceReference = FirebaseDatabase.getInstance().getReference("acceptance");
        mCityReference = FirebaseDatabase.getInstance().getReference("city");

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        getAcceptanceId();
    }

    public void getAcceptanceId() {
        mUserReference.child("acceptanceId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                acceptance = dataSnapshot.getValue().toString();
                getCityId(acceptance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Getting Acceptance Id failed, log a message
                Log.w(TAG, "loadAcceptanceId:onCancelled", databaseError.toException());
                Toast.makeText(getActivity().getApplicationContext(), "Failed to load Acceptance Id",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getCityId(String _acceptance) {
        mAcceptanceReference.child(_acceptance).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Acceptance acceptance = dataSnapshot.getValue(Acceptance.class);
                getCityInformation(acceptance.getCity());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Getting City Id failed, log a message
                Log.w(TAG, "loadCityId:onCancelled", databaseError.toException());
                Toast.makeText(getActivity().getApplicationContext(), "Failed to load City Id",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getCityInformation(final long cityId) {
        mCityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    City city = snapshot.getValue(City.class);
                    if (city.id == cityId) {
                        cityName = city.name;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting city failed
                Log.w(TAG, "loadCity:onCancelled", databaseError.toException());
                Toast.makeText(getActivity().getApplicationContext(), "Failed to load city",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Open map positions of services
    public View.OnClickListener card_view_Ambulatory_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=pronto+soccorso%2C+" + cityName);  //Search on Google "pronto soccorso, cityName"
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };
    public View.OnClickListener card_view_Municipality_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=municipio%2C+" + cityName);
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };
    public View.OnClickListener card_view_Postoffice_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=ufficio+postale%2C+" + cityName);
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };
    public View.OnClickListener card_view_PlacesOfWorship_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=luoghi+di+culto%2C+" + cityName);
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };
    public View.OnClickListener card_view_School_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=scuola+di+italiano%2C+" + cityName);
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };
    public View.OnClickListener card_view_Pharmacy_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=farmacia%2C+" + cityName);
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };
}
