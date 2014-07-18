package org.motechproject.whp.mtraining.domain;

import java.util.Comparator;

/**
 * Comparator for comparing version of two {@link org.motechproject.mtraining.domain.Content} objects.
 */

public class ContentVersionComparator implements Comparator<Content> {
   @Override
   public int compare(Content content1, Content content2) {
       return content1.getVersion().compareTo(content2.getVersion());
   }
}
