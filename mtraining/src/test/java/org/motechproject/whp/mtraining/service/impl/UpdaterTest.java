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
    public void shouldUpdateCourse() {
        ModuleDto module1 = new ModuleDto(true, "module1", null, asList(new ChapterDto(true, "chapter1", null, Collections.EMPTY_LIST)));
        ModuleDto module2 = new ModuleDto(true, "module2", null, Collections.EMPTY_LIST);
        UUID expectedModule1Id = UUID.randomUUID();
        UUID expectedChapter1Id = UUID.randomUUID();
        Updater<ModuleDto> updater = new TestUpdater(module1, expectedModule1Id, expectedChapter1Id);

        updater.update(asList(module1, module2));

        assertEquals(expectedModule1Id, module1.getContentId());
        assertEquals("module1", module1.getName());
        assertNull(module2.getContentId());
        assertEquals("module2", module2.getName());

        ChapterDto actualChapter = module1.getChapters().get(0);
        assertEquals(expectedChapter1Id, actualChapter.getContentId());
        assertEquals("chapter1", actualChapter.getName());
    }

    class TestUpdater extends Updater<ModuleDto> {

        private ModuleDto moduleDto;
        private UUID moduleId;
        private UUID chapterId;

        public TestUpdater(ModuleDto moduleDto, UUID moduleId, UUID chapterId) {
            this.moduleDto = moduleDto;
            this.moduleId = moduleId;
            this.chapterId = chapterId;
        }

        @Override
        protected void updateContentId(ModuleDto currentModule, ModuleDto existingModule) {
            currentModule.setContentId(existingModule.getContentId());
        }

        @Override
        protected void updateChildContents(ModuleDto moduleDto) {
            moduleDto.getChapters().get(0).setContentId(chapterId);
        }

        @Override
        protected List<ModuleDto> getExistingContents() {
            ChapterDto chapterDto = moduleDto.getChapters().get(0);
            ChapterDto chapterInDb = new ChapterDto(chapterId, 1, true, chapterDto.getName(), chapterDto.getDescription(), Collections.EMPTY_LIST);
            ModuleDto moduleInDb = new ModuleDto(moduleId, 2, true, moduleDto.getName(), moduleDto.getDescription(), asList(chapterInDb));
            return asList(moduleInDb);
        }

        @Override
        protected boolean isEqual(ModuleDto moduleDto1, ModuleDto moduleDto2) {
            return moduleDto1.getName().equalsIgnoreCase(moduleDto2.getName());
        }
    }
}
