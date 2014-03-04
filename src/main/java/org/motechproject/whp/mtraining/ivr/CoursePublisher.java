package org.motechproject.whp.mtraining.ivr;

import org.springframework.stereotype.Service;

@Service
public class CoursePublisher {

    public PublishingResult publish(String courseId) {
        return PublishingResult.SUCCESS;
    }
}
