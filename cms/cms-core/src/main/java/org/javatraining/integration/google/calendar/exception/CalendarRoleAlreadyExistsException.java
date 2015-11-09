package org.javatraining.integration.google.calendar.exception;

/**
 * Created by olga on 27.05.15.
 */
public class CalendarRoleAlreadyExistsException extends CalendarException {
    public CalendarRoleAlreadyExistsException() {
        super();
    }

    public CalendarRoleAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public CalendarRoleAlreadyExistsException(String message) {
        super(message);
    }

    public CalendarRoleAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
