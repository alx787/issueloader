package ru.segezhagroup.issueloader.jira;

public class SampleMessage {

    private String date;
    private String direction;
    private String author;
    private String authoremail;
    private String body;

    public SampleMessage(String date, String direction, String author, String authoremail, String body) {
        this.date = date;
        this.direction = direction;
        this.author = author;
        this.authoremail = authoremail;
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthoremail() {
        return authoremail;
    }

    public void setAuthoremail(String authoremail) {
        this.authoremail = authoremail;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
