package it.emgargano.famiapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.emgargano.famiapp.models.User;
import famiapp.R;

public class PatientProfile extends Fragment {
    private static final String ARG_PARAM1 = "patient_ID";

    private String patientID;
    private TextView mCell;
    private TextView mName;
    private TextView mEmail;
    private TextView mGender;
    private TextView mNation;
    private TextView mSurname;
    private TextView mBirthPlace;
    private TextView mDateOfBirth;
    DatabaseReference mUserReference;

    public PatientProfile() {
        // Required empty public constructor
    }

    public static PatientProfile newInstance(String patientID) {
        PatientProfile fragment = new PatientProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, patientID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        mName = rootView.findViewById(R.id.editTextName);
        mSurname = rootView.findViewById(R.id.editTextSurname);
        mDateOfBirth = rootView.findViewById(R.id.editTextBirthday);
        mBirthPlace = rootView.findViewById(R.id.editTextBirthPlace);
        mEmail = rootView.findViewById(R.id.editTextEmail);
        mCell = rootView.findViewById(R.id.editTextCell);
        mGender = rootView.findViewById(R.id.editTextGender);
        mNation = rootView.findViewById(R.id.editTextNation);

        mUserReference = FirebaseDatabase.getInstance().getReference("user").child(patientID);

        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                mName.setText(user.name);
                mSurname.setText(user.surname);
                mDateOfBirth.setText(user.date_of_birth);
                mBirthPlace.setText(user.birth_place);
                mCell.setText(user.cell);
                mGender.setText(user.gender);
                mEmail.setText(user.getMail());
                mNation.setText(user.getNation());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Toast.makeText(getContext(), "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
