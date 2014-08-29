package org.motechproject.whp.mtraining.web.domain;

import org.motechproject.whp.mtraining.domain.Location;

import java.util.UUID;

public class FlagResponse extends BasicResponse {
    private Location location;
    private CourseProgress courseProgress;

    public FlagResponse(long callerId, String uniqueId, UUID sessionId, Location location, CourseProgress courseProgress) {
        super(callerId, sessionId.toString(), uniqueId);
        this.location = location;
        this.courseProgress = courseProgress;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public CourseProgress getCourseProgress() {
        return courseProgress;
    }

    public void setCourseProgress(CourseProgress courseProgress) {
        this.courseProgress = courseProgress;
    }
}
