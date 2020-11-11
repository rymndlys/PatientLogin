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
import android.widget.Toast;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.patientlogin.dbutility.DBUtility.CHECK_PATIENT;
import static com.example.patientlogin.dbutility.DBUtility.REGISTER_PATIENT;


public class PatientRegister extends AppCompatActivity {

    private EditText fname;
    private EditText lname;
    private EditText email;
    private EditText password;
    private EditText contact;
    private Spinner type;

    private Button button_signUp;
    private Button button_login;


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

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        fname = (EditText)findViewById(R.id.editText_patientFName);
        lname = (EditText)findViewById(R.id.editText_patientLName);
        email = (EditText)findViewById(R.id.editText_patientEmail);
        password = (EditText)findViewById(R.id.editText_patientPasswordSet);
        contact = (EditText) findViewById(R.id.editText_patientContactNum);
        type = (Spinner)findViewById(R.id.patientType);
        button_signUp = (Button)findViewById(R.id.button_signUp);

        button_login = (Button)findViewById(R.id.buttonLogin);


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


    private class DoRegister extends AsyncTask<String,String,String> {

        String fName = fname.getText().toString();
        String lName = lname.getText().toString();
        String pEmail = email.getText().toString();
        String pPass = password.getText().toString();
        String pContact = contact.getText().toString();
        String pType = type.getSelectedItem().toString(); //get spinner selected item
        String z = "";
        boolean isSuccess = false;

        String checkNum, checkEmail;


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

            if(fName.trim().equals("")|| lName.trim().equals("")|| pEmail.trim().equals("")|| pPass.trim().equals("")|| pContact.trim().equals("")|| pType.trim().equals(""))
                z = "Please enter all fields.";
            else
            {
                try {



                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {

                        String query = REGISTER_PATIENT;

                        /*boolean check=true;*/

                        String query1 = CHECK_PATIENT;

                        PreparedStatement ps1 = con.prepareStatement(query1);
                        ps1.setString(1, pContact);
                        ps1.setString(2, pEmail);

                        ResultSet rs = ps1.executeQuery();

                            if(rs.next()) {

                                /*checkNum = rs.getString(1);
                                checkEmail = rs.getString(2);

                                *//*System.out.println(checkNum + checkEmail);*//*

                                if(checkNum.equals(pContact) || checkEmail.equals(pEmail)){
                                    z = "Account already exists.";
                                }else{

                                }*/

                                /*check=false;*/

                                z = "Account already exists.";

                            }
                            else {
                                PreparedStatement ps = con.prepareStatement(query);
                                ps.setString(1, pEmail);
                                ps.setString(2, pPass);
                                ps.setString(3, pType);
                                ps.setString(4, fName);
                                ps.setString(5, lName);
                                ps.setString(6, pContact);

                                ps.executeUpdate();

                                isSuccess = true;
                                z = "Registration successfull";
                            }


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
