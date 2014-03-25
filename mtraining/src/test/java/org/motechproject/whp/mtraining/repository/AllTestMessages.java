package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Message;
import org.springframework.stereotype.Repository;

@Repository
public class AllTestMessages extends RepositorySupport<Message> {
    @Override
    Class getType() {
        return Message.class;
    }
}
