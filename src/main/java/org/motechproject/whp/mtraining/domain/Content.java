package org.motechproject.whp.mtraining.domain;

import java.util.ArrayList;
import java.util.List;

public class Content {
    private String name;
    private ContentType contentType;
    private String status;
    private String description;
    private String fileName;
    private List<Content> childContents;

    public Content(String name, ContentType contentType, String status, String description, String fileName) {
        this.name = name;
        this.contentType = contentType;
        this.status = status;
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
        return contentType.toDto(name, description, fileName, childDtos);
    }
}
