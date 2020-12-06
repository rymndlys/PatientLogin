package com.example.patientlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientlogin.dbutility.DBUtility;
import com.example.patientlogin.security.Security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/*import static com.example.patientlogin.App.CHANNEL_2_ID;*/

public class PatientViewQueue extends AppCompatActivity implements DBUtility {

    private TextView queueNumber;
    private TextView currentlyServing;
    private TextView currentTime;
    private TextView currentDate;
    private TextView currentDepartment;
    private TextView currentDoctor;

    private NotificationManagerCompat notificationManager;
    ConnectionClass connectionClass;
    private KeruxSession session;//global variable
    ProgressDialog progressDialog;
    boolean cancelledB;
    boolean noshowB;
    boolean calledB;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_queue);

        notificationManager = NotificationManagerCompat.from(this);

        connectionClass = new ConnectionClass();

        session = new KeruxSession(getApplicationContext());

        progressDialog = new ProgressDialog(this);
        cancelledB=false;
        noshowB=false;
        calledB=false;

        currentDate = (TextView) findViewById(R.id.txtDate);
        currentDate.setText(giveDate());

        drawerLayout = findViewById(R.id.drawer_layout);

        queueNumber=(TextView)findViewById(R.id.txtPatientNumber);
        currentlyServing=(TextView)findViewById(R.id.txtCurrentlyServing);
        currentDepartment=(TextView)findViewById(R.id.txtChosenDepartment);
        currentDoctor=(TextView)findViewById(R.id.txtChosenDoctor);

        PatientQueueNumber pqn = new PatientQueueNumber();
        pqn.execute();

        /*if(currentlyServing.getText().equals(queueNumber.getText())){
            Toast.makeText(getBaseContext(),"You can now rate your queue experience.",Toast.LENGTH_LONG).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(PatientViewQueue.this);
            builder.setMessage("Your number is now being called! Please proceed to your doctor. Do you want to rate overall experience?")
                    .setCancelable(false)
                    .setPositiveButton("Go to Rating Page", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(PatientViewQueue.this, PatientRating.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Go back to Dashboard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(PatientViewQueue.this, PatientDashboard.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            notificationCalling();
        }
*/
        PatientCalling calling = new PatientCalling();
        calling.execute();

        PatientNoShow noShow = new PatientNoShow();
        noShow.execute();

        PatientCancelled cancelled = new PatientCancelled();
        cancelled.execute();

      /*  if(cancelled.isSuccess){
            Toast.makeText(getBaseContext(),"CANCELLED",Toast.LENGTH_LONG).show();
            notificationCancelled();
        }
        if(noShow.isSuccess){
            Toast.makeText(getBaseContext(),"NO SHOW",Toast.LENGTH_LONG).show();
            notificationNoShow();
        }*/
       /* if(calling.isSuccess){
            notificationCalling();
            Toast.makeText(getBaseContext(),"You can now rate your queue experience.",Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(PatientViewQueue.this);
            builder.setMessage("Your number is now being called! Please proceed to your doctor. Do you want to rate overall experience?")
                    .setCancelable(false)
                    .setPositiveButton("Go to Rating Page", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(PatientViewQueue.this, PatientRating.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Go back to Dashboard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(PatientViewQueue.this, PatientDashboard.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }*/

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
        //recreate activity
        recreate();
    }

    public void ClickScanQR(View view){
        //Redirect
        PatientDashboard.redirectActivity(this, PatientViewQueue.class);
    }

    public void ClickEditProfile(View view){
        //redirect
        PatientDashboard.redirectActivity(this, PatientEditProfile.class);
    }

    public void ClickSubmitReport(View view){
        //redirect
        PatientDashboard.redirectActivity(this, PatientReport.class);
    }

    public void notificationCalling(){

        String title = "Kerux Queue Updates";
        String message = "Calling now";

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.baidu.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(PatientViewQueue.this,0,intent,0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.keruxlogo);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= 26)
        {
            //When sdk version is larger than26
            String id = "channel_1";
            String description = "143";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
            /*channel.enableLights(true);
            channel.enableVibration(true);*/
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(PatientViewQueue.this, id)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(largeIcon)
                    .setGroup("Kerux Updates")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            manager.notify(1, notification);
        }
        else
        {
            //When sdk version is less than26
            Notification notification = new Notification.Builder(PatientViewQueue.this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            manager.notify(1,notification);
        }
    }

    public void notificationCancelled(){

        String title = "Kerux Queue Updates";
        String message = "Your queue has been cancelled";

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.baidu.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(PatientViewQueue.this,0,intent,0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.keruxlogo);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= 26)
        {
            //When sdk version is larger than26
            String id = "channel_1";
            String description = "143";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
            /*channel.enableLights(true);
            channel.enableVibration(true);*/
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(PatientViewQueue.this, id)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(largeIcon)
                    .setGroup("Kerux Updates")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            manager.notify(1, notification);
        }
        else
        {
            //When sdk version is less than26
            Notification notification = new Notification.Builder(PatientViewQueue.this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            manager.notify(1,notification);
        }
    }

    public void notificationNoShow(){

        String title = "Kerux Queue Updates";
        String message = "You have been marked as no show";

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.baidu.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(PatientViewQueue.this,0,intent,0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.keruxlogo);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= 26)
        {
            //When sdk version is larger than26
            String id = "channel_1";
            String description = "143";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
            /*channel.enableLights(true);
            channel.enableVibration(true);*/
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(PatientViewQueue.this, id)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(largeIcon)
                    .setGroup("Kerux Updates")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            manager.notify(1, notification);
        }
        else
        {
            //When sdk version is less than26
            Notification notification = new Notification.Builder(PatientViewQueue.this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            manager.notify(1,notification);
        }
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

    public String giveDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        return sdf.format(cal.getTime());
    }

    private class PatientQueueNumber extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;
        String qNumber, cCalling;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/ViewQueuePatientServlet");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("instanceid", session.getinstanceid())
                        .appendQueryParameter("queueid", session.getqueueid());
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

                    output.add(returnString);
                }
                for (int i = 0; i < output.size(); i++) {
                    String line=output.get(i);
                    if(i==0){
                        qNumber=line;
                    }
                    if(i==1){
                        cCalling=line;
                    }
                }
                in.close();



                isSuccess=true;
                z = "Queueing successfull";

            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions"+ex;
            }

            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Message", z);
            if(isSuccess) {
                queueNumber.setText(qNumber);
                currentlyServing.setText(cCalling);
                currentDepartment.setText(session.getchosendept());
                currentDoctor.setText(session.getchosendoc());
            }
            progressDialog.hide();
        }
    }

    private class PatientCalling extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;
        String qNumber, cCalling;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/CheckInstanceQueue");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
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

                    output.add(returnString);
                }
                for (int i = 0; i < output.size(); i++) {
                    String line=output.get(i);
                    if(line.equals("true")){
                        isSuccess=true;
                        calledB=true;
                    }
                }
                in.close();


                z = "Calling";

            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions"+ex;
            }

            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Message", z);
            if(isSuccess) {
                Toast.makeText(getBaseContext(),"You can now rate your queue experience.",Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(PatientViewQueue.this);
                builder.setMessage("Your number is now being called! Please proceed to your doctor. Do you want to rate overall experience?")
                        .setCancelable(false)
                        .setPositiveButton("Go to Rating Page", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(PatientViewQueue.this, PatientRating.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Go back to Dashboard", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(PatientViewQueue.this, PatientDashboard.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                notificationCalling();
            }
            progressDialog.hide();
        }
    }

    private class PatientNoShow extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;
        String qNumber, cCalling;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/CheckInstanceNoShow");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
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

                    output.add(returnString);
                }
                for (int i = 0; i < output.size(); i++) {
                    String line=output.get(i);
                    if(line.equals("true")){
                        isSuccess=true;
                        noshowB=true;
                    }
                }
                in.close();


                z = "Marked as No Show";

            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions"+ex;
            }

            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Message", z);
            if(isSuccess) {
                Toast.makeText(getBaseContext(),"NO SHOW",Toast.LENGTH_LONG).show();
                notificationNoShow();

                queueNumber.setText(0);
                currentlyServing.setText(cCalling);
            }
            progressDialog.hide();
        }
    }

    private class PatientCancelled extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;
        String qNumber, cCalling;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/CheckInstanceCancelled");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
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

                    output.add(returnString);
                }
                for (int i = 0; i < output.size(); i++) {
                    String line=output.get(i);
                    if(line.equals("true")){
                        isSuccess=true;
                        cancelledB=true;
                    }
                }
                in.close();

                z = "Marked as Cancelled";

            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions"+ex;
            }

            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Message", z);
            if(isSuccess) {
                Toast.makeText(getBaseContext(),"CANCELLED",Toast.LENGTH_LONG).show();
                notificationCancelled();

                queueNumber.setText("CANCELLED");
                currentlyServing.setText(cCalling);
            }
            progressDialog.hide();
        }
    }



}
