package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.service.ChapterService;

import java.util.Collections;
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
public class ChapterUpdaterTest {
    @Mock
    private ChapterService chapterService;

    @Mock
    private MessageUpdater messageUpdater;

    private ChapterUpdater chapterUpdater;

    @Before
    public void setUp() throws Exception {
        chapterUpdater = new ChapterUpdater(chapterService, messageUpdater);
    }

    @Test
    public void shouldUpdateContentId() {
        ChapterDto chapterDtoToBeUpdated = new ChapterDto(true, "chapter1", "some description", Collections.EMPTY_LIST);
        UUID chapterContentId = UUID.randomUUID();
        ChapterDto chapterDtoFromDb = new ChapterDto(chapterContentId, 1, true, "chapter1", "some description", Collections.EMPTY_LIST);

        chapterUpdater.updateContentId(chapterDtoToBeUpdated, chapterDtoFromDb);

        assertEquals(chapterContentId, chapterDtoToBeUpdated.getContentId());
    }

    @Test
    public void shouldUpdateChildContents() throws Exception {
        List<MessageDto> messages = asList(new MessageDto());
        ChapterDto chapterDto = new ChapterDto(true, "chapter1", "some description", messages);

        chapterUpdater.updateChildContents(chapterDto);

        verify(messageUpdater).update(messages);
    }

    @Test
    public void shouldGetExistingChaptersFromDbOnlyFirstTime() throws Exception {
        ChapterDto chapterDtoFromDb = new ChapterDto(true, "chapter1", "some description", Collections.EMPTY_LIST);
        when(chapterService.getAllChapters()).thenReturn(asList(chapterDtoFromDb));

        List<ChapterDto> existingContents1 = chapterUpdater.getExistingContents();

        verify(chapterService, times(1)).getAllChapters();
        assertEquals(1, existingContents1.size());
        assertEquals(chapterDtoFromDb, existingContents1.get(0));

        List<ChapterDto> existingContents2 = chapterUpdater.getExistingContents();

        verifyNoMoreInteractions(chapterService);
        assertEquals(1, existingContents2.size());
        assertEquals(chapterDtoFromDb, existingContents2.get(0));
    }

    @Test
    public void shouldEquateChaptersByName() throws Exception {
        ChapterDto chapter1 = new ChapterDto(true, "chapter1", "old description", Collections.EMPTY_LIST);
        ChapterDto chapter2 = new ChapterDto(UUID.randomUUID(), 1, true, "chapter1", "new description", Collections.EMPTY_LIST);
        assertTrue(chapterUpdater.isEqual(chapter1, chapter2));

        ChapterDto chapter3 = new ChapterDto(true, "chapter2", "old description", Collections.EMPTY_LIST);
        assertFalse(chapterUpdater.isEqual(chapter1, chapter3));
    }
}
