package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;

import java.util.ArrayList;
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
    public static final String FILENAME = "file name";

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
        CourseDto courseDtoToBeUpdated = new CourseDto(true, "course1", "some description", FILENAME, "Created By", Collections.EMPTY_LIST);
        UUID courseContentId = UUID.randomUUID();
        CourseDto courseDtoFromDb = new CourseDto(courseContentId, 1, true, "course1", "some description", FILENAME, "Created By", Collections.EMPTY_LIST);

        courseUpdater.updateContentId(courseDtoToBeUpdated, courseDtoFromDb);

        assertEquals(courseContentId, courseDtoToBeUpdated.getContentId());
    }

    @Test
    public void shouldUpdateChildContents() throws Exception {
        List<ModuleDto> modules = asList(new ModuleDto());
        CourseDto courseDto = new CourseDto(true, "course1", "some description", FILENAME, "Created By", modules);

        courseUpdater.updateChildContents(courseDto);

        verify(moduleUpdater).update(modules);
    }

    @Test
    public void shouldGetExistingCoursesFromDbOnlyFirstTime() throws Exception {
        CourseDto courseDtoFromDb = new CourseDto(true, "course1", "some description", FILENAME, "Created By", Collections.EMPTY_LIST);
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
        CourseDto course1 = new CourseDto(true, "course1", "old description", FILENAME, "Created By", Collections.EMPTY_LIST);
        CourseDto course2 = new CourseDto(UUID.randomUUID(), 1, true, "course1", "new description", FILENAME, "Created By", Collections.EMPTY_LIST);
        assertTrue(courseUpdater.isEqual(course1, course2));

        CourseDto course3 = new CourseDto(true, "course2", "old description", FILENAME, "Created By", Collections.EMPTY_LIST);
        assertFalse(courseUpdater.isEqual(course1, course3));
    }

    @Test
    public void shouldInvalidateExistingContentCacheAfterUpdate() {
        final CourseDto courseDtoFromDb = new CourseDto(true, "course1", "some description", FILENAME, "Created By", Collections.EMPTY_LIST);
        when(courseService.getAllCourses()).thenReturn(new ArrayList<CourseDto>() {{
            add(courseDtoFromDb);
        }});
        assertFalse(courseUpdater.getExistingContents().isEmpty());

        courseUpdater.update(asList(new CourseDto(true, "course2", null, FILENAME, "Created By", Collections.EMPTY_LIST)));

        assertTrue(courseUpdater.getExistingContents().isEmpty());
        verify(moduleUpdater).invalidateCache();
    }
}
