package com.glaydson.devicesapi.controller;

import com.glaydson.devicesapi.dto.DeviceRequest;
import com.glaydson.devicesapi.dto.DeviceSearchCriteria;
import com.glaydson.devicesapi.exception.ResourceNotFoundException;
import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    // Other questions
    // - How to apply versioning to the API?
    // https://restfulapi.net/versioning/
    // https://www.baeldung.com/rest-versioning
    // https://www.toptal.com/java/rest-versioning
    // what is missing for production?
    // - Exception handling
    // - Logging
    // - Security
    // - Testing
    // - Documentation
    // - Monitoring
    // - Performance
    // - Scalability
    // - Resilience
    // - CI/CD
    // - Containerization
    // - Orchestration
    // - Observability
    // - Tracing
    // - Alerting
    // - Backup
    // - Recovery
    // - Disaster recovery
    // - Load balancing
    // - Caching
    // - Rate limiting
    // - Circuit breaking
    // - Service discovery
    // - API gateway
    // how to handle authentication and authorization?
    // - https://www.baeldung.com/spring-security-authentication-with-a-database
    // - https://www.baeldung.com/spring-security-expressions
    // - https://www.baeldung.com/spring-security-method-security
    // - https://www.baeldung.com/spring-security-role-and-privilege
    // - https://www.baeldung.com/spring-security-oauth2-authentication
    // if a query takes too long, what to do

    public static final String DEVICE_NOT_FOUND_FOR_THIS_ID = "Device not found for this id :: ";
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("")
    public ResponseEntity<Device> createDevice(@RequestBody DeviceRequest deviceRequest) {
        log.info("Creating device: {}", deviceRequest);
        Device createdDevice = deviceService.createDevice(deviceRequest);
        log.info("Created device: {}", createdDevice);
        return ResponseEntity.ok(createdDevice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody DeviceRequest deviceRequest) {
        log.info("Updating device with id: {}", id);
        Device updatedDevice = deviceService.updateDevice(id, deviceRequest);
        log.info("Updated device: {}", updatedDevice);
        return ResponseEntity.ok(updatedDevice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        log.info("Getting device with id: {}", id);
        Device device = deviceService.getDeviceById(id);
        return ResponseEntity.ok(device);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Device>> getDevicesByCriteria(@RequestBody DeviceSearchCriteria criteria) {
        log.info("Searching devices with criteria: {}", criteria);
        List<Device> devices = deviceService.getDevicesByCriteria(criteria);
        return ResponseEntity.ok(devices);
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        log.info("Getting all devices");
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {

       log.info("Deleting device with id: {}", id);
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}