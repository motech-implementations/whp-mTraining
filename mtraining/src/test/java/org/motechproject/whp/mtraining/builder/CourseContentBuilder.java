package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.CourseUnitState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseContentBuilder {

    private String name = "Default Chapter Name";
    private String description = "Default Chapter Description";
    private CourseUnitState state = CourseUnitState.Inactive;

    private List<Chapter> chapters = new ArrayList<>();

    public CourseContentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CourseContentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public CourseContentBuilder asInactive(CourseUnitState state) {
        this.state = state;
        return this;
    }

    public CourseContentBuilder withChapters(List<Chapter> chapters) {
        this.chapters.clear();
        this.chapters.addAll(chapters);
        return this;
    }

    public Course buildCourse() {
        if (chapters.size() == 0) {
            return new Course(name, state, description);
        }
        return new Course(name, state, description, chapters);
    }
}
