package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class ProvidersIT {

    @Autowired
    ProviderService providerService;

    @Before
    public void before() {
        providerService.getAllProviders().clear();
    }

    @Test
    public void shouldAddAndRetrieveAProvider() {
        String remediId = "remedix";
        assertThat(providerService.getProviderByRemediId(remediId), IsNull.nullValue());
        Provider provider = new Provider(remediId, 717777L, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));

        providerService.createProvider(provider);

        Provider savedProvider = providerService.getProviderByRemediId(remediId);
        assertThat(savedProvider, IsNull.notNullValue());
        assertThat(savedProvider.getCallerId(), Is.is(717777L));
    }

    @Test
    public void shouldUpdateAndRetrieveAProvider() {
        long callerId = 7657667L;
        long callerIdNew = 7653333L;
        String remediId = "remediId";

        Provider oldProvider = new Provider(remediId, callerId, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        providerService.createProvider(oldProvider);

        Provider newProvider = new Provider(remediId, callerIdNew, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        providerService.createProvider(newProvider);

        Provider savedProvider = providerService.getProviderByRemediId(remediId);
        assertThat(savedProvider, IsNull.notNullValue());
        assertThat(savedProvider.getRemediId(), Is.is(remediId));
        assertThat(savedProvider.getCallerId(), Is.is(callerIdNew));
    }


    @After
    public void after() {
        providerService.getAllProviders().clear();
    }
}
