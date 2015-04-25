package org.yarr.merlionapi2;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.yarr.merlionapi2.model.Config;
import org.yarr.merlionapi2.scheduler.TaskQueue;

import java.io.File;

public class Main
{
    public static Config config;
    public static void main(String... args) throws Exception {
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

        TaskQueue.i();
        RestApplication.startUp();
        }
}
