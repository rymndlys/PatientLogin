package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class PatientRating extends AppCompatActivity {

    Button buttonRate;
    RatingBar ratingStars;
    Button buttonDashboard;

    float myRating = 0; //value of rating to be put in database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_rating);

        buttonRate = findViewById(R.id.button_rating);
        ratingStars = findViewById(R.id.ratingBar);
        buttonDashboard = findViewById(R.id.button_dashboard);

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
                Toast.makeText(PatientRating.this, String.valueOf(myRating), Toast.LENGTH_SHORT).show();
            }
        });

        DashboardButton();
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