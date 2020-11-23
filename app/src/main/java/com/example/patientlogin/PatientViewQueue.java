package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientlogin.dbutility.DBUtility;
import com.example.patientlogin.security.Security;

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
        //pqn.notify();
        //currentlyServing.notify();
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

                    URL url = new URL("http://192.168.43.166:8080/RootAdmin/ViewQueuePatientServlet");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(300000);
                    connection.setConnectTimeout(300000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("instanceid", session.getinstanceid());
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

                        output.add(returnString);
                    }
                    for (int i = 0; i < output.size(); i++) {
                        String line=output.get(i);
                        if(i==0){
                            qNumber=line;
                        }
                        if(i==1){
                            cCalling=line;
                        }
                    }
                    in.close();



                    isSuccess=true;
                    z = "Queueing successfull";

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
