package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ModuleDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "module", identityType = IdentityType.APPLICATION)
public class Module extends CourseContent {


    @Element(column = "module_id")
    @Order(column = "chapter_order")
    @Persistent(dependentElement = "true")
    private List<Chapter> chapters = new ArrayList<>();

    public Module(String name, UUID contentId, Integer version, String description, String modifiedBy, DateTime dateModified, List<Chapter> chapters, boolean isActive) {
        super(name, contentId, version, description, modifiedBy, dateModified, isActive);
        this.chapters = chapters;
    }

    public Module(ModuleDto moduleDto) {
        super(moduleDto.getName(), moduleDto.getContentId(), moduleDto.getVersion(), moduleDto.getDescription(), moduleDto.getCreatedBy(), moduleDto.getCreatedOn(), moduleDto.isActive());
        for (ChapterDto chapterDto : moduleDto.getChapters()) {
            chapters.add(new Chapter(chapterDto));
        }

    }

    public List<Chapter> getChapters() {
        return chapters;
    }


}
