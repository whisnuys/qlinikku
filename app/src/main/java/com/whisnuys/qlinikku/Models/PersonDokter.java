package com.whisnuys.qlinikku.Models;

public class PersonDokter {
    String namaLengkap, jenisKelamin, noTelepon, spesialis,role, gambar, uid;

    public PersonDokter() {
    }

    public PersonDokter(String namaLengkap, String jenisKelamin, String noTelepon, String spesialis, String role, String gambar, String uid) {
        this.namaLengkap = namaLengkap;
        this.jenisKelamin = jenisKelamin;
        this.noTelepon = noTelepon;
        this.spesialis = spesialis;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
