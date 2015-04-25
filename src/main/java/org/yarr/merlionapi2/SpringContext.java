package org.yarr.merlionapi2;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware
{
    static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        ctx = applicationContext;
    }

    public static ApplicationContext ctx() {
        return ctx;
    }
}
