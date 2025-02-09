package com.glaydson.devicesapi.service;

import com.glaydson.devicesapi.exception.DeviceInUseException;
import com.glaydson.devicesapi.exception.InvalidDeviceStateException;
import com.glaydson.devicesapi.exception.ResourceNotFoundException;
import com.glaydson.devicesapi.model.Device;
import com.glaydson.devicesapi.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private Device device;
    private Device device2;
    private Device device3;
    private Device device4;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        device = new Device(1L, "Device1", "Brand1", Device.State.AVAILABLE, LocalDateTime.now());
        device2 = new Device(2L, "Device2", "Brand2", Device.State.IN_USE, LocalDateTime.now());
        device3 = new Device(3L, "Device3", "Brand3", Device.State.INACTIVE, LocalDateTime.now());
        device4 = new Device(4L, "Device4", "Brand4", Device.State.AVAILABLE, LocalDateTime.now());
    }

    @Test
    void testCreateDevice() {
        when(deviceRepository.save(any(Device.class))).thenReturn(device);
        Device createdDevice = deviceService.createDevice(device);
        assertNotNull(createdDevice);
        assertEquals(device.getName(), createdDevice.getName());
    }

    @Test
    void testCreateDeviceWithNullState() {
        device.setState(null);
        assertThrows(InvalidDeviceStateException.class, () -> deviceService.createDevice(device));
    }

    @Test
    void testGetDeviceById() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        Optional<Device> foundDevice = deviceService.getDeviceById(1L);
        assertTrue(foundDevice.isPresent());
        assertEquals(device.getName(), foundDevice.get().getName());
    }

    @Test
    void testGetDeviceByIdNotFound() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> deviceService.getDeviceById(1L).orElseThrow(() -> new ResourceNotFoundException("Device not found")));
    }

    @Test
    void testUpdateDevice() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        Device savedDevice = new Device(1L, "UpdatedName", device.getBrand(), device.getState(), device.getCreationTime());
        when(deviceRepository.save(any(Device.class))).thenReturn(savedDevice);
        Device preUpdated = new Device(null, "UpdatedName", device.getBrand(), Device.State.IN_USE, device.getCreationTime());
        Device updatedDevice = deviceService.updateDevice(1L, preUpdated);
        assertEquals("UpdatedName", updatedDevice.getName());
    }

    @Test
    void testUpdateDeviceInUse() {
        when(deviceRepository.findById(2L)).thenReturn(Optional.of(device2));
        Device preUpdated = new Device(2L, "UpdatedName", device2.getBrand(), Device.State.IN_USE, device2.getCreationTime());
        assertThrows(DeviceInUseException.class, () -> deviceService.updateDevice(2L, preUpdated));
        device2 = deviceService.getDeviceById(2L).orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        assertEquals("Device2", device2.getName());
    }

    @Test
    void testUpdateDeviceCreationTime() {
        Device existingDevice = new Device(1L, "Device1", "Brand1", Device.State.AVAILABLE, LocalDateTime.now());
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));
        device.setCreationTime(LocalDateTime.now().plusDays(1));
        assertThrows(InvalidDeviceStateException.class, () -> deviceService.updateDevice(1L, device));
    }

    @Test
    void testDeleteDevice() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        doNothing().when(deviceRepository).deleteById(1L);
        deviceService.deleteDevice(1L);
        verify(deviceRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDeviceInUse() {
        device.setState(Device.State.IN_USE);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        assertThrows(DeviceInUseException.class, () -> deviceService.deleteDevice(1L));
    }
}