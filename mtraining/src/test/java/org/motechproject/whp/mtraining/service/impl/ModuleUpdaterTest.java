package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.ModuleService;

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
public class ModuleUpdaterTest {
    @Mock
    private ModuleService moduleService;

    @Mock
    private ChapterUpdater chapterUpdater;

    private ModuleUpdater moduleUpdater;

    @Before
    public void setUp() throws Exception {
        moduleUpdater = new ModuleUpdater(moduleService, chapterUpdater);
    }

    @Test
    public void shouldUpdateContentId() {
        ModuleDto moduleDtoToBeUpdated = new ModuleDto(true, "module1", "some description", Collections.EMPTY_LIST);
        UUID moduleContentId = UUID.randomUUID();
        ModuleDto moduleDtoFromDb = new ModuleDto(moduleContentId, 1, true, "module1", "some description", Collections.EMPTY_LIST);

        moduleUpdater.updateContentId(moduleDtoToBeUpdated, moduleDtoFromDb);

        assertEquals(moduleContentId, moduleDtoToBeUpdated.getContentId());
    }

    @Test
    public void shouldUpdateChildContents() throws Exception {
        List<ChapterDto> chapters = asList(new ChapterDto());
        ModuleDto moduleDto = new ModuleDto(true, "module1", "some description", chapters);

        moduleUpdater.updateChildContents(moduleDto);

        verify(chapterUpdater).update(chapters);
    }

    @Test
    public void shouldGetExistingModulesFromDbOnlyFirstTime() throws Exception {
        ModuleDto moduleDtoFromDb = new ModuleDto(true, "module1", "some description", Collections.EMPTY_LIST);
        when(moduleService.getAllModules()).thenReturn(asList(moduleDtoFromDb));

        List<ModuleDto> existingContents1 = moduleUpdater.getExistingContents();

        verify(moduleService, times(1)).getAllModules();
        assertEquals(1, existingContents1.size());
        assertEquals(moduleDtoFromDb, existingContents1.get(0));

        List<ModuleDto> existingContents2 = moduleUpdater.getExistingContents();

        verifyNoMoreInteractions(moduleService);
        assertEquals(1, existingContents2.size());
        assertEquals(moduleDtoFromDb, existingContents2.get(0));
    }

    @Test
    public void shouldEquateModulesByName() throws Exception {
        ModuleDto module1 = new ModuleDto(true, "module1", "old description", Collections.EMPTY_LIST);
        ModuleDto module2 = new ModuleDto(UUID.randomUUID(), 1, true, "module1", "new description", Collections.EMPTY_LIST);
        assertTrue(moduleUpdater.isEqual(module1, module2));

        ModuleDto module3 = new ModuleDto(true, "module2", "old description", Collections.EMPTY_LIST);
        assertFalse(moduleUpdater.isEqual(module1, module3));
    }
}
