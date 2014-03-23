package org.motechproject.whp.mtraining.service.impl;

import org.junit.Test;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UpdaterTest {
    @Test
    public void shouldUpdateAContentAndItsChildContents() {
        ChapterDto chapterWithSameNameAsExistingChapter = new ChapterDto(true, "chapter1", null, Collections.EMPTY_LIST, null);
        ModuleDto moduleWithNameSameAsExistingModule = new ModuleDto(true, "module1", null, asList(chapterWithSameNameAsExistingChapter));
        ChapterDto chapterToBeUpdatedForNotExistingModule = new ChapterDto(true, "chapter2", null, Collections.EMPTY_LIST, null);
        ModuleDto newModuleWithExistingChapter = new ModuleDto(true, "module2", null, asList(chapterToBeUpdatedForNotExistingModule));
        ModuleDto newModule = new ModuleDto(true, "module3", null, Collections.EMPTY_LIST);
        UUID expectedModuleId = UUID.randomUUID();
        UUID expectedChapterId = UUID.randomUUID();
        Updater<ModuleDto> updater = new TestUpdater(moduleWithNameSameAsExistingModule, chapterToBeUpdatedForNotExistingModule, expectedModuleId, expectedChapterId);

        updater.update(asList(moduleWithNameSameAsExistingModule, newModuleWithExistingChapter));

        assertEquals(expectedModuleId, moduleWithNameSameAsExistingModule.getContentId());
        assertEquals("module1", moduleWithNameSameAsExistingModule.getName());
        ChapterDto updatedChapter1 = moduleWithNameSameAsExistingModule.getChapters().get(0);
        assertEquals(expectedChapterId, updatedChapter1.getContentId());
        assertEquals("chapter1", updatedChapter1.getName());

        assertNull(newModuleWithExistingChapter.getContentId());
        assertEquals("module2", newModuleWithExistingChapter.getName());
        ChapterDto updatedChapter2 = newModuleWithExistingChapter.getChapters().get(0);
        assertEquals(expectedChapterId, updatedChapter2.getContentId());
        assertEquals("chapter2", updatedChapter2.getName());

        assertNull(newModule.getContentId());
        assertEquals("module3", newModule.getName());
    }

    class TestUpdater extends Updater<ModuleDto> {

        private ModuleDto moduleToUpdate;
        private ChapterDto chapterToUpdate;
        private UUID moduleId;
        private UUID chapterId;

        public TestUpdater(ModuleDto moduleToUpdate, ChapterDto chapterToUpdate, UUID moduleId, UUID chapterId) {
            this.moduleToUpdate = moduleToUpdate;
            this.chapterToUpdate = chapterToUpdate;
            this.moduleId = moduleId;
            this.chapterId = chapterId;
        }

        @Override
        protected void updateContentId(ModuleDto currentModule, ModuleDto existingModule) {
            currentModule.setContentId(existingModule.getContentId());
        }

        @Override
        protected void updateChildContents(ModuleDto moduleDto) {
            if (!moduleDto.getChapters().isEmpty())
                moduleDto.getChapters().get(0).setContentId(chapterId);
        }

        @Override
        protected List<ModuleDto> getExistingContents() {
            ChapterDto chapterDto = moduleToUpdate.getChapters().get(0);
            ChapterDto chapterInDb = new ChapterDto(chapterId, 1, true, chapterDto.getName(), chapterDto.getDescription(), Collections.EMPTY_LIST, null);
            ModuleDto moduleInDb = new ModuleDto(moduleId, 2, true, moduleToUpdate.getName(), moduleToUpdate.getDescription(), asList(chapterInDb));
            ChapterDto chapterForOrphanModule = new ChapterDto(chapterId, 1, true, chapterToUpdate.getName(), chapterToUpdate.getDescription(), Collections.EMPTY_LIST, null);
            ModuleDto orphanModule = new ModuleDto(true, "some_name", "some_desc", asList(chapterForOrphanModule));
            return asList(moduleInDb, orphanModule);
        }

        @Override
        protected boolean isEqual(ModuleDto moduleDto1, ModuleDto moduleDto2) {
            return moduleDto1.getName().equalsIgnoreCase(moduleDto2.getName());
        }
    }
}
