package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.UUID;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class CourseContent {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String name;
    @Persistent(column = "content_id")
    private UUID contentId;
    @Persistent
    private Integer version;
    @Persistent
    private String description;
    @Persistent(column = "modified_by")
    private String modifiedBy;
    @Persistent(column = "date_modified")
    private DateTime dateModified;

    protected CourseContent(String name, UUID contentId, Integer version, String description, String modifiedBy, DateTime dateModified) {
        this.name = name;
        this.contentId = contentId;
        this.version = version;
        this.description = description;
        this.modifiedBy = modifiedBy;
        this.dateModified = dateModified;
    }

    public String getName() {
        return name;
    }
}
