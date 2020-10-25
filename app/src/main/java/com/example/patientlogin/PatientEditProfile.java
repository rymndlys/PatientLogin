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

import static com.example.patientlogin.dbutility.DBUtility.EDIT_PROFILE;
import static com.example.patientlogin.dbutility.DBUtility.UPDATE_PROFILE;

public class PatientEditProfile extends AppCompatActivity {

    private EditText patientFName;
    private EditText patientLName;
    private EditText patientPassword;
    private EditText patientEmail;
    private EditText patientContactNo;
    private String PatientID;
    private Button saveChanges;

    ConnectionClass connectionClass;
    private KeruxSession session;//global variable
    ProgressDialog progressDialog;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_edit_profile);

        connectionClass = new ConnectionClass();

        /*TextView title = (TextView) findViewById(R.id.dateToday);
        title.setText(giveDate());*/

        session = new KeruxSession(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        patientFName = (EditText)findViewById(R.id.editFirstName);
        patientLName = (EditText)findViewById(R.id.editLastName);
        patientPassword = (EditText)findViewById(R.id.editPatientPassword);
        patientEmail = (EditText)findViewById(R.id.editEmailText);
        patientContactNo = (EditText)findViewById(R.id.editPatientContactNo);

        patientFName.setText(session.getfirstname());
        patientLName.setText(session.getlastname());
        patientContactNo.setText(session.getcontactno());
        patientEmail.setText(session.getemail());
        PatientID=session.getpatientid();

        saveChanges = (Button)findViewById(R.id.button_save);

        saveChanges.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                updatePatientInfo updatepinfo=new updatePatientInfo();
                updatepinfo.execute();
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


    /*public String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        return sdf.format(cal.getTime());
    }*/

    //=================================================================================================================

    private class updatePatientInfo extends AsyncTask<String,String,String> {
        String patientFirstName=patientFName.getText().toString();
        String patientLastName=patientLName.getText().toString();
        String patientPass = patientPassword.getText().toString();
        String patientEmai = patientEmail.getText().toString();
        String patientContactN = patientContactNo.getText().toString();

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
            if(patientFirstName.trim().equals("")||patientLastName.trim().equals("")||patientPass.trim().equals(""))
                z = "Please enter values in the Name and Password....";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        try {


                            //String query1 = EDIT_PROFILE; //select
                            String query = UPDATE_PROFILE; //update

                            PreparedStatement ps = con.prepareStatement(query); // ps for query which is edit profile
                            ps.setString(1, patientEmai);
                            ps.setString(2, patientPass);
                            ps.setString(3, patientFirstName);
                            ps.setString(4, patientLastName);
                            ps.setString(5, patientContactN);
                            ps.setString(6, PatientID);

                            //*ps.setString(2, session.getusername());*//*



                            // Statement stmt = con.createStatement();
                            // stmt.executeUpdate(query);
                            ps.executeUpdate(); // rs used by ps which is edit profile
                            isSuccess = true;
                            z = "Login successfull";





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
                // intent.putExtra("name",usernam);
                //*intent.putExtra("NAME", pName);*//*
                startActivity(intent);

            }
            progressDialog.hide();
        }


    }


}