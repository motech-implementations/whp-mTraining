package org.motechproject.whp.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ProviderServiceIT extends BasePaxIT {

    @Inject
    private ProviderService providerService;

    @After
    public void setup() {
        Provider provider = providerService.getProviderByRemediId("IntegrationTestRemediId");
        if (provider != null) {
            providerService.deleteProvider(provider);
        }
    }

    @Test
    public void shouldAddProvider() {
        Provider provider = new Provider("IntegrationTestRemediId", 76465465L, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        Long id = providerService.createProvider(provider).getId();
        provider = providerService.getProviderById(id);

        assertThat(provider.getRemediId(), Is.is("IntegrationTestRemediId"));
    }

    @Test
    public void shouldDeleteProvider() {
        Provider provider = new Provider("IntegrationTestRemediId", 76465465L, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        Long id = providerService.createProvider(provider).getId();
        provider = providerService.getProviderById(id);
        assertNotNull(provider);
        providerService.deleteProvider(provider);

        assertNull(providerService.getProviderById(id));
    }

    @Test
    public void shouldMarkCallerAsUnidentifiedIfCallerIdNotRegistered() {
        long callerId = 76465465L;
        ResponseStatus responseStatus = providerService.validateProvider(callerId);

        assertEquals(responseStatus.getCode(), UNKNOWN_PROVIDER.getCode());
    }

    @Test
    public void shouldMarkErrorIfProviderIsNotValid() {
        long callerId = 76465465L;
        Provider provider = new Provider("IntegrationTestRemediId", callerId, ProviderStatus.NOT_WORKING_PROVIDER, new Location("block", "district", "state"));
        providerService.createProvider(provider);
        ResponseStatus response = providerService.validateProvider(callerId);

        assertEquals(response, NOT_WORKING_PROVIDER);
    }

    @Test
    public void shouldAddAndRetrieveAProvider() {
        String remediId = "IntegrationTestRemediId";
        Provider provider = new Provider(remediId, 76465465L, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        providerService.createProvider(provider);
        Provider savedProvider = providerService.getProviderByRemediId(remediId);

        assertThat(savedProvider, IsNull.notNullValue());
        assertThat(savedProvider.getCallerId(), Is.is(76465465L));
    }

    @Test
    public void shouldUpdateAndRetrieveAProvider() {
        long callerId = 76465465L;
        long callerIdNew = 7653333L;
        String remediId = "IntegrationTestRemediId";

        Provider provider = new Provider(remediId, callerId, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        provider = providerService.createProvider(provider);
        provider.setCallerId(callerIdNew);
        providerService.updateProvider(provider);

        Provider savedProvider = providerService.getProviderByCallerId(callerIdNew);
        assertThat(savedProvider, IsNull.notNullValue());
        assertThat(savedProvider.getRemediId(), Is.is(remediId));
        assertThat(savedProvider.getCallerId(), Is.is(callerIdNew));
    }
}
