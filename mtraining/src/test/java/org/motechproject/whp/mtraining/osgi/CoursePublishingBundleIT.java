package org.motechproject.whp.mtraining.osgi;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.motechproject.whp.mtraining.IVRServer;
import org.motechproject.whp.mtraining.RequestInfo;
import org.motechproject.whp.mtraining.WebClient;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.ivr.IVRGateway;
import org.motechproject.whp.mtraining.ivr.IVRResponseParser;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.vmOption;

@Ignore
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CoursePublishingBundleIT extends BasePaxIT {

    protected IVRServer ivrServer;
    protected List<String> coursesPublished;
    protected DtoFactoryService dtoFactoryService;

    @Inject
    private MTrainingService mTrainingService;

    @Inject
    private SettingsFacade mTrainingSettings;

    IVRResponseParser ivrResponseHandler = new IVRResponseParser();

    @Before
    public void setUp() throws IOException, InterruptedException {
        ivrServer = new IVRServer(8888, "/ivr-wgn").start();
        coursesPublished = new ArrayList<>();
    }

    @Override
    @Configuration
    public Option[] config() throws IOException {
        Option[] debug = options(
                vmOption( "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005" ));
        return (Option[]) ArrayUtils.addAll(super.config(), debug);
    }

    @Test
    public void testThatCourseIsPublishedToIVR() throws IOException, InterruptedException {
        Course cs001 = buildCourse();
        mTrainingService.createCourse(cs001);
        new IVRGateway(mTrainingSettings, new WebClient(), ivrResponseHandler).postCourse((CoursePlanDto)dtoFactoryService.getDto(cs001));

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

    private Course buildCourse() {
        Lesson activeMessage = new Lesson("msg001", CourseUnitState.Active, "message desc");
        Lesson inactiveMessage = new Lesson("msg002", CourseUnitState.Inactive, "message desc");
        Question question = new Question("Q001", "correct-answer.wav");
        //Quiz quiz001 = new Quiz("Quiz001",  CourseUnitState.Active, null, Arrays.asList(question), 85.0);
        Chapter ch001 = new Chapter("ch001", CourseUnitState.Active, "chap-aud.wav", Arrays.asList(activeMessage, inactiveMessage));
        return new Course("CS001", CourseUnitState.Active, "course-aud.wav", Arrays.asList(ch001));
    }
}
