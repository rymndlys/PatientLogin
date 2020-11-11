package com.example.patientlogin.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.patientlogin.ConnectionClass;
import com.example.patientlogin.R;
import com.example.patientlogin.adapter.ClinicAdapter;
import com.example.patientlogin.dbutility.DBUtility;
import com.example.patientlogin.model.Clinic;

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
import java.util.ArrayList;

public class ClinicList extends AsyncTask<Void,Void,String> implements DBUtility {
    //FINISH THIS
    Context c;
    ListView lv;
    ProgressDialog pd;

    ArrayList<Clinic> clinicsList;
    ClinicAdapter clinicAdapter;
    ConnectionClass connectionClass;

    public ClinicList(Context c, ListView lv) {
        this.c = c;
        this.lv = lv;
        this.connectionClass = new ConnectionClass();
        this.clinicsList  = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd=new ProgressDialog(c);
        pd.setTitle("Downloading..");
        pd.setMessage("Retrieving data...Please wait");
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.retrieveData();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        pd.dismiss();

        lv.setAdapter(null);

        if(s != null)
        {
            clinicAdapter = new ClinicAdapter(c, clinicsList);
            lv.setAdapter(clinicAdapter);

        }else {
            Toast.makeText(c,"No data retrieved", Toast.LENGTH_SHORT).show();
        }
    }

    private String retrieveData()
    {
        String z;
        z=null;
        try {
            URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/ClinicListPatientServlet");
            URLConnection connection = url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String returnString="";
            ArrayList<String> output=new ArrayList<String>();
            while ((returnString = in.readLine()) != null)
            {
                z = "1";
                Log.d("returnString", returnString);
                output.add(returnString);
            }
            for (int i = 0; i < output.size(); i++) {
                String line=output.get(i);
                String [] words=line.split("\\s\\|\\s");
                    clinicsList.add(new Clinic(R.drawable.briefcase, Integer.parseInt(words[0]), words[1], words[2], words[3], words[4]));
                }
            in.close();



        }
        catch (Exception ex)
        {
            z=null;
            Log.d("Exceptionsss:", ex.getMessage());
        }

        return z;

    }
}
