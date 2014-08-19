package org.motechproject.whp.mtraining;

import org.motechproject.mtraining.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseBuilder {

    private final Double passPercentage = 100.0;
    private String courseName = "CS001";
    private CourseUnitState isActive = CourseUnitState.Active;
    private String content = "content";
    private String answerAudioFileName = "answer audio.wav";

    public CourseBuilder withName(String courseName) {
        this.courseName = courseName;
        return this;
    }

    public Course build() {
        return new Course(courseName, isActive, content, Arrays.asList(buildChapter("CHP001")));
    }

    private Chapter buildChapter(String chapterName) {
        Chapter chapter = new Chapter(chapterName, isActive, content, Arrays.asList(buildMessage("LES001")), buildQuiz("Test Quiz"));
        chapter.setQuiz(buildQuiz("Test Quiz"));
        return chapter;
    }

    private Lesson buildMessage(String lessonName) {
        return new Lesson(lessonName, isActive, content);
    }

    private Quiz buildQuiz(String quizName) {
        List<Question> questions = new ArrayList<>();
        questions.add(buildQuestion("Test Question 1"));
        questions.add(buildQuestion("Test Question 2"));
        Quiz quiz = new Quiz(quizName, isActive, content, questions, passPercentage);
        return quiz;
    }

    private Question buildQuestion(String questionName) {
        return new Question(questionName, answerAudioFileName);
    }
}
