package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "chapter", identityType = IdentityType.APPLICATION)
public class Chapter extends CourseContent {

    @Element(column = "chapter_id")
    @Order(column = "message_order")
    @Persistent(defaultFetchGroup = "true", dependentElement = "true")
    private List<Message> messages;

    @Persistent(defaultFetchGroup = "true", dependentElement = "true")
    @Column(name = "quiz_id")
    private Quiz quiz;

    public Chapter(String name, UUID contentId, Integer version, String description, String modifiedBy, DateTime dateModified, List<Message> messages) {
        super(name, contentId, version, description, modifiedBy, dateModified);
        this.messages = messages;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Quiz getQuiz() {
        return quiz;
    }
}
