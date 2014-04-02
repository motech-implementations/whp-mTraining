package org.motechproject.whp.mtraining.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.collections.CollectionUtils.find;

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
    @Persistent(column = "created_by")
    private String createdBy;
    @Persistent(column = "created_on")
    private DateTime createdOn;
    @Persistent(column = "is_active")
    private boolean isActive;

    protected CourseContent(String name, UUID contentId, Integer version, String createdBy, DateTime createdOn, boolean isActive) {
        this.name = name;
        this.contentId = contentId;
        this.version = version;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public boolean isActive() {
        return isActive;
    }

    public Integer getVersion() {
        return version;
    }

    public UUID getContentId() {
        return contentId;
    }


    protected static <T> boolean isBlank(Collection<T> contents) {
        return CollectionUtils.isEmpty(contents);
    }

    public static <T> void filter(Collection<T> contents) {
        CollectionUtils.filter(contents, new ActiveContentPredicate());
    }

    public static <T extends CourseContent> CourseContent getNextContent(List<T> contents, final UUID contentId) {
        T content = (T) findContentByContentId(contents, contentId);
        if (content == null) return null;
        int currentModuleIndex = contents.indexOf(content);
        if (currentModuleIndex < contents.size()-1)
            return contents.get(currentModuleIndex+1);
        return null;
    }

    public static <T extends CourseContent> Object findContentByContentId(List<T> contents, final UUID contentId) {
        return find(contents, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                T question = (T) o;
                return contentId.equals(question.getContentId());
            }
        });
    }


}
