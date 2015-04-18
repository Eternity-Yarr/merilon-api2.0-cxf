package org.yarr.merlionapi2.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CacheService
{
    private static Logger log = LoggerFactory.getLogger(CacheService.class);
    Cache<Class<?>, Object> repository = CacheBuilder.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .build();

    Map<Class<?>, Callable<?>> retrievers = new HashMap<>();

    public void put(Class<?> clazz, Callable<?> retriever) {
        retrievers.put(clazz, retriever);
    }

    @SuppressWarnings("unchecked") // This cast simply couldn't fail
    public <T> T retrieve(Class<T> type) {
        try
        {
            return (T) repository.get(type, retrievers.get(type));
        } catch (ExecutionException e) {
            log.error(
                    "Got an exception while tried to retrieve {} entry from cached directory",
                    type.getName(), e
            );
            return null;
        }
    }

    public static CacheService i() {
        return Lazy.service;
    }

    private static class Lazy {
        public static final CacheService service = new CacheService();
    }
}
