package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;

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
public class CourseUpdaterTest {
    @Mock
    private CourseService courseService;

    @Mock
    private ModuleUpdater moduleUpdater;

    private CourseUpdater courseUpdater;

    @Before
    public void setUp() throws Exception {
        courseUpdater = new CourseUpdater(courseService, moduleUpdater);
    }

    @Test
    public void shouldUpdateContentId() {
        CourseDto courseDtoToBeUpdated = new CourseDto("course1", "some description", true, Collections.EMPTY_LIST);
        UUID courseContentId = UUID.randomUUID();
        CourseDto courseDtoFromDb = new CourseDto("course1", "some description", new ContentIdentifierDto(courseContentId, 1), Collections.EMPTY_LIST);

        courseUpdater.updateContentId(courseDtoToBeUpdated, courseDtoFromDb);

        assertEquals(courseContentId, courseDtoToBeUpdated.getCourseIdentifier().getContentId());
    }

    @Test
    public void shouldUpdateChildContents() throws Exception {
        List<ModuleDto> modules = asList(new ModuleDto());
        CourseDto courseDto = new CourseDto("course1", "some description", true, modules);

        courseUpdater.updateChildContents(courseDto);

        verify(moduleUpdater).update(modules);
    }

    @Test
    public void shouldGetExistingCoursesFromDbOnlyFirstTime() throws Exception {
        CourseDto courseDtoFromDb = new CourseDto("course1", "some description", true, Collections.EMPTY_LIST);
        when(courseService.getAllCourses()).thenReturn(asList(courseDtoFromDb));

        List<CourseDto> existingContents1 = courseUpdater.getExistingContents();

        verify(courseService, times(1)).getAllCourses();
        assertEquals(1, existingContents1.size());
        assertEquals(courseDtoFromDb, existingContents1.get(0));

        List<CourseDto> existingContents2 = courseUpdater.getExistingContents();

        verifyNoMoreInteractions(courseService);
        assertEquals(1, existingContents2.size());
        assertEquals(courseDtoFromDb, existingContents2.get(0));
    }

    @Test
    public void shouldEquateCoursesByName() throws Exception {
        CourseDto course1 = new CourseDto("course1", "old description", true, Collections.EMPTY_LIST);
        CourseDto course2 = new CourseDto("course1", "new description", new ContentIdentifierDto(UUID.randomUUID(), 1), Collections.EMPTY_LIST);
        assertTrue(courseUpdater.isEqual(course1, course2));

        CourseDto course3 = new CourseDto("course2", "old description", true, Collections.EMPTY_LIST);
        assertFalse(courseUpdater.isEqual(course1, course3));
    }
}
