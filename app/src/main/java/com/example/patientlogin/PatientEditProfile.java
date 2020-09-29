package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.patientlogin.dbutility.DBUtility;
import com.example.patientlogin.security.Security;
import com.example.patientlogin.spinner.Downloader;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.ui.AppBarConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.patientlogin.App.CHANNEL_1_ID;
import static com.example.patientlogin.App.CHANNEL_2_ID;
import static com.example.patientlogin.dbutility.DBUtility.EDIT_PROFILE;
import static com.example.patientlogin.KeruxSession.*;
import static com.example.patientlogin.dbutility.DBUtility.UPDATE_PROFILE;

public class PatientEditProfile extends AppCompatActivity {

    private EditText patientName;
    private EditText patientPassword;
    private EditText patientEmail;
    private EditText patientContactNo;

    ConnectionClass connectionClass;
    private KeruxSession session;//global variable
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_edit_profile);

        connectionClass = new ConnectionClass();

        TextView title = (TextView) findViewById(R.id.dateToday);
        title.setText(giveDate());

        session = new KeruxSession(getApplicationContext());


        patientName = (EditText)findViewById(R.id.editPatientName);
        patientPassword = (EditText)findViewById(R.id.editPatientPassword);
        patientEmail = (EditText)findViewById(R.id.editEmailText);
        patientContactNo = (EditText)findViewById(R.id.editPatientContactNo);

    }

    public String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        return sdf.format(cal.getTime());
    }

    //=================================================================================================================

    /*private class retrievePatientInfo extends AsyncTask<String,String,String> {

        String patientNam = patientName.getText().toString();
        String patientPass = patientPassword.getText().toString();
        String patientEmai = patientEmail.getText().toString();
        String patientContactN = patientContactNo.getText().toString();

        String z = "";
        boolean isSuccess = false;

        String pID;

        ArrayList<String> ar = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        *//*sql retrieves current patient data using select method and place in corresponding text fields
                user will now have the chance to edit the said text fields
                user will press the update button
                sql update will get the entry from the fields and push the new data to the database
                make an intent that will either go back to the dashboard or stay at the edit profile page*//*

        @Override
        protected String doInBackground(String... params) {
            if(patientNam.trim().equals("")||patientPass.trim().equals(""))
                z = "Please enter values in the Name and Password....";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        try {


                            String query = EDIT_PROFILE; //select
                            String query1 = UPDATE_PROFILE; //update

                            PreparedStatement ps = con.prepareStatement(query); // ps for query which is edit profile
                            ps.setString(1, session.getpatientid());
                            *//*ps.setString(2, session.getusername());*//*



                            // Statement stmt = con.createStatement();
                            // stmt.executeUpdate(query);
                            ResultSet rs = ps.executeQuery(); // rs used by ps which is edit profile

                            while (rs.next()) {
                                pID = rs.getString(1);



                                *//*ar.add(patientNam);
                                ar.add(patientContactN);
                                ar.add(patientEmai);
                                ar.add(patientPass);*//*

                                if (cn.equals(usernam) && pass.equals(passstr)) {
                                    session.setcontactno(cn);
                                    session.setpassword(pass);
                                    session.setusername(pName);
                                    session.setpatientid(pID);
                                    isSuccess = true;
                                    z = "Login successfull";
                                } else
                                    isSuccess = false;

                            }


                        } catch (Exception e) {

                            Thread.dumpStack(); //always put this from sir mon
                        }
                    }
                }catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();
            if(isSuccess) {
                Intent intent=new Intent(PatientEditProfile.this,PatientDashboard.class);
                // intent.putExtra("name",usernam);
                *//*intent.putExtra("NAME", pName);*//*
                startActivity(intent);

            }
            progressDialog.hide();
        }


    }*/


}