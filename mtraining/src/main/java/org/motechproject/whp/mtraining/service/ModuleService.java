package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;

import java.util.List;

/**
 * Service Interface that exposes APIs to modules
 */
public interface ModuleService {

    /**
     * Add or update a  module
     * @param moduleDto
     * @return
     */
    ContentIdentifierDto addOrUpdateModule(ModuleDto moduleDto);

    /**
     * Retrieve a module given the module identifier
     * @param moduleIdentifier
     * @return
     */
    ModuleDto getModule(ContentIdentifierDto moduleIdentifier);

    /**
     * Retrieve all the modules
     * @return
     */
    List<ModuleDto> getAllModules();
}
