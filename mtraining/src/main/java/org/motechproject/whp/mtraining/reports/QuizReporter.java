package org.motechproject.whp.mtraining.reports;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.builder.BuilderHelper;
import org.motechproject.whp.mtraining.builder.FlagBuilder;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.dto.*;
import org.motechproject.whp.mtraining.exception.InvalidQuestionException;
import org.motechproject.whp.mtraining.exception.InvalidQuizException;
import org.motechproject.whp.mtraining.reports.domain.QuestionAttempt;
import org.motechproject.whp.mtraining.reports.domain.QuizAttempt;
import org.motechproject.whp.mtraining.service.CourseProgressService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.service.FlagService;
import org.motechproject.whp.mtraining.service.QuestionAttemptService;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.QuestionRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.motechproject.whp.mtraining.web.domain.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.statusFor;

@Component
public class QuizReporter {

    @Autowired
    private DtoFactoryService dtoFactoryService;

    @Autowired
    private CourseProgressService courseProgressService;

    @Autowired
    private FlagService flagService;

    @Autowired
    private QuestionAttemptService questionAttemptService;

    @Autowired
    private FlagBuilder flagBuilder;

    private Logger LOGGER = LoggerFactory.getLogger(QuizReporter.class);

    public MotechResponse processAndLogQuiz(String callerId, QuizReportRequest quizReportRequest) {
        QuizResultSheetDto quizResult;
        ValidationError error = validateQuizReportRequest(quizReportRequest);
        if (error != null)
            return new BasicResponse(quizReportRequest.getCallerId(), quizReportRequest.getSessionId(), quizReportRequest.getUniqueId(), statusFor(error.getErrorCode()));
        try {
            quizResult = dtoFactoryService.gradeQuiz(toQuizAnswerSheetDto(callerId, quizReportRequest));
            logQuizResult(callerId, quizReportRequest, quizResult);
        } catch (InvalidQuestionException ex) {
            return new BasicResponse(quizReportRequest.getCallerId(), quizReportRequest.getSessionId(), quizReportRequest.getUniqueId(), ResponseStatus.INVALID_QUESTION);
        } catch (InvalidQuizException ex) {
            return new BasicResponse(quizReportRequest.getCallerId(), quizReportRequest.getSessionId(), quizReportRequest.getUniqueId(), ResponseStatus.QUIZ_NOT_FOUND);
        }
        updateBookmark(callerId, quizResult, quizReportRequest);
        return new QuizReportResponse(quizReportRequest.getCallerId(), quizReportRequest.getSessionId(), quizReportRequest.getUniqueId(),
                quizResult.getScore(), quizResult.isPassed(), ResponseStatus.OK);

    }

    private ValidationError validateQuizReportRequest(QuizReportRequest quizReportRequest) {
        CoursePlanDto course = (CoursePlanDto) dtoFactoryService.getDtoByContentId(quizReportRequest.getCourse().getContentId(), CoursePlanDto.class);
        if (course == null) {
            LOGGER.error(String.format("No course found for courseId %s and version %s", quizReportRequest.getModule().getContentId(), quizReportRequest.getModule().getVersion()));
            return new ValidationError(ResponseStatus.INVALID_COURSE);
        }
        ModuleDto module = (ModuleDto) dtoFactoryService.getDtoByContentId(quizReportRequest.getModule().getContentId(), ModuleDto.class);
        if (module == null) {
            LOGGER.error(String.format("No module found for moduleId %s and version %s", quizReportRequest.getModule().getContentId(), quizReportRequest.getModule().getVersion()));
            return new ValidationError(ResponseStatus.INVALID_MODULE);
        }
        ChapterDto chapter = (ChapterDto) dtoFactoryService.getDtoByContentId(quizReportRequest.getChapter().getContentId(), ChapterDto.class);
        if (chapter == null) {
            LOGGER.error(String.format("No chapter found for chapterId %s and version %s", quizReportRequest.getChapter().getContentId(), quizReportRequest.getChapter().getVersion()));
            return new ValidationError(ResponseStatus.INVALID_CHAPTER);
        }
        chapter = dtoFactoryService.getChapterDtoWithQuiz(chapter.getId());
        if (chapter.getQuiz() != null && !chapter.getQuiz().getContentId().equals(UUID.fromString(quizReportRequest.getQuiz().getContentId()))) {
            LOGGER.error(String.format("No quiz found for quizId %s and version %s", quizReportRequest.getQuiz().getContentId(), quizReportRequest.getQuiz().getVersion()));
            return new ValidationError(ResponseStatus.QUIZ_NOT_FOUND);
        }
        return null;

    }

    private void logQuizResult(String callerId, QuizReportRequest quizReportRequest, QuizResultSheetDto quizResult) {
        QuizAttempt quizAttempt = new QuizAttempt(callerId, quizReportRequest.getCallerId(), quizReportRequest.getUniqueId(),
                quizReportRequest.getSessionId(), quizReportRequest.getCourse(), quizReportRequest.getModule(),
                quizReportRequest.getChapter(), quizReportRequest.getQuiz(), DateTime.parse(quizReportRequest.getStartTime()), DateTime.parse(quizReportRequest.getEndTime()),
                quizResult.isPassed(), quizResult.getScore(), quizReportRequest.IsIncompleteAttempt());
        List<QuestionAttempt> questionHistories = new ArrayList<>();
        for (QuestionResultDto questionResultDto : quizResult.getQuestionResultDtos()) {
            QuestionRequest questionRequest = (QuestionRequest) findContentByContentId(quizReportRequest.getQuestionRequests(), questionResultDto.getQuestionIdentifier().getContentId());
            questionHistories.add(new QuestionAttempt(quizAttempt, questionResultDto.getQuestionIdentifier(),
                    StringUtils.join(questionRequest.getInvalidInputs(), ';'), questionRequest.getSelectedOption(), questionResultDto.isCorrect(), questionRequest.getInvalidAttempt(), questionRequest.getTimeOut()));
        }
        for (QuestionAttempt questionAttempt : questionHistories) {
            questionAttemptService.createQuestionAttempt(questionAttempt);
        }
    }

    private void updateBookmark(String callerId, QuizResultSheetDto quizResult, QuizReportRequest quizReportRequest) {
        CourseProgress courseProgressForEnrollee;
        CoursePlanDto courseDto = (CoursePlanDto) dtoFactoryService.getDtoByContentId(quizReportRequest.getCourse().getContentId(), CoursePlanDto.class);
        ModuleDto moduleDto = (ModuleDto) dtoFactoryService.getDtoByContentId(quizReportRequest.getModule().getContentId(), ModuleDto.class);
        ChapterDto chapterDto = (ChapterDto) dtoFactoryService.getDtoByContentId(quizReportRequest.getChapter().getContentId(), ChapterDto.class);
        if (quizReportRequest.IsIncompleteAttempt()) {
            Flag bookmarkForQuizOfAChapter = flagBuilder.buildFlagFromFirstActiveMetadata(callerId, courseDto, moduleDto, chapterDto);
            courseProgressForEnrollee = getCourseProgress(callerId, UUID.fromString(quizReportRequest.getCourse().getContentId()));
            courseProgressForEnrollee.setFlag(bookmarkForQuizOfAChapter);
            if (courseProgressForEnrollee.getId() != 0) {
                courseProgressService.updateCourseProgress(courseProgressForEnrollee);
            } else {
                courseProgressService.createCourseProgress(courseProgressForEnrollee);
            }
            LOGGER.info("Quiz Result Request posted with an incomplete attempt. Hence Adding/Updating Course Progress.");
            return;
        }
        if (quizResult.isPassed()) {
            // get next active chapter
            ChapterDto nextChapter = null;
            CoursePlanDto courseWithChildren = dtoFactoryService.getCourseDtoWithChildCollections(courseDto.getId());
            Boolean nextActive = false;
            for (ModuleDto module : courseWithChildren.getModules()) {
                if (module.getId() == moduleDto.getId()) {
                    BuilderHelper.getNextActive(chapterDto, module.getChapters());
                }
            }
            Flag nextBookmark = flagBuilder.buildFlagFromFirstActiveMetadata(callerId, courseDto, moduleDto, nextChapter);
            if (nextBookmark == null || nextBookmark.getModuleIdentifier() == null && nextBookmark.getChapterIdentifier() == null) {
                courseProgressService.markCourseAsComplete(Long.valueOf(callerId), quizReportRequest.getStartTime(), courseDto.getContentId().toString());
                LOGGER.info("Quiz Result Request posted with a passed attempt on last quiz of course. Hence marking Course Progress as complete.");
                return;
            }
            courseProgressForEnrollee = getCourseProgress(callerId, UUID.fromString(quizReportRequest.getCourse().getContentId()));
            courseProgressForEnrollee.setFlag(nextBookmark);
            if (courseProgressForEnrollee.getId() != 0) {
                courseProgressService.updateCourseProgress(courseProgressForEnrollee);
            } else {
                courseProgressService.createCourseProgress(courseProgressForEnrollee);
            }
            LOGGER.info("Quiz Result Request posted with a passed attempt. Hence setting to next Bookmark in Course Progress.");
            return;
        }
        Flag flag = flagBuilder.buildFlagFromFirstActiveMetadata(callerId, courseDto, moduleDto, chapterDto);
        if (flag != null) {
            flagService.createFlag(flagBuilder.buildFlagFromFirstActiveMetadata(callerId, courseDto, moduleDto, chapterDto));
            LOGGER.info("Quiz Result Request posted with a failed attempt. Hence setting to first content of chapter in Bookmark.");
        } else {
            LOGGER.info("Could not create flag for failed attempt");
        }

    }

    private CourseProgress getCourseProgress(String callerId, UUID courseContentId) {
        return courseProgressService.getCourseProgressForProvider(Long.valueOf(callerId));
    }

    private QuizAnswerSheetDto toQuizAnswerSheetDto(String callerId, QuizReportRequest quizReportRequest) {
        return new QuizAnswerSheetDto(callerId, quizReportRequest.getQuiz(), toAnswerSheetDtos(quizReportRequest.getQuestionRequests()));
    }

    private List<AnswerSheetDto> toAnswerSheetDtos(List<QuestionRequest> questionRequests) {

        List<AnswerSheetDto> answerSheetDtos = new ArrayList<>();
        for (QuestionRequest questionRequest : questionRequests) {
            answerSheetDtos.add(new AnswerSheetDto(new ContentIdentifier(questionRequest.getQuestionId(), questionRequest.getVersion()), questionRequest.getSelectedOption()));
        }
        return answerSheetDtos;
    }

    private static Object findContentByContentId(List<QuestionRequest> contents, final String contentId) {
        return find(contents, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                QuestionRequest questionRequest = (QuestionRequest) o;
                return contentId.equals(questionRequest.getQuestionId());
            }
        });
    }
}