package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.domain.CourseConfiguration;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.whp.mtraining.csv.request.CourseConfigurationRequest;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.valueOf;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class CourseImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseImportService.class);

    @Autowired
    private MTrainingService mTrainingService;

    @Autowired
    private CourseConfigurationService courseConfigurationService;

    @Autowired
    private MotechUserService motechUserService;

    public Course importCourse(List<CourseCsvRequest> requests) {
        Course course = formCourse(requests);

        if (mTrainingService.getCourseById(course.getId()) == null) {
            return mTrainingService.createCourse(course);
        } else {
            return mTrainingService.updateCourse(course);
        }
    }

    public void importCourseConfig(List<CourseConfigurationRequest> requests) {
        for (CourseConfigurationRequest request : requests) {
            CourseConfiguration courseConfiguration = new CourseConfiguration(request.getCourseName(),
                   valueOf(request.getCourseDurationInDays()), new Location(request.getBlock(), request.getDistrict(), request.getState()));
            if (courseConfigurationService.getCourseConfigurationById(courseConfiguration.getId()) == null) {
                courseConfigurationService.createCourseConfiguration(courseConfiguration);
            } else {
                courseConfigurationService.updateCourseConfiguration(courseConfiguration);
            }
        }
    }

    private Course formCourse(List<CourseCsvRequest> requests) {
        CourseCsvRequest courseRequest = requests.get(0);
        Course course = new Course(courseRequest.getNodeName(), courseRequest.getStatus(), courseRequest.getFileName());

        Map<Chapter, CourseCsvRequest> chapters = new HashMap<>();
        Map<Lesson, CourseCsvRequest> lessons = new HashMap<>();
        Map<Question, CourseCsvRequest> questions = new HashMap<>();
        for (CourseCsvRequest request : requests) {
            String type = request.getNodeType();
            if (type.contentEquals("Chapter")) {
                Chapter chapter = new Chapter(request.getNodeName(), request.getStatus(), request.getFileName(), new ArrayList<Lesson>());
                chapters.put(chapter, request);
            } else if (type.contentEquals("Message") || type.contentEquals("Lesson")) {
                Lesson lesson = new Lesson(request.getNodeName(), request.getStatus(), request.getFileName());
                lessons.put(lesson, request);
            } else if (type.contentEquals("Question")) {
                Question question = new Question(request.getFileName(), request.getCorrectAnswerFileName());
                questions.put(question, request);
            }
        }

        for(Map.Entry<Chapter, CourseCsvRequest> chapter : chapters.entrySet()) {
            for(Map.Entry<Lesson, CourseCsvRequest> lesson : lessons.entrySet()) {
                if (lesson.getValue().getParentNode().contentEquals(chapter.getKey().getName())) {
                   chapter.getKey().getLessons().add(lesson.getKey());
                }
            }
            String noOfQuizQuestions = chapter.getValue().getNoOfQuizQuestions();
            Integer numberOfQuizQuestions = isBlank(noOfQuizQuestions) ? 0 : Integer.parseInt(noOfQuizQuestions);
            if(numberOfQuizQuestions > 0) {
                Quiz quiz = new Quiz();
                quiz.setPassPercentage(Double.valueOf(chapter.getValue().getPassPercentage()));
                quiz.setQuestions(new ArrayList<Question>());
                for(Map.Entry<Question, CourseCsvRequest> question : questions.entrySet()) {
                    if (question.getValue().getParentNode().contentEquals(chapter.getKey().getName())) {
                        quiz.getQuestions().add(question.getKey());
                        quiz.setName(question.getValue().getNodeName());
                    }
                }
            chapter.getKey().setQuiz(quiz);
            }
        }
        course.setChapters(new ArrayList<Chapter>(chapters.keySet()));

        return course;
    }

}