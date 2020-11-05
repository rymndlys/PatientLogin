package com.example.patientlogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientlogin.list.ClinicList;
import com.example.patientlogin.model.Clinic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
    private ListView listView;
    KeruxSession session;


    private Button smsButton;

    DrawerLayout drawerLayout;

    ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        session=new KeruxSession(getApplicationContext());

        drawerLayout = findViewById(R.id.drawer_layout);
        /*EditProfileButton();*/
        listView=(ListView)findViewById(R.id.listView_Clinics);
        connectionClass = new ConnectionClass();
        ClinicList cl =new ClinicList(PatientDashboard.this, listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(PatientDashboard.this, PatientQueue.class);
                Clinic item = (Clinic) listView.getAdapter().getItem(position);
                session.setclinicid(String.valueOf(item.getClinic_id()));
                startActivity(intent);
            }
        });
        //--------------------------------------------------------------------------------------------------

        /*TextView title = (TextView) findViewById(R.id.dateToday);
        title.setText(giveDate());

        TextView patientName = (TextView)findViewById(R.id.patientName);

        patientName.setText("Hello " +getIntent().getStringExtra("NAME"));

        notificationManager = NotificationManagerCompat.from(this);*/


    }

    public void ClickMenu (View view){
        //open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo (View view){
        //Close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //Close drawer layout
        //check condition
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            //When drawer is open
            //Close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickDashboard(View view){
        //Recreate activity
        recreate();
    }

    public void ClickQueueUp(View view){
        //Redirect activity to manage accounts
        redirectActivity(this, PatientQueue.class);
    }

    public void ClickViewCurrentQueue(View view){
        //Redirect activity to enrollment
        redirectActivity(this, PatientViewQueue.class);
    }

    public void ClickScanQR(View view){
        //redirect activity to revoke page
        redirectActivity(this, PatientScanQR.class);
    }
    public void ClickEditProfile(View view){
        //redirect activity to revoke page
        redirectActivity(this, PatientEditProfile.class);
    }
    public void ClickSubmitReport(View view){
        //redirect activity to revoke page
        redirectActivity(this, PatientReport.class);
    }

    public void ClickLogout(View view){
        logout(this);
    }

    public static void logout(final Activity activity) {
        //Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Logout");
        //set message
        builder.setMessage("Are you sure you want to logout?");
        //Positive yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Finish activity
                activity.finishAffinity();
                //exit app
                System.exit(0);
            }
        });

        //negative no button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dismiss dialog
                dialogInterface.dismiss();
            }
        });
        //show dialog
        builder.show();
    }

    public static void redirectActivity(Activity activity, Class aClass) {
        //Initialize intent
        Intent intent = new Intent(activity,aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }
    //--------------------------------------------------------------------------------------------------

    /*public String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        return sdf.format(cal.getTime());*/
    }
    //--------------------------------------------------------------------------------------------------


    /*public void EditProfileButton(){

        buttonEditProfile = (Button)findViewById(R.id.button_editProfile);

        buttonEditProfile.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        openEditProfile();
                    }
                }
        );
    }*/

    /*public void openEditProfile(){
        Intent intent = new Intent(this, PatientEditProfile.class);
        startActivity(intent);
    }*/
    //---------------------------------------------------------------


