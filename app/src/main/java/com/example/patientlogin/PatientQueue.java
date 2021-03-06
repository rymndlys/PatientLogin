package com.example.patientlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Messenger;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import com.example.patientlogin.dbutility.DBUtility;
import com.example.patientlogin.security.Security;
import com.example.patientlogin.spinner.Downloader;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Queue;

import static java.security.AccessController.getContext;

public class PatientQueue extends AppCompatActivity implements DBUtility {

    ConnectionClass connectionClass;
    ProgressDialog progressDialog;

    private String urlAddressDoctors = "https://isproj2a.benilde.edu.ph/Sympl/doctorSpinnerServlet";
    private String urlAddressDepartments = "https://isproj2a.benilde.edu.ph/Sympl/departmentSpinnerServlet";
    /*private String urlAddressTransaction = "http://192.168.1.2:80/kerux/doctorType.php";*/

    private Spinner spinnerDoc;
    private Spinner spinnerDept;
    /*private Spinner spinnerTransaction;*/

    private Button button_ViewQueue;
    private Button button_queueNow;

    private KeruxSession session;//global variable

    DrawerLayout drawerLayout;

    ImageView docI;
    String firstName;
    String lastName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_queue);

        /*BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_Home:
                        Intent b = new Intent(PatientQueue.this, PatientDashboard.class);
                        startActivity(b);
                        break;
                    case R.id.navigation_Scan:
                        Intent a = new Intent(PatientQueue.this, PatientScanQR.class);
                        startActivity(a);
                        break;
                    case R.id.navigation_Queue:
                        break;
                    case R.id.   navigation_Contact:
                        Intent c = new Intent(PatientQueue.this, PatientReportLoggedIn.class);
                        startActivity(c);
                        break;
                }
                return false;
            }
        });*/
        //--------------------------------------------------------------------------------------------------
        session=new KeruxSession(getApplicationContext());
        docI=findViewById(R.id.imgDoc);
        spinnerDoc = (Spinner)findViewById(R.id.spinnerPrefDoc);
        spinnerDept = (Spinner)findViewById(R.id.spinnerPrefArea);
        /*spinnerTransaction = (Spinner)findViewById(R.id.spinnerTransactionMethod);*/

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        button_queueNow = (Button)findViewById(R.id.buttonQueueNow);
        /*button_ViewQueue = (Button)findViewById(R.id.buttonViewQueue);*/

        button_queueNow.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                QueueNowMethod queueNow = new QueueNowMethod();
                queueNow.execute();
//                insertAudit();
            }
        });

        /*ViewQueueButton();*/

        Downloader doc=new Downloader(PatientQueue.this,urlAddressDoctors,spinnerDoc, "first_name last_name", session.getclinicid());
        doc.execute();
        Downloader dept=new Downloader(PatientQueue.this,urlAddressDepartments,spinnerDept, "name", session.getclinicid());
        dept.execute();
        /*Downloader transact=new Downloader(PatientQueue.this,urlAddressTransaction,spinnerTransaction, "Type");
        transact.execute();*/


        spinnerDoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                spinnerFunc();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        drawerLayout = findViewById(R.id.drawer_layout);


    }


    public void spinnerFunc(){
        String getDoctorValue = (String) spinnerDoc.getSelectedItem().toString();
        String[] words = getDoctorValue.split("\\W+");
        firstName = words[1];
        lastName = words[2];
        Log.d("FIRST", firstName+"[[[[[["+lastName);
        DocPhoto dp = new DocPhoto();
        dp.execute();
    }

    private class DocPhoto extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;
        String path_i;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/GetDocPhoto");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("firstname", firstName)
                        .appendQueryParameter("lastname", lastName);
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
                    path_i=output.get(i);

                }
                in.close();
                Log.d("PHOTO", path_i);


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
                try{
                    Uri pathh=Uri.parse(path_i);
                    Uri paths=Uri.fromFile(new File(path_i));
                    docI.setImageURI(pathh);

                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


    //insert to audit logs
    public void insertAudit(){

        Security sec = new Security();

        try {
            URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/InsertAuditAdminServlet");
            URLConnection connection = url.openConnection();

            connection.setReadTimeout(300000);
            connection.setConnectTimeout(300000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("first", sec.encrypt("Queue"))
                    .appendQueryParameter("second", sec.encrypt("Patient queue"))
                    .appendQueryParameter("third", sec.encrypt("Patient queuing"))
                    .appendQueryParameter("fourth", sec.encrypt("none"))
                    .appendQueryParameter("fifth", sec.encrypt("New Queue ID: " + session.getqueueid() + ", " + "Patient ID: " + session.getpatientid()))
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
        recreate();
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

    //dito mga own classes

    /*public void ViewQueueButton(){

        button_ViewQueue = (Button)findViewById(R.id.buttonViewQueue);

        button_ViewQueue.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openViewQueue();
                    }
                }
        );
    }*/

    /*public void openViewQueue(){
        Intent intent = new Intent(this, PatientViewQueue.class);
        startActivity(intent);
    }*/

    //=====================
    //=====================
    private class QueueNowMethod extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;

        String getDoctorValue = (String)spinnerDoc.getSelectedItem().toString();
        String getDeptValue = (String)spinnerDept.getSelectedItem().toString();
        /*String getQueueType = (String)spinnerTransaction.getSelectedItem().toString();*/
        String isPriority = "";
        Security sec = new Security();

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            if(session.getpatienttype().equals("PWD&Senior")){

                isPriority = "Yes";
            }else{
                isPriority = "No";
            }


            if (getDoctorValue == "" && getDeptValue == "")
                z = "Please enter all fields....";

            else
            {
                try {
                    session.setchosendept(getDeptValue);
                    session.setchosendoc(getDoctorValue.trim());
                    URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/QueuePatientServlet");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    Log.d("QUERY", getDeptValue+"&"+getDoctorValue.trim()+"&"+session.getpatientid()+"&"+session.getpatienttype()+"&"+isPriority);
                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("getdepval", getDeptValue.trim())
                            .appendQueryParameter("getdocval", getDoctorValue.trim())
                            .appendQueryParameter("getpatientid", session.getpatientid())
                            .appendQueryParameter("getpatienttype", session.getpatienttype())
                            .appendQueryParameter("isPriority", isPriority);
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
                        z = "Queueing successful";
                        isSuccess=true;
                        Log.d("returnString", returnString);
                        output.add(returnString);
                    }
                    for (int i = 0; i < output.size(); i++) {
                        String line=output.get(i);
                        String [] words=line.split("\\s\\|\\s");
                        session.setqueueid(words[0]);
                        session.setinstanceid(words[1]);
                        z=words[0]+"||"+words[1];
                    }
                    in.close();

                }
                catch (Exception ex)
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
                Intent intent=new Intent(PatientQueue.this,PatientViewQueue.class);
                // intent.putExtra("name",usernam);
                startActivity(intent);
                Toast.makeText(PatientQueue.this, getDoctorValue, Toast.LENGTH_SHORT).show();
            }
            progressDialog.hide();
        }
    }

    //=====================
/*    private class QueueLaterMethod extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;

        String getDoctorValue = (String)spinnerDoc.getSelectedItem().toString();
        String getDeptValue = (String)spinnerDept.getSelectedItem().toString();
        *//*String getQueueType = (String)spinnerTransaction.getSelectedItem().toString();*//*
        String isPriority = "";
        Security sec = new Security();

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            if(session.getpatienttype().equals("PWD&Senior")){

                isPriority = "Yes";
            }else{
                isPriority = "No";
            }


            if (getDoctorValue == "" && getDeptValue == "")
                z = "Please enter all fields....";

            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        String query=SELECT_QUEUE;

                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setString(1, getDeptValue);
                        ps.setString(2, getDoctorValue.trim());
                        session.setchosendept(getDeptValue);
                        session.setchosendoc(getDoctorValue.trim());


                        ResultSet rs= ps.executeQuery();
                        boolean once=true;
                        while (rs.next()&&once) {
                            once=false;
                            String qID=rs.getString(1);
                            session.setqueueid(qID);
                            String query2=QUEUE_PATIENT;

                            PreparedStatement ps2 = con.prepareStatement(query2);
                            ps2.setString(1, session.getpatientid());
                            ps2.setString(2, qID);
                            ps2.setString(3, session.getpatienttype());
                            ps2.setString(4, "InLine");
                            ps2.setString(5, isPriority);

                            ps2.execute();

                            String query3=SELECT_NEW_INSTANCE; //GETS THE INSTANCE ID OF THE NEW INSTANCE CREATED
                            PreparedStatement ps3 = con.prepareStatement(query3);
                            ResultSet rs1 = ps3.executeQuery();
                            while(rs1.next()){
                                String instanceid=rs1.getString(1);//THIS IS THE INSTANCE ID
                                //WE NEED TO PUT THIS IN A SESSION SO THAT WE CAN VIEW OUR QUEUENUMBER IN THE OTHER PAGE
                                session.setinstanceid(instanceid);
                                String query4=SELECT_COUNT_QUEUELIST;//COUNTS THE NUMBER OF PEOPLE ON THE QUEUE SO WE CAN ASSIGN A QUEUE NUMBER
                                PreparedStatement ps4=con.prepareStatement(query4);
                                ps4.setString(1,qID);
                                ResultSet rs2=ps4.executeQuery();
                                while(rs2.next()){
                                    int count=rs2.getInt(1);
                                    int queuenumber=count+1; //THIS IS THE QUEUE NUMBER
                                    String query5=INSERT_QUEUE_LIST;
                                    PreparedStatement ps5=con.prepareStatement(query5);
                                    ps5.setString(1, qID);
                                    ps5.setString(2, instanceid);
                                    ps5.setInt(3, queuenumber);
                                    ps5.executeQuery(); //WE INSERT DATA IN THE QUEUELIST

                                    String query6=UPDATE_QUEUE_NUMBER;
                                    PreparedStatement ps6=con.prepareStatement(query6);
                                    ps6.setInt(1, queuenumber);
                                    ps6.setString(2, instanceid);
                                    ps6.executeUpdate(); //UPDATE INSTANCE SO IT HAS QUEUENUMBER

                                }


                            }
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
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();
            if(isSuccess) {
                Intent intent=new Intent(PatientQueue.this,PatientSuccess.class);
                // intent.putExtra("name",usernam);
                startActivity(intent);
                Toast.makeText(PatientQueue.this, getDoctorValue, Toast.LENGTH_SHORT).show();
            }
            progressDialog.hide();
        }
    }*/

    public String timeStamp() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        return sdf.format(calendar.getTime());
    }

    public String timeS() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(calendar.getTime());
    }



}
