package org.motechproject.whp.mtraining.reports.domain;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@PersistenceCapable
@EmbeddedOnly
public class BookmarkReport {

    @Persistent
    private UUID courseId;
    @Persistent
    private Integer courseVersion;
    @Persistent
    private UUID moduleId;
    @Persistent
    private Integer moduleVersion;
    @Persistent
    private UUID chapterId;
    @Persistent
    private Integer chapterVersion;
    @Persistent
    private UUID messageId;
    @Persistent
    private Integer messageVersion;
    @Persistent
    private UUID quizId;
    @Persistent
    private Integer quizVersion;

    @Persistent
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime dateModified;

    public BookmarkReport(Bookmark bookmark) {
//        setCourse(bookmark.getCourse());
//        setModule(bookmark.getModule());
//        setChapter(bookmark.getChapter());
//        setMessage(bookmark.getMessage());
//        setQuiz(bookmark.getQuiz());
//        dateModified = ISODateTimeUtil.parseWithTimeZoneUTC(bookmark.getDateModified());
    }

    public UUID getCourseId() {
        return courseId;
    }

    public DateTime getDateModified() {
        return dateModified;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public Integer getMessageVersion() {
        return messageVersion;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public Integer getQuizVersion() {
        return quizVersion;
    }

//    private void setMessage(ContentIdentifierDto message) {
//        if (message == null) {
//            this.messageId = null;
//            this.messageVersion = null;
//        } else {
//            this.messageId = message.getContentId();
//            this.messageVersion = message.getVersion();
//        }
//    }
//
//    private void setChapter(ContentIdentifierDto chapter) {
//        this.chapterId = chapter.getContentId();
//        this.chapterVersion = chapter.getVersion();
//    }
//
//    private void setModule(ContentIdentifierDto module) {
//        this.moduleId = module.getContentId();
//        this.moduleVersion = module.getVersion();
//    }
//
//    private void setCourse(ContentIdentifierDto course) {
//        this.courseId = course.getContentId();
//        this.courseVersion = course.getVersion();
//    }
//
//    public boolean hasCourse(ContentIdentifierDto course) {
//        return this.courseId.equals(course.getContentId()) && this.courseVersion.equals(course.getVersion());
//    }
//
//    private void setQuiz(ContentIdentifierDto quiz) {
//        if (quiz == null) {
//            this.quizId = null;
//            this.quizVersion = null;
//        } else {
//            this.quizId = quiz.getContentId();
//            this.quizVersion = quiz.getVersion();
//        }
//    }

}
