package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientlogin.dbutility.DBUtility;
import com.example.patientlogin.security.Security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PatientViewQueue extends AppCompatActivity implements DBUtility {

    private TextView queueNumber;
    private TextView currentlyServing;
    private TextView currentTime;
    private TextView currentDate;
    private TextView currentDepartment;
    private TextView currentDoctor;

    ConnectionClass connectionClass;
    private KeruxSession session;//global variable
    ProgressDialog progressDialog;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_queue);

        connectionClass = new ConnectionClass();

        session = new KeruxSession(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        currentDate = (TextView) findViewById(R.id.txtDate);
        currentDate.setText(giveDate());

        drawerLayout = findViewById(R.id.drawer_layout);

        queueNumber=(TextView)findViewById(R.id.txtPatientNumber);
        currentlyServing=(TextView)findViewById(R.id.txtCurrentlyServing);
        currentDepartment=(TextView)findViewById(R.id.txtChosenDepartment);
        currentDoctor=(TextView)findViewById(R.id.txtChosenDoctor);

        PatientQueueNumber pqn = new PatientQueueNumber();
        pqn.execute();
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
        //recreate activity
        recreate();
    }

    public void ClickScanQR(View view){
        //Redirect
        PatientDashboard.redirectActivity(this, PatientViewQueue.class);
    }

    public void ClickEditProfile(View view){
        //redirect
        PatientDashboard.redirectActivity(this, PatientEditProfile.class);
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

    public String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        return sdf.format(cal.getTime());
    }

    private class PatientQueueNumber extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;
        String qNumber, cCalling;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Please check your internet connection";
                } else {
                    String query=SELECT_QUEUENUMBER;

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, session.getinstanceid());


                    ResultSet rs= ps.executeQuery();
                    while (rs.next()) {
                        qNumber=rs.getString(1);

                    }

                    String query2=SELECT_CURRENTLY_CALLING;
                    PreparedStatement ps2 = con.prepareStatement(query2);
                    ps2.setString(1, session.getinstanceid());


                    ResultSet rs2= ps2.executeQuery();
                    while (rs2.next()) {
                        cCalling=rs2.getString(1);

                    }


                    isSuccess=true;
                    z = "Queueing successfull";
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions"+ex;
            }

            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Message", z);
            if(isSuccess) {
                queueNumber.setText(qNumber);
                currentlyServing.setText(cCalling);
                currentDepartment.setText(session.getchosendept());
                currentDoctor.setText(session.getchosendoc());
            }
            progressDialog.hide();
        }
    }

}
