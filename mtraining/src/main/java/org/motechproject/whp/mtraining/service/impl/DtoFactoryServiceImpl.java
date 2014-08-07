package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.mtraining.domain.*;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.*;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.CoursePlanService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("dtoFactoryService")
public class DtoFactoryServiceImpl implements DtoFactoryService {

    @Autowired
    CoursePlanService coursePlanService;

    @Autowired
    MTrainingService mTrainingService;

    @Autowired
    ContentOperationService contentOperationService;

    public static final String DESCRIPTION_MAPPING_NAME = "description";
    public static final String FILENAME_MAPPING_NAME = "filename";

    @Override
    public List<CoursePlanDto> getAllCoursePlanDtos() {
        List<CoursePlan> allCourses = coursePlanService.getAllCoursePlans();
        return convertCoursePlanListToDtos(allCourses);
    }

    @Override
    public CoursePlanDto getCoursePlanDtoById(long courseId) {
        return convertCoursePlanToDto(coursePlanService.getCoursePlanById(courseId));
    }

    @Override
    public void createOrUpdateCoursePlanFromDto(CoursePlanDto coursePlanDto) {
        CoursePlan coursePlan;
        if (coursePlanDto.getId() == 0) {
            coursePlanService.createCoursePlan(generateCoursePlanFromDto(coursePlanDto));
        } else {
            coursePlan = coursePlanService.getCoursePlanById(coursePlanDto.getId());
            coursePlan.setName(coursePlanDto.getName());
            coursePlan.setState(coursePlanDto.getState());
            coursePlan.setContent(contentOperationService.codeFileNameAndDescriptionIntoContent
                    (coursePlanDto.getFilename(), coursePlanDto.getDescription()));
            coursePlanService.updateCoursePlan(coursePlan);
        }
    }

    @Override
    public CoursePlan generateCoursePlanFromDto(CoursePlanDto coursePlanDto) {
        return new CoursePlan(coursePlanDto.getName(), coursePlanDto.getState(),
                contentOperationService.codeFileNameAndDescriptionIntoContent(coursePlanDto.getFilename(), coursePlanDto.getDescription()));
    }
    @Override
    public CoursePlanDto convertCoursePlanToDto(CoursePlan coursePlan) {
        CoursePlanDto coursePlanDto = new CoursePlanDto(coursePlan.getId(),coursePlan.getName(),coursePlan.getState(),
                coursePlan.getCreationDate(), coursePlan.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(coursePlanDto, coursePlan.getContent());

        return coursePlanDto;
    }

    @Override
    public List<CoursePlanDto> convertCoursePlanListToDtos (List<CoursePlan> coursePlans) {
        List<CoursePlanDto> coursePlanDtos = new ArrayList<>();

        for (CoursePlan coursePlan : coursePlans) {
            coursePlanDtos.add(convertCoursePlanToDto(coursePlan));
        }
        return coursePlanDtos;
    }


    @Override
    public List<ModuleDto> getAllModuleDtos() {
        List<Course> allCourses = mTrainingService.getAllCourses();
        return convertModuleListToDtos(allCourses);
    }

    @Override
    public ModuleDto getModuleDtoById(long moduleId) {
        return convertModuleToDto(mTrainingService.getCourseById(moduleId));
    }

    @Override
    public void createOrUpdateModuleFromDto(ModuleDto moduleDto) {
        Course module;
        if (moduleDto.getId() == 0) {
            mTrainingService.createCourse(generateModuleFromDto(moduleDto));
        } else {
            module = mTrainingService.getCourseById(moduleDto.getId());
            module.setName(moduleDto.getName());
            module.setState(moduleDto.getState());
            module.setContent(contentOperationService.codeFileNameAndDescriptionIntoContent
                    (moduleDto.getFilename(), moduleDto.getDescription()));
            mTrainingService.updateCourse(module);
        }
    }

    @Override
    public Course generateModuleFromDto(ModuleDto moduleDto) {
        return new Course(moduleDto.getName(), moduleDto.getState(),
                contentOperationService.codeFileNameAndDescriptionIntoContent(moduleDto.getFilename(), moduleDto.getDescription()));
    }
    @Override
    public ModuleDto convertModuleToDto(Course module) {
        ModuleDto moduleDto = new ModuleDto(module.getId(),module.getName(),module.getState(),
                module.getCreationDate(), module.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(moduleDto, module.getContent());

        return moduleDto;
    }

    @Override
    public List<ModuleDto> convertModuleListToDtos (List<Course> modules) {
        List<ModuleDto> moduleDtos = new ArrayList<>();

        for (Course module : modules) {
            moduleDtos.add(convertModuleToDto(module));
        }
        return moduleDtos;
    }


    @Override
    public List<ChapterDto> getAllChapterDtos() {
        List<Chapter> allCourses = mTrainingService.getAllChapters();
        return convertChapterListToDtos(allCourses);
    }

    @Override
    public ChapterDto getChapterDtoById(long chapterId) {
        return convertChapterToDto(mTrainingService.getChapterById(chapterId));
    }

    @Override
    public void createOrUpdateChapterFromDto(ChapterDto chapterDto) {
        Chapter chapter;
        if (chapterDto.getId() == 0) {
            mTrainingService.createChapter(generateChapterFromDto(chapterDto));
        } else {
            chapter = mTrainingService.getChapterById(chapterDto.getId());
            chapter.setName(chapterDto.getName());
            chapter.setState(chapterDto.getState());
            chapter.setContent(contentOperationService.codeFileNameAndDescriptionIntoContent
                    (chapterDto.getFilename(), chapterDto.getDescription()));
            mTrainingService.updateChapter(chapter);
        }
    }

    @Override
    public Chapter generateChapterFromDto(ChapterDto chapterDto) {
        return new Chapter(chapterDto.getName(), chapterDto.getState(),
                contentOperationService.codeFileNameAndDescriptionIntoContent(chapterDto.getFilename(), chapterDto.getDescription()));
    }
    @Override
    public ChapterDto convertChapterToDto(Chapter chapter) {
        ChapterDto chapterDto = new ChapterDto(chapter.getId(),chapter.getName(),chapter.getState(),
                chapter.getCreationDate(), chapter.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(chapterDto, chapter.getContent());

        return chapterDto;
    }

    @Override
    public List<ChapterDto> convertChapterListToDtos (List<Chapter> chapters) {
        List<ChapterDto> chapterDtos = new ArrayList<>();

        for (Chapter chapter : chapters) {
            chapterDtos.add(convertChapterToDto(chapter));
        }
        return chapterDtos;
    }


    @Override
    public List<LessonDto> getAllLessonDtos() {
        List<Lesson> allCourses = mTrainingService.getAllLessons();
        return convertLessonListToDtos(allCourses);
    }

    @Override
    public LessonDto getLessonDtoById(long lessonId) {
        return convertLessonToDto(mTrainingService.getLessonById(lessonId));
    }

    @Override
    public void createOrUpdateLessonFromDto(LessonDto lessonDto) {
        Lesson lesson;
        if (lessonDto.getId() == 0) {
            mTrainingService.createLesson(generateLessonFromDto(lessonDto));
        } else {
            lesson = mTrainingService.getLessonById(lessonDto.getId());
            lesson.setName(lessonDto.getName());
            lesson.setState(lessonDto.getState());
            lesson.setContent(contentOperationService.codeFileNameAndDescriptionIntoContent
                    (lessonDto.getFilename(), lessonDto.getDescription()));
            mTrainingService.updateLesson(lesson);
        }
    }

    @Override
    public Lesson generateLessonFromDto(LessonDto lessonDto) {
        return new Lesson(lessonDto.getName(), lessonDto.getState(),
                contentOperationService.codeFileNameAndDescriptionIntoContent(lessonDto.getFilename(), lessonDto.getDescription()));
    }
    @Override
    public LessonDto convertLessonToDto(Lesson lesson) {
        LessonDto lessonDto = new LessonDto(lesson.getId(),lesson.getName(),lesson.getState(),
                lesson.getCreationDate(), lesson.getModificationDate());

        contentOperationService.getFileNameAndDescriptionFromContent(lessonDto, lesson.getContent());

        return lessonDto;
    }

    @Override
    public List<LessonDto> convertLessonListToDtos (List<Lesson> lessons) {
        List<LessonDto> lessonDtos = new ArrayList<>();

        for (Lesson lesson : lessons) {
            lessonDtos.add(convertLessonToDto(lesson));
        }
        return lessonDtos;
    }


    @Override
    public List<QuizDto> getAllQuizDtos() {
        List<Quiz> allCourses = mTrainingService.getAllQuizzes();
        return convertQuizListToDtos(allCourses);
    }

    @Override
    public QuizDto getQuizDtoById(long quizId) {
        return convertQuizToDto(mTrainingService.getQuizById(quizId));
    }

    @Override
    public void createOrUpdateQuizFromDto(QuizDto quizDto) {
        Quiz quiz;
        if (quizDto.getId() == 0) {
            mTrainingService.createQuiz(generateQuizFromDto(quizDto));
        } else {
            quiz = mTrainingService.getQuizById(quizDto.getId());
            quiz.setName(quizDto.getName());
            quiz.setState(quizDto.getState());
            quiz.setPassPercentage(quizDto.getPassPercentage());
            quiz.setQuestions(convertDtosToQuestionList(quizDto.getQuestions()));
            quiz.setContent(contentOperationService.codeFileNameAndDescriptionIntoContent
                    (quizDto.getFilename(), quizDto.getDescription()));
            mTrainingService.updateQuiz(quiz);
        }
    }

    @Override
    public Quiz generateQuizFromDto(QuizDto quizDto) {
        Quiz quiz = new Quiz(quizDto.getName(), quizDto.getState(),
                contentOperationService.codeFileNameAndDescriptionIntoContent(quizDto.getFilename(), quizDto.getDescription()));
        quiz.setPassPercentage(quizDto.getPassPercentage());
        quiz.setQuestions(convertDtosToQuestionList(quizDto.getQuestions()));
        return quiz;
    }
    @Override
    public QuizDto convertQuizToDto(Quiz quiz) {
        QuizDto quizDto = new QuizDto(quiz.getId(),quiz.getName(),quiz.getState(),
                quiz.getCreationDate(), quiz.getModificationDate(), quiz.getPassPercentage(), convertQuestionListToDtos(quiz.getQuestions()));

        contentOperationService.getFileNameAndDescriptionFromContent(quizDto, quiz.getContent());

        return quizDto;
    }

    @Override
    public List<QuizDto> convertQuizListToDtos (List<Quiz> quizzes) {
        List<QuizDto> quizDtos = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            quizDtos.add(convertQuizToDto(quiz));
        }
        return quizDtos;
    }

    @Override
    public QuestionDto convertQuestionToDto(Question question) {
        QuestionDto questionDto = new QuestionDto(question.getQuestion(), question.getAnswer());
        return questionDto;
    }

    @Override
    public List<QuestionDto> convertQuestionListToDtos (List<Question> questions) {
        List<QuestionDto> questionDtos = new ArrayList<>();

        for (Question question : questions) {
            questionDtos.add(convertQuestionToDto(question));
        }
        return questionDtos;
    }

    @Override
    public Question convertDtoToQuestion(QuestionDto questionDto) {
        Question question = new Question(questionDto.getQuestion(), questionDto.getAnswer());
        return question;
    }

    @Override
    public List<Question> convertDtosToQuestionList (List<QuestionDto> questionDtos) {
        List<Question> questions = new ArrayList<>();

        for (QuestionDto questionDto : questionDtos) {
            questions.add(convertDtoToQuestion(questionDto));
        }
        return questions;
    }
}
