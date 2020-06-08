package it.emgargano.famiapp;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import it.emgargano.famiapp.models.User;
import famiapp.R;

import static android.content.Context.DOWNLOAD_SERVICE;

public class AcceptanceActivity extends Fragment {
    //variable declaration
    private static final String TAG = "AcceptanceActivity";
    private DatabaseReference mUserReference;
    private DatabaseReference mAcceptanceReference;
    private DatabaseReference mCityReference;
    private String uId;
    private String acceptanceId;
    private String cityName;
    private TextView mName;
    private TextView mCity;
    private TextView mAddress;
    private TextView cityDescription;
    Button city, regulation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.activity_acceptance, container, false);

        mName = rootview.findViewById(R.id.editTextCenterName);
        mAddress = rootview.findViewById(R.id.editTextCenterLocation);
        mCity = rootview.findViewById(R.id.editTextCenterCity);
        regulation = rootview.findViewById(R.id.regulation_button);
        city = rootview.findViewById(R.id.city_button);
        cityDescription = rootview.findViewById(R.id.cityDescription);
        regulation.setOnClickListener(regulationlistener);
        city.setOnClickListener(citylistener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(uId);
        mAcceptanceReference = FirebaseDatabase.getInstance().getReference("acceptance");
        mCityReference = FirebaseDatabase.getInstance().getReference("city");

        return rootview;
    }


    @Override
    public void onStart() {  //Method called when the activity is started
        super.onStart();
        getAcceptanceId();
    }

//    Retrieve acceptanceID.
    public void getAcceptanceId() {
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            //called with a snapshot of the data at this location
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                acceptanceId = dataSnapshot.getValue(User.class).getAcceptanceId();
                getAcceptanceInfo(acceptanceId);
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

//    Retrive acceptance info.
    public void getAcceptanceInfo(String acceptanceId) {
        ValueEventListener cityListener = new ValueEventListener() {
            @Override
            //called with a snapshot of the data at this location
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Acceptance acceptance = dataSnapshot.getValue(Acceptance.class);
                findCityName(acceptance.getCity());
                mName.setText(acceptance.name);
                mAddress.setText(acceptance.address);
            }
            @Override
            //triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase rules.
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting acceptance failed
                Log.w(TAG, "loadAcceptance:onCancelled", databaseError.toException());
                Toast.makeText(getActivity().getApplicationContext(), "Failed to load acceptance",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mAcceptanceReference.child(acceptanceId).addValueEventListener(cityListener);
    }

//    Retrieve name of the city and description.
    public void findCityName(final long cityId) {
        DatabaseReference mCityDB = FirebaseDatabase.getInstance().getReference("city");
        mCityDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            //called with a snapshot of the data at this location
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    City city = snapshot.getValue(City.class);
                    if (cityId == city.getId()) {
                        cityName = city.name;
                        mCity.setText(cityName);
                        String cityInfo = city.name + " " + city.description;
                        cityDescription.setText(cityInfo);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting city failed
                Log.w(TAG, "loadCity:onCancelled", databaseError.toException());
                Toast.makeText(getActivity().getApplicationContext() , "Failed to load city.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public View.OnClickListener citylistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("geo:0,0?q=" + cityName);
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    };

    public View.OnClickListener regulationlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Uri pdf = Uri.parse("https://firebasestorage.googleapis.com/v0/b/asilapp-1dd34.appspot.com/o/Regolamento%20strutture%20di%20accoglienza%20convenzionate.pdf?alt=media&token=85697a3e-ed4f-473c-b8d3-37e5b0005d1d");
            DownloadData(pdf, view);
        }
    };
    private long DownloadData(Uri uri, View v) {
        long downloadReference;
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle("Regolamento interno centro di accoglienza");
        request.setDescription(getString(R.string.app_name));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getContext(),
                Environment.DIRECTORY_DOWNLOADS, "help.png");
        downloadReference = downloadManager.enqueue(request);

        Toast.makeText(getContext(), getResources().getString(R.string.downloadSuccess),
                Toast.LENGTH_LONG).show();
        return downloadReference;
    }

}
