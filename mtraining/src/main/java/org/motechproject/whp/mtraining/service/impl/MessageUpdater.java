package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageUpdater extends Updater<MessageDto> {

    private MessageService messageService;
    private List<MessageDto> existingMessages;

    @Autowired
    public MessageUpdater(MessageService messageService) {
        this.messageService = messageService;
        this.existingMessages = new ArrayList<>();
    }

    @Override
    protected void updateContentId(MessageDto messageDto, MessageDto existingMessageDto) {
        messageDto.setContentId(existingMessageDto.getContentId());
    }

    @Override
    protected void updateChildContents(MessageDto messageDto) {
    }

    @Override
    protected List<MessageDto> getExistingContents() {
        if (existingMessages.isEmpty()) {
            existingMessages = messageService.getAllMessages();
        }
        return existingMessages;
    }

    @Override
    protected boolean isEqual(MessageDto messageDto1, MessageDto messageDto2) {
        return messageDto1.getName().equalsIgnoreCase(messageDto2.getName());
    }
}
