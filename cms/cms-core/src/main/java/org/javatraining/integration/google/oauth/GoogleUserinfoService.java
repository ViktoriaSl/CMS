package org.javatraining.integration.google.oauth;

import org.javatraining.model.PersonVO;

/**
 * Created by olga on 29.05.15.
 */
public interface GoogleUserinfoService {
    String getClientIdByToken(String token);

    String getEmailByToken(String token);

    PersonVO getUserInfoByToken(String token);
}
