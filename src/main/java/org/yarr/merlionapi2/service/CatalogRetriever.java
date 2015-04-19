package org.yarr.merlionapi2.service;

import https.api_merlion_com.dl.mlservice2.ArrayOfCatalogResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarr.merlionapi2.MLPortProvider;
import org.yarr.merlionapi2.directory.Catalog;
import org.yarr.merlionapi2.model.CatalogNode;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CatalogRetriever implements Callable<Catalog>
{
    private final static Logger log = LoggerFactory.getLogger(CatalogRetriever.class);


    @Override
    public Catalog call() throws Exception
    {
        return new Catalog(retrieve());
    }

    private Map<String, CatalogNode> retrieve() {
        ArrayOfCatalogResult result = MLPortProvider.i().get().getCatalog("ALL");
        log.debug("Got {} catalog entries", result.getItem().size());

        return result
                .getItem()
                .parallelStream()
                .map(x -> new CatalogNode(
                        x.getIDPARENT().equals("Order") ? null : x.getIDPARENT(),
                        x.getDescription(), x.getID()))
                .collect(
                        Collectors.toMap(CatalogNode::id, Function.identity())
                );
    }
}
