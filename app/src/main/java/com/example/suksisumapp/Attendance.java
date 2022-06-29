package com.example.suksisumapp;

public class Attendance {
    private String attendanceId;
    private String attendanceName;
    private String attendanceDate;
    private String attendanceCurrentDate;

    public Attendance(){}

    public Attendance(String attendanceId, String attendanceName, String attendanceDate, String attendanceCurrentDate) {
        this.attendanceId = attendanceId;
        this.attendanceName = attendanceName;
        this.attendanceDate = attendanceDate;
        this.attendanceCurrentDate = attendanceCurrentDate;
    }

    public String getAtendanceId() {
        return attendanceId;
    }

    public String getAttendanceName() {
        return attendanceName;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public String getAttendanceCurrentDate() { return attendanceCurrentDate; }

}
