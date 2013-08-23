package com.anatas.cachetest.listeners;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.anatas.cachetest.spring.CacheProvider;

@WebListener
public class ContainerListener implements ServletContextListener {
    @Inject
    private CacheProvider cacheProvider;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(String.format("Shutting down"));
        cacheProvider.shutdown();
    }

}
