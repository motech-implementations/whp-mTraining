package org.motechproject.whp.mtraining.service.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.WORKING_PROVIDER;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ProviderImportServiceIT extends BasePaxIT {

    @Inject
    private ProviderService providerService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldAddOrUpdateToProvidersWhenImporting() {
        ProviderImportService providerImportService = new ProviderImportService(providerService);
        String remediId = "RemediX";
        String primaryContactNumber = "9898989898";
        String providerStatus = WORKING_PROVIDER.getStatus();
        String state = "state";
        String block = "block";
        String district = "district";

        providerImportService.importProviders(Arrays.asList(new ProviderCsvRequest(remediId, primaryContactNumber, providerStatus, state, block, district)));

        Provider provider = providerService.getProviderByCallerId(Long.valueOf(primaryContactNumber));
        assertEquals(provider.getRemediId(), remediId);
        assertEquals(provider.getCallerId(), Long.valueOf(primaryContactNumber));
        assertEquals(provider.getProviderStatus(), WORKING_PROVIDER);

        Location providerLocation = provider.getLocation();

        assertNotNull(providerLocation);
        assertEquals(providerLocation.getName(), state);
    }

}
