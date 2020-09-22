package com.example.patientlogin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.ui.AppBarConfiguration;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.patientlogin.App.CHANNEL_1_ID;
import static com.example.patientlogin.App.CHANNEL_2_ID;


public class PatientDashboard extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;

    private Button smsButton;
    private Button buttonEditProfile;

    ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_Home:
                        break;
                    case R.id.navigation_Scan:
                        Intent a = new Intent(PatientDashboard.this, PatientScanQR.class);
                        startActivity(a);
                        break;
                    case R.id.navigation_Queue:
                        Intent b = new Intent(PatientDashboard.this, PatientQueue.class);
                        startActivity(b);
                        break;
                    case R.id.   navigation_Contact:
                        Intent c = new Intent(PatientDashboard.this, PatientReportLoggedIn.class);
                        startActivity(c);
                        break;
                }
                return false;
            }
        });


        EditProfileButton();

        connectionClass = new ConnectionClass();
        //--------------------------------------------------------------------------------------------------

        TextView title = (TextView) findViewById(R.id.dateToday);
        title.setText(giveDate());

        TextView patientName = (TextView)findViewById(R.id.patientName);

        patientName.setText("Hello " +getIntent().getStringExtra("NAME"));

        notificationManager = NotificationManagerCompat.from(this);


    }
    //--------------------------------------------------------------------------------------------------

    public String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        return sdf.format(cal.getTime());
    }
    //--------------------------------------------------------------------------------------------------


    public void EditProfileButton(){

        buttonEditProfile = (Button)findViewById(R.id.button_editProfile);

        buttonEditProfile.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openEditProfile();
                    }
                }
        );
    }

    public void openEditProfile(){
        Intent intent = new Intent(this, PatientEditProfile.class);
        startActivity(intent);
    }
    //---------------------------------------------------------------

}
