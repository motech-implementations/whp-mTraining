package org.motechproject.whp.mtraining.reports;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.CourseDTOBuilder;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.Chapter;
import org.motechproject.whp.mtraining.domain.Message;
import org.motechproject.whp.mtraining.domain.Module;
import org.motechproject.whp.mtraining.repository.AllCourses;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CourseReporterTest {

    @Mock
    CourseService courseService;

    @Mock
    AllCourses allCourses;

    CourseReporter courseReporter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        courseReporter = new CourseReporter(courseService, allCourses);
    }

    @Test
    public void shouldAddACertificationCourseWhenCourseReported() {
        UUID courseId = UUID.randomUUID();

        CourseDto courseDto = new CourseDTOBuilder().withName("NT001").build();
        when(courseService.getCourse(new ContentIdentifierDto(courseId, 1))).thenReturn(courseDto);

        courseReporter.reportCourseAdded(courseId, 1);

        ArgumentCaptor<Course> certificationCourseArgumentCaptor = ArgumentCaptor.forClass(Course.class);
        verify(allCourses).add(certificationCourseArgumentCaptor.capture());

        Course courseAddedToReports = certificationCourseArgumentCaptor.getValue();

        assertThat(courseAddedToReports.getName(), Is.is(courseDto.getName()));
        assertModules(courseAddedToReports.getModules(), courseDto.getModules());
    }

    private void assertModules(List<Module> modules, List<ModuleDto> moduleDtos) {
        assertEquals(modules.size(), moduleDtos.size());
        Module module = modules.get(0);
        ModuleDto moduleDto = moduleDtos.get(0);
        assertEquals(moduleDto.getName(), module.getName());
        assertChapters(moduleDto.getChapters(), module.getChapters());
    }

    private void assertChapters(List<ChapterDto> chapterDtos, List<Chapter> chapters) {
        assertEquals(chapterDtos.size(), chapters.size());
        Chapter chapter = chapters.get(0);
        ChapterDto chapterDto = chapterDtos.get(0);
        assertEquals(chapterDto.getName(), chapter.getName());
        assertMessages(chapterDto.getMessages(), chapter.getMessages());
    }

    private void assertMessages(List<MessageDto> messageDtos, List<Message> messages) {
        assertEquals(messageDtos.size(), messages.size());
        Message message = messages.get(0);
        MessageDto messageDto = messageDtos.get(0);
        assertEquals(messageDto.getName(), message.getName());
    }

}
