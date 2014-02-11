package org.motechproject.mtraining.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PingControllerTest {

    @Test
    public void shouldPing() {
        PingController pingController = new PingController();

        assertEquals("Motech MTraining Ping Page", pingController.ping());
    }
}
