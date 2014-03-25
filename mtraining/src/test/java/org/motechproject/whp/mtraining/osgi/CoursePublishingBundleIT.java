package org.motechproject.whp.mtraining.osgi;

import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.motechproject.whp.mtraining.IVRServer;
import org.motechproject.whp.mtraining.RequestInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoursePublishingBundleIT extends AuthenticationAwareIT {


    @Override
    public void onSetUp() throws IOException, InterruptedException {
        super.onSetUp();
    }

    public void testThatCourseIsPublishedToIVR() throws IOException, InterruptedException {
        CourseService courseService = (CourseService) getApplicationContext().getBean("courseService");
        assertNotNull(courseService);

        courseService.addOrUpdateCourse(new CourseDto(true, "test-cs001", "Test course", "Created By", new ArrayList<ModuleDto>()));

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
        assertTrue(coursesPublished.get(0).contains("test-cs001"));

    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"test-blueprint.xml"};
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = super.getImports();
        imports.add("org.motechproject.whp.mtraining.service");
        return imports;
    }

    @Override
    protected void onTearDown() throws Exception {
        super.onTearDown();
        if (null != ivrServer) {
            ivrServer.stop();
        }
    }
}
