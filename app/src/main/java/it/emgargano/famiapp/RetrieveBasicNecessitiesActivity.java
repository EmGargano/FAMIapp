package it.emgargano.famiapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.Acceptance;
import it.emgargano.famiapp.models.City;
import it.emgargano.famiapp.models.Necessities;
import it.emgargano.famiapp.models.User;
import it.emgargano.famiapp.sms.prova.R;

public class RetrieveBasicNecessitiesActivity extends Fragment {
    //variable declaration
    private static final String TAG = "RBasicNecesActivity"; //tag too long
    private DatabaseReference mCityReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mBasicNecessities;
    private String uId;
    private Button mapFoodButton;
    private Button mapPharmacyButton;
    private String cityName;
    private Button qrcodeView1, qrcodeView2;
    private TextView mAddressFood;
    private TextView mAddressPharmacy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.activity_retrieve_basic_necessities, container, false);

        mapFoodButton = rootview.findViewById(R.id.location_foods_button);
        mAddressFood = rootview.findViewById(R.id.textViewFoodAddress);
        qrcodeView1 = rootview.findViewById(R.id.qr_code_button1);
        mapPharmacyButton = rootview.findViewById(R.id.location_pharmacy_button);
        mAddressPharmacy = rootview.findViewById(R.id.textViewPharmacyAddress);
        qrcodeView2 = rootview.findViewById(R.id.qr_code_button2);

        mapFoodButton.setOnClickListener(image_Map_Food_listener);
        mapPharmacyButton.setOnClickListener(image_Map_Pharmacy_listener);
        qrcodeView1.setOnClickListener(qrCodeGenerator);
        qrcodeView2.setOnClickListener(qrCodeGenerator);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(uId);
        mBasicNecessities = FirebaseDatabase.getInstance().getReference("basic_necessities");
        mCityReference = FirebaseDatabase.getInstance().getReference("city");

        return rootview;
    }

    public View.OnClickListener image_Map_Food_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + mAddressFood.getText());
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };

    public View.OnClickListener image_Map_Pharmacy_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + mAddressPharmacy.getText());
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };

    public View.OnClickListener qrCodeGenerator = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent qrIntent = new Intent(getActivity().getApplicationContext(), PopUpQrcodeActivity.class);
            qrIntent.putExtra("userId", uId);
            startActivity(qrIntent);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getAcceptanceId();
    }

    public void getAcceptanceId() {
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                getCityId(user.getAcceptanceId());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadAcceptanceId:onCancelled", databaseError.toException());
                Toast.makeText(getActivity().getApplicationContext(), "Failed to load Acceptance Id",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCityId(final String acceptanceId) {
        FirebaseDatabase.getInstance().getReference("acceptance").child(acceptanceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Acceptance acceptance = dataSnapshot.getValue(Acceptance.class);
                getBasicNecessitiesInfo(acceptance.getCity());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    Set basic necessities addresses to textviews.
    public void getBasicNecessitiesInfo(final long cityId) {
        getCityName(cityId);
        mBasicNecessities.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Necessities basic_necessities = snapshot.getValue(Necessities.class);
                    if (basic_necessities.city == cityId) {
                        String addressFood;
                        String addressPharmacy;
                        addressFood = basic_necessities.mall + ", " +cityName;
                        addressPharmacy = basic_necessities.pharmacy + ", " +cityName;
                        mAddressFood.setText(addressFood);
                        mAddressPharmacy.setText(addressPharmacy);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting basic necessities failed
                Log.w(TAG, "loadBasicNecessities:onCancelled", databaseError.toException());
                Toast.makeText(getActivity().getApplicationContext(), "Failed to load basic necessities",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCityName(final long cityC) {
        mCityReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    City city = snapshot.getValue(City.class);
                    if (cityC == city.id) {
                        cityName = city.name;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
