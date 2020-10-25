package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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
            }
        });

        session = new KeruxSession(getApplicationContext());

        /*session.getfirstname();
        session.getlastname();
        session.getcontactno();
        session.getemail();
        session.getpatientid();*/
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
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {
                        String query=" insert into error_report (firstname, lastname, description, status, email, contactno) " +
                                "values('"+session.getfirstname()+"', '"+session.getlastname()+"', '"+rMessage+"', 'ongoing', '"+session.getemail()+"', '"+session.getcontactno()+"') ";

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
