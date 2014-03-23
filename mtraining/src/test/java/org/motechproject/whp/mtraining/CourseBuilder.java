package org.motechproject.whp.mtraining;

import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.ArrayList;
import java.util.List;

public class CourseBuilder {

    private final boolean isActive = true;
    private String name = "CS001";
    private String desc = "Desc";

    private String fileNameExternalId = "ch001.wav";


    public CourseDto build() {
        return new CourseDto(isActive, "CS001", desc, buildModules());
    }

    private List<ModuleDto> buildModules() {
        List<ModuleDto> modules = new ArrayList<>();

        ModuleDto module = new ModuleDto(isActive, "m01", desc, buildChapters());
        modules.add(module);
        return modules;
    }

    private List<ChapterDto> buildChapters() {
        List<ChapterDto> chapters = new ArrayList<>();
        ChapterDto chapterDto = new ChapterDto(isActive, "ch001", desc, buildMessages(), null);
        chapters.add(chapterDto);
        return chapters;
    }

    private List<MessageDto> buildMessages() {
        List<MessageDto> messages = new ArrayList<>();
        MessageDto messageDto = new MessageDto(isActive, "ms001", desc, fileNameExternalId);
        messages.add(messageDto);
        return messages;
    }
}
