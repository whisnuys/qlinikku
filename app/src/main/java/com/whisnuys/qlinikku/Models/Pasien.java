package com.whisnuys.qlinikku.Models;

import java.util.ArrayList;
import java.util.List;

public class Pasien extends Person{
    private List<String> prevAppointmentIDs, upcomingAppointmentIDs, seenDoctorIDs;

    public Pasien(){
        this.prevAppointmentIDs = new ArrayList<String>();
        this.upcomingAppointmentIDs = new ArrayList<String>();
        this.seenDoctorIDs = new ArrayList<String>();
    }

    public Pasien(String namaLengkap, String nik,String role,String jenisKelamin, String tanggalLahir, String alamat, String noTelepon, String email, String password, String gambar, String uid){
        super(namaLengkap, nik, role, jenisKelamin, tanggalLahir, alamat, noTelepon, email, password, gambar, uid);

        this.prevAppointmentIDs = new ArrayList<String>();
        this.upcomingAppointmentIDs = new ArrayList<String>();
        this.seenDoctorIDs = new ArrayList<String>();
    }

    public List<String> getPrevAppointmentIDs() {
        return prevAppointmentIDs;
    }

    public void setPrevAppointmentIDs(List<String> prevAppointmentIDs) {
        this.prevAppointmentIDs = prevAppointmentIDs;
    }

    public List<String> getUpcomingAppointmentIDs() {
        return upcomingAppointmentIDs;
    }

    public void setUpcomingAppointmentIDs(List<String> upcomingAppointmentIDs) {
        this.upcomingAppointmentIDs = upcomingAppointmentIDs;
    }

    public List<String> getSeenDoctorIDs() {
        return seenDoctorIDs;
    }

    public void setSeenDoctorIDs(List<String> seenDoctorIDs) {
        this.seenDoctorIDs = seenDoctorIDs;
    }
}
