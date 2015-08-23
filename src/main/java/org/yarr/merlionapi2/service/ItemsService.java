package org.yarr.merlionapi2.service;

import https.api_merlion_com.dl.mlservice3.ArrayOfString;
import https.api_merlion_com.dl.mlservice3.ItemsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.MLPortProvider;
import org.yarr.merlionapi2.model.Item;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemsService
{
    private final static Logger log = LoggerFactory.getLogger(ItemsService.class);
    private final MLPortProvider portProvider;

    @Autowired
    public ItemsService(MLPortProvider portProvider) {
        this.portProvider = portProvider;
    }

    public List<Item> get(@NotNull Collection<String> ids) {
        ArrayOfString rx = new ArrayOfString();
        ids.forEach(rx.getItem()::add);
        List<ItemsResult> res = portProvider.get().getItems("", rx, "ДОСТАВКА", 0, 10000, "").getItem();
        log.debug("Got {} items", res.size());
        return res.stream().map(transform).collect(Collectors.toList());
    }

    protected Function<ItemsResult, Item> transform = (ItemsResult ir) ->
       new Item(ir.getNo(), ir.getGroupCode1(), ir.getVendorPart(), ir.getName(), ir.getBrand());
}
