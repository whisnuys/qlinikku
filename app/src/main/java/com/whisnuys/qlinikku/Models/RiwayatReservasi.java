package com.whisnuys.qlinikku.Models;

public class RiwayatReservasi {
    String name, image, time;

    public RiwayatReservasi() {
    }

    public RiwayatReservasi(String name, String image, String time) {
        this.name = name;
        this.image = image;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getTime() {
        return time;
    }
}
