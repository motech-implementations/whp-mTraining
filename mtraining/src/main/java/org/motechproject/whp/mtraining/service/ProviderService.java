package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.util.List;

public interface ProviderService {

    Provider createProvider(Provider provider);

    Provider updateProvider(Provider provider);

    Provider updateProviderbyRemediId(String remediId, Provider provider);

    void deleteProvider(Provider provider);

    List<Provider> getAllProviders();

    Provider getProviderById(long id);

    Provider getProviderByCallerId(Long callerId);

    Provider getProviderByRemediId(String remediId);

    Provider getProviderByLocation(Long id);

    ResponseStatus validateProvider(Long callerId);

    void resetCourseProgresses(String contentId);
}
