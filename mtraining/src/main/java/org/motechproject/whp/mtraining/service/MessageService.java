package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.dto.MessageDto;

import java.util.List;

/**
 * Service Interface that exposes APIs to messages
 */

public interface MessageService {

    /**
     * Add a message if it already does not exist, update it otherwise.
     *
     * @param messageDto
     * @return
     */
    ContentIdentifierDto addOrUpdateMessage(MessageDto messageDto);

    /**
     * Return message with given message identifier
     *
     * @param messageIdentifier
     * @return
     */
    MessageDto getMessage(ContentIdentifierDto messageIdentifier);

    /**
     * Return all messages
     *
     * @return
     */
    List<MessageDto> getAllMessages();
}
