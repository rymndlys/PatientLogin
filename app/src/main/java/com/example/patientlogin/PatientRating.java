package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.patientlogin.security.Security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PatientRating extends AppCompatActivity {

    Button buttonRate;
    RatingBar ratingStars;
    Button buttonDashboard;
    ProgressDialog progressDialog;
    private KeruxSession session;

    float myRating = 0; //value of rating to be put in database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_rating);

        buttonRate = findViewById(R.id.button_rating);
        ratingStars = findViewById(R.id.ratingBar);
        buttonDashboard = findViewById(R.id.button_dashboard);
        progressDialog = new ProgressDialog(this);
        session=new KeruxSession(getApplicationContext());

        ratingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                int ratingInt = (int) rating;
                String message = null;

                myRating = ratingBar.getRating();

                switch (ratingInt){
                    case 1:
                        message = "Sorry to hear that, we hope we can do better next time!";
                        break;
                    case 2:
                        message = "Sorry to hear that, we hope we can do better next time!";
                        break;
                    case 3:
                        message = "We always look forward to serving you better! Thank you for using Kerux.";
                        break;
                    case 4:
                        message = "Thank you for your rating! We hope to hear from you again.";
                        break;
                    case 5:
                        message = "Thank you for your rating! We hope to hear from you again.";
                        break;
                }

                Toast.makeText(PatientRating.this, message, Toast.LENGTH_SHORT).show();

            }
        });

        buttonRate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SendRating sendrating =new SendRating();
                sendrating.execute();
            }
        });

        DashboardButton();
    }

    private class SendRating extends AsyncTask<String,String,String> {
        Security sec = new Security();
        String z;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
                try {
                    z="Rating failed to send";
                    URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/GiveRatingServlet");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(300000);
                    connection.setConnectTimeout(300000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("rating", String.valueOf(myRating))
                            .appendQueryParameter("instanceid", session.getinstanceid())
                            .appendQueryParameter("patientid", session.getpatientid());
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
                        z = "Rating sent";
                        output.add(returnString);
                    }

                    in.close();
                }catch (Exception ex)
                {

                    z = "Exceptions"+ex;
                }

            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            progressDialog.hide();

        }


    }

    public void DashboardButton(){

        buttonDashboard.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openDashboard();
                    }
                }
        );
    }

    public void openDashboard(){
        Intent intent = new Intent(this, PatientDashboard.class);
        startActivity(intent);
    }

}