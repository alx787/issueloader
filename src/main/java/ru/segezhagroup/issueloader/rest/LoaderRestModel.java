package ru.segezhagroup.issueloader.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoaderRestModel {

    @XmlElement(name = "value")
    private String message;

    public LoaderRestModel() {
    }

    public LoaderRestModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}