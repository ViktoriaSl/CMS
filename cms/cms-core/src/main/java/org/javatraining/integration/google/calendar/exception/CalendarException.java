package org.javatraining.integration.google.calendar.exception;

/**
 * Created by olga on 27.05.15.
 */
public class CalendarException extends RuntimeException {
    public CalendarException() {
        super();
    }

    public CalendarException(Throwable cause) {
        super(cause);
    }

    public CalendarException(String message) {
        super(message);
    }

    public CalendarException(String message, Throwable cause) {
        super(message, cause);
    }
}
