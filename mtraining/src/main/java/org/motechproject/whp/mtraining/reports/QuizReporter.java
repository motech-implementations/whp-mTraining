package org.motechproject.whp.mtraining.reports;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.constants.CourseStatus;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.domain.Chapter;
import org.motechproject.whp.mtraining.domain.Module;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.domain.Question;
import org.motechproject.whp.mtraining.domain.Quiz;
import org.motechproject.whp.mtraining.reports.domain.QuestionHistory;
import org.motechproject.whp.mtraining.reports.domain.QuizHistory;
import org.motechproject.whp.mtraining.repository.AllQuestionHistories;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.QuestionRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.motechproject.mtraining.util.ISODateTimeUtil.nowInTimeZoneUTC;
import static org.motechproject.whp.mtraining.domain.CourseContent.filter;
import static org.motechproject.whp.mtraining.domain.CourseContent.findContentByContentId;
import static org.motechproject.whp.mtraining.domain.CourseContent.getNextContent;

@Component
public class QuizReporter {
    private BookmarkService bookmarkService;
    private ProviderService providerService;
    private CourseService courseService;
    private AllQuestionHistories allQuestionHistories;

    @Autowired
    public QuizReporter(ProviderService providerService, CourseService courseService, BookmarkService bookmarkService,
                        AllQuestionHistories allQuestionHistories) {

        this.providerService = providerService;
        this.courseService = courseService;
        this.bookmarkService = bookmarkService;
        this.allQuestionHistories = allQuestionHistories;
    }

    public MotechResponse validateAndProcessQuiz(QuizDto quizDto, QuizReportRequest quizReportRequest) {
        int score = 0;
        Boolean correctAnswer = false;
        Long callerId = quizReportRequest.getCallerId();
        String sessionId = quizReportRequest.getSessionId();
        String uniqueId = quizReportRequest.getUniqueId();
        Quiz quiz = new Quiz(quizDto);
        List<QuestionHistory> questionHistoryList = new ArrayList<>();
        for (QuestionRequest questionRequest : quizReportRequest.getQuestionRequests()) {
            Question validQuestion = (Question) findContentByContentId(quiz.getQuestions(), questionRequest.getQuestionId());
            if (validQuestion == null)
                return new BasicResponse(callerId, sessionId, uniqueId, ResponseStatus.INVALID_QUESTION);
            if (questionRequest.getSelectedOption().equalsIgnoreCase(validQuestion.getAnswer().getCorrectOption())) {
                score++;
                correctAnswer = true;
            }
            questionHistoryList.add(new QuestionHistory(null, validQuestion.getContentId(), validQuestion.getVersion(),
                    StringUtils.join(questionRequest.getInvalidInputs(), ';'), questionRequest.getSelectedOption(), correctAnswer));

        }
        Double actualPercentage = calculatePercentage(quiz.getNumberOfQuestions(), score);
        Boolean result = actualPercentage >= quiz.getPassPercentage();
        Provider provider = providerService.byCallerId(quizReportRequest.getCallerId());
        QuizHistory quizHistory = new QuizHistory(provider.getRemediId(), quizReportRequest.getCallerId(), quizReportRequest.getUniqueId(), quizReportRequest.getSessionId(),
                quizDto.getContentId(), quizDto.getVersion(), quizReportRequest.getStartTime(), quizReportRequest.getEndTime(), result, actualPercentage);
        for (QuestionHistory questionHistory : questionHistoryList) {
            questionHistory.setQuizHistoryId(quizHistory);
        }
        allQuestionHistories.bulkAdd(questionHistoryList);
        BookmarkDto bookmarkDto = result ? setNextBookmark(quizReportRequest, provider.getRemediId()) :
                resetBookmark(quizReportRequest, provider.getRemediId());
        if (bookmarkDto == null)
            bookmarkDto = new BookmarkDto(provider.getRemediId(), quizReportRequest.getCourseDto(), quizReportRequest.getModuleDto(), quizReportRequest.getChapterDto(),
                    null, quizReportRequest.getQuizDto(), ISODateTimeUtil.nowInTimeZoneUTC(), CourseStatus.COMPLETED);
        bookmarkService.addOrUpdate(bookmarkDto);
        return new QuizReportResponse(callerId, sessionId, uniqueId, actualPercentage, result, ResponseStatus.OK);
    }

    private BookmarkDto resetBookmark(QuizReportRequest quizReportRequest, String providerId) {
        CourseDto course = courseService.getCourse(quizReportRequest.getCourseDto());
        List<Module> modules = toModules(course.getModules());
        Module module = (Module) findContentByContentId(modules, quizReportRequest.getModuleDto().getContentId());
        Chapter chapter = (Chapter) findContentByContentId(module.getChapters(), quizReportRequest.getChapterDto().getContentId());
        if (isNotEmpty(chapter.getMessages()))
            return createBookmarkDto(providerId, quizReportRequest.getCourseDto(), module, chapter,
                    new ContentIdentifierDto(chapter.getMessages().get(0).getContentId(), chapter.getMessages().get(0).getVersion()), null);
        return createBookmarkDto(providerId, quizReportRequest.getCourseDto(), module, chapter,
                null, new ContentIdentifierDto(chapter.getQuiz().getContentId(), chapter.getQuiz().getVersion()));
    }

    private BookmarkDto setNextBookmark(QuizReportRequest quizReportRequest, String providerId) {
        CourseDto course = courseService.getCourse(quizReportRequest.getCourseDto());
        List<Module> modules = toModules(course.getModules());
        return createBookmark(quizReportRequest, modules, providerId);
    }

    private BookmarkDto createBookmark(QuizReportRequest quizReportRequest, List<Module> modules, String externalId) {
        filter(modules);
        Module module = (Module) findContentByContentId(modules, quizReportRequest.getModuleDto().getContentId());
        return createBookmarkForModule(quizReportRequest, modules, module, externalId);
    }

    private BookmarkDto createBookmarkForModule(QuizReportRequest quizReportRequest, List<Module> modules, Module module, String externalId) {
        if (module == null) return null;
        filter(module.getChapters());
        Chapter nextChapter;
        if (module.getContentId().equals(quizReportRequest.getModuleDto().getContentId()))
            nextChapter = (Chapter) getNextContent(module.getChapters(), quizReportRequest.getChapterDto().getContentId());
        else nextChapter = isEmpty(module.getChapters()) ? null : module.getChapters().get(0);

        if (nextChapter != null) {
            filter(nextChapter.getMessages());
            if (isEmpty(nextChapter.getMessages()))
                if (nextChapter.getQuiz() != null)
                    return createBookmarkDto(externalId, quizReportRequest.getCourseDto(), module, nextChapter,
                            null, new ContentIdentifierDto(nextChapter.getQuiz().getContentId(), nextChapter.getQuiz().getVersion()));
                else return null;
            return createBookmarkDto(externalId, quizReportRequest.getCourseDto(), module, nextChapter,
                    new ContentIdentifierDto(nextChapter.getMessages().get(0).getContentId(), nextChapter.getMessages().get(0).getVersion()),
                    null);

        }
        return createBookmarkForModule(quizReportRequest, modules, (Module) getNextContent(modules, module.getContentId()), externalId);
    }

    private BookmarkDto createBookmarkDto(String externalId, ContentIdentifierDto course, Module module, Chapter chapter, ContentIdentifierDto message, ContentIdentifierDto quiz) {
        return new BookmarkDto(externalId, course,
                new ContentIdentifierDto(module.getContentId(), module.getVersion()),
                new ContentIdentifierDto(chapter.getContentId(), chapter.getVersion()),
                message, quiz, nowInTimeZoneUTC(), CourseStatus.ONGOING);
    }

    private List<Module> toModules(List<ModuleDto> moduleDtos) {
        List<Module> modules = newArrayList();
        for (ModuleDto moduleDto : moduleDtos) {
            modules.add(new Module(moduleDto));
        }
        return modules;
    }

    private double calculatePercentage(int noOfQuestions, Integer score) {
        return (score.doubleValue() / noOfQuestions) * 100;
    }
}