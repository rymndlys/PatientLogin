package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PatientSuccess extends AppCompatActivity {

    private Button button_viewqueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_success);

        button_viewqueue = (Button)findViewById(R.id.button_viewqueue);

        ViewQueue();

    }

    public void ViewQueue(){

        button_viewqueue = (Button)findViewById(R.id.signUp);

        button_viewqueue.setOnClickListener(
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
}