package org.motechproject.whp.mtraining.web.model;

import java.util.HashMap;
import java.util.Map;

public class Chapter extends BaseModel {
    Map<String,Message> messages;

    public Chapter(String name, String description, String status) {
        super(name, description, status);
        this.messages= new HashMap<String,Message>();
    }
}
