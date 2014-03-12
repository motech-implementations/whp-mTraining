package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.service.MessageService;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageUpdaterTest {
    @Mock
    private MessageService messageService;

    private MessageUpdater messageUpdater;

    @Before
    public void setUp() throws Exception {
        messageUpdater = new MessageUpdater(messageService);
    }

    @Test
    public void shouldUpdateContentId() {
        MessageDto messageDtoToBeUpdated = new MessageDto("message1", "filename", "some description", true);
        UUID messageContentId = UUID.randomUUID();
        MessageDto messageDtoFromDb = new MessageDto("message1", "filename", "some description", new ContentIdentifierDto(messageContentId, 1));

        messageUpdater.updateContentId(messageDtoToBeUpdated, messageDtoFromDb);

        assertEquals(messageContentId, messageDtoToBeUpdated.getMessageIdentifier().getContentId());
    }

    @Test
    public void shouldGetExistingMessagesFromDbOnlyFirstTime() throws Exception {
        MessageDto messageDtoFromDb = new MessageDto("message1", "filename", "some description", true);
        when(messageService.getAllMessages()).thenReturn(asList(messageDtoFromDb));

        List<MessageDto> existingContents1 = messageUpdater.getExistingContents();

        verify(messageService, times(1)).getAllMessages();
        assertEquals(1, existingContents1.size());
        assertEquals(messageDtoFromDb, existingContents1.get(0));

        List<MessageDto> existingContents2 = messageUpdater.getExistingContents();

        verifyNoMoreInteractions(messageService);
        assertEquals(1, existingContents2.size());
        assertEquals(messageDtoFromDb, existingContents2.get(0));
    }

    @Test
    public void shouldEquateMessagesByName() throws Exception {
        MessageDto message1 = new MessageDto("message1", "fileName1", "old description", true);
        MessageDto message2 = new MessageDto("message1", "fileName2", "new description", new ContentIdentifierDto(UUID.randomUUID(), 1));
        assertTrue(messageUpdater.isEqual(message1, message2));

        MessageDto message3 = new MessageDto("message2", "fileName1", "old description", true);
        assertFalse(messageUpdater.isEqual(message1, message3));
    }
}
