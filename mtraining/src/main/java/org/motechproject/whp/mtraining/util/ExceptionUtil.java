package org.motechproject.whp.mtraining.util;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import javax.jdo.JDODataStoreException;

public class ExceptionUtil {

    public static String getConstraintViolationMessage(JDODataStoreException e) {
        for (Throwable t : e.getNestedExceptions()) {
            if (t.getClass().equals(MySQLIntegrityConstraintViolationException.class)) {
                return t.getMessage();
            }
        }
        return e.getMessage();
    }

}
