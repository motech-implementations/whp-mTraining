package org.motechproject.whp.mtraining.osgi;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.mtraining.domain.*;
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
        MTrainingService mTrainingService = (MTrainingService) getApplicationContext().getBean("mTrainingService");
        assertNotNull(mTrainingService);
        Course cs001 = buildCourse();
        mTrainingService.createCourse(cs001);
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
        }, 30000).start();

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
        imports.add("org.motechproject.whp.mtraining.mail");
        return imports;

    }

    @Override
    protected void onTearDown() throws Exception {
        if (ivrServer != null) {
            ivrServer.stop();
        }
    }

    private Course buildCourse() {
        Lesson activeMessage = new Lesson("msg001", CourseUnitState.Active, "message desc");
        Lesson inactiveMessage = new Lesson("msg002", CourseUnitState.Inactive, "message desc");
        Question question = new Question("Q001", "correct-answer.wav");
        Quiz quiz001 = new Quiz("Quiz001",  CourseUnitState.Active, null, Arrays.asList(question), 85.0);
        Chapter ch001 = new Chapter("ch001", CourseUnitState.Active, "chap-aud.wav", Arrays.asList(activeMessage, inactiveMessage), quiz001);
        return new Course("CS001", CourseUnitState.Active, "course-aud.wav", Arrays.asList(ch001));
    }
}
