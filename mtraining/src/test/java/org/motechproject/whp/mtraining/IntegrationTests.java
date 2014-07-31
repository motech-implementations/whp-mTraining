package org.motechproject.whp.mtraining;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.whp.mtraining.web.controller.CallLogControllerIT;

/**
 * Parent IT class to run all the individual service ITs
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CallLogControllerIT.class})
public class IntegrationTests {
}
