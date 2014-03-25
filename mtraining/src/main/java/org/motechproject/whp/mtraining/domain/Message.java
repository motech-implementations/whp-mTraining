package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.UUID;

@PersistenceCapable(table = "message", identityType = IdentityType.APPLICATION)
public class Message extends CourseContent {

    @Persistent(column = "audio_file_name")
    private String audioFileName;

    public Message(String name, UUID contentId, Integer version, String description, String modifiedBy, DateTime dateModified, String audioFileName) {
        super(name, contentId, version, description, modifiedBy, dateModified);
        this.audioFileName = audioFileName;
    }

    public String getAudioFileName() {
        return audioFileName;
    }
}
