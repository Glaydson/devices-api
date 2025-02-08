package com.glaydson.devicesapi.controller;

import com.glaydson.devicesapi.exception.ResourceNotFoundException;
import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        Device createdDevice = deviceService.createDevice(device);
        return ResponseEntity.ok(createdDevice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));
        return ResponseEntity.ok(device);
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Device>> getDevicesByBrand(@PathVariable String brand) {
        List<Device> devices = deviceService.getDevicesByBrand(brand);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<Device>> getDevicesByState(@PathVariable Device.State state) {
        List<Device> devices = deviceService.getDevicesByState(state);
        return ResponseEntity.ok(devices);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        deviceService.getDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));
        device.setId(id);
        Device updatedDevice = deviceService.updateDevice(device);
        return ResponseEntity.ok(updatedDevice);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Device> partiallyUpdateDevice(@PathVariable Long id, @RequestBody Device device) {
        Device existingDevice = deviceService.getDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));
        if (device.getName() != null) existingDevice.setName(device.getName());
        if (device.getBrand() != null) existingDevice.setBrand(device.getBrand());
        if (device.getState() != null) existingDevice.setState(device.getState());
        if (device.getCreationTime() != null) existingDevice.setCreationTime(device.getCreationTime());
        Device updatedDevice = deviceService.updateDevice(existingDevice);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.getDeviceById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found for this id :: " + id));
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}