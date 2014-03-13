package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.csv.request.CourseStructureCsvRequest;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CourseImportServiceTest {
    private CourseImportService courseImportService;

    @Mock
    private CourseService courseService;

    private TestCourseUpdater testCourseUpdater;

    @Before
    public void setUp() throws Exception {
        testCourseUpdater = new TestCourseUpdater(courseService, mock(ModuleUpdater.class));
        courseImportService = new CourseImportService(courseService, testCourseUpdater);
    }

    @Test
    public void shouldInvokeCourseServiceToAddACourseEventuallyByConstructingContentTree() {
        List<CourseStructureCsvRequest> requests = asList(
                new CourseStructureCsvRequest("course1", "course", "active", null, "course description", null),
                new CourseStructureCsvRequest("module1", "module", "active", "course1", "module1 description", null),
                new CourseStructureCsvRequest("module2", "Module", "active", "course1", "module2 description", null),
                new CourseStructureCsvRequest("chapter1", "CHAPTER", "active", "module1", "chapter1 description", null),
                new CourseStructureCsvRequest("chapter2", "CHAPTER", "active", "module2", "chapter2 description", null),
                new CourseStructureCsvRequest("message1", "message", "active", "chapter1", "message1 description", "filename1"),
                new CourseStructureCsvRequest("message2", "message", "active", "chapter1", "message2 description", "filename2"),
                new CourseStructureCsvRequest("message3", "message", "active", "chapter2", "message3 description", "filename3"),
                new CourseStructureCsvRequest("message4", "message", "active", "chapter2", "message4 description", "filename4"),
                new CourseStructureCsvRequest("message5", "message", "inactive", "chapter2", "message5 description", "filename4")
        );
        UUID module1Id = UUID.randomUUID();
        testCourseUpdater.setContentId(module1Id);

        courseImportService.importCourse(requests);

        ArgumentCaptor<CourseDto> courseDtoCaptor = ArgumentCaptor.forClass(CourseDto.class);
        verify(courseService).addOrUpdateCourse(courseDtoCaptor.capture());
        CourseDto savedCourseDto = courseDtoCaptor.getValue();

        assertCourseDetails(savedCourseDto);
        assertModuleDetails(savedCourseDto.getModules(), module1Id);
    }

    private void assertCourseDetails(CourseDto savedCourseDto) {
        assertEquals("course1", savedCourseDto.getName());
        assertEquals("course description", savedCourseDto.getDescription());
    }

    private void assertModuleDetails(List<ModuleDto> modules, UUID module1Id) {
        assertEquals(2, modules.size());
        assertEquals(module1Id, modules.get(0).getContentId());
        assertEquals("module1", modules.get(0).getName());
        assertEquals("module1 description", modules.get(0).getDescription());
        assertEquals("module2", modules.get(1).getName());
        assertEquals("module2 description", modules.get(1).getDescription());

        assertChapterDto(modules.get(0).getChapters(), modules.get(1).getChapters());
    }

    private void assertChapterDto(List<ChapterDto> module1Chapters, List<ChapterDto> module2Chapters) {
        assertChapterDetails(module1Chapters, "chapter1", "chapter1 description");
        assertChapterDetails(module2Chapters, "chapter2", "chapter2 description");
        assertMessageDto(module1Chapters.get(0).getMessages(), module2Chapters.get(0).getMessages());
    }

    private void assertChapterDetails(List<ChapterDto> chapterDtos, String expectedName, String expectedDescription) {
        assertEquals(1, chapterDtos.size());
        assertEquals(expectedName, chapterDtos.get(0).getName());
        assertEquals(expectedDescription, chapterDtos.get(0).getDescription());
    }

    private void assertMessageDto(List<MessageDto> chapter1Messages, List<MessageDto> chapter2Messages) {
        assertEquals(2, chapter1Messages.size());
        assertMessageDetailss(chapter1Messages.get(0), "message1", "message1 description", "filename1", true);
        assertMessageDetailss(chapter1Messages.get(1), "message2", "message2 description", "filename2", true);
        assertEquals(3, chapter2Messages.size());
        assertMessageDetailss(chapter2Messages.get(0), "message3", "message3 description", "filename3", true);
        assertMessageDetailss(chapter2Messages.get(1), "message4", "message4 description", "filename4", true);
        assertMessageDetailss(chapter2Messages.get(2), "message5", "message5 description", "filename4", false);
    }

    private void assertMessageDetailss(MessageDto messageDto, String expectedName, String expectedDescription, String expectedFileName, boolean expectedStatus) {
        assertEquals(expectedName, messageDto.getName());
        assertEquals(expectedDescription, messageDto.getDescription());
        assertEquals(expectedFileName, messageDto.getExternalId());
        assertEquals(expectedStatus, messageDto.isActive());
    }

    class TestCourseUpdater extends CourseUpdater {

        private UUID contentId;

        public TestCourseUpdater(CourseService courseService, ModuleUpdater moduleUpdater) {
            super(courseService, moduleUpdater);
        }

        @Override
        public void update(List<CourseDto> course) {
            CourseDto courseDto = course.get(0);
            courseDto.getModules().get(0).setContentId(contentId);
        }

        public void setContentId(UUID contentId) {
            this.contentId = contentId;
        }
    }
}
