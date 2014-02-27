package org.motechproject.whp.mtraining.web.model;

import java.util.HashMap;
import java.util.Map;

public class Course extends BaseModel {
    private Map<String,Module > modules;

    public Course(String name, String description, String status) {
        super(name,description,status);
        this.modules = new HashMap<String,Module>();
    }

    public void addModule(Module module) {
        modules.values();
    }

    public Module findModule(String name){
        return modules.get(name);
    }
}

