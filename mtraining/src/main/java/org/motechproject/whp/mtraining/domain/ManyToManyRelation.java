package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;

@Entity
public class ManyToManyRelation extends MdsEntity {

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

    public ManyToManyRelation() { }

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

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof ManyToManyRelation) {
            ManyToManyRelation relation = (ManyToManyRelation) object;
            return relation.getParentId() == this.parentId && relation.getChildId() == this.childId;
        }
        return false;
    }
}
