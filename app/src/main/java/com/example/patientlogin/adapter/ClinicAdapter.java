package com.example.patientlogin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.patientlogin.model.Clinic;
import com.example.patientlogin.R;
import java.util.ArrayList;

public class ClinicAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Clinic> clinics;

    public ClinicAdapter(Context context, ArrayList<Clinic> clinics) {
        this.context = context;
        this.clinics = clinics;
    }

    @Override
    public int getCount() {
        return clinics.size();
    }

    @Override
    public Object getItem(int position) {
        return clinics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = View.inflate(context , R.layout.dashboardselect ,null);
        }

        ImageView images = (ImageView) convertView.findViewById(R.id.clinicimageView);
        TextView clinicName = (TextView) convertView.findViewById(R.id.clinicName);
        TextView clinicHours = (TextView)convertView.findViewById(R.id.clinicHours);
        TextView clinicDays = (TextView)convertView.findViewById(R.id.clinicDays);
        Clinic clinic = clinics.get(position);
        images.setImageResource(clinic.getClinic_image());
        clinicName.setText(clinic.getClinicName());
        clinicHours.setText(clinic.getClinicHours());
        clinicDays.setText(clinic.getClinicDays());
        return convertView;
    }


}
