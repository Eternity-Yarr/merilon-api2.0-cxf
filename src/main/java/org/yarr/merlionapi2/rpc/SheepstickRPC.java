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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
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
        return update((String id, Integer stock) ->
            bitrixService
                .getQuantityById(id)
                .ifPresent(quantity -> {
                    if (quantity > stock)
                        bitrixService.setQuantityById(id, stock);
                }));
    }

    Map<String, String> update(BiConsumer<String, Integer> stockSynchronizationStrategy) {
        Stopwatch s = Stopwatch.createStarted();
        Map<String, String> response = new HashMap<>();
        bindService
            .all().bonds().values()
            .stream().reduce(new HashSet<>(), (acc, xs) -> {
                acc.addAll(xs);
                return acc;
            })
            .stream().filter(b -> !b.id().equals("-1"))
            .forEach(synchronizeStock(stockSynchronizationStrategy));
        response.put("Elapsed time", String.valueOf(s.elapsed(TimeUnit.SECONDS)) + " s");

        return response;
    }

    Consumer<Bond> synchronizeStock(BiConsumer<String, Integer> stockSynchronizationStrategy) {
        return b -> {
            try {
                StockAndItem si = itemsRepository.get(b);
                if (si.stock() != null) {
                    stockSynchronizationStrategy.accept(b.id(), si.stock().available());
                    bitrixService
                        .getPriceById(b.id())
                        .ifPresent(compareAndSetPrice(b, si));
                } else {
                    bitrixService.setQuantityById(b.id(), 0);
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }

    Consumer<Long> compareAndSetPrice(Bond b, StockAndItem si) {
        return currentPrice ->
            bitrixService.alreadyInStock(si.id(), configService.merlionSupplierId())
                .ifPresent(inStock -> {
                    long merlionPrice = (long) Math.ceil(rateService.usd2rub(si.stock().price()));
                    if (!inStock && currentPrice < merlionPrice) {
                        merlionPrice +=
                                (long) (Math.ceil(merlionPrice * configService.valudeAddedPercent() / 100.0));
                        log.warn("Setting new price for %s: %s, old price: %s", si, merlionPrice, currentPrice);
                        bitrixService.setPriceById(b.id(), merlionPrice);
                    }
                });
    }
}
