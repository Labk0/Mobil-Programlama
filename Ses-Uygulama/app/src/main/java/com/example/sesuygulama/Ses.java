package com.example.sesuygulama;

public class Ses {
    private String ad;
    private int kategoriId;
    private String dosyaYolu;

    public Ses(String ad, int kategoriId, String dosyaYolu) {
        this.ad = ad;
        this.kategoriId = kategoriId;
        this.dosyaYolu = dosyaYolu;
    }

    public String getAd() {
        return ad;
    }

    public int getKategoriId() {
        return kategoriId;
    }

    public String getDosyaYolu() {
        return dosyaYolu;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public void setKategoriId(int kategoriId) {
        this.kategoriId = kategoriId;
    }

    public void setDosyaYolu(String dosyaYolu) {
        this.dosyaYolu = dosyaYolu;
    }
}
