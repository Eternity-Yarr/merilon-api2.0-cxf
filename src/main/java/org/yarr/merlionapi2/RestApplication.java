package org.yarr.merlionapi2;

import com.google.common.collect.ImmutableSet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarr.merlionapi2.rest.*;
import org.yarr.merlionapi2.rest.errors.NPEMapper;
import org.yarr.merlionapi2.rest.errors.PokemonMapper;
import org.yarr.merlionapi2.rpc.MonitorRPC;
import org.yarr.merlionapi2.rpc.SheepstickRPC;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.net.InetSocketAddress;
import java.util.Set;

@ApplicationPath("/")
public class RestApplication extends Application
{
    private static final Logger log = LoggerFactory.getLogger(RestApplication.class);

    private final Set<Object> singletons;

    public RestApplication() {
        TrackRest track = SpringContext.ctx().getBean(TrackRest.class);
        ItemRest item = SpringContext.ctx().getBean(ItemRest.class);
        CatalogRest catalog = SpringContext.ctx().getBean(CatalogRest.class);
        MonitorRPC monitor = SpringContext.ctx().getBean(MonitorRPC.class);
        CatalogItemsRest catalogItems = SpringContext.ctx().getBean(CatalogItemsRest.class);
        StageRest stage = SpringContext.ctx().getBean(StageRest.class);
        BitrixRest bitrix = SpringContext.ctx().getBean(BitrixRest.class);
        BindRest bind = SpringContext.ctx().getBean(BindRest.class);
        SheepstickRPC sheep = SpringContext.ctx().getBean(SheepstickRPC.class);
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.getAllowedOrigins().add("*");
        singletons = ImmutableSet.of(
                track, item, catalog, monitor, corsFilter, catalogItems, stage, bitrix, bind, sheep);
    }

    public static void startUp()
    {
        HttpServletDispatcher dispatcher = new HttpServletDispatcher();
        ServletHolder holder = new ServletHolder(dispatcher);
        ServletContextHandler context = new ServletContextHandler();
        context.addEventListener(new ResteasyBootstrap());
        context.setInitParameter("javax.ws.rs.Application", "org.yarr.merlionapi2.RestApplication");
        context.addServlet(holder, "/");

        InetSocketAddress listenAddress = new InetSocketAddress("127.0.0.1", 8080);

        final Server server = new Server(listenAddress);
        server.setHandler(context);
        server.setStopAtShutdown(true);
        server.setStopTimeout(500);
        Runtime.getRuntime().addShutdownHook(
                new Thread(){
                    @Override
                    public void run()
                    {
                        try
                        {
                            log.info("Stopping rest server");
                            server.stop();
                        } catch (Exception e) {
                        log.error("Exception: ", e);}
                    }
                }
        );

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
    public Set<Object> getSingletons() {
        return singletons;
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return ImmutableSet.of(
                JacksonConfiguration.class, NPEMapper.class, PokemonMapper.class
        );
    }
}
