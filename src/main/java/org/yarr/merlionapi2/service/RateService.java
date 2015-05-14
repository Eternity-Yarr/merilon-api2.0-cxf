package org.yarr.merlionapi2.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;
import org.yarr.rates.Rates;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class RateService
{
    private final Cache<String, Double> rateCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .build();

    public double usd2rub() {
        try
        {
            return rateCache.get("USD", () -> new Rates().getRateOf("USD"));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public double usd2rub(double usd) {
        return usd2rub() * usd;
    }
}
