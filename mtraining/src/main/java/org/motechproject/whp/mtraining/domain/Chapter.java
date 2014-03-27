package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.QuizDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "chapter", identityType = IdentityType.APPLICATION)
public class Chapter extends CourseContent {

    @Element(column = "chapter_id")
    @Order(column = "message_order")
    @Persistent(defaultFetchGroup = "true", dependentElement = "true")
    private List<Message> messages = new ArrayList<>();

    @Persistent(defaultFetchGroup = "true", dependentElement = "true")
    @Column(name = "quiz_id")
    private Quiz quiz;

    public Chapter(String name, UUID contentId, Integer version, String description, String modifiedBy, DateTime dateModified, List<Message> messages, boolean isActive) {
        super(name, contentId, version, description, modifiedBy, dateModified, isActive);
        this.messages = messages;
    }

    public Chapter(ChapterDto chapterDto) {
        super(chapterDto.getName(), chapterDto.getContentId(), chapterDto.getVersion(), chapterDto.getDescription(), chapterDto.getCreatedBy(), chapterDto.getCreatedOn(), chapterDto.isActive());
        for (MessageDto messageDto : chapterDto.getMessages()) {
            messages.add(new Message(messageDto));
        }
        QuizDto quizDto = chapterDto.getQuiz();
        if (quizDto != null) {
            this.quiz = new Quiz(quizDto);
        }
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
