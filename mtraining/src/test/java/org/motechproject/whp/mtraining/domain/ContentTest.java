package org.motechproject.whp.mtraining.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContentTest {
    @Test
    public void shouldSetStatus() {
        Content contentWithActiveStatus = new Content("name", "module", "active", "desc", "fileName");
        assertTrue(contentWithActiveStatus.isActive());

        Content contentWithInActiveStatus = new Content("name", "module", "inActive", "desc", "fileName");
        assertFalse(contentWithInActiveStatus.isActive());

        Content contentWithBlankStatus = new Content("name", "module", "", "desc", "fileName");
        assertTrue(contentWithBlankStatus.isActive());
    }
}
