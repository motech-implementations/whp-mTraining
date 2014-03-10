package org.motechproject.ivr.stub;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("publishedCourseService")
public class PublishedCoursesService {

    private List<String> courses = new ArrayList<>();

    public void store(String course) {
        courses.add(course);
    }

    public List<String> all() {
        return courses;
    }

    public String latest() {
        if (courses.isEmpty()) {
            return null;
        }
        return courses.get(courses.size() - 1);
    }

    public void removeAll() {
        courses.clear();
    }
}
