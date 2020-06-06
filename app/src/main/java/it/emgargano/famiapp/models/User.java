package it.emgargano.famiapp.models;

public class User {
    public String user_id;
    public String name;
    public String surname;
    public String date_of_birth;
    public String birth_place;
    public String cell;
    public String gender;
    public int role;
    public String acceptanceId;
    public String mail;
    public String nation;

    //empty constructor
    public User(){

    }

    //constructor
    public User(String user_id, String name, String surname, String date_of_birth, String birth_place, String cell, String gender, String acceptanceName, int role, String mail, String nation) {
        this.user_id = user_id;
        this.name = name;
        this.surname = surname;
        this.date_of_birth = date_of_birth;
        this.birth_place = birth_place;
        this.cell = cell;
        this.gender = gender;
        this.acceptanceId = acceptanceName;
        this.role = role;
        this.mail = mail;
        this.nation = nation;
    }

    //Getter and Setter methods
    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getBirth_place() {
        return birth_place;
    }

    public void setBirth_place(String birth_place) {
        this.birth_place = birth_place;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAcceptanceId() {
        return acceptanceId;
    }

    public void setAcceptanceId(String acceptanceId) {
        this.acceptanceId = acceptanceId;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
}
