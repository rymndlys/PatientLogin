package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


import static com.example.patientlogin.dbutility.DBUtility.CONFIRM_PATIENT_PASS;
import static com.example.patientlogin.dbutility.DBUtility.UPDATE_PROFILE;
import static com.example.patientlogin.dbutility.DBUtility.UPDATE_PROFILE_PASS;


public class PatientEditProfile extends AppCompatActivity {

    private EditText patientFName;
    private EditText patientLName;
    private EditText patientNewPassword;
    private EditText patientOldPassword;
    private EditText patientEmail;
    private EditText patientContactNo;
    private String PatientID;
    private Button saveChanges;
    private Button confirmPass;

    ConnectionClass connectionClass;
    private KeruxSession session;//global variable
    ProgressDialog progressDialog;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_edit_profile);

        connectionClass = new ConnectionClass();

        session = new KeruxSession(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        patientFName = (EditText)findViewById(R.id.editFirstName);
        patientLName = (EditText)findViewById(R.id.editLastName);
        patientNewPassword = (EditText)findViewById(R.id.editPatientPassword);
        patientOldPassword = (EditText)findViewById(R.id.editPatientOldPassword);
        patientEmail = (EditText)findViewById(R.id.editEmailText);
        patientContactNo = (EditText)findViewById(R.id.editPatientContactNo);

        patientFName.setText(session.getfirstname());
        patientLName.setText(session.getlastname());
        patientContactNo.setText(session.getcontactno());
        patientEmail.setText(session.getemail());
        PatientID=session.getpatientid();

        saveChanges = (Button)findViewById(R.id.button_save);
        confirmPass = (Button)findViewById(R.id.button_confirm);

        saveChanges.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                updatePatientInfo updatepinfo=new updatePatientInfo();
                updatepinfo.execute();
            }
        });

        confirmPass.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                updatePatientPass updateppass=new updatePatientPass();
                updateppass.execute();
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);

    }

    public void ClickMenu (View view){
        //open drawer
        PatientDashboard.openDrawer(drawerLayout);
    }

    public void ClickLogo (View view){
        //Close drawer
        PatientDashboard.closeDrawer(drawerLayout);
    }

    public void ClickDashboard(View view){
        //Redirect activity to dashboard
        PatientDashboard.redirectActivity(this, PatientDashboard.class);
    }

    public void ClickQueueUp(View view){
        //recreate activity
        PatientDashboard.redirectActivity(this, PatientQueue.class);
    }

    public void ClickViewCurrentQueue(View view){
        //Redirect
        PatientDashboard.redirectActivity(this, PatientViewQueue.class);
    }

    public void ClickScanQR(View view){
        //redirect
        PatientDashboard.redirectActivity(this, PatientScanQR.class);
    }

    public void ClickEditProfile(View view){
        //recreate activity
        recreate();
    }

    public void ClickSubmitReport(View view){
        //redirect
        PatientDashboard.redirectActivity(this, PatientReport.class);
    }

    public void ClickLogout(View view){
        PatientDashboard.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        PatientDashboard.closeDrawer(drawerLayout);
    }

    //=================================================================================================================

    private class updatePatientInfo extends AsyncTask<String,String,String> {
        String patientFirstName=patientFName.getText().toString();
        String patientLastName=patientLName.getText().toString();
        String patientEmai = patientEmail.getText().toString();
        String patientContactN = patientContactNo.getText().toString();

        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        /*sql retrieves current patient data using select method and place in corresponding text fields
                user will now have the chance to edit the said text fields
                user will press the update button
                sql update will get the entry from the fields and push the new data to the database
                make an intent that will either go back to the dashboard or stay at the edit profile page*/

        @Override
        protected String doInBackground(String... params) {
            if(patientFirstName.trim().equals("")||patientLastName.trim().equals(""))
                z = "Please enter values in the First Name and Last Name.";
            else
            {
                try {

                            URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/UpdatePatientProfile");
                            URLConnection connection = url.openConnection();

                            connection.setReadTimeout(10000);
                            connection.setConnectTimeout(15000);
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            Uri.Builder builder = new Uri.Builder()
                                    .appendQueryParameter("patientEmai", patientEmai)
                                    .appendQueryParameter("patientFirstName", patientFirstName)
                                    .appendQueryParameter("patientLastName", patientLastName)
                                    .appendQueryParameter("patientContactN", patientContactN)
                                    .appendQueryParameter("PatientID", PatientID);
                            String query = builder.build().getEncodedQuery();

                            OutputStream os = connection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(query);
                            writer.flush();
                            writer.close();
                            os.close();

                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String returnString="";
                            ArrayList<String> output=new ArrayList<String>();
                            while ((returnString = in.readLine()) != null)
                            {
                                session.setemail(patientEmai);
                                session.setfirstname(patientFirstName);
                                session.setlastname(patientLastName);
                                session.setcontactno(patientContactN);
                                isSuccess=true;
                                z = "Profile successfully edited!";
                                output.add(returnString);
                            }
                            in.close();




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
                startActivity(intent);

            }
            progressDialog.hide();
        }


    }

    //method to update the patient password

    private class updatePatientPass extends AsyncTask<String,String,String> {

        String patientNewPass = patientNewPassword.getText().toString();
        String patientOldPass = patientOldPassword.getText().toString();


        String z = "";
        boolean isSuccess = false;

        /*String pID;*/

        /*ArrayList<String> ar = new ArrayList<String>();*/

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        /*sql retrieves current patient data using select method and place in corresponding text fields
                user will now have the chance to edit the said text fields
                user will press the update button
                sql update will get the entry from the fields and push the new data to the database
                make an intent that will either go back to the dashboard or stay at the edit profile page*/

        @Override
        protected String doInBackground(String... params) {
            if(patientOldPass.trim().equals("")||patientNewPass.trim().equals(""))
                z = "Please enter your old and new password.";
            else
            {
                try {


                            URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/UpdatePatientPass");
                            URLConnection connection = url.openConnection();

                            connection.setReadTimeout(10000);
                            connection.setConnectTimeout(15000);
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            Uri.Builder builder = new Uri.Builder()
                                    .appendQueryParameter("patientid", PatientID)
                                    .appendQueryParameter("oldPassword", patientOldPass)
                                    .appendQueryParameter("newpass", patientNewPass);
                            String query = builder.build().getEncodedQuery();

                            OutputStream os = connection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(query);
                            writer.flush();
                            writer.close();
                            os.close();

                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String returnString="";
                            ArrayList<String> output=new ArrayList<String>();
                            while ((returnString = in.readLine()) != null)
                            {
                                isSuccess=true;
                                z = "Password successfully edited!";
                                output.add(returnString);
                            }
                            in.close();




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
                startActivity(intent);

            }
            progressDialog.hide();
        }


    }


}