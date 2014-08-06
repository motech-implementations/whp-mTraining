package org.motechproject.whp.mtraining.dto;


import org.joda.time.DateTime;
import org.motechproject.whp.mtraining.domain.Location;

import java.util.List;

/**
 * DTO representation for CoursePlan class (WHP Course level logic)
 */
public class CoursePlanDto extends CourseUnitMetadataDto {

    private List<ModuleDto> modules;

    private Location location;

    public CoursePlanDto() {
    }

    public CoursePlanDto(Integer id, String name, String description, String state, String filename,
                         DateTime creationDate, DateTime modificationDate, List<ModuleDto> modules,
                         Location location) {
        super(id, name, description, state, filename, creationDate, modificationDate);
        this.modules = modules;
        this.location = location;
    }

    public List<ModuleDto> getCourses() {
        return modules;
    }

    public void setCourses(List<ModuleDto> modules) {
        this.modules = modules;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
