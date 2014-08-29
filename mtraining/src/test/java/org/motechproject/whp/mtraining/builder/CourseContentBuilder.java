package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseContentBuilder {

    private String name = "Default Chapter Name";
    private String description = "Default Chapter Description";
    private CourseUnitState state = CourseUnitState.Inactive;

    private List<ChapterDto> chapters = new ArrayList<>();

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

    public CourseContentBuilder withChapters(List<ChapterDto> chapters) {
        this.chapters.clear();
        this.chapters.addAll(chapters);
        return this;
    }

    public ModuleDto buildCourse() {
        ModuleDto course = new ModuleDto(0, name, state, null, null);
        course.setChapters(chapters);
        return course;
    }
}
