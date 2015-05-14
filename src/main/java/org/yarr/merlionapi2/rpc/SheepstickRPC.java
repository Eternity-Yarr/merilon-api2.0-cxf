package org.yarr.merlionapi2.rpc;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.directory.ItemsRepository;
import org.yarr.merlionapi2.model.Bond;
import org.yarr.merlionapi2.model.StockAndItem;
import org.yarr.merlionapi2.rest.BitrixRest;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.BitrixService;
import org.yarr.merlionapi2.service.RateService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Path("/rpc/sheep")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class SheepstickRPC
{
    private final static Logger log = LoggerFactory.getLogger(SheepstickRPC.class);

    private final BindService bindService;
    private final BitrixService bitrixService;
    private final ItemsRepository itemsRepository;
    private final RateService rateService;

    @Autowired
    public SheepstickRPC(
            BindService bindService,
            BitrixService bitrixService,
            ItemsRepository itemsRepository,
            RateService rateService) {
        this.bindService = bindService;
        this.bitrixService = bitrixService;
        this.itemsRepository = itemsRepository;
        this.rateService = rateService;
    }

    @GET
    @Path("/fullUpdate")
    public Map<String, String> fullUpdate() {
        Stopwatch s = Stopwatch.createStarted();
        Map<String, String> response = new HashMap<>();

        bindService.all().bonds().values()
                .stream()
                .reduce(new ArrayList<>(), (acc, xs) -> {
                    acc.addAll(xs);
                    return acc;
                })
                .stream()
                .forEach(b -> {
                    try
                    {
                        StockAndItem si = itemsRepository.get(b);
                        if (si.stock() != null)
                        {
                            bitrixService.setQuantityById(b.id(), si.stock().available());
                            bitrixService.getPriceById(b.id())
                                    .ifPresent(currentPrice -> {
                                        if (rateService.usd2rub(si.stock().price()) < currentPrice) {
                                            log.warn("Price for %s is lower than purchase price %s", si, currentPrice);
                                        }
                                    });
                        } else {
                            bitrixService.setQuantityById(b.id(), 0);
                        }

                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });


        response.put("Elapsed time", String.valueOf(s.elapsed(TimeUnit.SECONDS)) + " s");
        return response;
    }

    @GET
    @Path("/nightUpdate")
    public Map<String, String> partialUpdate() {
        Map<String, String> response = new HashMap<>();

        return response;
    }

    @GET
    @Path("/priceCheck")
    public Map<String, String> priceCheck() {
        Map<String, String> response = new HashMap<>();

        return response;
    }
}
