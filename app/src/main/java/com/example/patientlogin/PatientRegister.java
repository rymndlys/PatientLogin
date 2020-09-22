package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



public class PatientRegister extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText contact;
    private Spinner type;

    private Button button_signUp;
    private Button button_login;
    private Button button_report;

    ConnectionClass connectionClass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        Spinner mySpinner = (Spinner)findViewById(R.id.patientType);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(PatientRegister.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.patienttypes));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        BackToLogin();
        ReportButton();

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        name = (EditText)findViewById(R.id.editText_patientName);
        email = (EditText)findViewById(R.id.editText_patientEmail);
        password = (EditText)findViewById(R.id.editText_patientPasswordSet);
        contact = (EditText) findViewById(R.id.editText_patientContactNum);
        type = (Spinner)findViewById(R.id.patientType);
        button_signUp = (Button)findViewById(R.id.button_signUp);

        button_login = (Button)findViewById(R.id.buttonLogin);
        button_report = (Button)findViewById(R.id.reportButton);

        contact.setInputType(InputType.TYPE_CLASS_NUMBER);


        button_signUp.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                DoRegister doRegister = new DoRegister();
                doRegister.execute();
            }
        });

    }

    public void BackToLogin(){

        button_login = (Button)findViewById(R.id.buttonLogin);

        button_login.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openLogin();
                    }
                }
        );
    }

    public void openLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

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

    private class DoRegister extends AsyncTask<String,String,String> {

        String pName = name.getText().toString();
        String pEmail = email.getText().toString();
        String pPass = password.getText().toString();
        String pContact = contact.getText().toString();
        String pType = type.getSelectedItem().toString(); //get spinner selected item
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

            if(pType.equals("Regular")){
                pType = "1";
            }else if(pType.equals("PWD")){
                pType = "2";
            }else if(pType.equals("HMO")){
                pType = "3";
            }

            if(pName.trim().equals("")|| pEmail.trim().equals("")|| pPass.trim().equals("")|| pContact.trim().equals("")|| pType.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        String query=" insert into patient (email, password, patienttype_id, name, contactno, status) values('"+pEmail+"', '"+pPass+"', '"+pType+"'," +
                                " '"+pName+"', '"+pContact+"', 'Active')";

                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                        isSuccess=true;
                        z = "Registration successfull";
                    }
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
                Intent intent=new Intent(PatientRegister.this,MainActivity.class);
                // intent.putExtra("name",usernam);
                startActivity(intent);
            }
            progressDialog.hide();
        }
    }

}
