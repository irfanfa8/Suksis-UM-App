package com.example.suksisumapp;

public class Letter {
    private String letterId;
    private String letterName;
    private String letterTitle;
    private String letterDate;
    private String letterStatus;

    public Letter(){
        //this constructor is required
    }

    public Letter(String letterId, String letterName, String letterTitle, String letterDate, String letterStatus) {
        this.letterId = letterId;
        this.letterName = letterName;
        this.letterTitle = letterTitle;
        this.letterDate = letterDate;
        this.letterStatus = letterStatus;
    }

    public String getLetterId() {
        return letterId;
    }

    public String getLetterName() {
        return letterName;
    }

    public String getLetterTitle() {
        return letterTitle;
    }

    public String getLetterDate() { return letterDate; }

    public String getLetterStatus() {
        return letterStatus;
    }
}
