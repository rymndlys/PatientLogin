package com.example.patientlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
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
import java.util.Calendar;
import java.util.Queue;

public class PatientQueue extends AppCompatActivity implements DBUtility {

    ConnectionClass connectionClass;
    ProgressDialog progressDialog;

    private String urlAddressDoctors = "http://192.168.1.13:80/kerux/doctorSpinner.php";
    private String urlAddressDepartments = "http://192.168.1.13:80/kerux/departmentSpinner.php";
    private String urlAddressTransaction = "http://192.168.1.13:80/kerux/doctorType.php";

    private Spinner spinnerDoc;
    private Spinner spinnerDept;
    private Spinner spinnerTransaction;

    private Button button_ViewQueue;
    private Button button_queueNow;

    private KeruxSession session;//global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_queue);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
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
        });
        //--------------------------------------------------------------------------------------------------
        session=new KeruxSession(getApplicationContext());

        spinnerDoc = (Spinner)findViewById(R.id.spinnerPrefDoc);
        spinnerDept = (Spinner)findViewById(R.id.spinnerPrefArea);
        spinnerTransaction = (Spinner)findViewById(R.id.spinnerTransactionMethod);

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        button_queueNow = (Button)findViewById(R.id.buttonQueueNow);
        button_ViewQueue = (Button)findViewById(R.id.buttonViewQueue);

        button_queueNow.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                QueueNowMethod queueNow = new QueueNowMethod();
                queueNow.execute();
            }
        });

        ViewQueueButton();

        Downloader doc=new Downloader(PatientQueue.this,urlAddressDoctors,spinnerDoc, "Name");
        doc.execute();
        Downloader dept=new Downloader(PatientQueue.this,urlAddressDepartments,spinnerDept, "Name");
        dept.execute();
        Downloader transact=new Downloader(PatientQueue.this,urlAddressTransaction,spinnerTransaction, "Type");
        transact.execute();



    }

    //dito mga own classes

    public void ViewQueueButton(){

        button_ViewQueue = (Button)findViewById(R.id.buttonViewQueue);

        button_ViewQueue.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openViewQueue();
                    }
                }
        );
    }

    public void openViewQueue(){
        Intent intent = new Intent(this, PatientViewQueue.class);
        startActivity(intent);
    }

    //=====================
    //=====================
    private class QueueNowMethod extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;

        String getDoctorValue = (String)spinnerDoc.getSelectedItem().toString();
        String getDeptValue = (String)spinnerDept.getSelectedItem().toString();
        String getQueueType = (String)spinnerTransaction.getSelectedItem().toString();
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

            if(getQueueType.equals("PWD&Senior")){

                isPriority = "Yes";
            }else{
                isPriority = "No";
            }


            if (getDoctorValue == "" && getDeptValue == "" && getQueueType == "")
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
                        ps.setString(2, getDoctorValue);

                        ResultSet rs= ps.executeQuery();

                        while (rs.next()) {
                            String qID=rs.getString(1);
                            String query2=QUEUE_PATIENT;

                            PreparedStatement ps2 = con.prepareStatement(query2);
                            ps2.setString(1, session.getpatientid());
                            ps2.setString(2, qID);
                            ps2.setString(3, getQueueType);
                            ps2.setString(4, "InLine");
                            ps2.setString(5, isPriority);

                            ps2.execute();
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
                Intent intent=new Intent(PatientQueue.this,PatientQueue.class);
                // intent.putExtra("name",usernam);
                startActivity(intent);
                Toast.makeText(PatientQueue.this, getDoctorValue, Toast.LENGTH_SHORT).show();
            }
            progressDialog.hide();
        }
    }

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


    //---------------------------------
    // get selected radio button from radioGroup
    /*int selectedTransaction = transactionTypes.getCheckedRadioButtonId();

    // find the radiobutton by returned id
    String transact = ((RadioButton) findViewById(selectedTransaction)).getText().toString();

    //--------------------------------------------------

    // get selected radio button from radioGroup
    int selectedArea = preferredArea.getCheckedRadioButtonId();

    // find the radiobutton by returned id
    String area = ((RadioButton) findViewById(selectedArea)).getText().toString();

    //--------------------------------------------------

    // get selected radio button from radioGroup
    int selectedDoc = preferredDoctor.getCheckedRadioButtonId();

    // find the radiobutton by returned id
    String doc = ((RadioButton) findViewById(selectedDoc)).getText().toString();

                                Toast.makeText(PatientQueue.this, "Queue Now " + transact + " " + area + " " + doc, Toast.LENGTH_SHORT).show();

*/
}
