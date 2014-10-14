package org.motechproject.whp.mtraining.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.LocationService;
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

    @Inject
    private LocationService locationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    @After
    public void deleteFromDatabase(){
        Provider provider = providerService.getProviderByRemediId("IntegrationTestRemediId");
        if (provider != null) {
            providerService.deleteProvider(provider);
        }
    }

    @Test
    public void shouldAddOrUpdateToProvidersWhenImporting() {
        ProviderImportService providerImportService = new ProviderImportService(providerService, locationService);
        String remediId = "IntegrationTestRemediId";
        String primaryContactNumber = "76465465";
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
        assertEquals(providerLocation.getState(), state);
        assertEquals(providerLocation.getBlock(), null);
        assertEquals(providerLocation.getDistrict(), null);
    }

}
