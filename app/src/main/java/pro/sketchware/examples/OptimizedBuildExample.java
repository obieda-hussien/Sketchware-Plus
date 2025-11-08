package pro.sketchware.examples;

import android.content.Context;
import android.util.Log;

import java.io.File;

import pro.sketchware.utility.BuildCacheManager;
import pro.sketchware.utility.PerformanceMonitor;

/**
 * Example integration of new performance tools with the build system.
 * This demonstrates how to use BuildCacheManager and PerformanceMonitor
 * in the compilation process.
 * 
 * @since 7.1.0
 */
public class OptimizedBuildExample {
    private static final String TAG = "OptimizedBuildExample";
    
    private final Context context;
    private final BuildCacheManager cacheManager;
    private final PerformanceMonitor performanceMonitor;
    
    public OptimizedBuildExample(Context context) {
        this.context = context;
        this.cacheManager = BuildCacheManager.getInstance(context);
        this.performanceMonitor = PerformanceMonitor.getInstance(context);
    }
    
    /**
     * Example: Optimized Java compilation with caching and performance tracking
     */
    public void compileJavaWithOptimizations(File sourceFile) {
        // Track performance with auto-tracker
        try (PerformanceMonitor.AutoTracker tracker = 
                performanceMonitor.track("compile_java_" + sourceFile.getName())) {
            
            // Generate cache key based on file content
            String cacheKey = cacheManager.generateCacheKey(sourceFile);
            
            // Check if already compiled
            if (cacheManager.isCached(cacheKey)) {
                Log.i(TAG, "Using cached compilation for: " + sourceFile.getName());
                CompiledClass cached = cacheManager.get(cacheKey, CompiledClass.class);
                
                if (cached != null) {
                    Log.i(TAG, "Cache hit! Skipping compilation.");
                    return;
                }
            }
            
            // Not cached, perform compilation
            Log.i(TAG, "Compiling: " + sourceFile.getName());
            performanceMonitor.logMemoryUsage("before_compile");
            
            CompiledClass result = performCompilation(sourceFile);
            
            performanceMonitor.logMemoryUsage("after_compile");
            
            // Store in cache for next time
            cacheManager.put(cacheKey, result);
            Log.i(TAG, "Compilation cached for future use");
            
        } // Auto-tracker will log duration and memory delta here
    }
    
    /**
     * Example: Full project build with comprehensive performance tracking
     */
    public void buildProjectWithMonitoring(String projectPath) {
        performanceMonitor.clear(); // Start fresh
        
        try {
            // Overall build tracking
            performanceMonitor.startTracking("full_build");
            
            // Resource compilation
            try (var tracker = performanceMonitor.track("compile_resources")) {
                compileResources(projectPath);
            }
            
            // Java compilation
            try (var tracker = performanceMonitor.track("compile_java")) {
                compileJavaFiles(projectPath);
            }
            
            // DEX generation
            try (var tracker = performanceMonitor.track("generate_dex")) {
                generateDexFiles(projectPath);
            }
            
            // APK building
            try (var tracker = performanceMonitor.track("build_apk")) {
                buildApk(projectPath);
            }
            
            // Stop overall tracking
            long totalTime = performanceMonitor.stopTracking("full_build");
            
            // Generate detailed report
            String report = performanceMonitor.generateReport();
            Log.i(TAG, "Build Performance Report:\n" + report);
            
            // Save report to file
            File reportFile = new File(projectPath, "build_performance_report.txt");
            performanceMonitor.saveReport(reportFile);
            
            // Log cache statistics
            BuildCacheManager.CacheStats stats = cacheManager.getStats();
            Log.i(TAG, "Cache Statistics: " + stats.toString());
            
        } catch (Exception e) {
            Log.e(TAG, "Build failed", e);
            performanceMonitor.stopTracking("full_build");
        }
    }
    
    /**
     * Example: Incremental build with smart caching
     */
    public void incrementalBuild(String projectPath) {
        Log.i(TAG, "Starting incremental build...");
        
        // Get list of modified files
        File[] modifiedFiles = getModifiedFiles(projectPath);
        
        if (modifiedFiles.length == 0) {
            Log.i(TAG, "No files modified, using cache for everything");
            return;
        }
        
        Log.i(TAG, "Recompiling " + modifiedFiles.length + " modified files");
        
        int cacheHits = 0;
        int cacheMisses = 0;
        
        for (File file : modifiedFiles) {
            String cacheKey = cacheManager.generateCacheKey(file);
            
            // Invalidate cache for modified file
            cacheManager.invalidate(cacheKey);
            
            // Recompile
            compileJavaWithOptimizations(file);
            cacheMisses++;
        }
        
        // Check cache statistics
        Log.i(TAG, String.format("Incremental build complete. Cache hits: %d, misses: %d",
            cacheHits, cacheMisses));
    }
    
    /**
     * Example: Memory-intensive operation with monitoring
     */
    public void performMemoryIntensiveOperation() {
        performanceMonitor.logMemoryUsage("start");
        
        try (var tracker = performanceMonitor.track("memory_intensive_op")) {
            // Simulate memory-intensive operation
            for (int i = 0; i < 1000; i++) {
                // Do work...
                
                // Check memory every 100 iterations
                if (i % 100 == 0) {
                    performanceMonitor.logMemoryUsage("iteration_" + i);
                }
            }
        }
        
        // Optionally force GC and log results
        performanceMonitor.forceGC();
        performanceMonitor.logMemoryUsage("after_gc");
    }
    
    /**
     * Example: Batch operations with progress tracking
     */
    public void batchCompileWithProgress(File[] sourceFiles) {
        int total = sourceFiles.length;
        int completed = 0;
        
        performanceMonitor.startTracking("batch_compile");
        
        for (File file : sourceFiles) {
            compileJavaWithOptimizations(file);
            completed++;
            
            // Log progress
            int progress = (completed * 100) / total;
            Log.i(TAG, String.format("Compilation progress: %d%% (%d/%d)",
                progress, completed, total));
        }
        
        long duration = performanceMonitor.stopTracking("batch_compile");
        Log.i(TAG, String.format("Batch compilation completed in %.2f seconds",
            duration / 1000.0));
    }
    
    /**
     * Example: Cache management and cleanup
     */
    public void manageCacheLifecycle() {
        BuildCacheManager.CacheStats stats = cacheManager.getStats();
        
        Log.i(TAG, "Current cache state:");
        Log.i(TAG, "  Memory entries: " + stats.memoryEntries);
        Log.i(TAG, "  Disk entries: " + stats.diskEntries);
        Log.i(TAG, "  Disk size: " + stats.diskSizeMB + " MB");
        
        // Clear old cache if disk usage is too high
        if (stats.diskSizeMB > 500) {
            Log.w(TAG, "Cache size exceeds 500MB, clearing...");
            cacheManager.clearAll();
        }
    }
    
    // ========== Mock Methods (replace with actual implementations) ==========
    
    private CompiledClass performCompilation(File sourceFile) {
        // Actual compilation logic here
        return new CompiledClass(sourceFile.getName());
    }
    
    private void compileResources(String projectPath) {
        // Resource compilation logic
    }
    
    private void compileJavaFiles(String projectPath) {
        // Java compilation logic
    }
    
    private void generateDexFiles(String projectPath) {
        // DEX generation logic
    }
    
    private void buildApk(String projectPath) {
        // APK building logic
    }
    
    private File[] getModifiedFiles(String projectPath) {
        // Get list of modified files
        return new File[0];
    }
    
    /**
     * Simple data class for compiled code
     */
    private static class CompiledClass implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        final String fileName;
        
        CompiledClass(String fileName) {
            this.fileName = fileName;
        }
    }
    
    /**
     * Example usage
     */
    public static void main(String[] args) {
        // This is just for documentation purposes
        // Actual usage would be in Android context
        
        /*
        Context context = getApplicationContext();
        OptimizedBuildExample example = new OptimizedBuildExample(context);
        
        // Single file compilation
        File sourceFile = new File("/path/to/MainActivity.java");
        example.compileJavaWithOptimizations(sourceFile);
        
        // Full project build
        example.buildProjectWithMonitoring("/path/to/project");
        
        // Incremental build
        example.incrementalBuild("/path/to/project");
        */
    }
}
