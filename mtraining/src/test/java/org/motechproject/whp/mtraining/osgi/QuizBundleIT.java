package org.motechproject.whp.mtraining.osgi;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.whp.mtraining.dto.BookmarkDto;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.dto.CourseDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.dto.QuizDto;
import org.motechproject.whp.mtraining.service.BookmarkService;
import org.motechproject.whp.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.motechproject.whp.mtraining.CourseDTOBuilder;
import org.motechproject.whp.mtraining.IVRServer;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponse;
import org.motechproject.whp.mtraining.domain.test.CustomHttpResponseHandler;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.QuestionRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.motechproject.whp.mtraining.util.ISODateTimeUtil.nowAsStringInTimeZoneUTC;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.WORKING_PROVIDER;


public class QuizBundleIT extends AuthenticationAwareIT {
    PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 10);

    private List<Long> providersAdded = new ArrayList<>();

    private CourseService courseService;

    private ProviderService providerService;

    private ContentIdentifierDto courseIdentifier;

    private BookmarkService bookmarkService;


    private IVRServer ivrServer;
    private QuizService quizService;
    private Provider provider;
    private CourseDto courseDto;
    String startTime;
    String endTime;
    ModuleDto moduleDto;
    ChapterDto chapterDto;
    QuizDto quizDto;
    ContentIdentifierDto module;
    ContentIdentifierDto chapter;
    ContentIdentifierDto quiz;

    @Override
    public void onSetUp() throws IOException, InterruptedException {
        super.onSetUp();
        ivrServer = new IVRServer(8888, "/ivr-wgn").start();

        providerService = (ProviderService) getApplicationContext().getBean("providerService");
        assertNotNull(providerService);

        quizService = (QuizService) getApplicationContext().getBean("quizService");
        assertNotNull(providerService);

        courseService = (CourseService) getService("courseService");
        assertNotNull(courseService);

        bookmarkService = (BookmarkService) getService("bookmarkService");
        assertNotNull(bookmarkService);


        courseIdentifier = courseService.addOrUpdateCourse(new CourseDTOBuilder().build());
        removeAllProviders();
        provider = addProvider("remediId23", 222292L, WORKING_PROVIDER);
        courseIdentifier = courseService.addOrUpdateCourse(new CourseDTOBuilder().build());
        courseDto = courseService.getCourse(courseIdentifier);
        moduleDto = courseDto.firstActiveModule();
        chapterDto = moduleDto.findFirstActiveChapter();
        BookmarkDto bookmarkDto = new BookmarkDto("remediId23", courseDto.toContentIdentifierDto(), moduleDto.toContentIdentifierDto(),
                chapterDto.toContentIdentifierDto(), chapterDto.findFirstActiveMessage().toContentIdentifierDto(), null,
                ISODateTimeUtil.nowInTimeZoneUTC());
        bookmarkService.addOrUpdate(bookmarkDto);
        startTime = nowAsStringInTimeZoneUTC();
        endTime = nowAsStringInTimeZoneUTC();
        moduleDto = courseDto.getModules().get(0);
        chapterDto = moduleDto.getChapters().get(0);
        quizDto = chapterDto.getQuiz();
        module = moduleDto.toContentIdentifierDto();
        chapter = chapterDto.toContentIdentifierDto();
        quiz = chapterDto.getQuiz().toContentIdentifierDto();

    }

    public void testShouldReturnQuizResultResponseForQuizRequest() throws IOException, InterruptedException {
        HttpPost httpPost = (HttpPost) httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/quiz", TestContext.getJettyPort()), "POST");
        QuizReportRequest quizReportRequest = getQuizReportRequestforQuizIdAndQuestionId(quiz, quizDto.getQuestions().get(0).getContentId(), quizDto.getQuestions().get(1).getContentId());
        String quizReportAsJSON = getQuizReportAsJSON(quizReportRequest);

        httpPost.setEntity(new StringEntity(quizReportAsJSON));
        CustomHttpResponse response = httpClient.execute(httpPost, new CustomHttpResponseHandler());
        QuizReportResponse quizReportResponse = (QuizReportResponse) responseToJson(response.getContent(), QuizReportResponse.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(ResponseStatus.OK.getCode(), quizReportResponse.getResponseCode());
        assertFalse(quizReportResponse.getPassed());
    }

    public void testShouldReturnQuizResultResponseAsInvalidQuizForQuizRequestWithInvalidQuizId() throws IOException, InterruptedException {
        HttpPost httpPost = (HttpPost) httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/quiz", TestContext.getJettyPort()), "POST");
        QuizReportRequest quizReportRequest = getQuizReportRequestforQuizIdAndQuestionId(new ContentIdentifierDto(UUID.randomUUID(), 1), quizDto.getQuestions().get(0).getContentId(), quizDto.getQuestions().get(1).getContentId());
        String quizReportAsJSON = getQuizReportAsJSON(quizReportRequest);

        httpPost.setEntity(new StringEntity(quizReportAsJSON));
        CustomHttpResponse response = httpClient.execute(httpPost, new CustomHttpResponseHandler());
        QuizReportResponse quizReportResponse = (QuizReportResponse) responseToJson(response.getContent(), QuizReportResponse.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(ResponseStatus.QUIZ_NOT_FOUND.getCode(), quizReportResponse.getResponseCode());
    }

    public void testShouldReturnQuizResultResponseAsInvalidQuestionForQuizRequestWithInvalidQuestionId() throws IOException, InterruptedException {
        HttpPost httpPost = (HttpPost) httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/quiz", TestContext.getJettyPort()), "POST");
        QuizReportRequest quizReportRequest = getQuizReportRequestforQuizIdAndQuestionId(quiz, UUID.randomUUID(), quizDto.getQuestions().get(1).getContentId());
        String quizReportAsJSON = getQuizReportAsJSON(quizReportRequest);

        httpPost.setEntity(new StringEntity(quizReportAsJSON));
        CustomHttpResponse response = httpClient.execute(httpPost, new CustomHttpResponseHandler());
        QuizReportResponse quizReportResponse = (QuizReportResponse) responseToJson(response.getContent(), QuizReportResponse.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(ResponseStatus.INVALID_QUESTION.getCode(), quizReportResponse.getResponseCode());
    }

    private String getQuizReportAsJSON(QuizReportRequest quizReportRequest) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(quizReportRequest);
    }

    private QuizReportRequest getQuizReportRequestforQuizIdAndQuestionId(ContentIdentifierDto quiz, UUID questionId1, UUID questionId2) {

        QuestionRequest questionRequest = new QuestionRequest(questionId1, 1, newArrayList("5", "6"), "1", false, false);
        QuestionRequest questionRequest2 = new QuestionRequest(questionId2, 1, newArrayList("8", "6"), "2", false, false);
        List<QuestionRequest> questions = newArrayList(questionRequest, questionRequest2);
        return new QuizReportRequest(provider.getCallerId(), "unk001", "ssn001", courseDto.toContentIdentifierDto(),
                module, chapter, quiz, questions, startTime, endTime, false);
    }

    private Provider addProvider(String remediId, Long callerId, ProviderStatus providerStatus) {
        //this provider copy gets detached once saved,hence need to retrieve
        Provider provider = new Provider(remediId, callerId, providerStatus, new Location("block", "district", "state"));
        providersAdded.add(providerService.add(provider));
        return providerService.byCallerId(callerId);
    }


    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        imports.add("org.motechproject.commons.api");
        imports.add("org.apache.http.util");
        imports.add("org.mortbay.jetty");
        imports.add("org.mortbay.jetty.servlet");
        imports.add("javax.servlet");
        imports.add("javax.servlet.http");
        imports.add("org.apache.commons.io");
        imports.add("org.jasypt.encryption.pbe.config");
        imports.add("org.jasypt.encryption.pbe");
        imports.add("org.jasypt.spring.properties");
        imports.add("org.motechproject.whp.mtraining.mail");
        imports.add("org.motechproject.whp.mtraining.web.domain");
        imports.add("org.motechproject.whp.mtraining.domain");
//        imports.add("com.google.common.collect");
        return imports;

    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"test-blueprint.xml"};
    }

    private Object getService(String serviceBeanName) {
        return getApplicationContext().getBean(serviceBeanName);
    }

    @Override
    protected void onTearDown() throws Exception {
        removeAllProviders();
        if (ivrServer != null) {
            ivrServer.stop();
        }
    }

    private void removeAllProviders() {
        for (Long providerId : providersAdded) {
            providerService.delete(providerId);
        }
    }

    private MotechResponse responseToJson(String response, Class<? extends MotechResponse> responseType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser parser = factory.createJsonParser(response);
        return mapper.readValue(parser, responseType);
    }
}
