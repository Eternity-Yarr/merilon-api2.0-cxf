package org.yarr.merlionapi2;

import https.api_merlion_com.dl.mlservice3.MLPort;
import https.api_merlion_com.dl.mlservice3.MLService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.service.ConfigService;

import javax.xml.ws.BindingProvider;
import java.util.Map;

@Component
public class MLPortProvider
{
    private final static Logger log = LoggerFactory.getLogger(MLPortProvider.class);

    private final String LOGIN;
    private final String PASSWORD;

    @Autowired
    public MLPortProvider(ConfigService config) {
        this.LOGIN = config.merlionLogin();
        this.PASSWORD = config.merlionPassword();
    }

    private MLPort port;

    private void initialize() {
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
            MLService service = new MLService();
            port = service.getMLPort();
            Client client  = ClientProxy.getClient(port);
            client.getInInterceptors().add(new LoggingInInterceptor());
            client.getOutInterceptors().add(new LoggingOutInterceptor());

            Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
            rc.put(BindingProvider.USERNAME_PROPERTY, LOGIN);
            rc.put(BindingProvider.PASSWORD_PROPERTY, PASSWORD);
        } catch (Throwable e) {
            log.warn("Error during port initialization", e);
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
