// src/main/java/com/glaydson/devicesapi/service/DeviceService.java
package com.glaydson.devicesapi.service;

import com.glaydson.devicesapi.exception.DeviceInUseException;
import com.glaydson.devicesapi.exception.InvalidDeviceStateException;
import com.glaydson.devicesapi.exception.ResourceNotFoundException;
import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device createDevice(Device device) {
        if (device.getState() == null) {
            throw new InvalidDeviceStateException("Device state cannot be null");
        }
        return deviceRepository.save(device);
    }

    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public List<Device> getDevicesByBrand(String brand) {
        return deviceRepository.findByBrand(brand);
    }

    public List<Device> getDevicesByState(Device.State state) {
        return deviceRepository.findByState(state);
    }

    public Device updateDevice(Device device) {
        Device existingDevice = deviceRepository.findById(device.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Device not found for this id :: " + device.getId()));

        if (existingDevice.getState() == Device.State.IN_USE) {
            if (!existingDevice.getName().equals(device.getName()) || !existingDevice.getBrand().equals(device.getBrand())) {
                throw new DeviceInUseException("Name and brand cannot be updated if the device is in use");
            }
        }

        if (device.getCreationTime() != null && !device.getCreationTime().equals(existingDevice.getCreationTime())) {
            throw new InvalidDeviceStateException("Creation time cannot be updated");
        }

        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found for this id :: " + id));

        if (existingDevice.getState() == Device.State.IN_USE) {
            throw new DeviceInUseException("Devices in use cannot be removed");
        }

        deviceRepository.deleteById(id);
    }
}