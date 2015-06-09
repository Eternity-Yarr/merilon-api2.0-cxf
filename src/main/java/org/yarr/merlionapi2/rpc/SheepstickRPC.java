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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

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
    public Map<String, String> fullUpdate() {
        return update(bitrixService::setQuantityById);
    }

    @GET
    @Path("/nightUpdate")
    public Map<String, String> partialUpdate() {
        return update((String id, Integer stock) -> {
            final Boolean[] result = {null};

            bitrixService
                .getQuantityById(id)
                .ifPresent(quantity -> {
                    if (quantity > stock)
                        result[0] = bitrixService.setQuantityById(id, stock);
                });

            return result[0];
        });
    }

    Map<String, String> update(BiFunction<String, Integer, Boolean> stockSynchronizationStrategy) {
        Stopwatch s = Stopwatch.createStarted();
        Map<String, String> response = new HashMap<>();
        bindService
            .all().bonds().values()
            .stream().reduce(new HashSet<>(), (acc, xs) -> {
                acc.addAll(xs);
                return acc;
            })
            .stream().filter(b -> !b.id().equals("-1"))
            .forEach(synchronizeStock(response, stockSynchronizationStrategy));
        response.put("Elapsed time", String.valueOf(s.elapsed(TimeUnit.SECONDS)) + " s");

        return response;
    }

    Consumer<Bond> synchronizeStock(Map<String, String> response, BiFunction<String, Integer, Boolean> stockSynchronizationStrategy) {
        return b -> {
            try {
                StockAndItem si = itemsRepository.get(b);
                if (si.stock() != null) {
                    Optional.ofNullable(stockSynchronizationStrategy.apply(b.id(), si.stock().available()))
                            .ifPresent(result -> {
                                if (result)
                                    updateLogs(response, "Inserted", b.id());
                                else
                                    updateLogs(response, "Updated", b.id());
                            });

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
                    } catch (Exception e) {
                        updateLogs(response, "Currency error", b.id());
                    }
                    if (!inStock && currentPrice < merlionPrice) {
                        merlionPrice +=
                                (long) (Math.ceil(merlionPrice * configService.valudeAddedPercent() / 100.0));
                        log.warn("Setting new price for {}: {}, old price: {}", si, merlionPrice, currentPrice);
                        updateLogs(response, "New price", b.id());
                        bitrixService.setPriceById(b.id(), merlionPrice);
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
