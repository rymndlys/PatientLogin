package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.patientlogin.security.Security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class PatientReport extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText contactNum;
    private EditText message;
    private Button button_report;

    ConnectionClass connectionClass;
    private KeruxSession session;//global variable
    ProgressDialog progressDialog;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_report);

        contactNum = new EditText(this);
        contactNum.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        drawerLayout = findViewById(R.id.drawer_layout);

        /*name = findViewById(R.id.editText_name);
        email = findViewById(R.id.editText_email);
        contactNum = findViewById(R.id.editText_contactNum);*/
        message = findViewById(R.id.editText_message);
        button_report = findViewById(R.id.button_report);

        button_report.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                DoReport doreport = new DoReport();
                doreport.execute();
                insertAudit();
            }
        });

        session = new KeruxSession(getApplicationContext());

        /*session.getfirstname();
        session.getlastname();
        session.getcontactno();
        session.getemail();
        session.getpatientid();*/
    }

    //insert to audit logs
    public void insertAudit(){

        Security sec = new Security();

        try {
            URL url = new URL("http://192.168.1.22:8080/RootAdmin/InsertAuditAdminServlet");
            URLConnection connection = url.openConnection();

            connection.setReadTimeout(300000);
            connection.setConnectTimeout(300000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("first", sec.encrypt("Reports"))
                    .appendQueryParameter("second", sec.encrypt("Patient reports"))
                    .appendQueryParameter("third", sec.encrypt("Patient submitting a report"))
                    .appendQueryParameter("fourth", sec.encrypt("none"))
                    .appendQueryParameter("fifth", sec.encrypt("Patient ID: " + session.getpatientid()))
                    .appendQueryParameter("sixth", session.getpatientid());
            String query = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String returnString="";
            ArrayList<String> output=new ArrayList<String>();
            while ((returnString = in.readLine()) != null)
            {
                Log.d("returnString", returnString);
                output.add(returnString);
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
        //redirect
        PatientDashboard.redirectActivity(this, PatientEditProfile.class);
    }

    public void ClickSubmitReport(View view){
        //recreate activity
        recreate();
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

    private class DoReport extends AsyncTask<String,String,String> {

        /*String rName = name.getText().toString();
        String rEmail = email.getText().toString();
        String rContact = contactNum.getText().toString();*/
        String rMessage = message.getText().toString();
        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(rMessage.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {
                        URL url = new URL("http://192.168.1.22:8080/RootAdmin/ErrorReportPatientServlet");
                        URLConnection connection = url.openConnection();

                        connection.setReadTimeout(10000);
                        connection.setConnectTimeout(15000);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);

                        Uri.Builder builder = new Uri.Builder()
                                .appendQueryParameter("firstname", session.getfirstname())
                                .appendQueryParameter("lastname", session.getlastname())
                                .appendQueryParameter("message", rMessage)
                                .appendQueryParameter("email", session.getemail())
                                .appendQueryParameter("contactno", session.getcontactno());
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
                            z = returnString;
                            output.add(returnString);
                        }
                        in.close();


                        goBack();

                                isSuccess=true;
                                z = "Your response has been submitted.";


                }
                catch (Exception ex)
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
                Intent intent=new Intent(PatientReport.this,PatientDashboard.class);
                // intent.putExtra("name",usernam);
                startActivity(intent);
            }
            progressDialog.hide();
        }
    }

public void goBack(){
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
}

}
