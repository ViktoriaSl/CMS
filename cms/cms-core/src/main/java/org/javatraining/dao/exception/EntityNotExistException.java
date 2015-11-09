package org.javatraining.dao.exception;

import javax.ejb.ApplicationException;

/**
 * Created by vika on 23.06.15.
 */
@ApplicationException(rollback = false)
public class EntityNotExistException extends RuntimeException {
    public EntityNotExistException() {
        super();
    }

    public EntityNotExistException(Throwable cause) {
        super(cause);
    }

    public EntityNotExistException(String message) {
        super(message);
    }

    public EntityNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
