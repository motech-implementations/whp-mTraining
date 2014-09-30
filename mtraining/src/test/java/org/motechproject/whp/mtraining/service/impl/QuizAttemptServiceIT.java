package org.motechproject.whp.mtraining.service.impl;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.reports.domain.QuizAttempt;
import org.motechproject.whp.mtraining.service.QuizAttemptService;
import org.motechproject.whp.mtraining.util.JSONUtil;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class QuizAttemptServiceIT extends BasePaxIT {

    @Inject
    private QuizAttemptService quizAttemptService;

    @Test
    public void shouldConvertQuizAttemptToJson() throws IOException {
        QuizAttempt quizAttempt = quizAttemptService.createQuizAttempt(new QuizAttempt("1311", 321412l, "abc", "r32r23r23r23r",
                new ContentIdentifier(), new ContentIdentifier(), new ContentIdentifier(), new ContentIdentifier(),
                DateTime.now(), DateTime.now(), true, 50.0, false));

        String json = JSONUtil.toJsonString(quizAttempt);
        quizAttemptService.deleteQuizAttempt(quizAttempt);

        Assert.assertNotNull(json);
    }

}
