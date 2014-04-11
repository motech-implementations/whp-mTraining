package org.motechproject.whp.mtraining.web.domain;

import org.motechproject.whp.mtraining.util.JSONUtil;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_BOOKMARK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

public class BookmarkPostRequest extends IVRRequest {

    private Bookmark bookmark;

    public BookmarkPostRequest() {
    }

    public BookmarkPostRequest(Long callerId, String uniqueId, String sessionId, Bookmark bookmark) {
        super(callerId, sessionId, uniqueId);
        this.bookmark = bookmark;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = super.validate();
        if (isNotEmpty(validationErrors))
            return validationErrors;
        if (bookmark == null) {
            validationErrors.add(new ValidationError(INVALID_BOOKMARK));
            return validationErrors;
        }
        validationErrors.addAll(bookmark.validate());
        return validationErrors;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}