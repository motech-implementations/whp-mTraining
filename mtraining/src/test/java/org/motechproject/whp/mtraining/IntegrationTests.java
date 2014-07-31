package org.motechproject.whp.mtraining;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.whp.mtraining.service.CoursePublicationAttemptService;
import org.motechproject.whp.mtraining.service.impl.CourseImportServiceIT;
import org.motechproject.whp.mtraining.service.impl.ProviderImportServiceIT;
import org.motechproject.whp.mtraining.service.impl.ProviderServiceIT;

/**
 * Parent IT class to run all the individual service ITs
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CourseImportServiceIT.class, CoursePublicationAttemptService.class, ProviderImportServiceIT.class,
        ProviderServiceIT.class})
public class IntegrationTests {
}
