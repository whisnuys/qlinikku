package com.whisnuys.qlinikku.Models;

public class TentangKlinik {
    String nama, deskripsi, gambar, noTelp, alamat, email, wa;

    public TentangKlinik() {
    }

    public TentangKlinik(String nama, String deskripsi, String gambar, String noTelp, String alamat, String email, String wa) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.gambar = gambar;
        this.noTelp = noTelp;
        this.alamat = alamat;
        this.email = email;
        this.wa = wa;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWa() {
        return wa;
    }

    public void setWa(String wa) {
        this.wa = wa;
    }
}
