package com.glaydson.devicesapi.service;

import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private DeviceRepository deviceRepository;

    // Contructor
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device createDevice(Device device) {
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
        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }
}