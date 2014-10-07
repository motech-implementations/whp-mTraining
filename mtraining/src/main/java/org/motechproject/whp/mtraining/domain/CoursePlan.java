package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseUnitMetadata;
import org.motechproject.mtraining.domain.CourseUnitState;

import javax.jdo.annotations.Unique;
import java.util.List;

@Entity
public class CoursePlan extends CourseUnitMetadata {

    @Field
    private List<Course> courses;

    @Field
    @Unique
    private Location location;

    @Field
    private boolean isPublished;

    public CoursePlan(String name, CourseUnitState state, String content) {
        this(name, state, content, null);
    }

    public CoursePlan(String name, CourseUnitState state, String content, List<Course> courses) {
        super(name, state, content);
        this.courses = courses;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

}
