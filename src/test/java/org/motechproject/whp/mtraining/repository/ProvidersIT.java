package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class ProvidersIT {

    @Autowired
    Providers providers;

    @Before
    public void before() {
        providers.deleteAll();
    }

    @Test
    public void shouldAddAndRetrieveAProvider() {

        long callerId = 7657667L;

        assertThat(providers.getByCallerId(callerId), IsNull.nullValue());

        Provider provider = new Provider(callerId);
        Location bihar = new Location("Bihar");
        provider.setLocation(bihar);

        providers.save(provider);

        Provider savedProvider = providers.getByCallerId(callerId);
        assertThat(savedProvider, IsNull.notNullValue());
        assertThat(savedProvider.getCallerId(), Is.is(callerId));
    }


}
