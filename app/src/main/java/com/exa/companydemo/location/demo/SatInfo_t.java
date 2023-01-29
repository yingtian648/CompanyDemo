package com.exa.companydemo.location.demo;

public class SatInfo_t {
    public short pRN;
    public float elevation;
    public float azimuth;
    public byte sNR;

    public short getPRN() {
        return this.pRN;
    }

    public void setPRN(short var1) {
        this.pRN = var1;
    }

    public float getElevation() {
        return this.elevation;
    }

    public void setElevation(float var1) {
        this.elevation = var1;
    }

    public float getAzimuth() {
        return this.azimuth;
    }

    public void setAzimuth(float var1) {
        this.azimuth = var1;
    }

    public byte getSNR() {
        return this.sNR;
    }

    public void setSNR(byte var1) {
        this.sNR = var1;
    }
}
