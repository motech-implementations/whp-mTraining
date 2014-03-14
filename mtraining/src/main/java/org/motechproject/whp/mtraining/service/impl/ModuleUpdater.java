package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModuleUpdater extends Updater<ModuleDto> {

    private ModuleService moduleService;
    private ChapterUpdater chapterUpdater;
    private List<ModuleDto> existingModules;

    @Autowired
    public ModuleUpdater(ModuleService moduleService, ChapterUpdater chapterUpdater) {
        this.moduleService = moduleService;
        this.chapterUpdater = chapterUpdater;
        this.existingModules = new ArrayList<>();
    }

    @Override
    protected void updateContentId(ModuleDto moduleDto, ModuleDto existingModuleDto) {
       moduleDto.setContentId(existingModuleDto.getContentId());
    }

    @Override
    protected  void updateChildContents(ModuleDto moduleDto){
        chapterUpdater.update(moduleDto.getChapters());
    }

    @Override
    protected List<ModuleDto> getExistingContents() {
        if (existingModules.isEmpty()) {
            existingModules = moduleService.getAllModules();
        }
        return existingModules;

    }

    @Override
    protected boolean isEqual(ModuleDto moduleDto1, ModuleDto moduleDto2) {
        return moduleDto1.getName().equalsIgnoreCase(moduleDto2.getName());
    }

    public void invalidateCache() {
        existingModules.clear();
        chapterUpdater.invalidateCache();
    }
}
