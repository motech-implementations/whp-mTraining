package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.Provider;

public interface ProviderService {

    Long add(Provider provider);

    boolean delete(Long providerId);

}
