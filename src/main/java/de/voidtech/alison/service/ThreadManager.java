package main.java.de.voidtech.alison.service;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import org.springframework.stereotype.Service;

@Service
public class ThreadManager
{
    private HashMap<String, ExecutorService> threadMap;
    
    public ThreadManager() {
        this.threadMap = new HashMap<String, ExecutorService>();
    }
    
    private ExecutorService findOrSpawnThread(final String threadID) {
        if (!this.threadMap.containsKey(threadID)) {
            final BasicThreadFactory factory = new BasicThreadFactory.Builder().namingPattern(threadID + "-%d").daemon(true).priority(5).build();
            this.threadMap.put(threadID, Executors.newSingleThreadExecutor((ThreadFactory)factory));
        }
        return this.threadMap.get(threadID);
    }
    
    public ExecutorService getThreadByName(final String name) {
        return this.findOrSpawnThread(name);
    }
}
