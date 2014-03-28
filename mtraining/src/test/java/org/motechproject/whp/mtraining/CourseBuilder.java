package org.motechproject.whp.mtraining;

import org.joda.time.DateTime;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.domain.Answer;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.Chapter;
import org.motechproject.whp.mtraining.domain.Message;
import org.motechproject.whp.mtraining.domain.Module;
import org.motechproject.whp.mtraining.domain.Question;
import org.motechproject.whp.mtraining.domain.Quiz;

import java.util.Arrays;
import java.util.UUID;

public class CourseBuilder {

    private final long passPercentage = 100l;
    private UUID contentId = UUID.randomUUID();
    private String courseName = "CS001";
    private Integer version = 1;
    private String description = "description";
    private String modifiedBy = "User In Test";
    private DateTime dateModified = ISODateTimeUtil.nowInTimeZoneUTC();
    private String audioFileName = "audio.wav";
    private String questionAudioFileName = "question audio.wav";
    private String answerAudioFileName = "answer audio.wav";
    private boolean isActive = true;

    public CourseBuilder withName(String courseName) {
        this.courseName = courseName;
        return this;
    }

    public Course build() {
        return new Course(courseName, contentId, version, description, modifiedBy, dateModified, Arrays.asList(buildModule("MOD001")), isActive);
    }

    private Module buildModule(String moduleName) {
        return new Module(moduleName, contentId, version, description, modifiedBy, dateModified, Arrays.asList(buildChapter("CH001")), isActive);
    }

    private Chapter buildChapter(String chapterName) {
        Chapter chapter = new Chapter(chapterName, contentId, version, description, modifiedBy, dateModified, Arrays.asList(buildMessage("MSG001")), isActive);
        chapter.setQuiz(buildQuiz("Test Quiz"));
        return chapter;
    }

    private Message buildMessage(String messageName) {
        return new Message(messageName, contentId, version, description, modifiedBy, dateModified, audioFileName, isActive);
    }

    private Quiz buildQuiz(String quizName) {
        Quiz quiz = new Quiz(quizName, contentId, version, modifiedBy, dateModified, passPercentage, isActive);
        quiz.addQuestion(buildQuestion("Test Question"));
        return quiz;
    }

    private Question buildQuestion(String questionName) {
        return new Question(questionName, contentId, version, description, modifiedBy, dateModified, questionAudioFileName, Arrays.asList("A", "B"), new Answer(answerAudioFileName, "A"), isActive);
    }

    public CourseBuilder withMessageAudioFile(String audioFileName) {
        this.audioFileName = audioFileName;
        return this;
    }
}
