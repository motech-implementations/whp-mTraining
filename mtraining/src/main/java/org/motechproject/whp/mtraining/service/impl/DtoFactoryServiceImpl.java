package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.domain.*;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.domain.ParentType;
import org.motechproject.whp.mtraining.dto.*;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service("dtoFactoryService")
public class DtoFactoryServiceImpl implements DtoFactoryService {

    @Autowired
    CoursePlanService coursePlanService;

    @Autowired
    MTrainingService mTrainingService;

    @Autowired
    ContentOperationService contentOperationService;

    @Autowired
    ManyToManyRelationService manyToManyRelationService;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ContentOperationServiceImpl.class);

    @Override
    public List<CoursePlanDto> getAllCourseDtosWithChildCollections() {
        List<CoursePlan> allCourses = coursePlanService.getAllCoursePlans();
        List<CoursePlanDto> allCoursesPlanDto = convertToCoursePlanDtos(allCourses);

        for (CoursePlanDto coursePlanDto: allCoursesPlanDto) {
            setChildCollections(coursePlanDto);
        }
        return allCoursesPlanDto;
    }

    private void setChildCollections(CoursePlanDto coursePlanDto) {
        List<ModuleDto> childModuleDtos;
        List<ChapterDto> childChapterDtos;

        childModuleDtos = convertToModuleDtos(manyToManyRelationService.getCoursesByParentId(coursePlanDto.getId()));
        for (ModuleDto moduleDto : childModuleDtos) {
            childChapterDtos = convertToChapterDtos(manyToManyRelationService.getChaptersByParentId(moduleDto.getId()));
            for (ChapterDto chapterDto : childChapterDtos) {
                chapterDto.setLessons(convertToLessonDtos(manyToManyRelationService.getLessonsByParentId(chapterDto.getId())));
            }
            moduleDto.setChapters(childChapterDtos);
        }
        coursePlanDto.setCourses(childModuleDtos);
    }

    @Override
    public void createOrUpdateFromDto(CourseUnitMetadataDto courseUnitMetadataDto) {
        if (courseUnitMetadataDto.getId() == 0) {
            createCourseUnitMetadataFromDto(courseUnitMetadataDto);
        } else {
            updateCourseUnitMetadataFromDto(courseUnitMetadataDto);
        }
    }

    @Override
    public CourseUnitMetadataDto getDto(CourseUnitMetadata courseUnitMetadata) {
        if (courseUnitMetadata instanceof CoursePlan)
            return convertToCoursePlanDto((CoursePlan) courseUnitMetadata);
        if (courseUnitMetadata instanceof Course)
            return convertToModuleDto((Course) courseUnitMetadata);
        if (courseUnitMetadata instanceof Lesson)
            return convertToLessonDto((Lesson) courseUnitMetadata);
        if (courseUnitMetadata instanceof Quiz)
            return convertToQuizDto((Quiz) courseUnitMetadata);
        if (courseUnitMetadata instanceof Chapter)
            return convertToChapterDto((Chapter) courseUnitMetadata);
        LOG.warn("not supported CourseUnitMetadata child");
        return null;
    }

    @Override
    public QuestionDto getDto(Question question) {
        return convertToQuestionDto(question);
    }

    @Override
    public List<?> getDtos(List<?> list) {
        if (list != null && list.size() != 0) {
            Class genericType;
            genericType = list.get(0).getClass();

            if (genericType.equals(Course.class))
                return convertToModuleDtos((List<Course>) list);
            if (genericType.equals(Lesson.class))
                return convertToLessonDtos((List<Lesson>) list);
            if (genericType.equals(Quiz.class))
                return convertToQuizDtos((List<Quiz>) list);
            if (genericType.equals(CoursePlan.class))
                return convertToCoursePlanDtos((List<CoursePlan>) list);
            if (genericType.equals(Question.class))
                return convertToQuestionDtos((List<Question>) list);
            if (genericType.equals(Chapter.class))
                return convertToChapterDtos((List<Chapter>) list);
            LOG.warn("not supported Collection type");
            return null;
        }
        return list;
    }

    @Override
    public List<CoursePlanDto> getAllCoursePlanDtos() {
        List<?> allCourses = coursePlanService.getAllCoursePlans();
        return (List<CoursePlanDto>) getDtos(allCourses);
    }

    @Override
    public List<ModuleDto> getAllModuleDtos() {
        List<Course> allCourses = mTrainingService.getAllCourses();
        return (List<ModuleDto>) getDtos(allCourses);
    }

    @Override
    public List<ChapterDto> getAllChapterDtos() {
        List<Chapter> allCourses = mTrainingService.getAllChapters();
        return (List<ChapterDto>) getDtos(allCourses);
    }

    @Override
    public List<LessonDto> getAllLessonDtos() {
        List<Lesson> allCourses = mTrainingService.getAllLessons();
        return (List<LessonDto>) getDtos(allCourses);
    }

    @Override
    public List<QuizDto> getAllQuizDtos() {
        List<Quiz> allCourses = mTrainingService.getAllQuizzes();
        return (List<QuizDto>) getDtos(allCourses);
    }

    @Override
    public CoursePlanDto getCoursePlanDtoById(long courseId) {
        return (CoursePlanDto) getDto(coursePlanService.getCoursePlanById(courseId));
    }

    @Override
    public ModuleDto getModuleDtoById(long moduleId) {
        return (ModuleDto) getDto(mTrainingService.getCourseById(moduleId));
    }

    @Override
    public ChapterDto getChapterDtoById(long chapterId) {
        return (ChapterDto) getDto(mTrainingService.getChapterById(chapterId));
    }

    @Override
    public LessonDto getLessonDtoById(long lessonId) {
        return (LessonDto) getDto(mTrainingService.getLessonById(lessonId));
    }

    @Override
    public QuizDto getQuizDtoById(long quizId) {
        return (QuizDto) getDto(mTrainingService.getQuizById(quizId));
    }

    private CoursePlanDto convertToCoursePlanDto(CoursePlan coursePlan) {
        CoursePlanDto coursePlanDto = new CoursePlanDto(coursePlan.getId(), coursePlan.getName(), coursePlan.getState(),
                coursePlan.getCreationDate(), coursePlan.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(coursePlanDto, coursePlan.getContent());

        return coursePlanDto;
    }

    private List<CoursePlanDto> convertToCoursePlanDtos(List<CoursePlan> coursePlans) {
        List<CoursePlanDto> coursePlanDtos = new ArrayList<>();

        for (CoursePlan coursePlan : coursePlans) {
            coursePlanDtos.add((CoursePlanDto) getDto(coursePlan));
        }
        return coursePlanDtos;
    }


    private ModuleDto convertToModuleDto(Course module) {
        ModuleDto moduleDto = new ModuleDto(module.getId(), module.getName(), module.getState(),
                module.getCreationDate(), module.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(moduleDto, module.getContent());

        moduleDto.setParentIds(convertToIdSet(manyToManyRelationService.getCoursePlansByChildId(module.getId())));

        return moduleDto;
    }

    private List<ModuleDto> convertToModuleDtos(List<Course> modules) {
        List<ModuleDto> moduleDtos = new ArrayList<>();

        for (Course module : modules) {
            moduleDtos.add(convertToModuleDto(module));
        }
        return moduleDtos;
    }


    private ChapterDto convertToChapterDto(Chapter chapter) {
        ChapterDto chapterDto = new ChapterDto(chapter.getId(), chapter.getName(), chapter.getState(),
                chapter.getCreationDate(), chapter.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(chapterDto, chapter.getContent());

        chapterDto.setParentIds(convertToIdSet(manyToManyRelationService.getCoursesByChildId(chapter.getId())));
        if (chapter.getQuiz() != null) {
            chapterDto.setQuiz(convertToQuizDto(chapter.getQuiz()));
        }

        return chapterDto;
    }

    private List<ChapterDto> convertToChapterDtos(List<Chapter> chapters) {
        List<ChapterDto> chapterDtos = new ArrayList<>();

        for (Chapter chapter : chapters) {
            chapterDtos.add(convertToChapterDto(chapter));
        }
        return chapterDtos;
    }


    private LessonDto convertToLessonDto(Lesson lesson) {
        LessonDto lessonDto = new LessonDto(lesson.getId(), lesson.getName(), lesson.getState(),
                lesson.getCreationDate(), lesson.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(lessonDto, lesson.getContent());

        lessonDto.setParentIds(convertToIdSet(manyToManyRelationService.getChaptersByChildId(lesson.getId())));

        return lessonDto;
    }

    private List<LessonDto> convertToLessonDtos(List<Lesson> lessons) {
        List<LessonDto> lessonDtos = new ArrayList<>();

        for (Lesson lesson : lessons) {
            lessonDtos.add(convertToLessonDto(lesson));
        }
        return lessonDtos;
    }

    private boolean quizIsInRelation (long quizId) {
        List<Chapter> chapters = mTrainingService.getAllChapters();
        for (Chapter chapter : chapters) {
            if (chapter.getQuiz() != null && chapter.getQuiz().getId() == quizId) {
                return true;
            }
        }
        return false;
    }

    private QuizDto convertToQuizDto(Quiz quiz) {
        QuizDto quizDto = new QuizDto(quiz.getId(), quiz.getName(), quiz.getState(),
                quiz.getCreationDate(), quiz.getModificationDate(), quiz.getPassPercentage(), convertToQuestionDtos(quiz.getQuestions()));

        contentOperationService.getFileNameAndDescriptionFromContent(quizDto, quiz.getContent());
        quizDto.setInRelation(quizIsInRelation(quiz.getId()));

        return quizDto;
    }

    private List<QuizDto> convertToQuizDtos(List<Quiz> quizzes) {
        List<QuizDto> quizDtos = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            quizDtos.add(convertToQuizDto(quiz));
        }
        return quizDtos;
    }

    private QuestionDto convertToQuestionDto(Question question) {
        QuestionDto questionDto = new QuestionDto();
        contentOperationService.getQuestionNameAndDescriptionFromQuestion(questionDto, question.getQuestion());
        contentOperationService.getAnswersAndFilesNamesFromAnswer(questionDto, question.getAnswer());
        questionDto.setContentId(contentOperationService.getUuidFromJsonString(question.getQuestion()));
        return questionDto;
    }

    private List<QuestionDto> convertToQuestionDtos(List<Question> questions) {
        List<QuestionDto> questionDtos = new ArrayList<>();

        for (Question question : questions) {
            questionDtos.add(convertToQuestionDto(question));
        }
        return questionDtos;
    }

    private List<Question> convertDtosToQuestionList(List<QuestionDto> questionDtos) {
        List<Question> questions = new ArrayList<>();

        for (QuestionDto questionDto : questionDtos) {
            if (questionDto.getContentId() == null) {
                questions.add(convertDtoToQuestion(questionDto, UUID.randomUUID()));
            } else {
                questions.add(convertDtoToQuestion(questionDto, questionDto.getContentId()));
            }
        }
        return questions;
    }

    private Question convertDtoToQuestion(QuestionDto questionDto, UUID uuid) {
        String question = contentOperationService.codeIntoQuestion(questionDto.getName(), questionDto.getDescription(), uuid);
        String answer = contentOperationService.codeAnswersAndFilesNamesIntoAnswer(questionDto.getCorrectAnswer(), questionDto.getOptions(),
                questionDto.getFilename(), questionDto.getExplainingAnswerFilename());
        Question questionObject = new Question(question, answer);
        return questionObject;
    }

    private void createCourseUnitMetadataFromDto(CourseUnitMetadataDto courseUnitMetadataDto) {
        if (courseUnitMetadataDto instanceof CoursePlanDto) {
            CoursePlan coursePlan = new CoursePlan(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(), UUID.randomUUID()));
            coursePlanService.createCoursePlan(coursePlan);

        } else if (courseUnitMetadataDto instanceof ModuleDto) {
            Course module = new Course(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(), UUID.randomUUID()));
            module = mTrainingService.createCourse(module);
            createRelation(module, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof ChapterDto) {
            Chapter chapter = new Chapter(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(), UUID.randomUUID()));
            if (((ChapterDto) courseUnitMetadataDto).getQuiz() != null) {
                Quiz quiz = mTrainingService.getQuizById(((ChapterDto) courseUnitMetadataDto).getQuiz().getId());
                chapter.setQuiz(quiz);
            }
            chapter = mTrainingService.createChapter(chapter);
            createRelation(chapter, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof LessonDto) {
            Lesson lesson = new Lesson(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(), UUID.randomUUID()));
            lesson = mTrainingService.createLesson(lesson);
            createRelation(lesson, courseUnitMetadataDto);

        } else {
            Quiz quiz = new Quiz(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(), UUID.randomUUID()),
                    convertDtosToQuestionList(((QuizDto) courseUnitMetadataDto).getQuestions()), ((QuizDto) courseUnitMetadataDto).getPassPercentage());
            mTrainingService.createQuiz(quiz);
        }
    }

    private void updateCourseUnitMetadataFromDto(CourseUnitMetadataDto courseUnitMetadataDto) {
        if (courseUnitMetadataDto instanceof CoursePlanDto) {
            CoursePlan coursePlan = coursePlanService.getCoursePlanById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(coursePlan, courseUnitMetadataDto);
            coursePlanService.updateCoursePlan(coursePlan);

        } else if (courseUnitMetadataDto instanceof ModuleDto) {
            Course module = mTrainingService.getCourseById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(module, courseUnitMetadataDto);
            mTrainingService.updateCourse(module);
            manyToManyRelationService.deleteRelationsByChildId(ParentType.CoursePlan, module.getId());
            createRelation(module, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof ChapterDto) {
            Chapter chapter = mTrainingService.getChapterById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(chapter, courseUnitMetadataDto);
            if (((ChapterDto) courseUnitMetadataDto).getQuiz() != null) {
                Quiz quiz = mTrainingService.getQuizById(((ChapterDto) courseUnitMetadataDto).getQuiz().getId());
                chapter.setQuiz(quiz);
            } else {
                chapter.setQuiz(null);
            }
            mTrainingService.updateChapter(chapter);
            manyToManyRelationService.deleteRelationsByChildId(ParentType.Course, chapter.getId());
            createRelation(chapter, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof LessonDto) {
            Lesson lesson = mTrainingService.getLessonById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(lesson, courseUnitMetadataDto);
            mTrainingService.updateLesson(lesson);
            manyToManyRelationService.deleteRelationsByChildId(ParentType.Chapter, lesson.getId());
            createRelation(lesson, courseUnitMetadataDto);

        } else {
            Quiz quiz = mTrainingService.getQuizById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(quiz, courseUnitMetadataDto);
            quiz.setPassPercentage(((QuizDto) courseUnitMetadataDto).getPassPercentage());
            quiz.setQuestions(convertDtosToQuestionList(((QuizDto) courseUnitMetadataDto).getQuestions()));
            mTrainingService.updateQuiz(quiz);
        }
    }

    private void createRelation(CourseUnitMetadata courseUnitMetadata, CourseUnitMetadataDto courseUnitMetadataDto) {
        if (courseUnitMetadataDto instanceof ModuleDto) {
            for (Long id : ((ModuleDto) courseUnitMetadataDto).getParentIds()) {
                ManyToManyRelation relation = new ManyToManyRelation(id, courseUnitMetadata.getId(), ParentType.CoursePlan);
                manyToManyRelationService.createRelation(relation);
            }
        } else if (courseUnitMetadataDto instanceof ChapterDto) {
            for (Long id : ((ChapterDto) courseUnitMetadataDto).getParentIds()) {
                ManyToManyRelation relation = new ManyToManyRelation(id, courseUnitMetadata.getId(), ParentType.Course);
                manyToManyRelationService.createRelation(relation);
            }
        } else if (courseUnitMetadataDto instanceof LessonDto) {
            for (Long id : ((LessonDto) courseUnitMetadataDto).getParentIds()) {
                ManyToManyRelation relation = new ManyToManyRelation(id, courseUnitMetadata.getId(), ParentType.Chapter);
                manyToManyRelationService.createRelation(relation);
            }
        }
    }

    private void populateCourseUnitMetadataFields(CourseUnitMetadata courseUnitMetadata, CourseUnitMetadataDto courseUnitMetadataDto) {
        UUID uuid = contentOperationService.getUuidFromJsonString(courseUnitMetadata.getContent());
        courseUnitMetadata.setName(courseUnitMetadataDto.getName());
        courseUnitMetadata.setState(courseUnitMetadataDto.getState());
        courseUnitMetadata.setContent(contentOperationService.codeIntoContent
                (courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(), uuid));
    }

    private Set<Long> convertToIdSet(List<?> courseUnitMetadata) {
        Set<Long> ids = new LinkedHashSet<Long>();
        for (Object metadata : courseUnitMetadata) {
            ids.add(((CourseUnitMetadata) metadata).getId());
        }
        return ids;
    }
}
