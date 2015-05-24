package org.yarr.merlionapi2;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.yarr.merlionapi2.model.Config;

import java.io.File;

public class Main
{
    public static final Logger log = LoggerFactory.getLogger(Main.class);
    public static Config config;
    public static void main(String... args) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> log.error("Uncaught exception flying by, catching", e)
        );
        String configFile = System.getProperty("config");
        if (configFile == null)
            configFile = "./config.json";
        ObjectMapper om = new ObjectMapper();
        config = om.readValue(new File(configFile), Config.class);
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ApplicationConfiguration.class);
        /* Catalog catalog = service.get();
        for(CatalogNode node: catalog.nodes().values())
            System.out.println(catalog.canonicalName(node));
            */
        ctx.refresh();
        RestApplication.startUp();
        }
}
