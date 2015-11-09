package org.javatraining.integration.google.oauth.exception;

/**
 * Created by olga on 29.05.15.
 */
public class GoogleConnectionAuthException extends AuthException {

    public GoogleConnectionAuthException() {
        super();
    }

    public GoogleConnectionAuthException(Throwable cause) {
        super(cause);
    }

    public GoogleConnectionAuthException(String message) {
        super(message);
    }

    public GoogleConnectionAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
