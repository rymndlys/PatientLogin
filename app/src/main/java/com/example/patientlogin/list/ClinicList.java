package com.example.patientlogin.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.patientlogin.ConnectionClass;
import com.example.patientlogin.R;
import com.example.patientlogin.adapter.ClinicAdapter;
import com.example.patientlogin.dbutility.DBUtility;
import com.example.patientlogin.model.Clinic;

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
            Connection con = connectionClass.CONN();
            if (con == null) {
                z=null;
            } else {

                String query=SELECT_CLINIC;

                PreparedStatement ps = con.prepareStatement(query);
                // stmt.executeUpdate(query);


                ResultSet rs=ps.executeQuery();

                while (rs.next())
                {
                    String clinicid=rs.getString(1);
                    String clinicname=rs.getString(2);
                    String clinichours=rs.getString(5);
                    String clinicdays=rs.getString(4);
                    String clinicstatus=rs.getString(5);
                    Log.d("ClinicData", clinicid+clinicname+clinichours+clinicdays+clinicstatus);

                    clinicsList.add(new Clinic(R.drawable.briefcase, Integer.parseInt(clinicid), clinicname, clinicdays, clinichours, clinicstatus));
                }

                z="1";

            }
        }
        catch (Exception ex)
        {
            z=null;
            Log.d("Exceptionsss:", ex.getMessage());
        }

        return z;

    }
}
