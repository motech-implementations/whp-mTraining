package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;

import java.util.List;
import java.util.Set;

/**
 * DTO representation for Course class (WHP Module level logic)
 */
public class ModuleDto extends CourseUnitMetadataDto {

    private List<ChapterDto> chapters;

    private Set<Long> parentIds;

    public ModuleDto() {
    }

    public ModuleDto(Integer id, String name, String description, CourseUnitState state, String filename,
                     DateTime creationDate, DateTime modificationDate, List<ChapterDto> chapters, Set<Long> parentIds) {
        super(id, name, description, state, filename, creationDate, modificationDate);
        this.chapters = chapters;
        this.parentIds = parentIds;
    }

    public ModuleDto(long id, String name, CourseUnitState state, DateTime creationDate, DateTime modificationDate) {
        super(id, name, state, creationDate, modificationDate);
    }

    public List<ChapterDto> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterDto> chapters) {
        this.chapters = chapters;
    }

    public Set<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(Set<Long> parentIds) {
        this.parentIds = parentIds;
    }
}
