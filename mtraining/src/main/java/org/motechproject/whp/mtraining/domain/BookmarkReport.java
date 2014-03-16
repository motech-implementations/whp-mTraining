package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.util.DateTimeUtil;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@PersistenceCapable(table = "bookmark_report", identityType = IdentityType.APPLICATION)
public class BookmarkReport {


    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;
    @Persistent(column = "provider_remedy_id")
    private String remedyId;

    @Persistent(column = "course_id")
    private UUID courseId;

    @Persistent(column = "course_version")
    private Integer courseVersion;

    @Persistent(column = "module_id")
    private UUID moduleId;

    @Persistent(column = "module_version")
    private Integer moduleVersion;

    @Persistent(column = "chapter_id")
    private UUID chapterId;

    @Persistent(column = "chapter_version")
    private Integer chapterVersion;

    @Persistent(column = "message_id")
    private UUID messageId;

    @Persistent(column = "message_version")
    private Integer messageVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Persistent(column = "date_modified")
    private DateTime dateModified;

    public BookmarkReport(String remedyId, BookmarkDto bookmarkDto) {
        this.remedyId = remedyId;
        setCourse(bookmarkDto.getCourse());
        setModule(bookmarkDto.getModule());
        setChapter(bookmarkDto.getChapter());
        setMessage(bookmarkDto.getMessage());
        dateModified = DateTimeUtil.parse(bookmarkDto.getDateModified());
    }

    private void setCourse(ContentIdentifierDto course) {
        this.courseId = course.getContentId();
        this.courseVersion = course.getVersion();
    }

    private void setModule(ContentIdentifierDto module) {
        this.moduleId = module.getContentId();
        this.moduleVersion = module.getVersion();
    }

    private void setChapter(ContentIdentifierDto chapter) {
        this.chapterId = chapter.getContentId();
        this.chapterVersion = chapter.getVersion();
    }

    private void setMessage(ContentIdentifierDto message) {
        this.messageId = message.getContentId();
        this.messageVersion = message.getVersion();
    }

    public String getRemedyId() {
        return remedyId;
    }

    public UUID getMessageId() {
        return messageId;
    }
}
