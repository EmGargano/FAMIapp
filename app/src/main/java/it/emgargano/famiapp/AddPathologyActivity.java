package it.emgargano.famiapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.emgargano.famiapp.models.MedicalRecord;
import it.emgargano.famiapp.models.Pathology;
import it.emgargano.famiapp.sms.prova.R;

public class AddPathologyActivity extends AppCompatActivity {
    private static final String TAG = "AddPathologyActivity";

    private TextInputLayout mName;
    private TextInputLayout mDescription;
    private TextInputLayout mNutritional;
    private TextInputLayout mLifestyle;
    private TextInputLayout mMedicines;
    private Button submitButton;
    private String user_clicked;
    private String parameter_clicked;
    String name, description, nutritional, lifestyle, medicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pathology);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mName = findViewById(R.id.edit_name_pathology);
        mDescription = findViewById(R.id.edit_description_pathology);
        mNutritional = findViewById(R.id.edit_nutritional_pathology);
        mLifestyle = findViewById(R.id.edit_lifestyle_pathology);
        mMedicines = findViewById(R.id.edit_medicines_pathology);
        submitButton = findViewById(R.id.buttonSavePathology);
        submitButton.setOnClickListener(submitButton_listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent refresh = new Intent(this, AddPathologyActivity.class);
        startActivity(refresh);
        this.finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getCurrentFocus().clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }


    public View.OnClickListener submitButton_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            name = mName.getEditText().getText().toString();
            description = mDescription.getEditText().getText().toString();
            nutritional = mNutritional.getEditText().getText().toString();
            lifestyle = mLifestyle.getEditText().getText().toString();
            medicine = mMedicines.getEditText().getText().toString();

            if (name.isEmpty() ||
                    description.isEmpty() ||
                    nutritional.isEmpty() ||
                    lifestyle.isEmpty() ||
                    medicine.isEmpty()
            ) {
                Snackbar.make(view, getString(R.string.campi_vuoti), Snackbar.LENGTH_LONG).show();
            } else {
                addPathology(view);
            }
        }
    };

    public void addPathology(final View view) {
        user_clicked = getIntent().getExtras().getString("user_clicked");
        parameter_clicked = getIntent().getExtras().getString("_parameter");

        //Get id of pathology pushed
        String pathology_id = FirebaseDatabase.getInstance().getReference("acceptance").push().getKey();

        //New Constructor pathology
        Pathology pathology = new Pathology(
                name,
                description,
                nutritional,
                lifestyle,
                medicine
        );

        FirebaseDatabase.getInstance().getReference("pathology").child(pathology_id).setValue(pathology);
        //Get current data and set format
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String current_data = formatter.format(date);
        //New Constructor medical record
        MedicalRecord medicalRecord = new MedicalRecord(
                name,
                user_clicked,
                parameter_clicked,
                current_data,
                pathology_id
        );

        //Adding value to DB
        FirebaseDatabase.getInstance().getReference("medical_records").push().setValue(medicalRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //success message
                    Toast.makeText(AddPathologyActivity.this, getString(R.string.successPathology), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    //failure message
                    Snackbar.make(view, getString(R.string.failurePathology), Toast.LENGTH_LONG).show();
                }
            }
        });
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
