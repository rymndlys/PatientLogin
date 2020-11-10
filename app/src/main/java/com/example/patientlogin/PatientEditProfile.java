package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

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
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        try {


                            //method to update the basic information of the patient (w/o pass)
                            String query = UPDATE_PROFILE; //update

                            PreparedStatement ps = con.prepareStatement(query); // ps for query which is edit profile
                            ps.setString(1, patientEmai);
                            ps.setString(2, patientFirstName);
                            ps.setString(3, patientLastName);
                            ps.setString(4, patientContactN);
                            ps.setString(5, PatientID);


                            // Statement stmt = con.createStatement();
                            // stmt.executeUpdate(query);
                            ps.executeUpdate(); // rs used by ps which is edit profile
                            isSuccess = true;
                            z = "Profile successfully edited!";





                        } catch (Exception e) {
                            isSuccess = false;
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
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        try {

                            String query = CONFIRM_PATIENT_PASS;

                            PreparedStatement ps = con.prepareStatement(query);
                            ps.setString(1, patientOldPass);

                            ResultSet rs = ps.executeQuery();

                            while (rs.next()) {
                                String oldPassword = rs.getString(1);
                                if(oldPassword.equals(patientOldPass)){
                                    String query1 = UPDATE_PROFILE_PASS;

                                    PreparedStatement ps1 = con.prepareStatement(query1);
                                    ps1.setString(1, patientNewPass);

                                    ps1.executeUpdate();

                                }

                            }

                            ps.executeUpdate(); // rs used by ps which is edit profile
                            isSuccess = true;
                            z = "Password successfully edited!";


                        } catch (Exception e) {
                            isSuccess = false;
                            Thread.dumpStack(); //always put this from sir mon
                        }
                    }
                }catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;
        }

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