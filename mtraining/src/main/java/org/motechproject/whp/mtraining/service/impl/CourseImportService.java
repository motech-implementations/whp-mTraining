package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.whp.mtraining.csv.request.CourseConfigurationRequest;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.domain.CourseConfiguration;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    private MTrainingService mTrainingService;

    @Autowired
    private ManyToManyRelationService manyToManyRelationService;

    public CourseImportService() { }

    public CourseImportService(CoursePlanService coursePlanService, CourseConfigurationService courseConfigurationService,
                               MotechUserService motechUserService, ContentOperationService contentOperationService) {
        this.coursePlanService = coursePlanService;
        this.courseConfigurationService = courseConfigurationService;
        this.motechUserService = motechUserService;
        this.contentOperationService = contentOperationService;
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

    public void importCourseStructure(List<CourseCsvRequest> requests) {
        Map<String, Long> idToNameMap = new LinkedHashMap<>();


        Map<Chapter, CourseCsvRequest> chapters = new LinkedHashMap<>();
        Map<Question, CourseCsvRequest> questions = new LinkedHashMap<>();


        for (CourseCsvRequest request : requests) {
            String type = request.getNodeType();

            if (type.equalsIgnoreCase("Course")) {
                CoursePlan coursePlan = new CoursePlan(request.getNodeName(), request.getStatus(),
                        contentOperationService.codeIntoContent(request.getFileName(), request.getDescription(), UUID.randomUUID(), 1));
                coursePlan =  coursePlanService.createCoursePlan(coursePlan);
                idToNameMap.put(coursePlan.getName(), coursePlan.getId());

            } else if (type.equalsIgnoreCase("Module")) {
                Course course = new Course(request.getNodeName(), request.getStatus(),
                        contentOperationService.codeIntoContent(request.getFileName(), request.getDescription(), UUID.randomUUID(), 1),
                        new ArrayList<Chapter>());
                course = mTrainingService.createCourse(course);
                idToNameMap.put(course.getName(), course.getId());

            } else if (type.equalsIgnoreCase("Chapter")) {
                Chapter chapter = new Chapter(request.getNodeName(), request.getStatus(),
                        contentOperationService.codeIntoContent(request.getFileName(), request.getDescription(), UUID.randomUUID(), 1),
                        new ArrayList<Lesson>());
                chapter = mTrainingService.createChapter(chapter);
                idToNameMap.put(chapter.getName(), chapter.getId());
                chapters.put(chapter, request);

            } else if (type.equalsIgnoreCase("Message") || type.equalsIgnoreCase("Lesson")) {
                Lesson lesson = new Lesson(request.getNodeName(), request.getStatus(),
                        contentOperationService.codeIntoContent(request.getFileName(), request.getDescription(), UUID.randomUUID(), 1));
                lesson = mTrainingService.createLesson(lesson);
                idToNameMap.put(lesson.getName(), lesson.getId());

            } else if (type.equalsIgnoreCase("Question")) {
                String options = request.getOptions().replace(';', ',');
                String[] strArray = options.split("\\s*,\\s*");
                Integer[] intArray = new Integer[strArray.length];

                for(int i = 0; i < strArray.length; i++) {
                    intArray[i] = Integer.valueOf(strArray[i]);
                }

                Question question = new Question(contentOperationService.codeIntoQuestion(request.getNodeName(), request.getDescription(), UUID.randomUUID()),
                        contentOperationService.codeAnswersAndFilesNamesIntoAnswer(request.getCorrectAnswer(), Arrays.asList(intArray), request.getFileName(), request.getCorrectAnswerFileName()));
                questions.put(question, request);
            }
        }
        for (CourseCsvRequest request : requests) {
            String type = request.getNodeType();
            ParentType parentType;

            if (type.equalsIgnoreCase("Module")) {
                parentType = ParentType.CoursePlan;
            } else if (type.equalsIgnoreCase("Chapter")) {
                parentType = ParentType.Course;
            } else if (type.equalsIgnoreCase("Message") || type.equalsIgnoreCase("Lesson")) {
                parentType = ParentType.Chapter;
            } else {
                continue;
            }

            Long childId = idToNameMap.get(request.getNodeName());
            Long parentId = idToNameMap.get(request.getParentNode());

            manyToManyRelationService.createRelation(new ManyToManyRelation(parentId, childId, parentType));
        }

        for(Map.Entry<Chapter, CourseCsvRequest> chapterMap : chapters.entrySet()) {
            Chapter chapter = chapterMap.getKey();
            CourseCsvRequest chapterRow = chapterMap.getValue();

            String noOfQuizQuestions = chapterRow.getNoOfQuizQuestions();
            Integer numberOfQuizQuestions = isBlank(noOfQuizQuestions) ? 0 : Integer.parseInt(noOfQuizQuestions);
            if(numberOfQuizQuestions > 0) {
                Quiz quiz = new Quiz(chapterRow.getNodeName(), chapterRow.getStatus(),
                        contentOperationService.codeIntoQuizContent(chapterRow.getDescription(), UUID.randomUUID(), 1, numberOfQuizQuestions),
                        new ArrayList<Question>(), Double.valueOf(chapterRow.getPassPercentage()));

                for(Map.Entry<Question, CourseCsvRequest> question : questions.entrySet()) {
                    if (question.getValue().getParentNode().contentEquals(chapter.getName())) {
                        quiz.getQuestions().add(question.getKey());
                        quiz.setName(chapter.getName());
                        quiz = mTrainingService.createQuiz(quiz);
                    }
                }
                chapter.setQuiz(quiz);
                manyToManyRelationService.createRelation(new ManyToManyRelation(chapter.getId(), quiz.getId(), ParentType.Chapter));
            }
        }
    }

}