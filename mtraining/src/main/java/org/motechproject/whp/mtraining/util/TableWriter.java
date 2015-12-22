package org.motechproject.whp.mtraining.util;

import java.io.IOException;
import java.util.Map;

public interface TableWriter extends AutoCloseable {

    /**
     * Writes the table header.
     * @param headers an array of headers for the table
     * @throws IOException
     */
    void writeHeader(String[] headers) throws IOException;

    /**
     * Writes a row of data to the table.
     * @param row the row data, keys are field names and values are their values in string form
     *            that should be directly written to the output
     * @param headers the array of headers for the table
     * @throws IOException
     */
    void writeRow(Map<String, String> row, String[] headers) throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    void close();
}