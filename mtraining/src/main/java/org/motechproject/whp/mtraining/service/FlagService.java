package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.domain.Flag;

import java.util.List;

/**
 * Service interface for management of course flags for a user
 */
public interface FlagService {

    /**
     * Create a flag for a user
     * @param flag flag object to save
     * @return flag object created in the store
     */
    Flag createFlag(Flag flag);

    /**
     * Update the given flag gor the user
     * @param flag flag to update
     * @return updated flag object
     */
    Flag updateFlag(Flag flag);

    /**
     * get flag by flag id
     * @param flagId id of the flag
     * @return flag object with the id
     */
    Flag getFlagById(long flagId);

    /**
     * Get the latest flag for the user identified by externalId
     * @param externalId external tracking id for the user
     * @return flag object for the user
     */
    Flag getLatestFlagByUserId(String externalId);

    /**
     * Get all the flags for a user
     * @param externalId external tracking id for the user
     * @return list of flags for user
     */
    List<Flag> getAllFlagsForUser(String externalId);

    /**
     * Delete a flag with the given id
     * @param flagId id of the flag
     */
    void deleteFlag(long flagId);

    /**
     * Delete all flags for a given user
     * @param externalId external tracking id for the user
     */
    void deleteAllFlagsForUser(String externalId);

    /**
     * Get flag by externalId
     * @param externalId external tracking id for the user
     * @return flag object
     */
    Flag getFlagByExternalId(String externalId);

    /**
     * Given a course identifier,return the first bookmark from first active content of the course
     * If course not found then throw CourseNotFoundException
     *
     * @param externalId
     * @param courseIdentifier
     * @return
     */
    Flag getInitialFlag(String externalId, ContentIdentifier courseIdentifier);

}
