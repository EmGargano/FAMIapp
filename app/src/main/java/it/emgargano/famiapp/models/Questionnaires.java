package it.emgargano.famiapp.models;

public class Questionnaires {
    public String name;
    public String uri;

    //Empty constructor
    public Questionnaires() {
    }

    //Constructor with params
    public Questionnaires(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    //Getter and Setter methods
    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
