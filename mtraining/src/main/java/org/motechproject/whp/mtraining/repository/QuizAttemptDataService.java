package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.reports.domain.QuizAttempt;

public interface QuizAttemptDataService extends MotechDataService<QuizAttempt> {

    @Lookup
    QuizAttempt findQuizAttemptById(@LookupField(name = "id") long id);

}
