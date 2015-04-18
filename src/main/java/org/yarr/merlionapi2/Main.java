package org.yarr.merlionapi2;

import org.yarr.merlionapi2.directory.Catalog;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.service.CatalogService;

public class Main
{
    public static void main(String... args) {
        String login = System.getProperty("login");
        String password = System.getProperty("password");
        CatalogService service = new CatalogService(new MLPortProvider(login, password));
        Catalog catalog = service.get();
        for(CatalogNode node: catalog.nodes().values())
            System.out.println(catalog.canonicalName(node));
    }
}
