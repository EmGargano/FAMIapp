package it.emgargano.famiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.emgargano.famiapp.models.MedicalRecord;
import it.emgargano.famiapp.sms.prova.R;

public class PatientRecords extends Fragment {
    private static final String ARG_PARAM1 = "patient_ID";
    private String patientID;

    private ImageView button_addTemperature;
    private ImageView button_addBloodPressure;
    private ImageView button_addGlycemia;
    private ImageView button_addHeartbeat;
    private ImageView button_addSymptoms;
    ImageView button_addPathology;
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
    LinearLayout linearLayout;

    public PatientRecords() {
        // Required empty public constructor
    }

    public static PatientRecords newInstance(String patientID) {
        PatientRecords fragment = new PatientRecords();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_records, container, false);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getActivity().getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                getActivity().getCurrentFocus().clearFocus();
                }
                return true;
            }
        });

        linearLayout = rootView.findViewById(R.id.linear_medicalrecords);
        expandableView1 = rootView.findViewById(R.id.expandableView1);
        expandableView2 = rootView.findViewById(R.id.expandableView2);
        expandableView3 = rootView.findViewById(R.id.expandableView3);
        expandableView4 = rootView.findViewById(R.id.expandableView4);
        expandableView5 = rootView.findViewById(R.id.expandableView5);

        editValueParameter1 = rootView.findViewById(R.id.editValueParameter1);
        editValueParameter2 = rootView.findViewById(R.id.editValueParameter2);
        editValueParameter3 = rootView.findViewById(R.id.editValueParameter3);
        editValueParameter4 = rootView.findViewById(R.id.editValueParameter4);
        editValueParameter5 = rootView.findViewById(R.id.editValueParameter5);

        add1 = rootView.findViewById(R.id.buttonSave1);
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter1.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "1");
                }
            }
        });
        add2 = rootView.findViewById(R.id.buttonSave2);
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter2.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "2");
                }
            }
        });
        add3 = rootView.findViewById(R.id.buttonSave3);
        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter3.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "3");
                }
            }
        });
        add4 = rootView.findViewById(R.id.buttonSave4);
        add4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter4.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "4");
                }
            }
        });
        add5 = rootView.findViewById(R.id.buttonSave5);
        add5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editValueParameter5.getEditText().getText().toString();
                if(!value.isEmpty()) {
                    addNewMeasurement(value, "6");
                }
            }
        });
        image_seeTemperatureStats = rootView.findViewById(R.id.image_seeTemperatureStats);
        image_seeBloodPressureStats = rootView.findViewById(R.id.image_seeBloodPressureStats);
        image_seeGlycemiaStats = rootView.findViewById(R.id.image_seeGlycemiaStats);
        image_seeHeartbeatStats = rootView.findViewById(R.id.image_seeHeartbeatStats);
        image_seeSymptomsStats = rootView.findViewById(R.id.image_seeSymptomsStats);
        image_seePathologyStats = rootView.findViewById(R.id.image_SeePathologyStats);

        button_addTemperature = rootView.findViewById(R.id.buttonAddTemperature);
        button_addBloodPressure = rootView.findViewById(R.id.buttonAddBloodPressure);
        button_addGlycemia = rootView.findViewById(R.id.buttonAddGlycemia);
        button_addHeartbeat = rootView.findViewById(R.id.buttonAddHeartbeat);
        button_addSymptoms = rootView.findViewById(R.id.buttonAddSymptoms);
        button_addPathology = rootView.findViewById(R.id.buttonAddPathology);

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
        button_addPathology.setOnClickListener(button_addPathology_listener);



        return rootView;
    }

    public void addNewMeasurement(String value, String parameter) {
        //Get value to insert in DB
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String current_data = formatter.format(date);
        String pathology = "";

        //New Constructor
        MedicalRecord medicalRecord = new MedicalRecord(
                value,
                patientID,
                parameter,
                current_data,
                pathology
        );

        //Adding value to DB
        FirebaseDatabase.getInstance().getReference("medical_records").push().setValue(medicalRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //success message
                    Toast.makeText(getContext(), "Addedd successfully", Toast.LENGTH_LONG).show();
                } else {
                    //failure message
                    Toast.makeText(getContext(), "Addedd failed", Toast.LENGTH_LONG).show();
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
            } else {
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
            } else {
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
            } else {
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
            } else {
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
            } else {
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
                expandableView5.setVisibility(View.GONE);
                button_addSymptoms.animate().rotation(0f).setStartDelay(100).start();
            }
        }
    };
    public View.OnClickListener button_addPathology_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent add_pathologyIntent = new Intent(getContext(), AddPathologyActivity.class);
            //Pass data between intents
            add_pathologyIntent.putExtra("user_clicked", patientID);
            add_pathologyIntent.putExtra("_parameter", "7");
            startActivity(add_pathologyIntent);
        }
    };

    //On Click Listener to see medical records
    public View.OnClickListener image_seeTemperatureStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_temperatureIntent = new Intent(getContext(), ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_temperatureIntent.putExtra("user_clicked", patientID);
            see_temperatureIntent.putExtra("_parameter", "1");
            startActivity(see_temperatureIntent);
        }
    };
    public View.OnClickListener image_seeBloodPressureStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_bloodPressureIntent = new Intent(getContext(), ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_bloodPressureIntent.putExtra("user_clicked", patientID);
            see_bloodPressureIntent.putExtra("_parameter", "2");
            startActivity(see_bloodPressureIntent);
        }
    };
    public View.OnClickListener image_seeGlycemiaStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_glycemiaIntent = new Intent(getContext(), ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_glycemiaIntent.putExtra("user_clicked", patientID);
            see_glycemiaIntent.putExtra("_parameter", "3");
            startActivity(see_glycemiaIntent);
        }
    };
    public View.OnClickListener image_seeHeartbeatStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_heartbeatIntent = new Intent(getContext(), ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_heartbeatIntent.putExtra("user_clicked", patientID);
            see_heartbeatIntent.putExtra("_parameter", "4");
            startActivity(see_heartbeatIntent);
        }
    };
    public View.OnClickListener image_seeSymptomsStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Create new Intent
            Intent see_symptomsIntent = new Intent(getContext(), ReadMedicalRecordsActivity.class);
            //Pass data between intents
            see_symptomsIntent.putExtra("user_clicked", patientID);
            see_symptomsIntent.putExtra("_parameter", "6");
            startActivity(see_symptomsIntent);
        }
    };
    public View.OnClickListener image_seePathologyStats_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent see_pathologyIntent = new Intent(getContext(), ReadMedicalRecordsActivity.class);
            see_pathologyIntent.putExtra("user_clicked", patientID);
            see_pathologyIntent.putExtra("_parameter", "7");
            startActivity(see_pathologyIntent);
        }
    };

}
