package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.nio.charset.StandardCharsets;
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
    private Security sec;

    int attempt_counter = 5;

    ConnectionClass connectionClass;
    ProgressDialog progressDialog;


    private KeruxSession session;//global variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GuestButton();

        RegisterButton();

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        username = (EditText)findViewById(R.id.editText_username);

        username.setInputType(InputType.TYPE_CLASS_NUMBER);

        password = (EditText)findViewById(R.id.editText_password);
        /*attempt = (TextView) findViewById(R.id.attempt_count);*/
        button_login = (Button)findViewById(R.id.button_login);

        session = new KeruxSession(getApplicationContext());

        button_login.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                Dologin dologin=new Dologin();
                dologin.execute();
               // insertAudit();
            }
        });

    }

    //insert to audit logs
    public void insertAudit(){

        Security sec = new Security();

        try {
            URL url = new URL("http://192.168.1.22:8080/RootAdmin/InsertAuditAdminServlet");
            URLConnection connection = url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("first", sec.encrypt("Login"))
                    .appendQueryParameter("second", sec.encrypt("Patient login"))
                    .appendQueryParameter("third", sec.encrypt("Patient logging in to the application"))
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

    private class Dologin extends AsyncTask<String,String,String> {
        Security sec = new Security();
        String usernam = username.getText().toString();
        String passstr = password.getText().toString();
        String nameofuser="";
        String patientid="";
        String z = "";
        boolean isSuccess = false;

        String pID, fName, lName, cn,pass, email, pType;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(usernam.trim().equals("")||passstr.trim().equals(""))
                z = "Please enter all fields...."; //add validation if acct does not exist
            else
            {
                try {
                    z="Login failed";
                    URL url = new URL("http://192.168.1.22:8080/RootAdmin/LoginPatientServlet");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(300000);
                    connection.setConnectTimeout(300000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("username", sec.encrypt(usernam).trim())
                            .appendQueryParameter("password", sec.encrypt(passstr).trim());
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
                        z = "Logged in successfully!";
                        output.add(returnString);
                    }
                    for (int i = 0; i < output.size(); i++) {
                        if(i==0){
                            pID=output.get(i);
                        }
                        else if(i==1){
                            cn=output.get(i);
                        }
                        else if(i==2){
                            pass=output.get(i);
                        }
                        else if(i==3){
                            email=output.get(i);
                        }
                        else if(i==4){
                            fName=output.get(i);
                        }
                        else if(i==5){
                            lName=output.get(i);
                        }
                        else if(i==6){
                            pType=output.get(i);
                        }
                    }
                    in.close();
                    Log.d("DATA", pID+";;"+cn+";;"+pass+";;"+email+";;"+fName+";;"+lName+";;"+pType);
                }catch (Exception ex)
                {
                    z="Login failed";
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();
            if(isSuccess) {
                try {
                    session.setcontactno(sec.decrypt(cn));
                    session.setpassword(sec.decrypt(pass));
                    session.setemail(sec.decrypt(email));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                session.setfirstname(fName);
                session.setlastname(lName);
                session.setpatientid(pID);
                session.setpatienttype(pType);
                Intent intent=new Intent(MainActivity.this,PatientDashboard.class);
                // intent.putExtra("name",usernam);
                intent.putExtra("NAME", fName + " " + lName);
                startActivity(intent);


            }
            progressDialog.hide();

        }


    }
    // for numbers only in contact input




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

                        GuestLogin gl=new GuestLogin();
                        gl.execute();
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

    private class GuestLogin extends AsyncTask<String,String,String> {


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

            try {
                URL url = new URL("http://192.168.1.22:8080/RootAdmin/RegisterPatientServlet");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setDoInput(true);
                connection.setDoOutput(true);


                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String returnString = "";
                ArrayList<String> output = new ArrayList<String>();
                while ((returnString = in.readLine()) != null) {
                    isSuccess = true;
                    z = "Logged in as guest";
                    session.setguestid(returnString);
                    output.add(returnString);
                }
                in.close();
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions" + ex;
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();
            if(isSuccess) {
                openAsGuest();
            }
            progressDialog.hide();
        }
    }

}