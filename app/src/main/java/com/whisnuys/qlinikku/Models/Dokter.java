package com.whisnuys.qlinikku.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Dokter  {

    String namaLengkap, jenisKelamin, noTelepon, spesialis, gambar, uid;

    public Dokter() {
    }

    public Dokter(String namaLengkap, String jenisKelamin, String noTelepon, String spesialis, String gambar, String uid) {
        this.namaLengkap = namaLengkap;
        this.jenisKelamin = jenisKelamin;
        this.noTelepon = noTelepon;
        this.spesialis = spesialis;
        this.gambar = gambar;
        this.uid = uid;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public String getSpesialis() {
        return spesialis;
    }

    public void setSpesialis(String spesialis) {
        this.spesialis = spesialis;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
