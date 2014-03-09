package org.motechproject.whp.mtraining.web;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Sessions {
    public String create() {
        return UUID.randomUUID().toString();
    }
}
