package org.yarr.merlionapi2.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.MLPortProvider;
import org.yarr.merlionapi2.model.ShimpmentMethod;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class MLDirectoryService
{
    private final Cache<Class, Object> directoryCache =
            CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    private final MLPortProvider portProvider;
    
    @Autowired
    public MLDirectoryService(MLPortProvider portProvider) {
        this.portProvider = portProvider;
    }

}
