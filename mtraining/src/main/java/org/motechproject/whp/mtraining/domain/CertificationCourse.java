package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "certification_course", identityType = IdentityType.APPLICATION, detachable = "true")
public class CertificationCourse extends CourseContent {

    @Element(column = "course_id")
    @Order(column = "module_order")
    @Persistent(dependentElement = "true")
    private List<Module> modules;

    public CertificationCourse(String name, UUID courseId, Integer version, String description, String modifiedBy, DateTime dateModified, List<Module> modules) {
        super(name, courseId, version, description, modifiedBy, dateModified);
        this.modules = modules;
    }

    public List<Module> getModules() {
        return modules;
    }
}
