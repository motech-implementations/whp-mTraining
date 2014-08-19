package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class ManyToManyRelation {

    @Field
    private long parentId;

    @Field
    private long childId;

    @Field
    private ParentType parentType;

    public ManyToManyRelation(long parentId, long childId, ParentType parentType) {
        this.parentId = parentId;
        this.childId = childId;
        this.parentType = parentType;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public ParentType getParentType() {
        return parentType;
    }

    public void setParentType(ParentType parentType) {
        this.parentType = parentType;
    }

}
