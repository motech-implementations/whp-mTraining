package org.motechproject.whp.mtraining.web.model;

public class BaseModel {
    String name;
    String description;
    String status;

    public BaseModel(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }
}
