package org.motechproject.whp.mtraining.domain;

import java.util.ArrayList;
import java.util.List;

public class Content {
    private static final String INACTIVE_STATUS = "INACTIVE";

    private String name;
    private ContentType contentType;
    private boolean isActive;
    private String description;
    private String fileName;
    private List<Content> childContents;

    public Content(String name, String contentType, String status, String description, String fileName) {
        this.name = name;
        this.contentType = ContentType.from(contentType);
        this.isActive = !INACTIVE_STATUS.equalsIgnoreCase(status);
        this.description = description;
        this.fileName = fileName;
        this.childContents = new ArrayList<>();
    }

    public void addChildContent(Content childContent) {
        childContents.add(childContent);
    }

    public Object toDto() {
        List<Object> childDtos = new ArrayList<>();
        for (Content childContent : childContents) {
            Object childDto = childContent.toDto();
            childDtos.add(childDto);
        }
        return contentType.toDto(name, description, fileName, isActive, childDtos);
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }
}
