package com.example.patientlogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.patientlogin.security.Security;
import com.example.patientlogin.security.SecurityWEB;
/*import com.nexmo.client.NexmoClient;*/
/*import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.SmsSubmissionResponseMessage;
import com.nexmo.client.sms.messages.TextMessage;*/

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.aaro.gustav.passwordstrengthmeter.PasswordStrengthMeter;

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

    private TextView terms;
    private TextView termsBody;
    final Context context = this;

    ConnectionClass connectionClass;
    ProgressDialog progressDialog;
    KeruxSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);
        session = new KeruxSession(getApplicationContext());

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
        terms = findViewById(R.id.txtTerms);
        termsBody = findViewById(R.id.txtBody);

        button_login = (Button)findViewById(R.id.buttonLogin);


        contact.setInputType(InputType.TYPE_CLASS_NUMBER);


        button_signUp.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                if (!validateFName() || !validateLName() || !validateEmail() || !validatePassword()) {
                    confirmInput();
                } else {
                    // get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.activity_terms_and_conditions, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    terms = promptsView.findViewById(R.id.txtTerms);
                    termsBody = promptsView.findViewById(R.id.txtBody);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            DoRegister doRegister = new DoRegister();
                                            doRegister.execute();
                                            insertAudit();
                                        }
                                    })
                            .setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                            Toast.makeText(getBaseContext(),"You must accept Terms of User to register successfully",Toast.LENGTH_LONG).show();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            }
        });

        PasswordStrengthMeter meter = findViewById(R.id.passwordInputMeter);
        meter.setEditText(password);
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
                    .appendQueryParameter("first", sec.encrypt("Signup"))
                    .appendQueryParameter("second", sec.encrypt("Patient signup"))
                    .appendQueryParameter("third", sec.encrypt("Patient signing up in to the application"))
                    .appendQueryParameter("fourth", sec.encrypt("none"))
                    .appendQueryParameter("fifth", sec.encrypt("New Patient Record: " + session.getpatientid()))
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

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*?[A-Z])" +      //any letter
                    "(?=.*?[a-z])" +    //at least 1 special character
                    "(?=.*?[0-9])" +           //no white spaces
                    "(?=.*?[#?!@$%^&*-])"+
                    ".{8,}"+
                    "$");

    private boolean validatePassword() {
        String patientPw = password.getText().toString().trim();
        if (patientPw.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(patientPw).matches()) {
            password.setError("Password must be 8 characters, with 1 uppercase, 1 digit and 1 special character");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String patientEmail = email.getText().toString().trim();

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = patientEmail;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (patientEmail.isEmpty()) {
            email.setError("Field can't be empty");
            return false;
        } else if (!matcher.matches()) {
            email.setError("Please enter a valid email address");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validateFName() {
        String firstname = fname.getText().toString().trim();

        if(firstname.isEmpty()){
            fname.setError("Field can't be empty");
            return false;
        } else if (firstname.length() < 3){
            fname.setError("First Name too short");
            return false;
        } else if(firstname.matches("^[0-9]+$")){
            fname.setError("Last name cannot contain number values");
            return false;
        }  else {
            fname.setError(null);
            return true;
        }
    }

    private boolean validateLName() {
        String lastname = lname.getText().toString().trim();

        if(lastname.isEmpty()){
            lname.setError("Field can't be empty");
            return false;
        } else if (lastname.length() < 2){
            lname.setError("Last Name too short");
            return false;
        }else if(lastname.matches("^[0-9]+$")){
            lname.setError("Last name cannot contain number values");
            return false;
        }  else {
            lname.setError(null);
            return true;
        }
    }

    public boolean confirmInput() {
        String input = "Email: " + email.getText().toString();
        input += "\n";
        input += "First Name: " + fname.getText().toString();
        input += "\n";
        input += "Last Name: " + lname.getText().toString();
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
        return false;
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

        Security sec = new Security();
        SecurityWEB secw = new SecurityWEB();

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
                    URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/RegisterPatientServlet");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(300000);
                    connection.setConnectTimeout(300000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("pContact", sec.encrypt(pContact).trim())
                            .appendQueryParameter("pEmail", secw.encrypt(pEmail).trim())
                            .appendQueryParameter("pPass", sec.encrypt(pPass).trim())
                            .appendQueryParameter("pType", pType)
                            .appendQueryParameter("fName", fName)
                            .appendQueryParameter("lName", lName);
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

                    //sms
/*
                    NexmoClient client = new NexmoClient.Builder()
                            .apiKey("4ba61fad")
                            .apiSecret("i7WOVGsry1eM4p2F")
                            .build();

                    String messageText = "Hello from Vonage SMS API";
                    TextMessage message = new TextMessage("Vonage APIs", "639178216313", messageText);

                    SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

                    for (SmsSubmissionResponseMessage responseMessage : response.getMessages()) {
                        System.out.println(responseMessage);
                    }*/

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
