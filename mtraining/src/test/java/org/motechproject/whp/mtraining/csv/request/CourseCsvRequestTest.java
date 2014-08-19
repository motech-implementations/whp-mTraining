package org.motechproject.whp.mtraining.csv.request;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.mtraining.domain.CourseUnitState;

import static org.junit.Assert.*;

public class CourseCsvRequestTest {

    @Test
    public void testThatNodeIsACourse() throws Exception {
        CourseCsvRequest course = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Active, null, "Message Description", null);
        assertThat(course.isCourse(), Is.is(true));

        CourseCsvRequest chapter = new CourseCsvRequest("Basic TB Symptoms", "Chapter", CourseUnitState.Active, null, "Message Description", null);
        assertThat(chapter.isCourse(), Is.is(false));

        CourseCsvRequest invalidNode = new CourseCsvRequest("Basic TB Symptoms", null, CourseUnitState.Active, null, "Message Description", null);
        assertThat(invalidNode.isCourse(), Is.is(false));
    }

    @Test
    public void testThatNodeIsLesson() throws Exception {
        CourseCsvRequest lesson = new CourseCsvRequest("Basic TB Symptoms", "Lesson", CourseUnitState.Active, "parentNode", "Message Description", null);
        assertThat(lesson.isLesson(), Is.is(true));

        CourseCsvRequest course = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Active, null, "Message Description", null);
        assertThat(course.isLesson(), Is.is(false));

        CourseCsvRequest invalidNode = new CourseCsvRequest("Basic TB Symptoms", null, CourseUnitState.Active, null, "Message Description", null);
        assertThat(invalidNode.isLesson(), Is.is(false));

    }

    @Test
    public void shouldIndicateIfLessonHasAFileName() throws Exception {
        CourseCsvRequest lessonWithFile = new CourseCsvRequest("Basic TB Symptoms", "Lesson", CourseUnitState.Active, "parentNode", "Message Description", "some_file.wav");
        assertThat(lessonWithFile.hasFileName(), Is.is(true));

        CourseCsvRequest lessonWithoutFile = new CourseCsvRequest("Basic TB Symptoms", "Lesson", CourseUnitState.Active, "parentNode", "Message Description", null);
        assertThat(lessonWithoutFile.hasFileName(), Is.is(false));
    }

    @Test
    public void testThatNodeHasAParentNode() throws Exception {
        CourseCsvRequest course = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Active, "parentNode", "Message Description", null);
        assertThat(course.hasParent(), Is.is(true));
    }

    @Test
    public void testThatNodeHasValidStatus() {
        CourseCsvRequest courseWithActiveStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Active, "parentNode", "Message Description", null);
        assertTrue(courseWithActiveStatus.isValidStatus());

        CourseCsvRequest courseWithInActiveStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Inactive, "parentNode", "Message Description", null);
        assertTrue(courseWithInActiveStatus.isValidStatus());

        CourseCsvRequest courseWithPendingStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Pending, "parentNode", "Message Description", null);
        assertTrue(courseWithPendingStatus.isValidStatus());

        CourseCsvRequest courseWithNullStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", null, "parentNode", "Message Description", null);
        assertFalse(courseWithNullStatus.isValidStatus());
    }

    @Test
    public void testThatNodeHasInactiveStatus(){
        CourseCsvRequest courseWithActiveStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Active, "parentNode", "Message Description", null);
        assertFalse(courseWithActiveStatus.isInActive());

        CourseCsvRequest courseWithPendingStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Pending, "parentNode", "Message Description", null);
        assertFalse(courseWithPendingStatus.isInActive());

        CourseCsvRequest courseWithInActiveStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", CourseUnitState.Inactive, "parentNode", "Message Description", null);
        assertTrue(courseWithInActiveStatus.isInActive());
    }

}
