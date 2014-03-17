package org.motechproject.whp.mtraining.reports.domain;

import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
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


    public BookmarkReport(BookmarkDto bookmark) {
        setCourse(bookmark.getCourse());
        setModule(bookmark.getModule());
        setChapter(bookmark.getChapter());
        setMessage(bookmark.getMessage());
    }

    private void setMessage(ContentIdentifierDto message) {
        this.messageId = message.getContentId();
        this.messageVersion = message.getVersion();
    }

    private void setChapter(ContentIdentifierDto chapter) {
        this.chapterId = chapter.getContentId();
        this.chapterVersion = chapter.getVersion();
    }

    private void setModule(ContentIdentifierDto module) {
        this.moduleId = module.getContentId();
        this.moduleVersion = module.getVersion();
    }

    private void setCourse(ContentIdentifierDto course) {
        this.courseId = course.getContentId();
        this.courseVersion = course.getVersion();
    }
}
