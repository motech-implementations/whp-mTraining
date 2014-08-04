package org.motechproject.whp.mtraining.service.impl;

import org.joda.time.DateTime;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.repository.FlagDataService;
import org.motechproject.whp.mtraining.service.FlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for flags
 */
@Service("flagService")
public class FlagServiceImpl implements FlagService{

    @Autowired
    private FlagDataService flagDataService;

    /**
     * Create a flag fora user
     * @param flag flag object to save
     * @return flag object created in the store
     */
    @Override
    public Flag createFlag(Flag flag) {
        return flagDataService.create(flag);
    }

    /**
     * Update the given flag gor the user
     * @param flag flag to update
     * @return updated flag object
     */
    @Override
    public Flag updateFlag(Flag flag) {
        return flagDataService.update(flag);
    }

    /**
     * get flag by flag id
     * @param flagId id of the flag
     * @return flag object with the id
     */
    @Override
    public Flag getFlagById(long flagId) {
        return flagDataService.findFlagById(flagId);
    }

    /**
     * Get the latest flag for the user identified by externalId
     * @param externalId external tracking id for the user
     * @return flag object for the user
     */
    @Override
    public Flag getLatestFlagByUserId(String externalId) {
        Flag toReturn = null;
        DateTime latestTimestamp = null;
        List<Flag> flags = flagDataService.findFlagForUser(externalId);

        for (Flag current : flags) {
            DateTime flagDate = (DateTime) flagDataService.getDetachedField(current, "modificationDate");
            if (toReturn == null) {
                toReturn = current;
                latestTimestamp = flagDate;
            } else {

                if (flagDate.isAfter(latestTimestamp)) {
                    toReturn = current;
                    latestTimestamp = flagDate;
                }
            }
        }
        return toReturn;
    }

    /**
     * Get all the glags for a user
     * @param externalId external tracking id for the user
     * @return list of flags for user
     */
    @Override
    public List<Flag> getAllFlagsForUser(String externalId) {
        return flagDataService.findFlagForUser(externalId);
    }

    /**
     * Delete a flag with the given id
     * @param flagId id of the flag
     */
    @Override
    public void deleteFlag(long flagId) {
        flagDataService.delete("id", flagId);
    }

    /**
     * Delete all flags for a given user
     * @param externalId external tracking id for the user
     */
    @Override
    public void deleteAllFlagsForUser(String externalId) {
        for (Flag current : getAllFlagsForUser(externalId)) {
            flagDataService.delete(current);
        }

    }
}
