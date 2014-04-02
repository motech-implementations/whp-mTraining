package org.motechproject.whp.mtraining.domain;


import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class CourseContentTest {
    @Test
    public void shouldFindContentByContentId() {
        UUID contentId = UUID.randomUUID();
        UUID contentId2 = UUID.randomUUID();
        List<Message> messages = newArrayList(new Message("m1", contentId, 1, null, null, null, null, true), new Message("m1", contentId2, 1, null, null, null, null, true));
        Message contentByContentId = (Message) CourseContent.findContentByContentId(messages, contentId);
        assertNotNull(contentByContentId);
    }

    @Test
    public void shouldFindContentByContentIdReturnNullIfContentNotFound() {
        UUID contentId = UUID.randomUUID();
        UUID contentId2 = UUID.randomUUID();
        List<Message> messages = newArrayList(new Message("m1", contentId, 1, null, null, null, null, true), new Message("m1", contentId2, 1, null, null, null, null, true));
        Message contentByContentId = (Message) CourseContent.findContentByContentId(messages, UUID.randomUUID());
        assertNull(contentByContentId);
    }

    @Test
    public void shouldGetNextContentReturnNullIfContentIdNOtFound(){
        UUID contentId = UUID.randomUUID();
        UUID contentId2 = UUID.randomUUID();
        List<Message> messages = newArrayList(new Message("m1", contentId, 1, null, null, null, null, true), new Message("m1", contentId2, 1, null, null, null, null, true));
        Message contentByContentId = (Message) CourseContent.getNextContent(messages, UUID.randomUUID());
        assertNull(contentByContentId);
    }
}
