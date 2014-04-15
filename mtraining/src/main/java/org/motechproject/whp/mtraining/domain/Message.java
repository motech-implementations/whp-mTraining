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
    private String externalId;
    @Persistent
    private String description;


    public Message(String name, UUID contentId, Integer version, String description, String externalId, String createdBy, DateTime createdOn, boolean isActive) {
        super(name, contentId, version, createdBy, createdOn, isActive);
        this.externalId = externalId;
        this.description = description;
    }

    public Message(MessageDto messageDto) {
        this(messageDto.getName(), messageDto.getContentId(), messageDto.getVersion(), messageDto.getDescription(), messageDto.getExternalId(), messageDto.getCreatedBy(), messageDto.getCreatedOn(), messageDto.isActive());
    }

    public String getExternalId() {
        return externalId;
    }

    public String getDescription() {
        return description;
    }
}
