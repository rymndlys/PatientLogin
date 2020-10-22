package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PatientViewQueue extends AppCompatActivity {

    private TextView queueNumber;
    private TextView currentlyServing;
    private TextView currentTime;
    private TextView currentDate;

    ConnectionClass connectionClass;
    private KeruxSession session;//global variable
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_queue);

        connectionClass = new ConnectionClass();

        session = new KeruxSession(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        currentDate = (TextView) findViewById(R.id.txtDate);
        currentDate.setText(giveDate());
    }

    public String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        return sdf.format(cal.getTime());
    }

    private class PatientQueueNumber{

    }
}
