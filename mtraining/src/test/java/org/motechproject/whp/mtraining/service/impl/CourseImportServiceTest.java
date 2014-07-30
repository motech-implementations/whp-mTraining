package org.motechproject.whp.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseImportServiceTest {
    private CourseImportService courseImportService;

    @Mock
    private MTrainingService mTrainingService;
    @Mock
    private MotechUserService motechUserService;
    @Mock
    private CourseConfigurationService courseConfigService;

    @Before
    public void setUp() throws Exception {
        courseImportService = new CourseImportService();
    }

    @Test
    public void shouldInvokeCourseServiceToAddACourseEventuallyByConstructingContentTree() {
        List<CourseCsvRequest> requests = asList(
                new CourseCsvRequest("course1", "course", CourseUnitState.Active, null, "course description", "courseFileName"),
                new CourseCsvRequest("chapter1", "CHAPTER", CourseUnitState.Active, "course1", "chapter1 description", "chapter1FileName"),
                new CourseCsvRequest("chapter2", "CHAPTER", CourseUnitState.Active, "course1", "chapter2 description", "chapter2FileName"),
                new CourseCsvRequest("lesson1", "lesson", CourseUnitState.Active, "chapter1", "lesson1 description", "filename1"),
                new CourseCsvRequest("lesson2", "lesson", CourseUnitState.Active, "chapter1", "lesson2 description", "filename2"),
                new CourseCsvRequest("lesson3", "lesson", CourseUnitState.Active, "chapter2", "lesson3 description", "filename3"),
                new CourseCsvRequest("lesson4", "lesson", CourseUnitState.Active, "chapter2", "lesson4 description", "filename4"),
                new CourseCsvRequest("lesson5", "lesson", CourseUnitState.Inactive, "chapter2", "lesson5 description", "filename4")
        );
        
        UserDto userDTo = mock(UserDto.class);
        when(userDTo.getUserName()).thenReturn("Superman");
        when(motechUserService.getCurrentUser()).thenReturn(userDTo);

        courseImportService.importCoursePlan(requests);

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        verify(mTrainingService).createCourse(courseCaptor.capture());
        Course savedCourse = courseCaptor.getValue();

        assertCourseDetails(savedCourse);
        assertChapter(asList(savedCourse.getChapters().get(0)), asList(savedCourse.getChapters().get(1)));
    }

    @Test
    public void shouldRetrieveCurrentUserAndSetItAsCourseContentCreator() {
        List<CourseCsvRequest> requests = asList(
                new CourseCsvRequest("course1", "course", CourseUnitState.Active, null, "course description", null),
                new CourseCsvRequest("module1", "module", CourseUnitState.Active, "course1", "module1 description", null)
        );

        UserDto userDto = mock(UserDto.class);
        when(userDto.getUserName()).thenReturn("Course Admin");
        when(motechUserService.getCurrentUser()).thenReturn(userDto);

        courseImportService.importCoursePlan(requests);

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        verify(mTrainingService).createCourse(courseCaptor.capture());
        
        verify(motechUserService).getCurrentUser();
    }

    private void assertCourseDetails(Course savedCourse) {
        assertEquals("course1", savedCourse.getName());
        assertEquals("course description", savedCourse.getContent());
    }

    private void assertChapter(List<Chapter> module1Chapters, List<Chapter> module2Chapters) {
        assertChapterDetails(module1Chapters, "chapter1", "chapter1 description");
        assertChapterDetails(module2Chapters, "chapter2", "chapter2 description");
        assertMessageDto(module1Chapters.get(0).getLessons(), module2Chapters.get(0).getLessons());
    }

    private void assertChapterDetails(List<Chapter> chapters, String expectedName, String expectedDescription) {
        assertEquals(1, chapters.size());
        assertEquals(expectedName, chapters.get(0).getName());
        assertEquals(expectedDescription, chapters.get(0).getContent());
    }

    private void assertMessageDto(List<Lesson> chapter1Lessons, List<Lesson> chapter2Lessons) {
        assertEquals(2, chapter1Lessons.size());
        assertMessageDetailss(chapter1Lessons.get(0), "message1", "message1 description", "filename1", CourseUnitState.Active);
        assertMessageDetailss(chapter1Lessons.get(1), "message2", "message2 description", "filename2", CourseUnitState.Active);
        assertEquals(3, chapter2Lessons.size());
        assertMessageDetailss(chapter2Lessons.get(0), "message3", "message3 description", "filename3", CourseUnitState.Active);
        assertMessageDetailss(chapter2Lessons.get(1), "message4", "message4 description", "filename4", CourseUnitState.Active);
        assertMessageDetailss(chapter2Lessons.get(2), "message5", "message5 description", "filename4", CourseUnitState.Inactive);
    }

    private void assertMessageDetailss(Lesson lesson, String expectedName, String expectedDescription, String expectedFileName, CourseUnitState expectedStatus) {
        assertEquals(expectedName, lesson.getName());
        assertEquals(expectedDescription, lesson.getContent());
        assertEquals(expectedFileName, lesson.getId());
        assertEquals(expectedStatus, lesson.getState());
    }
}
