package org.javatraining.integration.google.oauth.exception;

/**
 * Created by olga on 29.05.15.
 */
public class AuthException extends RuntimeException {
    public AuthException() {
        super();
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
