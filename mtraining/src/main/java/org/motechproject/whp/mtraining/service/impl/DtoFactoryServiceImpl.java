package org.motechproject.whp.mtraining.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mtraining.domain.*;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.*;
import org.motechproject.whp.mtraining.dto.*;
import org.motechproject.whp.mtraining.exception.InvalidQuestionException;
import org.motechproject.whp.mtraining.exception.InvalidQuizException;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.LocationService;
import org.motechproject.whp.mtraining.service.ManyToManyRelationService;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.service.QuestionService;
import org.motechproject.whp.mtraining.util.ActiveContentPredicate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("dtoFactoryService")
public class DtoFactoryServiceImpl implements DtoFactoryService {

    @Autowired
    CoursePlanService coursePlanService;

    @Autowired
    CourseConfigurationService courseConfigurationService;

    @Autowired
    MTrainingService mTrainingService;

    @Autowired
    ContentOperationService contentOperationService;

    @Autowired
    private LocationService locationService;

    @Autowired
    ManyToManyRelationService manyToManyRelationService;

    @Autowired
    QuestionService questionService;

    @Autowired
    ProviderService providerService;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DtoFactoryServiceImpl.class);

    @Override
    public List<CoursePlanDto> getAllCourseDtosWithChildCollections() {
        List<CoursePlan> allCourses = coursePlanService.getAllCoursePlans();
        List<CoursePlanDto> allCoursesPlanDto = convertToCoursePlanDtos(allCourses);

        for (CoursePlanDto coursePlanDto: allCoursesPlanDto) {
            setChildCollections(coursePlanDto);
        }
        return allCoursesPlanDto;
    }

    @Override
    public CoursePlanDto getCourseDtoWithChildCollections(long courseId) {
        CoursePlan course = coursePlanService.getCoursePlanById(courseId);
        CoursePlanDto coursePlanDto = convertToCoursePlanDto(course);

        setChildCollections(coursePlanDto);

        return coursePlanDto;
    }

    @Override
    public CoursePlanDto removeInactiveCollections(CoursePlanDto course) {
        List<ModuleDto> modules = course.getModules();
        filter(modules);
        for(ModuleDto module : modules) {
            List<ChapterDto> chapters = module.getChapters();
            filter(chapters);
            for(ChapterDto chapter : chapters) {
                List<LessonDto> lessons = chapter.getLessons();
                QuizDto quiz = chapter.getQuiz();
                if (quiz != null && quiz.getState() == CourseUnitState.Inactive) {
                    chapter.setQuiz(null);
                }
                filter(lessons);
                chapter.setLessons(lessons);
            }
            module.setChapters(chapters);
        }
        course.setModules(modules);
        return course;
    }

    @Override
    public void updateCourseAndChildCollections(CoursePlanDto course) {
        List<ModuleDto> modules = course.getModules();
        for(ModuleDto module : modules) {
            List<ChapterDto> chapters = module.getChapters();
            for(ChapterDto chapter : chapters) {
                List<LessonDto> lessons = chapter.getLessons();
                for(LessonDto lesson : lessons) {
                    updateCourseUnitMetadataFromDto(lesson);
                }
                QuizDto quiz = chapter.getQuiz();
                if (quiz != null) {
                    updateCourseUnitMetadataFromDto(quiz);
                }
                updateCourseUnitMetadataFromDto(chapter);
            }
            updateCourseUnitMetadataFromDto(module);
        }
        updateCourseUnitMetadataFromDto(course);
    }

    public static <T> void filter(Collection<T> contents) {
        CollectionUtils.filter(contents, new ActiveContentPredicate());
    }

    private void setChildCollections(CoursePlanDto coursePlanDto) {
        List<ModuleDto> childModuleDtos;
        List<ChapterDto> childChapterDtos;

        childModuleDtos = convertToModuleDtos(manyToManyRelationService.getCoursesByParentId(coursePlanDto.getId()));
        for (ModuleDto moduleDto : childModuleDtos) {
            childChapterDtos = convertToChapterDtos(manyToManyRelationService.getChaptersByParentId(moduleDto.getId()));
            for (ChapterDto chapterDto : childChapterDtos) {
                chapterDto.setLessons(convertToLessonDtos(manyToManyRelationService.getLessonsByParentId(chapterDto.getId())));
                Quiz quiz = manyToManyRelationService.getQuizByParentId(chapterDto.getId());
                if (quiz != null) {
                    chapterDto.setQuiz(convertToQuizDto(quiz));
                }
            }
            moduleDto.setChapters(childChapterDtos);
        }
        coursePlanDto.setModules(childModuleDtos);
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
        if (courseUnitMetadata != null) {
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
        }
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
        List<CoursePlanDto> courses = (List<CoursePlanDto>)getDtos(coursePlanService.getAllCoursePlans());

        return courses;
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
        CoursePlanDto coursePlanDto = (CoursePlanDto) getDto(coursePlanService.getCoursePlanById(courseId));
        if (coursePlanDto != null) {
            CourseConfiguration courseConfiguration = courseConfigurationService.getCourseConfigurationByCourseId(courseId);
            coursePlanDto.setDuration(courseConfiguration.getCourseDuration());
        }
        return coursePlanDto;
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
    public ChapterDto getChapterDtoWithQuiz(long chapterId) {
        Chapter chapter = mTrainingService.getChapterById(chapterId);
        if (chapter != null) {
            ChapterDto chapterDto = new ChapterDto(chapter.getId(), chapter.getName(), chapter.getState(),
                    chapter.getCreationDate(), chapter.getModificationDate());
            chapterDto.setContentId(contentOperationService.getUuidFromJsonString(chapter.getContent()));
            Quiz quiz = manyToManyRelationService.getQuizByParentId(chapter.getId());
            if (quiz != null) {
                chapterDto.setQuiz(convertToQuizDto(quiz));
            }

            contentOperationService.getMetadataFromContent(chapterDto, chapter.getContent());

            chapterDto.setParentIds(convertToIdSet(manyToManyRelationService.getCoursesByChildId(chapter.getId())));
            return chapterDto;
        }
        return null;
    }

    @Override
    public LessonDto getLessonDtoById(long lessonId) {
        return (LessonDto) getDto(mTrainingService.getLessonById(lessonId));
    }

    @Override
    public QuizDto getQuizDtoById(long quizId) {
        return (QuizDto) getDto(mTrainingService.getQuizById(quizId));
    }

    @Override
    public QuizDto getQuizDtoByUuid(UUID uuid) {
        List<QuizDto> quizzes = getAllQuizDtos();
        for (QuizDto quiz : quizzes) {
            if (quiz.getContentId().equals(uuid)) {
                return quiz;
            }
        }
        return null;
    }

    @Override
    public void activateCourse(CoursePlanDto course) {
        course.setState(CourseUnitState.Active);
        for(ModuleDto module : course.getModules()) {
            module.setState(CourseUnitState.Active);
            createOrUpdateFromDto(module);
            for(ChapterDto chapter : module.getChapters()) {
                chapter.setState(CourseUnitState.Active);
                createOrUpdateFromDto(chapter);
                for(LessonDto lesson : chapter.getLessons()) {
                    lesson.setState(CourseUnitState.Active);
                    createOrUpdateFromDto(lesson);
                }
                QuizDto quiz = chapter.getQuiz();
                if (quiz != null) {
                    quiz.setState(CourseUnitState.Active);
                    createOrUpdateFromDto(quiz);
                }
            }
        }
        createOrUpdateFromDto(course);
    }

    private CoursePlanDto convertToCoursePlanDto(CoursePlan coursePlan) {
        CoursePlanDto coursePlanDto = new CoursePlanDto(coursePlan.getId(), coursePlan.getName(), coursePlan.getState(),
                coursePlan.getCreationDate(), coursePlan.getModificationDate());
        coursePlanDto.setContentId(contentOperationService.getUuidFromJsonString(coursePlan.getContent()));
        coursePlanDto.setLocation(coursePlan.getLocation());
        coursePlanDto.setPublished(coursePlan.isPublished());
        contentOperationService.getMetadataFromContent(coursePlanDto, coursePlan.getContent());
        CourseConfiguration configuration = courseConfigurationService.getCourseConfigurationByCourseId(coursePlan.getId());
        if (configuration == null) {
            configuration = createCourseConfiguration(coursePlanDto);
        }
        coursePlanDto.setDuration(configuration.getCourseDuration());

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
        moduleDto.setContentId(contentOperationService.getUuidFromJsonString(module.getContent()));

        contentOperationService.getMetadataFromContent(moduleDto, module.getContent());

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
        chapterDto.setContentId(contentOperationService.getUuidFromJsonString(chapter.getContent()));
        chapterDto.setLessons(convertToLessonDtos(manyToManyRelationService.getLessonsByParentId(chapter.getId())));

        contentOperationService.getMetadataFromContent(chapterDto, chapter.getContent());

        chapterDto.setParentIds(convertToIdSet(manyToManyRelationService.getCoursesByChildId(chapter.getId())));

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
        lessonDto.setContentId(contentOperationService.getUuidFromJsonString(lesson.getContent()));

        contentOperationService.getMetadataFromContent(lessonDto, lesson.getContent());

        lessonDto.setParentIds(convertToIdSet(manyToManyRelationService.getChaptersByChildId(lesson.getId())));

        return lessonDto;
    }

    private List<LessonDto> convertToLessonDtos(List<Lesson> lessons) {
        List<LessonDto> lessonDtos = new ArrayList<>();

        if (lessons != null && lessons.size() > 0) {
            for (Lesson lesson : lessons) {
                if (lesson != null) {
                    lessonDtos.add(convertToLessonDto(lesson));
                }
            }
        }
        return lessonDtos;
    }

    private boolean quizIsInRelation (long quizId) {
        return manyToManyRelationService.getChaptersByChildId(quizId).size() > 0;
    }

    private QuizDto convertToQuizDto(Quiz quiz) {
        QuizDto quizDto = new QuizDto(quiz.getId(), quiz.getName(), quiz.getState(),
                quiz.getCreationDate(), quiz.getModificationDate(), quiz.getPassPercentage(), convertToQuestionDtos(quiz.getQuestions()));
        quizDto.setContentId(contentOperationService.getUuidFromJsonString(quiz.getContent()));
        quizDto.setNoOfQuestionsToBePlayed(contentOperationService.getNoOfQuestionsToBePlayedFromJson(quiz.getContent()));
        contentOperationService.getMetadataFromContent(quizDto, quiz.getContent());

        quizDto.setParentIds(convertToIdSet(manyToManyRelationService.getChaptersByChildId(quiz.getId())));
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
        questionDto.setVersion(contentOperationService.getVersionFromJsonString(question.getQuestion()));
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
        String question = contentOperationService.codeIntoQuestion(questionDto.getName(), questionDto.getDescription(), uuid, questionDto.getVersion());
        String answer = contentOperationService.codeAnswersAndFilesNamesIntoAnswer(questionDto.getCorrectOption(), questionDto.getOptions(),
                questionDto.getExternalId(), questionDto.getExplainingAnswerFilename());
        Question questionObject = null;
        if (questionDto.getContentId() != null) {
            questionObject = questionService.getQuestionByContentId(questionDto.getContentId().toString());
        }
        if (questionObject != null) {
            questionObject.setQuestion(question);
            questionObject.setAnswer(answer);
        } else {
            questionObject = new Question(question, answer);
        }
        return questionObject;
    }

    private CourseConfiguration createCourseConfiguration(CoursePlanDto coursePlan) {
        Integer duration = (coursePlan.getDuration() != null) ? coursePlan.getDuration() : 365;
        CourseConfiguration courseConfiguration = new CourseConfiguration(coursePlan.getId(), duration, coursePlan.getLocation());
        return courseConfigurationService.createCourseConfiguration(courseConfiguration);
    }

    private void createCourseUnitMetadataFromDto(CourseUnitMetadataDto courseUnitMetadataDto) {
        if (courseUnitMetadataDto instanceof CoursePlanDto) {
            CoursePlan coursePlan = new CoursePlan(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(),
                            UUID.randomUUID(), courseUnitMetadataDto.getVersion()));
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(),
                            UUID.randomUUID(), courseUnitMetadataDto.getVersion());

            if (((CoursePlanDto) courseUnitMetadataDto).getLocation() != null) {
                Location location = locationService.getLocationById(((CoursePlanDto) courseUnitMetadataDto).getLocation().getId());
                coursePlan.setLocation(location);
            }

            coursePlan = coursePlanService.createCoursePlan(coursePlan);
            courseUnitMetadataDto.setId(coursePlan.getId());
            createCourseConfiguration((CoursePlanDto) courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof ModuleDto) {
            Course module = new Course(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(),
                            UUID.randomUUID(), courseUnitMetadataDto.getVersion()));

            module = mTrainingService.createCourse(module);
            updateRelations(module, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof ChapterDto) {
            Chapter chapter = new Chapter(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(),
                            UUID.randomUUID(), courseUnitMetadataDto.getVersion()));

            chapter = mTrainingService.createChapter(chapter);
            if (((ChapterDto) courseUnitMetadataDto).getQuiz() != null) {
                QuizDto quiz = getQuizDtoById(((ChapterDto) courseUnitMetadataDto).getQuiz().getId());
                quiz.setParentIds(new HashSet<Long>(Arrays.asList(chapter.getId())));
                updateCourseUnitMetadataFromDto(quiz);
            }
            updateRelations(chapter, courseUnitMetadataDto);
            courseUnitMetadataDto.setId(chapter.getId());

        } else if (courseUnitMetadataDto instanceof LessonDto) {
            Lesson lesson = new Lesson(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoContent(courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(),
                            UUID.randomUUID(), courseUnitMetadataDto.getVersion()));

            lesson = mTrainingService.createLesson(lesson);
            updateRelations(lesson, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof QuizDto) {
            Quiz quiz = new Quiz(courseUnitMetadataDto.getName(), courseUnitMetadataDto.getState(),
                    contentOperationService.codeIntoQuizContent(courseUnitMetadataDto.getDescription(),
                            UUID.randomUUID(), courseUnitMetadataDto.getVersion(), ((QuizDto) courseUnitMetadataDto).getNoOfQuestionsToBePlayed()),
                    convertDtosToQuestionList(((QuizDto) courseUnitMetadataDto).getQuestions()), ((QuizDto) courseUnitMetadataDto).getPassPercentage());

            quiz = mTrainingService.createQuiz(quiz);
            updateRelations(quiz, courseUnitMetadataDto);

        }
    }

    private void updateCourseUnitMetadataFromDto(CourseUnitMetadataDto courseUnitMetadataDto) {

        if (courseUnitMetadataDto instanceof CoursePlanDto) {
            CoursePlan coursePlan = coursePlanService.getCoursePlanById(courseUnitMetadataDto.getId());

            populateCourseUnitMetadataFields(coursePlan, courseUnitMetadataDto);
            if (((CoursePlanDto) courseUnitMetadataDto).getLocation() != null) {
                Location location = locationService.getLocationById(((CoursePlanDto) courseUnitMetadataDto).getLocation().getId());
                coursePlan.setLocation(location);
            } else {
                coursePlan.setLocation(null);
            }
            coursePlan.setPublished(((CoursePlanDto) courseUnitMetadataDto).isPublished());
            coursePlanService.updateCoursePlan(coursePlan);

            Integer duration = (((CoursePlanDto) courseUnitMetadataDto).getDuration() != null) ?
                ((CoursePlanDto) courseUnitMetadataDto).getDuration() : 365;
            CourseConfiguration courseConfiguration = courseConfigurationService.getCourseConfigurationByCourseId(coursePlan.getId());
            courseConfiguration.setCourseDuration(duration);
            courseConfigurationService.updateCourseConfiguration(courseConfiguration);

        } else if (courseUnitMetadataDto instanceof ModuleDto) {
            Course module = mTrainingService.getCourseById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(module, courseUnitMetadataDto);
            mTrainingService.updateCourse(module);
            updateRelations(module, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof ChapterDto) {
            Chapter chapter = mTrainingService.getChapterById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(chapter, courseUnitMetadataDto);
            mTrainingService.updateChapter(chapter);
            if (((ChapterDto) courseUnitMetadataDto).getQuiz() != null) {
                QuizDto quiz = getQuizDtoById(((ChapterDto) courseUnitMetadataDto).getQuiz().getId());
                quiz.setParentIds(new HashSet<Long>(Arrays.asList(chapter.getId())));
                updateCourseUnitMetadataFromDto(quiz);
            }
            updateRelations(chapter, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof LessonDto) {
            Lesson lesson = mTrainingService.getLessonById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(lesson, courseUnitMetadataDto);
            mTrainingService.updateLesson(lesson);
            updateRelations(lesson, courseUnitMetadataDto);

        } else if (courseUnitMetadataDto instanceof QuizDto)  {
            Quiz quiz = mTrainingService.getQuizById(courseUnitMetadataDto.getId());
            populateCourseUnitMetadataFields(quiz, courseUnitMetadataDto);
            quiz.setPassPercentage(((QuizDto) courseUnitMetadataDto).getPassPercentage());
            quiz.setQuestions(convertDtosToQuestionList(((QuizDto) courseUnitMetadataDto).getQuestions()));
            quiz = mTrainingService.updateQuiz(quiz);
            updateRelations(quiz, courseUnitMetadataDto);
        }
    }

    private void updateRelations(CourseUnitMetadata courseUnitMetadata, CourseUnitMetadataDto courseUnitMetadataDto) {
        List<ManyToManyRelation> relations = new ArrayList<>();
        if (courseUnitMetadataDto instanceof ModuleDto) {
            for (Long id : ((ModuleDto) courseUnitMetadataDto).getParentIds()) {
                relations.add(new ManyToManyRelation(id, courseUnitMetadata.getId(), ParentType.CoursePlan));
            }
        } else if (courseUnitMetadataDto instanceof ChapterDto) {
            for (Long id : ((ChapterDto) courseUnitMetadataDto).getParentIds()) {
                relations.add(new ManyToManyRelation(id, courseUnitMetadata.getId(), ParentType.Course));
            }
        } else if (courseUnitMetadataDto instanceof LessonDto) {
            for (Long id : ((LessonDto) courseUnitMetadataDto).getParentIds()) {
                relations.add(new ManyToManyRelation(id, courseUnitMetadata.getId(), ParentType.Chapter));
            }
        } else if (courseUnitMetadataDto instanceof QuizDto) {
            for (Long id : ((QuizDto) courseUnitMetadataDto).getParentIds()) {
                relations.add(new ManyToManyRelation(id, courseUnitMetadata.getId(), ParentType.Chapter));
            }
        }
        manyToManyRelationService.updateRelationsForChild(relations, courseUnitMetadata.getId());
    }

    private void populateCourseUnitMetadataFields(CourseUnitMetadata courseUnitMetadata, CourseUnitMetadataDto courseUnitMetadataDto) {
        UUID uuid = contentOperationService.getUuidFromJsonString(courseUnitMetadata.getContent());
        courseUnitMetadata.setName(courseUnitMetadataDto.getName());
        courseUnitMetadata.setState(courseUnitMetadataDto.getState());
        if (courseUnitMetadataDto instanceof QuizDto) {
            courseUnitMetadata.setContent(contentOperationService.codeIntoQuizContent
                    (courseUnitMetadataDto.getDescription(), uuid, courseUnitMetadataDto.getVersion(),
                            ((QuizDto)(courseUnitMetadataDto)).getNoOfQuestionsToBePlayed()));
        } else {
            courseUnitMetadata.setContent(contentOperationService.codeIntoContent
                    (courseUnitMetadataDto.getExternalId(), courseUnitMetadataDto.getDescription(), uuid, courseUnitMetadataDto.getVersion()));
        }
    }

    private Set<Long> convertToIdSet(List<?> courseUnitMetadata) {
        Set<Long> ids = new LinkedHashSet<Long>();
        for (Object metadata : courseUnitMetadata) {
            ids.add(((CourseUnitMetadata) metadata).getId());
        }
        return ids;
    }

    private CourseUnitMetadataDto getDtoById(Long id) {
        CourseUnitMetadataDto dto;
        if ((dto = getLessonDtoById(id)) != null || (dto = getChapterDtoById(id)) != null || (dto = getModuleDtoById(id)) != null ||
                (dto = getCoursePlanDtoById(id)) != null || (dto = getQuizDtoById(id)) != null) {
            return dto;
        }
        return null;
    }

    public void updateCourseUnitMetadataDto(CourseUnitMetadataDto dto) {
        CourseUnitMetadataDto courseUnitMetadataDto = getDtoById(dto.getId());
        String externalId = courseUnitMetadataDto.getExternalId();
        createOrUpdateFromDto(dto);
        if (dto instanceof CoursePlanDto) {
            String oldState = "", state = "";
            if (((CoursePlanDto)courseUnitMetadataDto).getLocation() != null) {
                oldState = ((CoursePlanDto)courseUnitMetadataDto).getLocation().getState();
            }
            if (((CoursePlanDto)dto).getLocation() != null) {
                state = ((CoursePlanDto)dto).getLocation().getState();
            }
            //check if changed externalId, name or location
            if ((externalId != null && !externalId.equals(dto.getExternalId())) ||
                    !courseUnitMetadataDto.getName().equals(dto.getName()) ||
                    !oldState.equals(state)) {
                increaseVersionsByChildId(dto.getId(), dto);
            }
        } else if (externalId != null && !externalId.equals(dto.getExternalId())) {
                increaseVersionsByChildId(dto.getId(), dto);
        }
    }

    @Override
    public List<Long> updateStates(Map<String, String> stateMap) {
        Set<ManyToManyRelation> allRelations = new LinkedHashSet<>();
        List<Long> updatedIds = new ArrayList<Long>();
        for (Map.Entry<String, String> entry : stateMap.entrySet()) {
            long id = Long.valueOf(entry.getKey());
            CourseUnitState state = CourseUnitState.valueOf(entry.getValue());
            CourseUnitMetadataDto dto = getDtoById(id);
            dto.setState(state);
            createOrUpdateFromDto(dto);
            List<ManyToManyRelation> relations = manyToManyRelationService.getRelationsByChildId(id);
            if (relations == null || relations.size() == 0) {
                updatedIds.addAll(increaseVersionsByChildId(id, dto));
            } else {
                allRelations.addAll(manyToManyRelationService.getRelationsByChildId(id));
            }
        }
        updatedIds.addAll(increaseVersionsByRelations(allRelations));
        return updatedIds;
    }

    private void increaseVersion(CourseUnitMetadataDto dto) {
        dto.increaseVersion();
        if (dto instanceof QuizDto) {
            List<QuestionDto> questions = ((QuizDto) dto).getQuestions();
            for (QuestionDto question : questions) {
                question.setVersion(question.getVersion() + 1);
            }
        } else if (dto instanceof CoursePlanDto) {
            providerService.resetCourseProgresses(dto.getContentId().toString());
            ((CoursePlanDto) dto).setPublished(false);
        }
        createOrUpdateFromDto(dto);
    }

    @Override
    public List<Long> increaseVersionsByRelations(Set<ManyToManyRelation> relations) {
        return increaseVersionsByRelations(relations, new ArrayList<Long>());
    }

    @Override
    public List<Long> increaseVersionsByRelations(Set<ManyToManyRelation> relations, List<Long> updatedIds) {
        for (ManyToManyRelation relation : relations) {
            long parentId = relation.getParentId();
            long childId = relation.getChildId();
            CourseUnitMetadataDto parent = null;
            CourseUnitMetadataDto child = null;
            if (relation.getParentType() == ParentType.CoursePlan) {
                parent = getCoursePlanDtoById(parentId);
                child = getModuleDtoById(childId);
                setCoursePublished(parent.getId(), false);
            } else if (relation.getParentType() == ParentType.Course) {
                parent = getModuleDtoById(parentId);
                child = getChapterDtoById(childId);
            } else {
                parent = getChapterDtoById(parentId);
                child = getLessonDtoById(childId);
                if (child == null) {
                    child = getQuizDtoById(childId);
                }
            }
            if (parent != null && !updatedIds.contains(parentId)) {
                updatedIds.add(parentId);
                Set<ManyToManyRelation> parentRelations = (relation.getParentType() == ParentType.CoursePlan) ? null :
                        new LinkedHashSet<>(manyToManyRelationService.getRelationsByChildId(parent.getId()));
                if (parentRelations != null && parentRelations.size() > 0) {
                    increaseVersionsByRelations(parentRelations);
                } else {
                    increaseVersion(parent);
                }
            }
            if (child != null && !updatedIds.contains(childId)) {
                updatedIds.add(childId);
                increaseVersion(child);
            }
        }
        return updatedIds;
    }

    private List<Long> increaseVersionsByChildId(Long id, CourseUnitMetadataDto dto) {
        List<ManyToManyRelation> relations = manyToManyRelationService.getRelationsByChildId(id);
        if (relations.size() == 0 && dto != null) {
            increaseVersion(dto);
            return Arrays.asList(dto.getId());
        } else {
            return increaseVersionsByRelations(new LinkedHashSet<ManyToManyRelation>(relations));
        }
    }

    @Override
    public QuizResultSheetDto gradeQuiz(QuizAnswerSheetDto quizAnswerSheetDto) {
        ContentIdentifier quizIdentifier = quizAnswerSheetDto.getQuizIdentifier();
        QuizDto quiz = (QuizDto) getDtoByContentId(quizIdentifier.getContentId(), QuizDto.class);
        if (quiz == null) {
            throw new InvalidQuizException(UUID.fromString(quizIdentifier.getContentId()));
        }
        List<QuestionResultDto> questionResultDtos = new ArrayList<>();
        Integer score = 0;
        for (AnswerSheetDto answerSheetDto : quizAnswerSheetDto.getAnswerSheetDtos()) {
            QuestionDto question = null;
            for (QuestionDto q : quiz.getQuestions()) {
                if (answerSheetDto.getQuestion().getContentId().equals(q.getContentId().toString())) {
                    question = q;
                }
            }
            if (question == null) {
                throw new InvalidQuestionException(UUID.fromString(quizIdentifier.getContentId()), UUID.fromString(answerSheetDto.getQuestion().getContentId()));
            }
            Boolean wasQuestionAnsweredCorrectly = question.getAnswer().getCorrectOption().equals(answerSheetDto.getSelectedOption());
            QuestionResultDto questionResultDto = new QuestionResultDto(answerSheetDto.getQuestion(), answerSheetDto.getSelectedOption(), wasQuestionAnsweredCorrectly);
            questionResultDtos.add(questionResultDto);
            if (questionResultDto.isCorrect()) {
                score++;
            }
        }
        Double percentageScored = score * 100.0 / quiz.getNoOfQuestionsToBePlayed();
        Boolean quizPassed = quiz.getPassPercentage() <= percentageScored;
        return new QuizResultSheetDto(quizAnswerSheetDto.getQuizIdentifier(), questionResultDtos, percentageScored, quizPassed);
    }

    @Override
    public CourseUnitMetadataDto getDtoByContentId(String contentId, Class<?> type) {
        if (type == CoursePlanDto.class) {
            List<CoursePlanDto> coursePlanDtos = getAllCoursePlanDtos();
            for (CoursePlanDto coursePlanDto : coursePlanDtos) {
                if (coursePlanDto.getContentId().toString().equals(contentId)) {
                    return coursePlanDto;
                }
            }
        } else if (type == ModuleDto.class) {
            List<ModuleDto> moduleDtos = getAllModuleDtos();
            for (ModuleDto moduleDto : moduleDtos) {
                if (moduleDto.getContentId().toString().equals(contentId)) {
                    return moduleDto;
                }
            }
        } else if (type == ChapterDto.class) {
            List<ChapterDto> chapterDtos = getAllChapterDtos();
            for (ChapterDto chapterDto : chapterDtos) {
                if (chapterDto.getContentId().toString().equals(contentId)) {
                    return chapterDto;
                }
            }
        } else if (type == LessonDto.class) {
            List<LessonDto> lessonDtos = getAllLessonDtos();
            for (LessonDto lessonDto : lessonDtos) {
                if (lessonDto.getContentId().toString().equals(contentId)) {
                    return lessonDto;
                }
            }
        } else if (type == QuizDto.class) {
            List<QuizDto> quizDtos = getAllQuizDtos();
            for (QuizDto quizDto : quizDtos) {
                if (quizDto.getContentId().toString().equals(contentId)) {
                    return quizDto;
                }
            }
        }
        return null;
    }

    public void setCoursePublished(long courseId, Boolean isPublished) {
        CoursePlan coursePlan = coursePlanService.getCoursePlanById(courseId);
        coursePlan.setPublished(isPublished);
        coursePlanService.updateCoursePlan(coursePlan);
    }

}
