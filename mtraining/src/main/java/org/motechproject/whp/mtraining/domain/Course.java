package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "course", identityType = IdentityType.APPLICATION, detachable = "true")
public class Course extends CourseContent {

    @Element(column = "course_id")
    @Order(column = "module_order")
    @Persistent(dependentElement = "true")
    private List<Module> modules = new ArrayList<>();

    public Course(String name, UUID courseId, Integer version, String description, String modifiedBy, DateTime dateModified, List<Module> modules, boolean isActive) {
        super(name, courseId, version, description, modifiedBy, dateModified, isActive);
        this.modules = modules;
    }

    public Course(CourseDto courseDto) {
        super(courseDto.getName(), courseDto.getContentId(), courseDto.getVersion(), courseDto.getDescription(), courseDto.getCreatedBy(), courseDto.getCreatedOn(), courseDto.isActive());
        for (ModuleDto moduleDto : courseDto.getModules()) {
            modules.add(new Module(moduleDto));
        }
    }

    public List<Module> getModules() {
        return modules;
    }
}
