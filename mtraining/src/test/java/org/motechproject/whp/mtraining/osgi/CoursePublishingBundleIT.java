package org.motechproject.whp.mtraining.osgi;

import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.motechproject.whp.mtraining.IVRServer;
import org.motechproject.whp.mtraining.RequestInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CoursePublishingBundleIT extends AuthenticationAwareIT {

    protected IVRServer ivrServer;
    protected List<String> coursesPublished;

    @Override
    public void onSetUp() throws IOException, InterruptedException {
        super.onSetUp();
        ivrServer = new IVRServer(8888, "/ivr-wgn").start();
        coursesPublished = new ArrayList<>();
    }

    public void testThatCourseIsPublishedToIVR() throws IOException, InterruptedException {
        CourseService courseService = (CourseService) getApplicationContext().getBean("courseService");
        assertNotNull(courseService);
        CourseDto cs001 = buildCourse();
        courseService.addOrUpdateCourse(cs001);
        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                RequestInfo requestInfo = ivrServer.detailForRequest("/ivr-wgn");
                if (requestInfo == null) {
                    return true;
                }
                Map<String, String> requestData = requestInfo.getRequestData();
                String postContent = requestData.get(IVRServer.POST_BODY);
                coursesPublished.add(postContent);
                return false;
            }
        }, 20000).start();

        assertFalse(coursesPublished.isEmpty());
        System.out.println(coursesPublished.get(0));
        assertTrue(coursesPublished.get(0).contains("CS001"));
        assertTrue(coursesPublished.get(0).contains("msg001"));
        assertFalse(coursesPublished.get(0).contains("msg002"));
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"test-blueprint.xml"};
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
        imports.add("org.motechproject.whp.mtraining.service");
        imports.add("org.jasypt.encryption.pbe.config");
        imports.add("org.jasypt.encryption.pbe");
        imports.add("org.jasypt.spring.properties");
        return imports;

    }

    @Override
    protected void onTearDown() throws Exception {
        if (ivrServer != null) {
            ivrServer.stop();
        }
    }

    private CourseDto buildCourse() {
        String createdBy = "author";

        MessageDto activeMessage = new MessageDto(true, "msg001", "aud01", "message desc", createdBy);
        MessageDto inactiveMessage = new MessageDto(false, "msg002", "aud01.wav", "message desc", createdBy);

        QuestionDto questionDto = new QuestionDto(true, "Q001", "ques desc", "ques-aud.wav", new AnswerDto("C", "correct-answer.wav"),
                Arrays.asList("A", "B", "C"), createdBy);

        QuizDto quiz001 = new QuizDto(true, "Quiz001", Arrays.asList(questionDto), 1, 85l, createdBy);

        ChapterDto ch001 = new ChapterDto(true, "ch001", "chapter description", createdBy, Arrays.asList(activeMessage, inactiveMessage), quiz001);

        ModuleDto mod001 = new ModuleDto(true, "MOD001", "module desc", createdBy, Arrays.asList(ch001));

        CourseDto cs001 = new CourseDto(true, "CS001", "Course Desc", createdBy, Arrays.asList(mod001));

        return cs001;
    }
}
