package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.service.MessageService;

import java.util.ArrayList;
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
    public static final String AUTHOR = "Author";
    @Mock
    private MessageService messageService;

    private MessageUpdater messageUpdater;

    @Before
    public void setUp() throws Exception {
        messageUpdater = new MessageUpdater(messageService);
    }

    @Test
    public void shouldUpdateContentId() {
        MessageDto messageDtoToBeUpdated = new MessageDto(true, "message1", "filename", "some description", AUTHOR);
        UUID messageContentId = UUID.randomUUID();
        MessageDto messageDtoFromDb = new MessageDto(messageContentId, 1, true, "message1", "filename", "some description", AUTHOR);

        messageUpdater.updateContentId(messageDtoToBeUpdated, messageDtoFromDb);

        assertEquals(messageContentId, messageDtoToBeUpdated.getContentId());
    }

    @Test
    public void shouldGetExistingMessagesFromDbOnlyFirstTime() throws Exception {
        MessageDto messageDtoFromDb = new MessageDto(true, "message1", "filename", "some description", AUTHOR);
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
        MessageDto message1 = new MessageDto(true, "message1", "fileName1", "old description", AUTHOR);
        MessageDto message2 = new MessageDto(UUID.randomUUID(), 1, true, "message1", "fileName2", "new description", AUTHOR);
        assertTrue(messageUpdater.isEqual(message1, message2));

        MessageDto message3 = new MessageDto(true, "message2", "fileName1", "old description", AUTHOR);
        assertFalse(messageUpdater.isEqual(message1, message3));
    }

    @Test
    public void shouldInvalidateExistingContentCache() {
        final MessageDto messageDtoFromDb = new MessageDto(true, "message1", "fileName", "some description", AUTHOR);
        when(messageService.getAllMessages()).thenReturn(new ArrayList<MessageDto>() {{
            add(messageDtoFromDb);
        }});
        assertFalse(messageUpdater.getExistingContents().isEmpty());

        messageUpdater.invalidateCache();

        assertTrue(messageUpdater.getExistingContents().isEmpty());
    }
}
