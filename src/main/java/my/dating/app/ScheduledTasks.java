package my.dating.app;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.solarframework.auth.obj.Account_EmailVerification.ClearUnregisterUsers;

@Component
public class ScheduledTasks {

    private final CacheManager cacheManager;
    public ScheduledTasks(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // Run every 10 minutes
    @Scheduled(fixedRate = 1000 * 60 * 10)
    public void each10min() {}

    // Run daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void midNight() {
        ClearUnregisterUsers();
        System.out.println("Cleaned up pending account creation...");
        System.out.println("Cleaning up " +  clearAllCaches() + " caches    ...");
    }

    private int clearAllCaches() {
        int i = 0;
        for (String cache : cacheManager.getCacheNames()) {
            Cache c = cacheManager.getCache(cache);
            i++;
            if (c != null) {
                c.invalidate();
                c.clear();
            }
        }
        return i;
    }
}