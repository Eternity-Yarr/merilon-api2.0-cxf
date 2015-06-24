package org.yarr.merlionapi2.rpc;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.directory.ItemsRepository;
import org.yarr.merlionapi2.model.Bond;
import org.yarr.merlionapi2.model.StockAndItem;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.BitrixService;
import org.yarr.merlionapi2.service.ConfigService;
import org.yarr.merlionapi2.service.RateService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Path("/rpc/sheep")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class SheepstickRPC
{
    private final static Logger log = LoggerFactory.getLogger(SheepstickRPC.class);
    private final ExecutorService es = Executors.newSingleThreadExecutor();

    private final BindService bindService;
    private final BitrixService bitrixService;
    private final ItemsRepository itemsRepository;
    private final RateService rateService;
    private final ConfigService configService;

    @Autowired
    public SheepstickRPC(
            BindService bindService,
            BitrixService bitrixService,
            ItemsRepository itemsRepository,
            RateService rateService,
            ConfigService configService) {
        this.bindService = bindService;
        this.bitrixService = bitrixService;
        this.itemsRepository = itemsRepository;
        this.rateService = rateService;
        this.configService = configService;
    }

    @GET
    @Path("/fullUpdate")
    public Map<String, String> fullUpdate() throws InterruptedException, ExecutionException {
        Future<Map<String, String>> result = es.submit(() -> update(bitrixService::setQuantityById));
        return result.get();
    }

    @GET
    @Path("/nightUpdate")
    public Map<String, String> partialUpdate() throws InterruptedException, ExecutionException {
        BiFunction<String, Integer, Boolean> nightStrategy = (String id, Integer stock) -> {
            final Boolean[] result = {null};
            bitrixService
                    .getQuantityById(id)
                    .ifPresent(quantity -> {
                        if (quantity > stock)
                            result[0] = bitrixService.setQuantityById(id, stock);
                    });

            return result[0];
        };
        Future<Map<String, String>> result = es.submit(() -> update(nightStrategy));
        return result.get();
    }

    Map<String, String> update(BiFunction<String, Integer, Boolean> stockSynchronizationStrategy) {
        Stopwatch s = Stopwatch.createStarted();
        Map<String, String> response = new HashMap<>();
        bindService.all().bonds().values().stream()
            .flatMap(Collection::stream)
            .filter(b -> !b.id().equals("-1"))
            .forEach(synchronizeStock(response, stockSynchronizationStrategy));
        response.put("Elapsed time", String.valueOf(s.elapsed(TimeUnit.SECONDS)) + " s");

        return response;
    }

    Consumer<Bond> synchronizeStock(Map<String, String> response, BiFunction<String, Integer, Boolean> stockSynchronizationStrategy) {
        return b -> {
            try {
                StockAndItem si = itemsRepository.get(b);
                if (si == null) {
                    log.info("Bond {} is obsolete, no such item in merlion found", b);
                    return;
                }

                if (si.stock() != null) {
                    Optional.ofNullable(stockSynchronizationStrategy.apply(b.id(), si.stock().available()))
                            .ifPresent(result -> {
                                if (result)
                                    updateLogs(response, "Inserted", b.id());
                                else
                                    updateLogs(response, "Updated", b.id());
                            });

                    if (si.stock().available() > 0)
                        bitrixService
                            .getPriceById(b.id())
                            .ifPresent(compareAndSetPrice(response, b, si));
                } else {
                    boolean result = bitrixService.setQuantityById(b.id(), 0);
                    if (result)
                        updateLogs(response, "Inserted", b.id());
                    else
                        updateLogs(response, "Updated", b.id());
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }

    Consumer<Long> compareAndSetPrice(Map<String, String> response, Bond b, StockAndItem si) {
        return currentPrice ->
            bitrixService.alreadyInStock(si.id(), configService.merlionSupplierId())
                .ifPresent(inStock -> {
                    long merlionPrice = 0;
                    try {
                        merlionPrice = (long) Math.ceil(rateService.usd2rub(si.stock().price()));
                        if (!inStock && currentPrice < merlionPrice) {
                            merlionPrice +=
                                    (long) (Math.ceil(merlionPrice * configService.valudeAddedPercent() / 100.0));
                            log.warn("Setting new price for {}: {}, old price: {}", si, merlionPrice, currentPrice);
                            updateLogs(response, "New price", b.id());
                            bitrixService.setPriceById(b.id(), merlionPrice);
                        }
                    } catch (RuntimeException e) {
                        updateLogs(response, "Currency error", b.id());
                    }
                });
    }

    void updateLogs(Map<String, String> response, String key, String value) {
        String current = response.get(key);

        if (current == null) {
            response.put(key, value);
        } else {
            response.put(key, current + "," + value);
        }
    }
}
