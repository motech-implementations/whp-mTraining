package org.motechproject.whp.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
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

    @Test
    public void shouldAddProvider() {
        Provider provider = new Provider("remediId", 654654l, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));

        Long id = providerService.createProvider(provider).getId();

        provider = providerService.getProviderById(id);
        assertThat(provider.getRemediId(), Is.is("remediId"));
    }

    @Test
    public void shouldDeleteProvider() {
        Provider provider = new Provider("remediId", 654654l, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        providerService.createProvider(provider).getId();
        provider = providerService.getAllProviders().get(0);
        assertNotNull(provider);
        Long id = provider.getId();
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
        long callerId = 76465464L;
        Provider provider = new Provider("remediId", callerId, ProviderStatus.NOT_WORKING_PROVIDER, new Location("block", "district", "state"));
        providerService.createProvider(provider);

        ResponseStatus response = providerService.validateProvider(callerId);

        assertEquals(response, NOT_WORKING_PROVIDER);
    }

    @Test
    public void shouldAddAndRetrieveAProvider() {
        String remediId = "remedix";
        Provider provider = new Provider(remediId, 717777L, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));

        providerService.updateProvider(provider);

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

        Provider savedProvider = providerService.getProviderByCallerId(callerIdNew);
        assertThat(savedProvider, IsNull.notNullValue());
        assertThat(savedProvider.getRemediId(), Is.is(remediId));
        assertThat(savedProvider.getCallerId(), Is.is(callerIdNew));
    }

}
