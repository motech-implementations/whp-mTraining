package org.motechproject.whp.mtraining.builder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.mtraining.domain.CourseUnitMetadata;
import org.motechproject.mtraining.domain.CourseUnitState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class BuilderHelper {

    protected static <T extends CourseUnitMetadata> T findFirstActive(List<T> collection) {
        Object match = CollectionUtils.find(collection, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CourseUnitMetadata courseUnitMetadata = (CourseUnitMetadata) object;
                return courseUnitMetadata.getState() == CourseUnitState.Active;
            }
        });
        return (T) match;
    }

    protected static <T extends CourseUnitMetadata> T findLastActive(List<T> collection) {
        List<T> copiedList = new ArrayList<>(collection);
        Collections.reverse(copiedList);
        Object match = CollectionUtils.find(copiedList, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CourseUnitMetadata courseUnitMetadata = (CourseUnitMetadata) object;
                return courseUnitMetadata.getState() == CourseUnitState.Active;
            }
        });
        return (T) match;
    }

    protected static <T extends CourseUnitMetadata> T getNextActive(T previous, List<T> collection) {
        int position = -1;
        Long id = previous.getId();
        for (int counter = 0; counter < collection.size(); counter++) {
            T element = collection.get(counter);
            if (element.getId() == id) {
                position = counter;
                break;
            }
        }
        if (position != -1 && position + 1 != collection.size()){
            return findFirstActive(collection.subList(position + 1, collection.size()));
        }
        return null;
    }
}