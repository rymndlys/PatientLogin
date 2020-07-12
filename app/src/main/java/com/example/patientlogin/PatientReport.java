package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PatientReport extends AppCompatActivity {

    private static EditText name;
    private static EditText email;
    private static EditText contactNum;
    private static EditText message;
    private static Button button_report;

    ConnectionClass connectionClass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_report);

        contactNum = new EditText(this);
        contactNum.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        connectionClass = new ConnectionClass();
        progressDialog = new ProgressDialog(this);

        name = findViewById(R.id.editText_name);
        email = findViewById(R.id.editText_email);
        contactNum = findViewById(R.id.editText_contactNum);
        message = findViewById(R.id.editText_message);
        button_report = findViewById(R.id.button_report);

        button_report.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                DoReport doreport = new DoReport();
                doreport.execute();
            }
        });
    }

    private class DoReport extends AsyncTask<String,String,String> {

        String rName = name.getText().toString();
        String rEmail = email.getText().toString();
        String rContact = contactNum.getText().toString();
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
            if(rName.trim().equals("") || rEmail.trim().equals("") || rContact.trim().equals("") || rMessage.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        String query=" insert into error_report (name, description, status, email, contactno) " +
                                "values('"+rName+"', '"+rMessage+"', 'ongoing', '"+rEmail+"', '"+rContact+"') ";

                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);

                        goBack();

                                isSuccess=true;
                                z = "Your response has been submitted.";

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
                Intent intent=new Intent(PatientReport.this,MainActivity.class);
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
