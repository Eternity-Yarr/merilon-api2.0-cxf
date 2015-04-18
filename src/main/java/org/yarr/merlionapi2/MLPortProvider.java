package org.yarr.merlionapi2;

import https.api_merlion_com.dl.mlservice2.MLPort;
import https.api_merlion_com.dl.mlservice2.MLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.BindingProvider;
import java.util.Map;
import java.util.logging.LoggingPermission;

public class MLPortProvider
{
    private final static Logger log = LoggerFactory.getLogger(MLPortProvider.class);

    private final String LOGIN;
    private final String PASSWORD;

    public MLPortProvider(String login, String password) {
        this.LOGIN = login;
        this.PASSWORD = password;
    }

    private volatile MLPort port;

    //TODO: kill me with fire
    private synchronized void initialize() {
        while(port == null) {
            if (!tryInitialize())
                try
                {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException ignored) {}
            else {
                log.info("Initialized SOAP port with login {}", LOGIN);
            }
        }
    }

    private boolean tryInitialize() {
        try
        {
            port = new MLService().getMLPort();
            Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
            rc.put(BindingProvider.USERNAME_PROPERTY, LOGIN);
            rc.put(BindingProvider.PASSWORD_PROPERTY, PASSWORD);
        } catch (Exception e) {
            port = null;
            return false;
        }

        return true;
    }

    public MLPort get() {
        if (port != null)
            return port;
        else
        {
            initialize();
            return port;
        }

    }
}
