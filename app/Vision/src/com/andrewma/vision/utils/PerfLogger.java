package com.andrewma.vision.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * Helper method to do performance logging
 * 
 * @author ajma
 */
public class PerfLogger {
    private static final String TAG = "Perf";
    private static Map<String, PerfLog> logMap = new HashMap<String, PerfLog>();

    public static void log(String tag, String msg) {
        if(!logMap.containsKey(tag)) {
            logMap.put(tag, new PerfLog());
        }
        PerfLog log = logMap.get(tag);
        Date current = new Date();
        Log.i(TAG, String.format("[%s] %s, since start: %dms, since last: %dms",
                tag, msg,
                (current.getTime() - log.startDate.getTime()),
                (current.getTime() - log.lastEventDate.getTime())));
        log.lastEventDate = current;
    }

    public static void stop(String tag, String msg) {
        log(tag, msg);
        logMap.remove(tag);
    }

    private static class PerfLog {
        private Date startDate;
        private Date lastEventDate;

        private PerfLog() {
            startDate = new Date();
            lastEventDate = new Date();
        }
    }
}
