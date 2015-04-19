package org.yarr.merlionapi2;

import org.yarr.merlionapi2.scheduler.TaskQueue;
import org.yarr.merlionapi2.service.CatalogService;

public class Main
{
    public static void main(String... args) {
        String login = System.getProperty("login");
        String password = System.getProperty("password");
        CatalogService service = new CatalogService();
        /* Catalog catalog = service.get();
        for(CatalogNode node: catalog.nodes().values())
            System.out.println(catalog.canonicalName(node));
            */
        TaskQueue.i();
        RestApplication.startUp();
        }
}
