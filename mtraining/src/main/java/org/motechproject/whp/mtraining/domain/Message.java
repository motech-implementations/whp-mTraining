package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.MessageDto;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.UUID;

@PersistenceCapable(table = "message", identityType = IdentityType.APPLICATION)
public class Message extends CourseContent {

    @Persistent(column = "audio_file_name")
    private String audioFileName;

    @Persistent
    private String description;


    public Message(String name, UUID contentId, Integer version, String description, String createdBy, DateTime createdOn, String audioFileName, boolean isActive) {
        super(name, contentId, version, createdBy, createdOn, isActive);
        this.audioFileName = audioFileName;
        this.description = description;
    }

    public Message(MessageDto messageDto) {
        this(messageDto.getName(), messageDto.getContentId(), messageDto.getVersion(), messageDto.getDescription(), messageDto.getCreatedBy(), messageDto.getCreatedOn(), messageDto.getExternalId(), messageDto.isActive());
    }

    public String getAudioFileName() {
        return audioFileName;
    }
}
