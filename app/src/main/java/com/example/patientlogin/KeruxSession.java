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

    public void setemail(String email) {

        prefs.edit().putString("email", email).commit();
    }

    public String getemail() {
        String email = prefs.getString("email","");
        return email;
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

    public void setfirstname(String firstname) {
        prefs.edit().putString("firstname", firstname).commit();
    }

    public String getfirstname() {
        String firstname = prefs.getString("firstname","");
        return firstname;
    }

    public void setlastname(String lastname) {
        prefs.edit().putString("lastname", lastname).commit();
    }

    public String getlastname() {
        String lastname = prefs.getString("lastname","");
        return lastname;
    }

    public void setpatienttype(String patienttype){
        prefs.edit().putString("patienttype", patienttype).commit();
    }

    public String getpatienttype() {
        String patienttype = prefs.getString("patienttype","");
        return patienttype;
    }

    public void setchosendept(String chosendept){
        prefs.edit().putString("chosendept", chosendept).commit();
    }

    public String getchosendept() {
        String patienttype = prefs.getString("chosendept","");
        return patienttype;
    }

}


