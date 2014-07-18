package org.motechproject.whp.mtraining;

import org.motechproject.whp.mtraining.dto.AnswerDto;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.CourseDto;
import org.motechproject.whp.mtraining.dto.MessageDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.dto.QuestionDto;
import org.motechproject.whp.mtraining.dto.QuizDto;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class CourseDTOBuilder {

    private final boolean isActive = true;
    private String courseName = "CS001";
    private String desc = "Desc";
    private String fileNameExternalId = "ch001.wav";
    public String contentAuthor = "createdBy";

    public CourseDTOBuilder withName(String courseName) {
        this.courseName = courseName;
        return this;
    }

    public CourseDto build() {
        return new CourseDto(isActive, courseName, desc, fileNameExternalId, contentAuthor, buildModules());
    }

    private List<ModuleDto> buildModules() {
        List<ModuleDto> modules = new ArrayList<>();

        ModuleDto module = new ModuleDto(isActive, "m01", desc, fileNameExternalId, contentAuthor, buildChapters());
        modules.add(module);
        return modules;
    }

    private List<ChapterDto> buildChapters() {
        List<ChapterDto> chapters = new ArrayList<>();
        ChapterDto chapterDto = new ChapterDto(isActive, "ch001", desc, fileNameExternalId, contentAuthor, buildMessages(), buildQuiz());
        chapters.add(chapterDto);
        return chapters;
    }

    private List<MessageDto> buildMessages() {
        List<MessageDto> messages = new ArrayList<>();
        MessageDto messageDto = new MessageDto(isActive, "ms001", desc, fileNameExternalId, contentAuthor);
        messages.add(messageDto);
        return messages;
    }

    private QuizDto buildQuiz() {
        QuestionDto questionDto1 = new QuestionDto(true, "q1", "desc", fileNameExternalId,
                new AnswerDto("1", fileNameExternalId), newArrayList("1", "2", "3"), contentAuthor);
        QuestionDto questionDto2 = new QuestionDto(true, "q2", "desc", fileNameExternalId,
                new AnswerDto("2", fileNameExternalId), newArrayList("1", "2", "3"), contentAuthor);
        QuestionDto questionDto3 = new QuestionDto(true, "q3", "desc", fileNameExternalId,
                new AnswerDto("3", fileNameExternalId), newArrayList("1", "2", "3"), contentAuthor);
        List<QuestionDto> questions = newArrayList(questionDto1, questionDto2, questionDto3);
        return new QuizDto(true, "quiz1", fileNameExternalId, questions, 3, 70.0, contentAuthor);
    }
}
