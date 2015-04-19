package org.yarr.merlionapi2;

import com.google.common.collect.ImmutableSet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarr.merlionapi2.rest.TrackRest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.net.InetSocketAddress;
import java.util.Set;

@ApplicationPath("/")
public class RestApplication extends Application
{
    private static final Logger log = LoggerFactory.getLogger(RestApplication.class);
    public static void startUp()
    {
        HttpServletDispatcher dispatcher = new HttpServletDispatcher();
        ServletHolder holder = new ServletHolder(dispatcher);
        ServletContextHandler context = new ServletContextHandler();
        context.setInitParameter("javax.ws.rs.Application", "org.yarr.merlionapi2.RestApplication");
        context.addServlet(holder, "/");

        InetSocketAddress listenAddress = new InetSocketAddress("127.0.0.1", 8080);
        Server server = new Server(listenAddress);
        server.setHandler(context);
        try
        {
            server.start();
            server.join();
        } catch (Exception e)
        {
            log.error("Something bad happened during server startup, exiting", e);
            System.exit(1);
        }
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return ImmutableSet.of(TrackRest.class);
    }
}
