package org.motechproject.whp.mtraining.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.List;

public abstract class Updater<T> {

    protected abstract void updateContentId(T object, T existingContentByName);

    protected abstract void updateChildContents(T childContents);

    protected abstract List<T> getExistingContents();

    protected abstract boolean isEqual(T object1, T object2);

    public void update(List<T> contentsToUpdate) {
        List<T> existingContents = getExistingContents();

        for (final T contentToUpdate : contentsToUpdate) {
            T existingContentByName = (T) CollectionUtils.find(existingContents, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    T existingContent = (T) object;
                    return isEqual(contentToUpdate, existingContent);
                }
            });

            if (existingContentByName != null) {
                updateContentId(contentToUpdate, existingContentByName);
            }
            updateChildContents(contentToUpdate);
        }
    }
}
