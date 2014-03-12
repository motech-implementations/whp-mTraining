package org.motechproject.whp.mtraining.ivr;

import static java.lang.String.format;

public class IVRException extends Exception {

    public IVRException(int statusCode, String reason) {
        super(format("IVR replied with status code %s and message %s", statusCode, reason));
    }
}
