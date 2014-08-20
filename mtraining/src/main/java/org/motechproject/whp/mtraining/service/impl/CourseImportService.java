package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.whp.mtraining.csv.request.CourseConfigurationRequest;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.domain.CourseConfiguration;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.valueOf;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public class CourseImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseImportService.class);

    @Autowired
    private CoursePlanService coursePlanService;

    @Autowired
    private CourseConfigurationService courseConfigurationService;

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private ContentOperationService contentOperationService;

    public CourseImportService() { }

    public CourseImportService(CoursePlanService coursePlanService, CourseConfigurationService courseConfigurationService,
                               MotechUserService motechUserService, ContentOperationService contentOperationService) {
        this.coursePlanService = coursePlanService;
        this.courseConfigurationService = courseConfigurationService;
        this.motechUserService = motechUserService;
        this.contentOperationService = contentOperationService;
    }

    public CoursePlan importCoursePlan(List<CourseCsvRequest> requests) {
        CoursePlan coursePlan = formCoursePlan(requests);

        return coursePlanService.updateCoursePlan(coursePlan);
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

    private CoursePlan formCoursePlan(List<CourseCsvRequest> requests) {
        CourseCsvRequest courseRequest = requests.get(0);
        CoursePlan coursePlan = new CoursePlan(courseRequest.getNodeName(), courseRequest.getStatus(),
                contentOperationService.codeFileNameAndDescriptionIntoContent(courseRequest.getFileName(), courseRequest.getDescription()));

        Map<Course, CourseCsvRequest> courses = new LinkedHashMap<>();
        Map<Chapter, CourseCsvRequest> chapters = new LinkedHashMap<>();
        Map<Lesson, CourseCsvRequest> lessons = new LinkedHashMap<>();
        Map<Question, CourseCsvRequest> questions = new LinkedHashMap<>();
        for (CourseCsvRequest request : requests) {
            String type = request.getNodeType();
            if (type.equalsIgnoreCase("Chapter")) {
                Chapter chapter = new Chapter(request.getNodeName(), request.getStatus(),
                        contentOperationService.codeFileNameAndDescriptionIntoContent(request.getFileName(), request.getDescription()),
                        new ArrayList<Lesson>());
                chapters.put(chapter, request);
            } else if (type.equalsIgnoreCase("Message") || type.equalsIgnoreCase("Lesson")) {
                Lesson lesson = new Lesson(request.getNodeName(), request.getStatus(),
                        contentOperationService.codeFileNameAndDescriptionIntoContent(request.getFileName(), request.getDescription()));
                lessons.put(lesson, request);
            } else if (type.equalsIgnoreCase("Question")) {
                Question question = new Question(request.getFileName(), request.getCorrectAnswerFileName());
                questions.put(question, request);
            } else if (type.equalsIgnoreCase("Module")) {
                Course course = new Course(request.getNodeName(), request.getStatus(),
                        contentOperationService.codeFileNameAndDescriptionIntoContent(request.getFileName(), request.getDescription()),
                        new ArrayList<Chapter>());
                courses.put(course, request);
            }
        }

        for(Map.Entry<Chapter, CourseCsvRequest> chapterMap : chapters.entrySet()) {
            Chapter chapter = chapterMap.getKey();
            CourseCsvRequest chapterRow = chapterMap.getValue();
            for(Map.Entry<Lesson, CourseCsvRequest> lesson : lessons.entrySet()) {
                if (lesson.getValue().getParentNode().contentEquals(chapter.getName())) {
                    chapter.getLessons().add(lesson.getKey());
                }
            }
            String noOfQuizQuestions = chapterRow.getNoOfQuizQuestions();
            Integer numberOfQuizQuestions = isBlank(noOfQuizQuestions) ? 0 : Integer.parseInt(noOfQuizQuestions);
            if(numberOfQuizQuestions > 0) {
                Quiz quiz = new Quiz();
                quiz.setPassPercentage(Double.valueOf(chapterRow.getPassPercentage()));
                quiz.setQuestions(new ArrayList<Question>());
                for(Map.Entry<Question, CourseCsvRequest> question : questions.entrySet()) {
                    if (question.getValue().getParentNode().contentEquals(chapter.getName())) {
                        quiz.getQuestions().add(question.getKey());
                        quiz.setName(question.getValue().getNodeName());
                    }
                }
                chapter.setQuiz(quiz);
            }

            for(Map.Entry<Course, CourseCsvRequest> course : courses.entrySet()) {
                if (course.getValue().getNodeName().contentEquals(chapterRow.getParentNode())) {
                    course.getKey().getChapters().add(chapter);
                }
            }
        }
        coursePlan.setCourses(new ArrayList<Course>(courses.keySet()));

        return coursePlan;
    }

}