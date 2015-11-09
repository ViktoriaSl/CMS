package org.javatraining.integration.google.calendar.exception;

/**
 * Created by olga on 27.05.15.
 */
public class CalendarRoleNotExistsException extends CalendarException {
    public CalendarRoleNotExistsException() {
        super();
    }

    public CalendarRoleNotExistsException(Throwable cause) {
        super(cause);
    }

    public CalendarRoleNotExistsException(String message) {
        super(message);
    }

    public CalendarRoleNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
