package com.example.tonguetherapy.personalData;

public class Users {

    String emailUser, fullName, age, dateBirth, uid;

    public Users() {
    }

    public Users(String emailUser, String fullName, String age, String dateBirth, String uid) {
        this.emailUser = emailUser;
        this.fullName = fullName;
        this.age = age;
        this.dateBirth = dateBirth;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }
}
