package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Class to manage caching.
 * 
 * For now it is just using the default Spring manager and so has to be manually cleared.
 * As we are not tracking realtime quotes, just end of day values, everything can be cached for a day. 
 */

@Configuration
@EnableCaching
public class CacheManagement {
	@Autowired
	private CacheManager cacheManager;
    
    @Scheduled(cron = "0 1 0 * * ?")
    public void clearCacheDaily() { 
    	cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }
}
