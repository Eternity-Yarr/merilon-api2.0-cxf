package org.yarr.merlionapi2.service;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import https.api_merlion_com.dl.mlservice3.ArrayOfString;
import https.api_merlion_com.dl.mlservice3.ItemsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.MLPortProvider;
import org.yarr.merlionapi2.model.Item;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemsService
{
    private final static Logger log = LoggerFactory.getLogger(ItemsService.class);
    private final MLPortProvider portProvider;

    private final Cache<String, Item> itemCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    @Autowired
    public ItemsService(MLPortProvider portProvider) {
        this.portProvider = portProvider;
    }

    public List<Item> get(Collection<String> ids) {
        Preconditions.checkNotNull(ids, "Must not be null");
        if(ids.isEmpty())
            return Collections.emptyList();

        List<Item> result = new ArrayList<>();
        ArrayOfString rx = new ArrayOfString();
        ids.forEach(id -> {
            Item i = itemCache.getIfPresent(id);
            if (i == null)
                rx.getItem().add(id);
            else
                result.add(i);
        });
        log.debug("Got {} items from cache", result.size());
        if(rx.getItem().size() > 0) {
            List<ItemsResult> res = portProvider.get().getItems("", rx, "ДОСТАВКА", 0, 10000, "").getItem();
            log.debug("Received {} items from merlion", res.size());
            List<Item> newItems = res.stream()
                    .map(transform)
                    .peek(i -> itemCache.put(i.id(), i))
                    .collect(Collectors.toList());
            result.addAll(newItems);
        } else {
            log.debug("No additional request required");
        }

        return result;
    }

    protected Function<ItemsResult, Item> transform = (ItemsResult ir) ->
       new Item(ir.getNo(), ir.getGroupCode1(), ir.getVendorPart(), ir.getName(), ir.getBrand());
}
