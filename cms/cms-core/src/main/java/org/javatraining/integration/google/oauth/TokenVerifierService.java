package org.javatraining.integration.google.oauth;

/**
 * Created by olga on 29.05.15.
 */
public interface TokenVerifierService {
    boolean isTokenValid(String token);
}
