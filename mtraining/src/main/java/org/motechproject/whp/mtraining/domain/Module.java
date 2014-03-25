package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "module", identityType = IdentityType.APPLICATION)
public class Module extends CourseContent {


    @Element(column = "module_id")
    @Order(column = "chapter_order")
    @Persistent(dependentElement = "true")
    private List<Chapter> chapters;

    public Module(String name, UUID contentId, Integer version, String description, String modifiedBy, DateTime dateModified, List<Chapter> chapters) {
        super(name, contentId, version, description, modifiedBy, dateModified);
        this.chapters = chapters;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
}
