package org.motechproject.whp.mtraining.osgi;

import org.motechproject.ivr.stub.PublishedCoursesService;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoursePublishingBundleIT extends AuthenticationAwareIT {

    //TODO: Fix and rename the test
    public void ignoreThatCourseIsPublishedToIVR() throws IOException, InterruptedException {
        CourseService courseService = (CourseService) getApplicationContext().getBean("courseService");
        assertNotNull(courseService);

        final PublishedCoursesService publishedCourseService = getPublisedCourseService();
        publishedCourseService.removeAll();
        assertNull(publishedCourseService.latest());

        courseService.addCourse(new CourseDto("test-cs001", "Test course", true, new ArrayList<ModuleDto>()));

        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return publishedCourseService.latest() == null;
            }
        }, 10000).start();


        String publishedCourse = publishedCourseService.latest();
        assertNotNull(publishedCourse);
        assertTrue(publishedCourse.contains("test-cs001"));

    }

    private PublishedCoursesService getPublisedCourseService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(PublishedCoursesService.class.getName());
        assertNotNull(serviceReference);
        return (PublishedCoursesService) bundleContext.getService(serviceReference);
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        imports.add("org.apache.http.util");
        imports.add("org.motechproject.whp.mtraining.service");
        return imports;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"test-blueprint.xml"};
    }


}
