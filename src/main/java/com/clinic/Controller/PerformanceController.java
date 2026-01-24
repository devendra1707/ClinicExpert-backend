package com.clinic.Controller;

import com.clinic.Annotation.PerformanceMonitor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Controller to test and demonstrate AOP performance monitoring
 */
@Slf4j
@RestController
@RequestMapping("/api/performance")
@Tag(name = "Performance Testing", description = "APIs to test performance monitoring")
public class PerformanceController {

    @GetMapping("/fast")
    @Operation(summary = "Fast API", description = "Test fast API execution (< 100ms)")
    public ResponseEntity<Map<String, Object>> fastApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a fast API");
        response.put("executionTime", "< 100ms");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medium")
    @Operation(summary = "Medium Speed API", description = "Test medium speed API (500ms)")
    public ResponseEntity<Map<String, Object>> mediumApi() throws InterruptedException {
        // Simulate processing time
        TimeUnit.MILLISECONDS.sleep(500);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a medium speed API");
        response.put("executionTime", "~500ms");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slow")
    @Operation(summary = "Slow API", description = "Test slow API execution (> 1 second)")
    public ResponseEntity<Map<String, Object>> slowApi() throws InterruptedException {
        // Simulate slow processing
        TimeUnit.SECONDS.sleep(2);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a slow API - will trigger performance warning");
        response.put("executionTime", "~2000ms");
        response.put("status", "success");
        response.put("warning", "Execution time exceeded 1 second");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/error")
    @Operation(summary = "Error API", description = "Test API that throws an error")
    public ResponseEntity<Map<String, Object>> errorApi() {
        log.info("This will throw an exception");
        throw new RuntimeException("Intentional error for testing AOP exception handling");
    }

    @PostMapping("/custom-monitor")
    @PerformanceMonitor("Custom Performance Test")
    @Operation(summary = "Custom Monitor API", description = "Test custom @PerformanceMonitor annotation")
    public ResponseEntity<Map<String, Object>> customMonitorApi(@RequestBody Map<String, String> data)
            throws InterruptedException {

        log.info("Processing custom monitored API with data: {}", data);

        // Simulate some processing
        TimeUnit.MILLISECONDS.sleep(300);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Custom monitored API executed");
        response.put("receivedData", data);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/database-simulation")
    @Operation(summary = "Database Operation Simulation", description = "Simulate a database operation")
    public ResponseEntity<Map<String, Object>> databaseSimulation() throws InterruptedException {
        log.info("Simulating database query");

        // Simulate DB query time
        TimeUnit.MILLISECONDS.sleep(150);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Database operation completed");
        response.put("recordsProcessed", 100);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-layers")
    @Operation(summary = "All Layers Test", description = "Test performance across all layers")
    public ResponseEntity<Map<String, Object>> allLayersTest() throws InterruptedException {
        log.info("Testing all layers");

        // Controller layer
        TimeUnit.MILLISECONDS.sleep(50);

        // Simulate service layer call
        serviceLayerSimulation();

        // Simulate repository layer call
        repositoryLayerSimulation();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All layers executed successfully");
        response.put("layers", new String[]{"Controller", "Service", "Repository"});
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @PerformanceMonitor("Service Layer")
    private void serviceLayerSimulation() throws InterruptedException {
        log.info("Service layer processing");
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @PerformanceMonitor("Repository Layer")
    private void repositoryLayerSimulation() throws InterruptedException {
        log.info("Repository layer processing");
        TimeUnit.MILLISECONDS.sleep(80);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get Performance Metrics", description = "Get current performance metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAPIs", 8);
        metrics.put("monitoringEnabled", true);
        metrics.put("aspectsActive", new String[]{
                "PerformanceAspect",
                "LoggingAspect",
                "ExceptionHandlingAspect"
        });
        metrics.put("performanceThresholds", Map.of(
                "warning", "500ms",
                "critical", "1000ms"
        ));
        return ResponseEntity.ok(metrics);
    }
}