package com.anatas.cachetest.spring;

import java.io.Serializable;

import javax.inject.Named;
import javax.inject.Singleton;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
public class CacheProvider implements Serializable {
    private static final Logger logger       = LoggerFactory.getLogger(CacheProvider.class);
    private CacheManager        cacheManager = new CacheManager();
    private Ehcache             cache;

    public CacheProvider() {
        cache = cacheManager.getEhcache("colors");
    }

    private Ehcache getCache() {
        // if (!cacheManager.cacheExists("test")) {
        // TerracottaClientConfiguration terracottaClientConfiguration = new TerracottaClientConfiguration();
        // terracottaClientConfiguration.setUrl(String.format("%s:%d", terracottaHost, terracottaDsoPort));
        // CacheConfiguration cacheConfiguration = new CacheConfiguration("test", 1000);
        // TerracottaConfiguration terracottaConfiguration = new TerracottaConfiguration();
        // cacheConfiguration.terracotta(terracottaConfiguration);
        // cacheManager.addCache(new Cache(cacheConfiguration));
        // }

        return cache;
    }

    public Element getColour(Long id) {
        return getCache().get(id);
    }

    public void putColour(Element element) {
        getCache().put(element);
    }

    public void shutdown() {
        cacheManager.shutdown();
    }
}
