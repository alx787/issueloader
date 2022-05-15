package ru.segezhagroup.issueloader.jira;

public class SampleAttachment {

    private String date;
    private String name;
    private String description;
    private String body;

    public SampleAttachment(String date, String name, String description, String body) {
        this.date = date;
        this.name = name;
        this.description = description;
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
