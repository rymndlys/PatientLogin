package com.example.patientlogin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class KeruxSession {

    private SharedPreferences prefs;

    public KeruxSession(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setcontactno(String contactno) {
        prefs.edit().putString("contactno", contactno).commit();
    }

    public String getcontactno() {
        String contactno = prefs.getString("contactno","");
        return contactno;
    }

    public void setpassword(String password) {
        prefs.edit().putString("password", password).commit();
    }

    public String getpassword() {
        String password = prefs.getString("password","");
        return password;
    }
    public void setpatientid(String patientid) {
        prefs.edit().putString("patientid", patientid).commit();
    }

    public String getpatientid() {
        String patientid = prefs.getString("patientid","");
        return patientid;
    }

    public void setusername(String username) {
        prefs.edit().putString("username", username).commit();
    }

    public String getusername() {
        String username = prefs.getString("username","");
        return username;
    }
}

