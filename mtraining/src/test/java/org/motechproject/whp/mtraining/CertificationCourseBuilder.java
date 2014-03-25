package org.motechproject.whp.mtraining;

import org.joda.time.DateTime;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.domain.CertificationCourse;
import org.motechproject.whp.mtraining.domain.Chapter;
import org.motechproject.whp.mtraining.domain.Message;
import org.motechproject.whp.mtraining.domain.Module;

import java.util.Arrays;
import java.util.UUID;

public class CertificationCourseBuilder {

    private UUID contentId = UUID.randomUUID();
    private String courseName = "CS001";
    private Integer version = 1;
    private String description = "description";
    private String modifiedBy = "User";
    private DateTime dateModified = ISODateTimeUtil.nowInTimeZoneUTC();
    private String audioFileName = "audio.wav";

    public CertificationCourseBuilder withName(String courseName) {
        this.courseName = courseName;
        return this;
    }

    public CertificationCourse build() {
        return new CertificationCourse(courseName, contentId, version, description, modifiedBy, dateModified, Arrays.asList(buildModule("MOD001")));
    }

    private Module buildModule(String moduleName) {
        return new Module(moduleName, contentId, version, description, modifiedBy, dateModified, Arrays.asList(buildChapter("CH001")));
    }

    private Chapter buildChapter(String chapterName) {
        return new Chapter(chapterName, contentId, version, description, modifiedBy, dateModified, Arrays.asList(buildMessage("MSG001")));
    }

    private Message buildMessage(String messageName) {
        return new Message(messageName, contentId, version, description, modifiedBy, dateModified, audioFileName);
    }


    public CertificationCourseBuilder withMessageAudioFile(String audioFileName) {
        this.audioFileName = audioFileName;
        return this;
    }
}
