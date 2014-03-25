package org.motechproject.whp.mtraining;

import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.ArrayList;
import java.util.List;

public class CourseBuilder {

    private final boolean isActive = true;
    private String courseName = "CS001";
    private String desc = "Desc";

    private String fileNameExternalId = "ch001.wav";

    public String contentAuthor = "createdBy";


    public CourseBuilder withName(String courseName) {
        this.courseName = courseName;
        return this;
    }

    public CourseDto build() {
        return new CourseDto(isActive, courseName, desc, contentAuthor, buildModules());
    }

    private List<ModuleDto> buildModules() {
        List<ModuleDto> modules = new ArrayList<>();

        ModuleDto module = new ModuleDto(isActive, "m01", desc, contentAuthor, buildChapters());
        modules.add(module);
        return modules;
    }

    private List<ChapterDto> buildChapters() {
        List<ChapterDto> chapters = new ArrayList<>();
        ChapterDto chapterDto = new ChapterDto(isActive, "ch001", desc, contentAuthor, buildMessages(), null);
        chapters.add(chapterDto);
        return chapters;
    }

    private List<MessageDto> buildMessages() {
        List<MessageDto> messages = new ArrayList<>();
        MessageDto messageDto = new MessageDto(isActive, "ms001", desc, contentAuthor, fileNameExternalId);
        messages.add(messageDto);
        return messages;
    }
}
