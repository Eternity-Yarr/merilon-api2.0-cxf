package org.yarr.merlionapi2.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MonitorService
{
    private static Cache<Long, LoggingEvent> info = CacheBuilder
            .newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    private static Cache<Long, LoggingEvent> warn = CacheBuilder
            .newBuilder()
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    private static Cache<Long, LoggingEvent> error = CacheBuilder
            .newBuilder()
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    public static void reg(LoggingEvent e) {
        if (e.getLevel().equals(Level.ERROR) || e.getLevel().equals(Level.FATAL)) {
            error.put(e.getTimeStamp(), e);
        } else if (e.getLevel().equals(Level.WARN)) {
            warn.put(e.getTimeStamp(), e);
        } else {
            info.put(e.getTimeStamp(), e);
        }
    }

    public static List<String> info() {
        Map<Long, LoggingEvent> concatenated = new TreeMap<>();
        concatenated.putAll(info.asMap());
        concatenated.putAll(warn.asMap());
        concatenated.putAll(error.asMap());
        return concatenated
                .values()
                .stream()
                .map(
                        x -> {
                            String ti =
                                x.getThrowableInformation() != null ?
                                " [" + x.getThrowableInformation().getThrowable().getClass().getName() + " at " + x.getThrowableInformation().getThrowable().getStackTrace()[x.getThrowableInformation().getThrowable().getStackTrace().length - 1] + "]"
                            : "";
                            return x.getLevel().toString() + ": " + x.getRenderedMessage() + ti;
                        }
                )
                .collect(Collectors.toList());
    }
    public static List<String> warn() {
        Map<Long, LoggingEvent> concatenated = new TreeMap<>();
        concatenated.putAll(warn.asMap());
        concatenated.putAll(error.asMap());
        return concatenated
                .values()
                .stream()
                .map(x -> x.getLevel().toString() + ": " + x.getRenderedMessage())
                .collect(Collectors.toList());
    }
    public static List<String> error() {
        Map<Long, LoggingEvent> concatenated = new TreeMap<>();
        concatenated.putAll(error.asMap());
        return concatenated
                .values()
                .stream()
                .map(x -> x.getLevel().toString() + ": " + x.getRenderedMessage())
                .collect(Collectors.toList());
    }
}
