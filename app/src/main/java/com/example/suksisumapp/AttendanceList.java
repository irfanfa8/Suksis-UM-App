package com.example.suksisumapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AttendanceList extends ArrayAdapter<Attendance> {
    private Activity context;
    List<Attendance> attendances;

    public AttendanceList(Activity context, List<Attendance> attendances) {
        super(context, R.layout.layout_attendance, attendances);
        this.context = context;
        this.attendances = attendances;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_attendance, null, true);

        TextView textViewFullname = (TextView) listViewItem.findViewById(R.id.textViewFullnameAtt);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDateAtt);
        TextView textViewCurrentDate = (TextView) listViewItem.findViewById(R.id.textViewCurrentDateAtt);

        Attendance attendance = attendances.get(position);
        textViewFullname.setText(attendance.getAttendanceName());
        textViewDate.setText(attendance.getAttendanceDate());
        textViewCurrentDate.setText(attendance.getAttendanceCurrentDate());

        return listViewItem;
    }

}

