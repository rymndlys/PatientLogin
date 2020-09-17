package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientlogin.dbutility.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DBUtility {

    private EditText username;
    private EditText password;
    private TextView attempt;
    private Button button_login;
    private Button button_guest;
    private Button button_register;
    private Button button_report;

    int attempt_counter = 5;

    ConnectionClass connectionClass;
    ProgressDialog progressDialog;


    private KeruxSession session;//global variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*LoginButton();*/
        GuestButton();
        RegisterButton();
        ReportButton();

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        username = (EditText)findViewById(R.id.editText_username);
        password = (EditText)findViewById(R.id.editText_password);
        attempt = (TextView) findViewById(R.id.attempt_count);
        button_login = (Button)findViewById(R.id.button_login);

        session = new KeruxSession(getApplicationContext());



        button_login.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                Dologin dologin=new Dologin();
                dologin.execute();
            }
        });

    }

    private class Dologin extends AsyncTask<String,String,String> {

        String usernam = username.getText().toString();
        String passstr = password.getText().toString();
        String nameofuser="";
        String patientid="";
        String z = "";
        boolean isSuccess = false;

        ArrayList <String> ar = new ArrayList<String>();

        String pID, pName,cn,pass;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(usernam.trim().equals("")||passstr.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        try {


                            String query = LOGIN_PATIENT;

                            PreparedStatement ps = con.prepareStatement(query);
                            ps.setString(1, usernam);
                            ps.setString(2, passstr);

                            /*Statement stmt = con.createStatement();*/
                            // stmt.executeUpdate(query);
                            ResultSet rs = ps.executeQuery();

                            while (rs.next()) {
                                pID = rs.getString(1);
                                cn = rs.getString(2);
                                pass = rs.getString(3);
                                pName = rs.getString(4);

                                ar.add(pName);

                                if (cn.equals(usernam) && pass.equals(passstr)) {
                                    session.setcontactno(cn);
                                    session.setpassword(pass);
                                    session.setusername(pName);
                                    session.setpatientid(pID);
                                    isSuccess = true;
                                    z = "Login successfull";
                                } else
                                    isSuccess = false;

                            /*attempt_counter--;
                            attempt.setText(Integer.toString(attempt_counter));

                            if(attempt_counter == 0)
                                button_login.setEnabled(false);*/
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
                Intent intent=new Intent(MainActivity.this,PatientDashboard.class);
                // intent.putExtra("name",usernam);
                intent.putExtra("NAME", pName);
                startActivity(intent);

            }
            progressDialog.hide();
        }


    }


    //---------------------------------------------------------------
   /* public void openDashboard(){
        Intent intent = new Intent(this, PatientRegister.class);
        startActivity(intent);
    }*/
    //---------------------------------------------------------------
    public void GuestButton(){

        button_guest = (Button)findViewById(R.id.signGuest);

        button_guest.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openAsGuest();
                    }
                }
        );
    }

    public void openAsGuest(){
        Intent intent = new Intent(this, PatientDashboard.class);
        startActivity(intent);
    }
    //---------------------------------------------------------------
    public void RegisterButton(){

        button_register = (Button)findViewById(R.id.signUp);

        button_register.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openRegister();
                    }
                }
        );
    }

    public void openRegister(){
        Intent intent = new Intent(this, PatientRegister.class);
        startActivity(intent);
    }
    //---------------------------------------------------------------
    public void ReportButton(){
        button_report = (Button)findViewById(R.id.reportButton);

        button_report.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openReport();
                    }
                }
        );

    }

    public void openReport(){
        Intent intent = new Intent(this, PatientReport.class);
        startActivity(intent);
    }
    //---------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}