package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;

/**
 * Includes operation over String content value
 * and other variable manipulation
 */
public interface ContentOperationService {

    String getFileNameFromContentString(String content);

    String getDescriptionFromContentString(String content);

    void getFileNameAndDescriptionFromContent(CourseUnitMetadataDto courseUnitMetadataDto, String content);

    String codeFileNameAndDescriptionIntoContent(String filename, String description);
}
