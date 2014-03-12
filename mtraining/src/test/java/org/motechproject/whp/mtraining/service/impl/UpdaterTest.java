package org.motechproject.whp.mtraining.service.impl;

import org.junit.Test;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
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
        ModuleDto module1 = new ModuleDto("module1", null, true, asList(new ChapterDto("chapter1", null, true, Collections.EMPTY_LIST)));
        ModuleDto module2 = new ModuleDto("module2", null, true, Collections.EMPTY_LIST);
        UUID expectedModule1Id = UUID.randomUUID();
        UUID expectedChapter1Id = UUID.randomUUID();
        Updater<ModuleDto> updater = new TestUpdater(module1, expectedModule1Id, expectedChapter1Id);

        updater.update(asList(module1, module2));

        assertEquals(expectedModule1Id, module1.getModuleIdentifier().getContentId());
        assertEquals("module1", module1.getName());
        assertNull(module2.getModuleIdentifier().getContentId());
        assertEquals("module2", module2.getName());

        ChapterDto actualChapter = module1.getChapters().get(0);
        assertEquals(expectedChapter1Id, actualChapter.getChapterIdentifier().getContentId());
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
            currentModule.setContentId(existingModule.getModuleIdentifier().getContentId());
        }

        @Override
        protected void updateChildContents(ModuleDto moduleDto) {
            moduleDto.getChapters().get(0).setContentId(chapterId);
        }

        @Override
        protected List<ModuleDto> getExistingContents() {
            ChapterDto chapterDto = moduleDto.getChapters().get(0);
            ChapterDto chapterInDb = new ChapterDto(chapterDto.getName(), chapterDto.getDescription(), new ContentIdentifierDto(chapterId, 1), Collections.EMPTY_LIST);
            ModuleDto moduleInDb = new ModuleDto(moduleDto.getName(), moduleDto.getDescription(), new ContentIdentifierDto(moduleId, 2), asList(chapterInDb));
            return asList(moduleInDb);
        }

        @Override
        protected boolean isEqual(ModuleDto moduleDto1, ModuleDto moduleDto2) {
            return moduleDto1.getName().equalsIgnoreCase(moduleDto2.getName());
        }
    }
}
