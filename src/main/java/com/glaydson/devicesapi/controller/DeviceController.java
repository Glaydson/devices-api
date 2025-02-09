package com.glaydson.devicesapi.controller;

import com.glaydson.devicesapi.exception.DeviceInUseException;
import com.glaydson.devicesapi.exception.InvalidDeviceStateException;
import com.glaydson.devicesapi.exception.ResourceNotFoundException;
import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    public static final String DEVICE_NOT_FOUND_FOR_THIS_ID = "Device not found for this id :: ";
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("")
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        log.info("Creating device: {}", device);
        Device createdDevice = deviceService.createDevice(device);
        return ResponseEntity.ok(createdDevice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        log.info("Getting device by id: {}", id);
        Device device = deviceService.getDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));
        return ResponseEntity.ok(device);
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        log.info("Getting all devices");
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Device>> getDevicesByBrand(@PathVariable String brand) {
        log.info("Getting devices by brand: {}", brand);
        List<Device> devices = deviceService.getDevicesByBrand(brand);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<Device>> getDevicesByState(@PathVariable Device.State state) {
        log.info("Getting devices by state: {}", state);
        List<Device> devices = deviceService.getDevicesByState(state);
        return ResponseEntity.ok(devices);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Device> partiallyUpdateDevice(@PathVariable Long id, @RequestBody Device device) {

            Device existingDevice = deviceService.getDeviceById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));
            log.info("Partially updating device: {}", existingDevice);
            log.info("New device: {}", device);
            Device updatedDevice = deviceService.updateDevice(id, device);
            log.info("Updated device: {}", updatedDevice);
            return ResponseEntity.ok(updatedDevice);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
       deviceService.getDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));
       log.info("Deleting device by id: {}", id);
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}