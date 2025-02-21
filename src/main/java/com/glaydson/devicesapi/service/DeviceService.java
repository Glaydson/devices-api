// src/main/java/com/glaydson/devicesapi/service/DeviceService.java
package com.glaydson.devicesapi.service;

import com.glaydson.devicesapi.dto.DeviceRequest;
import com.glaydson.devicesapi.dto.DeviceSearchCriteria;
import com.glaydson.devicesapi.exception.DeviceInUseException;
import com.glaydson.devicesapi.exception.InvalidDeviceStateException;
import com.glaydson.devicesapi.exception.MissingFieldsException;
import com.glaydson.devicesapi.exception.ResourceNotFoundException;
import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DeviceService {

    public static final String DEVICE_NOT_FOUND_FOR_THIS_ID = "Device not found for this id :: ";
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device createDevice(DeviceRequest deviceRequest) {
        // Test if all the mandatory fields are filled
        if (deviceRequest.getBrand() == null || deviceRequest.getName() == null || deviceRequest.getState() == null) {
            throw new MissingFieldsException("Device brand, name and state are mandatory");
        }
        Device device = new Device(deviceRequest.getName(), deviceRequest.getBrand(), deviceRequest.getState());
        return deviceRepository.save(device);
    }

    public Device getDeviceById(Long id) {
        Optional<Device> device = deviceRepository.findById(id);
        return device.orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));
    }

    public List<Device> getAllDevices() {
        // PAGINATION MISSING
        return deviceRepository.findAll();
    }


    public List<Device> getDevicesByCriteria(DeviceSearchCriteria criteria) {
        if (criteria.getBrand() != null && criteria.getState() != null) {
            return deviceRepository.findByBrandAndState(criteria.getBrand(), criteria.getState());
        } else if (criteria.getBrand() != null) {
            return deviceRepository.findByBrand(criteria.getBrand());
        } else if (criteria.getState() != null) {
            return deviceRepository.findByState(criteria.getState());
        } else {
            return deviceRepository.findAll();
        }
    }

    public Device updateDevice(Long id, DeviceRequest device) {
        // Improve this method, it is not good
        // why creation time is being tested here, not use device object
        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));

        if (existingDevice.getState() == Device.State.IN_USE && (!existingDevice.getName().equals(device.getName()) || !existingDevice.getBrand().equals(device.getBrand()))) {
            log.error("Device in use cannot have name and brand updated");
            throw new DeviceInUseException("Name and brand cannot be updated if the device is in use");
            }


        existingDevice.setName(device.getName());
        existingDevice.setBrand(device.getBrand());
        existingDevice.setState(device.getState());

        return deviceRepository.save(existingDevice);
    }

    public void deleteDevice(Long id) {
        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DEVICE_NOT_FOUND_FOR_THIS_ID + id));

        if (existingDevice.getState() == Device.State.IN_USE) {
            log.error("Devices in use cannot be removed");
            throw new DeviceInUseException("Devices in use cannot be removed");
        }

        deviceRepository.deleteById(id);
    }
}