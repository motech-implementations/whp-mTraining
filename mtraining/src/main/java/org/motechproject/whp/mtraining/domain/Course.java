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
public class Course extends CourseContent implements CourseContentHolder {

    @Element(column = "course_id")
    @Order(column = "module_order")
    @Persistent(dependentElement = "true")
    private List<Module> modules = new ArrayList<>();
    @Persistent
    private String description;
    @Persistent(column = "audio_file_name")
    private String externalId;


    public Course(String name, UUID courseId, Integer version, String description, String externalId, String createdBy, DateTime createdOn, List<Module> modules, boolean isActive) {
        super(name, courseId, version, createdBy, createdOn, isActive);
        this.description = description;
        this.externalId = externalId;
        this.modules = modules;
    }

    public Course(CourseDto courseDto) {
        this(courseDto.getName(), courseDto.getContentId(), courseDto.getVersion(), courseDto.getDescription(), courseDto.getExternalContentId(), courseDto.getCreatedBy(), courseDto.getCreatedOn(),
                mapToModules(courseDto.getModules()), courseDto.isActive());
    }

    private static List<Module> mapToModules(List<ModuleDto> moduleDtoList) {
        ArrayList<Module> modules = new ArrayList<>();
        if (isBlank(moduleDtoList)) {
            return modules;
        }
        for (ModuleDto moduleDto : moduleDtoList) {
            modules.add(new Module(moduleDto));
        }
        return modules;
    }

    public List<Module> getModules() {
        return modules;
    }

    public String getDescription() {
        return description;
    }

    public String getExternalId() {
        return externalId;
    }

    @Override
    public void removeInactiveContent() {
        filter(modules);
        for (Module module : modules) {
            module.removeInactiveContent();
        }
    }
}
