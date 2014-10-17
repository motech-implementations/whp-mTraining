package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.Persistent;


@Entity
public class BookmarkReport extends MdsEntity {

    @Field
    @Persistent(defaultFetchGroup = "true")
    private ContentIdentifier courseIdentifier;

    @Field
    @Persistent(defaultFetchGroup = "true")
    private ContentIdentifier moduleIdentifier;

    @Field
    @Persistent(defaultFetchGroup = "true")
    private ContentIdentifier chapterIdentifier;

    @Field
    @Persistent(defaultFetchGroup = "true")
    private ContentIdentifier messageIdentifier;

    @Field
    @Persistent(defaultFetchGroup = "true")
    private ContentIdentifier quizIdentifier;

    @Field
    private DateTime dateModified;

    public BookmarkReport() { }

    public BookmarkReport(Flag flag) {
        this.courseIdentifier = flag.getCourseIdentifier();
        this.moduleIdentifier = flag.getModuleIdentifier();
        this.chapterIdentifier = flag.getChapterIdentifier();
        this.messageIdentifier = flag.getLessonIdentifier();
        this.quizIdentifier = flag.getQuizIdentifier();
        this.dateModified = (flag.getModificationDate() == null) ? DateTime.now() : flag.getModificationDate();
    }

    public ContentIdentifier getCourseIdentifier() {
        return courseIdentifier;
    }

    public void setCourseIdentifier(ContentIdentifier courseIdentifier) {
        this.courseIdentifier = courseIdentifier;
    }

    public ContentIdentifier getModuleIdentifier() {
        return moduleIdentifier;
    }

    public void setModuleIdentifier(ContentIdentifier moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier;
    }

    public ContentIdentifier getChapterIdentifier() {
        return chapterIdentifier;
    }

    public void setChapterIdentifier(ContentIdentifier chapterIdentifier) {
        this.chapterIdentifier = chapterIdentifier;
    }

    public ContentIdentifier getMessageIdentifier() {
        return messageIdentifier;
    }

    public void setMessageIdentifier(ContentIdentifier messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    public ContentIdentifier getQuizIdentifier() {
        return quizIdentifier;
    }

    public void setQuizIdentifier(ContentIdentifier quizIdentifier) {
        this.quizIdentifier = quizIdentifier;
    }

    public DateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(DateTime dateModified) {
        this.dateModified = dateModified;
    }
}
