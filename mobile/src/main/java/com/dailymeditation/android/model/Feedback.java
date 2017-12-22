package com.dailymeditation.android.model;

/**
 * Created with <3 by liacob & <Pi> on 06-Sep-17.
 */

public class Feedback {

    private final String date;
    private final String country;
    private final String language;
    private final String feedback;

    public Feedback(String currentDate, String country, String language, String feedback) {
        this.date = currentDate;
        this.country = country;
        this.language = language;
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getDate() {
        return date;
    }
}
