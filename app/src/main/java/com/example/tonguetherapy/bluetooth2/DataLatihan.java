package com.example.tonguetherapy.bluetooth2;

public class DataLatihan {

    String dataTanggal, dataStatus1, dataStatus2, dataStatus3, dataLimit, dataEmail;

    public DataLatihan() {
    }

    public DataLatihan(String dataTanggal, String dataStatus1, String dataStatus2, String dataStatus3, String dataLimit, String dataEmail) {
        this.dataTanggal = dataTanggal;
        this.dataStatus1 = dataStatus1;
        this.dataStatus2 = dataStatus2;
        this.dataStatus3 = dataStatus3;
        this.dataLimit = dataLimit;
        this.dataEmail = dataEmail;
    }

    public String getDataTanggal() {
        return dataTanggal;
    }

    public void setDataTanggal(String dataTanggal) {
        this.dataTanggal = dataTanggal;
    }

    public String getDataStatus1() {
        return dataStatus1;
    }

    public void setDataStatus1(String dataStatus1) {
        this.dataStatus1 = dataStatus1;
    }

    public String getDataStatus2() {
        return dataStatus2;
    }

    public void setDataStatus2(String dataStatus2) {
        this.dataStatus2 = dataStatus2;
    }

    public String getDataStatus3() {
        return dataStatus3;
    }

    public void setDataStatus3(String dataStatus3) {
        this.dataStatus3 = dataStatus3;
    }

    public String getDataLimit() {
        return dataLimit;
    }

    public void setDataLimit(String dataLimit) {
        this.dataLimit = dataLimit;
    }

    public String getDataEmail() {
        return dataEmail;
    }

    public void setDataEmail(String dataEmail) {
        this.dataEmail = dataEmail;
    }
}
