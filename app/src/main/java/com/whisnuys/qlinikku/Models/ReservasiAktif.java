package com.whisnuys.qlinikku.Models;

public class ReservasiAktif {
    String name, image, time, uid;

    public ReservasiAktif() {
    }

    public ReservasiAktif(String name, String image, String time) {
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
