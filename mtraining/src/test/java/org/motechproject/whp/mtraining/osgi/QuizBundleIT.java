package org.motechproject.whp.mtraining.osgi;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.CourseBuilder;
import org.motechproject.whp.mtraining.builder.BuilderHelper;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
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
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;
import static org.motechproject.whp.mtraining.util.ISODateTimeUtil.nowAsStringInTimeZoneUTC;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.WORKING_PROVIDER;


public class QuizBundleIT extends AuthenticationAwareIT {
    PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 10);

    private List<Long> providersAdded = new ArrayList<>();

    private MTrainingService mTrainingService;

    private ProviderService providerService;

    private BookmarkService bookmarkService;


    private IVRServer ivrServer;
    private Provider provider;
    private Course course;
    String startTime;
    String endTime;
    Chapter chapter;
    Quiz quiz;

    @Override
    public void onSetUp() throws IOException, InterruptedException {
        super.onSetUp();
        ivrServer = new IVRServer(8888, "/ivr-wgn").start();

        providerService = (ProviderService) getApplicationContext().getBean("providerService");
        assertNotNull(providerService);

        mTrainingService = (MTrainingService) getService("mTrainingService");
        assertNotNull(mTrainingService);

        bookmarkService = (BookmarkService) getService("bookmarkService");
        assertNotNull(bookmarkService);

        removeAllProviders();
        provider = addProvider("remediId23", 222292L, WORKING_PROVIDER);
        course = mTrainingService.createCourse(new CourseBuilder().build());
        chapter = BuilderHelper.findFirstActive(course.getChapters());

        Bookmark bookmark = new Bookmark("remediId23", Objects.toString(course.getId()), Objects.toString(chapter.getId()), 
                Objects.toString(BuilderHelper.findFirstActive(chapter.getLessons()).getId()), null);
        bookmarkService.createBookmark(bookmark);
        startTime = nowAsStringInTimeZoneUTC();
        endTime = nowAsStringInTimeZoneUTC();
        chapter = course.getChapters().get(0);
        quiz = chapter.getQuiz();
    }

    public void testShouldReturnQuizResultResponseForQuizRequest() throws IOException, InterruptedException {
        HttpPost httpPost = (HttpPost) httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/quiz", TestContext.getJettyPort()), "POST");
        QuizReportRequest quizReportRequest = getQuizReportRequestforQuizIdAndQuestionId(quiz.getId(), quiz.getQuestions().get(0).getQuestion(), quiz.getQuestions().get(1).getQuestion());
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
        QuizReportRequest quizReportRequest = getQuizReportRequestforQuizIdAndQuestionId(321L, quiz.getQuestions().get(0).getQuestion(), quiz.getQuestions().get(1).getQuestion());
        String quizReportAsJSON = getQuizReportAsJSON(quizReportRequest);

        httpPost.setEntity(new StringEntity(quizReportAsJSON));
        CustomHttpResponse response = httpClient.execute(httpPost, new CustomHttpResponseHandler());
        QuizReportResponse quizReportResponse = (QuizReportResponse) responseToJson(response.getContent(), QuizReportResponse.class);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(ResponseStatus.QUIZ_NOT_FOUND.getCode(), quizReportResponse.getResponseCode());
    }

    public void testShouldReturnQuizResultResponseAsInvalidQuestionForQuizRequestWithInvalidQuestionId() throws IOException, InterruptedException {
        HttpPost httpPost = (HttpPost) httpRequestWithAuthHeaders(String.format("http://localhost:%s/mtraining/web-api/quiz", TestContext.getJettyPort()), "POST");
        QuizReportRequest quizReportRequest = getQuizReportRequestforQuizIdAndQuestionId(quiz.getId(), "Random", quiz.getQuestions().get(1).getQuestion());
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

    private QuizReportRequest getQuizReportRequestforQuizIdAndQuestionId(long quiz, String questionId1, String questionId2) {

        QuestionRequest questionRequest = new QuestionRequest(questionId1, 1, newArrayList("5", "6"), "1", false, false);
        QuestionRequest questionRequest2 = new QuestionRequest(questionId2, 1, newArrayList("8", "6"), "2", false, false);
        List<QuestionRequest> questions = newArrayList(questionRequest, questionRequest2);
        return new QuizReportRequest(provider.getCallerId(), "unk001", "ssn001", course.getId(), chapter.getId(), quiz, questions, startTime, endTime, false);
    }

    private Provider addProvider(String remediId, Long callerId, ProviderStatus providerStatus) {
        //this provider copy gets detached once saved,hence need to retrieve
        Provider provider = new Provider(remediId, callerId, providerStatus, new Location("block", "district", "state"));
        providersAdded.add(providerService.createProvider(provider).getId());
        return providerService.getProviderByCallerId(callerId);
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
        Provider provider;
        for (Long providerId : providersAdded) {
            provider = providerService.getProviderById(providerId);
            providerService.deleteProvider(provider);
        }
    }

    private MotechResponse responseToJson(String response, Class<? extends MotechResponse> responseType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser parser = factory.createJsonParser(response);
        return mapper.readValue(parser, responseType);
    }
}
