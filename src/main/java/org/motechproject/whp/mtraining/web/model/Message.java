package org.motechproject.whp.mtraining.web.model;

public class Message extends BaseModel  {
    String fileName;
    public Message(String name, String description, String status, String fileName) {
        super(name, description, status);
        this.fileName=fileName;
    }
}
