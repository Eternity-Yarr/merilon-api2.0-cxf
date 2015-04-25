package org.yarr.merlionapi2;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.yarr.merlionapi2.service.MonitorService;

public class MonitorAppender extends AppenderSkeleton
{
    @Override
    protected void append(LoggingEvent event)
    {
        MonitorService.reg(event);
    }

    @Override
    public void close()
    {

    }

    @Override
    public boolean requiresLayout()
    {
        return false;
    }
}
