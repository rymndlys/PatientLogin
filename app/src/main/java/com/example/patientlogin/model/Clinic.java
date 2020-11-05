package com.example.patientlogin.model;

public class Clinic {
    private int clinic_image;
    private int clinic_id;
    private String clinicName;
    private String clinicDays;
    private String clinicHours;
    private String status;

    public Clinic(int clinic_image, int clinic_id, String clinicName, String clinicDays, String clinicHours, String status) {
        this.clinic_image = clinic_image;
        this.clinic_id = clinic_id;
        this.clinicName = clinicName;
        this.clinicDays = clinicDays;
        this.clinicHours = clinicHours;
        this.status = status;
    }

    public int getClinic_image() {
        return clinic_image;
    }

    public void setClinic_image(int clinic_image) {
        this.clinic_image = clinic_image;
    }

    public int getClinic_id() {
        return clinic_id;
    }

    public void setClinic_id(int clinic_id) {
        this.clinic_id = clinic_id;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicDays() {
        return clinicDays;
    }

    public void setClinicDays(String clinicDays) {
        this.clinicDays = clinicDays;
    }

    public String getClinicHours() {
        return clinicHours;
    }

    public void setClinicHours(String clinicHours) {
        this.clinicHours = clinicHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
