package com.whisnuys.qlinikku.Models;

public class ReservasiAktif {
    String name, gambar, time, tanggal;

    public ReservasiAktif() {
    }

    public ReservasiAktif(String name, String gambar, String time, String tanggal) {
        this.name = name;
        this.gambar = gambar;
        this.time = time;
        this.tanggal = tanggal;
    }

    public String getName() {
        return name;
    }

    public String getGambar() {
        return gambar;
    }

    public String getTime() {
        return time;
    }

    public String getTanggal() {
        return tanggal;
    }
}
