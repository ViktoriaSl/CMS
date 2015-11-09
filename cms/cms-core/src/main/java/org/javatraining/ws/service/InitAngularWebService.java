package org.javatraining.ws.service;

import org.javatraining.config.system.GooglePropKeys;
import org.javatraining.config.system.props.ApplicationProperty;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by olga on 09.08.15.
 */
@Path("angular")
public class InitAngularWebService {

    private static final String REQUESTED_PARAM = "param";
    private static final String PARAM_CLIENT_ID = "client_id";

    private static final String LOCALHOST_INET_ADDR = "127.0.1.1";
    private static final String LOCALHOST_GOOGLE_CLIENT_ID = "283637906279-1to8vgk462pfintjmkmbqfqq2qadld3p.apps.googleusercontent.com";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBaseUrl(@QueryParam(REQUESTED_PARAM) String param) {
        if (param != null && param.equals(PARAM_CLIENT_ID)) {
            String clientId = getGoogleClientId();
            return Response.ok(clientId).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private String getGoogleClientId() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            String inetAddr = host.getHostAddress();
            if (inetAddr.equals(LOCALHOST_INET_ADDR)) {
                return LOCALHOST_GOOGLE_CLIENT_ID;
            } else {
                Instance<GoogleClientIdWrapper> clientIdWrapperInstance = CDI.current().select(GoogleClientIdWrapper.class);
                String clientId = clientIdWrapperInstance.get().getClientId();
                CDI.current().destroy(clientIdWrapperInstance);
                return clientId;
            }
        } catch (UnknownHostException ignore) {
        }
        return null;
    }

    @Dependent
    private static class GoogleClientIdWrapper {
        @Inject
        @ApplicationProperty(name = GooglePropKeys.CLIENT_ID)
        private String clientId;

        public String getClientId() {
            return clientId;
        }
    }
}
