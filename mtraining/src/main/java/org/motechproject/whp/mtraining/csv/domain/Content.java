package org.motechproject.whp.mtraining.csv.domain;

import org.motechproject.whp.mtraining.domain.ContentType;

import java.util.ArrayList;
import java.util.List;

public class Content {
    private static final String INACTIVE_STATUS = "INACTIVE";

    private String name;
    private String contentAuthor;
    private ContentType contentType;
    private boolean isActive;
    private String description;
    private String fileName;
    private Integer numberOfQuizQuestions;
    private List<String> options;
    private String correctAnswer;
    private String correctAnswerFileName;
    private Long passPercentage;

    private List<Content> childContents;

    public Content(String name, String contentType, String status, String description, String fileName,
                   Integer numberOfQuizQuestions, List<String> options, String correctAnswer,
                   String correctAnswerFileName, Long passPercentage, String contentAuthor) {
        this.name = name;
        this.passPercentage = passPercentage;
        this.contentType = ContentType.from(contentType);
        this.isActive = !INACTIVE_STATUS.equalsIgnoreCase(status);
        this.description = description;
        this.fileName = fileName;
        this.numberOfQuizQuestions = numberOfQuizQuestions;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.correctAnswerFileName = correctAnswerFileName;
        this.childContents = new ArrayList<>();
        this.contentAuthor = contentAuthor;
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
        return contentType.toDto(name, description, fileName, isActive, numberOfQuizQuestions, options, correctAnswer, correctAnswerFileName, passPercentage, childDtos, contentAuthor);
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }
}
