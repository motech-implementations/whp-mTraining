package org.motechproject.whp.mtraining.web.model;

import java.util.HashMap;
import java.util.Map;

public class Module  extends BaseModel  {
    Map<String,Chapter> chapters;

    public Module(String name, String description, String status) {
        super(name, description, status);
        this.chapters = new HashMap<String,Chapter>();
    }
}
