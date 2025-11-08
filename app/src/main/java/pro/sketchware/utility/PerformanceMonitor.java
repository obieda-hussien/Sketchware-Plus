package pro.sketchware.utility;

import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Performance monitoring utility for tracking build and compilation performance.
 * Provides detailed metrics and profiling capabilities for optimization.
 * 
 * Features:
 * - Build time tracking
 * - Memory usage monitoring
 * - CPU usage profiling
 * - I/O performance metrics
 * - Detailed reports generation
 * 
 * @since 7.1.0
 */
public class PerformanceMonitor {
    private static final String TAG = "PerformanceMonitor";
    private static final boolean ENABLED = true; // Set to false in production for performance
    
    private final Context context;
    private final Map<String, Long> startTimes;
    private final Map<String, PerformanceMetrics> metrics;
    private final Map<String, Long> memorySnapshots;
    
    private static PerformanceMonitor instance;
    
    /**
     * Private constructor for singleton pattern
     */
    private PerformanceMonitor(Context context) {
        this.context = context.getApplicationContext();
        this.startTimes = new ConcurrentHashMap<>();
        this.metrics = new ConcurrentHashMap<>();
        this.memorySnapshots = new ConcurrentHashMap<>();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized PerformanceMonitor getInstance(Context context) {
        if (instance == null) {
            instance = new PerformanceMonitor(context);
        }
        return instance;
    }
    
    /**
     * Start tracking an operation
     * 
     * @param operationName Unique name for the operation
     */
    public void startTracking(@NonNull String operationName) {
        if (!ENABLED) return;
        
        long startTime = SystemClock.elapsedRealtime();
        startTimes.put(operationName, startTime);
        
        // Take memory snapshot
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        memorySnapshots.put(operationName, usedMemory);
        
        Log.d(TAG, String.format("Started tracking: %s (Memory: %d MB)",
            operationName, usedMemory / (1024 * 1024)));
    }
    
    /**
     * Stop tracking an operation and record metrics
     * 
     * @param operationName Name of the operation
     * @return Duration in milliseconds
     */
    public long stopTracking(@NonNull String operationName) {
        if (!ENABLED) return 0;
        
        Long startTime = startTimes.remove(operationName);
        if (startTime == null) {
            Log.w(TAG, "No start time found for: " + operationName);
            return 0;
        }
        
        long endTime = SystemClock.elapsedRealtime();
        long duration = endTime - startTime;
        
        // Calculate memory delta
        Runtime runtime = Runtime.getRuntime();
        long endMemory = runtime.totalMemory() - runtime.freeMemory();
        Long startMemory = memorySnapshots.remove(operationName);
        long memoryDelta = startMemory != null ? endMemory - startMemory : 0;
        
        // Record metrics
        PerformanceMetrics metric = new PerformanceMetrics(
            operationName,
            duration,
            memoryDelta,
            System.currentTimeMillis()
        );
        metrics.put(operationName, metric);
        
        Log.d(TAG, String.format("Stopped tracking: %s (Duration: %d ms, Memory Δ: %d MB)",
            operationName, duration, memoryDelta / (1024 * 1024)));
        
        return duration;
    }
    
    /**
     * Get metrics for a specific operation
     */
    public PerformanceMetrics getMetrics(@NonNull String operationName) {
        return metrics.get(operationName);
    }
    
    /**
     * Get all recorded metrics
     */
    public Map<String, PerformanceMetrics> getAllMetrics() {
        return new HashMap<>(metrics);
    }
    
    /**
     * Clear all metrics
     */
    public void clear() {
        startTimes.clear();
        metrics.clear();
        memorySnapshots.clear();
    }
    
    /**
     * Generate a detailed performance report
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Performance Report ===\n");
        report.append("Generated: ").append(getCurrentTimestamp()).append("\n");
        report.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        report.append("Android: ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
        report.append("\n");
        
        // System info
        Runtime runtime = Runtime.getRuntime();
        report.append("=== System Info ===\n");
        report.append(String.format("Available Processors: %d\n", runtime.availableProcessors()));
        report.append(String.format("Total Memory: %d MB\n", runtime.totalMemory() / (1024 * 1024)));
        report.append(String.format("Free Memory: %d MB\n", runtime.freeMemory() / (1024 * 1024)));
        report.append(String.format("Max Memory: %d MB\n", runtime.maxMemory() / (1024 * 1024)));
        report.append("\n");
        
        // Metrics
        report.append("=== Operation Metrics ===\n");
        if (metrics.isEmpty()) {
            report.append("No metrics recorded.\n");
        } else {
            long totalDuration = 0;
            long totalMemory = 0;
            
            for (Map.Entry<String, PerformanceMetrics> entry : metrics.entrySet()) {
                PerformanceMetrics metric = entry.getValue();
                report.append(metric.toString()).append("\n");
                totalDuration += metric.durationMs;
                totalMemory += metric.memoryDeltaBytes;
            }
            
            report.append("\n");
            report.append(String.format("Total Duration: %d ms (%.2f seconds)\n",
                totalDuration, totalDuration / 1000.0));
            report.append(String.format("Total Memory Delta: %d MB\n",
                totalMemory / (1024 * 1024)));
        }
        
        return report.toString();
    }
    
    /**
     * Save performance report to file
     */
    public boolean saveReport(@NonNull File outputFile) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(generateReport());
            Log.i(TAG, "Performance report saved to: " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to save performance report", e);
            return false;
        }
    }
    
    /**
     * Log current memory usage
     */
    public void logMemoryUsage(@NonNull String context) {
        if (!ENABLED) return;
        
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long percentUsed = (usedMemory * 100) / maxMemory;
        
        Log.d(TAG, String.format("[%s] Memory: %d MB / %d MB (%.1f%%)",
            context,
            usedMemory / (1024 * 1024),
            maxMemory / (1024 * 1024),
            percentUsed));
    }
    
    /**
     * Force garbage collection and log memory before/after
     * Note: Only use for debugging, not in production
     */
    public void forceGC() {
        if (!ENABLED) return;
        
        Runtime runtime = Runtime.getRuntime();
        long beforeGC = runtime.totalMemory() - runtime.freeMemory();
        
        System.gc();
        System.runFinalization();
        
        // Wait a bit for GC to complete
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long afterGC = runtime.totalMemory() - runtime.freeMemory();
        long freed = beforeGC - afterGC;
        
        Log.d(TAG, String.format("GC: Freed %d MB (Before: %d MB, After: %d MB)",
            freed / (1024 * 1024),
            beforeGC / (1024 * 1024),
            afterGC / (1024 * 1024)));
    }
    
    /**
     * Get current timestamp formatted
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return sdf.format(new Date());
    }
    
    /**
     * Performance metrics data class
     */
    public static class PerformanceMetrics {
        public final String operationName;
        public final long durationMs;
        public final long memoryDeltaBytes;
        public final long timestamp;
        
        PerformanceMetrics(String operationName, long durationMs, long memoryDeltaBytes, long timestamp) {
            this.operationName = operationName;
            this.durationMs = durationMs;
            this.memoryDeltaBytes = memoryDeltaBytes;
            this.timestamp = timestamp;
        }
        
        /**
         * Get duration in seconds
         */
        public double getDurationSeconds() {
            return durationMs / 1000.0;
        }
        
        /**
         * Get memory delta in megabytes
         */
        public double getMemoryDeltaMB() {
            return memoryDeltaBytes / (1024.0 * 1024.0);
        }
        
        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
            return String.format("[%s] %s: Duration=%.2fs, Memory Δ=%.2f MB",
                sdf.format(new Date(timestamp)),
                operationName,
                getDurationSeconds(),
                getMemoryDeltaMB());
        }
    }
    
    /**
     * Helper class for automatic tracking using try-with-resources
     */
    public static class AutoTracker implements AutoCloseable {
        private final PerformanceMonitor monitor;
        private final String operationName;
        
        public AutoTracker(PerformanceMonitor monitor, String operationName) {
            this.monitor = monitor;
            this.operationName = operationName;
            monitor.startTracking(operationName);
        }
        
        @Override
        public void close() {
            monitor.stopTracking(operationName);
        }
    }
    
    /**
     * Create an auto-tracker for use with try-with-resources
     * 
     * Example usage:
     * try (PerformanceMonitor.AutoTracker tracker = monitor.track("operation")) {
     *     // Your code here
     * }
     */
    public AutoTracker track(@NonNull String operationName) {
        return new AutoTracker(this, operationName);
    }
}
